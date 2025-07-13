package local.jarios.helpers;

import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import java.util.List;
import java.util.Map;

/**
 * Description:
 * Author: juan
 * Date: 03/06/2025
 * Team:
 */
public final class JsonSerializationHelper {

    /**
     * Constructor privado
     */
    private JsonSerializationHelper() { } // Prevent instantiation

    /**
     * Añadir si no es vacío
     * @param jsonObject Donde se añade
     * @param propertyName Propiedad
     * @param list Lista que se comprueba qu eno sea vacía ni null
     * @param context Contexto
     * @param <T> Tipo de Objeto
     */
    public static <T> void addIfNotEmpty(
            JsonObject jsonObject, String propertyName, List<T> list, JsonSerializationContext context) {
        if (list != null && !list.isEmpty()) {
            jsonObject.add(propertyName, context.serialize(list));
        }
    }

    /**
     * Añadir si no es vacío
     * @param jsonObject Donde se añade
     * @param propertyName Propiedad
     * @param map Map que se comprueba qu eno sea vacía ni null
     * @param context Contexto
     * @param <T> Tipo de Objeto
     */
    public static <T> void addIfNotEmptyMap(
            JsonObject jsonObject, String propertyName, Map<T, T> map, JsonSerializationContext context) {
        if (map != null && !map.isEmpty()) {
            jsonObject.add(propertyName, context.serialize(map));
        }
    }

    /**
     * Añadir si no es null
     * @param jsonObject Donde se añade
     * @param propertyName Propiedad
     * @param obj Objeto que se añade en caso de no ser NULL
     * @param context Contexto
     * @param <T> Tipo de Objeto
     */
    public static <T> void addIfNotNull(
            JsonObject jsonObject, String propertyName, T obj, JsonSerializationContext context) {
        if (obj != null) {
            jsonObject.add(propertyName, context.serialize(obj));
        }
    }

    /**
     * Añadir property
     * @param jsonObject Donde
     * @param propertyName Propiedad
     * @param value Valor
     */
    public static void addProperty(JsonObject jsonObject, String propertyName, Object value) {
        if (value == null) return;

        if (value instanceof String strValue) {
            if (!strValue.isBlank()) {
                jsonObject.addProperty(propertyName, strValue);
            }
        } else {
            jsonObject.addProperty(propertyName, String.valueOf(value));
        }
    }
}
