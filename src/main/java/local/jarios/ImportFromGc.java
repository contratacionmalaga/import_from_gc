package local.jarios;

import java.io.File;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
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
import local.jarios.encrypt.exception.EncryptorException;
import local.jarios.entity.Estadistica;
import local.jarios.entity.Log;
import local.jarios.entity.ParseoFicherosGc;
import local.jarios.exceptions.MiParseException;
import local.jarios.exceptions.MiServiceException;
import local.jarios.exceptions.MiUnknownHostException;
import local.jarios.helpers.ComunHelper;
import local.jarios.helpers.FicheroGcParser;
import local.jarios.helpers.FileHelper;
import local.jarios.helpers.TimeHelper;
import local.jarios.properties.api.PropertiesManagerService;
import local.jarios.properties.api.PropertiesManagerServiceImpl;
import local.jarios.properties.exception.PropertiesManagerException;
import local.jarios.services.Service;
import local.jarios.services.ServiceImpl;
import local.jarios.version.api.Version;
import local.jarios.version.api.VersionImpl;
import local.jarios.version.exception.VersionException;
import lombok.extern.slf4j.Slf4j;

/** Clase principal para importar informacion desde ficheros GC al sistema. */
@Slf4j
public class ImportFromGc {

  /** Servicio de gestion de propiedades de configuracion. */
  public static final PropertiesManagerService propertiesManager =
      PropertiesManagerServiceImpl.getInstance();

  /** Nombre de la aplicacion, cargado desde properties externos. */
  public static String appName = null;

  /** Version de la aplicacion en ejecucion. */
  public static String appVersion = null;

  /** Constructor sin argumentos. */
  public ImportFromGc() {}

  /**
   * Método principal que ejecuta el proceso completo de importación.
   *
   * @param args argumentos de línea de comandos (no usados)
   */
  public static void main(String[] args) {
    System.exit(run(args));
  }

  /**
   * Ejecuta el proceso completo y devuelve el código de salida sin terminar la JVM.
   *
   * @param args argumentos de línea de comandos (no usados)
   * @return código de salida del proceso
   */
  static int run(String[] args) {
    return run(new DefaultRuntime());
  }

  /**
   * Ejecuta el proceso completo con dependencias inyectadas para poder probar el flujo principal.
   *
   * @param runtime dependencias de ejecución
   * @return código de salida del proceso
   */
  static int run(RuntimeGateway runtime) {
    log.info(Mensajes.INICIO);

    try {
      Version versionService = runtime.createVersionService();
      log.info("[main] El servicio de consulta de la versión del JAR se ha creado correctamente.");
      appVersion = versionService.getVersion(ImportFromGc.class);
      log.info("AppVersion: {}", appVersion);

      PropertiesManagerService runtimePropertiesManager = runtime.getPropertiesManager();
      runtimePropertiesManager.setConfigDir(Constantes.PROPERTIES_DIR);
      log.info("Directorio configurado: /{}", Constantes.PROPERTIES_DIR);
      Set<String> clavesSensibles = Set.of("password");
      runtimePropertiesManager.setSensitiveKeys(clavesSensibles);
      log.info("[main] Establezco el conjunto de claves Sensibles: {}", clavesSensibles);
      runtimePropertiesManager.loadAllProperties();
      log.info(
          "[main] Ficheros .properties cargados desde /{} correctamente",
          Constantes.PROPERTIES_DIR);
      appName = runtimePropertiesManager.getProperty(PropertiesFiles.APP, PropertiesKeys.APP_NAME);
      log.info("[main] AppName: {}", appName);
      appVersion = versionService.getVersion(ImportFromGc.class);
      log.info("[main] AppVersion: {}", appVersion);
      Log miLog = new Log();
      log.info(Mensajes.LOG_CREACION);
      Estadistica estadistica = new Estadistica(miLog);
      log.info(Mensajes.ESTADISTICA_CREACION);
      LocalDateTime localDateTime = TimeHelper.getLocalDateTimeNow();
      estadistica.setFechaHoraInicial(localDateTime);
      final Service service = runtime.createService();
      log.info(Mensajes.SERVICE_CREACION_CREADO);
      var path = runtimePropertiesManager.getProperty(PropertiesFiles.APP, PropertiesKeys.APP_PATH);
      log.info(Mensajes.RUTA_FICHEROS, path);
      File[] arrayFicherosDirectorio = runtime.listFiles(path);
      estadistica.setNTotalFicherosLeidos(arrayFicherosDirectorio.length);
      log.info(Mensajes.N_FICHEROS_RUTA, arrayFicherosDirectorio.length);
      ParseoFicherosGc parseoFicherosGc =
          runtime.parseFiles(miLog, arrayFicherosDirectorio, estadistica);
      miLog.setFicherosGc(parseoFicherosGc.getListFicherosGc());
      log.info(Mensajes.AGIGN_LISTA_FICHEROS_LEIDOS_TO_LOG);
      localDateTime = TimeHelper.getLocalDateTimeNow();
      estadistica.setFechaHoraFinal(localDateTime);
      log.info(
          Mensajes.ASIGN_FECHA_HORA_FINAL_PARSEO_TO_ESTADISTICA,
          ComunHelper.getFechaHoraFormateada(localDateTime));
      String duracion =
          ComunHelper.getDiferenciaLocalDateTime(
              estadistica.getFechaHoraInicial(), estadistica.getFechaHoraFinal());
      estadistica.setDuracion(duracion);
      log.info("[main] Asignada la duracion de la ejecución ({})", duracion);
      miLog.setEstadistica(estadistica);
      service.persistirEnBaseDeDatos(miLog, parseoFicherosGc);
      log.info(Mensajes.PERSISTIDO_LOG);

      runtime.sendEmail(estadistica, null, true);
      log.info("[main] Email enviado correctamente.");
      return finalizar(Mensajes.FINAL_CORRECTO, 0);

    } catch (MiServiceException ex) {
      return manejarExcepcion(runtime, ex, "[MiServiceException] ");
    } catch (MiParseException ex) {
      return manejarExcepcion(runtime, ex, "[MiParseException] ");
    } catch (MiUnknownHostException ex) {
      return manejarExcepcion(runtime, ex, "[MiUnknownHostException] ");
    } catch (EncryptorException ex) {
      return manejarExcepcion(runtime, ex, "[EncryptorException] ");
    } catch (EmailException ex) {
      return manejarExcepcion(runtime, ex, "[EmailServiceException] ");
    } catch (PropertiesManagerException ex) {
      return manejarExcepcion(runtime, ex, "[PropertiesManaerException] ");
    } catch (VersionException ex) {
      return manejarExcepcion(runtime, ex, "[VersionException] ");
    } catch (Exception ex) {
      return manejarExcepcion(runtime, ex, "[Exception] ");
    }
  }

