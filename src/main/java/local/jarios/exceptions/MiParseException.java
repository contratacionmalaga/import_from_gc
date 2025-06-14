package local.jarios.exceptions;

/**
 * Description:
 * Author: juan
 * Date: 28/12/2024
 * Team:
 */
public class MiParseException extends RuntimeException {

    public MiParseException(String message, Throwable cause) {

        super(message, cause);
    }

    public MiParseException(String message) {

        super(message);
    }
}
