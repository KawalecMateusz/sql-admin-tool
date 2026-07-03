package S1;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;


public class MainController {

	@FXML
	private TextArea sqlArea;
	
	@FXML
	private TableView<?> tableView;
	
	private final DBConnection db = new DBConnection();
	private final QueryExecutor executor = new QueryExecutor();
	
	@FXML
	public void initialize() {
		db.connect();
	}
	
	@FXML
	public void onExecute() {
	    String sql = sqlArea.getText();

	    try {
	        var conn = db.getConnection();
	        var stmt = conn.createStatement();
	        var rs = stmt.executeQuery(sql);

	        while (rs.next()) {
	            System.out.println(rs.getObject(1));
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

}
