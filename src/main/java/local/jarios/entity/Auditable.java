package local.jarios.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
@MappedSuperclass
public abstract class Auditable {

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

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
}
