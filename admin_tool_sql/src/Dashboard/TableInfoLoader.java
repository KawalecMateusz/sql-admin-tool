package Dashboard;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TableInfoLoader {

	public List<TableInfo> load(Connection connection) {

	    List<TableInfo> tables = new ArrayList<>();
	    
	    try {
	        Statement stmt = connection.createStatement();
	        ResultSet rs = stmt.executeQuery("SELECT relname, pg_size_pretty(pg_total_relation_size(relid)) FROM pg_catalog.pg_statio_user_tables ORDER BY pg_total_relation_size(relid) DESC");

	        while(rs.next()) {
	            TableInfo table = new TableInfo();

	            table.setName(rs.getString(1));
	            table.setSize(rs.getString(2));
	            tables.add(table);
	        }
	    }
	    catch(Exception e) {
	        e.printStackTrace();
	    }

	    return tables;
	}
}
