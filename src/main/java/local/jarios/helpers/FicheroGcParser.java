package local.jarios.helpers;

import java.io.File;
import java.util.List;
import java.util.Map;
import local.jarios.entity.Estadistica;
import local.jarios.entity.FicheroGc;
import local.jarios.entity.Log;
import local.jarios.entity.ParseoFicherosGc;
import local.jarios.entity.RegistroGc;
import local.jarios.genericode.CodeList;
import local.jarios.mappers.MapperRegistroGcFromCodeList;
import lombok.extern.slf4j.Slf4j;

/** Parsea ficheros GC y construye la estructura lista para persistencia. */
@Slf4j
public class FicheroGcParser {

  /** Constructor sin argumentos. */
  public FicheroGcParser() {}

  /**
   * Procesa todos los ficheros del directorio indicado.
   *
   * @param miLog log asociado a la ejecucion actual
   * @param arrayFicherosDirectorio ficheros de entrada
   * @param estadistica datos acumulados de ejecucion
   * @return resultado procesado listo para persistencia
   */
  public ParseoFicherosGc parsearFicheros(
      Log miLog, File[] arrayFicherosDirectorio, Estadistica estadistica) {
    log.info("[parsearFicheros] Numero de ficheros: {}", arrayFicherosDirectorio.length);

    ParseoFicherosGc parseoFicherosGc = new ParseoFicherosGc();
    for (File fichero : arrayFicherosDirectorio) {
      procesarFichero(miLog, estadistica, parseoFicherosGc, fichero);
    }

    return parseoFicherosGc;
  }

  /**
   * Procesa un fichero valido y lo anade al resultado acumulado.
   *
   * @param miLog log asociado a la ejecucion actual
   * @param estadistica datos acumulados de ejecucion
   * @param parseoFicherosGc resultado acumulado del parseo
   * @param fichero fichero de entrada a procesar
   */
  private void procesarFichero(
      Log miLog, Estadistica estadistica, ParseoFicherosGc parseoFicherosGc, File fichero) {
    String nombreFichero = fichero.getName();
    log.info("[parsearFicheros] Procesando fichero '{}'.", nombreFichero);

    if (FileHelper.isInvalidFile(fichero)) {
      log.info("[parsearFicheros] Fichero invalido, se ignora.");
      return;
    }

    CodeList codeList = CodeListHelper.getCodeListFromFile(fichero);
    log.info("[parsearFicheros] Codigo extraido correctamente.");

    FicheroGc ficheroGc = CodeListHelper.getFicheroGc(miLog, codeList);
    log.info("[parsearFicheros] FicheroGc obtenido.");

    if (ficheroGc == null) {
      log.info("[parsearFicheros] FicheroGc es NULL, ignorado.");
      return;
    }

    List<RegistroGc> listRegistroGc =
        MapperRegistroGcFromCodeList.getListRegistroGcFromCodeList(codeList, miLog.getId());
    estadistica.aumentarNRegistrosGc(listRegistroGc.size());
    log.info("[parsearFicheros] List<RegistroGc> {}", listRegistroGc.size());

    parseoFicherosGc.getListFicherosGc().add(ficheroGc);
    log.info("[parsearFicheros] FicheroGc anadido al objeto ParseoFicherosGc.");

    Map<String, List<RegistroGc>> mapFicherosGc = parseoFicherosGc.getMapRegistrosGcByFicheroGc();
    log.info("[parsearFicheros] Obtengo el Map de FicherosGc.");
    mapFicherosGc.put(ficheroGc.getShortName(), listRegistroGc);
    log.debug("[procesarFicheroGc] FicheroGc y registros anadidos al mapa correctamente.");
  }
}
