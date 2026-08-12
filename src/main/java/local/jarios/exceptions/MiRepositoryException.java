package local.jarios.exceptions;

/** Excepcion personalizada para errores de repositorio. */
public class MiRepositoryException extends RuntimeException {

  /**
   * Constructor con mensaje y causa original.
   *
   * @param message mensaje descriptivo del error ocurrido
   * @param cause causa original que produjo esta excepcion
   */
  public MiRepositoryException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructor con mensaje descriptivo.
   *
   * @param message mensaje descriptivo del error
   */
  public MiRepositoryException(String message) {
    super(message);
  }
}
