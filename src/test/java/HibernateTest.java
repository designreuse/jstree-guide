import org.junit.AfterClass;
import org.junit.BeforeClass;

import org.junit.Test;
import system.model.Node;
import system.model.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;


public class HibernateTest {
    private static EntityManagerFactory emf;
    private static EntityManager em;

    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("Test postgresql");
        em = emf.createEntityManager();
    }

    @AfterClass
    public static void tearDownClass() {
        System.out.println("Close EntityManagerFactory");
        em.close();
        emf.close();
    }

    /**
     * Create 2 users, add them to DB and get All of them
     */
    @Test
    public void testCreateUsers() throws Exception {
        System.out.println("begin testCreateUsers");
        String login1 = "testLogin1CreateU";
        String login2 = "testLogin2CreateU";
        em.getTransaction().begin();
        try {
            // Create 2 users
            User testUser1 = new User(login1);
            em.persist(testUser1);
            User testUser2 = new User(login2);
            em.persist(testUser2);
            List<User> allUsers = (List<User>) em.createNamedQuery(User.ALL_USERS, User.class)
                    .getResultList();
            System.out.println(allUsers);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.getTransaction().commit();
        }
        System.out.println("End of testCreateUsers");

    }

    /**
     * Create some nodes, add it to DB and get All of them
     */
    @Test
    public void testCreateNodes() {
        System.out.println("begin testCreateNodes");
        String login1 = "testLogin1CreateN";
        String login2 = "testLogin2CreateN";
        em.getTransaction().begin();
        User testUser1 = new User(login1);
        em.persist(testUser1);
        User testUser2 = new User(login2);
        em.persist(testUser2);
        try{
            for (int i = 1; i <= 10; i++) {
                Node node1 = new Node(testUser1, i % 2 + 1, "content1 " + i);
                em.persist(node1);
                Node node2 = new Node(testUser2, i % 2 + 1, "content2 " + i);
                em.persist(node2);
            }
            em.refresh(testUser1);
            em.refresh(testUser2);

            // get by id
            List<Node> listById = (List<Node>) em.createNamedQuery(Node.FIND_BY_USER_ID, Node.class)
                    .setParameter("userId", testUser1.getUserId())
                    .getResultList();
            System.out.println(listById);
            assertEquals(testUser1.getUserId(), listById.get(0).getUser().getUserId());

            listById = (List<Node>) em.createNamedQuery(Node.FIND_BY_USER_ID, Node.class)
                    .setParameter("userId", testUser2.getUserId())
                    .getResultList();
            System.out.println(listById);
            assertEquals(testUser2.getUserId(), listById.get(0).getUser().getUserId());
//            assertSame(listById, testUser2.getNodes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.getTransaction().commit();

        }
        System.out.println("End of testCreateNodes");
    }

    /**
     * Search in DB by userId and parentId. Print results of searching.
     */
    @Test
    public void testFindByUserAndParent() {
        System.out.println("begin testFindByUserAndParent");
        String login1 = "testLogin1Find";
        String login2 = "testLogin2Find";
        em.getTransaction().begin();
        User testUser1 = new User(login1);
        em.persist(testUser1);
        User testUser2 = new User(login2);
        em.persist(testUser2);
        try{
            for (int i = 1; i <= 10; i++) {
                Node node1 = new Node(testUser1, i % 2 + 1, "content11" + i);
                em.persist(node1);
                Node node2 = new Node(testUser2, i % 2 + 1, "content22" + i);
                em.persist(node2);
            }

            List<Node> listByIdUserAndParent = (List<Node>) em.createNamedQuery(Node.FIND_BY_USERS_AND_PARENTS_IDS, Node.class)
                    .setParameter("userId", testUser1.getUserId())
                    .setParameter("parentId", 1)
                    .getResultList();
            System.out.println(listByIdUserAndParent);
            assertEquals(testUser1.getUserId(), listByIdUserAndParent.get(0).getUser().getUserId());
            assertEquals(1, listByIdUserAndParent.get(0).getParentId());


            listByIdUserAndParent = (List<Node>) em.createNamedQuery(Node.FIND_BY_USERS_AND_PARENTS_IDS, Node.class)
                    .setParameter("userId", testUser2.getUserId())
                    .setParameter("parentId", 2)
                    .getResultList();
            System.out.println(listByIdUserAndParent);
            assertEquals(testUser2.getUserId(), listByIdUserAndParent.get(0).getUser().getUserId());
            assertEquals(2, listByIdUserAndParent.get(0).getParentId());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.getTransaction().commit();
        }
        System.out.println("End of testFindByUserAndParent");
    }



}
