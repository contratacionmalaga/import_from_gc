package local.jarios.repositorys;

import com.fasterxml.uuid.Generators;
import local.jarios.entity.Estadistica;
import local.jarios.entity.FicheroGc;
import local.jarios.entity.Log;
import local.jarios.enums.TipoFinalEjecucion;
import local.jarios.interfaces.Actualizable;
import local.jarios.models.ParseoFicherosGc;
import local.jarios.models.RegistroGc;
import local.jarios.properties.PropertyConstantes;
import local.jarios.properties.config.PropertiesManager;
import local.jarios.utils.Constantes;
import local.jarios.utils.FinalDelPrograma;
import local.jarios.utils.Mensajes;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Implementación del repositorio para persistencia de entidades relacionadas
 * con la importación de ficheros GC, logs y estadísticas.
 * <p>
 * Permite persistir logs, estadísticas, listas de ficheros y parseos de registros GC,
 * manejando transacciones e integridad de datos.
 * </p>
 *
 * @author Juan Antonio
 * @date 04/06/2024
 * @team Contratación Electrónica
 */
@Slf4j
public class RepositoryImpl implements Repository {

    /**
     * Constructor vacío.
     */
    public RepositoryImpl() {
        // Constructor vacío
    }

    /**
     * Persiste un objeto {@link Log} en la base de datos.
     *
     * @param session     Sesión Hibernate activa.
     * @param transaction Transacción activa.
     * @param miLog       Entidad Log a persistir.
     */
    @Override
    public void persistir(Session session, Transaction transaction, Log miLog) {
        try {
            session.persist(miLog);
            log.info("Persistido Log con id: {}", miLog.getId());
        } catch (HibernateException ex) {
            log.error("Error persistiendo Log: {}", ex.getMessage(), ex);
            rollbackAndExit(transaction);
        }
    }

    /**
     * Persiste una lista de {@link FicheroGc} en la base de datos.
     *
     * @param session       Sesión Hibernate activa.
     * @param transaction   Transacción activa.
     * @param listFicherosGc Lista de entidades FicheroGc a persistir.
     */
    @Override
    public void persistir(Session session, Transaction transaction, List<FicheroGc> listFicherosGc) {
        try {
            grabarLista(session, listFicherosGc);
            log.info("Persistida lista de FicheroGc con {} elementos", listFicherosGc.size());
        } catch (HibernateException ex) {
            log.error("Error persistiendo lista FicheroGc: {}", ex.getMessage(), ex);
            rollbackAndExit(transaction);
        }
    }

    /**
     * Persiste una entidad {@link Estadistica} en la base de datos.
     *
     * @param session     Sesión Hibernate activa.
     * @param transaction Transacción activa.
     * @param estadistica Entidad Estadistica a persistir.
     */
    @Override
    public void persistir(Session session, Transaction transaction, Estadistica estadistica) {
        try {
            session.persist(estadistica);
            log.info("Persistida Estadistica con id: {}", estadistica.getId());
        } catch (HibernateException ex) {
            log.error("Error persistiendo Estadistica: {}", ex.getMessage(), ex);
            rollbackAndExit(transaction);
        }
    }

    /**
     * Persiste el parseo de ficheros GC y sus registros en tablas dinámicas.
     * <p>
     * Para cada fichero parseado, verifica si existe la tabla correspondiente,
     * la elimina si existe, crea una nueva y luego inserta los registros asociados.
     * </p>
     *
     * @param session           Sesión Hibernate activa.
     * @param transaction       Transacción activa.
     * @param parseoFicherosGc  Objeto con el parseo de ficheros GC.
     * @param propertiesManager Gestor de propiedades para obtener configuraciones.
     */
    @Override
    public void persistir(Session session, Transaction transaction,
                          ParseoFicherosGc parseoFicherosGc,
                          PropertiesManager propertiesManager) {
        try {
            for (Map.Entry<String, List<RegistroGc>> entry : parseoFicherosGc.getMapRegistrosGcByFicheroGc().entrySet()) {
                String prefijo = propertiesManager.getProperty(Constantes.CONFIG_PROPERTIES, PropertyConstantes.CONFIG_PREFIJO);
                String nombreTabla = prefijo + entry.getKey().toLowerCase();

                if (tablaExiste(session, nombreTabla)) {
                    session.createNativeQuery("DROP TABLE " + nombreTabla).executeUpdate();
                    log.info(Mensajes.DROP_TABLE, Constantes.TABULADOR_1, nombreTabla);
                }

                crearTabla(session, nombreTabla);
                log.info(Mensajes.CREATE_TABLE, Constantes.TABULADOR_1, nombreTabla);

                insertarRegistrosEnTabla(session, nombreTabla, entry.getValue());
                log.info(Mensajes.INSERT_RECORDS, Constantes.TABULADOR_2, entry.getValue().size(), nombreTabla);
            }
        } catch (HibernateException ex) {
            log.error("Error persistiendo ParseoFicherosGc: {}", ex.getMessage(), ex);
            rollbackAndExit(transaction);
        }
    }

