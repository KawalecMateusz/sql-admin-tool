package UI;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeView;
import java.util.List;

import Database.*;

public class SQLController {


    @FXML
    private TextArea sqlArea;

    @FXML
    private TableView<ObservableList<String>> tableView;

    @FXML
    private TreeView<DatabaseTreeItem> databaseTree;

    private final SchemaLoader schemaLoader = new SchemaLoader();
    
    private ConnectionManager manager;


    private final List<QueryHistory> history =
            QueryHistoryManager.load();


    public void setManager(ConnectionManager manager){
        this.manager = manager;
    }


    @FXML
    public void onExecute(){

        String sql = sqlArea.getText();


        try {

            ConnectionConfig session =manager.getActive();


            if(session == null || session.getConnection() == null){
                throw new RuntimeException("No active connection");
            }
            
            session.getWorker().executeSQL(new SQLTask(sql,result -> {
                    javafx.application.Platform.runLater(()->{

                        try {
                            if(result.hasTable){TableBuilder.show(
                                    tableView, result.resultSet);
                            }
                            else {
                                TableBuilder.showMessage(
                                    tableView, result.message);
                            }
                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }

                    });

                })

            );


            String type =sql.trim().split(" ")[0].toUpperCase();

            history.add(new QueryHistory(
            		session.getName(), session.getDatabase(), sql, type));
            
            QueryHistoryManager.save(history);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    @FXML
    public void onClear(){
    	sqlArea.clear();
    	tableView.getItems().clear();
    }
    
    public void refreshTree(){

        ConnectionConfig config =  manager.getActive();

        if(config == null ||
           config.getConnection() == null){
        	databaseTree.setRoot(null);
            return;
        }

        databaseTree.setRoot(
            schemaLoader.load(config.getConnection(), config.getDatabase()));

    }
}