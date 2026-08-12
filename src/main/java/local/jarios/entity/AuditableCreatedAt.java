package local.jarios.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import local.jarios.helpers.TimeHelper;
import lombok.Getter;
import lombok.Setter;

/** Base de auditoria con marca de creacion para entidades persistentes. */
@Setter
@Getter
@MappedSuperclass
public abstract class AuditableCreatedAt {

  /** Marca temporal de creacion persistida en la columna {@code created_at}. */
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  /** Se invoca antes de persistir el objeto por primera vez. */
  protected void markAsCreated() {
    createdAt = TimeHelper.getLocalDateTimeNow();
  }

  /** Constructor sin argumentos. */
  protected AuditableCreatedAt() {}
}
