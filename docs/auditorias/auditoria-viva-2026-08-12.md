# Auditoria viva del proyecto import-from-gc

Fecha de auditoria: 2026-08-12
Ruta auditada: `C:\java\desarrollo\import-from-gc`
Version declarada en `pom.xml`: `6.0.0`
Stack principal: Java 21, Maven, Hibernate ORM 7, MariaDB, JAXB, Jackson/Gson, SLF4J/Logback.

## Como mantener viva esta auditoria

Este documento debe actualizarse cada vez que se cierre un hito. La regla practica es:

- Cambiar el estado del hito de `[ ]` a `[x]`.
- Anadir fecha de cierre y PR/commit, si existe.
- Mover cualquier riesgo residual a la seccion "Seguimiento".
- Ajustar el resumen ejecutivo si cambia el riesgo global.
- Registrar nuevas versiones disponibles en la tabla de dependencias cuando se revisen.

Estados recomendados:

- `[ ] Pendiente`
- `[~] En curso`
- `[x] Cerrado`
- `[!] Bloqueado`

## Resumen ejecutivo

El proyecto ha mejorado de forma clara respecto a la auditoria previa de 2026-05-09. Se han corregido varios puntos criticos: el workflow de GitHub Actions esta en `.github/workflows`, Hibernate usa `hibernate.hbm2ddl.auto=validate`, los properties reales estan ignorados por Git, OWASP Dependency Check ya no aparece desactivado en el POM, existen tests unitarios y el SQL dinamico valida identificadores y parametriza valores.

El riesgo global actual baja de alto a **medio-bajo**. Se han cerrado build reproducible, cobertura base, runner testeable, release sin `-DskipTests`, accion compartida para Maven privado, limpieza de dependencias directas, actualizaciones conservadoras y migracion JAXB a Jakarta. La deuda Checkstyle queda eliminada por completo y el umbral temporal queda cerrado en 0. OWASP Dependency Check queda cerrado para CI y uso local recurrente: los workflows exigen `secrets.NVD_API_KEY`, el secret existe en GitHub y el script local falla si `NVD_API_KEY` no esta definido. La generacion Javadoc de Maven queda limpia sin warnings.

## Alcance y limitaciones

Comprobaciones locales realizadas:

- Inventario de ficheros con `rg --files`.
- Estado Git con `git status --short --ignored`.
- Revision de `pom.xml`, `Readme.md`, `.github/workflows`, `properties`, `src/main/java`, `src/test/java` y reports bajo `target`.
- Revision puntual de clases de persistencia, politica destructiva, normalizacion de nombres de tabla, bootstrap, configuracion y serializacion JSON.
- Consulta externa de versiones vigentes en Maven Central, MvnRepository, Apache Maven, Oracle/OpenJDK y Adoptium.

Limitaciones:

- `mvn` no esta disponible en el `PATH`, pero el proyecto ya incluye Maven Wrapper y se pudo ejecutar `.\mvnw.cmd -version` y `.\mvnw.cmd -B clean verify`.
- Maven Wrapper queda incorporado con Maven 3.9.16; SpotBugs, versions, dependency:analyze y OWASP Dependency Check quedan ejecutados localmente. Para ejecuciones recurrentes, OWASP debe ejecutarse mediante `scripts/owasp-dependency-check.ps1` con `NVD_API_KEY`.
- La ultima verificacion completa revisada procede de `.\mvnw.cmd -B clean verify`: 20 tests, Checkstyle 0 y Javadoc Maven sin warnings.
- No se audito la base de datos real ni datos productivos.
- Las dependencias privadas `local.jarios:*` no se pudieron contrastar con GitHub Packages desde este entorno. El parent local `C:\java\desarrollo\jarios-parent` se actualizo a `1.0.4` y se publico correctamente en GitHub Packages el 2026-08-12.

## Estado actual observado

- Codigo principal: 44 ficheros bajo `src/main/java` segun inventario `rg --files`.
- Tests: 8 ficheros bajo `src/test/java`.
- Datos GC: 102 ficheros bajo `data/gc`.
- Tests ya ejecutados en `target/surefire-reports`: 20 tests, 0 fallos, 0 errores, 0 omitidos con Docker Desktop activo.
- Checkstyle: 0 avisos tras limpiar helpers, mappers, servicios, repositorios, DTOs JAXB, excepciones, enum y entidades JPA; el umbral queda en `checkstyle.max.violations=0`.
- Properties reales ignorados: `properties/app.properties`, `properties/jakarta_principal.properties`, `properties/mail.properties`.
- Directorio solicitado para auditorias: creado en `docs/auditorias`.
- Existe una auditoria historica en `doc/auditoria/2026_05_09/auditoria-proyecto.md`; conviene mantenerla solo como historico o migrarla a `docs/auditorias`.

