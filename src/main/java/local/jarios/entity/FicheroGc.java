package local.jarios.entity;

import com.fasterxml.uuid.Generators;
import jakarta.persistence.*;
import local.jarios.interfaces.Actualizable;
import local.jarios.utils.TamanoCampos;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.UUID;

/**
 * Representa la importación de ficheros Excel desde Internet,
 * con sus metadatos y URIs asociados.
 * Author: Juan Antonio
 * Date: 04/06/2024
 * Team: Juan Antonio
 */
@Slf4j
@Setter
@Getter
@Entity
@Table(
        name = "ficheros_gc",
        indexes = {
                @Index(name = "idx_ficheros_gc_shortname", columnList = "shortName", unique = true)
        }
)
public class FicheroGc extends AuditablePlus implements Actualizable<FicheroGc> {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "log_id",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_ficherosgc_log",
                    foreignKeyDefinition = "FOREIGN KEY (log_id) REFERENCES log(id) ON DELETE CASCADE"))
    private Log logEntity;

    @Column(name = "shortName", nullable = false, length = TamanoCampos.TAMANO_250)
    private String shortName;

    @Column(name = "longName", nullable = false, length = TamanoCampos.TAMANO_250)
    private String longName;

    @Column(name = "version", nullable = false, length = TamanoCampos.TAMANO_250)
    private String version;

    @Column(name = "canonicalUri", nullable = false, length = TamanoCampos.TAMANO_250)
    private String canonicalUri;

    @Column(name = "canonicalVersionUri", nullable = false, length = TamanoCampos.TAMANO_250)
    private String canonicalVersionUri;

    @Column(name = "locationUri", nullable = false, length = TamanoCampos.TAMANO_250)
    private String locationUri;

    /**
     * Constructor que genera un UUID basado en tiempo y registra creación.
     */
    public FicheroGc() {
        this.id = Generators.timeBasedEpochGenerator().generate();
        log.info("Creado FicheroGc con ID: {}", id);
    }

    /**
     * Compara si otro objeto es igual a esta instancia (ignorando mayúsculas).
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        FicheroGc that = (FicheroGc) obj;
        return comparar(that);
    }

    /**
     * Compara campos relevantes para determinar igualdad (ignora mayúsculas).
     */
    private boolean comparar(FicheroGc ficheroGcEntity) {
        return
                this.shortName.equalsIgnoreCase(ficheroGcEntity.getShortName()) &&
                        this.longName.equalsIgnoreCase(ficheroGcEntity.getLongName()) &&
                        this.version.equalsIgnoreCase(ficheroGcEntity.getVersion()) &&
                        this.canonicalUri.equalsIgnoreCase(ficheroGcEntity.getCanonicalUri()) &&
                        this.canonicalVersionUri.equalsIgnoreCase(ficheroGcEntity.getCanonicalVersionUri()) &&
                        this.locationUri.equalsIgnoreCase(ficheroGcEntity.getLocationUri());
    }

    /**
     * Código hash consistente con equals (ignora mayúsculas).
     */
    @Override
    public int hashCode() {
        return Objects.hash(
                shortName == null ? 0 : shortName.toLowerCase(),
                longName == null ? 0 : longName.toLowerCase(),
                version == null ? 0 : version.toLowerCase(),
                canonicalUri == null ? 0 : canonicalUri.toLowerCase(),
                canonicalVersionUri == null ? 0 : canonicalVersionUri.toLowerCase(),
                locationUri == null ? 0 : locationUri.toLowerCase()
        );
    }

    /**
     * Representación textual separada por ';' de los campos clave.
     */
    @Override
    public String toString() {
        return shortName + "; " +
                longName + "; " +
                version + "; " +
                canonicalUri + "; " +
                canonicalVersionUri + "; " +
                locationUri;
    }

    /**
     * Devuelve la clave única para mapear la entidad (campo shortName).
     */
    @Override
    public String getUniqueKey() {
        return this.shortName;
    }

    /**
     * Actualiza esta instancia con los valores de otro FicheroGc y registra el cambio.
     */
    @Override
    public void actualizarCon(FicheroGc otro) {
        this.longName = otro.getLongName();
        this.version = otro.getVersion();
        this.canonicalUri = otro.getCanonicalUri();
        this.canonicalVersionUri = otro.getCanonicalVersionUri();
        this.locationUri = otro.getLocationUri();
        log.info("FicheroGc con ID {} actualizado con nuevos valores.", this.id);
    }
}
