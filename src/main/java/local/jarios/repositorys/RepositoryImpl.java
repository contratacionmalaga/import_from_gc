package local.jarios.repositorys;

import com.fasterxml.uuid.Generators;
import local.jarios.common.util.Mensajes;
import local.jarios.database.SessionFactoryProvider;
import local.jarios.entity.Estadistica;
import local.jarios.entity.FicheroGc;
import local.jarios.entity.Log;
import local.jarios.exceptions.MiRepositoryException;
import local.jarios.exceptions.MiSessionFactoryProvider;
import local.jarios.exceptions.MiTransactionManagerException;
import local.jarios.interfaces.EsActualizable;
import local.jarios.models.ParseoFicherosGc;
import local.jarios.models.RegistroGc;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Implementación del Repository
 */
@Slf4j
public class RepositoryImpl implements Repository {

    /**
     * SessionFactory
     */
    private final SessionFactory sessionFactory;

    /**
     * Constructor
     */
    public RepositoryImpl() {
        try {

            this.sessionFactory = new SessionFactoryProvider().getSessionFactory();
            log.debug("[RepositoryImpl] - SessionFactory inicializada correctamente.");

        } catch (MiSessionFactoryProvider ex) {

            String msg = String.format("[RepositoryImpl] - Error creando SessionFactory. Error: %s", ex.getMessage());
            log.error(msg, ex);
            throw new MiRepositoryException(msg, ex);

        }
    }

    /**
     * Persistir Log
     * @param miLog Objeto Log a persistir
     * @throws MiRepositoryException Excepción
     */
    @Override
    public void persistirLog(Log miLog) throws MiRepositoryException {
        ejecutarDentroDeTransaccion(session -> session.persist(miLog), "persistirLog");
    }

    /**
     * Persistir Estadística
     * @param estadistica Objeto Estadistica a persistir
     * @throws MiRepositoryException Excepción
     */
    @Override
    public void persistirEstadistica(Estadistica estadistica) throws MiRepositoryException {
        ejecutarDentroDeTransaccion(session -> session.persist(estadistica), "persistirEstadistica");
    }

    /**
     * Persistir Lista de FicheroGc
     * @param ficherosGc Lista de FicheroGc a persistir
     * @throws MiRepositoryException Excepción
     */
    @Override
    public void persistirListaFicherosGc(List<FicheroGc> ficherosGc) throws MiRepositoryException {
        ejecutarDentroDeTransaccion(session -> {
            borrarFicherosGc(session);
            session.flush();
            persistirLista(session, ficherosGc);
        }, "persistirListaFicherosGc");
    }

    /**
     * Persistir ParseoFicherosGc
     * @param parseo Objeto ParseoFicherosGc a persistir
     * @param prefijo Prefijo utilizado en la creación de las tablas.
     * @throws MiRepositoryException Excepción
     */
    @Override
    public void persistirObjetoParseoFicherosGc(ParseoFicherosGc parseo, String prefijo) throws MiRepositoryException {
        ejecutarDentroDeTransaccion(session -> {
            for (Map.Entry<String, List<RegistroGc>> entry : parseo.getMapRegistrosGcByFicheroGc().entrySet()) {
                String nombreTabla = prefijo + entry.getKey().toLowerCase();
                log.debug("[RepositoryImpl] - Nombre tabla {}", nombreTabla.toLowerCase());
                if (existeTabla(session, nombreTabla)) {
                    session.createNativeQuery("DROP TABLE " + nombreTabla).executeUpdate();
                    log.debug(Mensajes.DROP_TABLE, "[persistirObjetoParseoFicherosGc] -", nombreTabla);
                }
                crearTabla(session, nombreTabla);
                log.debug("[RepositoryImpl] - Creada la tabla.");
                insertarRegistros(session, nombreTabla, entry.getValue());
                log.debug("[RepositoryImpl] - Insertados los registros: {}", entry.getValue());
            }
        }, "persistirObjetoParseoFicherosGc");
    }

    /**
     * Obtener Lista de FicheroGc existente en el servidor
     * @return Lista de FicheroGc
     * @throws MiRepositoryException Excepción
     */
    @Override
    public List<FicheroGc> getListFicherosGc() throws MiRepositoryException {
        try (Session session = sessionFactory.openSession()) {
            List<FicheroGc> listFicherosGc = session.createQuery("FROM FicheroGc", FicheroGc.class).getResultList();
            log.debug("[getListFicherosGc] - List<FicheroGc>: {}", listFicherosGc);
            return listFicherosGc;
        } catch (Exception ex) {
            String msg = String.format("[getListFicherosGc] - Error obteniendo lista FicheroGc: %s", ex.getMessage());
            log.error(msg, ex);
            throw new MiRepositoryException(msg, ex);
        }
    }

    // ----------- MÉTODOS PRIVADOS -------------

