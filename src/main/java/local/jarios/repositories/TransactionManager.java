package local.jarios.repositories;

import local.jarios.exceptions.MiTransactionManagerException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.Transaction;

/** Gestiona operaciones comunes sobre transacciones Hibernate. */
@Slf4j
public final class TransactionManager {

  /** Constructor privado de clase utilitaria. */
  private TransactionManager() {}

  /**
   * Inicia una nueva transaccion en la sesion Hibernate proporcionada.
   *
   * @param session sesion Hibernate donde se inicia la transaccion
   * @return transaccion iniciada
   */
  public static Transaction beginTransaction(Session session) {
    Transaction transaction = session.beginTransaction();
    log.debug("[beginTransaction] Inicio de transaccion.");
    return transaction;
  }

  /**
   * Realiza commit de la transaccion si esta no esta marcada para rollback.
   *
   * @param transaction transaccion a confirmar
   */
  public static void commitTransaction(Transaction transaction) {
    if (transaction != null && transaction.isActive() && !transaction.getRollbackOnly()) {
      transaction.commit();
      log.debug("[commitTransaction] Commit de la transaccion.");
    } else {
      log.warn(
          "[commitTransaction] No se puede hacer commit porque la transaccion no esta activa "
              + "o esta marcada para rollback.");
    }
  }

  /**
   * Realiza rollback de la transaccion indicada.
   *
   * @param transaction transaccion a revertir
   * @throws MiTransactionManagerException si ocurre un error durante el rollback
   */
  public static void rollbackTransaction(Transaction transaction)
      throws MiTransactionManagerException {
    if (transaction == null) {
      log.warn("[rollbackTransaction] La transaccion es null. No se realiza rollback.");
      return;
    }

    try {
      if (transaction.isActive() && !transaction.getRollbackOnly()) {
        transaction.rollback();
        log.warn("[rollbackTransaction] Rollback ejecutado correctamente.");
      } else {
        log.warn(
            "[rollbackTransaction] La transaccion no esta activa o ya esta marcada para "
                + "rollback. No se realiza rollback.");
      }
    } catch (Exception ex) {
      String msg =
          String.format("[rollbackTransaction] Error haciendo rollback: %s", ex.getMessage());
      log.error(msg, ex);
      throw new MiTransactionManagerException(msg, ex);
    }
  }
}
