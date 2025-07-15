package local.jarios.repositories;

import local.jarios.entity.FicheroGc;
import local.jarios.entity.Log;
import local.jarios.entity.ParseoFicherosGc;
import local.jarios.exceptions.MiRepositoryException;

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
     * @param miLog Objeto Log a persistir
     */
    void persistirLog(Log miLog) throws MiRepositoryException;

    /**
     * Persiste un objeto ParseoFicherosGc en la base de datos dentro de una transacción.
     *
     * @param parseoFicherosGc Objeto ParseoFicherosGc a persistir
     * @param prefijo Prefijo utilizado en la creación de las tablas.
     */
    void persistirObjetoParseoFicherosGc(ParseoFicherosGc parseoFicherosGc, String prefijo)
            throws MiRepositoryException;

    /**
     * Obtiene la lista de objetos FicheroGc desde la base de datos.
     *
     * @return Lista de FicheroGc recuperados
     */
    List<FicheroGc> getListFicherosGc() throws MiRepositoryException;
}
