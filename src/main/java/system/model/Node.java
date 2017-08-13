package system.model;

import javax.persistence.*;


@NamedQueries({
        @NamedQuery(name = Node.FIND_BY_USER_ID,
                query = "select n from Node n where n.user.userId = :userId"),
        @NamedQuery(name = Node.FIND_BY_USERS_AND_PARENTS_IDS,
                query = "select n from Node n where n.user.userId = :userId and n.parentId = :parentId"),
        @NamedQuery(name = Node.FIND_BY_NODE_ID,
                query = "select n from Node n where n.nodeId = :nodeId")
})
@Entity
@Table(name = "nodes")
public class Node implements Cloneable {

    public static final String FIND_BY_USER_ID = "Node.findById";
    public static final String FIND_BY_USERS_AND_PARENTS_IDS = "Node.findByUsersAndParentsIds";
    public static final String FIND_BY_NODE_ID = "Node.findByNodeId";


    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(name="user_id")
    User user;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "node_id", insertable = false, updatable = false)
    private int nodeId;

    @Column(name = "parent_id")
    private int parentId;

    @Column(name = "content")
    private String content;

    public Node(User user, int parentId, String content) {
        this.parentId = parentId;
        this.content = content;
        this.user = user;
    }

    public Node() {
    }



    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int node_id) {
        this.nodeId = node_id;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parent_id) {
        this.parentId = parent_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public Node clone() throws CloneNotSupportedException {
        return  (Node) super.clone();

    }

/*
    @Override
    public String toString() {
        return "Node{" +
                "user=" + user +
                ", nodeId=" + nodeId +
                ", userId=" + user.getUserId() +
                ", parentId=" + parentId +
                ", content='" + content + '\'' +
                '}';
    }
*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        if (nodeId != node.nodeId) return false;
        if (parentId != node.parentId) return false;
        return content.equals(node.content);
    }

    @Override
    public int hashCode() {
        int result = nodeId;
        result = 31 * result + parentId;
        result = 31 * result + content.hashCode();
        return result;
    }
}
