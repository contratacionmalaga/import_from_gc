package local.jarios.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DestructiveImportPolicyTest {

  @Test
  void onlyTrueEnablesDestructiveImport() {
    assertThat(DestructiveImportPolicy.parseBoolean("true")).isTrue();
    assertThat(DestructiveImportPolicy.parseBoolean(" TRUE ")).isTrue();
    assertThat(DestructiveImportPolicy.parseBoolean("false")).isFalse();
    assertThat(DestructiveImportPolicy.parseBoolean("yes")).isFalse();
    assertThat(DestructiveImportPolicy.parseBoolean(null)).isFalse();
  }

  @Test
  void environmentValueHasPriorityOverPropertyValue() {
    assertThat(DestructiveImportPolicy.isAllowed("false", "true")).isFalse();
    assertThat(DestructiveImportPolicy.isAllowed("true", "false")).isTrue();
  }

  @Test
  void propertyValueIsUsedWhenEnvironmentIsBlank() {
    assertThat(DestructiveImportPolicy.isAllowed("", "true")).isTrue();
    assertThat(DestructiveImportPolicy.isAllowed(null, "false")).isFalse();
  }
}
