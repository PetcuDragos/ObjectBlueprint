package representation;

public class NameNode implements Node {
    private final String value;
    private final Node nodeForValue;

    public NameNode(String value, Node node) {
        this.value = value;
        this.nodeForValue = node;
    }

    public String getValue() {
        return value;
    }

    public Node getNode() {
        return nodeForValue;
    }
}
