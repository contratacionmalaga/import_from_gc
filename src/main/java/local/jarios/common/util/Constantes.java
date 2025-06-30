package local.jarios.common.util;

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

    /** Retorno de carro / salto de línea. */
    public static final String CR = "\n";

    /** Ruta del directorio con los ficheros properties */
    public static final String CONFIG_DIR = "config";

    /** clave utilizada para la encriptación / desencriptación de las value con formato ENC(encrypt_value) */
    public static final String ENCRYPT_PASSWORD = "Malaga$2025";

    /**
     * Constructor privado para evitar instanciación.
     */
    private Constantes() {
        // No instanciable
    }
}
