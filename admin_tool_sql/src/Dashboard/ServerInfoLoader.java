package Dashboard;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import Database.ConnectionConfig;

public class ServerInfoLoader {

	public ServerInfo load(ConnectionConfig config) throws Exception{
		Connection conn = config.getConnection();
		
		ServerInfo info = new ServerInfo();
		
		info.setName(config.getName());
		info.setHost(config.getHost());
		info.setPort(config.getPort());
		info.setDatabase(config.getDatabase());
		
		Statement stmt = conn.createStatement();
		
		ResultSet rs = stmt.executeQuery("SELECT version()");
		if(rs.next()) {
			info.setVersion(rs.getString(1));
		}
		rs.close();
		
		rs = stmt.executeQuery("SELECT now() - pg_postmaster_start_time();");
		if(rs.next()) {
			info.setUptime(rs.getString(1));
		}
		rs.close();
		stmt.close();
		
		return info;
	}
}
