package local.jarios.common.util;

/**
 * Clase que mantiene las variables asociadas a las Keys de los ficheros properties
 * @author Home
 */
public final class PropertiesKeys {

    /* Nombre de las propiedades del fichero app.properties */
    /** Nombre */
    public static final String APP_NAME = "app.name";
    /** Descripción */
    public static final String APP_DESCRIPTION = "app.description";
    /** Path */
    public static final String APP_PATH = "app.path";
    /** Prefijo */
    public static final String APP_PREFIX = "app.prefix";
    /** Prefijo */
    public static final String APP_CHARACTER_ENCODING = "app.characterEncoding";
    /** Prefijo */
    public static final String APP_CONNECTION_COLLATION = "app.connectionCollation";
    /** Permite borrados y recreacion de datos durante la importacion */
    public static final String APP_ALLOW_DESTRUCTIVE_IMPORT = "app.allowDestructiveImport";

    /* Nombre de las propiedades del fichero hibernate.properties */
    /** Batch_size */
    public static final String HIBERNATE_JDBC_BATCH_SIZE = "hibernate.jdbc.batch_size";

    /*  */
    /** Url */
    public static final String JAKARTA_PERSISTENCE_JDBC_URL = "jakarta.persistence.jdbc.url";
    /** Driver */
    public static final String JAKARTA_PERSISTENCE_JDBC_DRIVER = "jakarta.persistence.jdbc.driver";
    /** Usuario */
    public static final String JAKARTA_PERSISTENCE_JDBC_USER = "jakarta.persistence.jdbc.user";
    /** Password */
    public static final String JAKARTA_PERSISTENCE_JDBC_PASSWORD= "jakarta.persistence.jdbc.password";

    /* Nombre de las propiedades del fichero mail.properties */
    /** User */
    public static final String MAIL_USER = "mail.smtp.user";
    /** Password */
    public static final String MAIL_PASSWORD = "mail.smtp.password";
    /** From */
    public static final String MAIL_FROM = "mail.from";
    /** To */
    public static final String MAIL_TO = "mail.to";

    /**
     * Constructor privado de la clase
     */
    private PropertiesKeys() {
        /* CONSTRUCTOR PRIVADO PRA EVITAR LA INSTANCIACIÓN */
    }

}



