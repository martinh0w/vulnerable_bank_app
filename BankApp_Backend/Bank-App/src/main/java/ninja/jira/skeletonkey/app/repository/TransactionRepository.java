package ninja.jira.skeletonkey.app.repository;

import org.springframework.data.repository.CrudRepository;
import java.util.List;
import java.util.Date;

import ninja.jira.skeletonkey.app.entity.Transaction;

/**
 * This class sends MYSQL queries to transaction db by interpreting from method names.
 * Provides default CRUD methods.
 */
public interface TransactionRepository extends CrudRepository<Transaction, Integer> {
    List<Transaction> findTransactionsByFromAccount (String fromAccount);
    List<Transaction> findTransactionsByFromAccountAndToAccount (String fromAccount, String toAccount);
    List<Transaction> findTransactionsByFromAccountAndDate (String fromAccount, Date date);
}
