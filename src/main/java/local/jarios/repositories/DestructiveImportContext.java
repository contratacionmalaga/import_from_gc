package local.jarios.repositories;

import local.jarios.common.util.PropertiesFiles;
import local.jarios.common.util.PropertiesKeys;
import local.jarios.properties.api.PropertiesManagerService;
import local.jarios.properties.exception.PropertiesManagerException;

/** Describe el destino efectivo de una importacion destructiva sin exponer secretos. */
final class DestructiveImportContext {

  /** Variable de entorno que sobrescribe la URL JDBC. */
  private static final String ENV_JDBC_URL = "IMPORT_FROM_GC_JDBC_URL";

  /** Variable de entorno que sobrescribe el usuario JDBC. */
  private static final String ENV_JDBC_USER = "IMPORT_FROM_GC_JDBC_USER";

  /** Texto usado cuando un valor no se puede resolver. */
  private static final String UNAVAILABLE = "<no disponible>";

  /** URL JDBC efectiva saneada para logs. */
  private final String jdbcTarget;

  /** Usuario JDBC efectivo saneado para logs. */
  private final String jdbcUser;

  /**
   * Constructor privado.
   *
   * @param jdbcUrl URL JDBC original
   * @param jdbcUser usuario JDBC original
   */
  private DestructiveImportContext(String jdbcUrl, String jdbcUser) {
    this.jdbcTarget = redactJdbcUrl(jdbcUrl);
    this.jdbcUser = sanitize(jdbcUser);
  }

  /**
   * Construye el contexto desde properties y variables de entorno.
   *
   * @param propertyManager gestor de propiedades de la aplicacion
   * @return contexto seguro para log
   */
  static DestructiveImportContext from(PropertiesManagerService propertyManager) {
    return new DestructiveImportContext(
        resolveProperty(propertyManager, PropertiesKeys.JAKARTA_PERSISTENCE_JDBC_URL, ENV_JDBC_URL),
        resolveProperty(
            propertyManager, PropertiesKeys.JAKARTA_PERSISTENCE_JDBC_USER, ENV_JDBC_USER));
  }

  /**
   * Devuelve una descripcion segura para logs operativos.
   *
   * @return descripcion sin password ni parametros de conexion
   */
  String describe() {
    return "jdbcTarget=" + jdbcTarget + ", jdbcUser=" + jdbcUser;
  }

  /**
   * Normaliza valores para log evitando cadenas vacias.
   *
   * @param value valor a mostrar
   * @return valor saneado
   */
  static String sanitize(String value) {
    if (value == null || value.isBlank()) {
      return UNAVAILABLE;
    }
    return value.trim();
  }

  /**
   * Redacta una URL JDBC para mostrar solo protocolo, host, puerto y base.
   *
   * @param jdbcUrl URL JDBC original
   * @return URL JDBC sin credenciales ni query string
   */
  static String redactJdbcUrl(String jdbcUrl) {
    String sanitized = sanitize(jdbcUrl);
    if (UNAVAILABLE.equals(sanitized)) {
      return sanitized;
    }

    int queryIndex = sanitized.indexOf('?');
    if (queryIndex >= 0) {
      sanitized = sanitized.substring(0, queryIndex) + "?<redacted>";
    }

    int schemeIndex = sanitized.indexOf("://");
    if (schemeIndex < 0) {
      return sanitized;
    }

    int authorityStart = schemeIndex + 3;
    int slashIndex = sanitized.indexOf('/', authorityStart);
    String authority = sanitized.substring(authorityStart);
    if (slashIndex >= 0) {
      authority = sanitized.substring(authorityStart, slashIndex);
    }

    int atIndex = authority.lastIndexOf('@');
    if (atIndex < 0) {
      return sanitized;
    }

    String redactedAuthority = "<credentials>@" + authority.substring(atIndex + 1);
    return sanitized.substring(0, authorityStart)
        + redactedAuthority
        + (slashIndex >= 0 ? sanitized.substring(slashIndex) : "");
  }

  /**
   * Resuelve una propiedad con prioridad de variable de entorno.
   *
   * @param propertyManager gestor de propiedades
   * @param key clave de properties
   * @param envName nombre de variable de entorno
   * @return valor efectivo o {@code null} si no se puede resolver
   */
  private static String resolveProperty(
      PropertiesManagerService propertyManager, String key, String envName) {
    String envValue = System.getenv(envName);
    if (envValue != null && !envValue.isBlank()) {
      return envValue;
    }

    try {
      return propertyManager.getProperty(PropertiesFiles.JAKARTA_PRINCIPAL, key);
    } catch (PropertiesManagerException ex) {
      return null;
    }
  }
}
