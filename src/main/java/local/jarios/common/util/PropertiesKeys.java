package local.jarios.common.util;

/** Claves usadas en los ficheros de propiedades. */
public final class PropertiesKeys {

  /** Nombre de la aplicacion. */
  public static final String APP_NAME = "app.name";

  /** Descripcion de la aplicacion. */
  public static final String APP_DESCRIPTION = "app.description";

  /** Ruta de ficheros de entrada. */
  public static final String APP_PATH = "app.path";

  /** Prefijo de aplicacion. */
  public static final String APP_PREFIX = "app.prefix";

  /** Codificacion de caracteres de la aplicacion. */
  public static final String APP_CHARACTER_ENCODING = "app.characterEncoding";

  /** Collation de conexion de la aplicacion. */
  public static final String APP_CONNECTION_COLLATION = "app.connectionCollation";

  /** Permite borrados y recreacion de datos durante la importacion. */
  public static final String APP_ALLOW_DESTRUCTIVE_IMPORT = "app.allowDestructiveImport";

  /** Tamano de lote JDBC de Hibernate. */
  public static final String HIBERNATE_JDBC_BATCH_SIZE = "hibernate.jdbc.batch_size";

  /** URL JDBC Jakarta Persistence. */
  public static final String JAKARTA_PERSISTENCE_JDBC_URL = "jakarta.persistence.jdbc.url";

  /** Driver JDBC Jakarta Persistence. */
  public static final String JAKARTA_PERSISTENCE_JDBC_DRIVER = "jakarta.persistence.jdbc.driver";

  /** Usuario JDBC Jakarta Persistence. */
  public static final String JAKARTA_PERSISTENCE_JDBC_USER = "jakarta.persistence.jdbc.user";

  /** Password JDBC Jakarta Persistence. */
  public static final String JAKARTA_PERSISTENCE_JDBC_PASSWORD =
      "jakarta.persistence.jdbc.password";

  /** Usuario SMTP. */
  public static final String MAIL_USER = "mail.smtp.user";

  /** Password SMTP. */
  public static final String MAIL_PASSWORD = "mail.smtp.password";

  /** Remitente de correo. */
  public static final String MAIL_FROM = "mail.from";

  /** Destinatario de correo. */
  public static final String MAIL_TO = "mail.to";

  /** Constructor privado de clase utilitaria. */
  private PropertiesKeys() {}
}
