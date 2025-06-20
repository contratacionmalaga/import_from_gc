package local.jarios.common.util;

/**
 * Clase final que contiene constantes de mensajes estáticos
 * usados en la aplicación para logging y trazabilidad.
 * <p>
 * Facilita la gestión centralizada de textos comunes para logs,
 * evitando duplicación y facilitando modificaciones.
 * </p>
 *
 * <p><b>Author:</b> Juan Antonio</p>
 * <p><b>Date:</b> 04/06/2024</p>
 * <p><b>Team:</b> Juan Antonio</p>
 */
public final class Mensajes {

    /**
     * Mensaje para indicar que la entidad Estadistica se ha persistido correctamente en la base de datos.
     */
    public static final String PERSISTIDO_ESTADISTICA =
            "Se ha persistido correctamente, la entidad Estadistica en la base de datos.";

    /**
     * Mensaje para indicar que los ficheros GC se han persistido correctamente en la base de datos.
     */
    public static final String PERSISTIDO_LISTA_FICHEROS_GC =
            "Se han persistido correctamente, los ficheros GC en la base de datos.";

    /**
     * Mensaje para indicar que el objeto ParseoFicherosGc se ha persistido correctamente en la base de datos.
     */
    public static final String PERSISTIDO_PARSEO_FICHEROS_GC =
            "Se ha persistido correctamente, el objeto ParseoFicherosGc en la base de datos.";

    /**
     * Mensaje para indicar que la entidad Log se ha persistido correctamente en la base de datos.
     */
    public static final String PERSISTIDO_LOG =
            "Se ha persistido correctamente, la entidad Log en la base de datos.";

    /**
     * Mensaje para indicar que se ha asignado la fecha y hora de inicio del parseo al objeto Estadística.
     * Contiene un marcador para la fecha y hora asignada.
     */
    public static final String ASIGN_FECHA_HORA_INICIAL_PARSEO_TO_ESTADISTICA =
            "Asignada la fecha y hora de inicio del parseo al objeto Estadísitica. {}";

    /**
     * Mensaje para indicar que se ha asignado la fecha y hora de inicio del parseo al objeto Estadística.
     * Contiene un marcador para la fecha y hora asignada.
     */
    public static final String AGIGN_LISTA_FICHEROS_LEIDOS_TO_LOG =
            "Asignada la lista de ficheros leídos desde el directorio al Log.";

    /**
     * Mensaje para indicar que se ha asignado la fecha y hora final del parseo al objeto Estadística.
     * Contiene un marcador para la fecha y hora asignada.
     */
    public static final String ASIGN_FECHA_HORA_FINAL_PARSEO_TO_ESTADISTICA =
            "Asignada la fecha y hora final del parseo al objeto Estadísitica. {}";

    /**
     * Mensaje para indicar que se ha asignado la fecha y hora de inicio de la persistencia en base de datos al objeto Estadística.
     * Contiene un marcador para la fecha y hora asignada.
     */
    public static final String ASIGN_FECHA_HORA_INICIAL_BASE_DATOS_TO_ESTADISTICA =
            "Asignada la fecha y hora de inicio de la persistencia en base de datos al objeto Estadísitica. {}";

    /**
     * Mensaje para indicar que se ha asignado la fecha y hora final de la persistencia en base de datos al objeto Estadística.
     * Contiene un marcador para la fecha y hora asignada.
     */
    public static final String ASIGN_FECHA_HORA_FINAL_BASE_DATOS_TO_ESTADISTICA =
            "Asignada la fecha y hora final de la persistencia en base de datos al objeto Estadísitica. {}";

    /**
     * Mensaje para indicar que se ha asignado correctamente la duración de la persistencia en base de datos al objeto Estadística.
     */
    public static final String ASIGN_DURACION_BASE_DATOS =
            "Asignada la duración de la persistencia en base de datos al objeto Estadística correctamente.";

    /**
     * Mensaje que indica la creación correcta del objeto Mail a partir del objeto Estadistica.
     */
    public static final String MAIL_CREACION =
            "Creación del objeto Mail a partir del objeto Estadistica correctamente.";

    /**
     * Mensaje que indica que el email con datos estadísticos de la ejecución ha sido enviado correctamente.
     */
    public static final String MAIL_ENVIADO =
            "Enviado Mail con los datos estadísticos de la ejecución.";

    /**
     * Mensaje que indica el inicio de la creación del servicio de conexión con la base de datos.
     */
    public static final String SERVICE_CREACION_INICIO =
            "Inicio de la creación del Servicio de conexión con la base de datos.";

    /**
     * Mensaje que indica que el servicio de conexión con la base de datos se ha creado correctamente.
     */
    public static final String SERVICE_CREACION_CREADO =
            "Servicio de conexión con la base de datos creado correctamente.";

    /**
     * Mensaje que indica la creación correcta del objeto Estadística.
     */
    public static final String ESTADISTICA_CREACION =
            "Creación del objeto Estadística correctamente.";

