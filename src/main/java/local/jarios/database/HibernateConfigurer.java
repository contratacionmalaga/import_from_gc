package local.jarios.database;

import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cfg.Configuration;

/** Construye la configuracion de Hibernate usada por la aplicacion. */
@Slf4j
public class HibernateConfigurer {

  /** Constructor por defecto. */
  public HibernateConfigurer() {}

  /**
   * Construye una configuracion de Hibernate a partir de las propiedades proporcionadas.
   *
   * @param hibernateProperties propiedades de Hibernate
   * @return configuracion preparada
   */
  public Configuration buildConfiguration(Properties hibernateProperties) {
    Configuration configuration = new Configuration();
    log.debug("[buildConfiguration] Objeto Configuration creado correctamente.");

    configuration.setProperties(hibernateProperties);
    log.debug(
        "[buildConfiguration] Hibernate Configuration creada con {} propiedades.",
        hibernateProperties.size());

    return configuration;
  }
}
