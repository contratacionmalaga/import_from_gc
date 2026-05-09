package local.jarios.repositories;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TableNameNormalizerTest {

    @Test
    void normalizeKeepsSafeLowercaseIdentifiers() {
        assertThat(TableNameNormalizer.normalize("PLACSP_tabla_01"))
                .isEqualTo("placsp_tabla_01");
    }

    @Test
    void normalizeReplacesUnsafeCharacters() {
        assertThat(TableNameNormalizer.normalize("CPV 2024/Contratos.csv"))
                .isEqualTo("cpv_2024_contratos_csv");
    }

    @Test
    void normalizeRejectsBlankValues() {
        assertThatThrownBy(() -> TableNameNormalizer.normalize(" "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no puede estar vacio");
    }
}
