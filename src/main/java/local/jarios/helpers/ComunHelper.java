package local.jarios.helpers;

import local.jarios.common.util.Constantes;
import local.jarios.exceptions.MiUnknownHostException;
import local.jarios.managers.ManagerJackson;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * Clase auxiliar con métodos comunes y utilidades generales.
 * <p>
 * Proporciona funciones para obtener el nombre del host, imprimir objetos,
 * calcular tiempos de ejecución y formatear fechas.
 * </p>
 *
 * @author Juan Antonio
 */
@Slf4j
public final class ComunHelper {

    /**
     * Constructor privado para evitar instanciación.
     */
    private ComunHelper() { }

    /**
     * Obtiene el nombre del equipo donde se está ejecutando la aplicación.
     *
     * @return Nombre del host local.
     * @throws MiUnknownHostException Si no se puede resolver el nombre del host.
     */
    public static String getHostName() throws MiUnknownHostException {
        log.debug("Intentando obtener el nombre del host local");
        try {
            String hostName = InetAddress.getLocalHost().getHostName();
            log.debug("Nombre del host obtenido: {}", hostName);
            return hostName;
        } catch (UnknownHostException ex) {
            log.error("Error al obtener el nombre del host", ex);
            throw new MiUnknownHostException(ex);
        }
    }

    /**
     * Imprime en el log el contenido formateado (pretty print) de un objeto JSON.
     *
     * @param object Objeto que se desea imprimir.
     */
    public static void imprimir(Object object) {
        Arrays
                .stream(ManagerJackson.objectToJsonPretty(object).split(Constantes.CR))
                .forEach(log::info);
    }

    /**
     * Calcula la diferencia entre dos instantes {@link LocalDateTime} y devuelve
     * una cadena con la duración en formato "Xh Ym Zs Wms".
     *
     * @param fechaHoraInicial Fecha y hora inicial.
     * @param fechaHoraFinal Fecha y hora final.
     * @return Cadena formateada con la duración entre las dos fechas.
     */
    public static String getDiferenciaLocalDateTime(LocalDateTime fechaHoraInicial, LocalDateTime fechaHoraFinal) {
        Duration duracion = Duration.between(fechaHoraInicial, fechaHoraFinal);
        long horas = duracion.toHours();
        long minutos = duracion.toMinutesPart();
        long segundos = duracion.toSecondsPart();
        long milisegundos = duracion.toMillisPart();
        return String.format("%dh %dm %ds %dms", horas, minutos, segundos, milisegundos);
    }

    /**
     * Devuelve una cadena con la fecha y hora formateada en el patrón "yyyy-MM-dd HH:mm:ss".
     * Si el parámetro es null, usa la fecha y hora actual.
     *
     * @param localDateTime El objeto {@link LocalDateTime} a formatear, o null para usar la fecha/hora actual.
     * @return Fecha y hora formateada como cadena.
     */
    public static String getFechaHoraFormateada(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return Constantes.NULL;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constantes.DATE_TIME_PATTERN);
        return localDateTime.format(formatter);
    }
}