  /**
   * Maneja de forma centralizada las excepciones que ocurren durante la ejecución del programa.
   *
   * @param runtime dependencias de ejecución
   * @param originalException La excepción que fue lanzada.
   * @param mensajeError El mensaje personalizado que describe el contexto del error.
   * @return código de salida de error
   */
  private static int manejarExcepcion(
      RuntimeGateway runtime, Exception originalException, String mensajeError) {

    log.error("{}. Error: {}", mensajeError, originalException);

    try {
      runtime.sendEmail(null, originalException, false);
      log.info("[manejarExcepcion] Correo de error enviado correctamente.");

    } catch (EmailException | MiUnknownHostException | PropertiesManagerException emailEx) {
      log.error("[manejarExcepcion] Error al enviar el correo con los errores:", emailEx);
    }

    return finalizar(Mensajes.FINAL_ERRONEO, 1);
  }

  /** Puerta de enlace inyectable para aislar dependencias runtime en tests. */
  interface RuntimeGateway {

    /**
     * Devuelve el gestor de propiedades runtime.
     *
     * @return gestor de propiedades configurable
     */
    PropertiesManagerService getPropertiesManager();

    /**
     * Crea el servicio de version.
     *
     * @return servicio de version de la aplicacion
     */
    Version createVersionService();

    /**
     * Crea el servicio de persistencia.
     *
     * @return servicio de persistencia
     */
    Service createService();

    /**
     * Lista ficheros de entrada.
     *
     * @param path ruta de entrada configurada
     * @return ficheros encontrados en la ruta
     */
    File[] listFiles(String path);

    /**
     * Parsea los ficheros de entrada.
     *
     * @param miLog log de ejecucion asociado
     * @param files ficheros a procesar
     * @param estadistica datos estadisticos acumulados
     * @return resultado de parseo preparado para persistencia
     */
    ParseoFicherosGc parseFiles(Log miLog, File[] files, Estadistica estadistica);

    /**
     * Envia el email de resultado.
     *
     * @param estadistica datos estadisticos de la ejecucion
     * @param ex excepcion de ejecucion o {@code null} si no hubo error
     * @param success indica si la ejecucion termino correctamente
     * @throws MiUnknownHostException si no se puede resolver el nombre del equipo
     * @throws PropertiesManagerException si falla la lectura de configuracion
     * @throws EmailException si falla el envio del correo
     */
    void sendEmail(Estadistica estadistica, Exception ex, boolean success)
        throws MiUnknownHostException, PropertiesManagerException, EmailException;
  }

