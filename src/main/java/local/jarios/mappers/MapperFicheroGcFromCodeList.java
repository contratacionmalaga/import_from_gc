package local.jarios.mappers;

import local.jarios.entity.FicheroGc;
import local.jarios.entity.Log;
import local.jarios.genericode.CodeList;
import local.jarios.genericode.Identification;
import local.jarios.utils.Constantes;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Function;

/**
 * Clase utilitaria para mapear un objeto {@link CodeList} a una entidad {@link FicheroGc}.
 * <p>
 * Esta clase encapsula la lógica de transformación, asegurando que los valores nulos sean tratados
 * correctamente y que el resultado esté siempre en un estado consistente.
 * </p>
 *
 * @author Juan Antonio
 * @since 04/06/2024
 */
@Slf4j
public final class MapperFicheroGcFromCodeList {

    /**
     * Constructor privado para evitar instanciación.
     */
    private MapperFicheroGcFromCodeList() {}

    /**
     * Realiza el mapeo de un objeto {@link CodeList} a una entidad {@link FicheroGc}.
     * <p>
     * Si el objeto {@link Identification} es nulo, se devuelve {@code null}.
     * En caso contrario, se mapean todos los campos relevantes de manera segura.
     * </p>
     *
     * @param logEntity Objeto {@link Log} que se asocia al {@link FicheroGc}.
     * @param codeList  Objeto fuente desde el cual se extraen los datos.
     * @return Entidad {@link FicheroGc} completamente construida, o {@code null} si el código no es válido.
     */
    public static FicheroGc getFicheroGcFromCodeList(Log logEntity, CodeList codeList) {
        Identification identification = codeList.getIdentification();

        if (identification == null) {
            log.warn("No se puede mapear CodeList a FicheroGc: identificación nula.");
            return null;
        }

        // Crear entidad con valores seguros
        var ficheroGcEntity = new FicheroGc();
        ficheroGcEntity.setLogEntity(logEntity);
        ficheroGcEntity.setShortName(getOrEmpty(identification, Identification::getShortName));
        ficheroGcEntity.setLongName(getOrEmpty(identification, Identification::getLongName));
        ficheroGcEntity.setVersion(getOrEmpty(identification, Identification::getVersion));
        ficheroGcEntity.setCanonicalUri(getOrEmpty(identification, Identification::getCanonicalUri));
        ficheroGcEntity.setCanonicalVersionUri(getOrEmpty(identification, Identification::getCanonicalVersionUri));
        ficheroGcEntity.setLocationUri(getOrEmpty(identification, Identification::getLocationUri));

        log.debug("FicheroGc mapeado correctamente desde CodeList con shortName='{}'.",
                ficheroGcEntity.getShortName());

        return ficheroGcEntity;
    }

    /**
     * Método auxiliar para obtener valores seguros desde el objeto {@link Identification}.
     * <p>
     * Si el valor obtenido es {@code null}, se devuelve una cadena vacía definida en {@link Constantes#CADENA_VACIA}.
     * </p>
     *
     * @param identification Objeto fuente.
     * @param getter         Función que obtiene un campo del objeto.
     * @return Valor obtenido o cadena vacía si es nulo.
     */
    private static String getOrEmpty(Identification identification, Function<Identification, String> getter) {
        String value = getter.apply(identification);
        return value != null ? value : Constantes.CADENA_VACIA;
    }
}
