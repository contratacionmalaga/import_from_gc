package local.jarios.helpers;

import local.jarios.common.util.Constantes;
import local.jarios.entity.Estadistica;
import local.jarios.exceptions.MiUnknownHostException;
import local.jarios.properties.PropertyConstantes;
import local.jarios.properties.api.PropertiesManagerService;
import local.jarios.properties.api.PropertiesManagerServiceImpl;
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

    /**
     * Constructor privado para evitar la instanciación de la clase utilitaria {@code MiMailHelper}.
     */
    private MiMailHelper() {
        // No instanciable
    }

    /**
     * Genera el cuerpo del mensaje HTML con los datos de la ejecución.
     *
     * @param estadisticaEntity Objeto que contiene las estadísticas a mostrar.
     * @return Cadena HTML representando el cuerpo del mensaje.
     * @throws MiUnknownHostException Si no se puede obtener el nombre del host.
     */
    public static String getCuerpoMensaje(
            Estadistica estadisticaEntity
    ) throws MiUnknownHostException {

        log.debug("[getCuerpoMensaje] - Generando cuerpo del mensaje de estadísticas...");
        //
        PropertiesManagerService propertiesManager = PropertiesManagerServiceImpl.getInstance();
        log.debug("[getCuerpoMensaje] - El servicio de consulta de los ficheros properties se ha creado correctamente.");

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
                getTablaEstadisticas(estadisticaEntity) +
                "</div>" +
                "<div class='footer'><p>Reporte generado automáticamente.</p></div>" +
                "</div>" +
                "</body>" +
                "</html>";

        log.debug("[getCuerpoMensaje] - Cuerpo del mensaje generado correctamente.");

        return html;
    }

    /**
     * Construye una tabla HTML con los valores estadísticos y de entorno.
     *
     * @param estadistica Objeto con las estadísticas procesadas.
     * @return Cadena HTML con la tabla de datos.
     * @throws MiUnknownHostException Si no se puede obtener el host local.
     */
    private static String getTablaEstadisticas(
            Estadistica estadistica
    ) throws MiUnknownHostException {

        log.debug("[getTablaEstadisticas] - Construyendo tabla de estadísticas...");

        //
        PropertiesManagerService propertiesManager = PropertiesManagerServiceImpl.getInstance();
        log.debug("[getTablaEstadisticas] - El servicio de consulta de los ficheros properties se ha creado correctamente.");

        if (estadistica == null) {
            log.debug("[getTablaEstadisticas] - Objeto Estadística recibido es nulo. La tabla será generada vacía.");
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
                "<tr><th>Tiempo de ejecución (Parseo)</th><td>" +
                estadistica.getDuracionParseo() +
                "</td></tr>" +
                "<tr><th>Tiempo de ejecución (Persistencia en BD)</th><td>" +
                estadistica.getDuracionBaseDatos() +
                "</td></tr>" +
                "</table>";

        log.debug("[getTablaEstadisticas] - Tabla de estadísticas generada correctamente.");

        return table;
    }

    /**
     * Genera el asunto del correo con nombre del equipo y timestamp.
     *
     * @return Asunto del correo electrónico.
     * @throws MiUnknownHostException Si no se puede obtener el nombre del host.
     */
    public static String getAsunto() throws MiUnknownHostException {

        log.debug("[getAsunto] -Generando asunto del correo...");

        //
        PropertiesManagerService propertiesManager = PropertiesManagerServiceImpl.getInstance();
        log.debug("[getAsunto] - El servicio de consulta de los ficheros properties se ha creado correctamente.");

        String asunto = String.format(
                "%s - Reporte de Estadísticas. Equipo: (%s). Fecha y hora: (%s)",
                propertiesManager.getProperty(Constantes.APP_PROPERTIES, PropertyConstantes.APP_NAME),
                ComunHelper.getHostName(),
                ComunHelper.getFechaHoraFormateada(null)
        );
        log.debug("Asunto del correo generado: {}", asunto);

        return asunto;
    }

    /**
     * Construye un cuerpo de mensaje en formato HTML con información detallada de una excepción.
     * <p>
     * Incluye la clase de excepción, el mensaje y la traza completa con sangría para mejor lectura.
     * </p>
     *
     * @param ex Excepción de la cual extraer la información.
     * @return Cadena con el cuerpo HTML preparado para el email.
     */
    public static String buildHtmlExceptionBody(Exception ex) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");
        sb.append("<h2>Se ha producido una excepción:</h2>");
        sb.append("<p><strong>Tipo:</strong> ").append(ex.getClass().getName()).append("</p>");
        sb.append("<p><strong>Mensaje:</strong> ").append(ex.getMessage()).append("</p>");
        sb.append("<pre>");

        for (StackTraceElement ste : ex.getStackTrace()) {
            sb.append("&nbsp;&nbsp;&nbsp;&nbsp;").append(ste.toString()).append("<br>");
        }

        sb.append("</pre>");
        sb.append("</body></html>");
        return sb.toString();
    }
}
