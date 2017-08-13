package system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import system.dao.NodeDao;
import system.dao.RoleDao;
import system.dto.NodeDto;
import system.model.Node;
import system.model.NodeResponseBody;
import system.model.User;

import java.util.ArrayList;
import java.util.List;

@Service("NodeService")
public class NodeService {
    enum pasteModes {CUT("cut"), COPY("copy");
        private final String text;

        private pasteModes(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    @Autowired
    private NodeDao nodeDao;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private UserService userService;

    public List<NodeResponseBody> findChildren(Integer userId, Integer parentId) {
        List<Node> nodes = nodeDao.getNodesByUserAndParentId(userId, parentId);
        List<NodeResponseBody> result = new ArrayList<>();

        for (Node node:
             nodes) {
            result.add(new NodeResponseBody
                    (String.valueOf(node.getNodeId()), node.getContent(), "true", "true", "false"));
        }
        return result;
    }


    public NodeDto save(String parentId, String username) {
        User user = userService.findUserByLogin(username);

        Node node = new Node();
        node.setUser(user);
        int parId = Integer.parseInt(parentId);
        node.setParentId(parId);
        node.setContent("New node!");

        nodeDao.save(node);
        List<Node> usersNodes = nodeDao.getNodesByUserAndParentId(user.getUserId(), parId);
        Node lastNode = usersNodes.get(usersNodes.size() - 1);
        if (usersNodes.size() > 0)
//            return usersNodes.get(usersNodes.size() - 1);
            return new NodeDto(lastNode.getNodeId(), lastNode.getParentId(), lastNode.getContent());
        else return null;
    }


    public void delete(String nodeId, String username) {
        User user = userService.findUserByLogin(username);
        recursiveDelete(Integer.parseInt(nodeId), user.getUserId());
    }


    public void edit(String nodeId, String title) {

        Node node = nodeDao.getNodeById(Integer.parseInt(nodeId));
        node.setContent(title);
        nodeDao.edit(node);
    }


    public void paste(String sourceNodeId, String destinyNodeId, String username, String pasteMode) {

        User user = userService.findUserByLogin(username);

        if (pasteMode.equals(pasteModes.CUT.toString())) {
            Node node = nodeDao.getNodeById(Integer.parseInt(sourceNodeId));
            node.setParentId(Integer.parseInt(destinyNodeId));
            nodeDao.edit(node);
        }
        if (pasteMode.equals(pasteModes.COPY.toString())) {
            recursiveCopy(Integer.parseInt(sourceNodeId), Integer.parseInt(destinyNodeId), user);
        }
    }

    private void recursiveCopy(Integer sourceId, Integer destId, User user) {
        Node node = nodeDao.getNodeById(sourceId);
        Node nodeCopy = new Node();
        nodeCopy.setUser(user);
        nodeCopy.setContent(node.getContent());
        nodeCopy.setParentId(destId);

        nodeDao.save(nodeCopy);

        List<Node> destForChildrenList = nodeDao.getNodesByUserAndParentId(user.getUserId(), destId);
        Node destForChildren = destForChildrenList.get(destForChildrenList.size() - 1);

        // Children of copying node
        List<Node> children = nodeDao.getNodesByUserAndParentId(user.getUserId(), node.getNodeId());

        if (children.size() > 0) {
            for (Node child : children) {
                recursiveCopy(child.getNodeId(), destForChildren.getNodeId(), user);
            }
        }
    }


    private void recursiveDelete(Integer nodeId, int userId) {
        List<Node> children = nodeDao.getNodesByUserAndParentId(userId, nodeId);
        if (children.size() == 0) {
            nodeDao.delete(nodeDao.getNodeById(nodeId));
        } else {
            for (Node child : children) {
                recursiveDelete(child.getNodeId(), userId);
            }
            nodeDao.delete(nodeDao.getNodeById(nodeId));
        }
    }


    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setNodeDao(NodeDao nodeDao) {
        this.nodeDao = nodeDao;
    }

    public NodeDao getNodeDao() {
        return nodeDao;
    }

    public BCryptPasswordEncoder getbCryptPasswordEncoder() {
        return bCryptPasswordEncoder;
    }

    public void setbCryptPasswordEncoder(BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public RoleDao getRoleDao() {
        return roleDao;
    }

    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }
}
