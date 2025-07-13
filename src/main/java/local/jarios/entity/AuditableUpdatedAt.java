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
public abstract class AuditableUpdatedAt extends AuditableCreatedAt {

    /**
     * Marca temporal de la última actualización del registro.
     * <p>
     * Se almacena en la columna "updated_at". Puede ser nulo y se actualiza al modificar el registro.
     * </p>
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Marca la entidad como eliminada lógicamente estableciendo la fecha y hora actual en el campo {@code deletedAt}.
     * <p>
     * Este método no elimina físicamente el registro de la base de datos, sino que registra el momento en que
     * se considera eliminado, permitiendo su exclusión lógica en consultas posteriores.
     * </p>
     * <p>
     * Se utiliza la hora actual basada en la zona horaria de España (Europe/Madrid) obtenida a través del {@code TimeHelper}.
     * </p>
     */
    public void markAsUpdated() {

        this.updatedAt = TimeHelper.getLocalDateTimeNow();
    }

    /**
     * Constructor sin argumentos.
     */
    protected AuditableUpdatedAt() {
        // Constructor vacío
    }
}
