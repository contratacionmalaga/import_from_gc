package local.jarios;

import local.jarios.email.exception.EmailException;
import local.jarios.entity.Estadistica;
import local.jarios.entity.FicheroGc;
import local.jarios.entity.Log;
import local.jarios.enums.TipoFinalEjecucion;
import local.jarios.exceptions.MiUnknownHostException;
import local.jarios.helpers.ComunHelper;
import local.jarios.helpers.FileHelper;
import local.jarios.helpers.ListHelper;
import local.jarios.models.ParseoFicherosGc;
import local.jarios.properties.PropertyConstantes;
import local.jarios.properties.config.PropertiesManager;
import local.jarios.services.Service;
import local.jarios.services.ServiceImpl;
import local.jarios.utils.Constantes;
import local.jarios.utils.FinalDelPrograma;
import local.jarios.utils.Mensajes;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Clase principal para la importación de información desde ficheros Excel al sistema.
 * <p>
 * Se encarga de leer ficheros GC, procesarlos, almacenar estadísticas y persistir los datos en base de datos.
 * Controla la gestión de excepciones y trazas para facilitar el diagnóstico.
 * </p>
 *
 * @author Juan Antonio
 * @since 04/06/2024
 */
@Slf4j
public class ImportFromGc {

    /**
     * Constructor sin argumentos.
     */
    public ImportFromGc() {
        // Constructor vacío
    }

    /**
     * Método principal que ejecuta el proceso completo de importación.
     *
     * @param args argumentos de línea de comandos (no usados)
     */
    public static void main(String[] args) {
        try {
            log.info(Mensajes.INICIO);

            // Cargo los ficheros properties utilizando el patrón SINGLETON
            PropertiesManager propertiesManager = PropertiesManager.getInstance();

            log.info("Aplicación: {}", propertiesManager.getProperty(Constantes.CONFIG_PROPERTIES, "config.name"));
            System.out.println("Versión del JAR: " + getVersionFromManifest(ImportFromGc.class));

            System.exit(0);
            // Imprimo el contenido de los ficheros asociados a la configuración Local (solo si debug activo)
            if (log.isDebugEnabled()) {
                propertiesManager.printAllProperties();
            }

            // Creo el objeto Log para esta ejecución
            Log miLog = new Log();
            log.info(Mensajes.LOG_CREACION, miLog);

            // Creo Estadistica ligada al Log creado
            Estadistica estadistica = new Estadistica(miLog);
            log.info(Mensajes.ESTADISTICA_CREACION);

            // Creo el servicio para interacción con la base de datos
            log.info(Mensajes.SERVICE_CREACION_INICIO);
            Service service = new ServiceImpl(propertiesManager);
            log.info(Mensajes.SERVICE_CREACION_CREADO);

            // Inicio del parseo de ficheros GC
            Timestamp timestampInicioParseo = Timestamp.valueOf(LocalDateTime.now());
            estadistica.setFechaHoraInicialParseo(timestampInicioParseo);
            log.info(Mensajes.ASIGN_FECHA_HORA_INICIAL_PARSEO_TO_ESTADISTICA, timestampInicioParseo);

            // Obtengo lista de ficheros GC ya existentes en base de datos
            List<FicheroGc> listFicherosGcEnBaseDatos = service.getListFicherosGc();

            // Obtengo la ruta de los ficheros a parsear desde configuración
            var path = propertiesManager.getProperty(Constantes.CONFIG_PROPERTIES, PropertyConstantes.CONFIG_PATH);
            log.info(Mensajes.RUTA_FICHEROS, path);

            // Obtengo el listado de ficheros en la ruta
            File[] arrayFiles = FileHelper.getListaFicherosFromPath(path);

            // Almaceno la cantidad de ficheros encontrados en estadística
            int nFilesLeidos = arrayFiles.length;
            estadistica.setNTotalFicherosLeidos(nFilesLeidos);
            log.info(Mensajes.NUEMRO_FICHEROS_LEIDOS, nFilesLeidos, path);

            // Creo objeto para almacenar el parseo
            ParseoFicherosGc parseoFicherosGc = new ParseoFicherosGc();

            if (nFilesLeidos > 0) {
                // Proceso la lista de ficheros
                parseoFicherosGc = FileHelper.procesarListaFicherosFromPath(miLog, arrayFiles);
                log.debug("Procesados {} ficheros GC desde la ruta: {}", parseoFicherosGc.getListFicherosGc().size(), path);
            } else {
                log.warn("No se encontraron ficheros para procesar en la ruta: {}", path);
            }

            // Unifico la lista de ficheros existentes con los nuevos parseados
            ListHelper.unificarListas(miLog, listFicherosGcEnBaseDatos, parseoFicherosGc.getListFicherosGc());

            // Asigno la lista unificada al Log
            miLog.setFicherosGc(listFicherosGcEnBaseDatos);

            // Registro fecha final del parseo
            Timestamp timestampFinParseo = Timestamp.valueOf(LocalDateTime.now());
            estadistica.setFechaHoraFinalParseo(timestampFinParseo);
            log.info(Mensajes.ASIGN_FECHA_HORA_FINAL_PARSEO_TO_ESTADISTICA, timestampFinParseo);

            // Actualizo estadísticas
            int nFicherosProcesados = (parseoFicherosGc.getListFicherosGc() != null) ? parseoFicherosGc.getListFicherosGc().size() : 0;
            estadistica.setNTotalFicherosProcesados(nFicherosProcesados);
            log.info(Mensajes.NUEMRO_FICHEROS_PROCESADOS, nFicherosProcesados, path);

            int nRegistrosGc = (parseoFicherosGc.getMapRegistrosGcByFicheroGc() != null)
                    ? parseoFicherosGc.getMapRegistrosGcByFicheroGc().values().stream()
                    .mapToInt(List::size)
                    .sum()
                    : 0;
            estadistica.setNRegistrosGc(nRegistrosGc);
            log.info(Mensajes.NUEMRO_REGISTROS_GC, nRegistrosGc);

            // Calculo duración del parseo
            String duracionParseo = ComunHelper.calcularTiempoEjecucion(
                    estadistica.getFechaHoraInicialParseo(),
                    estadistica.getFechaHoraFinalParseo());
            estadistica.setDuracionParseo(duracionParseo);

            // Inicio persistencia en base de datos
            Timestamp timestampInicioBD = Timestamp.valueOf(LocalDateTime.now());
            estadistica.setFechaHoraInicialBaseDatos(timestampInicioBD);
            log.info(Mensajes.ASIGN_FECHA_HORA_INICIAL_BASE_DATOS_TO_ESTADISTICA, timestampInicioBD);

            // Persisto los objetos
            service.persistir(miLog);
            log.info(Mensajes.PERSISTIDO_LOG);

            service.persistir(listFicherosGcEnBaseDatos);
            log.info(Mensajes.PERSISTIDO_FICHEROS_GC);

            service.persistir(parseoFicherosGc, propertiesManager);
            log.info(Mensajes.PERSISTIDO_PARSEO_FICHEROS_GC);

            // Final persistencia
            Timestamp timestampFinBD = Timestamp.valueOf(LocalDateTime.now());
            estadistica.setFechaHoraFinalBaseDatos(timestampFinBD);
            log.info(Mensajes.ASIGN_FECHA_HORA_FINAL_BASE_DATOS_TO_ESTADISTICA, ComunHelper.getFechaHoraFormateada(timestampFinBD));

            // Calculo duración persistencia
            String duracionBaseDatos = ComunHelper.calcularTiempoEjecucion(
                    estadistica.getFechaHoraInicialBaseDatos(),
                    estadistica.getFechaHoraFinalBaseDatos());
            estadistica.setDuracionBaseDatos(duracionBaseDatos);
            log.info(Mensajes.ASIGN_DURACION_BASE_DATOS, duracionBaseDatos);

            // Persisto estadísticas finales
            service.persistir(estadistica);
            log.info(Mensajes.PERSISTIDO_ESTADISTICA);

            // Finalizo el programa correctamente
            FinalDelPrograma.finalizar(TipoFinalEjecucion.CORRECTO);

        } catch (MiUnknownHostException ex) {
            log.error(Mensajes.EXCEPTION);
            log.error(Mensajes.EXCEPTION_MENSAJE, Constantes.TABULADOR_1, ex.getMessage());
            log.error(Mensajes.EXCEPTION_STACK_TRACE, Constantes.TABULADOR_1);
            for (StackTraceElement ste : ex.getStackTrace()) {
                log.error("{}{}", Constantes.TABULADOR_2, ste.toString());
            }
            FinalDelPrograma.finalizar(TipoFinalEjecucion.ERROR);

        } catch (EmailException ex) {
            log.error("Error en envío de email: {}", ex.getMessage());
            for (StackTraceElement ste : ex.getStackTrace()) {
                log.error("{}{}", Constantes.TABULADOR_2, ste.toString());
            }
            FinalDelPrograma.finalizar(TipoFinalEjecucion.ERROR);

        } catch (Exception ex) {
            log.error("Error inesperado: {}", ex.getMessage());
            for (StackTraceElement ste : ex.getStackTrace()) {
                log.error("{}{}", Constantes.TABULADOR_2, ste.toString());
            }
            FinalDelPrograma.finalizar(TipoFinalEjecucion.ERROR);
        }
    }

