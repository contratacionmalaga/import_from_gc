package local.jarios.services;

import local.jarios.entity.Log;
import local.jarios.entity.ParseoFicherosGc;
import local.jarios.exceptions.MiRepositoryException;
import local.jarios.exceptions.MiServiceException;
import local.jarios.repositories.Repository;
import local.jarios.repositories.RepositoryImpl;
import lombok.extern.slf4j.Slf4j;

/** Servicio encargado de persistir el resultado del parseo GC. */
@Slf4j
public class ServiceImpl implements Service {

  /** Repositorio de persistencia. */
  private final Repository repository;

  /**
   * Constructor que inicializa los componentes de persistencia.
   *
   * @throws MiServiceException si ocurre un error creando el repositorio
   */
  public ServiceImpl() throws MiServiceException {
    try {
      this.repository = new RepositoryImpl();
      log.debug("[ServiceImpl] Creado el objeto RepositoryImpl correctamente.");
    } catch (MiRepositoryException ex) {
      String msg = String.format("[ServiceImpl] Error creando el constructor: %s", ex.getMessage());
      log.error(msg, ex);
      throw new MiServiceException(msg, ex);
    }
  }

  /**
   * Persiste un objeto {@link Log} y el parseo GC en base de datos.
   *
   * @param miLog log a persistir
   * @param parseoFicherosGc parseo GC a persistir
   * @throws MiServiceException si falla la persistencia
   */
  public void persistirEnBaseDeDatos(Log miLog, ParseoFicherosGc parseoFicherosGc)
      throws MiServiceException {
    try {
      repository.persistirEnBaseDatos(miLog, parseoFicherosGc);
      log.debug("[persistirLog] Grabacion en base de datos correcta.");
    } catch (MiRepositoryException ex) {
      String msg =
          String.format(
              "[persistirLog] Error persistiendo Log con ID %s: %s",
              miLog.getId(), ex.getMessage());
      log.error(msg, ex);
      throw new MiServiceException(msg, ex);
    }
  }
}
