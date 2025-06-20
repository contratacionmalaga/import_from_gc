package local.jarios;

import local.jarios.common.util.Constantes;
import local.jarios.common.util.FinalDelPrograma;
import local.jarios.common.util.Mensajes;
import local.jarios.email.api.EmailService;
import local.jarios.email.api.EmailServiceImpl;
import local.jarios.email.exception.EmailServiceException;
import local.jarios.encryptor.exception.EncryptorException;
import local.jarios.entity.Estadistica;
import local.jarios.entity.FicheroGc;
import local.jarios.entity.Log;
import local.jarios.enums.TipoFinalEjecucion;
import local.jarios.exceptions.MiParseException;
import local.jarios.exceptions.MiServiceException;
import local.jarios.exceptions.MiUnknownHostException;
import local.jarios.genericode.CodeList;
import local.jarios.helpers.CodeListHelper;
import local.jarios.helpers.ComunHelper;
import local.jarios.helpers.FileHelper;
import local.jarios.helpers.MiMailHelper;
import local.jarios.mappers.MapperRegistroGcFromCodeList;
import local.jarios.models.ParseoFicherosGc;
import local.jarios.properties.PropertyConstantes;
import local.jarios.properties.api.PropertiesManagerService;
import local.jarios.properties.api.PropertiesManagerServiceImpl;
import local.jarios.properties.exception.PropertiesManagerException;
import local.jarios.services.Service;
import local.jarios.services.ServiceImpl;
import local.jarios.version.api.Version;
import local.jarios.version.api.VersionImpl;
import local.jarios.version.exception.VersionException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Clase principal para la importación de información desde ficheros Excel al sistema.
 * <p>
 * Se encarga de leer ficheros GC, procesarlos, almacenar estadísticas y persistir los datos en base de datos.
 * Controla la gestión de excepciones y trazas para facilitar el diagnóstico.
 * </p>
 *
 * @author Juan Antonio
 * @since 04/06/2024
 */
@Slf4j
public class ImportFromGc {

    /**
     * Constructor sin argumentos.
     */
    public ImportFromGc() {
        // Constructor vacío
    }

