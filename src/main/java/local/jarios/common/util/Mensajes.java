package local.jarios.common.util;

/** Mensajes estaticos usados para logging y trazabilidad. */
public final class Mensajes {

  /** Mensaje de finalizacion erronea. */
  public static final String FINAL_ERRONEO = "Error";

  /** Mensaje de finalizacion correcta. */
  public static final String FINAL_CORRECTO =
      "La ejecución del aplicativo ha finalizado correctamente";

  /** Mensaje de persistencia correcta de la entidad Estadistica. */
  public static final String PERSISTIDO_ESTADISTICA =
      "Se ha persistido correctamente, la entidad Estadistica en la base de datos.";

  /** Mensaje de persistencia correcta de ficheros GC. */
  public static final String PERSISTIDO_LISTA_FICHEROS_GC =
      "Se han persistido correctamente, los ficheros GC en la base de datos.";

  /** Mensaje de persistencia correcta del parseo de ficheros GC. */
  public static final String PERSISTIDO_PARSEO_FICHEROS_GC =
      "Se ha persistido correctamente, el objeto ParseoFicherosGc en la base de datos.";

  /** Mensaje de persistencia correcta de la entidad Log. */
  public static final String PERSISTIDO_LOG =
      "Se ha persistido correctamente, la entidad Log en la base de datos.";

  /** Mensaje de asignacion de fecha inicial de parseo. */
  public static final String ASIGN_FECHA_HORA_INICIAL_PARSEO_TO_ESTADISTICA =
      "Asignada la fecha y hora de inicio del parseo al objeto Estadísitica. {}";

  /** Mensaje de asignacion de ficheros leidos al log. */
  public static final String AGIGN_LISTA_FICHEROS_LEIDOS_TO_LOG =
      "Asignada la lista de ficheros leídos desde el directorio al Log.";

  /** Mensaje de asignacion de fecha final de parseo. */
  public static final String ASIGN_FECHA_HORA_FINAL_PARSEO_TO_ESTADISTICA =
      "Asignada la fecha y hora final del parseo al objeto Estadísitica. {}";

  /** Mensaje de asignacion de fecha inicial de persistencia. */
  public static final String ASIGN_FECHA_HORA_INICIAL_BASE_DATOS_TO_ESTADISTICA =
      "Asignada la fecha y hora de inicio de la persistencia en base de datos "
          + "al objeto Estadísitica. {}";

  /** Mensaje de asignacion de fecha final de persistencia. */
  public static final String ASIGN_FECHA_HORA_FINAL_BASE_DATOS_TO_ESTADISTICA =
      "Asignada la fecha y hora final de la persistencia en base de datos "
          + "al objeto Estadísitica. {}";

  /** Mensaje de asignacion correcta de duracion de persistencia. */
  public static final String ASIGN_DURACION_BASE_DATOS =
      "Asignada la duración de la persistencia en base de datos "
          + "al objeto Estadística correctamente.";

  /** Mensaje de creacion correcta del servicio de base de datos. */
  public static final String SERVICE_CREACION_CREADO =
      "Servicio de conexión con la base de datos creado correctamente.";

  /** Mensaje de creacion correcta de Estadistica. */
  public static final String ESTADISTICA_CREACION =
      "Creación del objeto Estadística correctamente.";

  /** Mensaje de creacion correcta de Log. */
  public static final String LOG_CREACION = "Creación del objeto Log correctamente.";

  /** Mensaje de inicio de ejecucion. */
  public static final String INICIO = "**** Inicio del log";

  /** Mensaje de fin de ejecucion. */
  public static final String FINAL = "**** Final del log";

  /** Mensaje de ruta de importacion de ficheros. */
  public static final String RUTA_FICHEROS = "Ruta desde la que se importarán los ficheros: {}";

  /** Mensaje de numero de ficheros en ruta. */
  public static final String N_FICHEROS_RUTA = "Número de ficheros en la ruta: {}";

  /** Mensaje de numero de ficheros GC en base de datos. */
  public static final String N_FICHEROS_EN_BASE_DATOS =
      "Número de FicherosGc existentes en la base de datos: {}";

  /** Mensaje de conversion de lista persistida a mapa. */
  public static final String CONVERTIR_LISTA_PERSISTIDOS_EN_MAP =
      "Convertida la lista de ficheros persistidos en un Map correctamente.";

  /** Mensaje de entidades encontradas en un paquete. */
  public static final String ENTIDADES = "Se han encontrado {} entidades dentro del paquete {}.";

  /** Mensaje de excepcion en clase y metodo. */
  public static final String EXCEPTION_ERROR =
      "Excepción ocurrida en el la clase:{}, método: {}. Mensaje: {}";

  /** Mensaje de borrado de tabla. */
  public static final String DROP_TABLE = "{}Borrada la tabla: {}";

  /** Constructor privado de clase utilitaria. */
  private Mensajes() {}
}
