package local.jarios.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DestructiveImportContextTest {

  @Test
  void redactJdbcUrlRemovesCredentialsAndQueryParameters() {
    String redacted =
        DestructiveImportContext.redactJdbcUrl(
            "jdbc:mariadb://user:secret@db.example.test:3306/import_gc?password=secret&ssl=true");

    assertThat(redacted)
        .isEqualTo("jdbc:mariadb://<credentials>@db.example.test:3306/import_gc?<redacted>")
        .doesNotContain("user")
        .doesNotContain("secret")
        .doesNotContain("password")
        .doesNotContain("ssl=true");
  }

  @Test
  void redactJdbcUrlKeepsSafeTargetWithoutQuery() {
    assertThat(DestructiveImportContext.redactJdbcUrl("jdbc:mariadb://localhost:3306/import_gc"))
        .isEqualTo("jdbc:mariadb://localhost:3306/import_gc");
  }

  @Test
  void sanitizeUsesUnavailableForBlankValues() {
    assertThat(DestructiveImportContext.sanitize(null)).isEqualTo("<no disponible>");
    assertThat(DestructiveImportContext.sanitize("  ")).isEqualTo("<no disponible>");
  }
}
