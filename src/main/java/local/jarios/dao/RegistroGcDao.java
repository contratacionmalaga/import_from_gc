package local.jarios.dao;

import com.fasterxml.uuid.Generators;
import local.jarios.common.util.PropertiesFiles;
import local.jarios.common.util.PropertiesKeys;
import local.jarios.entity.RegistroGc;
import local.jarios.exceptions.MiRepositoryException;
import local.jarios.properties.api.PropertiesManagerService;
import local.jarios.properties.exception.PropertiesManagerException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.util.List;
import java.util.UUID;

/**
 * DAO (Data Access Object) para la entidad {@link RegistroGc}.
 * <p>
 * Proporciona métodos para verificar la existencia de tablas, crearlas dinámicamente,
 * insertar registros y sanitizar entradas.
 */
@Slf4j
public class RegistroGcDao {

    /** Objeto para gestionar los ficheros properties. */
    private final PropertiesManagerService propertyManager;

    /**
     * Constructor de la clase
     * @param propertiesManager Manajeador de los ficheros properties
     */
    public RegistroGcDao(PropertiesManagerService propertiesManager) {
        this.propertyManager = propertiesManager;
    }

    /**
     * Verifica si una tabla existe en la base de datos.
     *
     * @param session     sesión de Hibernate activa
     * @param nombreTabla nombre de la tabla a verificar
     * @return {@code true} si la tabla existe, {@code false} en caso contrario
     */
    public boolean existeTabla(Session session, String nombreTabla) {
        String sql = "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = :nombre";
        log.debug("[existeTabla] - SQL: {}", sql);
        long count = ((Number) session.createNativeQuery(sql)
                .setParameter("nombre", nombreTabla)
                .getSingleResult()).longValue();
        log.debug("[existeTabla] - Count: {}", count);
        return count > 0;
    }

    /**
     * Crea una tabla en la base de datos si no existe.
     *
     * @param session     sesión de Hibernate activa
     * @param nombreTabla nombre de la tabla a crear
     */
    public void crearTabla(Session session, String nombreTabla) throws MiRepositoryException{

        try {

            String encoding = propertyManager.getProperty(PropertiesFiles.APP, PropertiesKeys.APP_CHARACTER_ENCODING);
            String collate = propertyManager.getProperty(PropertiesFiles.APP, PropertiesKeys.APP_CONNECTION_COLLATION);

            String sql = "CREATE TABLE IF NOT EXISTS " + nombreTabla + " (" +
                    "id UUID NOT NULL, " +
                    "code VARCHAR(50) NOT NULL PRIMARY KEY, " +
                    "nombre VARCHAR(500), " +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    ") CHARACTER SET = " + encoding +" COLLATE = " + collate + ";";
            log.info("[crearTabla] - SQL: {}", sql);
            session.createNativeQuery(sql).executeUpdate();

        } catch (HibernateException | PropertiesManagerException ex) {

            String msg = String.format("[getSessionFactory] - Error creando SessionFactory: %s", ex.getMessage());
            log.error(msg, ex.getMessage());
            throw new MiRepositoryException(msg, ex);

        }
    }

    /**
     * Inserta una lista de registros en la tabla especificada.
     *
     * @param session      sesión de Hibernate activa
     * @param nombreTabla  nombre de la tabla destino
     * @param registros    lista de objetos {@link RegistroGc} a insertar
     */
    public void insertarRegistros(Session session, String nombreTabla, List<RegistroGc> registros) {
        if (registros.isEmpty()) {
            log.warn("[insertarRegistros] - Lista de registros vacía.");
            return;
        }

        StringBuilder sql = new StringBuilder("INSERT INTO " + nombreTabla + " (id, code, nombre) VALUES ");

        for (int i = 0; i < registros.size(); i++) {
            RegistroGc r = registros.get(i);
            UUID id = Generators.timeBasedEpochGenerator().generate();

            sql.append("('")
                    .append(id).append("','")
                    .append(sanitizar(r.getCode())).append("','")
                    .append(sanitizar(r.getNombre())).append("')");

            if (i < registros.size() - 1) {
                sql.append(", ");
            }
        }

        log.debug("[insertarRegistros] - SQL: {}", sql);
        session.createNativeQuery(sql.toString()).executeUpdate();
        log.debug("[insertarRegistros] - Insertados {} registros en {}", registros.size(), nombreTabla);
    }

    /**
     * Escapa comillas simples en cadenas para evitar errores de SQL o inyecciones.
     *
     * @param valor cadena a sanitizar
     * @return cadena sanitizada (comillas simples duplicadas)
     */
    private String sanitizar(String valor) {
        return (valor == null) ? "" : valor.replace("'", "''");
    }

    /**
     * Elimina una tabla de la base de datos si existe.
     *
     * @param session     sesión de Hibernate activa
     * @param nombreTabla nombre de la tabla a eliminar
     */
    public void eliminarTabla(Session session, String nombreTabla) {
        String sql = "DROP TABLE IF EXISTS " + nombreTabla;
        log.info("[eliminarTabla] - SQL: {}", sql);
        session.createNativeQuery(sql).executeUpdate();
    }

}
