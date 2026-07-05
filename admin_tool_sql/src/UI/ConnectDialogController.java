package UI;

import Database.DBConnection;
import Database.ConnectionManager;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;

public class ConnectDialogController {

	@FXML private TextField host;
	@FXML private TextField db;
	@FXML private TextField user;
	@FXML private PasswordField password;
	
	private MainController mainController;
	
	public void setMainConnection(MainController controller) {
		this.mainController=controller;
	}
	
	@FXML public void onConnect() {
		try {
			DBConnection dbConn = new DBConnection();
			
			Connection conn = dbConn.connect(
					host.getText(),
					db.getText(),
					user.getText(),
					password.getText()
					);
			
			mainController.addConnection(host.getText(), db.getText(), conn);
			
			((Stage) host.getScene().getWindow()).close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
