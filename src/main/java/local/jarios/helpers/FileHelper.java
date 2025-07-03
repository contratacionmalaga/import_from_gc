package local.jarios.helpers;

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
        log.debug("[getListaFicherosFromPath] - Intentando obtener ficheros desde la ruta: {}", path);

        File directorio = new File(path);

        if (!directorio.exists() || !directorio.isDirectory()) {
            log.warn("[getListaFicherosFromPath] - El path '{}' no existe o no es un directorio válido.", path);
            return new File[0];
        }

        File[] ficheros = directorio.listFiles();
        int total = (ficheros != null) ? ficheros.length : 0;

        log.debug("[getListaFicherosFromPath] - Se han encontrado {} fichero(s) en el directorio '{}'.", total, path);
        return (ficheros != null) ? ficheros : new File[0];
    }

    /**
     * Analiza si un String que se pasa es un File válido (EXISTE, SE PUEDA LEER, .entity..)
     *
     * @param file Fichero con la ruta absoluta
     * @return Devuelve un valor indicando si el fichero es valido y en caso contrario indica el motivo
     */
    public static boolean isInvalidFile(File file) {

        if (file == null) {
            log.debug("[isInvalidFile] - El fichero es null.");
            return true;
        }

        if (!file.exists()) {
            log.debug("[isInvalidFile] - El fichero no existe: {}", file.getAbsolutePath());
            return true;
        }

        if (!file.isFile()) {
            log.debug("[isInvalidFile] - El fichero no es un fichero: {}", file.getAbsolutePath());
            return true;
        }

        if (!file.canRead()) {
            log.debug("[isInvalidFile] - El fichero no se puede leer: {}", file.getAbsolutePath());
            return true;
        }

        return false;
    }
}
