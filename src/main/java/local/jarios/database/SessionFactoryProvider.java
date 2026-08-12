package local.jarios.database;

import java.util.Properties;
import local.jarios.common.util.PropertiesFiles;
import local.jarios.common.util.PropertiesKeys;
import local.jarios.exceptions.MiSessionFactoryProvider;
import local.jarios.properties.api.PropertiesManagerService;
import local.jarios.properties.api.PropertiesManagerServiceImpl;
import local.jarios.properties.exception.PropertiesManagerException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.JdbcSettings;

/** Provee una instancia configurada de {@link SessionFactory}. */
@Slf4j
public class SessionFactoryProvider {

  /** Paquete donde se encuentran las entidades JPA. */
  private static final String CONFIG_PACKAGE_NAME = "local.jarios.entity";

  /** Objeto para gestionar los ficheros properties. */
  private final PropertiesManagerService propertyManager;

  /** Constructor sin argumentos. */
  public SessionFactoryProvider() {
    this.propertyManager = PropertiesManagerServiceImpl.getInstance();
  }

  /**
   * Construye una instancia de {@link SessionFactory} configurada.
   *
   * @return instancia de {@link SessionFactory}
   * @throws MiSessionFactoryProvider si ocurre un error creando la factoria de sesiones
   */
  public SessionFactory getSessionFactory() throws MiSessionFactoryProvider {
    try {
      Properties props = propertyManager.getProperties(PropertiesFiles.HIBERNATE);
      log.debug(
          "[getSessionFactory] Propiedades leidas desde el fichero: {}",
          PropertiesFiles.HIBERNATE);

      final var hibernateProperties = configurePrincipalProperties(props);
      log.debug(
          "[getSessionFactory] Propiedades de Hibernate configuradas. Total: {}",
          hibernateProperties.size());

      var hibernateConfigurer = new HibernateConfigurer();
      log.debug("[getSessionFactory] Objeto HibernateConfigurer creado correctamente.");

      var configuration = hibernateConfigurer.buildConfiguration(hibernateProperties);
      log.debug("[getSessionFactory] Configuracion Hibernate creada correctamente.");

      var entityScanner = new EntityScanner();
      log.debug("[getSessionFactory] Objeto EntityScanner creado correctamente.");

      entityScanner.scanAndAddEntities(configuration, CONFIG_PACKAGE_NAME);
      log.debug("[getSessionFactory] Entidades anadidas desde '{}'.", CONFIG_PACKAGE_NAME);

      var sessionFactory = configuration.buildSessionFactory();
      log.debug("[getSessionFactory] SessionFactory creada exitosamente.");
      return sessionFactory;
    } catch (HibernateException | PropertiesManagerException ex) {
      String msg =
          String.format("[getSessionFactory] Error creando SessionFactory: %s", ex.getMessage());
      log.error(msg, ex);
      throw new MiSessionFactoryProvider(msg, ex);
    }
  }

  /**
   * Configura propiedades para la conexion principal.
   *
   * @param props propiedades de Hibernate
   * @return propiedades actualizadas
   * @throws PropertiesManagerException si falla la lectura de properties
   */
  private Properties configurePrincipalProperties(Properties props)
      throws PropertiesManagerException {
    setCommonConnectionProperties(props, PropertiesFiles.JAKARTA_PRINCIPAL);
    return props;
  }

  /**
   * Asigna las propiedades JDBC comunes.
   *
   * @param props propiedades de Hibernate
   * @param file fichero desde el que cargar las propiedades
   * @throws PropertiesManagerException si falla la lectura de properties
   */
  private void setCommonConnectionProperties(Properties props, String file)
      throws PropertiesManagerException {
    props.setProperty(
        JdbcSettings.JAKARTA_JDBC_URL,
        getPropertyOrEnv(
            file, PropertiesKeys.JAKARTA_PERSISTENCE_JDBC_URL, "IMPORT_FROM_GC_JDBC_URL"));
    props.setProperty(
        JdbcSettings.JAKARTA_JDBC_DRIVER,
        getPropertyOrEnv(
            file, PropertiesKeys.JAKARTA_PERSISTENCE_JDBC_DRIVER, "IMPORT_FROM_GC_JDBC_DRIVER"));
    props.setProperty(
        JdbcSettings.JAKARTA_JDBC_USER,
        getPropertyOrEnv(
            file, PropertiesKeys.JAKARTA_PERSISTENCE_JDBC_USER, "IMPORT_FROM_GC_JDBC_USER"));
    props.setProperty(
        JdbcSettings.JAKARTA_JDBC_PASSWORD,
        getPropertyOrEnv(
            file,
            PropertiesKeys.JAKARTA_PERSISTENCE_JDBC_PASSWORD,
            "IMPORT_FROM_GC_JDBC_PASSWORD"));

    log.debug("Propiedades JDBC configuradas desde archivo '{}' y variables de entorno.", file);
  }

  /**
   * Obtiene una propiedad permitiendo que una variable de entorno tenga prioridad.
   *
   * @param file fichero de propiedades
   * @param key clave de propiedades
   * @param envName nombre de variable de entorno
   * @return valor configurado
   * @throws PropertiesManagerException si falla la lectura de properties
   */
  private String getPropertyOrEnv(String file, String key, String envName)
      throws PropertiesManagerException {
    String envValue = System.getenv(envName);
    if (envValue != null && !envValue.isBlank()) {
      return envValue;
    }

    return propertyManager.getProperty(file, key);
  }
}
