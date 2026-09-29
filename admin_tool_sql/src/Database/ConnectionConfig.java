/*
 * Stores database connection settings and the current connection state.
 */

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
    private transient ConnectionWorker worker;

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
    public Connection getConnection() {return connection; }

    public void setName(String name) {this.name = name; }
    public void setHost(String host) {this.host = host; }
    public void setPort(int port) {this.port = port; }
    public void setDatabase(String database) {this.database = database; }
    public void setUser(String user) {this.user = user; }
    public void setPassword(String password) {this.password = password; }    
    public void setConnection(Connection connection) {this.connection = connection; }

    public boolean isConnected() {
        return connection != null;
    }

    @Override
    public String toString() {
        return name + " (" + host + ") " +
                (connection != null ? "🟢" : "🔴");
    }
    
    public ConnectionWorker getWorker() {
    	return worker;
    }
    
    public void setWorker(ConnectionWorker worker) {
    	this.worker=worker;
    }
}