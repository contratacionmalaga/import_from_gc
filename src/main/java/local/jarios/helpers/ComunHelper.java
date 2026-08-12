package local.jarios.helpers;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import local.jarios.common.util.Constantes;
import local.jarios.exceptions.MiUnknownHostException;
import local.jarios.managers.ManagerJackson;
import lombok.extern.slf4j.Slf4j;

/** Utilidades comunes de host, JSON, duraciones y fechas. */
@Slf4j
public final class ComunHelper {

  /** Constructor privado de clase utilitaria. */
  private ComunHelper() {}

  /**
   * Obtiene el nombre del equipo donde se ejecuta la aplicacion.
   *
   * @return nombre del host local
   * @throws MiUnknownHostException si no se puede resolver el host
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
   * Imprime en el log el contenido JSON formateado de un objeto.
   *
   * @param object objeto que se desea imprimir
   */
  public static void imprimir(Object object) {
    Arrays.stream(ManagerJackson.objectToJsonPretty(object).split(Constantes.CR))
        .forEach(log::info);
  }

  /**
   * Calcula la diferencia entre dos instantes.
   *
   * @param fechaHoraInicial fecha y hora inicial
   * @param fechaHoraFinal fecha y hora final
   * @return duracion formateada entre las dos fechas
   */
  public static String getDiferenciaLocalDateTime(
      LocalDateTime fechaHoraInicial, LocalDateTime fechaHoraFinal) {
    Duration duracion = Duration.between(fechaHoraInicial, fechaHoraFinal);
    long horas = duracion.toHours();
    long minutos = duracion.toMinutesPart();
    long segundos = duracion.toSecondsPart();
    long milisegundos = duracion.toMillisPart();
    return String.format("%dh %dm %ds %dms", horas, minutos, segundos, milisegundos);
  }

  /**
   * Devuelve una cadena con la fecha y hora formateada.
   *
   * @param localDateTime fecha y hora a formatear, o null
   * @return fecha y hora formateada o valor NULL textual
   */
  public static String getFechaHoraFormateada(LocalDateTime localDateTime) {
    if (localDateTime == null) {
      return Constantes.NULL;
    }
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constantes.DATE_TIME_PATTERN);
    return localDateTime.format(formatter);
  }
}
