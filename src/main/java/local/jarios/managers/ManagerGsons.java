package local.jarios.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import local.jarios.adapters.EstadisticaEntityAdapter;
import local.jarios.adapters.FicheroGcEntityAdapter;
import local.jarios.adapters.LogEntityAdapter;
import local.jarios.entity.Estadistica;
import local.jarios.entity.FicheroGc;
import local.jarios.entity.Log;

/**
 * Clase utilitaria para convertir objetos Java a JSON utilizando Gson.
 * <p>
 * Esta clase aplica adaptadores personalizados a entidades específicas
 * para controlar la serialización de sus propiedades.
 * </p>
 * <p>
 * Se aplica formato pretty-print y se deshabilita el escape automático de HTML.
 * </p>
 *
 * @author Juan
 * @since 2025-03-01
 */
public final class ManagerGsons {

    /**
     * Constructor privado para evitar la instanciación de esta clase utilitaria.
     */
    private ManagerGsons() {
        // Constucotr vacío
    }

    /**
     * Convierte un objeto a su representación JSON formateada.
     *
     * @param objeto Objeto a convertir.
     * @return Cadena JSON representando el objeto, con formato legible.
     */
    public static String objectToJsonPretty(Object objeto) {
        return getJson(objeto);
    }

    /**
     * Configura y genera el JSON para el objeto especificado.
     * Se aplican adaptadores personalizados y se desactiva el escape de HTML.
     *
     * @param objeto Objeto a serializar.
     * @return JSON del objeto.
     */
    private static String getJson(Object objeto) {
        GsonBuilder gsonBuilder = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()

                // Registro de adaptadores personalizados
                .registerTypeAdapter(Estadistica.class, new EstadisticaEntityAdapter())
                .registerTypeAdapter(Log.class, new LogEntityAdapter())
                .registerTypeAdapter(FicheroGc.class, new FicheroGcEntityAdapter());

        Gson gson = gsonBuilder.create();

        return gson.toJson(objeto);
    }
}
