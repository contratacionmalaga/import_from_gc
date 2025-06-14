package local.jarios.repositorys;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * Description:
 * Author: juan
 * Date: 28/12/2024
 * Team:
 */
public class TransactionManager {

    /**
     * Constructor sin argumentos.
     */
    public TransactionManager() {
        // Constructor vacío
    }

    /**
     * Metodo que obtiene una sesión con la base de datos
     *
     * @param sessionFactory Configuración del acceso a la base de datos
     * @return Sesión con la base de datos
     */
    public Session getSession(SessionFactory sessionFactory) {

        return sessionFactory.openSession();
    }

    /**
     * Metodo utilizado para iniciar una transacción
     *
     * @param session Sesión con la base de datos
     * @return Transacción devuelta
     */
    public Transaction beginTransaction(Session session) {

        return session.beginTransaction();
    }

    //
    /**
     * Metodo para cerrar la sesión
     *
     * @param session Sesión con la base de datos
     */

    public void closeSession(Session session) {

        //
        if (session != null && session.isOpen()) {

            session.close();
        }
    }

    /**
     * Metodo para realizar commit
     *
     * @param transaction Transacción sobre la que se realiza el commit
     */
    public void commitTransaction(Transaction transaction) {

        //
        if (transaction != null) {

            transaction.commit();
        }
    }

    /**
     * Realiza un rollback (deshace) la transacción proporcionada si no es nula.
     * <p>
     * Este método garantiza que la transacción se revierta para evitar
     * que cambios no deseados queden persistidos en caso de error.
     * </p>
     *
     * @param transaction La transacción que se desea revertir. Si es {@code null}, no se realiza ninguna acción.
     */
    public void rollbackTransaction(Transaction transaction) {

        //
        if (transaction != null) {

            transaction.rollback();
        }
    }
}
