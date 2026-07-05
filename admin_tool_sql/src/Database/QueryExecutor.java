package Database;

import java.sql.*;

public class QueryExecutor {
	public String execute(Connection conn, String sql) throws SQLException{
		
		Statement stmt = conn.createStatement();
		boolean isResultSet = stmt.execute(sql);
		
		if(isResultSet) {
			ResultSet rs = stmt.getResultSet();
			return resultSetToString(rs);
		}
		else {
			int updated = stmt.getUpdateCount();
			return "Rows affected: " + updated;
		}
	}
	
	private String resultSetToString(ResultSet rs) throws SQLException{
		
		StringBuilder sb = new StringBuilder();
		
		ResultSetMetaData meta=rs.getMetaData();
		int cols=meta.getColumnCount();
		
		while(rs.next()) {
			for(int i=1;i<=cols;i++) {
				sb.append(rs.getString(i)).append(" | ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
}
