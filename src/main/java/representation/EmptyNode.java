package representation;

import java.util.List;

public class EmptyNode implements Node {
    private final List<Node> nodes;

    public EmptyNode(List<Node> nodes) {
        this.nodes = nodes;
    }

    public List<Node> getNodes() {
        return nodes;
    }
}
