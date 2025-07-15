package local.jarios.entity;

import jakarta.persistence.*;
import local.jarios.common.util.TamanoCampos;
import local.jarios.interfaces.EsActualizable;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * Representa la importación de ficheros Excel desde Internet,
 * con sus metadatos y URIs asociados.
 * <p>
 * Esta clase implementa la interfaz {@link EsActualizable} para permitir
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
        name = "ficheros_gc"
)
public class FicheroGc extends AuditableCreatedAt {

    /**
     * Identificador único del registro.
     * <p>
     * Se mapea a la columna "id" de la tabla en la base de datos.
     * No es actualizable ni nulo.
     * </p>
     */
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Relación muchos a uno con la entidad {@link Log}.
     * <p>
     * Se utiliza carga perezosa (lazy loading). La columna "log_id" es clave
     * foránea referenciando la columna "id" en la tabla log.
     * Se aplica borrado en cascada.
     * </p>
     */
    @ManyToOne(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JoinColumn(
            name = "log_id",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(
                    name = "fk_ficherosgc_log",
                    foreignKeyDefinition = "FOREIGN KEY (log_id) REFERENCES log(id) ON DELETE CASCADE"))
    private Log logEntity;

    /**
     * Nombre corto del fichero GC.
     * <p>
     * Se almacena en la columna "shortName". No puede ser nulo y su
     * longitud máxima está limitada por {@link TamanoCampos#TAMANO_250}.
     * </p>
     */
    @Column(name = "short_name", nullable = false, length = TamanoCampos.TAMANO_250)
    private String shortName;

    /**
     * Nombre largo o descriptivo del fichero GC.
     * <p>
     * Se almacena en la columna "longName". No puede ser nulo y su
     * longitud máxima está limitada por {@link TamanoCampos#TAMANO_250}.
     * </p>
     */
    @Column(name = "long_name", nullable = false, length = TamanoCampos.TAMANO_250)
    private String longName;

    /**
     * Versión del fichero GC.
     * <p>
     * Se almacena en la columna "version". No puede ser nulo y su
     * longitud máxima está limitada por {@link TamanoCampos#TAMANO_250}.
     * </p>
     */
    @Column(name = "version", nullable = false, length = TamanoCampos.TAMANO_250)
    private String version;

    /**
     * URI canónica del fichero GC.
     * <p>
     * Se almacena en la columna "canonicalUri". No puede ser nulo y su
     * longitud máxima está limitada por {@link TamanoCampos#TAMANO_250}.
     * </p>
     */
    @Column(name = "canonical_uri", nullable = false, length = TamanoCampos.TAMANO_250)
    private String canonicalUri;

    /**
     * URI canónica de la versión del fichero GC.
     * <p>
     * Se almacena en la columna "canonicalVersionUri". No puede ser nulo y su
     * longitud máxima está limitada por {@link TamanoCampos#TAMANO_250}.
     * </p>
     */
    @Column(name = "canonical_version_uri", nullable = false, length = TamanoCampos.TAMANO_250)
    private String canonicalVersionUri;

    /**
     * URI de localización del fichero GC.
     * <p>
     * Se almacena en la columna "locationUri". No puede ser nulo y su
     * longitud máxima está limitada por {@link TamanoCampos#TAMANO_250}.
     * </p>
     */
    @Column(name = "location_uri", nullable = false, length = TamanoCampos.TAMANO_250)
    private String locationUri;

    /**
     * Constructor que genera un UUID basado en tiempo y registra la creación del objeto.
     */
    public FicheroGc() {
        this.markAsCreated();
        // Constructor vacío
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
}
