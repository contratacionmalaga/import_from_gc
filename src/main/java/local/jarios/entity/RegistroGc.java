package local.jarios.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Representa un registro de importación de un fichero Excel desde Internet.
 * Extiende {@link AuditableCreatedAt} para añadir auditoría.
 * Contiene un código identificador y un nombre descriptivo.
 *
 * @author Juan Antonio
 * @since 04/06/2024
 * @author Juan Antonio
 */
@Setter
@Getter
@NoArgsConstructor
@Slf4j
public class RegistroGc extends AuditableCreatedAt {

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
