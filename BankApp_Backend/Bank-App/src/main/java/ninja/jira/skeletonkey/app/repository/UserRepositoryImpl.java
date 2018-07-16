package ninja.jira.skeletonkey.app.repository;


import ninja.jira.skeletonkey.app.entity.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * This class is an implementation of custom login method to write custom SQL queries.
 * This is done so that userID and PIN is not dynamic, in order to make it vulnerable.
 */
@Repository
@Transactional(readOnly = true)
public class UserRepositoryImpl implements UserRepositoryCustom{
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<User> login(String userID, String PIN) {
        //SQL injection code to bypass login
        //(Single quotes) ' or ''='
        Query query = entityManager.createNativeQuery("SELECT u.userID, u.PIN FROM user u where u.userID ='" + userID + "' and u.PIN='" + PIN + "'");
        return query.getResultList();
    }
}
