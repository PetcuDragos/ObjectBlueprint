package representation;

public class ValueNode implements Node {

    private final String value;

    public ValueNode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
