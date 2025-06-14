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
 * <p>
 * Esta clase implementa la interfaz {@link Actualizable} para permitir
 * actualizaciones basadas en otra instancia de {@code FicheroGc}.
 * </p>
 * <p>
 * Contiene trazabilidad mediante logs para la creación y actualización de instancias.
 * </p>
 * <p>
 * Author: Juan Antonio<br>
 * Date: 04/06/2024<br>
 * Team: Juan Antonio
 * </p>
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
     * Constructor que genera un UUID basado en tiempo y registra la creación del objeto.
     */
    public FicheroGc() {
        this.id = Generators.timeBasedEpochGenerator().generate();
        log.info("Creado FicheroGc con ID: {}", id);
    }

    /**
     * Compara si otro objeto es igual a esta instancia.
     * <p>
     * La comparación se realiza ignorando mayúsculas en campos clave.
     * </p>
     *
     * @param obj Objeto a comparar.
     * @return {@code true} si ambos objetos son iguales según los campos relevantes.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            log.debug("Comparando objeto con sí mismo: retorna true");
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            log.debug("Objeto a comparar es null o de clase diferente: retorna false");
            return false;
        }
        FicheroGc that = (FicheroGc) obj;
        boolean result = comparar(that);
        log.debug("Resultado comparación con objeto ID {}: {}", that.id, result);
        return result;
    }

    /**
     * Compara campos relevantes para determinar igualdad.
     *
     * @param ficheroGcEntity Objeto {@code FicheroGc} con el que se compara.
     * @return {@code true} si todos los campos comparados son iguales (ignorando mayúsculas).
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
     * Genera un código hash consistente con el método {@link #equals(Object)}.
     *
     * @return Código hash basado en campos clave en minúsculas.
     */
    @Override
    public int hashCode() {
        int hash = Objects.hash(
                shortName == null ? 0 : shortName.toLowerCase(),
                longName == null ? 0 : longName.toLowerCase(),
                version == null ? 0 : version.toLowerCase(),
                canonicalUri == null ? 0 : canonicalUri.toLowerCase(),
                canonicalVersionUri == null ? 0 : canonicalVersionUri.toLowerCase(),
                locationUri == null ? 0 : locationUri.toLowerCase()
        );
        log.debug("Hash code generado para FicheroGc con ID {}: {}", id, hash);
        return hash;
    }

    /**
     * Representación textual del objeto con campos clave separados por punto y coma.
     *
     * @return Cadena con representación de campos clave.
     */
    @Override
    public String toString() {
        String representation = shortName + "; " +
                longName + "; " +
                version + "; " +
                canonicalUri + "; " +
                canonicalVersionUri + "; " +
                locationUri;
        log.debug("toString generado: {}", representation);
        return representation;
    }

    /**
     * Devuelve la clave única que identifica a esta entidad.
     *
     * @return Valor del campo único {@code shortName}.
     */
    @Override
    public String getUniqueKey() {
        return this.shortName;
    }

    /**
     * Actualiza esta instancia con los valores de otro objeto {@code FicheroGc}.
     * Se registran los cambios mediante log informativo.
     *
     * @param otro Objeto con los datos que actualizarán esta instancia.
     */
    @Override
    public void actualizarCon(FicheroGc otro) {
        log.info("Actualizando FicheroGc con ID {} con nuevos valores del objeto ID {}", this.id, otro.getId());
        this.longName = otro.getLongName();
        this.version = otro.getVersion();
        this.canonicalUri = otro.getCanonicalUri();
        this.canonicalVersionUri = otro.getCanonicalVersionUri();
        this.locationUri = otro.getLocationUri();
        log.info("Actualización completada para FicheroGc con ID {}", this.id);
    }
}
