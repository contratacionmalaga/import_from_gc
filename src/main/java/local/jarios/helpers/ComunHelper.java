package local.jarios.helpers;

import local.jarios.exceptions.MiUnknownHostException;
import local.jarios.managers.ManagerGsons;
import local.jarios.utils.Constantes;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
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
        log.debug("Imprimiendo objeto de tipo: {}", object != null ? object.getClass().getSimpleName() : "null");
        Arrays.stream(ManagerGsons.objectToJsonPretty(object).split(Constantes.CR))
                .forEach(log::info);
        log.debug("Objeto impreso correctamente");
    }

    /**
     * Calcula el tiempo transcurrido entre dos marcas temporales y devuelve
     * un string formateado con horas, minutos, segundos y milisegundos.
     *
     * @param fechaHoraInicial Marca temporal inicial.
     * @param fechaHoraFinal   Marca temporal final.
     * @return Duración en formato "<hours>h <minutes>m <seconds>s <milliseconds>ml".
     */
    public static String calcularTiempoEjecucion(Timestamp fechaHoraInicial, Timestamp fechaHoraFinal) {
        log.debug("Calculando tiempo de ejecución entre {} y {}", fechaHoraInicial, fechaHoraFinal);

        int milesimas = 1000;
        int minutos = 60;
        int segundos = 60;
        String formatoDuracion = "%sh %sm %ss %sml";

        long diffInMillis = fechaHoraFinal.getTime() - fechaHoraInicial.getTime();

        long hours = diffInMillis / (milesimas * segundos * minutos);
        long minutes = (diffInMillis % (milesimas * segundos * minutos)) / (milesimas * segundos);
        long seconds = (diffInMillis % (milesimas * segundos)) / milesimas;
        long milliseconds = diffInMillis % milesimas;

        String duracionFormateada = String.format(formatoDuracion, hours, minutes, seconds, milliseconds);
        log.debug("Duración calculada: {}", duracionFormateada);
        return duracionFormateada;
    }

    /**
     * Formatea una marca temporal {@link Timestamp} a cadena con formato
     * "yyyy-MM-dd HH:mm:ss". Si la marca es null, se formatea la fecha y hora actuales.
     *
     * @param fechaHora Marca temporal a formatear.
     * @return Fecha y hora formateadas como cadena.
     */
    public static String getFechaHoraFormateada(Timestamp fechaHora) {
        LocalDateTime fecha = (fechaHora != null) ? fechaHora.toLocalDateTime() : LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String fechaFormateada = fecha.format(formatter);
        log.debug("Fecha formateada: {}", fechaFormateada);
        return fechaFormateada;
    }
}
