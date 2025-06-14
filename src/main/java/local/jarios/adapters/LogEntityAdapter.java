package local.jarios.adapters;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import local.jarios.entity.Log;

import java.lang.reflect.Type;

public record LogEntityAdapter() implements JsonSerializer<Log> {

    @Override
    public JsonElement serialize(Log logEntity, Type typeOfSrc, JsonSerializationContext context) {

        JsonObject jsonObject = new JsonObject();
        JsonObject logContent = new JsonObject();

        // Usar id en minúsculas (opcional)
        logContent.addProperty("id", logEntity.getId().toString());

        if (logEntity.getEstadistica() != null) {
            logContent.add("estadistica", context.serialize(logEntity.getEstadistica()));
        }

        if (logEntity.getFicherosGc() != null && !logEntity.getFicherosGc().isEmpty()) {
            logContent.add("ficherosGc", context.serialize(logEntity.getFicherosGc()));
        }

        jsonObject.add("log", logContent);

        return jsonObject;
    }
}
