package system.dto;


public class NodeDto {

    private int nodeId;

    private int parentId;

    private String content;


    public NodeDto(int nodeId, int parentId, String content) {
        this.nodeId = nodeId;
        this.parentId = parentId;
        this.content = content;
    }

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "NodeDto{" +
                "nodeId=" + nodeId +
                ", parentId=" + parentId +
                ", content='" + content + '\'' +
                '}';
    }
}
