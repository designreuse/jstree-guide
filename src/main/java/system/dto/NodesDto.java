package system.dto;


import system.model.Node;

import java.util.ArrayList;
import java.util.List;

public class NodesDto {

    private List<Node> nodes;

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public void copyNodes(List<Node> nodesDao) throws CloneNotSupportedException {
        this.nodes = new ArrayList<>(nodesDao.size());

        for (int i = 0; i < nodesDao.size(); i++) {
            nodes.add(nodesDao.get(i).clone());
            nodes.get(i).setUser(nodesDao.get(i).getUser().clone());
        }
    }
}
