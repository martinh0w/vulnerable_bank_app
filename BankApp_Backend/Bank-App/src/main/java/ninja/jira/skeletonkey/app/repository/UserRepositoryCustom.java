package ninja.jira.skeletonkey.app.repository;

import ninja.jira.skeletonkey.app.entity.User;

import java.util.List;

/**
 * This class is used to provide an interface for custom user SQL queries in UserRepositoryImpl
 */
public interface UserRepositoryCustom {

    List<User> login(String userID, String PIN);
}
