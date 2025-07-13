package local.jarios.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import local.jarios.helpers.TimeHelper;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Description: Clase que añade elementos de Auditorías a las clases que la extienden
 * Author: juan
 * Date: 04/06/2024
 * Team: Juan Antonio
 */
@Setter
@Getter
@MappedSuperclass
public abstract class AuditableCreatedAt {

    /**
     * Marca temporal de la creación del registro.
     * <p>
     * Se almacena en la columna "created_at". No puede ser nulo ni modificarse tras su asignación inicial.
     * </p>
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Se invoca antes de persistir el objeto por primera vez.
     */
    protected void markAsCreated() {
        createdAt = TimeHelper.getLocalDateTimeNow();
    }

    /**
     * Constructor sin argumentos.
     */
    protected AuditableCreatedAt() {
        // Constructor vacío
    }
}
