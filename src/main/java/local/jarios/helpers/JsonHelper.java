package local.jarios.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import local.jarios.common.util.Constantes;
import lombok.extern.slf4j.Slf4j;

/** Utilidad para conversion de objetos Java a JSON con Jackson. */
@Slf4j
public final class JsonHelper {

  /** Instancia singleton de {@link ObjectMapper}. */
  private static final ObjectMapper mapper = new ObjectMapper();

  static {
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  /** Constructor privado de clase utilitaria. */
  private JsonHelper() {}

  /**
   * Serializa una entidad a JSON.
   *
   * @param <T> tipo de la entidad
   * @param entidad entidad a serializar
   * @return JSON resultante o JSON vacio si la entidad es nula o falla la serializacion
   */
  public static <T> String serializeEntitytoJson(T entidad) {
    if (entidad == null) {
      return Constantes.JSON_VACIO;
    }

    try {
      return mapper.writeValueAsString(entidad);
    } catch (JsonProcessingException ex) {
      log.error("Error serializando el mapa a JSON", ex);
      return Constantes.JSON_VACIO;
    }
  }

  /**
   * Serializa un objeto a JSON con formato legible.
   *
   * @param obj objeto a serializar
   * @return JSON con formato legible
   */
  public static String toJsonPretty(Object obj) {
    try {
      return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    } catch (Exception ex) {
      throw new RuntimeException("Error serializando a JSON", ex);
    }
  }
}
