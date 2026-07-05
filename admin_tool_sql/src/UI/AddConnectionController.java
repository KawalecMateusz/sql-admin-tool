package UI;

import Database.ConnectionConfig;
import Database.DBConnection;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;

public class AddConnectionController {

    @FXML private TextField nameField;
    @FXML private TextField hostField;
    @FXML private TextField portField;
    @FXML private TextField dbField;
    @FXML private TextField userField;
    @FXML private PasswordField passwordField;

    private MainController mainController;

    private final DBConnection dbConnection = new DBConnection();
    
    private ConnectionConfig editingConfig = null;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void onSave() {
        try {
            String name = nameField.getText();
            String host = hostField.getText();
            int port = Integer.parseInt(portField.getText());
            String db = dbField.getText();
            String user = userField.getText();
            String pass = passwordField.getText();

            if (editingConfig != null) {

                editingConfig.setName(name);
                editingConfig.setHost(host);
                editingConfig.setPort(port);
                editingConfig.setDatabase(db);
                editingConfig.setUser(user);
                editingConfig.setPassword(pass);

                mainController.refreshList(); // patrz niżej

            } else {

                Connection conn = dbConnection.connect(host, port, db, user, pass);

                ConnectionConfig config = new ConnectionConfig(
                        name, host, port, db, user, pass
                );

                config.setConnection(conn);
                mainController.addConnection(config);
            }

            close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    public void onCancel() {
        close();
    }

    private void close() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
    
    public void setEditMode(ConnectionConfig config) {  	
        this.editingConfig = config;

        nameField.setText(config.getName());
        hostField.setText(config.getHost());
        portField.setText(String.valueOf(config.getPort()));
        dbField.setText(config.getDatabase());
        userField.setText(config.getUser());
        passwordField.setText(config.getPassword());
    }
}