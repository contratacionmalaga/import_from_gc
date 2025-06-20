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
        log.debug("[ServiceImpl] - ");
        this.repository = new RepositoryImpl();
        log.debug("[ServiceImpl] - Creado el objeto RepositoryImpl correctamente.");
    }

    /**
     * Persiste un objeto {@link Log} en la base de datos.
     *
     * @param logEntity Objeto {@link Log} a persistir.
     */
    public void persistirLog(Log logEntity) throws MiServiceException{
        // Inicio
        log.debug("[persistirLog] - Iniciando el proceso de persistencia.");
        try {
            // El repositorio se encarga de la persistencia y manejo de la las transacciones
            repository.persistirLog(logEntity);
            log.debug("[persistirLog] - LogEntity persistido correctamente.");
        } catch (Exception e) {
            log.error("[persistirLog] - Error persistiendo Log con ID {}: {}", logEntity.getId(), e.getMessage());
            throw new MiServiceException ("[persistirLog] - Error persistiendo el Log.", e);
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
        log.debug("[persistirListaFicherosGc] - Iniciando persistencia de {} FicheroGc.", listFicherosGc.size());
        try {
            // Delegamos la persistencia y la gestión de la transacción al repositorio
            repository.persistirListaFicherosGc(listFicherosGc);
            log.debug("[persistirListaFicherosGc] - {} FicheroGc persistidos correctamente.", listFicherosGc.size());
        } catch (Exception e) {
            log.error("[persistirListaFicherosGc] - Error persistiendo FicheroGc: {}", e.getMessage());
            throw new MiServiceException("[persistirListaFicherosGc] - Error persistiendo la List<FicherosGc>.", e);
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
        log.debug("[persistirObjetoParseoFicherosGc] - Iniciando persistencia de ParseoFicherosGc.");
        try {
            // Delegamos la persistencia y la gestión de la transacción al repositorio
            repository.persistirObjetoParseoFicherosGc(parseoFicherosGc, prefijo);
            log.debug("[persistirObjetoParseoFicherosGc] - ParseoFicherosGc persistido correcamente.");
        } catch (MiRepositoryException e) {
            log.error("[persistirObjetoParseoFicherosGc] - Error persistiendo ParseoFicherosGc: {}", e.getMessage());
            throw new MiServiceException("[persistirObjetoParseoFicherosGc] - Error persistiendo el objeto ParseoFicherosGc.", e);
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
        } catch (Exception e) {
            log.error("[persistirEstadistica] - Error persistiendo Estadistica: {}", e.getMessage());
            throw new MiServiceException("Error persistiendo Estadistica.", e);
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
        log.debug("[getListFicherosGc] - Iniciando consulta de List<FicheroGc>.");

        List<FicheroGc> listFicherosGc;
        try {
            // Delegamos la persistencia y la gestión de la transacción al repositorio
            listFicherosGc = repository.getListFicherosGc();
            log.debug("[getListFicherosGc] - List<FicheroGc> obtenida correctamente..");
        } catch (Exception e) {
            log.error("[getListFicherosGc] - Error obteniendo List<FicheroGc>: {}", e.getMessage());
            throw new MiServiceException("Error obteniendo List<FicheroGc>.", e);
        }
        return listFicherosGc;
    }
}
