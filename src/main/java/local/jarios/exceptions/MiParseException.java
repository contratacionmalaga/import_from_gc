package local.jarios.exceptions;

/** Excepcion personalizada para errores de parseo. */
public class MiParseException extends RuntimeException {

  /**
   * Constructor con mensaje y causa original.
   *
   * @param message mensaje descriptivo del error ocurrido
   * @param cause causa original que produjo esta excepcion
   */
  public MiParseException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructor con mensaje descriptivo.
   *
   * @param message mensaje descriptivo del error
   */
  public MiParseException(String message) {
    super(message);
  }
}
