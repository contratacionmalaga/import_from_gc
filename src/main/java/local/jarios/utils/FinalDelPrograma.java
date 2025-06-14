package local.jarios.utils;

import local.jarios.enums.TipoFinalEjecucion;
import lombok.extern.slf4j.Slf4j;

/**
 * Clase utilitaria para finalizar la ejecución del programa
 * registrando el resultado final mediante logs y terminando el proceso
 * con el código adecuado.
 * <p>
 * El método {@code finalizar} acepta un tipo de finalización que determina
 * si la ejecución terminó correctamente o con error y actúa en consecuencia.
 * </p>
 *
 * <p><b>Author:</b> Juan Antonio</p>
 */
@Slf4j
public final class FinalDelPrograma {

    /**
     * Constructor privado para evitar instanciación.
     */
    private FinalDelPrograma() {
        /* CONSTRUCTOR VACÍO */
    }

    /**
     * Finaliza la ejecución del programa registrando un mensaje
     * de resultado y llamando a {@code System.exit} con el código
     * 0 para ejecución correcta o 1 para error.
     *
     * @param tipoFinal Tipo de finalización de la ejecución.
     */
    public static void finalizar(TipoFinalEjecucion tipoFinal) {
        if (tipoFinal == TipoFinalEjecucion.CORRECTO) {
            log.info(Mensajes.FINAL_CORRECTO);
            System.exit(0);
        } else {
            log.error(Mensajes.FINAL_ERROR);
            System.exit(1);
        }
    }
}
