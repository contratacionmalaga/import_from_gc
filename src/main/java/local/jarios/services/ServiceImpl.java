package local.jarios.services;

import local.jarios.database.SessionFactoryProvider;
import local.jarios.entity.Estadistica;
import local.jarios.entity.FicheroGc;
import local.jarios.entity.Log;
import local.jarios.models.ParseoFicherosGc;
import local.jarios.properties.config.PropertiesManager;
import local.jarios.repositorys.Repository;
import local.jarios.repositorys.RepositoryImpl;
import local.jarios.repositorys.TransactionManager;
import local.jarios.utils.Constantes;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

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

    private final Repository repository;
    private final TransactionManager transactionManager;
    private final SessionFactory sessionFactory;

    /**
     * Constructor que inicializa los componentes necesarios para la persistencia.
     *
     * @param propertyManager Objeto que proporciona las propiedades de configuración.
     * @throws HibernateException Si ocurre un error al crear la {@link SessionFactory}.
     */
    public ServiceImpl(PropertiesManager propertyManager) throws HibernateException {
        this.repository = new RepositoryImpl();
        this.transactionManager = new TransactionManager();
        var sessionFactoryProvider = new SessionFactoryProvider();

        try {
            this.sessionFactory = sessionFactoryProvider.getSessionFactory(
                    propertyManager.getProperties(Constantes.HIBERNATE_PROPERTIES));
            log.info("SessionFactory creada con éxito.");
        } catch (HibernateException e) {
            log.error("Error creando SessionFactory: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Persiste un objeto {@link Log} en la base de datos.
     *
     * @param logEntity Objeto {@link Log} a persistir.
     */
    @Override
    public void persistir(Log logEntity) {
        log.debug("Iniciando persistencia de Log: {}", logEntity.getId());
        try (Session session = transactionManager.getSession(sessionFactory)) {
            Transaction transaction = transactionManager.beginTransaction(session);
            repository.persistir(session, transaction, logEntity);
            transactionManager.commitTransaction(transaction);
            log.info("Log con ID {} persistido correctamente.", logEntity.getId());
        } catch (Exception e) {
            log.error("Error persistiendo Log con ID {}: {}", logEntity.getId(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Persiste una lista de objetos {@link FicheroGc} en la base de datos.
     *
     * @param listFicherosGc Lista de objetos {@link FicheroGc} a persistir.
     */
    @Override
    public void persistir(List<FicheroGc> listFicherosGc) {
        log.debug("Iniciando persistencia de {} FicheroGc.", listFicherosGc.size());
        try (Session session = transactionManager.getSession(sessionFactory)) {
            Transaction transaction = transactionManager.beginTransaction(session);
            repository.persistir(session, transaction, listFicherosGc);
            transactionManager.commitTransaction(transaction);
            log.info("{} FicheroGc persistidos correctamente.", listFicherosGc.size());
        } catch (Exception e) {
            log.error("Error persistiendo FicheroGc: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Persiste un objeto {@link ParseoFicherosGc} en la base de datos.
     *
     * @param parseoFicherosGc Objeto {@link ParseoFicherosGc} a persistir.
     * @param propertiesManager Objeto que proporciona las propiedades de configuración.
     */
    @Override
    public void persistir(ParseoFicherosGc parseoFicherosGc, PropertiesManager propertiesManager) {
        log.debug("Iniciando persistencia de ParseoFicherosGc.");
        try (Session session = transactionManager.getSession(sessionFactory)) {
            Transaction transaction = transactionManager.beginTransaction(session);
            repository.persistir(session, transaction, parseoFicherosGc, propertiesManager);
            transactionManager.commitTransaction(transaction);
            log.info("ParseoFicherosGc persistido correctamente.");
        } catch (Exception e) {
            log.error("Error persistiendo ParseoFicherosGc: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Persiste un objeto {@link Estadistica} en la base de datos.
     *
     * @param estadistica Objeto {@link Estadistica} a persistir.
     */
    @Override
    public void persistir(Estadistica estadistica) {
        log.debug("Iniciando persistencia de Estadistica.");
        try (Session session = transactionManager.getSession(sessionFactory)) {
            Transaction transaction = transactionManager.beginTransaction(session);
            repository.persistir(session, transaction, estadistica);
            transactionManager.commitTransaction(transaction);
            log.info("Estadistica persistida correctamente.");
        } catch (Exception e) {
            log.error("Error persistiendo Estadistica: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Recupera una lista de objetos {@link FicheroGc} existentes en la base de datos.
     *
     * @return Lista de objetos {@link FicheroGc}.
     */
    @Override
    public List<FicheroGc> getListFicherosGc() {
        log.debug("Recuperando lista de FicheroGc desde la base de datos.");
        try (Session session = transactionManager.getSession(sessionFactory)) {
            List<FicheroGc> listFicherosGc = repository.getListFicherosGc(session);
            log.info("Recuperados {} FicheroGc.", listFicherosGc.size());
            return listFicherosGc;
        } catch (Exception e) {
            log.error("Error recuperando FicheroGc: {}", e.getMessage(), e);
            throw e;
        }
    }
}
