package local.jarios.helpers;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import java.io.File;
import local.jarios.entity.FicheroGc;
import local.jarios.entity.Log;
import local.jarios.exceptions.MiParseException;
import local.jarios.genericode.CodeList;
import local.jarios.mappers.MapperFicheroGcFromCodeList;
import lombok.extern.slf4j.Slf4j;

/** Procesa ficheros XML CodeList y los convierte en entidades de dominio. */
@Slf4j
public final class CodeListHelper {

  /** Constructor privado de clase utilitaria. */
  private CodeListHelper() {}

  /**
   * Convierte un fichero XML en un objeto {@link CodeList} usando JAXB.
   *
   * @param file fichero XML con estructura valida genericode
   * @return objeto {@link CodeList} deserializado desde el fichero
   * @throws MiParseException si ocurre un error durante el parseo
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
   * Convierte un objeto {@link CodeList} en una entidad {@link FicheroGc}.
   *
   * @param logEntity log usado para trazabilidad
   * @param codeList objeto con la informacion parseada del fichero
   * @return objeto {@link FicheroGc} generado, o {@code null} si no se pudo mapear
   */
  public static FicheroGc getFicheroGc(Log logEntity, CodeList codeList) {
    if (codeList == null) {
      log.warn("Se recibio un CodeList nulo. No se procesara.");
      return null;
    }

    log.debug("Procesando CodeList para generar FicheroGc...");
    FicheroGc ficheroGcEntity =
        MapperFicheroGcFromCodeList.getFicheroGcFromCodeList(logEntity, codeList);

    if (ficheroGcEntity == null) {
      log.warn("No se pudo mapear el CodeList a FicheroGc.");
      return null;
    }

    ficheroGcEntity.setLogEntity(logEntity);
    log.debug(
        "FicheroGc generado correctamente desde CodeList. ShortName: {}",
        ficheroGcEntity.getShortName());

    return ficheroGcEntity;
  }
}
