[CmdletBinding(SupportsShouldProcess)]
param(
    [string]$SourceDir = $PSScriptRoot,
    [string]$OutputDir = "C:\java\ejecutables\import-from-opendata-gc-ejecutables",
    [string]$Version,
    [string]$RollbackVersion
)

$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest
Add-Type -AssemblyName System.IO.Compression.FileSystem
$scriptNames = @('import_from_opendata_gc.bat', 'generar_ejecutables.ps1', 'publicar_ejecutables.ps1')

function Assert-Version([string]$Value) {
    if ($Value -notmatch '^\d+\.\d+\.\d+$') { throw "Version no valida: '$Value'. Use mayor.menor.parche." }
}

function Copy-Contents([string]$Source, [string]$Destination) {
    if (-not (Test-Path -LiteralPath $Source -PathType Container)) { throw "No existe el directorio $Source" }
    New-Item -ItemType Directory -Force -Path $Destination | Out-Null
    Get-ChildItem -LiteralPath $Source -Force | ForEach-Object {
        Copy-Item -LiteralPath $_.FullName -Destination $Destination -Recurse -Force
    }
}

function Get-JarVersion([string]$Path) {
    $zip = [System.IO.Compression.ZipFile]::OpenRead($Path)
    try {
        $entry = $zip.GetEntry('META-INF/MANIFEST.MF')
        if ($null -eq $entry) { throw "El JAR no tiene manifiesto: $Path" }
        $reader = [System.IO.StreamReader]::new($entry.Open())
        try { $manifest = $reader.ReadToEnd() } finally { $reader.Dispose() }
        if ($manifest -match '(?m)^(?:App-Version|Implementation-Version):\s*(\d+\.\d+\.\d+)\s*$') {
            return $Matches[1]
        }
        throw "No se puede identificar la version de $Path"
    } finally { $zip.Dispose() }
}

