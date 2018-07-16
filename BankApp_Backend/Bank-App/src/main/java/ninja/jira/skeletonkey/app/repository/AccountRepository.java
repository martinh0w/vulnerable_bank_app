package ninja.jira.skeletonkey.app.repository;

import org.springframework.data.repository.CrudRepository;
import java.util.List;

import ninja.jira.skeletonkey.app.entity.Account;

/**
 * This class sends MYSQL queries to account db by interpreting from method names.
 * Provides default CRUD methods.
 */
public interface AccountRepository extends CrudRepository<Account, String> {
    List<Account> findAccountsByUserID(String userID);
}