package local.jarios.services;

import local.jarios.entity.Estadistica;
import local.jarios.entity.FicheroGc;
import local.jarios.entity.Log;
import local.jarios.entity.ParseoFicherosGc;

/**
 * Interfaz que define los servicios relacionados con la persistencia de entidades en la base de datos.
 * <p>
 * Esta interfaz proporciona métodos para persistir objetos {@link Log}, {@link FicheroGc},
 * {@link ParseoFicherosGc} y {@link Estadistica} en la base de datos.
 * </p>
 * <p>
 * Además, gestiona la recuperación de listas de objetos {@link FicheroGc} existentes en la base de datos.
 * </p>
 *
 * @author Juan Antonio
 * @version 1.0
 * @since 2024-06-04
 */
public interface Service {

    /**
     * Persiste un objeto {@link Log} en la base de datos.
     * <p>
     * Este método se encarga de almacenar un objeto {@link Log} en la base de datos,
     * gestionando las operaciones necesarias para su persistencia.
     * </p>
     *
     * @param miLog Objeto {@link Log} a persistir.
     * @param parseo Objeto que contiene un Map con el nombre del FicheroGc y los Registros asociados
     */
    void persistirEnBaseDeDatos(Log miLog, ParseoFicherosGc parseo);
}
