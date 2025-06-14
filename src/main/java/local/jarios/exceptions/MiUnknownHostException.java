package local.jarios.exceptions;

import java.net.UnknownHostException;

/**
 * Description:
 * Author: juan
 * Date: 28/12/2024
 * Team:
 */
public class MiUnknownHostException extends Exception {

    /**
     * Constructor que envuelve una {@link UnknownHostException} en una
     * excepción personalizada {@code MiUnknownHostException}.
     *
     * @param ex Excepción original de tipo {@link UnknownHostException} que se desea encapsular.
     */
    public MiUnknownHostException(UnknownHostException ex) {

        super(ex);
    }
}
