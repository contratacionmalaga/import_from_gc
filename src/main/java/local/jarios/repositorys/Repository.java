package local.jarios.repositorys;

import local.jarios.entity.Estadistica;
import local.jarios.entity.FicheroGc;
import local.jarios.entity.Log;
import local.jarios.models.ParseoFicherosGc;
import local.jarios.properties.config.PropertiesManager;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

/**
 * Interface para operaciones de persistencia relacionadas con la importación de ficheros Excel desde Internet.
 * Define métodos para guardar entidades y obtener listas desde la base de datos usando Hibernate.
 *
 * @author Juan Antonio
 * @since 04/06/2024
 * @author Contratacion Electrónica
 */
public interface Repository {

    /**
     * Persiste un objeto Log en la base de datos dentro de una transacción.
     *
     * @param session La sesión Hibernate activa
     * @param transaction La transacción en curso
     * @param miLog Objeto Log a persistir
     */
    void persistir(
            Session session,
            Transaction transaction,
            Log miLog
    );

    /**
     * Persiste una lista de objetos FicheroGc en la base de datos dentro de una transacción.
     *
     * @param session La sesión Hibernate activa
     * @param transaction La transacción en curso
     * @param listFicherosGc Lista de FicheroGc a persistir
     */
    void persistir(
            Session session,
            Transaction transaction,
            List<FicheroGc> listFicherosGc
    );

    /**
     * Persiste un objeto Estadistica en la base de datos dentro de una transacción.
     *
     * @param session La sesión Hibernate activa
     * @param transaction La transacción en curso
     * @param estadistica Objeto Estadistica a persistir
     */
    void persistir(
            Session session,
            Transaction transaction,
            Estadistica estadistica
    );

    /**
     * Persiste un objeto ParseoFicherosGc en la base de datos dentro de una transacción.
     *
     * @param session La sesión Hibernate activa
     * @param transaction La transacción en curso
     * @param parseoFicherosGc Objeto ParseoFicherosGc a persistir
     * @param propertiesManager Objeto PropertiesManager que me permite el acceso a las key de los ficheros
     */
    void persistir(
            Session session,
            Transaction transaction,
            ParseoFicherosGc parseoFicherosGc,
            PropertiesManager propertiesManager
    );

    /**
     * Obtiene la lista de objetos FicheroGc desde la base de datos.
     *
     * @param session La sesión Hibernate activa
     * @return Lista de FicheroGc recuperados
     */
    List<FicheroGc> getListFicherosGc(Session session);
}
