$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest
Add-Type -AssemblyName System.IO.Compression.FileSystem
$repo = Split-Path -Parent $PSScriptRoot
$root = Join-Path ([System.IO.Path]::GetTempPath()) ('gc-publication-tests-' + [guid]::NewGuid().ToString('N'))
$source = Join-Path $root 'source'
$output = Join-Path $root 'output'
foreach ($path in @('target','properties','data\gc')) { New-Item -ItemType Directory -Force -Path (Join-Path $source $path) | Out-Null }
foreach ($name in @('publicar_ejecutables.ps1','generar_ejecutables.ps1','import_from_opendata_gc.bat')) {
    Copy-Item -LiteralPath (Join-Path $repo $name) -Destination $source
}
$publisher = Join-Path $repo 'publicar_ejecutables.ps1'
$utf8 = [System.Text.UTF8Encoding]::new($false)
function Write-Text([string]$Path, [string]$Text) { [System.IO.File]::WriteAllText($Path, $Text, $utf8) }
function Assert([bool]$Condition, [string]$Message) { if (-not $Condition) { throw $Message } }
function New-Jar([string]$Path, [string]$Version) {
    $zip = [System.IO.Compression.ZipFile]::Open($Path, [System.IO.Compression.ZipArchiveMode]::Create)
    try {
        $entry = $zip.CreateEntry('META-INF/MANIFEST.MF')
        $writer = [System.IO.StreamWriter]::new($entry.Open())
        try { $writer.Write("Manifest-Version: 1.0`r`nImplementation-Version: $Version`r`nMain-Class: local.jarios.ImportFromGc`r`n`r`n") } finally { $writer.Dispose() }
    } finally { $zip.Dispose() }
}
Write-Text (Join-Path $source 'pom.xml') '<project><version>6.1.0</version></project>'
Write-Text (Join-Path $source 'properties\app.properties') 'new-configuration'
Write-Text (Join-Path $source 'data\gc\new.gc') 'new-data'
New-Jar (Join-Path $source 'target\import-from-gc-6.1.0.jar') '6.1.0'
& $publisher -SourceDir $source -OutputDir $output -WhatIf
Assert (-not (Test-Path -LiteralPath $output)) 'WhatIf ha creado archivos'
New-Item -ItemType Directory -Force -Path (Join-Path $output 'properties'),(Join-Path $output 'data\gc') | Out-Null
Write-Text (Join-Path $output 'properties\app.properties') 'old-configuration'
Write-Text (Join-Path $output 'data\gc\old.gc') 'old-data'
New-Jar (Join-Path $output 'import_from_opendata_gc.jar') '6.0.5'
$oldHash = (Get-FileHash -LiteralPath (Join-Path $output 'import_from_opendata_gc.jar')).Hash
& $publisher -SourceDir $source -OutputDir $output
Assert ((Get-FileHash -LiteralPath (Join-Path $output 'versions\6.0.5\import-from-gc-6.0.5.jar')).Hash -eq $oldHash) 'JAR antiguo no conservado'
Assert ((Get-Content -Raw -LiteralPath (Join-Path $output 'versions\6.0.5\properties\app.properties')) -eq 'old-configuration') 'Configuracion antigua no conservada'
Assert (Test-Path -LiteralPath (Join-Path $output 'versions\6.0.5\data\gc\old.gc')) 'Datos antiguos no conservados'
Assert (-not (Test-Path -LiteralPath (Join-Path $output 'data\gc\old.gc'))) 'Catalogos antiguos mezclados'
Assert (Test-Path -LiteralPath (Join-Path $output 'data\gc\new.gc')) 'Faltan datos nuevos'
Assert ((Get-FileHash -LiteralPath (Join-Path $output 'import_from_opendata_gc.jar')).Hash -eq (Get-FileHash -LiteralPath (Join-Path $source 'target\import-from-gc-6.1.0.jar')).Hash) 'Alias no actualizado'
$manifest = Get-Content -Raw -LiteralPath (Join-Path $output 'versions\6.1.0\manifest.json') | ConvertFrom-Json
foreach ($file in $manifest.files) {
    Assert ((Get-FileHash -LiteralPath (Join-Path $output "versions\6.1.0\$($file.path)")).Hash -eq $file.sha256) "Hash incorrecto: $($file.path)"
}
& $publisher -SourceDir $source -OutputDir $output -RollbackVersion '6.0.5'
Assert ((Get-FileHash -LiteralPath (Join-Path $output 'import_from_opendata_gc.jar')).Hash -eq $oldHash) 'Rollback no restaura JAR'
Assert ((Get-Content -Raw -LiteralPath (Join-Path $output 'properties\app.properties')) -eq 'old-configuration') 'Rollback no restaura properties'
Assert (Test-Path -LiteralPath (Join-Path $output 'data\gc\old.gc')) 'Rollback no restaura datos'
Assert (-not (Test-Path -LiteralPath (Join-Path $output 'data\gc\new.gc'))) 'Rollback mezcla catalogos'
& $publisher -SourceDir $source -OutputDir $output
Assert (@(Get-ChildItem -LiteralPath (Join-Path $output 'backups\releases') -Directory).Count -eq 1) 'Republicar no conserva copia anterior'
& (Join-Path $output 'publicar_ejecutables.ps1') -OutputDir $output -RollbackVersion '6.0.5'
Assert ((Get-FileHash -LiteralPath (Join-Path $output 'import_from_opendata_gc.jar')).Hash -eq $oldHash) 'Rollback desde la instalacion falla'
& (Join-Path $output 'publicar_ejecutables.ps1') -OutputDir $output -RollbackVersion '6.1.0'
$before = (Get-FileHash -LiteralPath (Join-Path $output 'active-release.properties')).Hash
try { & $publisher -SourceDir $source -OutputDir $output -Version '6.2.0'; throw 'Se acepto un JAR inexistente' } catch {
    Assert ($_.Exception.Message -like 'No existe el JAR compilado:*') 'Error inesperado con JAR inexistente'
}
try { & $publisher -SourceDir $source -OutputDir $output -RollbackVersion '..\otra'; throw 'Se acepto una version insegura' } catch {
    Assert ($_.Exception.Message -like 'Version no valida:*') 'Error inesperado con version insegura'
}
Assert ((Get-FileHash -LiteralPath (Join-Path $output 'active-release.properties')).Hash -eq $before) 'Una validacion fallida ha modificado la instalacion'
Write-Host "Publicacion, migracion 6.0.5, SHA256, rollback, republicacion y validaciones correctos. Evidencias: $root"
