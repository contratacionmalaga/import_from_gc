# Import From GC

Aplicacion Java para importar ficheros GC de la PLACSP, parsearlos y persistir
logs, estadisticas, ficheros leidos y registros normalizados en MariaDB mediante
Hibernate.

## Estado del proyecto

- Java 21 y Maven 3.6.3 o superior.
- Hibernate con `hibernate.hbm2ddl.auto=validate` para evitar creacion o borrado
  automatico del esquema.
- Logback como backend de logging.
- Dependencias privadas publicadas en GitHub Packages.
- CI en GitHub Actions con build, tests, SpotBugs y OWASP Dependency Check.
- Paquete de release automatico al publicar una release en GitHub.

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

La importacion puede borrar registros de tablas gestionadas por la aplicacion y
recrear tablas dinamicas de GC. Por defecto debe mantenerse deshabilitada:

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
- `NVD_API_KEY`: API key de NVD para acelerar OWASP Dependency Check.

Los workflows leen esos secrets desde GitHub Actions. No deben aparecer en
`pom.xml`, `properties`, scripts ni documentacion.

## Verificacion

```powershell
mvn -B clean verify
mvn -B com.github.spotbugs:spotbugs-maven-plugin:4.9.8.2:check
mvn -B org.owasp:dependency-check-maven:check
```

El proyecto conserva deuda historica de Checkstyle. El umbral esta configurado
para permitir la migracion gradual, pero debe reducirse conforme se limpien las
violaciones.

## Releases

Al publicar una release en GitHub, el workflow `Release Package`:

- compila y verifica el proyecto;
- ejecuta SpotBugs y OWASP Dependency Check;
- publica el paquete Maven en GitHub Packages;
- adjunta a la release el JAR con dependencias, sources, javadocs y checksums.

## Auditoria

La auditoria tecnica esta en:

```text
doc/auditoria/2026_05_09/auditoria-proyecto.md
```

Puntos ya abordados:

- configuracion destructiva de Hibernate;
- secretos versionables;
- SQL nativo construido por concatenacion;
- CI y release package;
- Dependency Check con `NVD_API_KEY`;
- configuracion Checkstyle versionada.

Pendiente principal:

- ampliar tests automatizados;
- reducir deuda Checkstyle;
- seguir retirando estado global mutable;
- incorporar Maven Wrapper.