## Fortalezas

- Separacion basica por capas: entrada, helpers, mappers, repositories, entities y database.
- Java 21 declarado en compilacion.
- Versiones centralizadas en propiedades Maven.
- CI y release workflows presentes y ubicados correctamente.
- Uso de variables de entorno para secretos JDBC y SMTP.
- `hibernate.hbm2ddl.auto=validate`, evitando generacion destructiva automatica de esquema.
- Politica explicita para operaciones destructivas: `app.allowDestructiveImport` o `IMPORT_FROM_GC_ALLOW_DESTRUCTIVE_IMPORT`.
- Normalizacion de nombres de tabla y validacion de identificadores SQL.
- Inserciones nativas parametrizadas.
- Tests unitarios iniciales para normalizacion de tablas y politica destructiva.

## Hallazgos

### H1 - Build local no reproducible

Prioridad: P0
Estado: `[x] Cerrado el 2026-08-12`
Evidencia: se anadio Maven Wrapper 3.3.4 con Maven 3.9.16; `.\mvnw.cmd -version` y `.\mvnw.cmd -B clean verify` ejecutan correctamente.

Impacto: cualquier desarrollador o agente sin Maven instalado queda bloqueado para validar cambios. El README recomienda Maven, pero el repositorio no trae la version ejecutable.

Recomendacion:

- Anadir Maven Wrapper apuntando a Maven 3.9.16 o una version estable aprobada.
- Documentar `.\mvnw.cmd -B clean verify` como comando principal.
- Subir `.mvn/wrapper/maven-wrapper.properties`, `mvnw` y `mvnw.cmd`.

Criterio de cierre:

- `.\mvnw.cmd -B clean verify` ejecuta en una maquina limpia con JDK 21.
- El README usa wrapper en todos los comandos.

### H2 - Cobertura de tests insuficiente

Prioridad: P0
Estado: `[x] Cerrado el 2026-08-12`
Evidencia: hay 7 clases de test. `GenericodeCatalogContractTest` parsea y mapea los 102 ficheros `.gc`; los tests de mappers cubren transformaciones clave; `RegistroGcDaoMariaDbIntegrationTest` valida DDL/DML con MariaDB/Testcontainers; `ImportFromGcRunnerTest` cubre flujo feliz y error sin terminar la JVM.

Impacto: la red de seguridad base ya existe para parseo, mappers, persistencia dinamica y orquestacion principal. Queda pendiente ampliar cobertura de serializacion y casos extremos, pero ya no bloquea actualizaciones controladas.

Recomendacion:

- Anadir tests de contrato para parsear todos los ficheros de `data/gc`.
- Cubrir `MapperFicheroGcFromCodeList`, `MapperRegistroGcFromCodeList`, `FicheroGcParser`, `FileHelper` y `CodeListHelper`.
- Extraer un `run()` testeable desde `ImportFromGc` y dejar `System.exit` solo en `main`.
- Anadir integracion con Testcontainers MariaDB para validar DDL/DML real.

Criterio de cierre:

- Tests unitarios cubren parseo/mapeo/configuracion.
- Existe al menos una prueba de integracion de persistencia.
- El flujo principal puede probarse sin terminar la JVM.

### H3 - Deuda Checkstyle muy alta y no bloqueante

Severidad: media.
Estado: `[x] Cerrado el 2026-08-12`

Evidencia inicial: `target/checkstyle-result.xml` contenia 1520 avisos y `checkstyle.max.violations=10000`.

Accion aplicada: se eliminaron tabuladores de los ficheros Java, se normalizaron `ImportFromGc.java`, `common/util`, `RegistroGcDao.java`, `RepositoryImpl.java`, `database/*`, `TransactionManager.java`, `FileHelper.java`, `FicheroGcParser.java`, `MapperRegistroGcFromCodeList.java`, `ServiceImpl.java`, helpers/mappers adicionales, repositorios, servicios y Javadocs privados, y se bajo el umbral temporal a `checkstyle.max.violations=300`.

Resultado actual: Checkstyle queda en 0 avisos. `.\mvnw.cmd -B clean verify` pasa con `checkstyle.max.violations=0`, 20 tests correctos y generacion Javadoc de Maven sin warnings.

