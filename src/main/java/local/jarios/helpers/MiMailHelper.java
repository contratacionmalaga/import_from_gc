package local.jarios.helpers;

import local.jarios.entity.Estadistica;
import local.jarios.exceptions.MiUnknownHostException;
import local.jarios.properties.PropertyConstantes;
import local.jarios.properties.config.PropertiesManager;
import local.jarios.utils.Constantes;
import lombok.extern.slf4j.Slf4j;

/**
 * Clase helper encargada de construir mensajes de correo electrónico HTML con estadísticas del procesamiento.
 * También genera el asunto del mensaje.
 *
 * @author Juan
 * @since 2025-02-28
 */
@Slf4j
public final class MiMailHelper {

    private MiMailHelper() {
        // No instanciable
    }

    /**
     * Genera el cuerpo del mensaje HTML con los datos de la ejecución.
     *
     * @param propertiesManager Gestor de propiedades para leer configuraciones.
     * @param estadisticaEntity Objeto que contiene las estadísticas a mostrar.
     * @return Cadena HTML representando el cuerpo del mensaje.
     * @throws MiUnknownHostException Si no se puede obtener el nombre del host.
     */
    public static String getCuerpoMensaje(
            PropertiesManager propertiesManager,
            Estadistica estadisticaEntity
    ) throws MiUnknownHostException {

        log.debug("Generando cuerpo del mensaje de estadísticas...");

        String html = "<!DOCTYPE html>" +
                "<html lang='es'>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<title>Estadísticas</title>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; background-color: #f4f4f9; margin: 0; padding: 0; }" +
                ".container { width: 80%; margin: 0 auto; background-color: #ffffff; " +
                "box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); padding: 20px; }" +
                ".header { background-color: #007bff; color: #ffffff; padding: 10px 0; text-align: center; }" +
                ".content { padding: 20px; }" +
                ".content h2 { color: #333333; }" +
                ".stats-table { width: 100%; border-collapse: collapse; margin-top: 20px; }" +
                ".stats-table th, .stats-table td { padding: 10px; text-align: left; border-bottom: 1px solid #dddddd; }" +
                ".stats-table th { background-color: #f2f2f2; }" +
                ".footer { text-align: center; padding: 10px; font-size: 12px; color: #666666; background-color: #f9f9f9; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'><h1>Reporte de Estadísticas</h1></div>" +
                "<div class='content'>" +
                getTablaEstadisticas(propertiesManager, estadisticaEntity) +
                "</div>" +
                "<div class='footer'><p>Reporte generado automáticamente.</p></div>" +
                "</div>" +
                "</body>" +
                "</html>";

        log.debug("Cuerpo del mensaje generado correctamente.");

        return html;
    }

    /**
     * Construye una tabla HTML con los valores estadísticos y de entorno.
     *
     * @param propertiesManager Gestor de propiedades.
     * @param estadistica Objeto con las estadísticas procesadas.
     * @return Cadena HTML con la tabla de datos.
     * @throws MiUnknownHostException Si no se puede obtener el host local.
     */
    private static String getTablaEstadisticas(
            PropertiesManager propertiesManager,
            Estadistica estadistica
    ) throws MiUnknownHostException {

        log.debug("Construyendo tabla de estadísticas...");

        if (estadistica == null) {
            log.warn("Objeto Estadística recibido es nulo. La tabla será generada vacía.");
            estadistica = new Estadistica(); // Evitamos null
        }

        String table = "<table class='stats-table'>" +
                "<tr><th>Fecha y Hora del Envío del Correo</th><td>" +
                ComunHelper.getFechaHoraFormateada(null) +
                "</td></tr>" +
                "<tr><th>Equipo desde el que se realiza el Envío</th><td>" +
                ComunHelper.getHostName() +
                "</td></tr>" +
                "<tr><th>URL de la conexión a la base de datos local</th><td>" +
                propertiesManager.getProperty(Constantes.HIBERNATE_PROPERTIES, PropertyConstantes.LOCAL_URL) +
                "</td></tr>" +
                "<tr><th>Número de ficheros en la carpeta</th><td>" +
                estadistica.getNTotalFicherosLeidos() +
                "</td></tr>" +
                "<tr><th>Número de ficheros procesados</th><td>" +
                estadistica.getNTotalFicherosProcesados() +
                "</td></tr>" +
                "<tr><th>Tiempo de ejecución (Parseo)</th><td>" +
                estadistica.getDuracionParseo() +
                "</td></tr>" +
                "<tr><th>Tiempo de ejecución (Persistencia en BD)</th><td>" +
                estadistica.getDuracionBaseDatos() +
                "</td></tr>" +
                "</table>";

        log.debug("Tabla de estadísticas generada correctamente.");
        return table;
    }

    /**
     * Genera el asunto del correo con nombre del equipo y timestamp.
     *
     * @param propertiesManager Gestor de propiedades para obtener el nombre del sistema.
     * @return Asunto del correo electrónico.
     * @throws MiUnknownHostException Si no se puede obtener el nombre del host.
     */
    public static String getAsunto(PropertiesManager propertiesManager) throws MiUnknownHostException {
        log.debug("Generando asunto del correo...");

        String asunto = String.format(
                "%s - Reporte de Estadísticas. Equipo: (%s). Fecha y hora: (%s)",
                propertiesManager.getProperty(Constantes.CONFIG_PROPERTIES, PropertyConstantes.CONFIG_NAME),
                ComunHelper.getHostName(),
                ComunHelper.getFechaHoraFormateada(null)
        );

        log.info("Asunto del correo generado: {}", asunto);
        return asunto;
    }
}
