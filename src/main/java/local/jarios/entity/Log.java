package local.jarios.entity;

import com.fasterxml.uuid.Generators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Description: Entidad Log para auditorías y relaciones con FicheroGc y Estadistica
 * Author: Juan Antonio
 * Date: 04/06/2024
 * Team: Juan Antonio
 */

@Setter
@Getter
@Entity
@Table(name = "log")
public class Log extends Auditable {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    // Relación OneToMany con FicheroGc, en la entidad FicheroGc está el atributo logEntity
    @OneToMany(mappedBy = "logEntity", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<FicheroGc> ficherosGc = new ArrayList<>();

    // Relación OneToOne con Estadistica, en Estadistica está el atributo logEntity
    @OneToOne(mappedBy = "logEntity", orphanRemoval = true, cascade = CascadeType.ALL)
    private Estadistica estadistica;

    @Override
    public String toString() {
        return this.id.toString();
    }

    // Constructor que genera un UUID basado en tiempo
    public Log() {
        this.id = Generators.timeBasedEpochGenerator().generate();
    }
}
