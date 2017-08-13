package system.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import system.model.Role;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class RoleDao {

    @PersistenceContext
    private EntityManager em;

    public void setEm(EntityManager em) {
        this.em = em;
    }

    @Transactional(value = "transactionManager", propagation = Propagation.REQUIRED)
    public Role findRole(String roleName) {
        try {
            return em.createNamedQuery(Role.FIND_USER_ROLE, Role.class)
                    .setParameter("role", roleName)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public RoleDao() {
    }
}
