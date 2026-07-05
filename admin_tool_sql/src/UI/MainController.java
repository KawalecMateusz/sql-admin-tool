package UI;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;
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
	
	@FXML
	private ListView<String> connectionsList;
	
	@FXML
	private Label statusLabel;	
	
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
	
	@FXML
	public void onConnect() {
	    statusLabel.setText("Connected");
	}

	@FXML
	public void onDisconnect() {
	    statusLabel.setText("Disconnected");
	}

	@FXML
	public void onClear() {
	    sqlArea.clear();
	    tableView.getItems().clear();
	}

}
