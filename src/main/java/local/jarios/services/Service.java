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
     */
    void persistirLog(Log miLog);

    /**
     * Persiste los registros contenidos en un objeto {@link ParseoFicherosGc}.
     *
     * <p>
     * Este método crea dinámicamente tablas (una por cada tipo de fichero representado)
     * con el nombre basado en el prefijo proporcionado, y almacena en ellas los registros
     * asociados a cada fichero.
     * </p>
     *
     * @param parseoFicherosGc Objeto que agrupa los registros por tipo de fichero.
     * @param prefijo          Prefijo que se antepone al nombre de las tablas dinámicas.
     */
    void persistirObjetoParseoFicherosGc(ParseoFicherosGc parseoFicherosGc, String prefijo);
}
