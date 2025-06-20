package local.jarios.helpers;

import local.jarios.common.util.Constantes;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Clase de utilidad para operaciones relacionadas con fechas y horas.
 * <p>
 * Provee métodos para formatear fechas según los patrones definidos en {@link Constantes}.
 * </p>
 *
 * @author Juan Antonio
 * @since 2025
 */
@Slf4j
public final class FechaHelper {

    /**
     * Constructor privado para evitar instanciación.
     */
    private FechaHelper() { }

    /**
     * Devuelve una representación formateada en cadena de una fecha y hora en formato largo.
     *
     * @param timestampFechaHora Objeto {@link Timestamp} que representa la fecha y hora a formatear.
     * @return Cadena de texto con la fecha formateada. Si el parámetro es {@code null}, devuelve {@code null}.
     */
    public static String getFormatoFechaLargo(Timestamp timestampFechaHora) {
        if (timestampFechaHora == null) {
            log.warn("Se ha recibido un Timestamp nulo en getFormatoFechaLargo.");
            return null;
        }

        try {
            DateTimeFormatter formato = DateTimeFormatter.ofPattern(Constantes.FORMATO_FECHA);
            LocalDateTime fechaLocal = timestampFechaHora.toLocalDateTime();
            String fechaFormateada = fechaLocal.format(formato);
            log.debug("Fecha formateada correctamente: {}", fechaFormateada);
            return fechaFormateada;
        } catch (Exception e) {
            log.error("Error al formatear la fecha: {}", e.getMessage(), e);
            return null;
        }
    }
}
