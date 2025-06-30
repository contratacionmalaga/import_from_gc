package local.jarios.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Enum que representa las rutas de los ficheros de configuración usados en el sistema.
 * <p>
 * Proporciona la ruta a los distintos archivos de propiedades que el sistema
 * debe cargar, como configuración general, Hibernate, correo, y release.
 * </p>
 * <p>
 * Autor: Juan Antonio Ríos Peláez<br>
 * Fecha: 03/03/2024<br>
 * Equipo: Contratación Electrónica
 * </p>
 */
@Getter
public enum PropertyFile {

    /** Archivo de configuración general */
    PROPERTY_CONFIG("config/app.properties"),

    /** Archivo de configuración de Hibernate */
    PROPERTY_HIBERNATE("config/hibernate.properties"),

    /** Archivo de configuración de correo */
    PROPERTY_MAIL("config/mail.properties");

    /** Ruta asociada al fichero de propiedades */
    private final String ruta;

    /**
     * Constructor del enumerado para asignar la ruta.
     *
     * @param ruta Ruta que se asignará a la constante del enumerado.
     */
    PropertyFile(String ruta) {
        this.ruta = ruta;
    }

    /**
     * Devuelve la lista con todas las rutas de los ficheros de configuración definidos.
     *
     * @return Lista de rutas de ficheros de configuración.
     */
    public static List<String> getAllFilePaths() {
        List<String> paths = new ArrayList<>();
        for (PropertyFile file : PropertyFile.values()) {
            paths.add(file.getRuta());
        }
        return paths;
    }
}
