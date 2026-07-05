package Database;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class ConnectionManager {

	public static class DBSession{
		public String host;
		public String dbName;
		public Connection connection;
		
		public DBSession(String host, String dbName, Connection connection) {
			this.host=host;
			this.dbName=dbName;
			this.connection=connection;
		}
		
		@Override
		public String toString() {
			return "(" + host + " | " + dbName + ")";
		}
	}
	
	private final List<DBSession> sessions = new ArrayList<>();
	private DBSession active;
	
	public void addSession(String host, String dbName, Connection conn) {
		DBSession session = new DBSession(host, dbName, conn);
		sessions.add(session);
		active=session;
	}
	
	public void removeActive() {
		if(active!=null) {
			try {
				active.connection.close();
			}
			catch(Exception ignored) {}
			
			sessions.remove(active);
			active=sessions.isEmpty()? null : sessions.get(0);
		}
	}
	
	public List<DBSession> getSessions(){
		return sessions;
	}
	
	public DBSession getActive() {
		return active;
	}
	
	public void setActive(DBSession session) {
		this.active=session;
	}
}
