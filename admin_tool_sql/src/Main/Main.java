package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	
	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main.fxml"));
		Scene scene = new Scene(loader.load(), 900, 600);
		
		stage.setTitle("ADMIN SQL TOOL");
		stage.setScene(scene);
		stage.show();
		}

	public static void main(String[] args) {
		launch(args);
	}
}


/*
 * 127.0.0.1
 * testdb
 * postgres
 * password
 * 
 * testdb2
 * books
 * 
 * testdb3
 * departments
 */