# Auditoria tecnica del proyecto import-from-gc

Fecha: 2026-05-09
Ruta auditada: `C:\java\desarrollo\import-from-gc`
Version declarada: `5.2.0`
Stack principal: Java 21, Maven, Hibernate ORM 7, MariaDB, JAXB, SLF4J/Logback, Jackson/Gson.

## Resumen ejecutivo

El proyecto es una aplicacion Java de consola que parsea ficheros GC/genericode desde `data/gc`, crea entidades de trazabilidad, genera tablas dinamicas por fichero y persiste el resultado en MariaDB. La base funcional esta acotada y el codigo se organiza por capas simples (`helpers`, `mappers`, `repositories`, `services`, `entity`), pero el estado actual no es apto para tratarse como release sin correcciones previas.

Riesgo global estimado: **alto**, principalmente por configuracion destructiva de base de datos, credenciales en texto/plano o reversible dentro del repositorio, SQL nativo construido por concatenacion, pipeline CI incompleto o corrupto y ausencia de tests automatizados.

## Alcance y limitaciones

Se reviso el arbol de trabajo actual, incluyendo cambios staged y unstaged. No se revirtieron cambios locales.

Comprobaciones realizadas:

- Inventario de ficheros con `rg --files`.
- Estado Git con `git status --short`, `git diff --stat` y `git diff --cached --stat`.
- Revision de `pom.xml`, `Readme.md`, `.github`, `properties`, `src/main/java`, `src/main/resources` y `target/checkstyle-result.xml`.
- Conteo de artefactos: 44 ficheros Java en `src/main/java`, 102 ficheros `.gc` en `data/gc`, 0 tests Java bajo `src/test/java`.
- Revision externa puntual de estado de dependencias en Snyk/MvnRepository para los componentes principales.

Limitaciones:

- No se pudo ejecutar `mvn clean verify`, `mvn test`, `mvn spotbugs:check` ni `dependency-check:check` porque `mvn` no esta disponible en el `PATH` y no existe Maven Wrapper (`mvnw`) en el repositorio.
- El informe de Checkstyle procede de `target/checkstyle-result.xml` ya existente, no de una ejecucion regenerada durante esta auditoria.
- La auditoria de CVE no sustituye una ejecucion formal de OWASP Dependency-Check, Snyk CLI, GitHub Dependabot o similar sobre el arbol completo.

## Estado del repositorio

El arbol de trabajo no esta limpio.

Cambios unstaged observados:

- `.idea/compiler.xml`
- `pom.xml`
- `src/main/java/local/jarios/ImportFromGc.java`
- `src/main/java/local/jarios/common/util/Constantes.java`
- `src/main/java/local/jarios/helpers/ComunHelper.java`

Cambios staged observados:

- `config/hibernate.properties` movido a `properties/hibernate.properties`.
- Eliminado `qodana.yaml`.
- Eliminados adapters Gson: `EstadisticaEntityAdapter`, `FicheroGcEntityAdapter`, `LogEntityAdapter`.
- Eliminado `ManagerGsons`.
- Añadidos `JsonHelper` y `ManagerJackson`.
- Eliminado `src/main/resources/log4j2.xml`.
- Añadido `src/main/resources/logback.xml`.

Ficheros no versionados observados:

- `maven-import.ps1`
- `properties/app.properties`
- `properties/jakarta_principal.properties`
- `properties/mail.properties`
- `src/main/java/local/jarios/interfaces/`

Impacto: la auditoria refleja el estado local, no necesariamente el ultimo commit ni una version publicada.

## Hallazgos criticos

### 1. Configuracion de Hibernate destruye esquema al arrancar

Evidencia: `properties/hibernate.properties:63`

```properties
hibernate.hbm2ddl.auto=create
```

Impacto: `create` recrea el esquema y puede destruir datos previos. Para una herramienta de importacion conectada a MariaDB, esto es un riesgo critico si el fichero properties se usa fuera de un entorno local controlado.

Recomendacion:

- Cambiar a `validate` o `none` en entornos no locales.
- Separar perfiles `dev`, `test`, `prod`.
- Usar migraciones versionadas con Flyway o Liquibase si el esquema debe evolucionar.

### 2. Credenciales y datos sensibles versionables en `properties`

Evidencia:

- `properties/jakarta_principal.properties:12-13` contiene usuario de BD y password.
- `properties/mail.properties:16-18` contiene cuenta SMTP y password cifrada/reversible.
- `pom.xml:280` contiene una API key de NVD.

