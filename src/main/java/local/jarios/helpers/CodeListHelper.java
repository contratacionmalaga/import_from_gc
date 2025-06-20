package local.jarios.helpers;

import local.jarios.entity.FicheroGc;
import local.jarios.entity.Log;
import local.jarios.exceptions.MiParseException;
import local.jarios.genericode.CodeList;
import local.jarios.mappers.MapperFicheroGcFromCodeList;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.File;

/**
 * Clase helper encargada de procesar ficheros XML codificados como CodeList y convertirlos en entidades de dominio.
 * Ofrece utilidades para deserializar y mapear archivos con formato estándar XML GenCode.
 *
 * @author Juan
 * @since 04/02/2025
 */
@Slf4j
public final class CodeListHelper {

    /**
     * Constructor privado para evitar instanciación.
     */
    private CodeListHelper() { }

    /**
     * Convierte un fichero XML en un objeto {@link CodeList} usando JAXB.
     *
     * @param file Fichero XML con estructura válida GenCode.
     * @return Objeto {@link CodeList} deserializado desde el fichero.
     * @throws MiParseException Si ocurre un error durante el parseo.
     */
    public static CodeList getCodeListFromFile(File file) throws MiParseException {
        if (file == null || !file.exists()) {
            log.error("Fichero nulo o no encontrado al intentar parsear.");
            throw new MiParseException("El fichero es nulo o no existe.");
        }

        try {
            log.debug("Iniciando parseo del fichero: {}", file.getAbsolutePath());

            JAXBContext jc = JAXBContext.newInstance(CodeList.class);
            var unmarshaller = jc.createUnmarshaller();
            CodeList codeList = (CodeList) unmarshaller.unmarshal(file);

            log.debug("Fichero '{}' parseado correctamente.", file.getName());
            return codeList;

        } catch (JAXBException ex) {
            log.error("Error al parsear el fichero '{}': {}", file.getName(), ex.getMessage(), ex);
            throw new MiParseException("Error al parsear el fichero: " + file.getName(), ex);
        }
    }

    /**
     * Procesa un objeto {@link CodeList} para convertirlo en una entidad {@link FicheroGc}.
     *
     * @param logEntity Objeto {@link Log} para trazabilidad.
     * @param codeList Objeto {@link CodeList} con la información parseada del fichero.
     * @return Objeto {@link FicheroGc} generado, o {@code null} si no se pudo mapear.
     */
    public static FicheroGc getFicheroGc(Log logEntity, CodeList codeList) {
        if (codeList == null) {
            log.warn("Se recibió un CodeList nulo. No se procesará.");
            return null;
        }

        log.debug("Procesando CodeList para generar FicheroGc...");

        FicheroGc ficheroGcEntity = MapperFicheroGcFromCodeList.getFicheroGcFromCodeList(logEntity, codeList);

        if (ficheroGcEntity == null) {
            log.warn("No se pudo mapear el CodeList a FicheroGc.");
            return null;
        }

        ficheroGcEntity.setLogEntity(logEntity);
        log.debug("FicheroGc generado correctamente desde CodeList. ShortName: {}", ficheroGcEntity.getShortName());

        return ficheroGcEntity;
    }
}
