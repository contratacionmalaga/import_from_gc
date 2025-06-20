package local.jarios.database;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cfg.Configuration;

import java.util.Properties;

/**
 * Description:
 * Clase encargada de construir y configurar la instancia de Hibernate Configuration.
 * Author: juan
 * Date: 28/12/2024
 * Team:
 */
@Slf4j
public class HibernateConfigurer {

    /**
     * Constructor por defecto.
     */
    public HibernateConfigurer() { /*    */ }

    /**
     * Construye una configuración de Hibernate a partir de las propiedades proporcionadas.
     *
     * @param hibernateProperties Propiedades para configurar Hibernate (conexión, dialecto, etc.)
     * @return Configuration configurada con las propiedades
     */
    public Configuration buildConfiguration(Properties hibernateProperties) {
        Configuration configuration = new Configuration();

        // Seteamos las propiedades
        configuration.setProperties(hibernateProperties);
        log.debug("[buildConfiguration] - Hibernate Configuration creada con {} propiedades.", hibernateProperties.size());

        return configuration;
    }
}