    /**
     * Persiste o actualiza una lista de entidades que implementan {@link Actualizable}.
     *
     * @param session Sesión Hibernate activa.
     * @param lista   Lista de entidades a persistir o actualizar.
     * @param <T>     Tipo genérico que implementa Actualizable.
     * @throws HibernateException Si ocurre un error durante la persistencia.
     */
    private static <T extends Actualizable<T>> void grabarLista(Session session, List<T> lista) throws HibernateException {
        for (T entidad : lista) {
            if (entidad.getId() != null) {
                session.merge(entidad);
                log.debug("Entidad actualizada con merge: {}", entidad);
            } else {
                session.persist(entidad);
                log.debug("Entidad persistida: {}", entidad);
            }
        }
    }

    /**
     * Comprueba si una tabla existe en la base de datos.
     *
     * @param session            Sesión Hibernate activa.
     * @param nombreTablaSinEsquema Nombre de la tabla a comprobar.
     * @return true si la tabla existe, false en caso contrario.
     */
    private boolean tablaExiste(Session session, String nombreTablaSinEsquema) {
        String sql = "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = :nombre";
        Long count = ((Number) session.createNativeQuery(sql)
                .setParameter("nombre", nombreTablaSinEsquema)
                .getSingleResult()).longValue();
        log.debug("Tabla '{}' existe: {}", nombreTablaSinEsquema, count > 0);
        return count > 0;
    }

    /**
     * Crea una tabla con el esquema necesario si no existe.
     *
     * @param session           Sesión Hibernate activa.
     * @param nombreTablaConEsquema Nombre completo de la tabla a crear.
     */
    private void crearTabla(Session session, String nombreTablaConEsquema) {
        String sql = "CREATE TABLE IF NOT EXISTS " + nombreTablaConEsquema + " (" +
                "id UUID NOT NULL, " +
                "code VARCHAR(50) NOT NULL PRIMARY KEY, " +
                "nombre VARCHAR(500)" +
                ")";
        session.createNativeQuery(sql).executeUpdate();
        log.debug("Tabla creada o existente: {}", nombreTablaConEsquema);
    }

    /**
     * Inserta una lista de registros en la tabla especificada.
     *
     * @param session       Sesión Hibernate activa.
     * @param tableName     Nombre de la tabla donde se insertan los registros.
     * @param listRegistroGc Lista de registros a insertar.
     */
    private void insertarRegistrosEnTabla(Session session, String tableName, List<RegistroGc> listRegistroGc) {
        StringBuilder sql = new StringBuilder("INSERT INTO " + tableName + " (id, code, nombre) VALUES ");

        for (int i = 0; i < listRegistroGc.size(); i++) {
            RegistroGc registro = listRegistroGc.get(i);

            UUID id = Generators.timeBasedEpochGenerator().generate();
            String code = registro.getCode().replace("'", "''");
            String nombre = registro.getNombre().replace("'", "''");

            sql.append("('").append(id).append("','")
                    .append(code).append("','")
                    .append(nombre).append("')");

            if (i < listRegistroGc.size() - 1) {
                sql.append(", ");
            }
        }

        session.createNativeQuery(sql.toString()).executeUpdate();
        log.debug("Insertados {} registros en la tabla {}", listRegistroGc.size(), tableName);
    }

    /**
     * Obtiene la lista completa de objetos {@link FicheroGc} de la base de datos.
     *
     * @param session Sesión Hibernate activa.
     * @return Lista de entidades FicheroGc recuperadas.
     */
    @Override
    public List<FicheroGc> getListFicherosGc(Session session) {
        List<FicheroGc> result = new ArrayList<>();
        String jpql = "SELECT f FROM FicheroGc f";

        try {
            result = session.createQuery(jpql, FicheroGc.class).getResultList();
            log.info("Recuperados {} registros FicheroGc", result.size());
        } catch (HibernateException ex) {
            log.error("Error obteniendo lista FicheroGc: {}", ex.getMessage(), ex);
            FinalDelPrograma.finalizar(TipoFinalEjecucion.ERROR);
        }
        return result;
    }

    /**
     * Realiza rollback en la transacción y finaliza el programa con código de error.
     *
     * @param transaction Transacción a revertir.
     */
    private void rollbackAndExit(Transaction transaction) {
        try {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
                log.warn("Transacción revertida debido a error.");
            }
        } catch (HibernateException e) {
            log.error("Error durante rollback de transacción: {}", e.getMessage(), e);
        }
        FinalDelPrograma.finalizar(TipoFinalEjecucion.ERROR);
    }
}
