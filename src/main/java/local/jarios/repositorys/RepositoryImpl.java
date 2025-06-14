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
 * Description: Importación de Ficheros Excel desde Internet
 * Author: Juan Antonio
 * Date: 04/06/2024
 * Team: Contratacion Electrónica
 */
@Slf4j
public class RepositoryImpl implements Repository {

    public RepositoryImpl() {/* CONSTRUCTOR VACÍO */}

    /**
     * Recibe como parámetro una instancia del objeto Estadistica.
     * <p>Este objeto se utiliza para almacenar las estadísticas de la ejecución del aplicativo, de forma
     * que al finalizar la ejecución del aplicativo puedan ser enviadas por email.</p>
     *
     * @param miLog Objeto que contiene la información estadística de la ejecución del aplicativo
     */
    @Override
    public void persistir(Session session, Transaction transaction, Log miLog) {

        try {

            // Persistir el log
            session.persist(miLog);
            log.info("Persistidas las siguientes entidades: Log, Configuracion, Feeds");

        } catch (HibernateException ex) {

            // Registro la excepción con información adicional
            log.error("Error en el método: persistirLog(). Error: {}", ex.getMessage());

            // Deshago los cambios de la transacción en la base de datos
            transaction.rollback();

            // Finaliza la ejecución del programa
            FinalDelPrograma.finalizar(TipoFinalEjecucion.ERROR);

        }
    }

    /**
     * Recibe como parámetro una instancia del objeto Estadistica.
     * <p>Este objeto se utiliza para almacenar las estadísticas de la ejecución del aplicativo, de forma
     * que al finalizar la ejecución del aplicativo puedan ser enviadas por email.</p>
     *
     * @param listFicherosGc Objeto que contiene la información estadística de la ejecución del aplicativo
     */
    @Override
    public void persistir(Session session, Transaction transaction, List<FicheroGc> listFicherosGc) {

        try {

            // Persistir la lista de ficherosGc
            grabarLista(session, listFicherosGc);

        } catch (HibernateException ex) {

            // Registro la excepción con información adicional
            log.error("Error en el método: persistirListFicherosGc(). Error: {}", ex.getMessage());

            // Deshago los cambios de la transacción en la base de datos
            transaction.rollback();

            // Finaliza la ejecución del programa
            FinalDelPrograma.finalizar(TipoFinalEjecucion.ERROR);

        }
    }

    /**
     * @param session Configuración de la sesión actual con la base de datos
     * @param transaction Identificador de la ejecución del programa
     * @param estadistica Objeto que contiene la información estadística de la ejecución del aplicativo
     */
    @Override
    public void persistir(Session session, Transaction transaction, Estadistica estadistica) {

        try {

            // Persistir estadistica
            session.persist(estadistica);
            log.info("Persistidas la entidad: Estadistica");

        } catch (HibernateException ex) {

            // Registro la excepción con información adicional
            log.error("Error en el método: persistirEstadistica(). Error: {}", ex.getMessage());

            // Deshago los cambios de la transacción en la base de datos
            transaction.rollback();

            // Finaliza la ejecución del programa
            FinalDelPrograma.finalizar(TipoFinalEjecucion.ERROR);

        }
    }

    /**
     *
     * @param session Configuración de la sesión actual con la base de datos
     * @param transaction Identificador de la ejecución del programa
     * @param parseoFicherosGc Objeto que contiene el parseo de los ficheros
     */
    @Override
    public void persistir(
            Session session,
            Transaction transaction,
            ParseoFicherosGc parseoFicherosGc,
            PropertiesManager propertiesManager
    ) {

        //
        try {

            //
            for (Map.Entry<String, List<RegistroGc>> entry :
                    parseoFicherosGc.getMapRegistrosGcByFicheroGc().entrySet()) {

                //
                String configPrefijo = propertiesManager.getProperty(
                        Constantes.CONFIG_PROPERTIES,
                        PropertyConstantes.CONFIG_PREFIJO);
                String nombreTablaSinEsquema = configPrefijo + entry.getKey().toLowerCase();

                //
                if (tablaExiste(session, nombreTablaSinEsquema)) {

                    //
                    var dropSql = "DROP TABLE " + nombreTablaSinEsquema;

                    //
                    session.createNativeQuery(dropSql).executeUpdate();
                    log.info(Mensajes.DROP_TABLE, Constantes.TABULADOR_1, nombreTablaSinEsquema);

                }

                //
                crearTabla(session, nombreTablaSinEsquema);
                log.info(Mensajes.CREATE_TABLE, Constantes.TABULADOR_1, nombreTablaSinEsquema);

                //
                insertarRegistrosEnTabla(session, nombreTablaSinEsquema, entry.getValue());
                log.info(
                        Mensajes.INSERT_RECORDS,
                        Constantes.TABULADOR_2,
                        entry.getValue().size(),
                        nombreTablaSinEsquema);

            }

        } catch (HibernateException ex) {

            // Registro la excepción con información adicional
            log.error("Error en el método: persistir(). Error: {}", ex.getMessage());

            // Deshago los cambios de la transacción en la base de datos
            transaction.rollback();

            // Finaliza la ejecución del programa
            FinalDelPrograma.finalizar(TipoFinalEjecucion.ERROR);
        }
    }

