package Dashboard;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ActiveConnectionLoader {

	public List<ActiveConnectionInfo> load(Connection connection) {

	    List<ActiveConnectionInfo> connections = new ArrayList<>();

	    try {

	        Statement stmt = connection.createStatement();

	        ResultSet rs = stmt.executeQuery("SELECT pid, usename, datname, client_addr, state, application_name FROM pg_stat_activity WHERE datname IS NOT NULL ORDER BY pid");


	        while(rs.next()) {

	            ActiveConnectionInfo info = new ActiveConnectionInfo();

	            info.setPid(rs.getInt("pid"));
	            info.setUser(rs.getString("usename"));
	            info.setDatabase(rs.getString("datname"));
	            info.setClient(rs.getString("client_addr"));
	            info.setState(rs.getString("state"));
	            info.setApplication(rs.getString("application_name"));
	            String client = rs.getString(6);
	            if(client == null)
	                client = "local";
	            info.setClient(client);
	            
	            connections.add(info);
	        }

	    }
	    catch(Exception e) {
	        e.printStackTrace();
	    }

	    return connections;
	}
}