package ninja.jira.skeletonkey.app.repository;

import org.springframework.data.repository.CrudRepository;
import java.util.List;

import ninja.jira.skeletonkey.app.entity.RecurringPayment;

/**
 * This class sends MYSQL queries to recurring payment db by interpreting from method names.
 * Provides default CRUD methods.
 */
public interface RecurringPaymentRepository extends CrudRepository<RecurringPayment, Integer> {
    List<RecurringPayment> findRecurringPaymentsByPeriod (String period);
    List<RecurringPayment> findReccuringPaymentsByFromAccount (String fromAccount);
    List<RecurringPayment> findRecurringPaymentsByFromAccountAndPeriod (String fromAccount, String period);
}