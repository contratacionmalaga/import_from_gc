param(
    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]]$MavenArgs
)
$ErrorActionPreference = 'Stop'
$javaHome = 'C:\java\software\jdk-21.0.11'
if (-not (Test-Path -LiteralPath "$javaHome\bin\java.exe")) {
    throw "No se encuentra Java 21 en $javaHome"
}
$wrapper = Join-Path (Split-Path -Parent $PSScriptRoot) 'mvnw.cmd'
if (-not (Test-Path -LiteralPath $wrapper)) { throw "No se encuentra Maven Wrapper: $wrapper" }
$env:JAVA_HOME = $javaHome
$env:Path = "$javaHome\bin;$env:Path"
& $wrapper @MavenArgs
exit $LASTEXITCODE
