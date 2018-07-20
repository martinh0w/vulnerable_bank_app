package ninja.jira.skeletonkey.app.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ninja.jira.skeletonkey.app.entity.User;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * This class sends MYSQL queries to user db by interpreting from method names.
 * Provides default CRUD methods.
 */
public interface UserRepository extends CrudRepository<User, String>, UserRepositoryCustom {

}