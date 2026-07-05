package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
	
	private Connection connection;
	
	public Connection connect(String host, int port, String db, String user, String pass) {
		try {
			String url = "jdbc:postgresql://" + host + ":" + port + "/" + db;
			connection = DriverManager.getConnection(url, user, pass);
			return connection;
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public Connection getConnection() {
		return connection;
	}
	
	public void disconnect() {
		try {
			if(connection!=null) connection.close();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
}
