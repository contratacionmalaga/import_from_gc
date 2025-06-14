package local.jarios.entity;

import com.fasterxml.uuid.Generators;
import jakarta.persistence.*;
import local.jarios.interfaces.Actualizable;
import local.jarios.utils.TamanoCampos;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

/**
 * Description: Importaciones de Ficheros Excel desde Internet
 * Author: Juan Antonio
 * Date: 04/06/2024
 * Team: Juan Antonio
 */

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
     * Constructor sin argumentos que genera un UUID basado en tiempo.
     */
    public FicheroGc() {
        this.id = Generators.timeBasedEpochGenerator().generate();
    }

    /**
     * Método encargado de devolver si un objeto es igual a la instancia de esta clase
     * @param obj Objeto que voy a comparar con la clase actual
     * @return Valor devuelto TRUE | FALSE
     */
    @Override
    public boolean equals(Object obj) {
        // Caso base devuelvo TRUE
        if (this == obj) return true;

        // En caso de que el objeto sea NULL o que no sea de la misma CLASE devuelvo FALSE
        if (obj == null || getClass() != obj.getClass()) return false;

        // En otro caso realizo un CAST del objeto como un FicheroGc
        FicheroGc that = (FicheroGc) obj;

        // Devuelvo la comparación
        return comparar(that);
    }

    /**
     * Metodo utlizado para comparar un objeto FicheroGc con la instancia actual de la clase
     * @param ficheroGcEntity Objeto que voy a comparar con la instancia actual de la clase
     * @return Devuelvo TRUE | FALSE si los objetos son iguales
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
     * Método hashCode coherente con equalsIgnoreCase utilizado en equals()
     * @return código hash del objeto
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
     * Método que devuelve una cadena de caracteres con la representación del objeto
     * @return Cadena de caracteres con la representación del objeto
     */
    @Override
    public String toString() {
        return
                this.shortName + "; " +
                        this.longName + "; " +
                        this.version + "; " +
                        this.canonicalUri + "; " +
                        this.canonicalVersionUri + "; " +
                        this.locationUri;
    }

    /**
     * Método que se tiene que implementar al extender la clase Actualizable. Devuelve el campo único que servirá como
     * Key para el Map
     * @return Devuelve el valor del campo único
     */
    @Override
    public String getUniqueKey() {
        // Devuelve el valor del campo único (tiene definido un índice de tipo UNIQUE)
        return this.shortName;
    }

    /**
     * Método que actualiza la instancia actual de FicheroGc con otro valor
     * @param otro El objeto que actualizará la instancia actual de FicheroGc
     */
    @Override
    public void actualizarCon(FicheroGc otro) {
        // Actualiza los campos de este objeto con los valores del objeto otro
        this.longName = otro.getLongName();
        this.version = otro.getVersion();
        this.canonicalUri = otro.getCanonicalUri();
        this.canonicalVersionUri = otro.getCanonicalVersionUri();
        this.locationUri = otro.getLocationUri();
    }
}
