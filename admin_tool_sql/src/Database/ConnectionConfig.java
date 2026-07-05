package Database;

import java.io.Serializable;
import java.sql.Connection;

public class ConnectionConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String host;
    private int port;
    private String database;
    private String user;
    private String password;

    private transient Connection connection;

    public ConnectionConfig(String name, String host, int port, String database, String user, String password) {
        this.name = name;
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
    }

    public String getName() { return name; }
    public String getHost() { return host; }
    public int getPort() { return port; }
    public String getDatabase() { return database; }
    public String getUser() { return user; }
    public String getPassword() { return password; }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public boolean isConnected() {
        return connection != null;
    }

    @Override
    public String toString() {
        return name + " (" + host + ") " +
                (connection != null ? "🟢" : "🔴");
    }
}