    /**
     * Método principal que ejecuta el proceso completo de importación.
     *
     * @param args argumentos de línea de comandos (no usados)
     */
    public static void main(String[] args) {

        // Inicio del log
        log.info(Mensajes.INICIO);

        try {

            // Obtener la instancia singleton
            Version versionService = new VersionImpl();
            log.info("El servicio de consulta de la versión del JAR se ha creado correctamente.");

            PropertiesManagerService propertiesManager = PropertiesManagerServiceImpl.getInstance();
            log.info("El servicio de consulta de los ficheros properties se ha creado correctamente.");

            propertiesManager.setConfigDir(Constantes.CONFIG_DIR);
            log.info("Directorio configurado: {}", Constantes.CONFIG_DIR);

            // === Configuración inicial ===
            Set<String> clavesSensibles = Set.of("password");
            propertiesManager.setSensitiveKeys(clavesSensibles);  // Ahora se aplica sobre la instancia
            log.info("Establezco el conjunto de claves Sensibles: {}", clavesSensibles);

            // Configurar clave secreta
            propertiesManager.setSecretKey(Constantes.ENCRYPT_PASSWORD);
            log.info("Clave secreta configurada: {}", Constantes.ENCRYPT_PASSWORD);

            // Cargar todas las propiedades desde el directorio de configuración
            propertiesManager.loadAllProperties();
            log.info("Ficheros .properties cargados desde /{} correctamente", Constantes.CONFIG_DIR);

            String appName = propertiesManager.getProperty(Constantes.APP_PROPERTIES, Constantes.KEY_APP_NAME);
            log.info("AppName: {}", appName);

            String appDescripcion = propertiesManager.getProperty(Constantes.APP_PROPERTIES, Constantes.KEY_APP_DESCRIPTION);
            log.info("AppDescription: {}", appDescripcion);

            String appVersion = versionService.getVersion(VersionDemo.class);
            log.info("AppVersion: {}", appVersion);

            // Creo el objeto Log para esta ejecución
            Log miLog = new Log();
            log.info(Mensajes.LOG_CREACION);

            // Creo Estadistica ligada al Log creado
            Estadistica estadistica = new Estadistica(miLog);
            log.info(Mensajes.ESTADISTICA_CREACION);

            // Creo el servicio para interacción con la base de datos
            Service service = new ServiceImpl();
            log.info(Mensajes.SERVICE_CREACION_CREADO);

            // Inicio del parseo de ficheros GC
            Timestamp timestampInicioParseo = Timestamp.valueOf(LocalDateTime.now());
            estadistica.setFechaHoraInicialParseo(timestampInicioParseo);
            log.info(
                    Mensajes.ASIGN_FECHA_HORA_INICIAL_PARSEO_TO_ESTADISTICA,
                    ComunHelper.getFechaHoraFormateada(timestampInicioParseo));

            // Obtengo la ruta de los ficheros a parsear desde el directorio definido en el fichero properties
            var path = propertiesManager.getProperty(Constantes.APP_PROPERTIES, PropertyConstantes.APP_PATH);
            log.info(Mensajes.RUTA_FICHEROS, path);

            // Obtengo el listado de ficheros en la ruta
            File[] arrayFicherosDirecotorio = FileHelper.getListaFicherosFromPath(path);
            estadistica.setNTotalFicherosLeidos(arrayFicherosDirecotorio.length);
            log.info(Mensajes.N_FICHEROS_RUTA, arrayFicherosDirecotorio.length);

            // Obtengo el listado de los ficheros que se encuentran persistidos en la base de datos
            List<FicheroGc> listFicherosGcPersistidos = service.getListFicherosGc();
            log.info(Mensajes.N_FICHEROS_PERSISTIDOS, listFicherosGcPersistidos.size());

            // Convertir la lista persistida a Map por nombre
            Map<String, FicheroGc> mapaPersistidos = listFicherosGcPersistidos.stream()
                    .collect(Collectors.toMap(FicheroGc::getShortName, Function.identity()));
            log.info(Mensajes.CONVERTIR_LISTA_PERSISTIDOS_EN_MAP);

            // Obtengo el objeto encargado del procesamiento de los ficheros
            ParseoFicherosGc parseoFicherosGc = getParseoFicherosGc(miLog, arrayFicherosDirecotorio, mapaPersistidos);

            // Asigno la lista unificada al Log
            miLog.setFicherosGc(parseoFicherosGc.getListFicherosGc());
            log.info(Mensajes.AGIGN_LISTA_FICHEROS_LEIDOS_TO_LOG);

            // Registro fecha final del parseo
            Timestamp timestampFinParseo = Timestamp.valueOf(LocalDateTime.now());
            estadistica.setFechaHoraFinalParseo(timestampFinParseo);
            log.info(
                    Mensajes.ASIGN_FECHA_HORA_FINAL_PARSEO_TO_ESTADISTICA,
                    ComunHelper.getFechaHoraFormateada(timestampFinParseo));

            // Calculo duración del parseo
            String duracionParseo = ComunHelper.calcularTiempoEjecucion(
                    estadistica.getFechaHoraInicialParseo(),
                    estadistica.getFechaHoraFinalParseo());
            estadistica.setDuracionParseo(duracionParseo);

            // Inicio persistencia en base de datos
            Timestamp timestampInicioBD = Timestamp.valueOf(LocalDateTime.now());
            estadistica.setFechaHoraInicialBaseDatos(timestampInicioBD);
            log.info(
                    Mensajes.ASIGN_FECHA_HORA_INICIAL_BASE_DATOS_TO_ESTADISTICA,
                    ComunHelper.getFechaHoraFormateada(timestampInicioBD));

            // Persisto los objetos
            service.persistirLog(miLog);
            log.info(Mensajes.PERSISTIDO_LOG);

            // Persisto los objetos
            service.persistirListaFicherosGc(parseoFicherosGc.getListFicherosGc());
            log.info(Mensajes.PERSISTIDO_LISTA_FICHEROS_GC);

            String prefijo = propertiesManager.getProperty(Constantes.APP_PROPERTIES, Constantes.KEY_APP_PREFIX);
            log.info("Prefijo de las tablas: {}", prefijo);

            service.persistirObjetoParseoFicherosGc(parseoFicherosGc, prefijo);
            log.info(Mensajes.PERSISTIDO_PARSEO_FICHEROS_GC);

            // Final persistencia
            Timestamp timestampFinBD = Timestamp.valueOf(LocalDateTime.now());
            estadistica.setFechaHoraFinalBaseDatos(timestampFinBD);
            log.info(
                    Mensajes.ASIGN_FECHA_HORA_FINAL_BASE_DATOS_TO_ESTADISTICA,
                    ComunHelper.getFechaHoraFormateada(timestampFinBD));

            // Calculo duración persistencia
            String duracionBaseDatos = ComunHelper.calcularTiempoEjecucion(
                    estadistica.getFechaHoraInicialBaseDatos(),
                    estadistica.getFechaHoraFinalBaseDatos());
            estadistica.setDuracionBaseDatos(duracionBaseDatos);
            log.info(Mensajes.ASIGN_DURACION_BASE_DATOS, duracionBaseDatos);

            // Persisto estadísticas finales
            service.persistirEstadistica(estadistica);
            log.info(Mensajes.PERSISTIDO_ESTADISTICA);

            enviarEmail(MiMailHelper.getAsunto(), MiMailHelper.getCuerpoMensaje(estadistica));
            log.info("Enviado email correctamente.");

            // Finalizo el programa correctamente
            FinalDelPrograma.finalizar(TipoFinalEjecucion.CORRECTO);

        } catch (MiServiceException ex) {
            manejarExcepcion(ex, "[MiServiceException] - ");
        } catch (MiParseException ex) {
            manejarExcepcion(ex, "[MiParseException] - ");
        } catch (MiUnknownHostException ex) {
            manejarExcepcion(ex, "[MiUnknownHostException] - ");
        } catch (EncryptorException ex) {
            manejarExcepcion(ex, "[EncryptorException] - ");
        } catch (EmailServiceException ex) {
            manejarExcepcion(ex, "[EmailServiceException] - ");
        } catch (PropertiesManagerException ex) {
            manejarExcepcion(ex, "[PropertiesManaerException] - ");
        } catch (VersionException ex) {
            manejarExcepcion(ex, "[VersionException] - ");
        } catch (Exception ex) {
            manejarExcepcion(ex, "[Exception] - ");
        }
    }

