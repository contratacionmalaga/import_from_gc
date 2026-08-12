package local.jarios.repositories;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import local.jarios.common.util.Mensajes;
import local.jarios.common.util.PropertiesFiles;
import local.jarios.common.util.PropertiesKeys;
import local.jarios.dao.RegistroGcDao;
import local.jarios.database.SessionFactoryProvider;
import local.jarios.entity.FicheroGc;
import local.jarios.entity.Log;
import local.jarios.entity.ParseoFicherosGc;
import local.jarios.entity.RegistroGc;
import local.jarios.exceptions.MiRepositoryException;
import local.jarios.exceptions.MiSessionFactoryProvider;
import local.jarios.exceptions.MiTransactionManagerException;
import local.jarios.properties.api.PropertiesManagerService;
import local.jarios.properties.api.PropertiesManagerServiceImpl;
import local.jarios.properties.exception.PropertiesManagerException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/** Repositorio Hibernate para persistir logs y registros GC. */
@Slf4j
public class RepositoryImpl implements Repository {

  /** Mensaje de error de transaccion. */
  private static final String LOG_TRANSACCION_ERROR =
      "[ejecutarEnTransaccion] Método: {}, Error en transacción: {}";

  /** Mensaje de error de rollback. */
  private static final String LOG_ROLLBACK_ERROR =
      "[ejecutarEnTransaccion] Método: {}, Error durante rollback.";

  /** Objeto SessionFactory. */
  private final SessionFactory sessionFactory;

  /** DAO de registros GC. */
  private final RegistroGcDao registroGcDao;

  /** Objeto para gestionar los ficheros properties. */
  private final PropertiesManagerService propertyManager;

  /** Constructor que inicializa el proveedor de sesiones Hibernate y el DAO. */
  public RepositoryImpl() {
    this.propertyManager = PropertiesManagerServiceImpl.getInstance();
    this.registroGcDao = new RegistroGcDao(propertyManager);

    try {
      this.sessionFactory = new SessionFactoryProvider().getSessionFactory();
    } catch (MiSessionFactoryProvider ex) {
      String msg = "[RepositoryImpl] Error creando SessionFactory: " + ex.getMessage();
      log.error(msg, ex);
      throw new MiRepositoryException(msg, ex);
    }

    log.debug("[RepositoryImpl] Inicialización completada correctamente.");
  }

  /**
   * Persiste un objeto {@link Log} en base de datos.
   *
   * @param miLog objeto log a persistir
   * @param parseo resultado del parseo de ficheros GC
   * @throws MiRepositoryException si ocurre un error durante la operacion
   */
  @Override
  public void persistirEnBaseDatos(final Log miLog, final ParseoFicherosGc parseo)
      throws MiRepositoryException {
    final String prefijo = obtenerPrefijoAplicacion();

    try {
      ejecutarEnTransaccion(
          session -> {
            DestructiveImportPolicy.requireAllowed(propertyManager);
            borrarTodos(session, FicheroGc.class);
            session.flush();
            session.merge(miLog);
            log.debug("[persistirLog] Log persistido: {}", miLog);

            for (Map.Entry<String, List<RegistroGc>> entry :
                parseo.getMapRegistrosGcByFicheroGc().entrySet()) {
              final String nombreTabla = prefijo + TableNameNormalizer.normalize(entry.getKey());
              log.debug("[persistirObjetoParseoFicherosGc] Procesando tabla: {}", nombreTabla);

              if (registroGcDao.existeTabla(session, nombreTabla)) {
                registroGcDao.eliminarTabla(session, nombreTabla);
                log.debug(Mensajes.DROP_TABLE, "[persistirObjetoParseoFicherosGc]", nombreTabla);
              }

              try {
                registroGcDao.crearTabla(session, nombreTabla);
              } catch (MiRepositoryException ex) {
                throw new RuntimeException("Error creando tabla " + nombreTabla, ex);
              }

              registroGcDao.insertarRegistros(session, nombreTabla, entry.getValue());
            }

            return null;
          },
          "persistirEnBaseDatos");
    } catch (RuntimeException ex) {
      if (ex.getCause() instanceof MiRepositoryException cause) {
        throw cause;
      }
      throw ex;
    }
  }

  /**
   * Obtiene el prefijo definido en el fichero de configuracion.
   *
   * @return el prefijo definido en las propiedades
   * @throws MiRepositoryException si no se puede acceder a la configuracion
   */
  private String obtenerPrefijoAplicacion() throws MiRepositoryException {
    try {
      return TableNameNormalizer.normalize(
          propertyManager.getProperty(PropertiesFiles.APP, PropertiesKeys.APP_PREFIX));
    } catch (PropertiesManagerException ex) {
      String msg =
          "[obtenerPrefijoAplicacion] Error al obtener prefijo de configuración: "
              + ex.getMessage();
      log.error(msg, ex);
      throw new MiRepositoryException(msg, ex);
    }
  }

  /**
   * Ejecuta una operacion dentro de una transaccion Hibernate.
   *
   * @param function operacion a ejecutar que devuelve un resultado
   * @param metodo nombre del metodo para logs
   * @param <T> tipo de retorno de la operacion
   * @throws MiRepositoryException si ocurre un error durante la transaccion
   */
  private <T> void ejecutarEnTransaccion(Function<Session, T> function, String metodo)
      throws MiRepositoryException {
    Transaction transaction = null;

    try (Session session = sessionFactory.openSession()) {
      transaction = TransactionManager.beginTransaction(session);
      function.apply(session);
      TransactionManager.commitTransaction(transaction);
    } catch (Exception ex) {
      log.error(LOG_TRANSACCION_ERROR, metodo, ex.getMessage(), ex);
      rollbackTransaccion(transaction, metodo);
      throw new MiRepositoryException("[ejecutarEnTransaccion] Error durante la transacción.", ex);
    }
  }

  /**
   * Realiza rollback de una transaccion activa.
   *
   * @param transaction transaccion activa
   * @param metodo metodo en el que se produjo el error
   */
  private void rollbackTransaccion(Transaction transaction, String metodo) {
    if (transaction != null && transaction.getStatus().canRollback()) {
      try {
        TransactionManager.rollbackTransaction(transaction);
      } catch (MiTransactionManagerException rollbackEx) {
        log.error(LOG_ROLLBACK_ERROR, metodo, rollbackEx);
      }
    } else {
      log.warn("[rollbackTransaccion] No se puede hacer rollback: la transacción no está activa.");
    }
  }

  /**
   * Elimina todos los registros de una entidad especifica usando HQL.
   *
   * @param session sesion Hibernate activa
   * @param entidadClass clase de la entidad a eliminar
   */
  private void borrarTodos(Session session, Class<?> entidadClass) {
    DestructiveImportPolicy.requireAllowed(propertyManager);
    String hql = "delete from " + entidadClass.getSimpleName();
    int resultado = session.createMutationQuery(hql).executeUpdate();
    log.info(
        "[borrarTodos] Borrados todos los registros ({}) de {}",
        resultado,
        entidadClass.getSimpleName());
  }
}
