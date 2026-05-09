# Importar Ficheros GC (configuración de PLACSP) desde EXCEL a una base de datos

## Información General del Aplicativo
`ImportFromGc` es una aplicación Java destinada a la importación de información desde ficheros Excel (GC) hacia un sistema, procesando dichos ficheros, almacenando estadísticas y persistiendo la información en una base de datos. El programa incluye gestión avanzada de logs y manejo de excepciones para facilitar el diagnóstico de errores.

---

## Características principales

- Lectura y parseo de ficheros GC desde una ruta configurada.
- Almacenamiento de estadísticas de ejecución (número de ficheros leídos, procesados, registros, tiempos).
- Persistencia de logs, ficheros y estadísticas en base de datos.
- Gestión de configuración mediante ficheros properties utilizando patrón Singleton.
- Manejo de excepciones específicas y logging detallado con `slf4j`.
- Modularidad y organización clara para facilitar mantenimiento y ampliaciones.

---

## Herramientas utilizadas
- __Lenguaje de Programación__: Java en su versión `v21.0.7`.
- __Base de datos__: MariaDB en su versión `11.6` haciendo uso de esquemas.
- __Versión del framework Hibernate__: HikariCP en su versión `7.0.0` para core y `7.0.0` para HikariCP
- __Biblioteca Lombok__: Biblioteca que facilita la programación mediante la inyección de código mediante etiquetas. Utilizamos la versión `1.18.38`.
- __Gestión de Logs__: Utilizamos `slf4j` como fachada y `logback` como implementación de logging.
- Parseo de los ficheros GC con
    - __jaxb-core__: `2.3.0.1`
    - __jaxb-imp__: `2.3.0.1`
    - __jaxb-api__: `2.4.0-b180830.0359`
    - __javax.activation-api__: `1.2.0`

---

## Requisitos

- Java 21+ (o versión compatible)
- Maven 3.6.3+ hasta que el proyecto incorpore Maven Wrapper.
- Dependencias:
  - Lombok (para anotaciones como `@Slf4j`)
  - Framework de persistencia compatible (Hibernate, JPA, JDBC, etc. según implementación en `ServiceImpl`)
- Base de datos configurada y accesible
- Ficheros GC en formato Excel ubicados en la ruta configurada en el fichero properties

## Configuración

Los ficheros `properties/*.properties` locales pueden contener valores específicos de entorno y no deben incluir secretos reales en el repositorio. Se incluyen plantillas `*.properties.example` para crear la configuración local.

Variables de entorno soportadas:

- `IMPORT_FROM_GC_JDBC_URL`
- `IMPORT_FROM_GC_JDBC_DRIVER`
- `IMPORT_FROM_GC_JDBC_USER`
- `IMPORT_FROM_GC_JDBC_PASSWORD`
- `IMPORT_FROM_GC_MAIL_USER`
- `IMPORT_FROM_GC_MAIL_PASSWORD`
- `IMPORT_FROM_GC_MAIL_FROM`
- `IMPORT_FROM_GC_MAIL_TO`

Por seguridad, `hibernate.hbm2ddl.auto` debe mantenerse como `validate` o `none` fuera de entornos locales controlados. La aplicación puede borrar y recrear tablas dinámicas durante la importación, por lo que no debe ejecutarse contra una base compartida sin validar antes el modo de operación.

## Verificación

```powershell
mvn clean verify
mvn spotbugs:check
mvn org.owasp:dependency-check-maven:check
```

## Actuaciones realizadas

* Auditoría técnica generada en `doc/auditoria/2026_05_09`.
* Configuración endurecida para evitar secretos versionados y creación destructiva de esquema por defecto.
    

***

<p>
Juan Antonio Ríos Peláez
</p>

`jarios@malaga.es`
