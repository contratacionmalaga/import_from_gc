package local.jarios.helpers;

import local.jarios.entity.Log;
import local.jarios.interfaces.Actualizable;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * Clase de utilidad para la gestión y sincronización de listas de objetos actualizables.
 * <p>
 * Permite unificar una lista de datos nuevos con una lista de datos existentes, actualizando los existentes
 * o insertando nuevos registros, según corresponda.
 * </p>
 *
 * @author Juan
 * @since 28/02/2025
 */
@Slf4j
public final class ListHelper {

    /**
     * Constructor privado para evitar instanciación.
     */
    private ListHelper() { }

    /**
     * Unifica dos listas de objetos del mismo tipo, comparando por clave única.
     * <p>
     * Si un objeto de la lista de importación existe en la base de datos y ha cambiado, se actualiza.
     * Si no existe, se inserta. Los objetos deben implementar {@link Actualizable}.
     * </p>
     *
     * @param logEntity Objeto {@link Log} que se asignará a los registros nuevos o actualizados.
     * @param listElementosEnBaseDatos Lista de elementos actuales en base de datos (se modificará).
     * @param listElementosPendientesImportar Lista de nuevos elementos a importar.
     * @param <T> Tipo genérico que extiende {@link Actualizable}.
     */
    public static <T extends Actualizable<T>> void unificarListas(
            Log logEntity,
            List<T> listElementosEnBaseDatos,
            List<T> listElementosPendientesImportar) {

        if (listElementosEnBaseDatos == null || listElementosPendientesImportar == null) {
            log.warn("Alguna de las listas proporcionadas es nula. Proceso cancelado.");
            return;
        }

        log.info("Iniciando proceso de unificación de listas. Elementos BD: {}, Elementos a importar: {}",
                listElementosEnBaseDatos.size(), listElementosPendientesImportar.size());

        // Crear un mapa a partir de la lista de base de datos
        Map<String, T> mapElementosEnBaseDatos = new HashMap<>();
        for (T elemento : listElementosEnBaseDatos) {
            mapElementosEnBaseDatos.put(elemento.getUniqueKey(), elemento);
        }

        int totalInsertados = 0;
        int totalActualizados = 0;

        for (T nuevoElemento : listElementosPendientesImportar) {
            String clave = nuevoElemento.getUniqueKey();
            T existente = mapElementosEnBaseDatos.get(clave);

            if (existente != null) {
                // Ya existe en BD
                if (!nuevoElemento.equals(existente)) {
                    existente.actualizarCon(nuevoElemento);
                    existente.setLogEntity(logEntity);
                    totalActualizados++;
                    log.debug("Elemento con clave '{}' actualizado.", clave);
                } else {
                    log.debug("Elemento con clave '{}' ya estaba actualizado.", clave);
                }
            } else {
                // Nuevo elemento
                nuevoElemento.setLogEntity(logEntity);
                listElementosEnBaseDatos.add(nuevoElemento);
                totalInsertados++;
                log.debug("Elemento con clave '{}' insertado.", clave);
            }
        }

        log.info("Proceso completado. Total insertados: {}, total actualizados: {}", totalInsertados, totalActualizados);
    }
}