  /** Implementacion runtime real usada por la aplicacion. */
  private static final class DefaultRuntime implements RuntimeGateway {

    /** Constructor sin argumentos. */
    private DefaultRuntime() {}

    @Override
    public PropertiesManagerService getPropertiesManager() {
      return propertiesManager;
    }

    @Override
    public Version createVersionService() {
      return new VersionImpl();
    }

    @Override
    public Service createService() {
      return new ServiceImpl();
    }

    @Override
    public File[] listFiles(String path) {
      return FileHelper.getListaFicherosFromPath(path);
    }

    @Override
    public ParseoFicherosGc parseFiles(Log miLog, File[] files, Estadistica estadistica) {
      FicheroGcParser ficheroGcParser = new FicheroGcParser();
      return ficheroGcParser.parsearFicheros(miLog, files, estadistica);
    }

    @Override
    public void sendEmail(Estadistica estadistica, Exception ex, boolean success)
        throws MiUnknownHostException, PropertiesManagerException, EmailException {
      enviarEmail(getPropertiesManager(), estadistica, ex, success);
    }
  }

  /**
   * Construye el email de resultado del proceso.
   *
   * @param runtimePropertiesManager gestor de propiedades runtime
   * @param estadistica datos del proceso; puede ser {@code null} en caso de error
   * @param ex excepcion de ejecucion o {@code null} si el proceso fue exitoso
   * @param success indica si el proceso finalizo correctamente
   * @return email listo para envio
   * @throws MiUnknownHostException si no se puede obtener el nombre del equipo
   * @throws PropertiesManagerException si falla la lectura de properties
   */
  private static EmailData construirEmailData(
      PropertiesManagerService runtimePropertiesManager,
      Estadistica estadistica,
      Exception ex,
      boolean success)
      throws MiUnknownHostException, PropertiesManagerException {

    String equipo = ComunHelper.getHostName();
    log.info("[construirEmailData] Equipo desde el que se envía el email: {}", equipo);

    String from =
        getPropertyOrEnv(
            runtimePropertiesManager,
            PropertiesFiles.MAIL,
            PropertiesKeys.MAIL_FROM,
            "IMPORT_FROM_GC_MAIL_FROM");
    log.info("[construirEmailData] Remitente: {}", from);

    String to =
        getPropertyOrEnv(
            runtimePropertiesManager,
            PropertiesFiles.MAIL,
            PropertiesKeys.MAIL_TO,
            "IMPORT_FROM_GC_MAIL_TO");
    log.info("[construirEmailData] Destinatarios: {}", to);
    String asunto = EmailHelper.getAsunto(appName, appVersion, equipo, success);
    log.info("[construirEmailData] Asunto del correo: {}.", asunto);

    String cuerpo;
    if (success) {
      cuerpo = EmailHelper.getCuerpoEstadistica(toStringMatrix(estadistica));
    } else {
      cuerpo = EmailHelper.getCuerpoExcepcion(obtenerStackTraceComoArray(ex));
    }
    log.info("[construirEmailData] Cuerpo del email creado correctamente");

    return new EmailData(from, to, asunto, cuerpo);
  }

  /**
   * Envia un email con el asunto y cuerpo indicados.
   *
   * @param runtimePropertiesManager gestor de propiedades runtime
   * @param estadistica datos estadisticos de la ejecucion
   * @param ex excepcion producida durante la ejecucion
   * @param success indica si la ejecucion termino correctamente
   * @throws MiUnknownHostException generada al intentar acceder al nombre del equipo
   * @throws PropertiesManagerException generada al intentar acceder a un fichero properties
   * @throws EmailException generada al intentar envair un email
   */
  private static void enviarEmail(
      PropertiesManagerService runtimePropertiesManager,
      Estadistica estadistica,
      Exception ex,
      boolean success)
      throws MiUnknownHostException, PropertiesManagerException, EmailException {
    Properties emailProps = runtimePropertiesManager.getProperties(PropertiesFiles.MAIL);
    applyEnvOverride(emailProps, PropertiesKeys.MAIL_USER, "IMPORT_FROM_GC_MAIL_USER");
    applyEnvOverride(emailProps, PropertiesKeys.MAIL_PASSWORD, "IMPORT_FROM_GC_MAIL_PASSWORD");
    applyEnvOverride(emailProps, PropertiesKeys.MAIL_FROM, "IMPORT_FROM_GC_MAIL_FROM");
    applyEnvOverride(emailProps, PropertiesKeys.MAIL_TO, "IMPORT_FROM_GC_MAIL_TO");
    log.info("[enviarEmail] Properties cargadas correctamente.");
    EmailData emailData = construirEmailData(runtimePropertiesManager, estadistica, ex, success);
    log.info("[enviarEmail] EmailData creado correctamente.");

    EmailRequestValidator.validarEmailRequest(emailProps, emailData);
    log.info("[enviarEmail] Properties e EmailData validados correctamente.");
    EmailSender emailSender = new EmailSenderImpl();
    log.info("[enviarEmail] Creación del objeto EmailSender correctamente.");

    EmailService emailService = new EmailServiceImpl(emailSender);
    log.info("[enviarEmail] Creado el objeto EmailService correctamente.");
    emailService.sendEmail(emailProps, emailData);
    log.info("[enviarEmail] Correo enviado correctamente.");
  }

