package local.jarios.exceptions;

/**
 * Description:
 * Author: juan
 * Date: 28/12/2024
 * Team:
 */
public class MiParseException extends RuntimeException {

    /**
     * Constructor que crea una excepción {@code MiParseException} con un mensaje
     * y una causa especificada.
     *
     * @param message Mensaje descriptivo del error ocurrido.
     * @param cause   Causa original que produjo esta excepción.
     */
    public MiParseException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor que crea una excepción {@code MiParseException} con solo un mensaje
     * descriptivo del error ocurrido.
     *
     * @param message Mensaje descriptivo del error.
     */
    public MiParseException(String message) {
        super(message);
    }
}
