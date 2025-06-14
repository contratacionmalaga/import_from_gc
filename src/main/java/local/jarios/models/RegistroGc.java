package local.jarios.models;

import local.jarios.entity.Auditable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Representa un registro de importación de un fichero Excel desde Internet.
 * Extiende {@link Auditable} para añadir auditoría.
 * Contiene un código identificador y un nombre descriptivo.
 *
 * @author Juan Antonio
 * @since 04/06/2024
 * @author Juan Antonio
 */
@Setter
@Getter
@NoArgsConstructor
public class RegistroGc extends Auditable {

    private static final Logger log = LoggerFactory.getLogger(RegistroGc.class);

    /** Código identificador del registro */
    private String code;

    /** Nombre o descripción del registro */
    private String nombre;

    /**
     * Constructor con parámetros.
     *
     * @param code Código identificador
     * @param nombre Nombre o descripción
     */
    public RegistroGc(String code, String nombre) {
        this.code = code;
        this.nombre = nombre;
        log.debug("RegistroGc creado con code='{}' y nombre='{}'", code, nombre);
    }

    /**
     * Representación en cadena del registro.
     *
     * @return cadena en formato (code,nombre)
     */
    @Override
    public String toString() {
        String result = "(" + code + "," + nombre + ")";
        log.debug("toString() llamado: {}", result);
        return result;
    }
}