Riesgo residual: cerrado para Checkstyle. Se mantienen supresiones locales justificadas en `Estadistica` para nombres legacy (`nTotalFicherosLeidos`, `nRegistrosGc`, `aumentarNRegistrosGc`) sin cambiar API ni columnas.

Siguiente paso: mantener Checkstyle en 0 en CI y no aceptar nuevas supresiones sin justificacion de contrato/API.
### H4 - Release ejecuta deploy saltando tests

Severidad: media.
Estado: `[x] Cerrado el 2026-08-12`

Evidencia inicial: `.github/workflows/release-package.yml` ejecutaba `mvn -B clean verify`, SpotBugs y OWASP, pero despues publicaba con `mvn -B -DskipTests deploy`.

Accion aplicada: el workflow de release publica con `mvn -B deploy`, sin `-DskipTests`. La configuracion Maven privada queda centralizada en `.github/actions/setup-maven-private/action.yml` y se reutiliza desde CI y release.

Riesgo residual: el deploy vuelve a recorrer el ciclo Maven y tarda mas, pero evita publicar con tests saltados. Si se quiere promocionar exactamente el binario verificado, habria que separar build/publicacion con artefactos firmados.

Criterio de cierre: cumplido con `.github/workflows/release-package.yml` y verificado con `rg` sobre workflows.
### H5 - Dependencias y propiedades declaradas sin uso claro

Severidad: media.
Estado: `[x] Cerrado el 2026-08-12`

Accion aplicada: `dependency:analyze` detecto dependencias usadas sin declarar (`jakarta.persistence-api`, `jackson-core`, `jackson-annotations`, `testcontainers`). Se declararon de forma explicita y se retiro `testcontainers-junit-jupiter`, que no se usaba.

Resultado actual: `.\mvnw.cmd -B dependency:analyze` ya no reporta dependencias usadas sin declarar. Mantiene avisos de dependencias declaradas no usadas por analisis estatico: `jaxb-runtime`, `hibernate-hikaricp`, `mariadb-java-client`, `HikariCP` y `junit-jupiter-engine`. Se conservan porque son runtime/proveedor/reflexion o motor de tests.

Riesgo residual: si se quiere endurecer mas, se puede configurar `maven-dependency-plugin` con exclusiones documentadas para que el analisis sea bloqueante sin falsos positivos.

Criterio de cierre: cumplido con `dependency:analyze` en BUILD SUCCESS.
### H6 - JAXB legacy en proyecto Java 21

Severidad: media.
Estado: `[x] Cerrado el 2026-08-12`

Evidencia inicial: el proyecto usaba `javax.xml.bind:jaxb-api`, `com.sun.xml.bind:jaxb-core`, `jaxb-impl` y `javax.activation-api`.

Accion aplicada: se migro el parseo genericode a `jakarta.xml.bind.*` y se sustituyeron dependencias antiguas por `jakarta.xml.bind:jakarta.xml.bind-api:4.0.4` y `org.glassfish.jaxb:jaxb-runtime:4.0.6`.

Verificacion: `.\mvnw.cmd -B clean verify` pasa. `GenericodeCatalogContractTest` parsea y mapea los 102 ficheros `.gc`, cubriendo el riesgo principal de regresion JAXB.

Riesgo residual: `jaxb-runtime` aparece como unused en `dependency:analyze` porque se usa como proveedor runtime de JAXB. Debe conservarse.

Criterio de cierre: JAXB queda actualizado y verificado en Java 21.
### H7 - Operacion destructiva protegida pero todavia gruesa

Severidad: media.
Estado: `[x] Cerrado por decision de negocio el 2026-08-12`

Decision: el modo operativo aceptado es destructivo y no debe conservar historicos. La importacion reemplaza el estado gestionado por la aplicacion.

Estado tecnico: la operacion destructiva sigue protegida por `app.allowDestructiveImport` o `IMPORT_FROM_GC_ALLOW_DESTRUCTIVE_IMPORT`, y Hibernate permanece en `validate`, no en generacion destructiva automatica de esquema.

Riesgo residual: reducido. El modo destructivo sigue siendo una decision operativa aceptada, pero ahora registra un aviso con destino JDBC saneado y usuario efectivo antes de borrar datos; la checklist operativa queda documentada. Deben mantenerse permisos separados por entorno y backups externos.

