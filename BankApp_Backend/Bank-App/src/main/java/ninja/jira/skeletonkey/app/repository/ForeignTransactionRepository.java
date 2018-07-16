package ninja.jira.skeletonkey.app.repository;

import org.springframework.data.repository.CrudRepository;

import ninja.jira.skeletonkey.app.entity.ForeignTransaction;

/**
 * This class sends MYSQL queries to foreign transaction db by interpreting from method names.
 * Provides default CRUD methods.
 */
public interface ForeignTransactionRepository extends CrudRepository<ForeignTransaction, Integer> {

}