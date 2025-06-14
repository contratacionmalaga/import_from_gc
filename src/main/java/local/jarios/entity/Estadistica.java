package local.jarios.entity;

import com.fasterxml.uuid.Generators;
import jakarta.persistence.*;
import local.jarios.exceptions.MiUnknownHostException;
import local.jarios.helpers.ComunHelper;
import local.jarios.utils.TamanoCampos;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

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

    public Estadistica(Log logEntity) throws MiUnknownHostException {
        this.id = Generators.timeBasedEpochGenerator().generate();
        this.logEntity = logEntity;
        this.fechaHoraInicialParseo = Timestamp.from(Instant.now());
        this.equipo = ComunHelper.getHostName();
    }
}