Criterio de cierre: no se implementan modos `validate|replace|upsert` ni historico porque contradicen la decision de negocio indicada.
### H8 - `ImportFromGc` concentra demasiada responsabilidad

Prioridad: P2
Estado: `[x] Cerrado el 2026-08-12`
Evidencia: `ImportFromGc.main` queda limitado a `System.exit(run(args))`; `run(RuntimeGateway)` devuelve codigo de salida y permite inyectar propiedades, version, servicio, parser/listado de ficheros y email.

Impacto: el flujo principal ya puede probarse sin terminar la JVM. La clase sigue siendo grande, pero el acoplamiento critico a `System.exit` queda aislado.

Recomendacion:

- Mantener `run(RuntimeGateway)` como punto de orquestacion testeable.
- Evitar que nueva logica de negocio se anada directamente a `main`.
- En un hito posterior, extraer email/configuracion a servicios dedicados si crece la complejidad.

Criterio de cierre:

- Cerrado con `ImportFromGcRunnerTest`: flujo feliz devuelve `0`; fallo de persistencia devuelve `1` y envia email de error sin ejecutar `System.exit`.

### H9 - Workflows duplican configuracion Maven privada

Severidad: baja-media.
Estado: `[x] Cerrado el 2026-08-12`

Evidencia inicial: `maven-ci.yml` y `release-package.yml` repetian el bloque de `settings.xml` y `dependency:get` para helpers privados.

Accion aplicada: se creo `.github/actions/setup-maven-private/action.yml`, una accion compuesta local que configura `settings.xml`, resuelve los helpers privados y limpia metadatos Maven locales. CI y release la reutilizan.

Riesgo residual: si cambian artefactos privados o repositorios, solo hay que tocar la accion compartida. Sigue dependiendo de que `PACKAGES_TOKEN` o `GITHUB_TOKEN` tenga permisos correctos.

Criterio de cierre: cumplido; los workflows ya no duplican el bloque Maven privado.
### H10 - Versiones de test muy antiguas

Prioridad: P2
Estado: `[x] Cerrado el 2026-08-12`
Evidencia inicial: `junit.version=5.8.2` y `assertj.version=3.21.0`.
Evidencia actual: JUnit Jupiter 6.1.3 y AssertJ 3.27.7 heredados de `jarios-parent:1.0.4`; no se sube AssertJ 4.0.0-M1 por ser milestone.

Impacto: cerrado para versiones GA actuales. Se mantiene fuera cualquier salto a milestones o betas.

Recomendacion:

- Mantener JUnit 6.1.3 y AssertJ 3.27.7 desde el parent comun.
- Revalorar AssertJ 4 solo cuando exista version GA.

Criterio de cierre:

- Tests pasan con versiones actualizadas.
- No hay warnings relevantes de Surefire/JUnit.
## Actualizaciones de versiones disponibles

`versions-maven-plugin` se ejecuto correctamente el 2026-08-12 tras mover las versiones comunes a `jarios-parent:1.0.4`. No quedan actualizaciones estables pendientes en las dependencias directas del proyecto. Las actualizaciones restantes detectadas son alpha, beta, RC o milestone y se excluyen por decision tecnica.

