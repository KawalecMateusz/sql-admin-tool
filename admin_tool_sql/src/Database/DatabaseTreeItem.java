package Database;

public class DatabaseTreeItem {

    private final NodeType type;
    private final String name;

    public DatabaseTreeItem(NodeType type, String name) {
        this.type = type;
        this.name = name;
    }

    public NodeType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}