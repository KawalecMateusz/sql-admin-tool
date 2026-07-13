package UI;

import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;

import javafx.scene.layout.BorderPane;

import java.sql.Connection;

import Database.DBConnection;
import Database.ConnectionManager;
import Database.ConnectionConfig;
import Database.ConnectionStorage;
import Database.ConnectionWorker;

public class MainController {

	@FXML
	private ListView<ConnectionConfig> connectionList;
	
	@FXML
	private Label statusLabel;	
	
	@FXML
	private BorderPane mainPane;
	
	private final DBConnection db = new DBConnection();
	private final ConnectionManager manager = new ConnectionManager();
	private SQLController sqlController;
	
	@FXML
	public void initialize() {

	    List<ConnectionConfig> loaded = ConnectionStorage.load();
	    manager.setSessions(loaded);

	    connectionList.getSelectionModel().selectedItemProperty()
	    .addListener((obs, oldValue, newValue) -> {
	        if(newValue != null) {

	            manager.setActive(newValue);

	            updateStatus();

	            if(sqlController != null){
	                sqlController.refreshTree();
	            }
	        }});

	    updateList();
	    updateStatus();
	}
	@FXML
	public void showDashboard(){

	    try {
	        Parent root = FXMLLoader.load(getClass().getResource("/dashboard.fxml"));
	        mainPane.setCenter(root);

	    } catch(Exception e){
	        e.printStackTrace();
	    }
	}
	
	@FXML
	public void showSQL(){

	    try {
	    	FXMLLoader loader =
	    	        new FXMLLoader(getClass().getResource("/sql.fxml"));
	    	Parent root = loader.load();
	    	
	    	sqlController = loader.getController();
	    	sqlController.setManager(manager);
	    	mainPane.setCenter(root);

	    } catch(Exception e){
	        e.printStackTrace();
	    }
	}
	
	
	@FXML
	public void onConnect() {
	    ConnectionConfig config = manager.getActive();
	    if (config == null) return;

	    try {
	    	Connection conn = db.connect( config.getHost(),config.getPort(),config.getDatabase(),config.getUser(),config.getPassword());

	    	config.setConnection(conn);
	       ConnectionWorker worker = new ConnectionWorker(config, conn);
	       
	       config.setWorker(worker);
	       
	       worker.start();

	       if(sqlController != null){
	    	    sqlController.refreshTree();
	    	}
	       
	        updateList();
	        updateStatus();

	        saveState();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	@FXML
	public void onHistory(){

	    try{

	        FXMLLoader loader =
	            new FXMLLoader(
	              getClass().getResource("/History.fxml")
	            );

	        Parent root = loader.load();

	        Stage stage = new Stage();

	        stage.setTitle("Query history");
	        stage.setScene(new Scene(root));
	        stage.show();


	    }catch(Exception e){
	        e.printStackTrace();
	    }
	}
	
	@FXML
	public void onDisconnect() {
	    ConnectionConfig config = manager.getActive();
	    if (config == null) return;

	    try {
	    	if(config.getWorker() != null) {
	    	    config.getWorker().stopWorker();
	    	    config.setWorker(null);
	    	}
	    	if(config.getConnection() != null) {
	    	    config.getConnection().close();
	    	    config.setConnection(null);
	    	}

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    updateList();
	    updateStatus();
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
	
	private void saveState() {
	    ConnectionStorage.save(manager.getSessions());
	}
	
	public ConnectionManager getManager(){
	    return manager;
	}
	
	public void refreshList() {
	    updateList();
	    updateStatus();
	}
	
}