Impacto: exposicion de secretos, reutilizacion accidental en entornos compartidos y dificultad para rotar credenciales. Aunque una clave este cifrada, si el mecanismo y la clave comun viven en el ecosistema del proyecto, debe tratarse como secreto.

Recomendacion:

- Mover secretos a variables de entorno, vault o secretos del CI.
- Versionar solo plantillas, por ejemplo `*.properties.example`.
- Rotar las credenciales expuestas.
- Eliminar el API key de NVD del `pom.xml` y leerlo desde variable de entorno.

### 3. SQL nativo construido por concatenacion

Evidencia:

- `src/main/java/local/jarios/dao/RegistroGcDao.java:67-74` crea tablas concatenando nombre, charset y collation.
- `src/main/java/local/jarios/dao/RegistroGcDao.java:98-115` inserta registros construyendo un `INSERT` masivo manual.
- `src/main/java/local/jarios/dao/RegistroGcDao.java:135-138` ejecuta `DROP TABLE` concatenando nombre de tabla.

Impacto: riesgo de SQL injection si algun identificador deriva de contenido de fichero GC o properties no validado. El escape de comillas en `sanitizar` reduce errores en valores, pero no valida identificadores SQL ni sustituye parametros preparados.

Recomendacion:

- Validar nombres de tabla con allowlist estricta: `^[a-zA-Z0-9_]+$`.
- Derivar nombres desde un mapeo interno controlado, no directamente desde contenido parseado.
- Usar parametros para valores (`code`, `nombre`) o batch JDBC/Hibernate.
- Evitar loguear SQL completo con datos.

### 4. Pipeline de GitHub Actions esta incompleto/corrupto

Evidencia: `.github/workflow/maven-ci.yml:61-65` termina en:

```yaml
GITHUB_ACTOR: ${{ git_
```

Impacto: el workflow no es YAML valido o queda truncado, por lo que CI/CD puede no arrancar. Ademas, el directorio esta como `.github/workflow`, pero GitHub Actions espera `.github/workflows`.

Recomendacion:

- Renombrar a `.github/workflows/maven-ci.yml`.
- Completar o eliminar el bloque de deploy.
- Actualizar actions a versiones vigentes.
- Hacer que el pipeline ejecute `mvn clean verify`, SpotBugs/FindSecBugs y Dependency-Check sin `skip`.

### 5. Sin tests automatizados

Evidencia: `src/test/java` existe pero no contiene tests.

Impacto: los cambios en parseo, mapeo, generacion SQL, persistencia y migracion Gson/Jackson no tienen red de seguridad automatizada.

Recomendacion inicial:

- Tests unitarios de `MapperFicheroGcFromCodeList`, `MapperRegistroGcFromCodeList`, `CodeListHelper` y `FileHelper`.
- Tests de integracion con Testcontainers MariaDB o H2 solo si el SQL usado es compatible.
- Test de contrato para que cada fichero `.gc` de `data/gc` parsea correctamente.

## Hallazgos altos

### 6. Checkstyle no protege la calidad del build

Evidencia:

- `pom.xml:250` tiene `failsOnError=false`.
- `pom.xml:73` permite `checkstyle.max.violations=10000`.
- `target/checkstyle-result.xml` contiene 1.420 avisos.

Distribucion principal del informe existente:

- 867 avisos de indentacion.
- 123 avisos de Javadoc paragraph.
- 110 tabuladores en ficheros.
- 102 lineas largas.
- 91 problemas de orden de imports.

Ficheros con mas avisos:

- `ImportFromGc.java`: 209.
- `RepositoryImpl.java`: 106.
- `RegistroGcDao.java`: 69.
- `SessionFactoryProvider.java`: 56.
- `Identification.java`: 55.

Impacto: la regla existe, pero esta configurada para no bloquear casi nada.

Recomendacion:

- Bajar progresivamente `checkstyle.max.violations`.
- Activar `failsOnError=true` cuando la deuda este por debajo de un umbral asumible.
- Separar reglas de formato de reglas de diseño para facilitar adopcion.

### 7. OWASP Dependency-Check esta configurado pero desactivado

Evidencia: `pom.xml:275-282`

```xml
<artifactId>dependency-check-maven</artifactId>
...
<skip>true</skip>
```

Impacto: la presencia del plugin puede dar falsa sensacion de cobertura de seguridad, pero no se ejecuta.

Recomendacion:

- Leer el NVD API key desde secreto externo.
- Ejecutar en CI al menos semanalmente y en pull requests relevantes.
- Fallar por severidad alta/critica una vez estabilizado.

### 8. Log de propiedades puede exponer secretos en DEBUG

Evidencia:

