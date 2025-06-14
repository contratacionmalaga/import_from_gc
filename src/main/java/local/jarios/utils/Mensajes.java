package local.jarios.utils;

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

    public static final String PERSISTIDO_ESTADISTICA =
            "Se ha persistido correctamente, la entidad Estadistica en la base de datos.";

    public static final String PERSISTIDO_FICHEROS_GC =
            "Se han persistido correctamente, los ficheros GC en la base de datos.";

    public static final String PERSISTIDO_PARSEO_FICHEROS_GC =
            "Se ha persistido correctamente, el objeto ParseoFicherosGc en la base de datos.";

    public static final String PERSISTIDO_LOG =
            "Se ha persistido correctamente, la entidad Log en la base de datos.";

    public static final String ASIGN_FECHA_HORA_INICIAL_PARSEO_TO_ESTADISTICA =
            "Asignada la fecha y hora de inicio del parseo al objeto Estadísitica. {}";

    public static final String ASIGN_FECHA_HORA_FINAL_PARSEO_TO_ESTADISTICA =
            "Asignada la fecha y hora final del parseo al objeto Estadísitica. {}";

    public static final String ASIGN_FECHA_HORA_INICIAL_BASE_DATOS_TO_ESTADISTICA =
            "Asignada la fecha y hora de inicio de la persistencia en base de datos al objeto Estadísitica. {}";

    public static final String ASIGN_FECHA_HORA_FINAL_BASE_DATOS_TO_ESTADISTICA =
            "Asignada la fecha y hora final de la persistencia en base de datos al objeto Estadísitica. {}";

    public static final String ASIGN_DURACION_BASE_DATOS =
            "Asignada la duración de la persistencia en base de datos al objeto Estadística correctamente.";

    public static final String MAIL_CREACION =
            "Creación del objeto Mail a partir del objeto Estadistica correctamente.";

    public static final String MAIL_ENVIADO =
            "Enviado Mail con los datos estadísticos de la ejecución.";

    public static final String SERVICE_CREACION_INICIO =
            "Inicio de la creación del Servicio de conexión con la base de datos.";

    public static final String SERVICE_CREACION_CREADO =
            "Servicio de conexión con la base de datos creado correctamente.";

    public static final String ESTADISTICA_CREACION =
            "Creación del objeto Estadística correctamente.";

    public static final String LOG_CREACION =
            "Creación del objeto Log correctamente. Id: {}";

    public static final String INICIO =
            "**** Inicio de la ejecución del programa ****";

    public static final String FINAL_ERROR =
            "!!!! La ejecución ha finalizado con ERRORES !!!!";

    public static final String EXCEPTION =
            "***** Excepción ocurrida *****";

    public static final String EXCEPTION_MENSAJE =
            "{}Mensaje: {}";

    public static final String EXCEPTION_STACK_TRACE =
            "{}Pila con del error:";

    public static final String PROPERTY_LOG =
            "Contenido de las variables definidas en los ficheros properties.";

    public static final String NUEMRO_FICHEROS_LEIDOS =
            "Se han leído {} ficheros del directorio: {}";

    public static final String NUEMRO_FICHEROS_PROCESADOS =
            "Se han procesado un total de {} ficheros";

    public static final String NUEMRO_REGISTROS_GC =
            "Se han procesado un total de {} registros";

    public static final String RUTA_FICHEROS =
            "Ruta desde la que se importarán los ficheros: {}";

    public static final String FINAL_CORRECTO =
            "La ejecución ha finalizado CORRECTAMENTE.";

    public static final String ENTIDADES =
            "Se han encontrado {} entidades dentro del paquete {}.";

    public static final String EXCEPTION_ERROR =
            "Excepción ocurrida en el la clase:{}, método: {}. Mensaje: {}";

    public static final String DROP_TABLE =
            "{}Borrada la tabla: {}";

    public static final String CREATE_TABLE =
            "{}Creada la tabla: {}";

    public static final String INSERT_RECORDS =
            "{}Insertardos {} registros en la tabla {}";

    /**
     * Constructor privado para evitar la instanciación.
     */
    private Mensajes() {}
}