| Componente | Version actual | Version disponible observada | Accion recomendada |
|---|---:|---:|---|
| Java | 21 | Mantener 21 LTS o planificar 25 LTS | Mantener Java 21 por estabilidad; evaluar Java 25 LTS como hito separado. |
| Maven runtime | Wrapper 3.9.16 | Sin actualizacion GA necesaria | Mantener wrapper en 3.9.16. |
| `ch.qos.logback:logback-classic` | 1.6.2 heredado de `jarios-parent:1.0.4` | Sin actualizacion estable pendiente detectada | Cerrado. |
| Jackson core/databind/jsr310 | 2.22.1 heredado de `jarios-parent:1.0.4` | Sin actualizacion estable pendiente | Cerrado. |
| `com.fasterxml.jackson.core:jackson-annotations` | 2.22 heredado de `jarios-parent:1.0.4` | 3.0-rc5 | No actualizar: RC. Desde Jackson 2.20 annotations no sigue necesariamente el patch del resto de modulos. |
| `jakarta.xml.bind:jakarta.xml.bind-api` | 4.0.4 heredado de `jarios-parent:1.0.4` | 4.1.0-M1 | No actualizar: milestone. |
| `org.glassfish.jaxb:jaxb-runtime` | 4.0.9 heredado de `jarios-parent:1.0.4` | Sin actualizacion estable pendiente | Cerrado. |
| `com.google.code.gson:gson` | 2.14.0 | Sin actualizacion estable pendiente | Cerrado. |
| Hibernate ORM (`hibernate-core`, `hibernate-hikaricp`) | 7.3.0.Final heredado de `jarios-parent:1.0.4` | 8.0.0.Beta1 | No actualizar: beta. |
| `org.mariadb.jdbc:mariadb-java-client` | 3.5.10 heredado de `jarios-parent:1.0.4` | Sin actualizacion estable pendiente | Cerrado. |
| `com.zaxxer:HikariCP` | 7.1.0 heredado de `jarios-parent:1.0.4` | Sin actualizacion estable pendiente | Cerrado. |
| `org.slf4j:slf4j-api` | 2.0.17 heredado de `jarios-parent:1.0.4` | 2.1.0-alpha1 | No actualizar: alpha. |
| `org.projectlombok:lombok` | 1.18.46 heredado de `jarios-parent:1.0.4` | Sin actualizacion estable pendiente | Cerrado. |
| JUnit Jupiter | 6.1.3 heredado de `jarios-parent:1.0.4` | Sin actualizacion estable pendiente | Cerrado. |
| AssertJ Core | 3.27.7 heredado de `jarios-parent:1.0.4` | 4.0.0-M1 | No actualizar: milestone. |
| `maven-compiler-plugin` | 3.15.0 heredado de `jarios-parent:1.0.4` | 4.0.0-beta-* | No actualizar: requiere Maven 4 beta/RC. |
| `maven-surefire-plugin` | 3.5.5 heredado de `jarios-parent:1.0.4` | 3.6.0-M1 | No actualizar: milestone. |
| `dependency-check-maven` | 13.0.0 heredado de `jarios-parent:1.0.4` | Sin actualizacion estable pendiente | Cerrado; mantiene requisito de `NVD_API_KEY`. |
| `spotbugs-maven-plugin` | 4.10.3.0 heredado de `jarios-parent:1.0.4` | Sin actualizacion estable pendiente | Cerrado. |
| `maven-enforcer-plugin` | 3.6.3 heredado de `jarios-parent:1.0.4` | Sin actualizacion estable pendiente | Cerrado. |
| `maven-jar-plugin` | 3.5.1 heredado de `jarios-parent:1.0.4` | 4.0.0-beta-* | No actualizar: requiere Maven 4 beta/RC. |
Fuentes consultadas:

- Maven releases history: https://maven.apache.org/docs/history.html
- Oracle Java SE Support Roadmap: https://www.oracle.com/in/java/technologies/java-se-support-roadmap.html
- Eclipse Temurin 21 releases: https://github.com/adoptium/temurin21-binaries/releases/
- Maven Central / MvnRepository para Logback, Jackson, Gson, Hibernate, MariaDB JDBC, HikariCP, SLF4J, Lombok, JUnit, AssertJ, Maven plugins, SpotBugs y OWASP Dependency Check.

## Cuestiones de negocio y operacion

Estas decisiones deben resolverse antes o durante la ejecucion de algunos hitos, porque condicionan el diseno tecnico:

| Decision | Afecta a | Pregunta a resolver | Recomendacion tecnica por defecto |
|---|---|---|---|
| Politica de importacion | H7 | La importacion debe reemplazar siempre todo, hacer upsert incremental o permitir solo validacion sin escritura? | Decidido: modo destructivo aceptado; no se implementan modos alternativos por ahora. |
| Conservacion historica | H7 | Hay que conservar historico de ficheros GC importados, registros anteriores y estadisticas por ejecucion? | Decidido: no conservar historicos dentro de la aplicacion. |
| Entorno objetivo | H1, H4 | El proyecto se ejecutara solo en CI/servidor controlado o tambien en equipos locales de desarrollo? | Cerrado: Maven Wrapper y JDK 21 documentados. |
| Version Java objetivo | H6, H10 | Se prioriza estabilidad en Java 21 LTS o migracion temprana a Java 25 LTS? | Mantener Java 21 hasta cerrar tests de parseo/persistencia; abrir hito separado para Java 25. |
| Dependencias privadas | H5, H9 | Las librerias `local.jarios:*` evolucionan junto a este proyecto o tienen ciclo de release independiente? | Cerrado: accion compartida local para GitHub Actions. |
| Notificaciones por email | H8 | El email es obligatorio para ejecuciones batch o debe poder desactivarse por entorno? | Anadir `app.mail.enabled=false|true` y no fallar la importacion completa por fallo SMTP si negocio lo permite. |
| Base de datos destino | H2, H7 | Se acepta usar MariaDB real/Testcontainers en tests de integracion? | Usar Testcontainers MariaDB para validar DDL/DML y dejar tests unitarios sin dependencia externa. |
| Riesgo aceptado en calidad | H3 | Checkstyle debe bloquear releases desde ya o se acepta deuda temporal con umbral decreciente? | Cerrado: Checkstyle queda en 0 y el umbral Maven tambien en 0. |

