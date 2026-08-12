package local.jarios.helpers;

import java.time.LocalDateTime;
import java.time.ZoneId;

/** Utilidad para operaciones relacionadas con tiempo y fechas. */
public final class TimeHelper {

  /** Constructor privado de clase utilitaria. */
  private TimeHelper() {}

  /**
   * Obtiene la fecha y hora local actual en la zona horaria de Madrid.
   *
   * @return fecha y hora actual en Europe/Madrid
   */
  public static LocalDateTime getLocalDateTimeNow() {
    ZoneId zonaMadrid = ZoneId.of("Europe/Madrid");
    return LocalDateTime.now(zonaMadrid);
  }
}
