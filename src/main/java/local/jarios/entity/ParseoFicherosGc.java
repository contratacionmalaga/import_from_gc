package local.jarios.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/** Agrupa ficheros GC parseados y sus registros asociados. */
@Setter
@Getter
@Slf4j
public class ParseoFicherosGc extends AuditableCreatedAt {

  /** Lista de ficheros GC importados. */
  private List<FicheroGc> listFicherosGc;

  /** Mapa de registros por clave de fichero GC. */
  private Map<String, List<RegistroGc>> mapRegistrosGcByFicheroGc;

  /** Constructor que inicializa las colecciones internas. */
  public ParseoFicherosGc() {
    this.listFicherosGc = new ArrayList<>();
    this.mapRegistrosGcByFicheroGc = new HashMap<>();
    log.debug("Instancia de ParseoFicherosGc creada. Listas y mapas inicializados.");
  }

  /**
   * Anade un fichero GC a la lista interna.
   *
   * @param fichero fichero a anadir
   */
  public void addFicheroGc(FicheroGc fichero) {
    this.listFicherosGc.add(fichero);
    log.debug("FicheroGc anadido: {}", fichero);
  }

  /**
   * Anade un registro a la lista asociada a un fichero identificado por clave.
   *
   * @param claveFichero clave identificadora del fichero
   * @param registro registro a anadir
   */
  public void addRegistroGc(String claveFichero, RegistroGc registro) {
    this.mapRegistrosGcByFicheroGc
        .computeIfAbsent(claveFichero, k -> new ArrayList<>())
        .add(registro);
    log.debug("RegistroGc anadido para fichero '{}': {}", claveFichero, registro);
  }
}
