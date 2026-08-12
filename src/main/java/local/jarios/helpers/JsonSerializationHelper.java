package local.jarios.helpers;

import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.Map;

/** Utilidades para serializacion condicional con Gson. */
public final class JsonSerializationHelper {

  /** Constructor privado de clase utilitaria. */
  private JsonSerializationHelper() {}

  /**
   * Anade una lista al JSON si no es nula ni vacia.
   *
   * @param jsonObject objeto JSON destino
   * @param propertyName nombre de la propiedad
   * @param list lista a serializar
   * @param context contexto de serializacion Gson
   * @param <T> tipo de los elementos
   */
  public static <T> void addIfNotEmpty(
      JsonObject jsonObject, String propertyName, List<T> list, JsonSerializationContext context) {
    if (list != null && !list.isEmpty()) {
      jsonObject.add(propertyName, context.serialize(list));
    }
  }

  /**
   * Anade un mapa al JSON si no es nulo ni vacio.
   *
   * @param jsonObject objeto JSON destino
   * @param propertyName nombre de la propiedad
   * @param map mapa a serializar
   * @param context contexto de serializacion Gson
   * @param <T> tipo de claves y valores
   */
  public static <T> void addIfNotEmptyMap(
      JsonObject jsonObject, String propertyName, Map<T, T> map, JsonSerializationContext context) {
    if (map != null && !map.isEmpty()) {
      jsonObject.add(propertyName, context.serialize(map));
    }
  }

  /**
   * Anade un objeto al JSON si no es nulo.
   *
   * @param jsonObject objeto JSON destino
   * @param propertyName nombre de la propiedad
   * @param obj objeto a serializar
   * @param context contexto de serializacion Gson
   * @param <T> tipo del objeto
   */
  public static <T> void addIfNotNull(
      JsonObject jsonObject, String propertyName, T obj, JsonSerializationContext context) {
    if (obj != null) {
      jsonObject.add(propertyName, context.serialize(obj));
    }
  }

  /**
   * Anade una propiedad simple al JSON si tiene valor.
   *
   * @param jsonObject objeto JSON destino
   * @param propertyName nombre de la propiedad
   * @param value valor a anadir
   */
  public static void addProperty(JsonObject jsonObject, String propertyName, Object value) {
    if (value == null) {
      return;
    }

    if (value instanceof String strValue) {
      if (!strValue.isBlank()) {
        jsonObject.addProperty(propertyName, strValue);
      }
    } else {
      jsonObject.addProperty(propertyName, String.valueOf(value));
    }
  }
}
