package local.jarios.database;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;

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

    /**
     * Paquete donde se encuentran las entidades JPA para el escaneo automático.
     */
    private static final String CONFIG_PACKAGE_NAME = "local.jarios.entity";

    /**
     * Constructor vacío.
     */
    public SessionFactoryProvider() {
        // Constructor por defecto
    }

    /**
     * Construye y devuelve una instancia de {@link SessionFactory} configurada
     * con las propiedades Hibernate proporcionadas.
     *
     * @param hibernateProperties Propiedades de configuración de Hibernate.
     * @return Instancia de {@link SessionFactory} configurada.
     * @throws HibernateException Si ocurre un error durante la creación de la SessionFactory.
     */
    public SessionFactory getSessionFactory(Properties hibernateProperties) throws HibernateException {
        log.debug("Iniciando construcción de SessionFactory con las propiedades proporcionadas.");

        var hibernateConfigurer = new HibernateConfigurer();
        var configuration = hibernateConfigurer.buildConfiguration(hibernateProperties);
        log.debug("Configuración Hibernate creada correctamente.");

        var entityScanner = new EntityScanner();
        entityScanner.scanAndAddEntities(configuration, CONFIG_PACKAGE_NAME);
        log.debug("Entidades escaneadas y añadidas desde el paquete '{}'.", CONFIG_PACKAGE_NAME);

        try {
            var sessionFactory = configuration.buildSessionFactory();
            log.info("SessionFactory creada exitosamente.");
            return sessionFactory;
        } catch (HibernateException e) {
            log.error("Error creando SessionFactory: {}", e.getMessage(), e);
            throw e; // Propagar la excepción para que el llamador la maneje
        }
    }
}
