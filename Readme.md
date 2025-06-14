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
- __Gestión de Logs__: Utilizamos `slf4j` como fachada para los logs de los componentes y como elemento generador de logs utilizamos `log4j`. Las versiones de los productos utilizadas son las siguientes:
    - __Fachada__: `slf4j` versión `2.0.16` junto con la implementación `log4j-slf4j2-impl` para `log4j` en su versión `2.24.3`.
    - __Gestor de Logs__: `log4j` en su versión `2.24.3`.
- Parseo de los ficheros GC con
    - __jaxb-core__: `2.3.0.1`
    - __jaxb-imp__: `2.3.0.1`
    - __jaxb-api__: `2.4.0-b180830.0359`
    - __javax.activation-api__: `1.2.0`

---

## Requisitos

- Java 11+ (o versión compatible)
- Dependencias:
  - Lombok (para anotaciones como `@Slf4j`)
  - Framework de persistencia compatible (Hibernate, JPA, JDBC, etc. según implementación en `ServiceImpl`)
- Base de datos configurada y accesible
- Ficheros GC en formato Excel ubicados en la ruta configurada en el fichero properties

## Actuaciones realizadas

* __TODO__
    

***

<p>
Juan Antonio Ríos Peláez
</p>

`jarios@malaga.es`
