package local.jarios.services;

import local.jarios.entity.Log;
import local.jarios.entity.ParseoFicherosGc;

/** Servicio de persistencia del resultado de una importacion GC. */
public interface Service {

  /**
   * Persiste el log y el resultado del parseo en base de datos.
   *
   * @param miLog log a persistir
   * @param parseo resultado del parseo GC
   */
  void persistirEnBaseDeDatos(Log miLog, ParseoFicherosGc parseo);
}
