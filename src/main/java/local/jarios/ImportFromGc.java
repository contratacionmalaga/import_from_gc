package local.jarios;

import local.jarios.common.util.Constantes;
import local.jarios.common.util.Mensajes;
import local.jarios.common.util.PropertiesFiles;
import local.jarios.common.util.PropertiesKeys;
import local.jarios.email.api.EmailSender;
import local.jarios.email.api.EmailSenderImpl;
import local.jarios.email.api.EmailService;
import local.jarios.email.api.EmailServiceImpl;
import local.jarios.email.exception.EmailException;
import local.jarios.email.helper.EmailHelper;
import local.jarios.email.model.EmailData;
import local.jarios.email.validator.EmailRequestValidator;
import local.jarios.encryptor.exception.EncryptorException;
import local.jarios.entity.Estadistica;
import local.jarios.entity.FicheroGc;
import local.jarios.entity.Log;
import local.jarios.exceptions.MiParseException;
import local.jarios.exceptions.MiServiceException;
import local.jarios.exceptions.MiUnknownHostException;
import local.jarios.helpers.ComunHelper;
import local.jarios.helpers.FileHelper;
import local.jarios.entity.ParseoFicherosGc;
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
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static local.jarios.helpers.FicheroGcHelper.getParseoFicherosGc;

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
     * Servicio de gestión de propiedades de configuración.
     * <p>
     * Se obtiene como instancia singleton mediante {@link PropertiesManagerServiceImpl#getInstance()}.
     * Permite cargar, acceder y gestionar propiedades definidas en ficheros externos.
     * </p>
     */
    public static final PropertiesManagerService propertiesManager = PropertiesManagerServiceImpl.getInstance();

    /**
     * Nombre de la aplicación, cargado desde las propiedades externas.
     * <p>
     * Se obtiene desde el fichero de configuración a través de {@code propertiesManager}
     * utilizando la clave {@code Constantes.KEY_APP_NAME}.
     * </p>
     */
    public static String appName = null;

    /**
     * Versión de la aplicación en ejecución.
     * <p>
     * Se determina mediante el componente {@link local.jarios.version.api.Version}
     * que analiza los metadatos del JAR en ejecución.
     * </p>
     */
    public static String appVersion = null;

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

            // Muestro el valor de APP_NAME
            appName = propertiesManager.getProperty(PropertiesFiles.APP, PropertiesKeys.APP_NAME);
            log.info("AppName: {}", appName);

            // Obtengo y muestro el valor de APP_VERSION
            appVersion = versionService.getVersion(VersionDemo.class);
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
            var path = propertiesManager.getProperty(PropertiesFiles.APP, PropertiesKeys.APP_PATH);
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

            String prefijo = propertiesManager.getProperty(PropertiesFiles.APP, PropertiesKeys.APP_PREFIX);
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

            //
            enviarEmail(estadistica, null, true);
            log.info("Email enviado correctamente.");

            // Finalizo el programa correctamente
            finalizar (Mensajes.FINAL_CORRECTO, 0);

        } catch (MiServiceException ex) {
            manejarExcepcion(ex, "[MiServiceException] - ");
        } catch (MiParseException ex) {
            manejarExcepcion(ex, "[MiParseException] - ");
        } catch (MiUnknownHostException ex) {
            manejarExcepcion(ex, "[MiUnknownHostException] - ");
        } catch (EncryptorException ex) {
            manejarExcepcion(ex, "[EncryptorException] - ");
        } catch (EmailException ex) {
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
     * @param originalException           La excepción que fue lanzada.
     * @param mensajeError El mensaje personalizado que describe el contexto del error.
     */
    private static void manejarExcepcion(Exception originalException, String mensajeError) {

        log.error("{}. Error: {}", mensajeError, originalException);

        try {
            enviarEmail(null, originalException, false);
            log.info("[manejarExcepcion] - Correo de error enviado correctamente.");

        } catch (EmailException | MiUnknownHostException | PropertiesManagerException emailEx) {
            log.error("[manejarExcepcion] - Error al enviar el correo con los errores:", emailEx);
        }

        finalizar(Mensajes.FINAL_ERRONEO, 1);
    }

    /**
     * Construye un objeto {@link EmailData} con toda la información necesaria para el envío de un correo,
     * en función del resultado del proceso (éxito o error).
     * <p>
     * Utiliza la configuración cargada desde el sistema de propiedades para establecer remitente y destinatario.
     * El asunto y el cuerpo del mensaje se generan usando las utilidades de {@link EmailHelper}.
     * </p>
     *
     * @param estadistica Objeto {@link Estadistica} que contiene datos del proceso. Puede ser {@code null} en caso de error.
     * @param ex Excepción lanzada durante la ejecución, en caso de fallo. Puede ser {@code null} si el proceso fue exitoso.
     * @param success Indicador booleano que señala si el proceso finalizó correctamente ({@code true}) o con error ({@code false}).
     * @return Objeto {@link EmailData} completamente inicializado y listo para ser enviado.
     * @throws MiUnknownHostException En caso de no poder obtener el nombre del equipo
     * @throws PropertiesManagerException En caso de tener problemas para leer el fichero properties
     */
    private static EmailData construirEmailData(Estadistica estadistica, Exception ex, boolean success)
            throws MiUnknownHostException, PropertiesManagerException  {

        String equipo = ComunHelper.getHostName();
        log.info("[construirEmailData] - Equipo desde el que se envía el email: {}", equipo);

        String from = propertiesManager.getProperty(PropertiesFiles.MAIL, PropertiesKeys.MAIL_FROM);
        log.info("[construirEmailData] - Remitente: {}", from);

        String to = propertiesManager.getProperty(PropertiesFiles.MAIL, PropertiesKeys.MAIL_TO);
        log.info("[construirEmailData] - Destinatarios: {}", to);

        // Defino el asunto y el cupero del Email
        String asunto = EmailHelper.getAsunto(appName, appVersion, equipo, success);
        log.info("[construirEmailData] - Asunto del correo: {}.", asunto);

        String cuerpo;
        if (success) {
            cuerpo = EmailHelper.getCuerpoEstadistica(toStringMatrix(estadistica));
        } else {
            cuerpo = EmailHelper.getCuerpoExcepcion(obtenerStackTraceComoArray(ex));
        }
        log.info("[construirEmailData] - Cuerpo del email creado correctamente");

        return new EmailData(from, to, asunto, cuerpo);

    }

    /**
     * Envía un email con el asunto y cuerpo indicados usando la configuración de propiedades.
     * <p>
     * Método común para centralizar el envío de emails evitando duplicidad.
     * Controla excepciones relacionadas con el envío y las registra.
     * </p>
     *
     * @param estadistica            Asunto del email a enviar.
     * @param ex        Cuerpo del email en formato HTML.
     * @param success        Cuerpo del email en formato HTML.
     * @throws MiUnknownHostException generada al intentar acceder al nombre del equipo
     * @throws PropertiesManagerException generada al intentar acceder a un fichero properties
     * @throws EmailException generada al intentar envair un email
     */
    private static void enviarEmail(Estadistica estadistica, Exception ex, boolean success)
            throws MiUnknownHostException, PropertiesManagerException, EmailException {

        // Configuración del servidor SMTP
        Properties emailProps = propertiesManager.getProperties(PropertiesFiles.MAIL);
        log.info("[enviarEmail] - Properties cargadas correctamente.");

        // Construcción de los datos del correo
        EmailData emailData = construirEmailData(estadistica, ex, success);
        log.info("[enviarEmail] - EmailData creado correctamente.");

        EmailRequestValidator.validarEmailRequest(emailProps, emailData);
        log.info("[enviarEmail] - Properties e EmailData validados correctamente.");

        // Creación del servicio de correo con la implementación de envío SMTP
        EmailSender emailSender = new EmailSenderImpl();
        log.info("[enviarEmail] - Creación del objeto EmailSender correctamente.");

        EmailService emailService = new EmailServiceImpl(emailSender);
        log.info("[enviarEmail] - Creado el objeto EmailService correctamente.");

        // Envío del correo
        emailService.sendEmail(emailProps, emailData);
        log.info("[enviarEmail] - Correo enviado correctamente.");

    }

    /**
     * Finaliza la ejecución del programa mostrando un mensaje de log
     * y llamando a System.exit con el código proporcionado.
     *
     * @param mensaje  Mensaje que se mostrará en el log.
     * @param exitCode Código de salida del sistema:
     *                 0 para éxito, 1 para error. Otros valores también serán aceptados.
     */
    public static void finalizar(String mensaje, int exitCode) {

        //
        if (exitCode == 0) {
            log.info(mensaje);
        } else {
            log.error("{} (Código de salida: {})", mensaje, exitCode);
        }

        log.info(Mensajes.FINAL); // Se asume que FINAL es una constante tipo String
        System.exit(exitCode);
    }

    /**
     * Convierte los campos de una instancia de {@link Estadistica} en una matriz de cadenas de texto.
     * <p>
     * Cada fila de la matriz representa un par clave-valor donde:
     * <ul>
     *     <li>La primera columna es el nombre del campo.</li>
     *     <li>La segunda columna es el valor del campo convertido a {@code String}.</li>
     * </ul>
     * Para campos que son instancias de {@link Log}, se utiliza el valor del identificador ({@code getId()}).
     *
     * @param estadistica la instancia de {@code Estadistica} que se va a procesar
     * @return una matriz de {@code String} con los nombres y valores de los campos de la instancia
     */
    private static String[][] toStringMatrix(Estadistica estadistica) {

        List<String[]> datos = new ArrayList<>();
        log.debug("[toStringMatrix] - Creación de List<String[]>");

        Field[] fields = Estadistica.class.getDeclaredFields(); // también corregido esto: getClass() → .class
        log.debug("[toStringMatrix] - Creación de Field[]");

        for (Field field : fields) {

            log.debug("[toStringMatrix] - Campo: {}", field.getName());
            field.setAccessible(true);

            try {

                Object value = field.get(estadistica); //
                log.debug("[toStringMatrix] - Obtengo el valor: {}", value);

                String nombreCampo = field.getName();
                String valorCampo;

                if (value instanceof Log logEntity && logEntity.getId() != null) {
                    valorCampo = logEntity.getId().toString();
                } else {
                    valorCampo = String.valueOf(value); // Maneja null de forma segura
                }

                datos.add(new String[]{nombreCampo, valorCampo});
                log.debug("[toStringMatrix] - {} - {}", nombreCampo, valorCampo);

            } catch (IllegalAccessException e) {

                log.debug("[toStringMatrix] - Error de acceso ilegal. Error: {}", e.getMessage());
                datos.add(new String[]{field.getName(), "Error al acceder"});

            }
        }

        return datos.toArray(new String[0][0]);
    }

    /**
     * Convierte el stack trace de una excepción en un arreglo de cadenas de texto.
     * <p>
     * Cada elemento del arreglo representa una línea del stack trace, tal como se imprimiría
     * en un log o consola. Este método es útil para enviar errores por correo o almacenarlos
     * en sistemas donde no se puede registrar el {@code Throwable} directamente.
     * </p>
     *
     * @param ex la excepción de la cual se extrae el stack trace
     * @return un arreglo de {@code String} que representa línea por línea el stack trace
     */
    public static String[] obtenerStackTraceComoArray(Throwable ex) {

        //
        StackTraceElement[] elementos = ex.getStackTrace();
        log.debug("[obtenerStackTraceComoArray] - Obtenidos los elementos del StactTrace. Nº elementos: {}", elementos.length);
        String[] resultado = new String[elementos.length];
        log.debug("[obtenerStackTraceComoArray] - Defino un String[] con el número de elementos del StackTrace.");
        for (int i = 0; i < elementos.length; i++) {
            resultado[i] = elementos[i].toString();
            log.debug("[obtenerStackTraceComoArray] - Elemento: {} - {}", i, elementos[i].toString());
        }
        return resultado;
    }
}