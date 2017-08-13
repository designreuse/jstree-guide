package system.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@NamedQueries({
        @NamedQuery(name = User.ALL_USERS,
                query = "select u from User u"),
        @NamedQuery(name = User.FIND_USER_BY_LOGIN,
                query = "select u from User u where username = :login")
})
@Entity
@Table(name = "users")
public class User implements Cloneable {
    public static final String ALL_USERS = "User.allUsers";
    public static final String FIND_USER_BY_LOGIN = "User.findByUsername";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private
    int userId;

    @Column(name = "login", unique = true)
    private String username;

    @Column(name = "password")
    private String password;

    @Transient
    private String confirmPassword;

    @JsonIgnore
    @OneToMany(targetEntity = Node.class,
            fetch = FetchType.LAZY, //EAGER LAZY
            cascade = CascadeType.ALL,
            mappedBy = "user")
    private List<Node> nodes = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;


    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int user_id) {
        this.userId = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String login) {
        this.username = login;
    }

    public User() {
    }

    public User(String name) {
        this.username = name;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    @Override
    public User clone() throws CloneNotSupportedException {
        return  (User) super.clone();
    }

    @Override
    public String toString() {
        return "User{" +
                "user_id=" + userId +
                ", login='" + username + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (userId != user.userId) return false;
        return username.equals(user.username);
    }

    @Override
    public int hashCode() {
        int result = userId;
        result = 31 * result + username.hashCode();
        return result;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
