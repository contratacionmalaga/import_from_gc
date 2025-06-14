package local.jarios.adapters;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import local.jarios.entity.Estadistica;

import java.lang.reflect.Type;

public record EstadisticaEntityAdapter() implements JsonSerializer<Estadistica> {

    @Override
    public JsonElement serialize(Estadistica estadistica, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("id", estadistica.getId().toString());
        jsonObject.addProperty("nTotalFicherosLeidos", estadistica.getNTotalFicherosLeidos());
        jsonObject.addProperty("nTotalFicherosProcesados", estadistica.getNTotalFicherosProcesados());
        jsonObject.addProperty("nRregistrosGc", estadistica.getNRegistrosGc());
        jsonObject.addProperty("duracionParseo", estadistica.getDuracionParseo());
        jsonObject.addProperty("duracionPersistenciaEnBaseDatos", estadistica.getDuracionBaseDatos());

        return jsonObject;
    }
}
