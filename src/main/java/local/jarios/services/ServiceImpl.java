package local.jarios.services;

import local.jarios.entity.Estadistica;
import local.jarios.entity.FicheroGc;
import local.jarios.entity.Log;
import local.jarios.exceptions.MiRepositoryException;
import local.jarios.exceptions.MiServiceException;
import local.jarios.models.ParseoFicherosGc;
import local.jarios.repositorys.Repository;
import local.jarios.repositorys.RepositoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;

import java.util.List;

/**
 * Implementación del servicio encargado de la persistencia de entidades en la base de datos.
 * Utiliza Hibernate para gestionar las transacciones y operaciones CRUD.
 * <p>
 * Esta clase proporciona métodos para persistir objetos {@link Log}, {@link FicheroGc},
 * {@link ParseoFicherosGc} y {@link Estadistica} en la base de datos.
 * </p>
 * <p>
 * Además, gestiona la creación y cierre de sesiones de Hibernate y transacciones asociadas.
 * </p>
 *
 * @author Juan Antonio
 * @version 1.0
 * @since 2024-06-04
 */
@Slf4j
public class ServiceImpl implements Service {

    /**
     * Instancia del repositorio para acceso y gestión de datos.
     * <p>
     * Se utiliza para realizar operaciones CRUD sobre las entidades persistentes.
     * </p>
     */
    private final Repository repository;


    /**
     * Constructor que inicializa los componentes necesarios para la persistencia.
     *
     * @throws HibernateException Si ocurre un error al crear la {@link SessionFactory}.
     */
    public ServiceImpl() throws MiServiceException {

        //
        try {

            this.repository = new RepositoryImpl();
            log.debug("[ServiceImpl] - Creado el objeto RepositoryImpl correctamente.");

        } catch (MiRepositoryException ex) {

            String msg = String.format("[ServiceImpl] - Error creando el constructor: %s", ex.getMessage());
            log.error(msg, ex);
            throw new MiServiceException(msg, ex);

        }
    }

    /**
     * Persiste un objeto {@link Log} en la base de datos.
     *
     * @param miLog Objeto {@link Log} a persistir.
     */
    public void persistirLog(Log miLog) throws MiServiceException{

        // Inicio
        try {

            // El repositorio se encarga de la persistencia y manejo de la las transacciones
            repository.persistirLog(miLog);
            log.debug("[persistirLog] - miLog persistido correctamente.");

        } catch (MiRepositoryException ex) {

            String msg = String.format("[persistirLog] - Error persistiendo Log con ID %s: %s", miLog.getId(), ex.getMessage());
            log.error(msg, ex.getMessage(), ex);
            throw new MiServiceException (msg, ex);

        }
    }

    /**
     * Persiste una lista de objetos {@link FicheroGc} en la base de datos.
     *
     * @param listFicherosGc Lista de objetos {@link FicheroGc} a persistir.
     */
    @Override
    public void persistirListaFicherosGc(List<FicheroGc> listFicherosGc) throws MiServiceException {

        // Inicio
        try {

            // Delegamos la persistencia y la gestión de la transacción al repositorio
            repository.persistirListaFicherosGc(listFicherosGc);
            log.debug("[persistirListaFicherosGc] - {} FicheroGc persistidos correctamente.", listFicherosGc.size());

        } catch (MiRepositoryException ex) {

            String msg = String.format("[persistirListaFicherosGc] - Error persistiendo la List<FicherosGc>: %s, Error: %s", listFicherosGc, ex.getMessage());
            log.error(msg, ex.getMessage(), ex);
            throw new MiServiceException(msg, ex);

        }
    }

    /**
     * Persiste un objeto {@link ParseoFicherosGc} en la base de datos.
     *
     * @param parseoFicherosGc Objeto {@link ParseoFicherosGc} a persistir.
     * @param prefijo Prefijo utilizado en la creación de las tablas.
     */
    public void persistirObjetoParseoFicherosGc(ParseoFicherosGc parseoFicherosGc, String prefijo) throws MiServiceException {

        // Inicio
        try {

            // Delegamos la persistencia y la gestión de la transacción al repositorio
            repository.persistirObjetoParseoFicherosGc(parseoFicherosGc, prefijo);
            log.debug("[persistirObjetoParseoFicherosGc] - ParseoFicherosGc persistido correcamente.");

        } catch (MiRepositoryException ex) {

            String msg = String.format("[persistirObjetoParseoFicherosGc] - Error persistiendo el objeto ParseoFicherosGc: %s, Error: %s", parseoFicherosGc, ex.getMessage());
            log.error(msg, ex.getMessage(), ex);
            throw new MiServiceException(msg, ex);

        }
    }

    /**
     * Persiste un objeto {@link Estadistica} en la base de datos.
     *
     * @param estadistica Objeto {@link Estadistica} a persistir.
     */
    @Override
    public void persistirEstadistica(Estadistica estadistica) throws MiServiceException {
        // Inicio
        log.debug("[persistirEstadistica] - Iniciando persistencia de Estadistica.");
        try {
            // Delegamos la persistencia y la gestión de la transacción al repositorio
            repository.persistirEstadistica(estadistica);
            log.debug("[persistirEstadistica] - Estadistica persistido correcamente.");
        } catch (MiRepositoryException ex) {
            String msg = String.format("[persistirEstadistica] - Error persistiendo Estadistica. Error: %s", ex.getMessage());
            log.error(msg, ex);
            throw new MiServiceException(msg, ex);
        }
    }

    /**
     * Recupera una lista de objetos {@link FicheroGc} existentes en la base de datos.
     *
     * @return Lista de objetos {@link FicheroGc}.
     */
    @Override
    public List<FicheroGc> getListFicherosGc() throws MiServiceException {

        // Inicio
        try {
            // Delegamos la persistencia y la gestión de la transacción al repositorio
            List<FicheroGc>  listFicherosGc = repository.getListFicherosGc();
            log.debug("[getListFicherosGc] - List<FicheroGc> obtenida correctamente..");
            return listFicherosGc;
        } catch (MiRepositoryException ex) {
            String msg = String.format("[getListFicherosGc] - Error obteniendo List<FicheroGc>. Error: %s", ex.getMessage());
            log.error(msg, ex);
            throw new MiServiceException(msg, ex);
        }
    }
}
