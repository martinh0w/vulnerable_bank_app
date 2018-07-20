package ninja.jira.skeletonkey.app.repository;

import org.springframework.data.repository.CrudRepository;
import java.util.List;

import ninja.jira.skeletonkey.app.entity.Payee;

/**
 * This class sends MYSQL queries to payee db by interpreting from method names.
 * Provides default CRUD methods.
 */
public interface PayeeRepository extends CrudRepository<Payee, Integer> {
    List<Payee> findByUserID (String userID);
    List<Payee> findPayeesByUserIDAndAccountNumber (String userID, String accountNumber);
}