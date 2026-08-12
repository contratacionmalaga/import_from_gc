package local.jarios.enums;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

/** Enum que representa las rutas de los ficheros de configuracion usados por la aplicacion. */
@Getter
public enum PropertyFile {

  /** Archivo de configuracion general. */
  PROPERTY_CONFIG("config/app.properties"),

  /** Archivo de configuracion de Hibernate. */
  PROPERTY_HIBERNATE("config/hibernate.properties"),

  /** Archivo de configuracion de correo. */
  PROPERTY_MAIL("config/mail.properties");

  /** Ruta asociada al fichero de propiedades. */
  private final String ruta;

  /**
   * Constructor del enumerado para asignar la ruta.
   *
   * @param ruta ruta que se asigna a la constante del enumerado
   */
  PropertyFile(String ruta) {
    this.ruta = ruta;
  }

  /**
   * Devuelve la lista con todas las rutas de configuracion definidas.
   *
   * @return lista de rutas de ficheros de configuracion
   */
  public static List<String> getAllFilePaths() {
    List<String> paths = new ArrayList<>();
    for (PropertyFile file : PropertyFile.values()) {
      paths.add(file.getRuta());
    }
    return paths;
  }
}
