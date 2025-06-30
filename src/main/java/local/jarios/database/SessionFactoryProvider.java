package local.jarios.database;

import local.jarios.common.util.PropertiesFiles;
import local.jarios.common.util.PropertiesKeys;
import local.jarios.properties.api.PropertiesManagerService;
import local.jarios.properties.api.PropertiesManagerServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.JdbcSettings;

import java.util.Properties;

/**
 * Clase responsable de proveer una instancia de {@link SessionFactory} configurada para Hibernate.
 * <p>
 * Esta clase se encarga de construir la {@code SessionFactory} a partir de las propiedades
 * proporcionadas y de escanear las entidades ubicadas en el paquete configurado.
 * </p>
 * <p>
 * Author: Juan Antonio<br>
 * Date: 14/06/2025<br>
 * Team: Contratacion Electrónica
 * </p>
 */
@Slf4j
public class SessionFactoryProvider {

    /** Paquete donde se encuentran las entidades JPA para el escaneo automático. */
    private static final String CONFIG_PACKAGE_NAME = "local.jarios.entity";

    /** Objeto para gestionar los ficheros properties. */
    private final PropertiesManagerService propertyManager;

    /**
     * Constructor vacío.
     */
    public SessionFactoryProvider() {
        this.propertyManager = PropertiesManagerServiceImpl.getInstance();
    }

    /**
     * Construye y devuelve una instancia de {@link SessionFactory} configurada
     * con las propiedades Hibernate proporcionadas.
     *
     * @return Instancia de {@link SessionFactory} configurada.
     * @throws HibernateException Si ocurre un error durante la creación de la SessionFactory.
     */
    public SessionFactory getSessionFactory() throws HibernateException {

        PropertiesManagerService propertiesManager = PropertiesManagerServiceImpl.getInstance();
        log.debug("[getSessionFactory] - El servicio de consulta de los ficheros properties se ha creado correctamente.");

        final var hibernateProperties = configurePrincipalProperties(propertyManager.getProperties(PropertiesFiles.HIBERNATE));
        log.debug("[getSessionFactory] - Propiedades de conexión a la BD: {}", hibernateProperties);

        var hibernateConfigurer = new HibernateConfigurer();
        log.debug("[getSessionFactory] - Objeto HibernateConfigurer creado correctamente.");

        var configuration = hibernateConfigurer.buildConfiguration(hibernateProperties);
        log.debug("[getSessionFactory] - Configuración Hibernate creada correctamente.");

        var entityScanner = new EntityScanner();
        log.debug("[getSessionFactory] - Objeto EntityScanner creado correctamente.");

        entityScanner.scanAndAddEntities(configuration, CONFIG_PACKAGE_NAME);
        log.debug("[getSessionFactory] - Entidades escaneadas y añadidas desde el paquete '{}'.", CONFIG_PACKAGE_NAME);

        try {

            var sessionFactory = configuration.buildSessionFactory();
            log.debug("[getSessionFactory] - SessionFactory creada exitosamente.");
            return sessionFactory;

        } catch (HibernateException e) {
            log.error("[getSessionFactory] - Error creando SessionFactory: {}", e.getMessage());
            throw e; // Propagar la excepción para que el llamador la maneje
        }
    }

    /**
     * Configura propiedades para la conexión principal.
     * @param props objeto Properties
     * @return objeto Properties
     */
    private Properties configurePrincipalProperties(Properties props) {
        setCommonConnectionProperties(props, PropertiesFiles.JAKARTA_PRINCIPAL);
        return props;
    }

    /**
     * Asigna las propiedades JDBC comúnes
     * @param props objeto Properties
     * @param file fichero desde el que cargar las propiedaes
     */
    private void setCommonConnectionProperties(Properties props, String file) {
        props.setProperty(JdbcSettings.JAKARTA_JDBC_URL,
                propertyManager.getProperty(file, PropertiesKeys.JAKARTA_PERSISTENCE_JDBC_URL));
        props.setProperty(JdbcSettings.JAKARTA_JDBC_DRIVER,
                propertyManager.getProperty(file, PropertiesKeys.JAKARTA_PERSISTENCE_JDBC_DRIVER));
        props.setProperty(JdbcSettings.JAKARTA_JDBC_USER,
                propertyManager.getProperty(file, PropertiesKeys.JAKARTA_PERSISTENCE_JDBC_USER));
        props.setProperty(JdbcSettings.JAKARTA_JDBC_PASSWORD,
                propertyManager.getProperty(file, PropertiesKeys.JAKARTA_PERSISTENCE_JDBC_PASSWORD));

        log.debug("Propiedades configuradas desde archivo '{}': {}", file, props);
    }
}
