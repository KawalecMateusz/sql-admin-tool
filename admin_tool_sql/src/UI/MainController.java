package UI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import Database.DBConnection;
import Database.QueryExecutor;
import Database.ConnectionManager;

public class MainController {

	@FXML
	private TextArea sqlArea;
	
	@FXML
	private TableView<ObservableList<String>> tableView;
	
	@FXML
	private ListView<ConnectionManager.DBSession> connectionList;
	
	@FXML
	private Label statusLabel;	
	
	private final DBConnection db = new DBConnection();
	private final QueryExecutor executor = new QueryExecutor();
	private final ConnectionManager manager = new ConnectionManager();
	
	@FXML
	public void initialize() {

	    connectionList.setOnMouseClicked(e -> {
	        var selected = connectionList.getSelectionModel().getSelectedItem();
	        if (selected != null) {
	            manager.setActive(selected);
	            updateStatus();
	        }
	    });
	}
	
	@FXML
	public void onExecute() {
	    String sql = sqlArea.getText();

	    try {
	        ConnectionManager.DBSession session = manager.getActive();
	        
	        if(session == null || session.connection == null) {
	        	throw new RuntimeException("No active ceonnection");
	        }
	        var conn = session.connection;
	        var stmt = conn.createStatement();
	        var rs = stmt.executeQuery(sql);
	        TableBuilder.show(tableView, rs);

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	@FXML
	public void onConnect() {
	    try {
	    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/ConnectDialog.fxml"));
	    	Parent root = loader.load();
	    	
	    	ConnectDialogController controller = loader.getController();
	    	controller.setMainConnection(this);
	    	
	    	Stage stage = new Stage();
	    	stage.setScene(new Scene(root));
	    	stage.show();
	    }
	    catch(Exception e) {
	    	e.printStackTrace();
	    }
	}

	@FXML
	public void onDisconnect() {
	    manager.removeActive();
	    
	    connectionList.getItems().clear();
	    connectionList.getItems().addAll(manager.getSessions());
	    
	    updateStatus();
	}

	@FXML
	public void onClear() {
	    sqlArea.clear();
	    tableView.getItems().clear();
	}
	
	public void addConnection(String host, String dbName, Connection conn) {
	    manager.addSession(host, dbName, conn);

	    connectionList.getItems().setAll(manager.getSessions());

	    connectionList.getSelectionModel().selectLast();
	    manager.setActive(manager.getSessions().getLast());

	    updateStatus();
	}

	private void updateStatus() {
		ConnectionManager.DBSession session = manager.getActive();
		
		if(session == null || session.connection == null) {
			statusLabel.setText("Disconnected");
			return;
		}
		statusLabel.setText("Active: " + session.dbName + " | " + session.host);
	}
}
