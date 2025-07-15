package local.jarios.repositories;

import local.jarios.entity.Log;
import local.jarios.entity.ParseoFicherosGc;
import local.jarios.exceptions.MiRepositoryException;

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
     * @param parseo Objeto que contiene un Map con el nombre del FicheroGc y los Registros asociados
     */
    void persistirEnBaseDatos(Log miLog, ParseoFicherosGc parseo) throws MiRepositoryException;

}