- `src/main/java/local/jarios/database/SessionFactoryProvider.java:58-59` loguea `hibernateProperties`.
- `src/main/java/local/jarios/database/SessionFactoryProvider.java:112` loguea `props` tras inyectar URL, user y password.

Impacto: si se activa DEBUG, el password JDBC puede acabar en logs locales o artefactos de CI.

Recomendacion:

- Enmascarar claves sensibles antes de loguear.
- No loguear objetos `Properties` completos.
- Centralizar sanitizacion de logs de configuracion.

### 9. Operacion destructiva de datos en cada persistencia

Evidencia:

- `RepositoryImpl.java:83` borra todos los `FicheroGc`.
- `RepositoryImpl.java:93` elimina tablas existentes.
- `RegistroGcDao.java:136-138` hace `DROP TABLE IF EXISTS`.

Impacto: cada ejecucion reemplaza estructuras y datos. Puede ser correcto para una carga completa, pero debe estar documentado y protegido para evitar ejecuciones accidentales en entornos compartidos.

Recomendacion:

- Añadir modo explicito `--replace` o propiedad `app.import.mode=replace|upsert|validate`.
- Confirmar entorno antes de acciones destructivas.
- Guardar historico o versionar importaciones si se necesita trazabilidad.

## Hallazgos medios

### 10. Nombres de tabla derivados de `shortName`

Evidencia: `FicheroGcParser.java:69` usa `ficheroGc.getShortName()` como clave del mapa y `RepositoryImpl.java:90` lo transforma en nombre de tabla con prefijo.

Impacto: acopla estructura fisica de BD al contenido de ficheros de entrada. Cualquier cambio de nomenclatura en los GC puede cambiar el esquema.

Recomendacion: introducir normalizador y catalogo de nombres de tabla con validacion estricta.

### 11. `System.exit` dificulta pruebas y reutilizacion

Evidencia: `ImportFromGc.java:326-336`.

Impacto: complica tests unitarios del flujo principal y evita reutilizar la aplicacion como libreria o job embebido.

Recomendacion: extraer `run()` que devuelva codigo de salida y dejar `System.exit` solo en `main`.

### 12. Uso de estado global mutable

Evidencia:

- `ImportFromGc.java:61` expone `propertiesManager` como `public static final`.
- `ImportFromGc.java:70` y `ImportFromGc.java:79` exponen `appName` y `appVersion` como `public static`.

Impacto: acoplamiento global, dificultad de test y riesgo de contaminacion entre ejecuciones en el mismo proceso.

Recomendacion: encapsular configuracion en un objeto de contexto inyectado.

### 13. README desactualizado respecto al stack real

Evidencia:

- `Readme.md` menciona Log4j como gestor de logs.
- El proyecto actual tiene `src/main/resources/logback.xml` y se elimino `log4j2.xml`.
- Versiones del README no coinciden con `pom.xml` para Hibernate, HikariCP, Lombok, SLF4J, etc.

Impacto: onboarding y operacion propensos a error.

Recomendacion: actualizar README con ejecucion real, configuracion, perfiles, variables de entorno y modo de despliegue.

### 14. Codificacion de properties con caracteres corruptos

Evidencia: comentarios de `properties/*.properties` muestran caracteres como `CONFIGURACI�N`.

Impacto: indica mezcla de codificaciones o lectura incorrecta. Aunque sean comentarios, puede acabar afectando valores reales si se introducen acentos.

Recomendacion:

- Normalizar ficheros a UTF-8.
- Asegurar `project.build.sourceEncoding=UTF-8` y configurar editor/IDE.

### 15. Maven Wrapper ausente

Evidencia: no existen `mvnw` ni `.mvn/wrapper`.

Impacto: no hay forma reproducible de ejecutar build local en maquinas sin Maven instalado.

Recomendacion: añadir Maven Wrapper y documentar `.\mvnw.cmd clean verify`.

## Dependencias y seguridad

Observaciones locales:

- `pom.xml` mezcla JAXB `javax.*` 2.x con Hibernate/Jakarta moderno. Puede funcionar, pero mantiene una dependencia legacy para JAXB.
- `gson` sigue declarado aunque se ha introducido Jackson. Si ya no se usa, conviene eliminarlo para reducir superficie.
- `jsoup.version`, `poi.version` y `uuid.version` estan declaradas pero no parecen usadas como dependencias directas.
- Dependencias internas `local.jarios:*` dependen de repositorios/paquetes no verificables desde este entorno.

Consulta externa puntual:

- Snyk indica que `jackson-databind 2.20.1` no tiene vulnerabilidades directas conocidas, pero existe version mas reciente `2.21.2`.
- Snyk indica que `gson 2.13.2` no tiene vulnerabilidades directas conocidas, pero existe `2.14.0`.
- Snyk indica que `logback-classic 1.5.24` no tiene vulnerabilidades directas conocidas, pero `logback-core 1.5.24` aparece afectado por una vulnerabilidad baja corregida en `1.5.25`.
- Snyk indica que `hibernate-core` en grupo `org.hibernate.orm` no tiene vulnerabilidades directas conocidas en la familia revisada, pero hay versiones mas recientes.
- MvnRepository indica que `mariadb-java-client 3.5.7` no muestra vulnerabilidades en esa pagina y existe `3.5.8`.
- Snyk/MvnRepository no sustituyen el analisis transitivo completo de OWASP Dependency-Check.

Fuentes consultadas:

- https://security.snyk.io/package/maven/com.fasterxml.jackson.core%3Ajackson-databind/2.20.1
- https://security.snyk.io/package/maven/com.google.code.gson%3Agson/2.13.2
- https://security.snyk.io/package/maven/ch.qos.logback%3Alogback-classic/1.5.24
- https://security.snyk.io/package/maven/ch.qos.logback%3Alogback-core/1.5.24
- https://security.snyk.io/package/maven/org.hibernate.orm%3Ahibernate-core
- https://mvnrepository.com/artifact/org.mariadb.jdbc/mariadb-java-client/3.5.7

## Arquitectura y mantenibilidad

Fortalezas:

- Separacion basica de responsabilidades: entrada (`ImportFromGc`), parseo (`helpers`), mapeo (`mappers`), persistencia (`repositories`) y dominio (`entity`).
- Uso de excepciones especificas de dominio.
- Uso de SLF4J.
- Centralizacion de versiones en `pom.xml`.
- Entidades con auditoria de creacion.

Debilidades:

- `ImportFromGc` concentra orquestacion, email, formateo de estadisticas, manejo de errores y terminacion de proceso.
- Persistencia mezcla Hibernate ORM con DDL/DML nativo manual.
- El repositorio conoce detalles de tablas dinamicas y de borrado masivo.
- La configuracion esta acoplada a singleton global.
- No hay contratos automatizados para datos GC, esquema o carga.

## CI/CD

Estado actual:

- Dependabot existe para Maven con frecuencia semanal.
- Workflow de CI esta ubicado en `.github/workflow`, no `.github/workflows`.
- Workflow truncado en linea 65.
- Usa `actions/checkout@v3`, `actions/setup-java@v3` y `actions/cache@v3`; conviene actualizar a versiones actuales.
- Ejecuta OWASP en GitHub Action externa, pero el `pom.xml` tambien tiene Dependency-Check desactivado.

Recomendacion de pipeline minimo:

1. Checkout.
2. Setup JDK 21.
3. Cache Maven.
4. `./mvnw -B clean verify`.
5. `./mvnw -B spotbugs:check`.
6. `./mvnw -B org.owasp:dependency-check-maven:check`.
7. Upload de reports.
8. Deploy solo en tags/releases o ramas protegidas, con secretos configurados.

## Plan de remediacion recomendado

Prioridad 0 - antes de cualquier release:

- Cambiar `hibernate.hbm2ddl.auto=create` fuera de local.
- Sacar secretos del repositorio y rotarlos.
- Corregir `.github/workflows/maven-ci.yml`.
- Añadir Maven Wrapper.
- Ejecutar `clean verify` en una maquina con Maven/JDK 21.

Prioridad 1 - seguridad y datos:

- Validar identificadores SQL y parametrizar inserts.
- Introducir modo explicito para operaciones destructivas.
- Enmascarar propiedades sensibles en logs.
- Activar Dependency-Check/SpotBugs en CI.

Prioridad 2 - calidad:

- Crear tests unitarios de parseo y mapeo.
- Crear test de integracion de persistencia.
- Reducir deuda Checkstyle por paquetes.
- Eliminar dependencias y propiedades no usadas.

Prioridad 3 - mantenibilidad:

- Extraer orquestador testeable desde `ImportFromGc`.
- Encapsular configuracion en un contexto inyectable.
- Documentar configuracion por entorno.
- Normalizar encoding de properties y documentacion.

## Conclusiones

El proyecto tiene una base funcional clara, pero en el estado auditado depende demasiado de configuracion local y de operaciones destructivas implicitas. La prioridad debe ser asegurar datos y secretos, reparar CI y crear una verificacion reproducible. Sin esos pasos, cualquier cambio sobre parseo, persistencia o dependencias tiene riesgo alto de regresion no detectada.
