package local.jarios.services;

import local.jarios.entity.Estadistica;
import local.jarios.entity.FicheroGc;
import local.jarios.entity.Log;
import local.jarios.models.ParseoFicherosGc;
import local.jarios.properties.config.PropertiesManager;

import java.util.List;

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
    void persistir(Log miLog);

    /**
     * Persiste una lista de objetos {@link FicheroGc} en la base de datos.
     * <p>
     * Este método se encarga de almacenar una lista de objetos {@link FicheroGc} en la base de datos,
     * gestionando las operaciones necesarias para su persistencia.
     * </p>
     *
     * @param listFicherosGc Lista de objetos {@link FicheroGc} a persistir.
     */
    void persistir(List<FicheroGc> listFicherosGc);

    /**
     * Persiste un objeto {@link ParseoFicherosGc} en la base de datos.
     * <p>
     * Este método se encarga de almacenar un objeto {@link ParseoFicherosGc} en la base de datos,
     * gestionando las operaciones necesarias para su persistencia.
     * </p>
     *
     * @param parseoFicherosGc Objeto {@link ParseoFicherosGc} a persistir.
     * @param propertiesManager Objeto que proporciona las propiedades de configuración.
     */
    void persistir(ParseoFicherosGc parseoFicherosGc, PropertiesManager propertiesManager);

    /**
     * Persiste un objeto {@link Estadistica} en la base de datos.
     * <p>
     * Este método se encarga de almacenar un objeto {@link Estadistica} en la base de datos,
     * gestionando las operaciones necesarias para su persistencia.
     * </p>
     *
     * @param estadistica Objeto {@link Estadistica} a persistir.
     */
    void persistir(Estadistica estadistica);

    /**
     * Recupera una lista de objetos {@link FicheroGc} existentes en la base de datos.
     * <p>
     * Este método se encarga de recuperar una lista de objetos {@link FicheroGc} desde la base de datos,
     * proporcionando los datos almacenados.
     * </p>
     *
     * @return Lista de objetos {@link FicheroGc}.
     */
    List<FicheroGc> getListFicherosGc();
}
