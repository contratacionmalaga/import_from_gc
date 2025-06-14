package local.jarios.adapters;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import local.jarios.entity.Log;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;

/**
 * Adaptador para serializar objetos {@link Log} a JSON usando Gson.
 * <p>
 * Implementa {@link JsonSerializer} para personalizar la serialización JSON de la entidad {@code Log}.
 * </p>
 * <p>
 * Author: Juan Antonio<br>
 * Date: 14/06/2025<br>
 * Team: Contratacion Electrónica
 * </p>
 */
@Slf4j
public record LogEntityAdapter() implements JsonSerializer<Log> {

    /**
     * Serializa una instancia de {@link Log} a un objeto JSON personalizado.
     *
     * @param logEntity Objeto a serializar.
     * @param typeOfSrc Tipo del objeto de entrada.
     * @param context   Contexto de serialización proporcionado por Gson.
     * @return {@link JsonElement} que representa el objeto JSON resultante.
     */
    @Override
    public JsonElement serialize(Log logEntity, Type typeOfSrc, JsonSerializationContext context) {
        log.debug("Serializando Log con ID: {}", logEntity.getId());

        JsonObject jsonObject = new JsonObject();
        JsonObject logContent = new JsonObject();

        logContent.addProperty("id", logEntity.getId().toString());

        if (logEntity.getEstadistica() != null) {
            logContent.add("estadistica", context.serialize(logEntity.getEstadistica()));
        }

        if (logEntity.getFicherosGc() != null && !logEntity.getFicherosGc().isEmpty()) {
            logContent.add("ficherosGc", context.serialize(logEntity.getFicherosGc()));
        }

        jsonObject.add("log", logContent);

        log.debug("Serialización completada para Log con ID: {}", logEntity.getId());
        return jsonObject;
    }
}
