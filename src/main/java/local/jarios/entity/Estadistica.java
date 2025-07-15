package local.jarios.entity;

import jakarta.persistence.*;
import local.jarios.common.util.TamanoCampos;
import local.jarios.exceptions.MiUnknownHostException;
import local.jarios.helpers.ComunHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad que representa las estadísticas de la ejecución de la aplicación.
 * Almacena tiempos, contadores y metadatos relevantes para auditoría y análisis.
 * <p>
 * Author: Juan Antonio<br>
 * Date: 14/06/2025<br>
 * Team: Contratacion Electrónica
 * </p>
 */
@Slf4j
@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "estadistica")
public class Estadistica extends AuditableCreatedAt {

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
     * Relación uno a uno con la entidad {@link Log}.
     * <p>
     * Se utiliza carga perezosa (lazy loading). La columna "log_id" es
     * clave foránea referenciando la columna "id" en la tabla log.
     * Se aplica borrado en cascada.
     * </p>
     */
    @OneToOne(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JoinColumn(
            name = "log_id",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(
                    name = "fk_estadistica_log",
                    foreignKeyDefinition = "FOREIGN KEY (log_id) REFERENCES log(id) ON DELETE CASCADE")
    )
    private Log logEntity;

    /**
     * Nombre del equipo asociado a esta estadística.
     * <p>
     * Se almacena en la columna "equipo". No puede ser nulo y tiene un
     * tamaño máximo definido por {@link TamanoCampos#TAMANO_250}.
     * </p>
     */
    @Column(name = "equipo", nullable = false, length = TamanoCampos.TAMANO_250)
    private String equipo;

    /**
     * Número total de ficheros leídos durante el proceso.
     */
    @Column(name = "nTotalFicherosLeidos")
    private int nTotalFicherosLeidos;

    /**
     * Número total de registros GC procesados.
     */
    @Column(name = "nRegistrosGc")
    private int nRegistrosGc;

    /**
     * Fecha y hora de inicio del proceso de importación.
     */
    @Column(name = "fechaHoraInicial")
    private LocalDateTime fechaHoraInicial;

    /**
     * Fecha y hora de finalización del proceso de importación.
     */
    @Column(name = "fechaHoraFinal")
    private LocalDateTime fechaHoraFinal;

    /**
     * Duración total del proceso de importación en formato legible.
     */
    @Column(name = "duracion", length = TamanoCampos.TAMANO_250)
    private String duracion;


    /**
     * Constructor que inicializa la entidad Estadistica con un Log asociado,
     * asigna un UUID, la fecha/hora inicial del parseo y el nombre del equipo.
     *
     * @param logEntity La entidad Log asociada a esta estadística
     * @throws MiUnknownHostException Si no se puede obtener el nombre del host
     */
    public Estadistica(Log logEntity) throws MiUnknownHostException {
        this.markAsCreated();
        this.logEntity = logEntity;
        this.equipo = ComunHelper.getHostName();
    }

    /**
     * Aumentar el número de regsitros según un valor
     * @param incremento Valor que se incrementa el nRegistrosGc
     */
    public void aumentarNRegistrosGc(int incremento) {
        this.nRegistrosGc += incremento;
    }

    /**
     * Representación en texto del objeto Estadistica con todos sus campos.
     *
     * @return String con la representación del objeto
     */
    @Override
    public String toString() {
        return "Estadistica [" +
                "equipo='" + equipo + "', " +
                "nTotalFicherosLeidos=" + nTotalFicherosLeidos + ", " +
                "nRegistrosGc=" + nRegistrosGc + ", " +
                "fechaHoraInicial=" + ComunHelper.getFechaHoraFormateada(fechaHoraInicial) + ", " +
                "fechaHoraFinal=" + ComunHelper.getFechaHoraFormateada(fechaHoraFinal) + ", " +
                "duracion=" + duracion +
                "createdAt=" + ComunHelper.getFechaHoraFormateada(getCreatedAt()) +
                "]";
    }
}