  /**
   * Aplica una variable de entorno sobre una propiedad cuando esta definida.
   *
   * @param props propiedades base
   * @param propertyKey clave a sobrescribir
   * @param envName nombre de la variable de entorno
   */
  private static void applyEnvOverride(Properties props, String propertyKey, String envName) {
    String envValue = System.getenv(envName);
    if (envValue != null && !envValue.isBlank()) {
      props.setProperty(propertyKey, envValue);
    }
  }

  /**
   * Obtiene una propiedad permitiendo que una variable de entorno tenga prioridad.
   *
   * @param runtimePropertiesManager gestor de propiedades runtime
   * @param file fichero de propiedades
   * @param key clave de propiedades
   * @param envName nombre de variable de entorno
   * @return valor configurado
   * @throws PropertiesManagerException si falla la lectura del fichero de propiedades
   */
  private static String getPropertyOrEnv(
      PropertiesManagerService runtimePropertiesManager, String file, String key, String envName)
      throws PropertiesManagerException {
    String envValue = System.getenv(envName);
    if (envValue != null && !envValue.isBlank()) {
      return envValue;
    }

    return runtimePropertiesManager.getProperty(file, key);
  }

  /**
   * Finaliza la ejecución del programa mostrando un mensaje de log y llamando a System.exit con el
   * código proporcionado.
   *
   * @param mensaje Mensaje que se mostrará en el log.
   * @param exitCode codigo de salida del sistema
   * @return codigo de salida calculado
   */
  public static int finalizar(String mensaje, int exitCode) {

    if (exitCode == 0) {
      log.info(mensaje);
    } else {
      log.error("{} (Código de salida: {})", mensaje, exitCode);
    }

    log.info(Mensajes.FINAL);
    return exitCode;
  }

  /**
   * Convierte los campos de una instancia de {@link Estadistica} en una matriz de texto.
   *
   * @param estadistica la instancia de {@code Estadistica} que se va a procesar
   * @return una matriz de {@code String} con los nombres y valores de los campos de la instancia
   */
  private static String[][] toStringMatrix(Estadistica estadistica) {

    List<String[]> datos = new ArrayList<>();
    log.debug("[toStringMatrix] Creación de List<String[]>");

    Field[] fields = Estadistica.class.getDeclaredFields();
    log.debug("[toStringMatrix] Creación de Field[]");

    for (Field field : fields) {

      log.debug("[toStringMatrix] Campo: {}", field.getName());
      field.setAccessible(true);

      try {

        Object value = field.get(estadistica);
        log.debug("[toStringMatrix] Obtengo el valor: {}", value);

        String nombreCampo = field.getName();
        String valorCampo;

        if (value instanceof Log logEntity && logEntity.getId() != null) {
          valorCampo = logEntity.getId().toString();
        } else {
          valorCampo = String.valueOf(value);
        }

        datos.add(new String[] {nombreCampo, valorCampo});
        log.debug("[toStringMatrix] {} - {}", nombreCampo, valorCampo);

      } catch (IllegalAccessException e) {

        log.debug("[toStringMatrix] Error de acceso ilegal. Error: {}", e.getMessage());
        datos.add(new String[] {field.getName(), "Error al acceder"});
      }
    }

    return datos.toArray(new String[0][0]);
  }

  /**
   * Convierte el stack trace de una excepcion en un arreglo de cadenas de texto.
   *
   * @param ex la excepción de la cual se extrae el stack trace
   * @return un arreglo de {@code String} que representa línea por línea el stack trace
   */
  public static String[] obtenerStackTraceComoArray(Throwable ex) {

    StackTraceElement[] elementos = ex.getStackTrace();
    log.debug(
        "[obtenerStackTraceComoArray] Obtenidos elementos del stack trace: {}", elementos.length);
    String[] resultado = new String[elementos.length];
    log.debug("[obtenerStackTraceComoArray] Defino un String[] para el stack trace.");
    for (int i = 0; i < elementos.length; i++) {
      resultado[i] = elementos[i].toString();
      log.debug("[obtenerStackTraceComoArray] Elemento: {} - {}", i, elementos[i].toString());
    }
    return resultado;
  }
}
