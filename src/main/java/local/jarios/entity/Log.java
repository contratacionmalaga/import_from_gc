package local.jarios.entity;

import com.fasterxml.uuid.Generators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entidad Log para auditorías y relaciones con FicheroGc y Estadistica.
 * Representa un log que agrupa ficheros y estadísticas.
 * Author: Juan Antonio
 * Date: 04/06/2024
 * Team: Juan Antonio
 */
@Slf4j
@Setter
@Getter
@Entity
@Table(name = "log")
public class Log extends Auditable {

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
     * Lista de ficheros asociados a este log.
     * Cascada y eliminación en orfanato activados.
     */
    @OneToMany(mappedBy = "logEntity", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<FicheroGc> ficherosGc = new ArrayList<>();

    /**
     * Estadística asociada a este log.
     * Cascada y eliminación en orfanato activados.
     */
    @OneToOne(mappedBy = "logEntity", orphanRemoval = true, cascade = CascadeType.ALL)
    private Estadistica estadistica;

    /**
     * Constructor que genera un UUID basado en tiempo y registra la creación.
     */
    public Log() {
        this.id = Generators.timeBasedEpochGenerator().generate();
        log.info("Creado Log con ID: {}", this.id);
    }

    /**
     * Representación en texto del ID del log.
     *
     * @return cadena con el UUID en formato String
     */
    @Override
    public String toString() {
        return this.id.toString();
    }
}
