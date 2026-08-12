package local.jarios.mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import local.jarios.common.util.Constantes;
import local.jarios.entity.RegistroGc;
import local.jarios.genericode.CodeList;
import local.jarios.genericode.Row;
import local.jarios.genericode.Value;
import lombok.extern.slf4j.Slf4j;

/** Mapea un {@link CodeList} a una lista de {@link RegistroGc}. */
@Slf4j
public final class MapperRegistroGcFromCodeList {

  /** Constructor privado de clase utilitaria. */
  private MapperRegistroGcFromCodeList() {}

  /**
   * Convierte un {@link CodeList} en una lista de objetos {@link RegistroGc}.
   *
   * @param codeList objeto desde el que se extraen los datos
   * @param logId identificador asociado al log de ejecucion
   * @return registros generados a partir del codigo fuente
   */
  public static List<RegistroGc> getListRegistroGcFromCodeList(CodeList codeList, UUID logId) {
    List<RegistroGc> listaRegistrosGc = new ArrayList<>();

    if (codeList == null || codeList.getSimpleCodeList() == null) {
      log.warn("CodeList o su contenido SimpleCodeList es nulo. No se puede procesar.");
      return listaRegistrosGc;
    }

    var filas = codeList.getSimpleCodeList().getRow();
    if (filas == null || filas.isEmpty()) {
      log.debug("No se encontraron filas en el CodeList para procesar.");
      return listaRegistrosGc;
    }

    log.debug("Procesando {} fila(s) del CodeList.", filas.size());
    for (Row row : filas) {
      agregarRegistro(listaRegistrosGc, row, logId);
    }

    log.debug("Se generaron {} registros desde el CodeList.", listaRegistrosGc.size());
    return listaRegistrosGc;
  }

  /**
   * Extrae los valores de una fila y anade el registro si esta completo.
   *
   * @param listaRegistrosGc lista acumulada de registros generados
   * @param row fila genericode de origen
   * @param logId identificador del log de ejecucion
   */
  private static void agregarRegistro(List<RegistroGc> listaRegistrosGc, Row row, UUID logId) {
    String code = null;
    String nombre = null;

    for (Value value : row.getValues()) {
      switch (value.getColumnRef()) {
        case Constantes.VALUE_CODE -> code = value.getSimpleValue();
        case Constantes.VALUE_NOMBRE -> nombre = value.getSimpleValue();
        case Constantes.VALUE_NAME -> {
          // Columna ignorada de forma intencionada.
        }
        default ->
            log.debug(
                "Columna no reconocida: ColumnRef='{}', Valor='{}'",
                value.getColumnRef(),
                value.getSimpleValue());
      }
    }

    if (code == null || nombre == null) {
      log.warn("Fila con valores incompletos: code='{}', nombre='{}'. Se omitira.", code, nombre);
      return;
    }

    RegistroGc registroGc = new RegistroGc(code, nombre, logId);
    log.debug("[getListRegistroGcFromCodeList] Creado {}", registroGc);
    listaRegistrosGc.add(registroGc);
    log.debug("[getListRegistroGcFromCodeList] Anadido a la lista ({})", listaRegistrosGc.size());
  }
}
