# Release 6.3.0

## Cambios

- Logging con el mismo formato operativo que `import_from_opendata`: progreso
  y errores resumidos en consola; las trazas completas quedan en el log tecnico.
- El ejecutable incorpora `logback-classic` y `log4j-to-slf4j`. Se elimina el
  aviso `No SLF4J providers were found` que ocultaba los errores de importacion.
- Cada ejecucion genera logs UTF-8 de actividad, errores e Hibernate/Hikari/JDBC,
  sin mensajes duplicados. Los errores conservan el mensaje y la excepcion.
- `properties/bd.properties` sustituye a `jakarta_principal.properties` tanto
  para la conexion JDBC como para el contexto de diagnostico del destino.
- Dependencias y plugins compartidos alineados con `import_from_opendata`
  mediante `jarios-parent:1.0.15`: Hibernate 7.4.10.Final, SLF4J 2.0.20,
  Logback 1.6.4 y Log4j 2.26.1.
- Helpers alineados: encrypt 7.1.0, email 6.2.0, properties 6.1.0 y version 6.2.0.
- Corregidas las claves `hibernate.*` de la configuracion versionada y anadida
  `properties/hibernate.properties.example`. El esquema se valida con
  `hibernate.hbm2ddl.auto=validate`.
- Se conservan los scripts de publicacion, copias de seguridad y rollback de
  la version 6.2.0. La release adjunta el ejecutable, sources, javadocs,
  scripts de despliegue y este `readme.md`.

## Actualizacion de la configuracion

Conserva los valores de conexion que utiliza tu instalacion y renombra:

```text
properties/jakarta_principal.properties -> properties/bd.properties
```

Si `bd.properties` ya existe, conserva ese fichero; no lo sobrescribas con una
plantilla. Las claves del fichero siguen siendo:

```properties
jakarta.persistence.jdbc.url=...
jakarta.persistence.jdbc.driver=org.mariadb.jdbc.Driver
jakarta.persistence.jdbc.user=...
jakarta.persistence.jdbc.password=...
```

Las variables `IMPORT_FROM_GC_JDBC_URL`, `IMPORT_FROM_GC_JDBC_DRIVER`,
`IMPORT_FROM_GC_JDBC_USER` e `IMPORT_FROM_GC_JDBC_PASSWORD` siguen teniendo
prioridad sobre el fichero. Las configuraciones locales con credenciales
no se incluyen en los archivos publicados en GitHub.

## Logs

Los ficheros de cada ejecucion se guardan en:

```text
logs/import-from-gc_<ejecucion>.log
logs/import-from-gc_error_<ejecucion>.log
logs/import-from-gc_hibernate_<ejecucion>.log
```

Rotan a los 25 MB y se comprimen en `logs/archive/`. Se conservan hasta
30 dias para actividad, 90 para errores y 14 para Hibernate. Puedes fijar
el identificador con `-Dgc.execution.id=...` y el directorio con
`-Dgc.log.path=...`, antes de `-jar`.

## Despliegue

Sustituye el ejecutable por `import-from-gc-6.3.0.jar`. El BAT puede conservar
su nombre estable y ejecutar una copia llamada `import_from_opendata_gc.jar`.
Conserva la configuracion operativa, los catalogos `data/gc/` y los logs.

```powershell
java -jar import-from-gc-6.3.0.jar
```

Para compilar y publicar desde desarrollo con los scripts existentes:

```powershell
.\generar_ejecutables.ps1 -NoDeploy
.\publicar_ejecutables.ps1 -WhatIf
.\publicar_ejecutables.ps1
```

`publicar_ejecutables.ps1` copia tambien la configuracion y los catalogos
del origen al destino. Utiliza como origen los valores que quieras activar;
antes de sustituirlos, conserva la instalacion anterior en backups y `versions/`.
Desde una carpeta de preparacion con los archivos de GitHub, puedes utilizar
`publicar_ejecutables.ps1 -Version 6.3.0`.

Publicar los archivos no inicia Java ni ejecuta una importacion. Esta release
no requiere una migracion del esquema ni repetir una importacion ya realizada.
El envio de correo sigue desactivado por defecto y la importacion conserva su
control explicito `app.allowDestructiveImport`.

Para restaurar una version archivada:

```powershell
.\publicar_ejecutables.ps1 -RollbackVersion 6.2.0
```

El rollback restaura los archivos guardados de esa version, incluida su
configuracion. No revierte los datos ya importados en MariaDB.

## Verificacion

```powershell
.\mvnw.cmd -B -ntp verify
.\mvnw.cmd -B -ntp -DimportFromGc.requireDocker=true -Pquality verify
.\scripts\test-publicar-ejecutables.ps1
```

La verificacion de la release utiliza los 102 catalogos versionados del
repositorio, sin las copias ni los catalogos modificados de una instalacion local.
Las pruebas de logging comprueban el proveedor de SLF4J, el puente de Log4j,
la separacion de ficheros y la ausencia de duplicados en consola. CI exige
Docker y comprueba la persistencia contra una instancia temporal de MariaDB.
