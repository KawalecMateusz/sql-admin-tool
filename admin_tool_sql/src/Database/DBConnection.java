package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
	
	private Connection connection;
	
	public void connect() {
		try {
			connection = DriverManager.getConnection(
							"jdbc:postgresql://localhost:5432/testdb",
							"postgres",
							"password");
			
			System.out.println("COnnection to DB");
		}
		catch (SQLException e) {
			e.printStackTrace();
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
