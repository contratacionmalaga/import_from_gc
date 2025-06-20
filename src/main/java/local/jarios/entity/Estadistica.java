package local.jarios.entity;

import com.fasterxml.uuid.Generators;
import jakarta.persistence.*;
import local.jarios.exceptions.MiUnknownHostException;
import local.jarios.helpers.ComunHelper;
import local.jarios.common.util.TamanoCampos;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.time.Instant;
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
public class Estadistica extends Auditable {

    /**
     * Identificador único del registro.
     * <p>
     * Se mapea a la columna "id" de la tabla en la base de datos.
     * No es actualizable ni nulo.
     * </p>
     */
    @Id
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
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "log_id",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_estadistica_log",
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
     * Fecha y hora del inicio del proceso de parseo.
     * <p>
     * No puede ser nulo.
     * </p>
     */
    @Column(name = "fechaHoraInicialParseo", nullable = false)
    private Timestamp fechaHoraInicialParseo;

    /**
     * Fecha y hora de finalización del proceso de parseo.
     * <p>
     * No puede ser nulo.
     * </p>
     */
    @Column(name = "fechaHoraFinalParseo", nullable = false)
    private Timestamp fechaHoraFinalParseo;

    /**
     * Fecha y hora del inicio del proceso de persistencia en base de datos.
     * <p>
     * No puede ser nulo.
     * </p>
     */
    @Column(name = "fechaHoraInicialBaseDatos", nullable = false)
    private Timestamp fechaHoraInicialBaseDatos;

    /**
     * Fecha y hora de finalización del proceso de persistencia en base de datos.
     * <p>
     * No puede ser nulo.
     * </p>
     */
    @Column(name = "fechaHoraFinalBaseDatos", nullable = false)
    private Timestamp fechaHoraFinalBaseDatos;

    /**
     * Duración total del proceso de parseo, en formato legible.
     * <p>
     * No puede ser nulo y tiene longitud máxima definida.
     * </p>
     */
    @Column(name = "duracionParseo", nullable = false, length = TamanoCampos.TAMANO_250)
    private String duracionParseo;

    /**
     * Duración total del proceso de persistencia en base de datos, en formato legible.
     * <p>
     * No puede ser nulo y tiene longitud máxima definida.
     * </p>
     */
    @Column(name = "duracionBaseDatos", nullable = false, length = TamanoCampos.TAMANO_250)
    private String duracionBaseDatos;


    /**
     * Constructor que inicializa la entidad Estadistica con un Log asociado,
     * asigna un UUID, la fecha/hora inicial del parseo y el nombre del equipo.
     *
     * @param logEntity La entidad Log asociada a esta estadística
     * @throws MiUnknownHostException Si no se puede obtener el nombre del host
     */
    public Estadistica(Log logEntity) throws MiUnknownHostException {
        this.id = Generators.timeBasedEpochGenerator().generate();
        this.logEntity = logEntity;
        this.fechaHoraInicialParseo = Timestamp.from(Instant.now());
        this.equipo = ComunHelper.getHostName();
    }

    /**
     * Registra la fecha y hora de finalización del parseo y calcula la duración en formato hh:mm:ss.SSS.
     * @param fechaHoraFinalParseo Fecha y hora cuando finalizó el parseo
     */
    public void registrarFinParseo(Timestamp fechaHoraFinalParseo) {
        this.fechaHoraFinalParseo = fechaHoraFinalParseo;
        long duracionMs = fechaHoraFinalParseo.getTime() - this.fechaHoraInicialParseo.getTime();
        this.duracionParseo = formatDuracion(duracionMs);
        log.info("Parseo finalizado. Duración: {}", duracionParseo);
    }

    /**
     * Registra las fechas y horas de inicio y fin de la persistencia en base de datos y calcula la duración.
     * @param fechaHoraInicialBaseDatos Fecha y hora de inicio de persistencia
     * @param fechaHoraFinalBaseDatos Fecha y hora de fin de persistencia
     */
    public void registrarDuracionBaseDatos(Timestamp fechaHoraInicialBaseDatos, Timestamp fechaHoraFinalBaseDatos) {
        this.fechaHoraInicialBaseDatos = fechaHoraInicialBaseDatos;
        this.fechaHoraFinalBaseDatos = fechaHoraFinalBaseDatos;
        long duracionMs = fechaHoraFinalBaseDatos.getTime() - fechaHoraInicialBaseDatos.getTime();
        this.duracionBaseDatos = formatDuracion(duracionMs);
        log.info("Persistencia en BD finalizada. Duración: {}", duracionBaseDatos);
    }

    /**
     * Formatea milisegundos a string legible hh:mm:ss.SSS
     * @param duracionMs duración en milisegundos
     * @return duración formateada
     */
    private String formatDuracion(long duracionMs) {
        long horas = duracionMs / 3600000;
        long minutos = (duracionMs % 3600000) / 60000;
        long segundos = (duracionMs % 60000) / 1000;
        long milisegundos = duracionMs % 1000;
        return String.format("%02d:%02d:%02d.%03d", horas, minutos, segundos, milisegundos);
    }
}
