package local.jarios.helpers;

import local.jarios.entity.FicheroGc;
import local.jarios.entity.Log;
import local.jarios.entity.ParseoFicherosGc;
import local.jarios.entity.RegistroGc;
import local.jarios.genericode.CodeList;
import local.jarios.mappers.MapperRegistroGcFromCodeList;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Clase encargada de parsear ficheros desde un directorio y construir la estructura {@link ParseoFicherosGc}
 * con la información necesaria para su posterior procesamiento y persistencia.
 */
@Slf4j
public class FicheroGcParser {

    /**
     * Constructor que recibe el procesador para procesar ficheros nuevos.
     *
     */
    public FicheroGcParser() {
        //
    }

    /**
     * Procesa todos los ficheros del directorio indicado.
     * <p>
     * Para cada fichero válido, extrae el {@link CodeList}, obtiene el {@link FicheroGc} asociado y, si es válido,
     *
     * @param miLog                   Log asociado a la ejecución actual.
     * @param arrayFicherosDirectorio Array con los ficheros en el directorio para su procesamiento.
     * @return Objeto {@link ParseoFicherosGc} con la información procesada lista para persistencia.
     */
    public ParseoFicherosGc parsearFicheros(Log miLog, File[] arrayFicherosDirectorio) {

        log.info("[parsearFicheros] - Nº de ficheros: {}", arrayFicherosDirectorio.length);

        ParseoFicherosGc parseoFicherosGc = new ParseoFicherosGc();

        for (File fichero : arrayFicherosDirectorio) {
            String nombreFichero = fichero.getName();
            log.info("[parsearFicheros] - Procesando fichero '{}'.", nombreFichero);

            if (FileHelper.isInvalidFile(fichero)) {
                log.info("[parsearFicheros] - Fichero inválido, se ignora.");
                continue;
            }

            CodeList codeList = CodeListHelper.getCodeListFromFile(fichero);
            log.info("[parsearFicheros] - Código extraído correctamente.");

            FicheroGc ficheroGc = CodeListHelper.getFicheroGc(miLog, codeList);
            log.info("[parsearFicheros] - FicheroGc obtenido.");

            if (ficheroGc != null) {
                // Obtengo la lista de RegistroGc asociada al FicheroGc
                List<RegistroGc> listRegistroGc = MapperRegistroGcFromCodeList.getListRegistroGcFromCodeList(codeList, miLog.getId());
                log.info("[parsearFicheros] - List<RegistroGc> {}", listRegistroGc.size());
                parseoFicherosGc.getListFicherosGc().add(ficheroGc);
                log.info("[parsearFicheros] - FicheroGc añadido a la listFicherosGc en el objeto ParseoFicheroGc.");
                Map<String, List<RegistroGc>> mapFicherosGc = parseoFicherosGc.getMapRegistrosGcByFicheroGc();
                log.info("[parsearFicheros] - Obtengo el Map de FicherosGc.");
                mapFicherosGc.put(ficheroGc.getShortName(), listRegistroGc);
                log.debug("[procesarFicheroGc] - FicheroGc y su List<RegistroGc> añadido al mapa correctamente.");
            } else {
                log.info("[parsearFicheros] - FicheroGc es NULL, ignorado.");
            }
        }

        return parseoFicherosGc;
    }
}
