package local.jarios.exceptions;

/** Excepcion personalizada para errores de creacion de SessionFactory. */
public class MiSessionFactoryProvider extends RuntimeException {

  /**
   * Constructor con mensaje y causa original.
   *
   * @param message mensaje descriptivo del error ocurrido
   * @param cause causa original que produjo esta excepcion
   */
  public MiSessionFactoryProvider(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructor con mensaje descriptivo.
   *
   * @param message mensaje descriptivo del error
   */
  public MiSessionFactoryProvider(String message) {
    super(message);
  }
}
