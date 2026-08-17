package local.jarios.dao;

import com.fasterxml.uuid.Generators;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import local.jarios.common.util.PropertiesFiles;
import local.jarios.common.util.PropertiesKeys;
import local.jarios.entity.RegistroGc;
import local.jarios.exceptions.MiRepositoryException;
import local.jarios.properties.api.PropertiesManagerService;
import local.jarios.properties.exception.PropertiesManagerException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.Session;

/** DAO para la entidad {@link RegistroGc}. */
@Slf4j
public class RegistroGcDao {

  /** Patron permitido para identificadores SQL generados por la aplicacion. */
  private static final Pattern SAFE_SQL_IDENTIFIER = Pattern.compile("[A-Za-z0-9_]+");

  /** Objeto para gestionar los ficheros properties. */
  private final PropertiesManagerService propertyManager;

  /**
   * Constructor de la clase.
   *
   * @param propertiesManager manejador de los ficheros properties
   */
  public RegistroGcDao(PropertiesManagerService propertiesManager) {
    this.propertyManager = propertiesManager;
  }

  /**
   * Verifica si una tabla existe en la base de datos.
   *
   * @param session sesion de Hibernate activa
   * @param nombreTabla nombre de la tabla a verificar
   * @return {@code true} si la tabla existe, {@code false} en caso contrario
   */
  public boolean existeTabla(Session session, String nombreTabla) {
    validarIdentificadorSql(nombreTabla, "nombreTabla");
    String sql = "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = :nombre";
    log.debug("[existeTabla] SQL: {}", sql);
    long count =
        ((Number)
                session
                    .createNativeQuery(sql)
                    .setParameter("nombre", nombreTabla)
                    .getSingleResult())
            .longValue();
    log.debug("[existeTabla] Count: {}", count);
    return count > 0;
  }

  /**
   * Crea una tabla en la base de datos si no existe.
   *
   * @param session sesion de Hibernate activa
   * @param nombreTabla nombre de la tabla a crear
   * @throws MiRepositoryException si falla la creacion de la tabla
   */
  public void crearTabla(Session session, String nombreTabla) throws MiRepositoryException {
    try {
      validarIdentificadorSql(nombreTabla, "nombreTabla");
      String encoding =
          propertyManager.getProperty(PropertiesFiles.APP, PropertiesKeys.APP_CHARACTER_ENCODING);
      String collate =
          propertyManager.getProperty(PropertiesFiles.APP, PropertiesKeys.APP_CONNECTION_COLLATION);
      validarIdentificadorSql(encoding, "encoding");
      validarIdentificadorSql(collate, "collate");

      String sql =
          "CREATE TABLE IF NOT EXISTS "
              + nombreTabla
              + " ("
              + "id UUID NOT NULL, "
              + "code VARCHAR(50) NOT NULL PRIMARY KEY, "
              + "nombre VARCHAR(500), "
              + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP"
              + ") CHARACTER SET = "
              + encoding
              + " COLLATE = "
              + collate
              + ";";
      log.info("[crearTabla] SQL: {}", sql);
      session.createNativeQuery(sql).executeUpdate();
    } catch (HibernateException | PropertiesManagerException ex) {
      String msg =
          String.format("[getSessionFactory] Error creando SessionFactory: %s", ex.getMessage());
      log.error(msg, ex.getMessage());
      throw new MiRepositoryException(msg, ex);
    }
  }

  /**
   * Inserta una lista de registros en la tabla especificada.
   *
   * @param session sesion de Hibernate activa
   * @param nombreTabla nombre de la tabla destino
   * @param registros lista de objetos {@link RegistroGc} a insertar
   */
  public void insertarRegistros(Session session, String nombreTabla, List<RegistroGc> registros) {
    validarIdentificadorSql(nombreTabla, "nombreTabla");

    if (registros.isEmpty()) {
      log.warn("[insertarRegistros] Lista de registros vacia.");
      return;
    }

    StringBuilder sql =
        new StringBuilder("INSERT INTO " + nombreTabla + " (id, code, nombre) VALUES ");

    for (int i = 0; i < registros.size(); i++) {
      sql.append("(:id")
          .append(i)
          .append(", :code")
          .append(i)
          .append(", :nombre")
          .append(i)
          .append(")");

      if (i < registros.size() - 1) {
        sql.append(", ");
      }
    }

    var query = session.createNativeQuery(sql.toString());
    for (int i = 0; i < registros.size(); i++) {
      RegistroGc registro = registros.get(i);
      UUID id = Generators.timeBasedEpochGenerator().generate();

      query.setParameter("id" + i, id.toString());
      query.setParameter("code" + i, registro.getCode());
      query.setParameter("nombre" + i, registro.getNombre());
    }

    log.debug(
        "[insertarRegistros] SQL parametrizado preparado para {} registros.", registros.size());
    query.executeUpdate();
    log.debug("[insertarRegistros] Insertados {} registros en {}", registros.size(), nombreTabla);
  }

  /**
   * Valida identificadores SQL generados por la aplicacion.
   *
   * @param valor valor a validar
   * @param campo nombre logico del campo validado
   */
  private void validarIdentificadorSql(String valor, String campo) {
    if (valor == null || !SAFE_SQL_IDENTIFIER.matcher(valor).matches()) {
      throw new IllegalArgumentException(
          "Identificador SQL no permitido en " + campo + ": " + valor);
    }
  }

  /**
   * Elimina una tabla de la base de datos si existe.
   *
   * @param session sesion de Hibernate activa
   * @param nombreTabla nombre de la tabla a eliminar
   */
  public void eliminarTabla(Session session, String nombreTabla) {
    validarIdentificadorSql(nombreTabla, "nombreTabla");
    String sql = "DROP TABLE IF EXISTS " + nombreTabla;
    log.info("[eliminarTabla] SQL: {}", sql);
    session.createNativeQuery(sql).executeUpdate();
  }
}
