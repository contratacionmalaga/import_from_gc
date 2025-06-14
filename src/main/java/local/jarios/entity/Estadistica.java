package local.jarios.entity;

import com.fasterxml.uuid.Generators;
import jakarta.persistence.*;
import local.jarios.exceptions.MiUnknownHostException;
import local.jarios.helpers.ComunHelper;
import local.jarios.utils.TamanoCampos;
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

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "log_id",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_estadistica_log",
                    foreignKeyDefinition = "FOREIGN KEY (log_id) REFERENCES log(id) ON DELETE CASCADE")
    )
    private Log logEntity;

    @Column(name = "equipo", nullable = false, length = TamanoCampos.TAMANO_250)
    private String equipo;

    @Column(name = "nTotalFicherosLeidos")
    private int nTotalFicherosLeidos;

    @Column(name = "nTotalFicherosProcesados")
    private int nTotalFicherosProcesados;

    @Column(name = "nRegistrosGc") // corregido typo
    private int nRegistrosGc;

    @Column(name = "fechaHoraInicialParseo", nullable = false)
    private Timestamp fechaHoraInicialParseo;

    @Column(name = "fechaHoraFinalParseo", nullable = false)
    private Timestamp fechaHoraFinalParseo;

    @Column(name = "fechaHoraInicialBaseDatos", nullable = false)
    private Timestamp fechaHoraInicialBaseDatos;

    @Column(name = "fechaHoraFinalBaseDatos", nullable = false)
    private Timestamp fechaHoraFinalBaseDatos;

    @Column(name = "duracionParseo", nullable = false, length = TamanoCampos.TAMANO_250)
    private String duracionParseo;

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
        log.info("Estadistica creada con ID: {}, equipo: {}, inicio parseo: {}", id, equipo, fechaHoraInicialParseo);
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
