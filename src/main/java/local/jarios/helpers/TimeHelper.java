package local.jarios.helpers;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Clase utilitaria para operaciones relacionadas con tiempo y fechas.
 * <p>
 * Proporciona métodos para obtener la fecha y hora actual en zonas horarias específicas.
 * </p>
 *
 * Author: juan
 * Date: 21/06/2025
 */
public final class TimeHelper {

    /**
     * Constructor privado para evitar instanciación.
     */
    private TimeHelper() { }

    /**
     * Obtiene la fecha y hora local actual en la zona horaria de Madrid (Europe/Madrid).
     *
     * @return {@link LocalDateTime} con la fecha y hora actual en Madrid.
     */
    public static LocalDateTime getLocalDateTimeNow() {
        ZoneId zonaMadrid = ZoneId.of("Europe/Madrid");
        return LocalDateTime.now(zonaMadrid);
    }
}
