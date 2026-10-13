# Import From GC

Aplicacion Java para importar ficheros GC de la PLACSP, parsearlos y persistir
logs, estadisticas, ficheros leidos y registros normalizados en MariaDB mediante
Hibernate.

## Estado del proyecto

- Version actual: **6.0.5**.
- Java 21.
- Maven Wrapper incluido con Maven 3.9.16 para builds reproducibles.
- Hibernate con `hibernate.hbm2ddl.auto=validate` para evitar creacion o borrado
  automatico del esquema.
- Parseo GC con Jakarta JAXB en Java 21.
- Logback como backend de logging.
- Dependencias privadas publicadas en GitHub Packages.
- CI en GitHub Actions con build, tests, SpotBugs y OWASP Dependency Check.
- Paquete de release automatico al publicar una etiqueta `v*` en GitHub.

## Novedades de 6.0.5

- Las altas de `log.id`, `estadistica.id` y `ficheros_gc.id` generan UUID v7
  mediante `@UuidGenerator(style = UuidGenerator.Style.VERSION_7)` de Hibernate.
- Los catalogos conservan `Generators.timeBasedEpochGenerator()`, que genera v7.
- `estadistica.log_id` y `ficheros_gc.log_id` guardan el mismo UUID de `log.id`;
  son referencias y no tienen generadores propios.
- Las pruebas en MariaDB 11.4 aislada comprueban `UUID.version() == 7`, el tipo
  nativo `UUID` de los identificadores y referencias, y la conservacion de IDs
  al actualizar con `merge`, incluidos registros existentes con UUID v4.
- CI, Quality y Release Package exigen Docker para estas pruebas: una base no
  disponible provoca un fallo en lugar de omitir la verificacion.

El cambio afecta a altas futuras. Los UUID v4 almacenados no se convierten ni
se regeneran al actualizar la misma fila. La importacion completa conserva su
comportamiento destructivo documentado abajo: las filas borradas y recreadas
reciben identificadores nuevos.

## Configuracion local

Los ficheros `properties/*.properties` reales son locales y no deben versionarse
con secretos. Usa las plantillas `properties/*.properties.example` como base.

Variables de entorno soportadas:

- `IMPORT_FROM_GC_JDBC_URL`
- `IMPORT_FROM_GC_JDBC_DRIVER`
- `IMPORT_FROM_GC_JDBC_USER`
- `IMPORT_FROM_GC_JDBC_PASSWORD`
- `IMPORT_FROM_GC_MAIL_USER`
- `IMPORT_FROM_GC_MAIL_PASSWORD`
- `IMPORT_FROM_GC_MAIL_FROM`
- `IMPORT_FROM_GC_MAIL_TO`
- `IMPORT_FROM_GC_ALLOW_DESTRUCTIVE_IMPORT`

La importacion esta disenada como reemplazo destructivo del estado gestionado
por la aplicacion y no conserva historico interno de ejecuciones anteriores.
Puede borrar registros y recrear tablas dinamicas de GC, por lo que por
defecto debe mantenerse deshabilitada:

```properties
app.allowDestructiveImport=false
```

Para ejecutar una importacion real contra una base preparada, habilitala de forma
explicita en `properties/app.properties` o mediante entorno:

```powershell
$env:IMPORT_FROM_GC_ALLOW_DESTRUCTIVE_IMPORT = "true"
```

## GitHub Actions y secretos

Configura estos secrets en el repositorio:

- `PACKAGES_TOKEN`: token con acceso a GitHub Packages privados usados por los
  helpers internos.
- `NVD_API_KEY`: API key de NVD obligatoria para OWASP Dependency Check en CI y ejecuciones locales recurrentes.

Los workflows leen esos secrets desde GitHub Actions. No deben aparecer en
`pom.xml`, `properties`, scripts ni documentacion.

## Verificacion

```powershell
.\mvnw.cmd -B clean verify
.\mvnw.cmd -B -DimportFromGc.requireDocker=true clean verify
.\mvnw.cmd -B com.github.spotbugs:spotbugs-maven-plugin:4.9.8.2:check
.\scripts\owasp-dependency-check.ps1
```

Checkstyle esta cerrado en 0 avisos y el umbral Maven tambien esta en 0. OWASP Dependency Check debe ejecutarse con `NVD_API_KEY`; el script local falla antes de invocar Maven si la variable no existe.

La segunda orden requiere Docker en ejecucion y verifica las entidades y los
catalogos en un contenedor `mariadb:11.4` temporal con `create-drop`. No utiliza
las credenciales ni la base de produccion. Sin Docker, la orden local por defecto
omite las pruebas de integracion; los workflows siempre las exigen.

## Releases

Al publicar una etiqueta `v*` en GitHub, el workflow `Release Package`:

- compila y verifica el proyecto;
- publica el paquete Maven en GitHub Packages;
- crea la release y adjunta los JAR principal, con dependencias, sources y javadocs.

El workflow `Quality` ejecuta Spotless, Checkstyle y SpotBugs antes de integrar
los cambios. OWASP Dependency Check dispone de su propio workflow semanal y
de ejecucion manual.

Para desplegar 6.0.5, utiliza `import-from-gc-6.0.5-jar-with-dependencies.jar`
publicado en la release `v6.0.5`. Sustituye el JAR configurado en el servicio o
tarea de importacion, manteniendo sus ficheros de configuracion externos, y
comprueba que el proceso utiliza ese archivo y muestra la version 6.0.5.
No se necesita migrar ni reescribir los identificadores existentes.

## Auditoria

La auditoria tecnica esta en:

```text
docs/auditorias/auditoria-viva-2026-08-12.md
```

Puntos ya abordados:

- configuracion destructiva de Hibernate;
- secretos versionables;
- SQL nativo construido por concatenacion;
- CI y release package;
- Dependency Check con `NVD_API_KEY` obligatorio en CI y script local;
- configuracion Checkstyle versionada.

Pendiente principal:

- ampliar tests automatizados;
- reducir deuda Checkstyle;
- seguir retirando estado global mutable;
- mantener actualizada la auditoria viva en `docs/auditorias`.
