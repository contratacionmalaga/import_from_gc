package local.jarios.utils;

/**
 * Clase que contiene constantes generales utilizadas a lo largo de la aplicación.
 * <p>
 * Contiene cadenas comunes, formatos de fecha y caracteres de control,
 * para evitar el uso de valores mágicos en el código.
 * </p>
 *
 * <p><b>Author:</b> Juan Antonio</p>
 * <p><b>Date:</b> 04/06/2024</p>
 * <p><b>Team:</b> Juan Antonio</p>
 */
public final class Constantes {

    /** Cadena vacía. */
    public static final String CADENA_VACIA = "";

    /** Clave "code" utilizada en mapas o JSON. */
    public static final String VALUE_CODE = "code";

    /** Clave "nombre" utilizada en mapas o JSON. */
    public static final String VALUE_NOMBRE = "nombre";

    /** Clave "name" utilizada en mapas o JSON. */
    public static final String VALUE_NAME = "name";

    /** Un tabulador simple (4 espacios). */
    public static final String TABULADOR_1 = "    ";

    /** Dos tabuladores (8 espacios). */
    public static final String TABULADOR_2 = "        ";

    /** Retorno de carro / salto de línea. */
    public static final String CR = "\n";

    /** Formato de fecha para logs y visualización. */
    public static final String FORMATO_FECHA = "dd-MM-yyyy HH:mm:ss.SSS";

    /** Nombre sin extensión del fichero config.properties */
    public static final String CONFIG_PROPERTIES = "config";

    /** Nombre sin extensión del fichero config.properties */
    public static final String HIBERNATE_PROPERTIES = "hibernate";

    /**
     * Constructor privado para evitar instanciación.
     */
    private Constantes() {
        // No instanciable
    }
}
