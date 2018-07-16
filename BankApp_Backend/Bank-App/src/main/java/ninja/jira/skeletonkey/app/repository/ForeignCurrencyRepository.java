package ninja.jira.skeletonkey.app.repository;

import org.springframework.data.repository.CrudRepository;
import java.util.List;

import ninja.jira.skeletonkey.app.entity.ForeignCurrency;

/**
 * This class sends MYSQL queries to foreign currency db by interpreting from method names.
 * Provides default CRUD methods.
 */
public interface ForeignCurrencyRepository extends CrudRepository<ForeignCurrency, String> {
    List<ForeignCurrency> findForeignCurrencyByCurrencyName (String currencyName);
}