Trabajo que puedo ejecutar sin decision adicional:

- Maven Wrapper incorporado y verificado el 2026-08-12.
- Actualizar README para usar wrapper.
- Crear tests unitarios de parseo/mapeo y ampliar cobertura de utilidades.
- Limpiar propiedades Maven no usadas, si `dependency:analyze` lo confirma.
- Actualizar dependencias de bajo riesgo y plugins Maven menores.
- Reducir Checkstyle de forma incremental hasta umbral 0.
- Extraer duplicacion de workflows.

Trabajo que requiere decision previa:

- Cambiar el modo funcional de importacion (`replace`, `upsert`, `validate`).
- Definir historico y trazabilidad de cargas.
- Cambiar de Java 21 LTS a Java 25 LTS.
- Retirar Gson si todavia se necesita por compatibilidad externa.
- JAXB `javax.*` a `jakarta.*` queda cerrado; mantener runtime JAXB documentado como dependencia de proveedor.
- Decidir si un fallo de email debe hacer fallar toda la ejecucion.
## Roadmap de remediacion

### Fase 0 - Reproducibilidad y verificacion

- [x] H1 Anadir Maven Wrapper. Cerrado el 2026-08-12; verificado con `.\mvnw.cmd -version` y `.\mvnw.cmd -B clean verify`.
- [x] Ejecutar `.\mvnw.cmd -B clean verify`. Cerrado el 2026-08-12.
- [x] Ejecutar `.\mvnw.cmd -B com.github.spotbugs:spotbugs-maven-plugin:4.9.8.2:check`. Cerrado el 2026-08-12; 0 bug instances, 0 errores.
- [x] Ejecutar `.\mvnw.cmd -B org.owasp:dependency-check-maven:check`. Cerrado el 2026-08-12; reporte en `target/dependency-check-report.html`.
- [x] Cerrar OWASP con `NVD_API_KEY` para CI/local recurrente. Cerrado el 2026-08-12; workflows exigen el secret y `scripts/owasp-dependency-check.ps1` valida la variable antes de invocar Maven.
- [x] Guardar resultados iniciales en esta auditoria. Cerrado el 2026-08-12.

### Fase 1 - Red de seguridad

- [x] H2 Tests de parseo de todos los ficheros `.gc`. Cerrado el 2026-08-12 con `GenericodeCatalogContractTest`.
- [x] H2 Tests de mappers. Cerrado el 2026-08-12 con `MapperFicheroGcFromCodeListTest` y `MapperRegistroGcFromCodeListTest`.
- [x] H2 Test de integracion MariaDB/Testcontainers. Cerrado el 2026-08-12 contra MariaDB 11.4.12 con Docker Desktop activo.
- [x] H8 Extraer runner testeable sin `System.exit`. Cerrado el 2026-08-12 con `ImportFromGcRunnerTest`.

### Fase 2 - Dependencias

- [x] H5 Ejecutar `dependency:analyze`. Cerrado el 2026-08-12.
- [x] H5 Retirar/justificar dependencias sin uso. Cerrado el 2026-08-12.
- [x] H10 Actualizar dependencias de test y plugins conservadores. Cerrado el 2026-08-12.
- [x] Actualizar parches de bajo riesgo conservadores. Cerrado el 2026-08-12; SLF4J queda sin subir por alpha.
- [x] Actualizar dependencias estables recomendables en parent comun. Cerrado el 2026-08-12; Jackson 2.22.1 con annotations 2.22, JAXB runtime 4.0.9 y OWASP Dependency Check 13.0.0 en `jarios-parent:1.0.4`. Quedan fuera alpha/beta/RC/milestone.

### Fase 3 - Calidad y operacion

