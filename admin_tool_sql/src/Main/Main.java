package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;

import java.sql.DriverManager;

import java.sql.Statement;

public class Main extends Application {
	
	@Override
	public void start(Stage stage) throws Exception {
		
		startLoadTest();
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main.fxml"));
		Scene scene = new Scene(loader.load(), 900, 600);
		
		stage.setTitle("ADMIN SQL TOOL");
		stage.setScene(scene);
		stage.show();
		}

	public static void main(String[] args) {
		launch(args);
	}
	
	
	
	
	
	
	
	
	private void startLoadTest() {

        try {

            Connection db1 = DriverManager.getConnection(

                    "jdbc:postgresql://127.0.0.1:5432/testdb",

                    "postgres",

                    "password"

            );

            Connection db2 = DriverManager.getConnection(

                    "jdbc:postgresql://127.0.0.1:5432/testdb2",

                    "postgres",

                    "password"

            );

            Connection db3 = DriverManager.getConnection(

                    "jdbc:postgresql://127.0.0.1:5432/testdb3",

                    "postgres",

                    "password"

            );

            new Thread(() -> load(db1,1)).start();

            new Thread(() -> load(db2,5)).start();

            new Thread(() -> load(db3,100)).start();

            System.out.println("TEST LOAD STARTED");

        }

        catch(Exception e) {

            e.printStackTrace();

        }

    }

    private void load(Connection connection, int amount) {

        while(true) {

            try {

                for(int i = 0; i < amount; i++) {

                    Statement stmt = connection.createStatement();

                    stmt.executeQuery(

                        "SELECT COUNT(*) FROM pg_catalog.pg_tables"

                    );

                    stmt.close();

                }

                Thread.sleep(100);

            }

            catch(Exception e) {

                e.printStackTrace();

                break;

            }

        }

    }
}


/*
 * 127.0.0.1
 * testdb
 * 
 * testdb2
 * 
 * testdb3
 * 
 * postgres
 * password
 */