    /**
     * Maneja de forma centralizada las excepciones que ocurren durante la ejecución del programa.
     * <p>
     * Este método registra el mensaje de error proporcionado, imprime el stack trace del error
     * con sangría personalizada, intenta enviar un email con el detalle del error,
     * y finaliza el programa indicando un error en la ejecución.
     * </p>
     *
     * @param ex           La excepción que fue lanzada.
     * @param mensajeError El mensaje personalizado que describe el contexto del error.
     */
    private static void manejarExcepcion(Exception ex, String mensajeError) {

        log.info("[manejarExcepcion] -");
        log.error(mensajeError, ex.getMessage());

        for (StackTraceElement ste : ex.getStackTrace()) {
            log.error("{}", ste);
        }

        try {

            // Construcción de asunto y cuerpo HTML para el email de error
            String asunto = "ERROR - Fallo en ejecución: " + ex.getClass().getSimpleName();
            String cuerpoHtml = MiMailHelper.buildHtmlExceptionBody(ex);

            enviarEmail(asunto, cuerpoHtml);
            log.info("Correo de error enviado correctamente.");

        } catch (EmailServiceException e) {
            log.error("Error inesperado al intentar enviar email de fallo: {}", e.getMessage());
        }

        FinalDelPrograma.finalizar(TipoFinalEjecucion.ERROR);
    }

    /**
     * Procesa un fichero que no existe aún en el sistema persistido.
     * Agrega el nuevo fichero a la lista de ficheros a persistir y registra los datos asociados.
     *
     * @param ficheroGc         Objeto {@link FicheroGc} creado a partir del fichero nuevo.
     * @param codeList          Lista de {@link CodeList} extraída del fichero.
     * @param parseoFicherosGc  Estructura que contiene los ficheros y registros procesados.
     */
    private static void procesarFicheroNuevo(
            FicheroGc ficheroGc,
            CodeList codeList,
            ParseoFicherosGc parseoFicherosGc
    ) {
        parseoFicherosGc.getListFicherosGc().add(ficheroGc);
        parseoFicherosGc.getMapRegistrosGcByFicheroGc()
                .put(ficheroGc.getShortName(), MapperRegistroGcFromCodeList.getListRegistroGcFromCodeList(codeList));
        log.info("  El fichero es nuevo. Se añade para realizar un persist.");
    }

    /**
     * Procesa un fichero que ya existe en el sistema persistido.
     * Compara el fichero nuevo con el existente para detectar si ha sido modificado.
     * Si hay diferencias, se actualiza el fichero existente.
     *
     * @param existente  Fichero persistido previamente en el sistema.
     * @param nuevo      Nuevo objeto {@link FicheroGc} generado a partir del fichero actual.
     * @param parseoFicherosGc Objeto que aglutina la importación
     */
    private static void procesarFicheroExistente(
            FicheroGc existente,
            FicheroGc nuevo,
            ParseoFicherosGc parseoFicherosGc
    ) {
        if (!existente.equals(nuevo)) {
            log.info("  El fichero se encuentra modificado.");
            existente.actualizarCon(nuevo);
            parseoFicherosGc.getListFicherosGc().remove(existente);
            log.info("  Elimino de la lista el antiguo.");
            parseoFicherosGc.getListFicherosGc().add(nuevo);
            log.info("  Elimino de la lista el nuevo.");
        } else {
            log.info("  El fichero no presenta cambios.");
        }
    }

