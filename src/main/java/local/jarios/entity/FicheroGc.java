package local.jarios.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import local.jarios.common.util.TamanoCampos;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/** Entidad con los metadatos de un fichero GC importado. */
@Slf4j
@Setter
@Getter
@Entity
@Table(name = "ficheros_gc")
public class FicheroGc extends AuditableCreatedAt {

  /** Identificador unico del registro. */
  @Id
  @GeneratedValue(generator = "UUID")
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  /** Relacion muchos a uno con la entidad {@link Log}. */
  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(
      name = "log_id",
      nullable = false,
      referencedColumnName = "id",
      foreignKey =
          @ForeignKey(
              name = "fk_ficherosgc_log",
              foreignKeyDefinition = "FOREIGN KEY (log_id) REFERENCES log(id) ON DELETE CASCADE"))
  private Log logEntity;

  /** Nombre corto del fichero GC. */
  @Column(name = "short_name", nullable = false, length = TamanoCampos.TAMANO_250)
  private String shortName;

  /** Nombre largo o descriptivo del fichero GC. */
  @Column(name = "long_name", nullable = false, length = TamanoCampos.TAMANO_250)
  private String longName;

  /** Version del fichero GC. */
  @Column(name = "version", nullable = false, length = TamanoCampos.TAMANO_250)
  private String version;

  /** URI canonica del fichero GC. */
  @Column(name = "canonical_uri", nullable = false, length = TamanoCampos.TAMANO_250)
  private String canonicalUri;

  /** URI canonica de la version del fichero GC. */
  @Column(name = "canonical_version_uri", nullable = false, length = TamanoCampos.TAMANO_250)
  private String canonicalVersionUri;

  /** URI de localizacion del fichero GC. */
  @Column(name = "location_uri", nullable = false, length = TamanoCampos.TAMANO_250)
  private String locationUri;

  /** Constructor que registra la creacion del objeto. */
  public FicheroGc() {
    this.markAsCreated();
  }

  /**
   * Representacion textual del objeto con campos clave separados por punto y coma.
   *
   * @return cadena con representacion de campos clave
   */
  @Override
  public String toString() {
    String representation =
        shortName
            + "; "
            + longName
            + "; "
            + version
            + "; "
            + canonicalUri
            + "; "
            + canonicalVersionUri
            + "; "
            + locationUri;
    log.debug("toString generado: {}", representation);
    return representation;
  }
}
