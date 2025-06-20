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
        log.debug("Intentando obtener ficheros desde la ruta: {}", path);

        File directorio = new File(path);

        if (!directorio.exists() || !directorio.isDirectory()) {
            log.warn("El path '{}' no existe o no es un directorio válido.", path);
            return new File[0];
        }

        File[] ficheros = directorio.listFiles();
        int total = (ficheros != null) ? ficheros.length : 0;

        log.debug("Se han encontrado {} fichero(s) en el directorio '{}'.", total, path);
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
    public static boolean esIncorrectoFichero(File file) {
        boolean valido = file == null || !file.exists() || !file.canRead();
        log.debug(
                "Validación del fichero '{}': {}",
                file != null ? file.getName() : "null",
                valido ? "Correcto" : "Incorrecto");
        return valido;
    }
}
