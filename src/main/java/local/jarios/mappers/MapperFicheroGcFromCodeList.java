package local.jarios.mappers;

import java.util.function.Function;
import local.jarios.common.util.Constantes;
import local.jarios.entity.FicheroGc;
import local.jarios.entity.Log;
import local.jarios.genericode.CodeList;
import local.jarios.genericode.Identification;
import lombok.extern.slf4j.Slf4j;

/** Mapea un {@link CodeList} a una entidad {@link FicheroGc}. */
@Slf4j
public final class MapperFicheroGcFromCodeList {

  /** Constructor privado de clase utilitaria. */
  private MapperFicheroGcFromCodeList() {}

  /**
   * Realiza el mapeo de un objeto {@link CodeList} a una entidad {@link FicheroGc}.
   *
   * @param logEntity log asociado al fichero GC
   * @param codeList objeto fuente desde el que se extraen los datos
   * @return entidad construida o {@code null} si la identificacion es nula
   */
  public static FicheroGc getFicheroGcFromCodeList(Log logEntity, CodeList codeList) {
    Identification identification = codeList.getIdentification();
    if (identification == null) {
      log.warn("No se puede mapear CodeList a FicheroGc: identificacion nula.");
      return null;
    }

    var ficheroGcEntity = new FicheroGc();
    ficheroGcEntity.setLogEntity(logEntity);
    ficheroGcEntity.setShortName(getOrEmpty(identification, Identification::getShortName));
    ficheroGcEntity.setLongName(getOrEmpty(identification, Identification::getLongName));
    ficheroGcEntity.setVersion(getOrEmpty(identification, Identification::getVersion));
    ficheroGcEntity.setCanonicalUri(getOrEmpty(identification, Identification::getCanonicalUri));
    ficheroGcEntity.setCanonicalVersionUri(
        getOrEmpty(identification, Identification::getCanonicalVersionUri));
    ficheroGcEntity.setLocationUri(getOrEmpty(identification, Identification::getLocationUri));

    log.debug(
        "FicheroGc mapeado correctamente desde CodeList con shortName='{}'.",
        ficheroGcEntity.getShortName());

    return ficheroGcEntity;
  }

  /**
   * Obtiene un valor seguro desde el objeto {@link Identification}.
   *
   * @param identification objeto fuente
   * @param getter funcion que obtiene un campo del objeto
   * @return valor obtenido o cadena vacia si es nulo
   */
  private static String getOrEmpty(
      Identification identification, Function<Identification, String> getter) {
    String value = getter.apply(identification);
    return value != null ? value : Constantes.CADENA_VACIA;
  }
}
