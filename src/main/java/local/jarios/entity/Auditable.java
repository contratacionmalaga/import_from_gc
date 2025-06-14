package local.jarios.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * Clase base para auditoría que añade timestamps de creación y actualización.
 * Las entidades que la extiendan heredarán estos campos y comportamiento.
 * Author: Juan Antonio
 * Date: 04/06/2024
 */
@Setter
@Getter
@MappedSuperclass
public abstract class Auditable {

    /**
     * Marca temporal de la creación del registro.
     * <p>
     * Se almacena en la columna "created_at". No puede ser nulo ni modificarse tras su asignación inicial.
     * </p>
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    /**
     * Marca temporal de la última actualización del registro.
     * <p>
     * Se almacena en la columna "updated_at". Puede ser nulo y se actualiza al modificar el registro.
     * </p>
     */
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    /**
     * Se invoca antes de persistir el objeto por primera vez.
     */
    @PrePersist
    protected void onCreate() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        createdAt = now;
        updatedAt = now;
    }

    /**
     * Se invoca antes de actualizar el objeto.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Constructor sin argumentos.
     */
    protected Auditable() {
        // Constructor vacío
    }
}
