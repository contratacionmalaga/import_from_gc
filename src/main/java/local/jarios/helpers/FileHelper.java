package local.jarios.helpers;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;

/** Utilidades para manejo de ficheros de entrada. */
@Slf4j
public final class FileHelper {

  /** Constructor privado de clase utilitaria. */
  private FileHelper() {}

  /**
   * Obtiene todos los ficheros contenidos en un directorio.
   *
   * @param path ruta del directorio a inspeccionar
   * @return ficheros encontrados o array vacio si el path no es valido
   */
  public static File[] getListaFicherosFromPath(String path) {
    log.debug("[getListaFicherosFromPath] Intentando obtener ficheros desde: {}", path);

    Path basePath = Paths.get("").toAbsolutePath().normalize();
    Path resolvedPath = basePath.resolve(path).normalize();

    if (!resolvedPath.startsWith(basePath)) {
      log.warn("[getListaFicherosFromPath] El path '{}' queda fuera del trabajo.", path);
      return new File[0];
    }

    File directorio = resolvedPath.toFile();
    if (!directorio.exists() || !directorio.isDirectory()) {
      log.warn("[getListaFicherosFromPath] El path '{}' no existe o no es directorio.", path);
      return new File[0];
    }

    File[] ficheros = directorio.listFiles();
    int total = ficheros == null ? 0 : ficheros.length;

    log.debug("[getListaFicherosFromPath] Encontrados {} fichero(s) en '{}'.", total, path);
    return ficheros == null ? new File[0] : ficheros;
  }

  /**
   * Indica si un fichero no existe, no es fichero regular o no se puede leer.
   *
   * @param file fichero a validar
   * @return {@code true} si el fichero no puede procesarse
   */
  public static boolean isInvalidFile(File file) {
    if (file == null) {
      log.debug("[isInvalidFile] El fichero es null.");
      return true;
    }

    if (!file.exists()) {
      log.debug("[isInvalidFile] El fichero no existe: {}", file.getAbsolutePath());
      return true;
    }

    if (!file.isFile()) {
      log.debug("[isInvalidFile] El fichero no es un fichero: {}", file.getAbsolutePath());
      return true;
    }

    if (!file.canRead()) {
      log.debug("[isInvalidFile] El fichero no se puede leer: {}", file.getAbsolutePath());
      return true;
    }

    return false;
  }
}
