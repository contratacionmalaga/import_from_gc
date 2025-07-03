package local.jarios.helpers;

import local.jarios.entity.FicheroGc;
import local.jarios.entity.Log;
import local.jarios.genericode.CodeList;
import local.jarios.mappers.MapperRegistroGcFromCodeList;
import local.jarios.models.ParseoFicherosGc;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Map;

/**
 * Clase de utilidad para el manejo y procesamiento de ficheros Gc.
 * <p>
 * Contiene métodos estáticos para:
 * <ul>
 *     <li>Procesar fichero nuevo</li>
 *     <li>Procesar fichero existente</li>
 *     <li>Parseo de fichero Gc</li>
 * </ul>
 * Esta clase no debe ser instanciada.
 */
@Slf4j
public final class FicheroGcHelper {

    /**
     * Constructor privado para evitar instanciación.
     */
    private FicheroGcHelper() {
        // Clase de utilidades - no instanciable
    }

    /**
     * Procesa un fichero que no existe aún en el sistema persistido.
     * Agrega el nuevo fichero a la lista de ficheros a persistir y registra los datos asociados.
     *
     * @param ficheroGc         Objeto {@link FicheroGc} creado a partir del fichero nuevo.
     * @param codeList          Lista de {@link CodeList} extraída del fichero.
     * @param parseoFicherosGc  Estructura que contiene los ficheros y registros procesados.
     */
    public static void procesarFicheroNuevo(
            FicheroGc ficheroGc,
            CodeList codeList,
            ParseoFicherosGc parseoFicherosGc
    ) {
        parseoFicherosGc.getListFicherosGc().add(ficheroGc);
        parseoFicherosGc.getMapRegistrosGcByFicheroGc()
                .put(ficheroGc.getShortName(), MapperRegistroGcFromCodeList.getListRegistroGcFromCodeList(codeList));
        log.info("[procesarFicheroNuevo] - El fichero es nuevo. Se añade para realizar un persist.");
    }

    /**
     * Procesa un fichero que ya existe en el sistema persistido.
     * Compara el fichero nuevo con el existente para detectar si ha sido modificado.
     * Si hay diferencias, se actualiza el fichero existente.
     *
     * @param existente  Fichero persistido previamente en el sistema.
     * @param nuevo      Nuevo objeto {@link FicheroGc} generado a partir del fichero actual.
     * @param parseoFicherosGc Objeto que aglutina la importación
     */
    public static void procesarFicheroExistente(
            FicheroGc existente,
            FicheroGc nuevo,
            ParseoFicherosGc parseoFicherosGc
    ) {
        if (!existente.equals(nuevo)) {
            log.info("[procesarFicheroExistente] - El fichero se encuentra modificado.");
            existente.actualizarCon(nuevo);
            parseoFicherosGc.getListFicherosGc().remove(existente);
            log.info("[procesarFicheroExistente] - Elimino de la lista el antiguo.");
            parseoFicherosGc.getListFicherosGc().add(nuevo);
            log.info("[procesarFicheroExistente] - Elimino de la lista el nuevo.");
        } else {
            log.info("[procesarFicheroExistente] - El fichero no presenta cambios.");
        }
    }

    /**
     * Procesa los ficheros del directorio, comparando con los ya persistidos, y construye
     * una estructura para su posterior almacenamiento en base de datos.
     *
     * @param miLog                     Log asociado a la ejecución actual.
     * @param arrayFicherosDirecotorio  Array con los ficheros en el directorio para su procesamiento.
     * @param mapaPersistidos           Mapa con los ficheros actualmente persistidos en la base de datos.
     * @return {@link ParseoFicherosGc} con los datos procesados listos para persistencia.
     */
    public static ParseoFicherosGc getParseoFicherosGc(
            Log miLog,
            File[] arrayFicherosDirecotorio,
            Map<String, FicheroGc> mapaPersistidos
    ) {

        log.info("[getParseoFicherosGc] - Nº de ficheros: {}", arrayFicherosDirecotorio.length);

        // Creo el objeto encargado de almacenar la información del Parseo para despues persistirla
        ParseoFicherosGc parseoFicherosGc = new ParseoFicherosGc();

        // Procesa todos los ficheros del directorio
        for (File fichero : arrayFicherosDirecotorio) {
            String nombreFichero = fichero.getName();
            log.info("[getParseoFicherosGc] - Procesando fichero '{}'.", nombreFichero);

            boolean isInvalidFile = FileHelper.isInvalidFile(fichero);

            if (isInvalidFile) {
                log.info("[getParseoFicherosGc] - No es un fichero válido y será ignorado.");
            }

            var codeList = CodeListHelper.getCodeListFromFile(fichero);
            log.info("[getParseoFicherosGc] - Obtención correcta de CodeList a partir del fichero.");
            var ficheroGc = CodeListHelper.getFicheroGc(miLog, codeList);
            log.info("[getParseoFicherosGc] - Obtención correcta de FicheroGc a partir del CodeList.");

            if (ficheroGc != null) {
                log.info("[getParseoFicherosGc] - FicheroGc no Nulo.");
                FicheroGc existente = mapaPersistidos.get(nombreFichero);
                if (existente == null) {
                    log.info("[getParseoFicherosGc] - No existe FicheroGc en el MAP.");
                    procesarFicheroNuevo(ficheroGc, codeList, parseoFicherosGc);
                    log.info("[getParseoFicherosGc] - FicheroGc procesado como nuevo correctamente.");
                } else {
                    log.info("[getParseoFicherosGc] - Existe FicheroGc en el MAP.");
                    procesarFicheroExistente(existente, ficheroGc, parseoFicherosGc);
                    log.info("[getParseoFicherosGc] - FicheroGc procesado como existente correctamente.");
                }
            } else {
                log.info("[getParseoFicherosGc] - FicheroGc es null y será ignorado.");
            }
        }

        //
        return parseoFicherosGc;
    }
}
