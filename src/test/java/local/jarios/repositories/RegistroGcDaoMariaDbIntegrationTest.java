package local.jarios.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import local.jarios.common.util.PropertiesFiles;
import local.jarios.common.util.PropertiesKeys;
import local.jarios.dao.RegistroGcDao;
import local.jarios.entity.Estadistica;
import local.jarios.entity.FicheroGc;
import local.jarios.entity.Log;
import local.jarios.entity.RegistroGc;
import local.jarios.properties.api.PropertiesManagerService;
import local.jarios.properties.exception.PropertiesManagerException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.MariaDBContainer;

class RegistroGcDaoMariaDbIntegrationTest {

  private static final MariaDBContainer<?> MARIA_DB = new MariaDBContainer<>("mariadb:11.4");

  private static SessionFactory sessionFactory;

  @BeforeAll
  static void startDatabase() {
    boolean dockerAvailable = isDockerAvailable();
    if (Boolean.getBoolean("importFromGc.requireDocker")) {
      assertThat(dockerAvailable).as("Docker obligatorio para verificar UUID en MariaDB").isTrue();
    }
    assumeTrue(dockerAvailable, "Docker no disponible");
    MARIA_DB.start();
    sessionFactory = buildSessionFactory();
  }

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

      List<String> ids =
          session
              .createNativeQuery("SELECT CAST(id AS CHAR) FROM " + tableName, String.class)
              .getResultList();
      assertThat(ids).hasSize(2).doesNotHaveDuplicates();
      ids.forEach(id -> assertThat(UUID.fromString(id).version()).isEqualTo(7));
      assertNativeUuidColumn(session, tableName, "id");

      dao.eliminarTabla(session, tableName);
      assertThat(dao.existeTabla(session, tableName)).isFalse();

