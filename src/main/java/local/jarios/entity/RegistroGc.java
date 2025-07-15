package local.jarios.entity;

import local.jarios.helpers.ComunHelper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

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
@Slf4j
public class RegistroGc extends AuditableCreatedAt {

    /** Código identificador del registro */
    private String code;

    /** Nombre o descripción del registro */
    private String nombre;

    /** Nombre o descripción del registro */
    private UUID logId;

    /**
     * Constructor con parámetros.
     *
     * @param code Código identificador
     * @param nombre Nombre o descripción
     * @param logId Identificador único asociado al Log de ejecución
     */
    public RegistroGc(String code, String nombre, UUID logId) {
        this.code = code;
        this.nombre = nombre;
        this.logId = logId;
        this.markAsCreated();
    }

    /**
     * Representación en cadena del registro.
     *
     * @return cadena en formato (code,nombre)
     */
    @Override
    public String toString() {
        return "RegistroGc: [" +
                "logId='" + logId + "', " +
                "code='" + code + "', " +
                "nombre='" + nombre + "', " +
                "createdAt='" + ComunHelper.getFechaHoraFormateada(this.getCreatedAt()) +
                "']";

    }

    /** Constructor privado */
    private RegistroGc() {
        //
    }
}
