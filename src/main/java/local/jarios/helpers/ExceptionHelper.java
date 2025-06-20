package local.jarios.helpers;

import local.jarios.common.util.Mensajes;
import lombok.extern.slf4j.Slf4j;

/**
 * Clase helper para manejo y registro de excepciones con trazabilidad de clase y método.
 * <p>
 * Proporciona un método estático para loggear excepciones indicando desde qué clase y método ocurrió.
 * </p>
 *
 * @author Juan
 * @since 04/02/2025
 */
@Slf4j
public final class ExceptionHelper {

    /**
     * Constructor privado para evitar instanciación.
     */
    private ExceptionHelper() { }

    /**
     * Registra una excepción en el log con la clase y método donde ocurrió la llamada a este método.
     *
     * @param ex Excepción a registrar.
     */
    public static void logException(Exception ex) {
        // Obtenemos la pila de ejecución actual
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        /*
         * La pila tiene este esquema:
         * 0 -> Thread.getStackTrace()
         * 1 -> ExceptionHelper.logException()
         * 2 -> método que llamó a logException (caller)
         */
        StackTraceElement caller = null;
        if (stackTrace.length > 2) {
            caller = stackTrace[2];
        }

        String className = caller != null ? caller.getClassName() : "UnknownClass";
        String methodName = caller != null ? caller.getMethodName() : "UnknownMethod";

        // Registro la excepción con la plantilla definida en Mensajes.EXCEPTION_ERROR
        log.error(Mensajes.EXCEPTION_ERROR, className, methodName, ex.getMessage(), ex);
    }
}
