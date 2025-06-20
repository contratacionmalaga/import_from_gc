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

    /** Un tabulador simple (4 espacios). */
    public static final String TABULADOR_1 = "    ";

    /** Dos tabuladores (8 espacios). */
    public static final String TABULADOR_2 = "        ";

    /** Retorno de carro / salto de línea. */
    public static final String CR = "\n";

    /** Formato de fecha para logs y visualización. */
    public static final String FORMATO_FECHA = "dd-MM-yyyy HH:mm:ss.SSS";

    /** Nombre sin extensión del fichero app.properties */
    public static final String APP_PROPERTIES = "app";

    /** Nombre sin extensión del fichero app.properties */
    public static final String EMAIL_PROPERTIES = "email";

    /** Nombre sin extensión del fichero app.properties */
    public static final String HIBERNATE_PROPERTIES = "hibernate";

    /** Ruta del directorio con los ficheros properties */
    public static final String CONFIG_DIR = "config";

    /** clave utilizada para la encriptación / desencriptación de las value con formato ENC(encrypt_value) */
    public static final String ENCRYPT_PASSWORD = "Malaga$2025";

    /** clave utilizada para la encriptación / desencriptación de las value con formato ENC(encrypt_value) */
    public static final String KEY_EMAIL_FROM = "mail.from";

    /** clave utilizada para la encriptación / desencriptación de las value con formato ENC(encrypt_value) */
    public static final String KEY_EMAIL_TO = "mail.to";

    /** clave utilizada para la encriptación / desencriptación de las value con formato ENC(encrypt_value) */
    public static final String KEY_APP_NAME = "app.name";

    /** clave utilizada para la encriptación / desencriptación de las value con formato ENC(encrypt_value) */
    public static final String KEY_APP_DESCRIPTION = "app.description";

    /** clave utilizada para la encriptación / desencriptación de las value con formato ENC(encrypt_value) */
    public static final String KEY_APP_PREFIX = "app.prefix";

    /**
     * Constructor privado para evitar instanciación.
     */
    private Constantes() {
        // No instanciable
    }
}
