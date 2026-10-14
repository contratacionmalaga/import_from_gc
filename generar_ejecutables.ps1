param(
    [string]$OutputDir = "C:\java\ejecutables\import-from-opendata-gc-ejecutables",
    [bool]$SkipTests = $true,
    [switch]$SkipClean,
    [switch]$NoDeploy
)
$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest
$mavenScript = Join-Path $PSScriptRoot 'scripts\use-java21-maven3916.ps1'
if (-not (Test-Path -LiteralPath $mavenScript)) { throw "No existe $mavenScript. Compile desde el proyecto de desarrollo." }
Push-Location $PSScriptRoot
try {
    $mavenArgs = @()
    if (-not $SkipClean) { $mavenArgs += 'clean' }
    $mavenArgs += 'package'
    if ($SkipTests) { $mavenArgs += '-DskipTests' }
    & $mavenScript @mavenArgs
    if ($LASTEXITCODE -ne 0) { throw "Fallo Maven: $LASTEXITCODE" }
    if (-not $NoDeploy) {
        & (Join-Path $PSScriptRoot 'publicar_ejecutables.ps1') -SourceDir $PSScriptRoot -OutputDir $OutputDir
    } else { Write-Host "JAR generado en target. Publicacion omitida por -NoDeploy." }
} finally { Pop-Location }
