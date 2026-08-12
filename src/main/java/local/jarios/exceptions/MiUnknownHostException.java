package local.jarios.exceptions;

import java.net.UnknownHostException;

/** Excepcion personalizada para errores al resolver el nombre del host. */
public class MiUnknownHostException extends Exception {

  /**
   * Constructor que envuelve una {@link UnknownHostException}.
   *
   * @param ex excepcion original de resolucion de host
   */
  public MiUnknownHostException(UnknownHostException ex) {
    super(ex);
  }
}
