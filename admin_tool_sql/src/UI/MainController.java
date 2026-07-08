package UI;

import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import Database.DBConnection;
import Database.QueryExecutor;
import Database.ConnectionManager;
import Database.ConnectionConfig;
import Database.ConnectionStorage;
import Database.DatabaseTreeItem;
import Database.SchemaLoader;

public class MainController {

	@FXML
	private TextArea sqlArea;
	
	@FXML
	private TableView<ObservableList<String>> tableView;
	
	@FXML
	private ListView<ConnectionConfig> connectionList;
	
	@FXML
	private Label statusLabel;	
	
	@FXML
	private TreeView<DatabaseTreeItem> databaseTree;
	
	private final DBConnection db = new DBConnection();
	private final QueryExecutor executor = new QueryExecutor();
	private final ConnectionManager manager = new ConnectionManager();
	private final SchemaLoader schemaLoader = new SchemaLoader();
	
	@FXML
	public void initialize() {

	    List<ConnectionConfig> loaded = ConnectionStorage.load();
	    manager.setSessions(loaded);

	    connectionList.getSelectionModel().selectedItemProperty()
	        .addListener((obs, oldValue, newValue) -> {
	            if(newValue != null) {

	                manager.setActive(newValue);

	                updateStatus();
	                updateDatabaseTree();
	            }});

	    updateList();
	    updateStatus();
	}
	
	@FXML
	public void onExecute() {
	    String sql = sqlArea.getText();

	    try {
	        ConnectionConfig session = manager.getActive();
	        
	        if(session == null || session.getConnection() == null) {
	        	throw new RuntimeException("No active ceonnection");
	        }
	        var conn = session.getConnection();
	        var stmt = conn.createStatement();
	        var rs = stmt.executeQuery(sql);
	        TableBuilder.show(tableView, rs);

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	@FXML
	public void onConnect() {
	    ConnectionConfig config = manager.getActive();
	    if (config == null) return;

	    try {
	        Connection conn = db.connect(
	                config.getHost(),
	                config.getPort(),
	                config.getDatabase(),
	                config.getUser(),
	                config.getPassword()
	        );

	        config.setConnection(conn);

	        config.setConnection(conn);

	        updateList();
	        updateStatus();
	        updateDatabaseTree();

	        saveState();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	@FXML
	public void onDisconnect() {
	    ConnectionConfig config = manager.getActive();
	    if (config == null) return;

	    try {
	        if (config.getConnection() != null)
	            config.getConnection().close();

	        config.setConnection(null);

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    updateList();
	    updateStatus();
	    updateDatabaseTree();
	}
	@FXML
	public void onClear() {
	    sqlArea.clear();
	    tableView.getItems().clear();
	}
	
	@FXML
	public void onAdd() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddConnectionDialog.fxml"));
			Parent root = loader.load();
			
			AddConnectionController controller = loader.getController();
			controller.setMainController(this);
			
			Stage stage = new Stage();
			stage.setTitle("Add connection");
			stage.setScene(new Scene(root));
			stage.show();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	public void onRemove() {
	    ConnectionConfig session = manager.getActive();
	    if (session == null) return;

	    manager.removeSession(session);

	    updateList();
	    updateStatus();

	    saveState();
	}
	
	@FXML
	public void onEdit() {
	    ConnectionConfig selected = connectionList.getSelectionModel().getSelectedItem();
	    if (selected == null) return;

	    try {
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddConnectionDialog.fxml"));
	        Parent root = loader.load();

	        AddConnectionController controller = loader.getController();
	        controller.setMainController(this);
	        controller.setEditMode(selected);

	        Stage stage = new Stage();
	        stage.setTitle("Edit connection");
	        stage.setScene(new Scene(root));
	        stage.show();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public void addConnection(ConnectionConfig config) {

	    manager.addSession(config);
	    updateList();

	    connectionList.getSelectionModel().select(config);

	    updateStatus();
	    updateDatabaseTree();

	    saveState();
	}

	private void updateStatus() {
	    ConnectionConfig session = manager.getActive();

	    if (session == null) {
	        statusLabel.setText("Disconnected");
	        return;
	    }

	    statusLabel.setText(
	        session.getName() +
	        " | " +
	        (session.getConnection() != null ? "ONLINE" : "OFFLINE")
	    );
	}
	
	private void updateList() {
	    connectionList.getItems().setAll(manager.getSessions());
	}
	
	private void updateDatabaseTree() {

	    ConnectionConfig config = manager.getActive();

	    if(config == null || config.getConnection() == null) {
	        databaseTree.setRoot(null);
	        return;
	    }

	    databaseTree.setRoot(
	        schemaLoader.load(
	            config.getConnection(),
	            config.getDatabase()
	        )
	    );
	}
	
	private void saveState() {
	    ConnectionStorage.save(manager.getSessions());
	}
	
	public void refreshList() {
	    updateList();
	    updateStatus();
	    updateDatabaseTree();
	}
}
