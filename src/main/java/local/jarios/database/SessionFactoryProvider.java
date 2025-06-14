package local.jarios.database;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;

import java.util.Properties;

@Slf4j
public class SessionFactoryProvider {

    private static final String CONFIG_PACKAGE_NAME = "local.jarios.entity";

    public SessionFactoryProvider() { /* CONSTRUCTOR VACÍO */ }

    public SessionFactory getSessionFactory(
            Properties hibernateProperties
    ) throws HibernateException {

        var hibernateConfigurer = new HibernateConfigurer();
        var configuration = hibernateConfigurer.buildConfiguration(hibernateProperties);

        var entityScanner = new EntityScanner();
        entityScanner.scanAndAddEntities(configuration, CONFIG_PACKAGE_NAME);

        try {
            return configuration.buildSessionFactory();
        } catch (HibernateException e) {
            log.error("Error creando SessionFactory: {}", e.getMessage(), e);
            throw e;  // Rethrow para que quien llame lo gestione
        }
    }
}
