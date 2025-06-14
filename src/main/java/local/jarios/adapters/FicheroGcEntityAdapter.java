package local.jarios.adapters;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import local.jarios.entity.FicheroGc;

import java.lang.reflect.Type;

public record FicheroGcEntityAdapter() implements JsonSerializer<FicheroGc> {

    @Override
    public JsonElement serialize(FicheroGc ficheroGcEntity, Type typeOfSrc, JsonSerializationContext context) {
        var jsonObject = new JsonObject();

        jsonObject.addProperty("id", ficheroGcEntity.getId().toString());
        jsonObject.addProperty("shortName", ficheroGcEntity.getShortName());
        jsonObject.addProperty("longName", ficheroGcEntity.getLongName());
        jsonObject.addProperty("version", ficheroGcEntity.getVersion());
        jsonObject.addProperty("canonicalUri", ficheroGcEntity.getCanonicalUri());
        jsonObject.addProperty("canonicalVersionUri", ficheroGcEntity.getCanonicalVersionUri());
        jsonObject.addProperty("locationUri", ficheroGcEntity.getLocationUri());

        return jsonObject;
    }
}