    private static <T extends Actualizable<T>> void grabarLista(
            Session session, List<T> lista) throws HibernateException {

        //
        for (T registro : lista) {

            //
            if (registro.getId() != null) {

                //
                session.merge(registro);

            } else {

                //
                session.persist(registro);
            }
        }
    }

    /**
     *
     * @param session Sessión establecida con la base de datos
     * @param nombreTablaSinEsquema Nombre de la tabla incluido el esquema
     * @return boolean Indicando si la tabla existe en la base de datos o no
     */
    private boolean tablaExiste(Session session, String nombreTablaSinEsquema) {

        // SQL nativo para verificar la existencia de la tabla
        String sql = "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = :nombreTablaSinEsquema";

        //
        Long count = (Long) session.createNativeQuery(sql)
                .setParameter("nombreTablaSinEsquema", nombreTablaSinEsquema)
                .getSingleResult();

        //
        return count > 0;
    }

    /**
     *
     * @param session Sessión establecida con la base de datos
     * @param nombreTablaConEsquema Nombre de la tabla incluido el esquema
     */
    private void crearTabla(Session session, String nombreTablaConEsquema) {

        // SQL nativo para crear la tabla
        String createTableSql = "CREATE TABLE IF NOT EXISTS " + nombreTablaConEsquema + " (" +
                "id UUID NOT NULL, " +
                "code VARCHAR(50) NOT NULL PRIMARY KEY, " +
                "nombre VARCHAR(500)" +
                ")";

        // Crear la tabla si no existe
        session.createNativeQuery(createTableSql).executeUpdate();
    }

    /**
     *
     * @param session Sessión establecida con la base de datos
     * @param tableName Tabla en la que se realizará la inserción de los datos (Inserción ÚNICA!!!)
     * @param listRegistroGc Lista de registros que se insertarán en una única vez
     */
    private void insertarRegistrosEnTabla(
            Session session,
            String tableName,
            List<RegistroGc> listRegistroGc) {

        // Usar StringBuilder para construir la consulta de inserción
        StringBuilder insertSql = new StringBuilder("INSERT INTO " + tableName + " (id, code, nombre) VALUES ");

        // Crear los valores para insertar
        for (int i = 0; i < listRegistroGc.size(); i++) {
            RegistroGc registro = listRegistroGc.get(i);

            // Escapar comillas simples en los valores de texto
            UUID id = Generators.timeBasedEpochGenerator().generate();
            String code = registro.getCode().replace("'", "''");      // Escapar comillas simples en 'code'
            String nombre = registro.getNombre().replace("'", "''");  // Escapar comillas simples en 'nombre'

            // Agregar los valores para cada fila
            insertSql.append("(")
                    .append("'").append(id).append("'").append(", ")
                    .append("'").append(code).append("'").append(", ")
                    .append("'").append(nombre).append("'").append(")");

            // Agregar una coma si no es el último registro
            if (i < listRegistroGc.size() - 1) {
                insertSql.append(", ");
            }
        }

        // Ejecutar la consulta
        session.createNativeQuery(insertSql.toString()).executeUpdate();
    }

    /**
     * Devuelve la lista de FicherosGc existente en la base de datos
     * @param session Sessión establecida con la base de datos
     * @return Lista de FicherosGc desde la base de datos
     */
    public List<FicheroGc> getListFicherosGc(Session session) {

        //
        String jpql = "SELECT f FROM FicheroGc f";

        List<FicheroGc> listFicherosGc = new ArrayList<>();

        try {

            listFicherosGc = session.createQuery(jpql, FicheroGc.class).getResultList();

        } catch (HibernateException ex) {

            // Registro la excepción con información adicional
            log.error("Error en el método: getListFicherosGc(). Error: {}", ex.getMessage());

            // Finaliza la ejecución del programa
            FinalDelPrograma.finalizar(TipoFinalEjecucion.ERROR);
        }

        //
        return listFicherosGc;
    }
}