    /**
     * Obtiene la versión (Implementation-Version) desde el MANIFEST.MF
     * del JAR que contiene la clase especificada.
     *
     * @param clazz Clase de referencia para localizar el JAR
     * @return Versión obtenida del MANIFEST.MF o "Desconocida" si no se encuentra
     */
    public static String getVersionFromManifest(Class<?> clazz) {
        try {
            String className = clazz.getSimpleName() + ".class";
            URL classUrl = clazz.getResource(className);

            if (classUrl == null) {
                return "No se encontró recurso de clase";
            }

            if (!"jar".equals(classUrl.getProtocol())) {
                // Probablemente en entorno desarrollo (no en JAR)
                return "Ejecutando sin JAR (modo desarrollo)";
            }

            JarURLConnection jarConnection = (JarURLConnection) classUrl.openConnection();
            JarFile jarFile = jarConnection.getJarFile();

            Manifest manifest = jarFile.getManifest();
            if (manifest == null) {
                return "No se encontró MANIFEST.MF en el JAR";
            }

            Attributes mainAttributes = manifest.getMainAttributes();
            String version = mainAttributes.getValue("Implementation-Version");

            if (version == null || version.isEmpty()) {
                return "Versión no especificada en MANIFEST.MF";
            }

            return version;

        } catch (IOException e) {
            return "Error leyendo MANIFEST.MF: " + e.getMessage();
        }
    }
}
