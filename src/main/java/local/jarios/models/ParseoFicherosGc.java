package local.jarios.models;

import local.jarios.entity.Auditable;
import local.jarios.entity.FicheroGc;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase para manejar las importaciones de ficheros Excel desde Internet,
 * agrupando los ficheros y sus registros asociados.
 * <p>
 * Esta clase extiende {@link Auditable} para heredar propiedades de auditoría.
 * </p>
 *
 * @author Juan Antonio
 * @since 04/06/2024
 * @author Juan Antonio
 */
@Setter
@Getter
public class ParseoFicherosGc extends Auditable {

    private static final Logger log = LoggerFactory.getLogger(ParseoFicherosGc.class);

    /**
     * Lista de objetos {@link FicheroGc} importados.
     */
    private List<FicheroGc> listFicherosGc;

    /**
     * Mapa que relaciona la clave (por ejemplo nombre o id) de un fichero {@link FicheroGc}
     * con la lista de registros asociados {@link RegistroGc}.
     */
    private Map<String, List<RegistroGc>> mapRegistrosGcByFicheroGc;

    /**
     * Constructor por defecto.
     * Inicializa las listas y mapas internos.
     * También registra la creación de la instancia en el log.
     */
    public ParseoFicherosGc() {
        this.listFicherosGc = new ArrayList<>();
        this.mapRegistrosGcByFicheroGc = new HashMap<>();
        log.debug("Instancia de ParseoFicherosGc creada. Listas y mapas inicializados.");
    }

    /**
     * Añade un fichero {@link FicheroGc} a la lista interna.
     *
     * @param fichero el fichero a añadir
     */
    public void addFicheroGc(FicheroGc fichero) {
        this.listFicherosGc.add(fichero);
        log.debug("FicheroGc añadido: {}", fichero);
    }

    /**
     * Añade un registro {@link RegistroGc} a la lista asociada a un fichero identificado por clave.
     *
     * @param claveFichero clave identificadora del fichero
     * @param registro registro a añadir
     */
    public void addRegistroGc(String claveFichero, RegistroGc registro) {
        this.mapRegistrosGcByFicheroGc
                .computeIfAbsent(claveFichero, k -> new ArrayList<>())
                .add(registro);
        log.debug("RegistroGc añadido para fichero '{}': {}", claveFichero, registro);
    }
}
