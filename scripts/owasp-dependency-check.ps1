param(
    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]] $MavenArgs = @()
)

$ErrorActionPreference = 'Stop'

if ([string]::IsNullOrWhiteSpace($env:NVD_API_KEY)) {
    Write-Error 'NVD_API_KEY no esta definida. Configurala en la sesion o en el gestor de secretos antes de ejecutar OWASP Dependency Check.'
}

$extraArgs = @('-B', 'org.owasp:dependency-check-maven:check', "-DnvdApiKey=$env:NVD_API_KEY")
if ($MavenArgs.Count -gt 0) {
    $extraArgs += $MavenArgs
}

& "$PSScriptRoot\..\mvnw.cmd" @extraArgs
exit $LASTEXITCODE
