package local.jarios.exceptions;

/** Excepcion personalizada para errores de servicio. */
public class MiServiceException extends RuntimeException {

  /**
   * Constructor con mensaje y causa original.
   *
   * @param message mensaje descriptivo del error ocurrido
   * @param cause causa original que produjo esta excepcion
   */
  public MiServiceException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructor con mensaje descriptivo.
   *
   * @param message mensaje descriptivo del error
   */
  public MiServiceException(String message) {
    super(message);
  }
}
