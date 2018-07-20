package ninja.jira.skeletonkey.app.repository;

import org.springframework.data.repository.CrudRepository;
import java.util.List;

import ninja.jira.skeletonkey.app.entity.Card;

/**
 * This class sends MYSQL queries to card db by interpreting from method names.
 * Provides default CRUD methods.
 */
public interface CardRepository extends CrudRepository<Card, String> {
    List<Card> findCardsByAccountNumber(String accountNumber);
}