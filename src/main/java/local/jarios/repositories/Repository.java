package local.jarios.repositories;

import local.jarios.entity.Log;
import local.jarios.entity.ParseoFicherosGc;
import local.jarios.exceptions.MiRepositoryException;

/** Repositorio de persistencia para importaciones GC. */
public interface Repository {

  /**
   * Persiste el log y el resultado del parseo dentro de una transaccion.
   *
   * @param miLog log a persistir
   * @param parseo resultado del parseo GC
   * @throws MiRepositoryException si falla la persistencia
   */
  void persistirEnBaseDatos(Log miLog, ParseoFicherosGc parseo) throws MiRepositoryException;
}
