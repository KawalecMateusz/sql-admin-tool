package UI;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import Database.DBConnection;
import Database.QueryExecutor;


public class MainController {

	@FXML
	private TextArea sqlArea;
	
	@FXML
	private TableView<ObservableList<String>> tableView;
	
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
	        TableBuilder.show(tableView, rs);

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

}