- [x] H3 Reducir Checkstyle por umbrales. Cerrado: 1520 -> 0 y umbral 10000 -> 0.
- [x] H4 Eliminar `-DskipTests` del deploy de release. Cerrado el 2026-08-12.
- [x] H7 Cerrar modos/historico por decision de negocio destructiva. Cerrado el 2026-08-12.
- [x] H7 Guardarrail operativo del modo destructivo. Cerrado el 2026-08-12; log de destino JDBC saneado, usuario efectivo y checklist documentada.
- [x] H9 Extraer accion compartida para Maven privado. Cerrado el 2026-08-12.

## Seguimiento

| Fecha | Hito | Estado | Evidencia | Notas |
|---|---|---|---|---|
| 2026-08-12 | Auditoria inicial viva | Creada | `docs/auditorias/auditoria-viva-2026-08-12.md` | Maven no disponible localmente; se deja como primer hito. |
| 2026-08-12 | H1 Maven Wrapper | Cerrado | `mvnw`, `mvnw.cmd`, `.mvn/wrapper/maven-wrapper.jar`, `.mvn/wrapper/maven-wrapper.properties` | `.\mvnw.cmd -B clean verify` pasa; persisten warnings Checkstyle para H3. |
| 2026-08-12 | SpotBugs | Cerrado | `.\mvnw.cmd -B com.github.spotbugs:spotbugs-maven-plugin:4.9.8.2:check` | 0 bug instances, 0 errores. Avisos de clases auxiliares faltantes sin impacto en resultado. |
| 2026-08-12 | H2 contrato GC | Cerrado | `src/test/java/local/jarios/helpers/GenericodeCatalogContractTest.java` | Los 102 `.gc` parsean y se mapean; mappers e integracion BD cubiertos en hitos posteriores. |
| 2026-08-12 | H2 tests de mappers | Cerrado | `src/test/java/local/jarios/mappers/MapperFicheroGcFromCodeListTest.java`, `src/test/java/local/jarios/mappers/MapperRegistroGcFromCodeListTest.java` | Cubiertos campos de Identification, nulos, filas incompletas, columnas ignoradas y logId. |
| 2026-08-12 | H2 integracion MariaDB | Cerrado | `src/test/java/local/jarios/repositories/RegistroGcDaoMariaDbIntegrationTest.java`, `pom.xml` | Testcontainers 2.0.5 ejecutado contra MariaDB 11.4.12: crea tabla dinamica, inserta 2 registros, cuenta filas y elimina tabla. |
| 2026-08-12 | H8 runner testeable | Cerrado | `src/main/java/local/jarios/ImportFromGc.java`, `src/test/java/local/jarios/ImportFromGcRunnerTest.java` | `main` queda limitado a `System.exit(run(args))`; `run(RuntimeGateway)` permite probar flujo feliz y error sin terminar la JVM. |
| 2026-08-12 | H4 release | Cerrado | `.github/workflows/release-package.yml` | Deploy sin `-DskipTests`; riesgo residual: mayor tiempo de pipeline. |
| 2026-08-12 | H9 Maven privado | Cerrado | `.github/actions/setup-maven-private/action.yml` | Configuracion Maven privada centralizada para CI y release. |
| 2026-08-12 | H5 dependencias | Cerrado | `pom.xml` | Sin dependencias usadas sin declarar; runtime/reflexion justificados. |
| 2026-08-12 | H10 versiones | Cerrado | `pom.xml` | Actualizaciones conservadoras aplicadas; `clean verify` pasa. |
| 2026-08-12 | H6 JAXB Jakarta | Cerrado | `pom.xml`, `src/main/java/local/jarios/genericode`, `src/main/java/local/jarios/helpers/CodeListHelper.java` | Migrado a Jakarta JAXB; 102 ficheros `.gc` parsean. |
| 2026-08-12 | H7 destructivo | Cerrado | Auditoria/README | Decision de negocio: no conservar historicos y aceptar reemplazo destructivo protegido. |
| 2026-08-12 | H7 guardarrail operativo | Cerrado | `src/main/java/local/jarios/repositories/DestructiveImportContext.java`, `src/main/java/local/jarios/repositories/RepositoryImpl.java`, `src/test/java/local/jarios/repositories/DestructiveImportContextTest.java`, `Readme.md` | Antes de borrar, se registra modo destructivo activo con destino JDBC saneado y usuario efectivo, sin password ni parametros. `.\mvnw.cmd -B clean verify`: 20 tests, Checkstyle 0 y Javadoc Maven sin warnings. |
| 2026-08-12 | H3 Checkstyle hito 1200 | Cerrado | `src/main/java/local/jarios/ImportFromGc.java`, `pom.xml` | `ImportFromGc.java` baja de 252 a 36 avisos; total Checkstyle 1159; umbral `checkstyle.max.violations=1200`. |
| 2026-08-12 | H3 Checkstyle hito 900 | Cerrado | `src/main/java/local/jarios/common/util`, `src/main/java/local/jarios/dao/RegistroGcDao.java`, `src/main/java/local/jarios/repositories/RepositoryImpl.java`, `pom.xml` | Total Checkstyle 848; umbral `checkstyle.max.violations=900`; `.\mvnw.cmd -B clean verify` pasa con 17 tests. |
| 2026-08-12 | H3 Checkstyle hito 600 | Cerrado | `src/main/java/local/jarios/database`, `src/main/java/local/jarios/repositories/TransactionManager.java`, `src/main/java/local/jarios/helpers/FileHelper.java`, `src/main/java/local/jarios/helpers/FicheroGcParser.java`, `src/main/java/local/jarios/mappers/MapperRegistroGcFromCodeList.java`, `src/main/java/local/jarios/services/ServiceImpl.java`, `pom.xml` | Total Checkstyle 532; umbral `checkstyle.max.violations=600`; `.\mvnw.cmd -B clean verify` pasa con 17 tests y 21 warnings Javadoc. |
| 2026-08-12 | H3 Checkstyle hito 300 y Javadoc Maven | Cerrado | `pom.xml`, `src/main/java/local/jarios/ImportFromGc.java`, helpers, mappers, repositorios y servicios | Total Checkstyle 266; umbral `checkstyle.max.violations=300`; `.\mvnw.cmd -B clean verify` pasa con 17 tests y generacion Javadoc de Maven sin warnings. |
| 2026-08-12 | H3 Checkstyle hito 0 | Cerrado | `pom.xml`, `src/main/java/local/jarios/entity`, `src/main/java/local/jarios/genericode`, `src/main/java/local/jarios/exceptions`, `src/main/java/local/jarios/enums/PropertyFile.java` | Total Checkstyle 0; umbral `checkstyle.max.violations=0`; `.\mvnw.cmd -B clean verify` pasa con 17 tests y Javadoc sin warnings. |
| 2026-08-12 | OWASP local | Cerrado | `target/dependency-check-report.html` | Reporte generado localmente; para repeticion se exige `NVD_API_KEY`. |
| 2026-08-12 | OWASP con NVD_API_KEY | Cerrado | `.github/workflows/maven-ci.yml`, `.github/workflows/release-package.yml`, `scripts/owasp-dependency-check.ps1`, GitHub secrets | `NVD_API_KEY` existe como secret en `contratacionmalaga/import_from_gc` y `contratacionmalaga/import-from-gc`; CI/release fallan si falta; el script local falla sin variable de entorno. |
| 2026-08-12 | Actualizaciones estables en parent | Cerrado | `C:\java\desarrollo\jarios-parent\pom.xml`, `pom.xml` | Parent subido a `1.0.4`; gestiona Jackson 2.22.1, `jackson-annotations` 2.22, JAXB runtime 4.0.9 y OWASP Dependency Check 13.0.0. `jarios-parent:1.0.4` publicado en GitHub Packages; `import-from-gc` hereda esas versiones y `clean verify` pasa con 20 tests, Checkstyle 0 y Javadoc sin warnings. |

## Comandos de verificacion recomendados

Con Maven Wrapper ya incorporado:

```powershell
.\mvnw.cmd -version
.\mvnw.cmd -B clean verify
.\mvnw.cmd -B checkstyle:check
.\mvnw.cmd -B com.github.spotbugs:spotbugs-maven-plugin:4.9.8.2:check
.\mvnw.cmd -B org.owasp:dependency-check-maven:check
.\mvnw.cmd -B dependency:analyze
.\mvnw.cmd -B versions:display-property-updates
.\mvnw.cmd -B versions:display-dependency-updates
.\mvnw.cmd -B versions:display-plugin-updates
```
## Riesgo residual

El proyecto queda en riesgo bajo-medio operativo: H1, H2, H3, H4, H5, H6, H7, H8, H9, H10, Javadoc Maven y OWASP con `NVD_API_KEY` estan cerrados o aceptados. Ya no queda deuda Checkstyle ni riesgo de ejecuciones OWASP recurrentes sin clave. Los riesgos residuales son de evolucion: mantener permisos/backups externos como control operativo permanente, tratar actualizaciones mayores o no GA de Hibernate/SLF4J/JUnit/Jakarta/Maven como hitos separados.