    /**
     * Procesa los ficheros del directorio, comparando con los ya persistidos, y construye
     * una estructura para su posterior almacenamiento en base de datos.
     *
     * @param miLog                     Log asociado a la ejecución actual.
     * @param arrayFicherosDirecotorio  Array con los ficheros en el directorio para su procesamiento.
     * @param mapaPersistidos           Mapa con los ficheros actualmente persistidos en la base de datos.
     * @return {@link ParseoFicherosGc} con los datos procesados listos para persistencia.
     */
    private static ParseoFicherosGc getParseoFicherosGc(
            Log miLog,
            File[] arrayFicherosDirecotorio,
            Map<String, FicheroGc> mapaPersistidos
    ) {

        log.info(">>>> Inicio del parse de FicherosGc. Parseando: {}", arrayFicherosDirecotorio.length);

        // Creo el objeto encargado de almacenar la información del Parseo para despues persistirla
        ParseoFicherosGc parseoFicherosGc = new ParseoFicherosGc();

        // Procesa todos los ficheros del directorio
        for (File fichero : arrayFicherosDirecotorio) {
            String nombreFichero = fichero.getName();
            log.info("Procesando fichero '{}'.", nombreFichero);

            boolean ficheroValido = !FileHelper.esIncorrectoFichero(fichero);
            log.info("  Fichero válido: {}", ficheroValido);

            if (ficheroValido) {
                var codeList = CodeListHelper.getCodeListFromFile(fichero);
                log.info("  Obtención correcta de CodeList a partir del fichero.");
                var ficheroGc = CodeListHelper.getFicheroGc(miLog, codeList);
                log.info("  Obtención correcta de FicheroGc a partir del CodeList.");

                if (ficheroGc != null) {
                    log.info("  FicheroGc no Nulo.");
                    FicheroGc existente = mapaPersistidos.get(nombreFichero);
                    if (existente == null) {
                        log.info("  No existe FicheroGc en el MAP.");
                        procesarFicheroNuevo(ficheroGc, codeList, parseoFicherosGc);
                        log.info("  FicheroGc procesado como nuevo correctamente.");
                    } else {
                        log.info("  Existe FicheroGc en el MAP.");
                        procesarFicheroExistente(existente, ficheroGc, parseoFicherosGc);
                        log.info(  "FicheroGc procesado como existente correctamente.");
                    }
                } else {
                    log.debug("  FicheroGc es null y será ignorado.");
                }
            } else {
                log.debug("  No es un fichero válido y será ignorado.");
            }
        }

        return parseoFicherosGc;
    }

    /**
     * Envía un email con el asunto y cuerpo indicados usando la configuración de propiedades.
     * <p>
     * Método común para centralizar el envío de emails evitando duplicidad.
     * Controla excepciones relacionadas con el envío y las registra.
     * </p>
     *
     * @param asunto            Asunto del email a enviar.
     * @param cuerpoHtml        Cuerpo del email en formato HTML.
     */
    private static void enviarEmail(String asunto, String cuerpoHtml) {
        try {

            //
            PropertiesManagerService propertiesManager = PropertiesManagerServiceImpl.getInstance();
            log.info("El servicio de consulta de los ficheros properties se ha creado correctamente.");

            //
            Properties emailProperties = propertiesManager.getProperties(Constantes.EMAIL_PROPERTIES);
            log.info("Leídas las propiedades del fichero: {}", Constantes.EMAIL_PROPERTIES);
            String from = emailProperties.getProperty(Constantes.KEY_EMAIL_FROM);
            log.info("Remitente: {}", from);
            String to = emailProperties.getProperty(Constantes.KEY_EMAIL_TO);
            log.info("Destinatarios: {}", to);

            EmailService emailService = new EmailServiceImpl();
            log.info("Leídas las propiedades del fichero: {}", Constantes.EMAIL_PROPERTIES);

            emailService.enviarEmail(emailProperties, from, to, asunto, cuerpoHtml);
            log.info("Leídas las propiedades del fichero: {}", Constantes.EMAIL_PROPERTIES);

        } catch (EmailServiceException e) {
            log.error("No se pudo enviar el email: Error en el servicio de correo -> {}", e.getMessage());
            throw new EmailServiceException ("No se pudo enviar el email: Error en el servicio de correo", e);
        }
    }
}