    /**
     * Ejecutar dentro de Transacción
     * @param consumer Objeto SessionConsumer
     * @param metodo metodo
     * @throws MiRepositoryException Excepción
     */
    private void ejecutarDentroDeTransaccion(SessionConsumer consumer, String metodo) throws MiRepositoryException {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = local.jarios.repositories.TransactionManager.beginTransaction(session);
            consumer.accept(session);
            local.jarios.repositories.TransactionManager.commitTransaction(transaction);
        } catch (Exception ex) {
            if ((transaction != null) && transaction.getStatus().canRollback()) {
                try {
                    local.jarios.repositories.TransactionManager.rollbackTransaction(transaction);
                } catch (MiTransactionManagerException rollbackEx) {
                    log.error("[ejecutarDentroDeTransaccion] - Error durante rollback de la transacción.");

                }
            }
            throw new MiRepositoryException("Error en " + metodo, ex);
        }
    }

    /**
     * Borrar FicherosGc del servidor de base de datos
     * @param session Objeto Session
     */
    private void borrarFicherosGc(Session session) {
        session.createQuery("DELETE FROM FicheroGc").executeUpdate();
        log.debug("[borrarFicherosGc] - Ficheros eliminados.");
    }

    /**
     * Persistir Lista
     * @param session Objeto Session
     * @param lista Lista de Objetos T
     * @param <T> Objeto
     */
    private <T extends EsActualizable<T>> void persistirLista(Session session, List<T> lista) {
        for (T entidad : lista) {
            log.debug("[persistirLista] - Entidad: {}.", entidad.toString());
            if (entidad.getId() == null) {
                entidad.setId();
                session.persist(entidad);
                log.debug("[persistirLista] - entidad.getId() es null. Entidad persistida.");
            } else {
                session.merge(entidad);
                log.debug("[persistirLista] - entidad.getId() NO es null. Entidad actualizada.");
            }
        }
        log.debug("[persistirLista] - {} entidades procesadas.", lista.size());
    }

    /**
     * Devuelve si existe una tabla en el Servidor de Base de Datos
     * @param session Objeto Session
     * @param nombreTabla Nombre de la Tabla
     * @return boolean
     */
    private boolean existeTabla(Session session, String nombreTabla) {

        //
        String sql = "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = :nombre";
        log.debug("[existeTabla] - SQL: {}", sql);
        long count = ((Number) session.createNativeQuery(sql)
                .setParameter("nombre", nombreTabla)
                .getSingleResult()).longValue();
        log.debug("[existeTabla] - Count: {}", count);
        return count > 0;
    }

    /**
     * Crear Tabla
     * @param session Objeto Session
     * @param nombreTabla Nombre de la Tabla
     */
    private void crearTabla(Session session, String nombreTabla) {
        String sql = "CREATE TABLE IF NOT EXISTS " + nombreTabla + " (" +
                "id UUID NOT NULL, " +
                "code VARCHAR(50) NOT NULL PRIMARY KEY, " +
                "nombre VARCHAR(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci" +
                ") CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;";
        log.debug("[crearTabla] - SQL: {}", sql);
        session.createNativeQuery(sql).executeUpdate();
    }

    /**
     * Insertar Registros
     * @param session Objeto Session
     * @param nombreTabla Nombre de la tabla
     * @param registros Lista Registros Lista RegistroGc
     */
    private void insertarRegistros(Session session, String nombreTabla, List<RegistroGc> registros) {
        StringBuilder sql = new StringBuilder("INSERT INTO " + nombreTabla + " (id, code, nombre) VALUES ");
        log.debug("[insertarRegistros] - SQL: {}", sql);
        for (int i = 0; i < registros.size(); i++) {
            RegistroGc r = registros.get(i);
            log.debug("[insertarRegistros] - ({}/{}) - RegistroGc: {}", i, registros.size(), r.toString());
            UUID id = Generators.timeBasedEpochGenerator().generate();
            log.debug("[insertarRegistros] - Generado el id del registro: {}", id);
            sql.append("('")
                    .append(id).append("','")
                    .append(sanitizar(r.getCode())).append("','")
                    .append(sanitizar(r.getNombre())).append("')");
            if (i < registros.size() - 1) sql.append(", ");
            log.debug("[insertarRegistros] - SQL Actualizado: {}", sql);
        }
        session.createNativeQuery(sql.toString()).executeUpdate();
        log.debug("[insertarRegistros] - Insertados {} registros en la tabla {}", registros.size(), nombreTabla);
    }

    /**
     * Sanitizar
     * @param valor Cadena
     * @return Cadena sanitizada
     */
    private String sanitizar(String valor) {

         String cadenaSanitizada = valor == null ? "" : valor.replace("'", "''");
         log.debug("[sanitizar] - Cadena: {}, Cadena Sanitizada: {}", valor, cadenaSanitizada);
         return cadenaSanitizada;
    }

    /**
     * Interface SessionConsumer
     */
    @FunctionalInterface
    private interface SessionConsumer {

        /**
         * Este método es un consumidor funcional que acepta una sesión de Hibernate.
         * Se espera que realice alguna operación dentro de la sesión proporcionada.
         *
         * @param session la sesión de Hibernate que será consumida
         * @throws HibernateException si ocurre un error durante la operación en la sesión
         */
        void accept(Session session) throws HibernateException;
    }
}
