package local.jarios.exceptions;

/** Excepcion personalizada para errores de gestion transaccional. */
public class MiTransactionManagerException extends RuntimeException {

  /**
   * Constructor con mensaje y causa original.
   *
   * @param message mensaje descriptivo del error ocurrido
   * @param cause causa original que produjo esta excepcion
   */
  public MiTransactionManagerException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructor con mensaje descriptivo.
   *
   * @param message mensaje descriptivo del error
   */
  public MiTransactionManagerException(String message) {
    super(message);
  }
}