    /**
     * Mensaje que indica la creación correcta del objeto Log.
     * Contiene un marcador para el Id del log.
     */
    public static final String LOG_CREACION =
            "Creación del objeto Log correctamente.";

    /**
     * Mensaje que indica el inicio de la ejecución del programa.
     */
    public static final String INICIO =
            "**** Inicio del log";

    /**
     * Mensaje que indica el inicio de la ejecución del programa.
     */
    public static final String FINAL =
            "**** Final del log";

    /**
     * Mensaje que indica que la ejecución ha finalizado con errores.
     */
    public static final String FINAL_ERROR =
            "!!!! La ejecución ha finalizado con ERRORES !!!!";

    /**
     * Mensaje que indica que ha ocurrido una excepción.
     */
    public static final String EXCEPTION =
            "***** Excepción ocurrida *****";

    /**
     * Mensaje para mostrar el detalle del mensaje de una excepción.
     * Contiene marcadores para tabuladores y mensaje.
     */
    public static final String EXCEPTION_MENSAJE =
            "{}Mensaje: {}";

    /**
     * Mensaje que indica el inicio de la pila (stack trace) del error.
     * Contiene marcador para tabulador.
     */
    public static final String EXCEPTION_STACK_TRACE =
            "{}Pila con del error:";

    /**
     * Mensaje que muestra el contenido de las variables definidas en los ficheros properties.
     */
    public static final String PROPERTY_LOG =
            "Contenido de las variables definidas en los ficheros properties.";

    /**
     * Mensaje que indica el número de ficheros leídos de un directorio.
     * Contiene marcadores para cantidad y ruta.
     */
    public static final String NUMERO_FICHEROS_LEIDOS =
            "Se han leído {} ficheros del directorio: {}";

    /**
     * Mensaje que indica el número total de ficheros procesados.
     * Contiene marcador para cantidad.
     */
    public static final String NUEMRO_FICHEROS_PROCESADOS =
            "Se han procesado un total de {} ficheros";

    /**
     * Mensaje que indica el número total de registros GC procesados.
     * Contiene marcador para cantidad.
     */
    public static final String NUEMRO_REGISTROS_GC =
            "Se han procesado un total de {} registros";

    /**
     * Mensaje que indica la ruta desde la que se importarán los ficheros.
     * Contiene marcador para la ruta.
     */
    public static final String RUTA_FICHEROS =
            "Ruta desde la que se importarán los ficheros: {}";

    /**
     * Mensaje que indica la ruta desde la que se importarán los ficheros.
     * Contiene marcador para la ruta.
     */
    public static final String N_FICHEROS_RUTA =
            "Número de ficheros en la ruta: {}";

    /**
     * Mensaje que indica la ruta desde la que se importarán los ficheros.
     * Contiene marcador para la ruta.
     */
    public static final String N_FICHEROS_PERSISTIDOS =
            "Número de FicherosGc existentes en la base de datos: {}";

    /**
     * Mensaje que indica la ruta desde la que se importarán los ficheros.
     * Contiene marcador para la ruta.
     */
    public static final String CONVERTIR_LISTA_PERSISTIDOS_EN_MAP =
            "Convertida la lista de ficheros persistidos en un Map correctamente.";

    /**
     * Mensaje que indica que la ejecución ha finalizado correctamente.
     */
    public static final String FINAL_CORRECTO =
            "La ejecución ha finalizado CORRECTAMENTE.";

    /**
     * Mensaje que indica la cantidad de entidades encontradas dentro de un paquete.
     * Contiene marcadores para la cantidad y el nombre del paquete.
     */
    public static final String ENTIDADES =
            "Se han encontrado {} entidades dentro del paquete {}.";

    /**
     * Mensaje que indica una excepción ocurrida en una clase y método específicos.
     * Contiene marcadores para clase, método y mensaje de error.
     */
    public static final String EXCEPTION_ERROR =
            "Excepción ocurrida en el la clase:{}, método: {}. Mensaje: {}";

    /**
     * Mensaje que indica que una tabla ha sido borrada.
     * Contiene marcadores para tabulador y nombre de tabla.
     */
    public static final String DROP_TABLE =
            "{}Borrada la tabla: {}";

    /**
     * Mensaje que indica que una tabla ha sido creada.
     * Contiene marcadores para tabulador y nombre de tabla.
     */
    public static final String CREATE_TABLE =
            "{}Creada la tabla: {}";

    /**
     * Mensaje que indica la inserción de registros en una tabla.
     * Contiene marcadores para tabulador, número de registros y nombre de tabla.
     */
    public static final String INSERT_RECORDS =
            "{}Insertardos {} registros en la tabla {}";

    /**
     * Constructor privado para evitar la instanciación de esta clase de utilidades.
     */
    private Mensajes() {
        // Constructor privado
    }
}
