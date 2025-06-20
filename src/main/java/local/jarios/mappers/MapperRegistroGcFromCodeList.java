package local.jarios.mappers;

import local.jarios.models.RegistroGc;
import local.jarios.genericode.CodeList;
import local.jarios.genericode.Row;
import local.jarios.genericode.Value;
import local.jarios.common.util.Constantes;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase responsable de mapear un {@link CodeList} a una lista de objetos {@link RegistroGc}.
 * <p>
 * Esta clase lee cada fila del código fuente y extrae las columnas definidas por las constantes
 * {@link Constantes#VALUE_CODE} y {@link Constantes#VALUE_NOMBRE}. Cualquier columna adicional se ignora
 * y se deja constancia mediante logging.
 * </p>
 *
 * @author Juan
 * @since 04/02/2025
 */
@Slf4j
public final class MapperRegistroGcFromCodeList {

    /**
     * Constructor privado para evitar instanciación.
     */
    private MapperRegistroGcFromCodeList() { }

    /**
     * Convierte un {@link CodeList} en una lista de objetos {@link RegistroGc}.
     *
     * @param codeList Objeto {@link CodeList} desde el cual se extraen los datos.
     * @return Lista de objetos {@link RegistroGc} generados a partir del código fuente.
     */
    public static List<RegistroGc> getListRegistroGcFromCodeList(CodeList codeList) {
        List<RegistroGc> listaRegistrosGc = new ArrayList<>();

        if (codeList == null || codeList.getSimpleCodeList() == null) {
            log.warn("CodeList o su contenido SimpleCodeList es nulo. No se puede procesar.");
            return listaRegistrosGc;
        }

        var filas = codeList.getSimpleCodeList().getRow();

        if (filas == null || filas.isEmpty()) {
            log.info("No se encontraron filas en el CodeList para procesar.");
            return listaRegistrosGc;
        }

        log.debug("Procesando {} fila(s) del CodeList.", filas.size());

        for (Row row : filas) {
            String code = null;
            String nombre = null;

            for (Value value : row.getValues()) {
                switch (value.getColumnRef()) {
                    case Constantes.VALUE_CODE -> code = value.getSimpleValue();
                    case Constantes.VALUE_NOMBRE -> nombre = value.getSimpleValue();
                    case Constantes.VALUE_NAME -> {
                        // Esta columna se ignora de forma intencionada
                    }
                    default ->
                        log.debug(
                                "Columna no reconocida: ColumnRef='{}', Valor='{}'",
                                value.getColumnRef(), value.getSimpleValue()
                        );
                }
            }

            if (code == null || nombre == null) {
                log.warn("Fila con valores incompletos: code='{}', nombre='{}'. Se omitirá.", code, nombre);
                continue;
            }

            listaRegistrosGc.add(new RegistroGc(code, nombre));
            log.debug("RegistroGc añadido: code='{}', nombre='{}'", code, nombre);
        }

        log.debug("Se generaron {} registros desde el CodeList.", listaRegistrosGc.size());
        return listaRegistrosGc;
    }
}
