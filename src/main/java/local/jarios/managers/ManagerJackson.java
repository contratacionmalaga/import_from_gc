package local.jarios.managers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/** Utilidad para serializar objetos Java a JSON con Jackson. */
public final class ManagerJackson {

  /** Instancia de {@link ObjectMapper} configurada de forma global. */
  private static final ObjectMapper objectMapper;

  static {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
  }

  /** Constructor privado de clase utilitaria. */
  private ManagerJackson() {}

  /**
   * Convierte un objeto Java a una cadena JSON con formato legible.
   *
   * @param objeto objeto Java a serializar
   * @return cadena JSON con formato legible
   */
  public static String objectToJsonPretty(Object objeto) {
    try {
      return objectMapper.writeValueAsString(objeto);
    } catch (JsonProcessingException ex) {
      throw new RuntimeException("Error al serializar objeto a JSON", ex);
    }
  }
}
