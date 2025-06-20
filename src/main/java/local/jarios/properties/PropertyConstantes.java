package local.jarios.properties;

/**
 * Constantes utilizadas para las claves de configuración en archivos properties.
 * Esta clase es final y no instanciable, solo provee constantes estáticas.
 *
 * @author Juan Antonio
 * @since 04/06/2024
 */
public final class PropertyConstantes {

    /**
     * Clave para la ruta base de configuración.
     */
    public static final String APP_PATH = "app.path";

    /**
     * Clave para el nombre de la configuración.
     */
    public static final String APP_DESCRIPCION = "app.descripcion";

    /**
     * Clave para el prefijo de configuración.
     */
    public static final String APP_PREFIJO = "app.prefijo";

    /**
     * Clave para el nombre de la configuración.
     */
    public static final String APP_NAME = "app.name";

    /**
     * Clave para la URL JDBC de Jakarta Persistence.
     */
    public static final String LOCAL_URL = "jakarta.persistence.jdbc.url";

    /**
     * Constructor privado para evitar instanciación.
     */
    private PropertyConstantes() {
        // Constructor vacío
    }
}
