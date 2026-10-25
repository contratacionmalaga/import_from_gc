package local.jarios.logging;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.util.LogbackMDCAdapter;
import ch.qos.logback.core.status.Status;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.LoggerFactory;

class LogbackConfigurationTest {

  @TempDir Path logDirectory;

  @Test
  void runtimeIncludesLogbackAndRoutesLog4jThroughSlf4j() {
    assertThat(LoggerFactory.getILoggerFactory()).isInstanceOf(LoggerContext.class);
    assertThat(org.apache.logging.log4j.LogManager.getContext().getClass().getName())
        .isEqualTo("org.apache.logging.slf4j.SLF4JLoggerContext");
  }

  @Test
  void separatesProgressErrorsAndHibernateWithoutDuplicatingConsoleMessages() throws Exception {
    ByteArrayOutputStream console = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    LoggerContext context = new LoggerContext();
    context.setMDCAdapter(new LogbackMDCAdapter());
    context.putProperty("gc.log.path", logDirectory.toString());
    context.putProperty("gc.execution.id", "test-execution");

    try (PrintStream capture = new PrintStream(console, true, StandardCharsets.UTF_8)) {
      System.setOut(capture);
      JoranConfigurator configurator = new JoranConfigurator();
      configurator.setContext(context);
      configurator.doConfigure(getClass().getResource("/logback.xml"));
      assertThat(context.getStatusManager().getCopyOfStatusList())
          .noneMatch(status -> status.getLevel() == Status.ERROR);

      context.getLogger("local.jarios.ImportFromGc").info("avance-importacion");
      context
          .getLogger("local.jarios.repositories.RepositoryImpl")
          .error("detalle-repositorio", new IllegalStateException("causa-tecnica"));
      context
          .getLogger("local.jarios.ImportFromGc")
          .error("fallo-importacion", new IllegalStateException("causa-tecnica"));
      context.getLogger("org.hibernate.SQL").warn("detalle-hibernate");
    } finally {
      context.stop();
      System.setOut(originalOut);
    }

    String output = console.toString(StandardCharsets.UTF_8);
    assertThat(output)
        .containsOnlyOnce("avance-importacion")
        .containsOnlyOnce("fallo-importacion")
        .doesNotContain("detalle-repositorio", "detalle-hibernate", "causa-tecnica");
    assertThat(Files.readString(logDirectory.resolve("import-from-gc_test-execution.log")))
        .contains("avance-importacion")
        .doesNotContain("fallo-importacion", "detalle-repositorio", "detalle-hibernate");
    assertThat(Files.readString(logDirectory.resolve("import-from-gc_error_test-execution.log")))
        .contains(
            "fallo-importacion", "detalle-repositorio", "IllegalStateException: causa-tecnica")
        .doesNotContain("avance-importacion", "detalle-hibernate");
    assertThat(
            Files.readString(logDirectory.resolve("import-from-gc_hibernate_test-execution.log")))
        .contains("detalle-hibernate")
        .doesNotContain("avance-importacion", "fallo-importacion");
  }
}
