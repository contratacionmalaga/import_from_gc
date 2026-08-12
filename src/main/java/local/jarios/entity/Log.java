package local.jarios.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/** Entidad que agrupa ficheros GC y estadisticas de una ejecucion. */
@Slf4j
@Setter
@Getter
@Entity
@Table(name = "log")
public class Log extends AuditableCreatedAt {

  /** Identificador unico del registro. */
  @Id
  @GeneratedValue(generator = "UUID")
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  /** Lista de ficheros asociados a este log. */
  @OneToMany(mappedBy = "logEntity", orphanRemoval = true, cascade = CascadeType.ALL)
  private List<FicheroGc> ficherosGc = new ArrayList<>();

  /** Estadistica asociada a este log. */
  @OneToOne(mappedBy = "logEntity", orphanRemoval = true, cascade = CascadeType.ALL)
  private Estadistica estadistica;

  /** Constructor que registra la creacion. */
  public Log() {
    this.markAsCreated();
  }
}
