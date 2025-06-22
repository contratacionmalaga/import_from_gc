package local.jarios.interfaces;

import local.jarios.entity.Log;

import java.util.UUID;

/**
 * Interfaz genérica que define el contrato para entidades actualizables.
 * <p>
 * Cualquier clase que implemente esta interfaz podrá ser identificada por una clave única,
 * tener un identificador tipo UUID, ser actualizada a partir de otra instancia y registrar un log.
 * </p>
 *
 * @param <T> Tipo de entidad que implementa la interfaz.
 *
 * @author Juan
 * @since 01/03/2025
 */
public interface EsActualizable<T> {

    /**
     * Obtiene una clave única representativa del objeto (por ejemplo, una combinación de campos clave).
     * Esta clave será usada como identificador lógico en estructuras de comparación o unificación.
     *
     * @return Cadena única representativa del objeto.
     */
    String getUniqueKey();

    /**
     * Obtiene el identificador persistente del objeto, generalmente una clave primaria tipo UUID.
     *
     * @return UUID del objeto.
     */
    UUID getId();

    /**
     * Asigna el identificador único al objeto.
     */
    void setId();

    /**
     * Actualiza los atributos del objeto actual usando los valores de otro objeto del mismo tipo.
     * <p>
     * Este método no debería modificar campos inmutables como el ID o claves únicas.
     * </p>
     *
     * @param otro Objeto desde el cual se copiarán los valores.
     */
    void actualizarCon(T otro);

    /**
     * Asocia un objeto {@link Log} a la entidad actual.
     * <p>
     * Este log puede ser utilizado para trazabilidad de actualizaciones o inserciones.
     * </p>
     *
     * @param logEntity Objeto de log a asociar.
     */
    void setLogEntity(Log logEntity);
}
