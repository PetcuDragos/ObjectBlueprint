package format;

import representation.*;

import java.util.stream.Collectors;

public class JsonBlueprint {


    public static String getJsonString(Node node) {
        return processNode(node);
    }

    private static String processNode(Node node) {
        if (node instanceof ValueNode) {
            return ((ValueNode) node).getValue();
        }
        if (node instanceof NameNode) {
            return ((NameNode) node).getValue() + ":" + processNode(((NameNode) node).getNode());
        }
        if (node instanceof CollectionNode) {
            return "[" + ((EmptyNode) node).getNodes().stream().map(JsonBlueprint::processNode).collect(Collectors.joining(",")) + "]";
        }
        if (node instanceof EmptyNode) {
            return "{" + ((EmptyNode) node).getNodes().stream().map(JsonBlueprint::processNode).collect(Collectors.joining(",")) + "}";
        }
        return "Problem with representation JSON";
    }

}
