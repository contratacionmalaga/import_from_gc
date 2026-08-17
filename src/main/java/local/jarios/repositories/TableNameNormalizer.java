package local.jarios.repositories;

import java.util.Locale;

/** Normaliza valores externos antes de usarlos como identificadores de tabla. */
final class TableNameNormalizer {

  /** Constructor privado para evitar instanciacion. */
  private TableNameNormalizer() {
    // Utility class.
  }

  /**
   * Convierte un valor de configuracion o fichero GC en un identificador SQL acotado.
   *
   * @param valor valor original
   * @return identificador en minusculas con caracteres no seguros sustituidos por guion bajo
   */
  static String normalize(String valor) {
    if (valor == null || valor.isBlank()) {
      throw new IllegalArgumentException("El identificador de tabla no puede estar vacio.");
    }

    return valor.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_]", "_");
  }
}
