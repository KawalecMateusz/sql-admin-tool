package Database;

import java.sql.Connection;
import javafx.scene.control.TreeItem;

import Database.DatabaseTreeItem;
import Database.NodeType;

public class SchemaLoader {
	
	public static TreeItem<DatabaseTreeItem> load(Connection conn,
            String databaseName) {

		TreeItem<DatabaseTreeItem> root = new TreeItem<>( new DatabaseTreeItem(
						NodeType.DATABASE,
						databaseName));

		TreeItem<DatabaseTreeItem> tablesFolder = new TreeItem<>( new DatabaseTreeItem(
						NodeType.FOLDER,
						"Tables"));
		
		try {

		    String sql = """
		        SELECT table_name
		        FROM information_schema.tables
		        WHERE table_schema='public'
		        ORDER BY table_name
		        """;

		    var stmt = conn.createStatement();
		    var rs = stmt.executeQuery(sql);

		    while(rs.next()) {

		        String tableName = rs.getString("table_name");

		        TreeItem<DatabaseTreeItem> tableItem = new TreeItem<>(
		                        new DatabaseTreeItem(
		                                NodeType.TABLE,
		                                tableName));

		        tablesFolder.getChildren().add(tableItem);}
		}
		catch(Exception e) {
		    e.printStackTrace();
		}
		
		root.getChildren().add(tablesFolder);

		return root;
	}
}
