package local.jarios.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import local.jarios.common.util.PropertiesFiles;
import local.jarios.common.util.PropertiesKeys;
import local.jarios.dao.RegistroGcDao;
import local.jarios.entity.Estadistica;
import local.jarios.entity.FicheroGc;
import local.jarios.entity.Log;
import local.jarios.entity.RegistroGc;
import local.jarios.properties.api.PropertiesManagerService;
import local.jarios.properties.exception.PropertiesManagerException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.MariaDBContainer;

class RegistroGcDaoMariaDbIntegrationTest {

  private static final MariaDBContainer<?> MARIA_DB = new MariaDBContainer<>("mariadb:11.4");

  private static SessionFactory sessionFactory;

  @AfterAll
  static void stopDatabase() {
    if (sessionFactory != null) {
      sessionFactory.close();
    }
    if (MARIA_DB.isRunning()) {
      MARIA_DB.stop();
    }
  }

  @Test
  void createsInsertsAndDropsDynamicRegistroTableInMariaDb() {
    assumeTrue(isDockerAvailable(), "Docker no disponible");
    MARIA_DB.start();
    sessionFactory = buildSessionFactory();
    RegistroGcDao dao = new RegistroGcDao(new TestPropertiesManager());

    try (var session = sessionFactory.openSession()) {
      var transaction = session.beginTransaction();

      String tableName = "placsp_contract_code_test";
      dao.crearTabla(session, tableName);
      assertThat(dao.existeTabla(session, tableName)).isTrue();

      dao.insertarRegistros(
          session,
          tableName,
          List.of(
              new RegistroGc("A", "Activo", java.util.UUID.randomUUID()),
              new RegistroGc("B", "Borrador", java.util.UUID.randomUUID())));

      long count =
          ((Number)
                  session.createNativeQuery("SELECT COUNT(*) FROM " + tableName).getSingleResult())
              .longValue();

      assertThat(count).isEqualTo(2);

      dao.eliminarTabla(session, tableName);
      assertThat(dao.existeTabla(session, tableName)).isFalse();

      transaction.commit();
    }
  }

  private static boolean isDockerAvailable() {
    try {
      return DockerClientFactory.instance().isDockerAvailable();
    } catch (RuntimeException exception) {
      return false;
    }
  }

  private static SessionFactory buildSessionFactory() {
    Properties properties = new Properties();
    properties.setProperty("jakarta.persistence.jdbc.url", MARIA_DB.getJdbcUrl());
    properties.setProperty("jakarta.persistence.jdbc.driver", MARIA_DB.getDriverClassName());
    properties.setProperty("jakarta.persistence.jdbc.user", MARIA_DB.getUsername());
    properties.setProperty("jakarta.persistence.jdbc.password", MARIA_DB.getPassword());
    properties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
    properties.setProperty("hibernate.show_sql", "false");
    properties.setProperty("hibernate.format_sql", "false");

    return new Configuration()
        .setProperties(properties)
        .addAnnotatedClass(Log.class)
        .addAnnotatedClass(FicheroGc.class)
        .addAnnotatedClass(Estadistica.class)
        .buildSessionFactory();
  }

  private static final class TestPropertiesManager implements PropertiesManagerService {

    @Override
    public String getProperty(String file, String key) {
      if (PropertiesFiles.APP.equals(file) && PropertiesKeys.APP_CHARACTER_ENCODING.equals(key)) {
        return "utf8mb4";
      }
      if (PropertiesFiles.APP.equals(file) && PropertiesKeys.APP_CONNECTION_COLLATION.equals(key)) {
        return "utf8mb4_unicode_ci";
      }
      throw new IllegalArgumentException("Propiedad no soportada en test: " + file + "." + key);
    }

    @Override
    public Set<String> getSensitiveKeys() {
      return Set.of();
    }

    @Override
    public void setSensitiveKeys(Set<String> sensitiveKeys) {}

    @Override
    public List<String> getListFiles() {
      return List.of();
    }

    @Override
    public void loadAllProperties() {}

    @Override
    public void printProperties(String file) {}

    @Override
    public void printAllProperties() {}

    @Override
    public Properties getProperties(String file) {
      return new Properties();
    }

    @Override
    public void setProperty(String file, String key, String value) {}

    @Override
    public boolean hasLoaded(String file) {
      return true;
    }

    @Override
    public Map<String, Properties> getAllProperties() {
      return Map.of();
    }

    @Override
    public String exportPropertiesToJson(String file, boolean includeSensitive) {
      return "{}";
    }

    @Override
    public String exportAllPropertiesToJson(boolean includeSensitive) {
      return "{}";
    }

    @Override
    public boolean validateRequiredKeys(String file, Set<String> requiredKeys) {
      return true;
    }

    @Override
    public void reload() {}

    @Override
    public String getConfigDir() throws PropertiesManagerException {
      return "";
    }

    @Override
    public void setConfigDir(String configDir) {}

    @Override
    public void addProperties(String file, Properties properties) {}
  }
}
