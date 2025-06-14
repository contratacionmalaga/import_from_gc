package local.jarios.adapters;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import local.jarios.entity.FicheroGc;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;

/**
 * Adaptador para serializar objetos {@link FicheroGc} a JSON usando Gson.
 * <p>
 * Implementa {@link JsonSerializer} para personalizar la serialización JSON de la entidad {@code FicheroGc}.
 * </p>
 * <p>
 * Author: Juan Antonio<br>
 * Date: 14/06/2025<br>
 * Team: Contratacion Electrónica
 * </p>
 */
@Slf4j
public record FicheroGcEntityAdapter() implements JsonSerializer<FicheroGc> {

    /**
     * Serializa una instancia de {@link FicheroGc} a un objeto JSON personalizado.
     *
     * @param ficheroGcEntity Objeto a serializar.
     * @param typeOfSrc       Tipo del objeto de entrada.
     * @param context         Contexto de serialización proporcionado por Gson.
     * @return {@link JsonElement} que representa el objeto JSON resultante.
     */
    @Override
    public JsonElement serialize(FicheroGc ficheroGcEntity, Type typeOfSrc, JsonSerializationContext context) {
        log.debug("Serializando FicheroGc con ID: {}", ficheroGcEntity.getId());

        var jsonObject = new JsonObject();

        jsonObject.addProperty("id", ficheroGcEntity.getId().toString());
        jsonObject.addProperty("shortName", ficheroGcEntity.getShortName());
        jsonObject.addProperty("longName", ficheroGcEntity.getLongName());
        jsonObject.addProperty("version", ficheroGcEntity.getVersion());
        jsonObject.addProperty("canonicalUri", ficheroGcEntity.getCanonicalUri());
        jsonObject.addProperty("canonicalVersionUri", ficheroGcEntity.getCanonicalVersionUri());
        jsonObject.addProperty("locationUri", ficheroGcEntity.getLocationUri());

        log.debug("Serialización completada para FicheroGc con ID: {}", ficheroGcEntity.getId());

        return jsonObject;
    }
}
