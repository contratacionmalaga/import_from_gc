package local.jarios.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

/**
 * Clase base para entidades que requieren seguimiento de auditoría en cuanto a fechas de creación y actualización.
 * <p>
 * Esta clase proporciona los campos {@code createdAt} y {@code updatedAt} que se actualizan automáticamente
 * durante las operaciones de persistencia para registrar los momentos en que se crean o actualizan las entidades.
 * </p>
 * <p>
 * Author: Juan Antonio<br>
 * Date: 14/06/2025<br>
 * Team: Contratacion Electrónica
 * </p>
 */
@Slf4j
@Getter
@Setter
@MappedSuperclass
public abstract class AuditablePlus {

    /**
     * Fecha y hora de creación del registro.
     * <p>
     * Se almacena en la columna "created_at". No puede ser nulo ni modificarse una vez establecido.
     * </p>
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Fecha y hora de la última actualización del registro.
     * <p>
     * Se almacena en la columna "updated_at". No puede ser nulo y puede actualizarse con cada modificación.
     * </p>
     */
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /**
     * Método que se ejecuta antes de persistir la entidad para establecer las fechas de creación y actualización.
     * <p>
     * Este método se invoca automáticamente durante la operación de persistencia para registrar el momento en
     * que la entidad es creada.
     * </p>
     */
    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
        log.debug("Entidad creada en: {}", now);
    }

    /**
     * Método que se ejecuta antes de actualizar la entidad para establecer la fecha de actualización.
     * <p>
     * Este método se invoca automáticamente durante la operación de actualización para registrar el momento en
     * que la entidad es modificada.
     * </p>
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
        log.debug("Entidad actualizada en: {}", updatedAt);
    }

    /**
     * Constructor sin argumentos.
     */
    protected AuditablePlus() {
        // Constructor vacío
    }
}
