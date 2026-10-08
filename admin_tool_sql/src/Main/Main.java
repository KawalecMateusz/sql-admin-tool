/*
 * Starts the application and initializes the main JavaFX window.
 */

package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	
	@Override
	public void start(Stage stage) throws Exception {
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main.fxml"));
		Scene scene = new Scene(loader.load(), 1350, 900);
		
		scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
		
		stage.setTitle("ADMIN SQL TOOL");
		stage.setScene(scene);
		stage.show();
		}

	public static void main(String[] args) {
		launch(args);
	}
}