      transaction.commit();
    }
  }

  @Test
  void generatesVersionSevenIdsAndPreservesThemOnMerge() {
    Log source = new Log();
    Estadistica estadistica = new Estadistica();
    estadistica.setCreatedAt(source.getCreatedAt());
    estadistica.setEquipo("uuid-v7-test");
    estadistica.setLogEntity(source);
    source.setEstadistica(estadistica);
    FicheroGc fichero = new FicheroGc();
    fichero.setLogEntity(source);
    fichero.setShortName("uuid-v7-test");
    fichero.setLongName("Catalogo de prueba UUID v7");
    fichero.setVersion("1.0");
    fichero.setCanonicalUri("urn:test:uuid-v7");
    fichero.setCanonicalVersionUri("urn:test:uuid-v7:1.0");
    fichero.setLocationUri("urn:test:uuid-v7:location");
    source.getFicherosGc().add(fichero);

    Log detached;
    try (var session = sessionFactory.openSession()) {
      var transaction = session.beginTransaction();
      // El repositorio real persiste el grafo mediante merge.
      detached = session.merge(source);
      session.flush();
      assertThat(detached.getId().version()).isEqualTo(7);
      assertThat(detached.getEstadistica().getId().version()).isEqualTo(7);
      assertThat(detached.getFicherosGc().getFirst().getId().version()).isEqualTo(7);
      assertThat(
              List.of(
                  detached.getId(),
                  detached.getEstadistica().getId(),
                  detached.getFicherosGc().getFirst().getId()))
          .doesNotHaveDuplicates();
      assertStoredGraph(session, detached);
      assertNativeUuidColumn(session, "log", "id");
      assertNativeUuidColumn(session, "estadistica", "id");
      assertNativeUuidColumn(session, "estadistica", "log_id");
      assertNativeUuidColumn(session, "ficheros_gc", "id");
      assertNativeUuidColumn(session, "ficheros_gc", "log_id");
      transaction.commit();
    }

    UUID logId = detached.getId();
    UUID estadisticaId = detached.getEstadistica().getId();
    UUID ficheroId = detached.getFicherosGc().getFirst().getId();
    detached.getEstadistica().setEquipo("updated-v7");
    detached.getFicherosGc().getFirst().setLongName("updated-v7");
    assertMergePreservesIds(detached, logId, estadisticaId, ficheroId, "updated-v7");
  }

  @Test
  void preservesExistingVersionFourIdsAndReferencesOnMerge() {
    UUID logId = UUID.randomUUID();
    UUID estadisticaId = UUID.randomUUID();
    UUID ficheroId = UUID.randomUUID();
    try (var session = sessionFactory.openSession()) {
      var transaction = session.beginTransaction();
      // Simula registros anteriores a la actualizacion sin invocar los nuevos generadores.
      session
          .createNativeMutationQuery(
              "INSERT INTO log (id, created_at) VALUES (:id, CURRENT_TIMESTAMP)")
          .setParameter("id", logId.toString())
          .executeUpdate();
      session
          .createNativeMutationQuery(
              "INSERT INTO estadistica "
                  + "(id, log_id, equipo, nTotalFicherosLeidos, nRegistrosGc, created_at) "
                  + "VALUES (:id, :logId, 'legacy-v4', 0, 0, CURRENT_TIMESTAMP)")
          .setParameter("id", estadisticaId.toString())
          .setParameter("logId", logId.toString())
          .executeUpdate();
      session
          .createNativeMutationQuery(
              "INSERT INTO ficheros_gc (id, log_id, short_name, long_name, version, "
                  + "canonical_uri, canonical_version_uri, location_uri, created_at) "
                  + "VALUES (:id, :logId, 'legacy', 'legacy-v4', '1.0', "
                  + "'urn:test:legacy', 'urn:test:legacy:1.0', 'urn:test:legacy', CURRENT_TIMESTAMP)")
          .setParameter("id", ficheroId.toString())
          .setParameter("logId", logId.toString())
          .executeUpdate();
      transaction.commit();
    }

    Log detached;
    try (var session = sessionFactory.openSession()) {
      detached = session.find(Log.class, logId);
      detached.getEstadistica().setEquipo("updated-v4");
      detached.getFicherosGc().getFirst().setLongName("updated-v4");
    }
    assertMergePreservesIds(detached, logId, estadisticaId, ficheroId, "updated-v4");
  }

  private static void assertMergePreservesIds(
      Log detached, UUID logId, UUID estadisticaId, UUID ficheroId, String updatedValue) {
    try (var session = sessionFactory.openSession()) {
      var transaction = session.beginTransaction();
      Log merged = session.merge(detached);
      assertThat(merged.getId()).isEqualTo(logId);
      assertThat(merged.getEstadistica().getId()).isEqualTo(estadisticaId);
      assertThat(merged.getFicherosGc().getFirst().getId()).isEqualTo(ficheroId);
      transaction.commit();
    }
    // Lee de nuevo desde MariaDB, sin reutilizar el estado en memoria.
    try (var session = sessionFactory.openSession()) {
      Log reloaded = session.find(Log.class, logId);
      assertThat(reloaded.getEstadistica().getId()).isEqualTo(estadisticaId);
      assertThat(reloaded.getFicherosGc().getFirst().getId()).isEqualTo(ficheroId);
      assertThat(reloaded.getEstadistica().getEquipo()).isEqualTo(updatedValue);
      assertThat(reloaded.getFicherosGc().getFirst().getLongName()).isEqualTo(updatedValue);
      assertStoredGraph(session, reloaded);
    }
  }

  private static void assertStoredGraph(Session session, Log stored) {
    assertThat(
            session
                .createNativeQuery("SELECT CAST(id AS CHAR) FROM log WHERE id = :id", String.class)
                .setParameter("id", stored.getId().toString())
                .getSingleResult())
        .isEqualTo(stored.getId().toString());
    assertStoredReference(session, "estadistica", stored.getEstadistica().getId(), stored.getId());
    assertStoredReference(
        session, "ficheros_gc", stored.getFicherosGc().getFirst().getId(), stored.getId());
  }

  private static void assertStoredReference(
      Session session, String tableName, UUID id, UUID logId) {
    assertThat(
            session
                .createNativeQuery(
                    "SELECT CAST(log_id AS CHAR) FROM " + tableName + " WHERE id = :id",
                    String.class)
                .setParameter("id", id.toString())
                .getSingleResult())
        .isEqualTo(logId.toString());
  }

  private static void assertNativeUuidColumn(Session session, String tableName, String columnName) {
    assertThat(
            session
                .createNativeQuery(
                    "SELECT DATA_TYPE FROM information_schema.columns "
                        + "WHERE table_schema = DATABASE() "
                        + "AND table_name = :tableName AND column_name = :columnName",
                    String.class)
                .setParameter("tableName", tableName)
                .setParameter("columnName", columnName)
                .getSingleResult())
        .isEqualToIgnoringCase("uuid");
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
