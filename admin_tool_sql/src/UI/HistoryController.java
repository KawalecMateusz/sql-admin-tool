package UI;

import Database.QueryHistory;
import Database.QueryHistoryManager;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.List;


public class HistoryController {

    @FXML
    private TextField databaseField;

    @FXML
    private ComboBox<String> typeBox;

    @FXML
    private DatePicker fromDate;

    @FXML
    private DatePicker toDate;

    @FXML
    private TableView<QueryHistory> historyTable;

    @FXML
    private TableColumn<QueryHistory,String> nameColumn;
    
    @FXML
    private TableColumn<QueryHistory,String> databaseColumn;

    @FXML
    private TableColumn<QueryHistory,String> typeColumn;

    @FXML
    private TableColumn<QueryHistory,String> dateColumn;

    @FXML
    private TableColumn<QueryHistory,String> sqlColumn;

    private List<QueryHistory> history;

    @FXML
    public void initialize(){
        history = QueryHistoryManager.load();
        typeBox.setItems(
            FXCollections.observableArrayList(
                "ALL",
                "SELECT",
                "INSERT",
                "UPDATE",
                "DELETE"));
        
        typeBox.setValue("ALL");
        
        nameColumn.setCellValueFactory(data ->
        	    new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        
        databaseColumn.setCellValueFactory( data ->
        	new javafx.beans.property.SimpleStringProperty(data.getValue().getDatabase()));

        typeColumn.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(data.getValue().getType()));

        dateColumn.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(data.getValue().getDate().toString()));

        sqlColumn.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(data.getValue().getSql()));
        
        historyTable.getItems().setAll(history);
    }

    @FXML
    public void onFilter(){

        ObservableList<QueryHistory> filtered =FXCollections.observableArrayList();

        for(QueryHistory q : history){
            boolean ok=true;

            if(!databaseField.getText().isEmpty()) {
                filtered.removeIf(h ->
                	h.getDatabase() == null ||
                    !h.getDatabase().toLowerCase()
                    .contains(databaseField.getText().toLowerCase()));
            }

            if(!typeBox.getValue().equals("ALL")){
                ok &= q.getType().equals(typeBox.getValue());
            }

            LocalDate from = fromDate.getValue();
            LocalDate to = toDate.getValue();

            if(from != null){
                ok &= !q.getDate().toLocalDate().isBefore(from);
            }

            if(to != null){
                ok &= !q.getDate().toLocalDate().isAfter(to);
            }

            if(ok)filtered.add(q);
            }

        historyTable.getItems().setAll(filtered);

    }

    @FXML
    public void onClear(){
        databaseField.clear();
        typeBox.setValue("ALL");
        fromDate.setValue(null);
        toDate.setValue(null);

        historyTable.getItems().setAll(history);
    }
    
    @FXML
    public void onRefresh(){
        history.clear();
        history.addAll(QueryHistoryManager.load());

        historyTable.getItems().setAll(history);
    }

}