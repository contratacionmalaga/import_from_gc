package local.jarios.helpers;

import local.jarios.entity.Log;
import local.jarios.mappers.MapperRegistroGcFromCodeList;
import local.jarios.models.ParseoFicherosGc;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * Clase de utilidad para el manejo y procesamiento de ficheros.
 * <p>
 * Contiene métodos estáticos para:
 * <ul>
 *     <li>Listar ficheros de un directorio</li>
 *     <li>Validar si un fichero es correcto</li>
 *     <li>Procesar ficheros y generar resultados estructurados</li>
 * </ul>
 * Esta clase no debe ser instanciada.
 */
@Slf4j
public final class FileHelper {

    /**
     * Constructor privado para evitar instanciación.
     */
    private FileHelper() {
        // Clase de utilidades - no instanciable
    }

    /**
     * Obtiene todos los ficheros contenidos en un directorio.
     *
     * @param path Ruta del directorio a inspeccionar.
     * @return Array de ficheros encontrados. Si el path no es válido, se retorna un array vacío.
     */
    public static File[] getListaFicherosFromPath(String path) {
        log.debug("Intentando obtener ficheros desde la ruta: {}", path);

        File directorio = new File(path);

        if (!directorio.exists() || !directorio.isDirectory()) {
            log.warn("El path '{}' no existe o no es un directorio válido.", path);
            return new File[0];
        }

        File[] ficheros = directorio.listFiles();
        int total = (ficheros != null) ? ficheros.length : 0;

        log.info("Se han encontrado {} fichero(s) en el directorio '{}'.", total, path);
        return (ficheros != null) ? ficheros : new File[0];
    }

    /**
     * Verifica si un fichero es válido para su procesamiento.
     * Un fichero es válido si:
     * <ul>
     *     <li>Existe</li>
     *     <li>Es legible</li>
     * </ul>
     *
     * @param file Objeto {@link File} a verificar.
     * @return {@code true} si es válido, {@code false} en caso contrario.
     */
    public static boolean esFicheroCorrecto(File file) {
        boolean valido = file != null && file.exists() && file.canRead();
        log.debug("Validación del fichero '{}': {}", file != null ? file.getName() : "null", valido ? "Correcto" : "Incorrecto");
        return valido;
    }

    /**
     * Procesa una lista de ficheros y genera un objeto {@link ParseoFicherosGc} con los resultados.
     * <p>
     * Se valida cada fichero antes de procesarlo. Si se puede extraer un objeto válido, se guarda
     * junto con sus registros asociados.
     * </p>
     *
     * @param logEntity Objeto de log utilizado en el proceso de transformación.
     * @param listFiles Lista de ficheros a procesar.
     * @return Objeto {@link ParseoFicherosGc} con la información agregada.
     */
    public static ParseoFicherosGc procesarListaFicherosFromPath(Log logEntity, File[] listFiles) {
        int totalInput = (listFiles != null) ? listFiles.length : 0;
        log.info("Iniciando el procesamiento de {} fichero(s).", totalInput);

        ParseoFicherosGc parseoFicherosGc = new ParseoFicherosGc();

        if (listFiles == null || listFiles.length == 0) {
            log.warn("No se recibieron ficheros para procesar.");
            return parseoFicherosGc;
        }

        for (File file : listFiles) {
            if (!esFicheroCorrecto(file)) {
                log.warn("Fichero '{}' no es válido y será ignorado.", file != null ? file.getName() : "null");
                continue; // único continue permitido
            }

            log.debug("Procesando fichero '{}'.", file.getName());

            var codeList = CodeListHelper.getCodeListFromFile(file);
            var ficheroGcEntity = CodeListHelper.procesarCodeList(logEntity, codeList);

            if (ficheroGcEntity == null) {
                log.warn("El fichero '{}' fue ignorado porque no se pudo procesar correctamente.", file.getName());
            } else {
                parseoFicherosGc.getListFicherosGc().add(ficheroGcEntity);
                parseoFicherosGc
                        .getMapRegistrosGcByFicheroGc()
                        .put(
                                ficheroGcEntity.getShortName(),
                                MapperRegistroGcFromCodeList.getListRegistroGcFromCodeList(codeList)
                        );

                log.info("Fichero '{}' procesado correctamente.", ficheroGcEntity.getShortName());
            }
        }

        int totalProcesados = parseoFicherosGc.getListFicherosGc().size();
        log.info("Finalizado el procesamiento. Total de ficheros procesados correctamente: {}", totalProcesados);

        return parseoFicherosGc;
    }
}
