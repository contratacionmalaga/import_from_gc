$basePath = "C:\ZZZ_Nubes\OneDrive\00.DESARROLLO\JAVA\librerias"
$outputFile = Join-Path $basePath "comandos_mvn.txt"

# Limpiar o crear el archivo de salida
"" | Out-File -FilePath $outputFile -Encoding utf8

# Lista de JARs a instalar
$libs = @(
    @{ file = "email-helper-3.1.0.jar"; groupId = "local.jarios"; artifactId = "email-helper"; version = "3.1.0" },
    @{ file = "properties-helper-3.2.0.jar"; groupId = "local.jarios"; artifactId = "properties-helper"; version = "3.2.0" },
    @{ file = "version-helper-2.2.0.jar"; groupId = "local.jarios"; artifactId = "version-helper"; version = "2.2.0" },
    @{ file = "encrypt-helper-2.1.0.jar"; groupId = "local.jarios"; artifactId = "encrypt-helper"; version = "2.1.0" }
)

# Expresión regular permitida: letras, números, guiones y puntos
$validPattern = '^[a-zA-Z0-9._-]+$'

foreach ($lib in $libs) {
    $filePath = Join-Path $basePath $lib.file

    # Validación de campos
    if ($lib.groupId -notmatch $validPattern) {
        Write-Warning "groupId inválido: $($lib.groupId)"
        continue
    }
    if ($lib.artifactId -notmatch $validPattern) {
        Write-Warning "artifactId inválido: $($lib.artifactId)"
        continue
    }
    if ($lib.version -notmatch $validPattern) {
        Write-Warning "version inválida: $($lib.version)"
        continue
    }

    # Limpiar caracteres no válidos
    $lib.groupId = $lib.groupId -replace '[^a-zA-Z0-9._-]', ''
    $lib.artifactId = $lib.artifactId -replace '[^a-zA-Z0-9._-]', ''
    $lib.version = $lib.version -replace '[^a-zA-Z0-9._-]', ''

    # Verificar la longitud del nombre del archivo
    if ($filePath.Length -gt 260) {
        Write-Warning "La ruta del archivo es demasiado larga: $filePath"
        continue
    }

    if (Test-Path $filePath) {
        $cmd = "mvn install:install-file -Dfile=`"$filePath`" -DgroupId=`"$($lib.groupId)`" -DartifactId=`"$($lib.artifactId)`" -Dversion=`"$($lib.version)`" -Dpackaging=jar"
        Write-Host "`nPreparando comando para $($lib.file)..."

        # Guardar el comando en el fichero
        $cmd | Out-File -FilePath $outputFile -Append -Encoding utf8

        # Si quieres ejecutar el comando, descomenta la siguiente línea:
        Invoke-Expression $cmd
    }
    else {
        Write-Warning "Archivo no encontrado: $filePath"
    }
}

Write-Host "`nComandos Maven guardados en: $outputFile"
