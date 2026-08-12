package local.jarios.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import local.jarios.common.util.TamanoCampos;
import local.jarios.exceptions.MiUnknownHostException;
import local.jarios.helpers.ComunHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/** Entidad con las estadisticas de una ejecucion de importacion GC. */
@Slf4j
@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "estadistica")
public class Estadistica extends AuditableCreatedAt {

  /** Identificador unico del registro. */
  @Id
  @GeneratedValue(generator = "UUID")
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  /** Relacion uno a uno con la entidad {@link Log}. */
  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(
      name = "log_id",
      nullable = false,
      referencedColumnName = "id",
      foreignKey =
          @ForeignKey(
              name = "fk_estadistica_log",
              foreignKeyDefinition = "FOREIGN KEY (log_id) REFERENCES log(id) ON DELETE CASCADE"))
  private Log logEntity;

  /** Nombre del equipo asociado a esta estadistica. */
  @Column(name = "equipo", nullable = false, length = TamanoCampos.TAMANO_250)
  private String equipo;

  /** Numero total de ficheros leidos durante el proceso. */
  @Column(name = "nTotalFicherosLeidos")
  // CHECKSTYLE.OFF: MemberName
  private int nTotalFicherosLeidos;
  // CHECKSTYLE.ON: MemberName

  /** Numero total de registros GC procesados. */
  @Column(name = "nRegistrosGc")
  // CHECKSTYLE.OFF: MemberName
  private int nRegistrosGc;
  // CHECKSTYLE.ON: MemberName

  /** Fecha y hora de inicio del proceso de importacion. */
  @Column(name = "fechaHoraInicial")
  private LocalDateTime fechaHoraInicial;

  /** Fecha y hora de finalizacion del proceso de importacion. */
  @Column(name = "fechaHoraFinal")
  private LocalDateTime fechaHoraFinal;

  /** Duracion total del proceso de importacion en formato legible. */
  @Column(name = "duracion", length = TamanoCampos.TAMANO_250)
  private String duracion;

  /**
   * Constructor que inicializa la estadistica con un log asociado.
   *
   * @param logEntity entidad {@link Log} asociada a esta estadistica
   * @throws MiUnknownHostException si no se puede obtener el nombre del host
   */
  public Estadistica(Log logEntity) throws MiUnknownHostException {
    this.markAsCreated();
    this.logEntity = logEntity;
    this.equipo = ComunHelper.getHostName();
  }

  /**
   * Aumenta el numero de registros GC procesados.
   *
   * @param incremento valor que incrementa el contador actual
   */
  // CHECKSTYLE.OFF: AbbreviationAsWordInName
  public void aumentarNRegistrosGc(int incremento) {
    this.nRegistrosGc += incremento;
  }
  // CHECKSTYLE.ON: AbbreviationAsWordInName

  /**
   * Representacion en texto del objeto Estadistica con sus campos principales.
   *
   * @return texto con la representacion del objeto
   */
  @Override
  public String toString() {
    return "Estadistica ["
        + "equipo='"
        + equipo
        + "', "
        + "nTotalFicherosLeidos="
        + nTotalFicherosLeidos
        + ", "
        + "nRegistrosGc="
        + nRegistrosGc
        + ", "
        + "fechaHoraInicial="
        + ComunHelper.getFechaHoraFormateada(fechaHoraInicial)
        + ", "
        + "fechaHoraFinal="
        + ComunHelper.getFechaHoraFormateada(fechaHoraFinal)
        + ", "
        + "duracion="
        + duracion
        + "createdAt="
        + ComunHelper.getFechaHoraFormateada(getCreatedAt())
        + "]";
  }
}
