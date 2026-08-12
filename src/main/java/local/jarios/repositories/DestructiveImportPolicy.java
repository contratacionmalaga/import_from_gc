package local.jarios.repositories;

import local.jarios.common.util.PropertiesFiles;
import local.jarios.common.util.PropertiesKeys;
import local.jarios.exceptions.MiRepositoryException;
import local.jarios.properties.api.PropertiesManagerService;
import local.jarios.properties.exception.PropertiesManagerException;

/** Controla si la importacion puede borrar tablas o registros existentes. */
final class DestructiveImportPolicy {

  /** Variable de entorno que habilita operaciones destructivas. */
  private static final String ENV_ALLOW_DESTRUCTIVE_IMPORT =
      "IMPORT_FROM_GC_ALLOW_DESTRUCTIVE_IMPORT";

  /** Constructor privado para evitar instanciacion. */
  private DestructiveImportPolicy() {}

  /**
   * Comprueba si las operaciones destructivas estan habilitadas.
   *
   * @param propertyManager gestor de propiedades de la aplicacion
   * @return {@code true} si la importacion destructiva esta habilitada
   */
  static boolean isAllowed(PropertiesManagerService propertyManager) {
    String envValue = System.getenv(ENV_ALLOW_DESTRUCTIVE_IMPORT);
    if (envValue != null && !envValue.isBlank()) {
      return isAllowed(envValue, null);
    }

    try {
      return isAllowed(
          null,
          propertyManager.getProperty(
              PropertiesFiles.APP, PropertiesKeys.APP_ALLOW_DESTRUCTIVE_IMPORT));
    } catch (PropertiesManagerException ex) {
      return false;
    }
  }

  /**
   * Resuelve la politica aplicando prioridad de variable de entorno sobre property.
   *
   * @param envValue valor de la variable de entorno
   * @param propertyValue valor de properties
   * @return {@code true} si la configuracion efectiva habilita borrados
   */
  static boolean isAllowed(String envValue, String propertyValue) {
    if (envValue != null && !envValue.isBlank()) {
      return parseBoolean(envValue);
    }

    return parseBoolean(propertyValue);
  }

  /**
   * Lanza una excepcion si la importacion destructiva no esta habilitada.
   *
   * @param propertyManager gestor de propiedades de la aplicacion
   */
  static void requireAllowed(PropertiesManagerService propertyManager) {
    if (!isAllowed(propertyManager)) {
      throw new MiRepositoryException(
          "La importacion destructiva esta deshabilitada. Configure "
              + "app.allowDestructiveImport=true o "
              + "IMPORT_FROM_GC_ALLOW_DESTRUCTIVE_IMPORT=true para permitir borrados.");
    }
  }

  /**
   * Interpreta valores booleanos estrictos para configuracion.
   *
   * @param value valor original
   * @return {@code true} solo cuando el valor es true ignorando mayusculas y espacios
   */
  static boolean parseBoolean(String value) {
    return "true".equalsIgnoreCase(value == null ? "" : value.trim());
  }
}
