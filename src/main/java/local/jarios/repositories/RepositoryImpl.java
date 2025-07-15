package local.jarios.repositories;

import local.jarios.common.util.Mensajes;
import local.jarios.dao.RegistroGcDao;
import local.jarios.database.SessionFactoryProvider;
import local.jarios.entity.FicheroGc;
import local.jarios.entity.Log;
import local.jarios.entity.ParseoFicherosGc;
import local.jarios.entity.RegistroGc;
import local.jarios.exceptions.MiRepositoryException;
import local.jarios.exceptions.MiSessionFactoryProvider;
import local.jarios.exceptions.MiTransactionManagerException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Implementación del repositorio para el manejo de persistencia de entidades.
 * <p>
 * Proporciona métodos para persistir logs, estadísticas, listas de ficheros y objetos
 * relacionados con ParseoFicherosGc, gestionando transacciones y sesiones Hibernate.
 */
@Slf4j
public class RepositoryImpl implements Repository {

    /** Variable global */
    private static final String LOG_TRANSACCION_ERROR = "[ejecutarEnTransaccion] - Método: {}, Error en transacción: {}";

    /** Variable global */
    private static final String LOG_ROLLBACK_ERROR = "[ejecutarEnTransaccion] - Método: {}, Error durante rollback de la transacción.";

    /** Objeto SessionFactory */
    private final SessionFactory sessionFactory;

    /** Objeto RegistroGcDao */
    private final RegistroGcDao registroGcDao;

    /**
     * Constructor que inicializa el proveedor de sesiones Hibernate y el DAO.
     *
     * @throws MiRepositoryException si ocurre un error durante la inicialización
     */
    public RepositoryImpl() {
        try {
            this.sessionFactory = new SessionFactoryProvider().getSessionFactory();
            this.registroGcDao = new RegistroGcDao();
            log.debug("[RepositoryImpl] - SessionFactory y DAO inicializados correctamente.");
        } catch (MiSessionFactoryProvider ex) {
            String msg = String.format("[RepositoryImpl] - Error creando SessionFactory: %s", ex.getMessage());
            log.error(msg, ex);
            throw new MiRepositoryException(msg, ex);
        }
    }

    /**
     * Persiste un objeto {@link Log} en base de datos, eliminando previamente los {@link FicheroGc}.
     *
     * @param miLog objeto log a persistir
     * @throws MiRepositoryException si ocurre un error durante la operación
     */
    @Override
    public void persistirLog(Log miLog) throws MiRepositoryException {
        ejecutarEnTransaccion(session -> {
            borrarTodos(session, FicheroGc.class);
            session.flush();
            session.merge(miLog);
            log.debug("[persistirLog] - Persistida entidad Log: {}", miLog);
            return null;
        }, "persistirLog");
    }

    /**
     * Persiste los registros de un objeto {@link ParseoFicherosGc} creando tablas dinámicas por cada fichero.
     *
     * @param parseo  Objeto que contiene los registros agrupados por fichero
     * @param prefijo Prefijo para los nombres de las tablas dinámicas
     * @throws MiRepositoryException si ocurre un error durante la transacción
     */
    @Override
    public void persistirObjetoParseoFicherosGc(ParseoFicherosGc parseo, String prefijo) throws MiRepositoryException {
        ejecutarEnTransaccion(session -> {
            for (Map.Entry<String, List<RegistroGc>> entry : parseo.getMapRegistrosGcByFicheroGc().entrySet()) {
                String nombreTabla = prefijo + entry.getKey().toLowerCase();
                log.debug("[persistirObjetoParseoFicherosGc] - Nombre tabla: {}", nombreTabla);

                if (registroGcDao.existeTabla(session, nombreTabla)) {
                    registroGcDao.eliminarTabla(session, nombreTabla);
                    log.debug(Mensajes.DROP_TABLE, "[persistirObjetoParseoFicherosGc]", nombreTabla);
                }

                registroGcDao.crearTabla(session, nombreTabla);
                registroGcDao.insertarRegistros(session, nombreTabla, entry.getValue());
            }
            return null;
        }, "persistirObjetoParseoFicherosGc");
    }

    /**
     * Recupera todos los registros de {@link FicheroGc} desde la base de datos.
     *
     * @return Lista de objetos FicheroGc
     * @throws MiRepositoryException si ocurre un error durante la consulta
     */
    @Override
    public List<FicheroGc> getListFicherosGc() throws MiRepositoryException {
        try (Session session = sessionFactory.openSession()) {
            List<FicheroGc> ficheros = session
                    .createQuery("FROM FicheroGc", FicheroGc.class)
                    .getResultList();
            log.debug("[getListFicherosGc] - Lista recuperada: {}", ficheros);
            return ficheros;
        } catch (Exception ex) {
            String msg = String.format("[getListFicherosGc] - Error obteniendo lista: %s", ex.getMessage());
            log.error(msg, ex);
            throw new MiRepositoryException(msg, ex);
        }
    }

    // ---------- MÉTODOS PRIVADOS ----------

    /**
     * Ejecuta una operación dentro de una transacción Hibernate con rollback y logging de errores.
     *
     * @param function Operación a ejecutar que devuelve un resultado
     * @param metodo   Nombre del método para logs
     * @param <T>      Tipo de retorno de la operación
     * @throws MiRepositoryException si ocurre un error durante la transacción
     */
    private <T> void ejecutarEnTransaccion(Function<Session, T> function, String metodo) throws MiRepositoryException {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = TransactionManager.beginTransaction(session);
            function.apply(session);
            TransactionManager.commitTransaction(transaction);
        } catch (Exception ex) {
            log.error(LOG_TRANSACCION_ERROR, metodo, ex.getMessage(), ex);
            rollbackTransaccion(transaction, metodo);
            throw new MiRepositoryException("[ejecutarEnTransaccion] - Error durante la transacción.", ex);
        }
    }

    /**
     * Realiza rollback de una transacción activa, registrando posibles errores.
     *
     * @param transaction Transacción activa
     * @param metodo      Método en el que se produjo el error
     */
    private void rollbackTransaccion(Transaction transaction, String metodo) {
        if (transaction != null && transaction.getStatus().canRollback()) {
            try {
                TransactionManager.rollbackTransaction(transaction);
            } catch (MiTransactionManagerException rollbackEx) {
                log.error(LOG_ROLLBACK_ERROR, metodo, rollbackEx);
            }
        } else {
            log.warn("[rollbackTransaccion] - No se puede hacer rollback: la transacción no está activa.");
        }
    }

    /**
     * Elimina todos los registros de una entidad específica usando HQL.
     *
     * @param session      Sesión Hibernate activa
     * @param entidadClass Clase de la entidad a eliminar
     */
    private void borrarTodos(Session session, Class<?> entidadClass) {
        String hql = "delete from " + entidadClass.getSimpleName();
        log.info("[borrarTodos] Borrado masivo con HQL: {}", hql);
        int resultado = session.createMutationQuery(hql).executeUpdate();
        log.info("[borrarTodos] Número de registros borrados: {}", resultado);
    }
}