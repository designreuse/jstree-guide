package system.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import system.model.User;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager em;

    public void setEm(EntityManager em) {
        this.em = em;
    }

    public UserDao() {}

    @Transactional(value = "transactionManager", propagation = Propagation.REQUIRED)
    public User findByUsername(String login) {
        try {
            return em.createNamedQuery(User.FIND_USER_BY_LOGIN, User.class)
                    .setParameter("login", login)
                    .getSingleResult();
        } catch (NoResultException notFound) {
            return null;
        }
    }

    @Transactional(value = "transactionManager", propagation = Propagation.REQUIRED)
    public void save(User userForm) {
        try {
            em.persist(userForm);
        } catch (TransactionException e) {

        }
    }
}
