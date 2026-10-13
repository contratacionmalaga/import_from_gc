package local.jarios;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import local.jarios.common.util.PropertiesFiles;
import local.jarios.common.util.PropertiesKeys;
import local.jarios.email.exception.EmailException;
import local.jarios.entity.Estadistica;
import local.jarios.entity.Log;
import local.jarios.entity.ParseoFicherosGc;
import local.jarios.exceptions.MiServiceException;
import local.jarios.exceptions.MiUnknownHostException;
import local.jarios.properties.api.PropertiesManagerService;
import local.jarios.properties.exception.PropertiesManagerException;
import local.jarios.services.Service;
import local.jarios.version.api.Version;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ImportFromGcRunnerTest {

  @Test
  void returnsZeroAndRunsMainFlowWithoutTerminatingJvm() {
    TestRuntime runtime = new TestRuntime();
    runtime.propertiesManager.emailEnabled = "true";

    int exitCode = ImportFromGc.run(runtime);

    assertThat(exitCode).isZero();
    assertThat(runtime.listedPath).isEqualTo("data/gc");
    assertThat(runtime.parseCalls).isEqualTo(1);
    assertThat(runtime.service.persistCalls).isEqualTo(1);
    assertThat(runtime.successEmails).isEqualTo(1);
    assertThat(runtime.errorEmails).isZero();
  }

  @Test
  void returnsOneAndSendsFailureEmailWhenPersistenceFails() {
    TestRuntime runtime = new TestRuntime();
    runtime.propertiesManager.emailEnabled = "true";
    runtime.service.failOnPersist = true;

    int exitCode = ImportFromGc.run(runtime);

    assertThat(exitCode).isEqualTo(1);
    assertThat(runtime.service.persistCalls).isEqualTo(1);
    assertThat(runtime.successEmails).isZero();
    assertThat(runtime.errorEmails).isEqualTo(1);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"false", " FALSE ", " ", "unexpected"})
  void completesImportWithoutEmailWhenDisabledOrNotConfigured(String value) {
    TestRuntime runtime = new TestRuntime();
    runtime.propertiesManager.emailEnabled = value;
    runtime.failOnSend = true;

    int exitCode = ImportFromGc.run(runtime);

    assertThat(exitCode).isZero();
    assertThat(runtime.parseCalls).isEqualTo(1);
    assertThat(runtime.service.persistCalls).isEqualTo(1);
    assertThat(runtime.successEmails).isZero();
    assertThat(runtime.errorEmails).isZero();
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"false", " FALSE ", " ", "unexpected"})
  void preservesImportFailureWithoutSendingEmailWhenDisabledOrNotConfigured(String value) {
    TestRuntime runtime = new TestRuntime();
    runtime.propertiesManager.emailEnabled = value;
    runtime.service.failOnPersist = true;
    runtime.failOnSend = true;

    int exitCode = ImportFromGc.run(runtime);

    assertThat(exitCode).isEqualTo(1);
    assertThat(runtime.service.persistCalls).isEqualTo(1);
    assertThat(runtime.successEmails).isZero();
    assertThat(runtime.errorEmails).isZero();
  }

  @Test
  void enablesEmailWithTrimmedCaseInsensitiveTrue() {
    TestRuntime runtime = new TestRuntime();
    runtime.propertiesManager.emailEnabled = " TRUE ";

    assertThat(ImportFromGc.run(runtime)).isZero();
    assertThat(runtime.successEmails).isEqualTo(1);
  }

  @Test
  void preservesEmailFailureWhenExplicitlyEnabled() {
    TestRuntime runtime = new TestRuntime();
    runtime.propertiesManager.emailEnabled = "true";
    runtime.failOnSend = true;

    assertThat(ImportFromGc.run(runtime)).isEqualTo(1);
    assertThat(runtime.service.persistCalls).isEqualTo(1);
    assertThat(runtime.successEmails).isEqualTo(1);
    assertThat(runtime.errorEmails).isEqualTo(1);
  }

  private static final class TestRuntime implements ImportFromGc.RuntimeGateway {

    private final TestPropertiesManager propertiesManager = new TestPropertiesManager();
    private final TestService service = new TestService();
    private String listedPath;
    private int parseCalls;
    private int successEmails;
    private int errorEmails;
    private boolean failOnSend;

    @Override
    public PropertiesManagerService getPropertiesManager() {
      return propertiesManager;
    }

    @Override
    public Version createVersionService() {
      return applicationClass -> "test-version";
    }

    @Override
    public Service createService() {
      return service;
    }

    @Override
    public File[] listFiles(String path) {
      listedPath = path;
      return new File[] {new File("catalog.gc")};
    }

    @Override
    public ParseoFicherosGc parseFiles(Log miLog, File[] files, Estadistica estadistica) {
      parseCalls++;
      return new ParseoFicherosGc();
    }

    @Override
    public void sendEmail(Estadistica estadistica, Exception ex, boolean success)
        throws MiUnknownHostException, PropertiesManagerException, EmailException {
      if (success) {
        successEmails++;
      } else {
        errorEmails++;
      }
      if (failOnSend) {
        throw new EmailException("SMTP no disponible");
      }
    }
  }

  private static final class TestService implements Service {

    private boolean failOnPersist;
    private int persistCalls;

    @Override
    public void persistirEnBaseDeDatos(Log miLog, ParseoFicherosGc parseo) {
      persistCalls++;
      if (failOnPersist) {
        throw new MiServiceException("fallo simulado");
      }
    }
  }

  private static final class TestPropertiesManager implements PropertiesManagerService {

    private String emailEnabled;

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
    public String getProperty(String file, String key) throws PropertiesManagerException {
      if (PropertiesFiles.APP.equals(file) && PropertiesKeys.APP_NAME.equals(key)) {
        return "import-from-gc-test";
      }
      if (PropertiesFiles.APP.equals(file) && PropertiesKeys.APP_PATH.equals(key)) {
        return "data/gc";
      }
      if (PropertiesFiles.APP.equals(file) && PropertiesKeys.APP_EMAIL_ENABLED.equals(key)) {
        if (emailEnabled == null) {
          throw new PropertiesManagerException("Clave inexistente app.email.enabled");
        }
        return emailEnabled;
      }
      throw new IllegalArgumentException("Propiedad no soportada en test: " + file + "." + key);
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
