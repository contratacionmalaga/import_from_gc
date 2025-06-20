package local.jarios.common.util;

/**
 * Clase utilitaria que define constantes para los tamaños máximos
 * de campos que se usan en la aplicación, facilitando la gestión
 * de límites para atributos o columnas en la base de datos.
 * <p>
 * Esta clase es final y su constructor es privado para evitar
 * instanciación y herencia.
 * </p>
 *
 * <p><b>Author:</b> Juan Antonio</p>
 * <p><b>Date:</b> 04/06/2024</p>
 * <p><b>Team:</b> Juan Antonio</p>
 */
public final class TamanoCampos {

    /**
     * Tamaño máximo común para campos que admiten hasta 250 caracteres.
     */
    public static final int TAMANO_250 = 250;

    /**
     * Constructor privado para evitar la creación de instancias de esta clase.
     */
    private TamanoCampos() {}
}
