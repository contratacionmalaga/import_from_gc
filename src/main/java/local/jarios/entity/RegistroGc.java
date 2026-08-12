package local.jarios.entity;

import java.util.UUID;
import local.jarios.helpers.ComunHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/** Representa un registro GC con codigo, nombre y log asociado. */
@Setter
@Getter
@NoArgsConstructor
@Slf4j
public class RegistroGc extends AuditableCreatedAt {

  /** Codigo identificador del registro. */
  private String code;

  /** Nombre o descripcion del registro. */
  private String nombre;

  /** Identificador unico asociado al log de ejecucion. */
  private UUID logId;

  /**
   * Constructor con parametros.
   *
   * @param code codigo identificador
   * @param nombre nombre o descripcion
   * @param logId identificador unico asociado al log de ejecucion
   */
  public RegistroGc(String code, String nombre, UUID logId) {
    this.code = code;
    this.nombre = nombre;
    this.logId = logId;
    this.markAsCreated();
  }

  /**
   * Representacion en cadena del registro.
   *
   * @return cadena en formato de registro GC
   */
  @Override
  public String toString() {
    return "RegistroGc: ["
        + "logId='"
        + logId
        + "', "
        + "code='"
        + code
        + "', "
        + "nombre='"
        + nombre
        + "', "
        + "createdAt='"
        + ComunHelper.getFechaHoraFormateada(this.getCreatedAt())
        + "']";
  }
}
