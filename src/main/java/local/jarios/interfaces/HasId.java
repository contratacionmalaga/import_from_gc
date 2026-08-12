package local.jarios.interfaces;

import java.util.UUID;

/** Contrato para objetos con identificador UUID. */
public interface HasId {

  /**
   * Obtiene el identificador unico del objeto.
   *
   * @return identificador UUID
   */
  UUID getId();
}
