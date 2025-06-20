package local.jarios.adapters;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import local.jarios.entity.Estadistica;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;

/**
 * Adaptador para serializar objetos {@link Estadistica} a JSON usando Gson.
 * <p>
 * Implementa {@link JsonSerializer} para personalizar la serialización JSON de la entidad {@code Estadistica}.
 * </p>
 * <p>
 * Author: Juan Antonio<br>
 * Date: 14/06/2025<br>
 * Team: Contratacion Electrónica
 * </p>
 */
@Slf4j
public record EstadisticaEntityAdapter() implements JsonSerializer<Estadistica> {

    /**
     * Serializa una instancia de {@link Estadistica} a un objeto JSON personalizado.
     *
     * @param estadistica Objeto a serializar.
     * @param typeOfSrc   Tipo del objeto de entrada.
     * @param context     Contexto de serialización proporcionado por Gson.
     * @return {@link JsonElement} que representa el objeto JSON resultante.
     */
    @Override
    public JsonElement serialize(Estadistica estadistica, Type typeOfSrc, JsonSerializationContext context) {
        log.debug("Serializando Estadistica con ID: {}", estadistica.getId());

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("id", estadistica.getId().toString());
        jsonObject.addProperty("nTotalFicherosLeidos", estadistica.getNTotalFicherosLeidos());
        jsonObject.addProperty("nRegistrosGc", estadistica.getNRegistrosGc());
        jsonObject.addProperty("duracionParseo", estadistica.getDuracionParseo());
        jsonObject.addProperty("duracionPersistenciaEnBaseDatos", estadistica.getDuracionBaseDatos());

        log.debug("Serialización completada para Estadistica con ID: {}", estadistica.getId());

        return jsonObject;
    }
}
