package system.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import system.dto.NodesDto;
import system.model.Node;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Repository
public class NodeDao {

    @PersistenceContext(unitName = "p_unit")
    private EntityManager em;

    public void setEm(EntityManager em) {
        this.em = em;
    }

    public NodeDao() {
    }

    @Transactional(value = "transactionManager", propagation = Propagation.REQUIRED)
    public List<Node> getNodesByUserAndParentId(Integer userId, Integer parentId) {

            return em.createNamedQuery(Node.FIND_BY_USERS_AND_PARENTS_IDS, Node.class)
                    .setParameter("userId", userId)
                    .setParameter("parentId", parentId)
                    .getResultList();

    }

    @Transactional(value = "transactionManager", propagation = Propagation.REQUIRED)
    public void save(Node node) {
            em.persist(node);
    }


    @Transactional(value = "transactionManager", propagation = Propagation.REQUIRED)
    public void delete(Node node) {
            Node nodeToDelete = em.getReference(Node.class, node.getNodeId());
            em.remove(nodeToDelete);
    }


    @Transactional(value = "transactionManager", propagation = Propagation.REQUIRED)
    public void edit(Node node) {
            em.merge(node);
    }


    @Transactional(value = "transactionManager", propagation = Propagation.REQUIRED)
    public Node getNodeById(Integer nodeId) {
        try {
            return em.createNamedQuery(Node.FIND_BY_NODE_ID, Node.class)
                    .setParameter("nodeId", nodeId).getSingleResult();
        } catch (NoResultException notFound) {
            return null;
        }
    }


    @Transactional(value = "transactionManager", propagation = Propagation.REQUIRED)
    public List<Node> getNodesByUserId(NodesDto dto, Integer userId) throws CloneNotSupportedException {
            List<Node> result = em.createNamedQuery(Node.FIND_BY_USER_ID, Node.class)
                    .setParameter("userId", userId)
                    .getResultList();
            dto.copyNodes(result);
            return dto.getNodes();
    }

}