function Write-Manifest([string]$Directory, [string]$ReleaseVersion, [string]$Origin) {
    $files = @(Get-ChildItem -LiteralPath $Directory -File -Recurse | Where-Object Name -ne 'manifest.json' |
        Sort-Object FullName | ForEach-Object {
            [ordered]@{
                path = $_.FullName.Substring($Directory.Length).TrimStart('\', '/')
                sha256 = (Get-FileHash -LiteralPath $_.FullName -Algorithm SHA256).Hash
                bytes = $_.Length
            }
        })
    [ordered]@{
        version = $ReleaseVersion
        publishedAtUtc = (Get-Date).ToUniversalTime().ToString('o')
        sourceDirectory = $Origin
        files = $files
    } | ConvertTo-Json -Depth 5 | Set-Content -LiteralPath (Join-Path $Directory 'manifest.json') -Encoding UTF8
}

function Assert-Release([string]$Directory, [string]$ReleaseVersion) {
    $jar = Join-Path $Directory "import-from-gc-$ReleaseVersion.jar"
    if (-not (Test-Path -LiteralPath $jar -PathType Leaf)) { throw "Falta el ejecutable $jar" }
    if ((Get-JarVersion $jar) -ne $ReleaseVersion) { throw "La version interna del JAR no coincide con $ReleaseVersion" }
    foreach ($name in @('properties', 'data\gc')) {
        if (-not (Test-Path -LiteralPath (Join-Path $Directory $name) -PathType Container)) { throw "Falta $name en $Directory" }
    }
}

function Save-CurrentInstallation([string]$BaseDirectory) {
    $alias = Join-Path $BaseDirectory 'import_from_opendata_gc.jar'
    if (-not (Test-Path -LiteralPath $alias -PathType Leaf)) { return }
    $oldVersion = Get-JarVersion $alias
    # Conserva una instantanea completa antes de sustituir ejecutable, datos o configuracion.
    $backup = Join-Path $BaseDirectory ("backups\installation\" + (Get-Date -Format 'yyyyMMdd_HHmmss_ffff') + '-' + [guid]::NewGuid().ToString('N').Substring(0,8))
    New-Item -ItemType Directory -Force -Path $backup | Out-Null
    Get-ChildItem -LiteralPath $BaseDirectory -File | ForEach-Object {
        Copy-Item -LiteralPath $_.FullName -Destination $backup
    }
    foreach ($name in @('properties', 'data')) {
        $path = Join-Path $BaseDirectory $name
        if (Test-Path -LiteralPath $path) { Copy-Contents $path (Join-Path $backup $name) }
    }
    Write-Manifest $backup $oldVersion $BaseDirectory
    $oldRelease = Join-Path $BaseDirectory "versions\$oldVersion"
    if (-not (Test-Path -LiteralPath $oldRelease)) {
        Copy-Contents $backup $oldRelease
        Copy-Item -LiteralPath $alias -Destination (Join-Path $oldRelease "import-from-gc-$oldVersion.jar")
        Write-Manifest $oldRelease $oldVersion $BaseDirectory
        Write-Host "Version anterior conservada: $oldRelease"
    }
    Write-Host "Copia de la instalacion anterior: $backup"
}

function Activate-Release([string]$BaseDirectory, [string]$ReleaseDirectory, [string]$ReleaseVersion, [bool]$Backup = $true) {
    Assert-Release $ReleaseDirectory $ReleaseVersion
    if ($Backup) { Save-CurrentInstallation $BaseDirectory }
    foreach ($name in @('properties', 'data')) {
        # Se aparta el directorio anterior para que no queden catalogos GC obsoletos mezclados.
        $destination = Join-Path $BaseDirectory $name
        if (Test-Path -LiteralPath $destination) {
            $archive = Join-Path $BaseDirectory ("backups\replaced\" + [guid]::NewGuid().ToString('N'))
            New-Item -ItemType Directory -Force -Path $archive | Out-Null
            $resolvedBase = [System.IO.Path]::GetFullPath($BaseDirectory).TrimEnd('\', '/') + [System.IO.Path]::DirectorySeparatorChar
            $resolvedTarget = [System.IO.Path]::GetFullPath($destination)
            if (-not $resolvedTarget.StartsWith($resolvedBase, [System.StringComparison]::OrdinalIgnoreCase)) { throw "Destino fuera de la instalacion: $destination" }
            Move-Item -LiteralPath $destination -Destination (Join-Path $archive $name)
        }
        Copy-Contents (Join-Path $ReleaseDirectory $name) $destination
    }
    $jar = Join-Path $ReleaseDirectory "import-from-gc-$ReleaseVersion.jar"
    Copy-Item -LiteralPath $jar -Destination (Join-Path $BaseDirectory "import-from-gc-$ReleaseVersion.jar") -Force
    Copy-Item -LiteralPath $jar -Destination (Join-Path $BaseDirectory 'import_from_opendata_gc.jar') -Force
    # El BAT estable sigue sirviendo para servicios y tareas existentes.
    foreach ($name in $scriptNames) {
        $script = Join-Path $ReleaseDirectory $name
        if (Test-Path -LiteralPath $script) { Copy-Item -LiteralPath $script -Destination (Join-Path $BaseDirectory $name) -Force }
    }
    @("version=$ReleaseVersion", "activated_at_utc=$((Get-Date).ToUniversalTime().ToString('o'))") |
        Set-Content -LiteralPath (Join-Path $BaseDirectory 'active-release.properties') -Encoding UTF8
}

$SourceDir = (Resolve-Path -LiteralPath $SourceDir).Path
$OutputDir = [System.IO.Path]::GetFullPath($OutputDir)
if ($Version -and $RollbackVersion) { throw 'Use Version o RollbackVersion, no ambos.' }
if ($RollbackVersion) {
    Assert-Version $RollbackVersion
    $release = Join-Path $OutputDir "versions\$RollbackVersion"
    Assert-Release $release $RollbackVersion
    if ($PSCmdlet.ShouldProcess($OutputDir, "Restaurar version $RollbackVersion, properties y datos GC")) {
        Activate-Release $OutputDir $release $RollbackVersion
        Write-Host "Version $RollbackVersion restaurada en $OutputDir"
    }
    return
}
$sourcePrefix = $SourceDir.TrimEnd('\', '/') + [System.IO.Path]::DirectorySeparatorChar
$outputPrefix = $OutputDir.TrimEnd('\', '/') + [System.IO.Path]::DirectorySeparatorChar
if ($outputPrefix.StartsWith($sourcePrefix, [System.StringComparison]::OrdinalIgnoreCase) -or
    $sourcePrefix.StartsWith($outputPrefix, [System.StringComparison]::OrdinalIgnoreCase)) {
    throw 'Origen y destino deben ser directorios separados, sin contenerse entre si.'
}
if (-not $Version) {
    [xml]$pom = Get-Content -Raw -LiteralPath (Join-Path $SourceDir 'pom.xml')
    $Version = [string]$pom.project.version
}
Assert-Version $Version
$target = Join-Path $SourceDir 'target'
$jar = Join-Path $target "import-from-gc-$Version.jar"
if (-not (Test-Path -LiteralPath $jar -PathType Leaf)) { throw "No existe el JAR compilado: $jar" }
if ((Get-JarVersion $jar) -ne $Version) { throw 'La version interna del JAR no coincide.' }
foreach ($name in @('properties', 'data\gc') + $scriptNames) {
    if (-not (Test-Path -LiteralPath (Join-Path $SourceDir $name))) { throw "Falta $name en $SourceDir" }
}
if (-not $PSCmdlet.ShouldProcess($OutputDir, "Publicar y activar version $Version, properties y datos GC")) { return }
$versions = Join-Path $OutputDir 'versions'
New-Item -ItemType Directory -Force -Path $versions | Out-Null
# Archiva primero la instalacion preexistente, incluida una instalacion antigua sin versions/.
Save-CurrentInstallation $OutputDir
$release = Join-Path $versions $Version
$staging = Join-Path $versions (".$Version.staging-" + [guid]::NewGuid().ToString('N'))
New-Item -ItemType Directory -Force -Path $staging | Out-Null
Copy-Item -LiteralPath $jar -Destination $staging
foreach ($name in $scriptNames) { Copy-Item -LiteralPath (Join-Path $SourceDir $name) -Destination $staging }
foreach ($name in @('properties', 'data')) { Copy-Contents (Join-Path $SourceDir $name) (Join-Path $staging $name) }
$notes = Join-Path $SourceDir "docs\releases\v$Version"
if (Test-Path -LiteralPath $notes) { Copy-Contents $notes (Join-Path $staging 'docs') }
Write-Manifest $staging $Version $SourceDir
Assert-Release $staging $Version
if (Test-Path -LiteralPath $release) {
    $archive = Join-Path $OutputDir ("backups\releases\$Version-" + [guid]::NewGuid().ToString('N'))
    New-Item -ItemType Directory -Force -Path (Split-Path -Parent $archive) | Out-Null
    $resolvedVersions = [System.IO.Path]::GetFullPath($versions).TrimEnd('\', '/') + [System.IO.Path]::DirectorySeparatorChar
    if (-not ([System.IO.Path]::GetFullPath($release)).StartsWith($resolvedVersions, [System.StringComparison]::OrdinalIgnoreCase)) { throw 'Ruta de version fuera de versions.' }
    Move-Item -LiteralPath $release -Destination $archive
}
Move-Item -LiteralPath $staging -Destination $release
Activate-Release $OutputDir $release $Version $false
Write-Host "Version $Version publicada y activada en $OutputDir"
