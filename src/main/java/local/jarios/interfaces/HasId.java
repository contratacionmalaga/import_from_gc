package local.jarios.interfaces;

import java.util.UUID;

/**
 * Interfaz para entidades que poseen un identificador único tipo UUID.
 * <p>
 * Garantiza que las clases que implementen esta interfaz tengan los métodos
 * para obtener y establecer su ID.
 * </p>
 *
 * @author juan
 * @since 01/03/2025
 */
public interface HasId {

    /**
     * Obtiene el identificador único de la entidad.
     *
     * @return UUID del objeto.
     */
    UUID getId();

}
