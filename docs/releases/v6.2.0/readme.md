# Release 6.2.0

## Novedades

- Compilacion y publicacion local automatica mediante `generar_ejecutables.ps1`.
  Utiliza Java 21 y el Maven Wrapper 3.9.16 del proyecto.
- Destino predeterminado:
  `C:\java\ejecutables\import-from-opendata-gc-ejecutables`.
- Versiones completas en `versions/<version>/`: JAR ejecutable, properties,
  catalogos GC, scripts, documentacion y manifiesto SHA256.
- Copia de la instalacion anterior en `backups/installation/`. Una instalacion
  antigua sin estructura de versiones se identifica por el manifiesto del JAR
  y se archiva con sus propios datos y configuracion; incluye la version 6.0.5.
- Al republicar una version, la copia previa se conserva en `backups/releases/`.
  Las versiones antiguas no se eliminan automaticamente.
- Restauracion mediante `-RollbackVersion` y simulacion mediante `-WhatIf`.
- El BAT conserva el nombre estable `import_from_opendata_gc.jar` y el codigo
  de salida de Java. El JAR versionado tambien queda disponible en la raiz.
- Pruebas aisladas del flujo de publicacion, migracion, hashes y rollback.
- La release adjunta ejecutable, sources, javadocs, este README y un ZIP
  con los scripts de despliegue. El ZIP no incluye configuraciones locales.

## Actualizacion desde el proyecto de desarrollo

Desde `C:\java\desarrollo\import-from-gc`:

```powershell
.\generar_ejecutables.ps1
```

Compila y publica solo si Maven termina correctamente. Los tests se omiten por
defecto; para ejecutarlos utiliza `-SkipTests $false`. `-SkipClean` omite clean,
`-NoDeploy` compila sin copiar y `-OutputDir` cambia el destino.

Para publicar los archivos ya compilados en `target/`:

```powershell
.\publicar_ejecutables.ps1 -WhatIf
.\publicar_ejecutables.ps1
```

La publicacion copia las properties y los catalogos de desarrollo a la
instalacion. Conserva antes los archivos activos en backups y aparta los
directorios sustituidos en `backups/replaced/` para evitar mezclar catalogos.
Los logs permanecen en la carpeta operativa.

## Actualizacion con los archivos de GitHub Release

Descomprime `import-from-gc-6.2.0-deployment-scripts.zip` en una carpeta de
preparacion separada del destino. Crea `target/`, coloca dentro el ejecutable
`import-from-gc-6.2.0.jar` descargado de esta release y prepara `properties/`
y `data/gc/` con la configuracion y los catalogos que quieras activar.

El ZIP incluye los scripts en la raiz; puedes publicar sin el codigo fuente:

```powershell
.\publicar_ejecutables.ps1 -Version 6.2.0 -WhatIf
.\publicar_ejecutables.ps1 -Version 6.2.0
```

Para compilar con `generar_ejecutables.ps1` necesitas el repositorio completo,
incluidos `pom.xml`, `mvnw.cmd` y `.mvn/`.

## Volver a una version anterior

Desde el proyecto de desarrollo o desde la carpeta operativa:

```powershell
.\publicar_ejecutables.ps1 -RollbackVersion 6.0.5 -WhatIf
.\publicar_ejecutables.ps1 -RollbackVersion 6.0.5
```

La version indicada debe existir en `versions/`. El rollback restaura JAR,
properties y catalogos GC y conserva una copia de la instalacion sustituida.
No revierte cambios de una importacion ya ejecutada en la base de datos.

Publicar y restaurar archivos no ejecuta Java ni inicia una importacion.
Esta version no requiere una migracion del esquema ni repetir la importacion.

## Verificacion

```powershell
.\scripts\test-publicar-ejecutables.ps1
```

Las pruebas utilizan JARs sinteticos y directorios temporales, sin conexiones
a bases de datos. Comprueban migracion de la 6.0.5, manifiestos SHA256,
restauracion de properties y catalogos sin mezclar versiones, republicacion,
rollback desde la carpeta operativa y validaciones sin modificar archivos.
