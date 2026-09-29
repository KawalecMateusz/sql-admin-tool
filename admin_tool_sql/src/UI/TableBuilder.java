/*
 * Provides helper methods for displaying query results in the SQL view.
 * Builds table columns and rows from a query result or displays a message
 * when a query does not return a result set.
 */

package UI;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import Database.QueryResult;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class TableBuilder {

    public static void show(TableView<ObservableList<String>> tableView, QueryResult result) throws Exception {
        tableView.getColumns().clear();
        tableView.getItems().clear();

        ResultSet rs = result.resultSet;
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            final int columnIndex = i - 1;

            TableColumn<ObservableList<String>, String> column = new TableColumn<>(meta.getColumnName(i));
            column.setCellValueFactory( cellData -> new SimpleStringProperty(cellData.getValue().get(columnIndex)));
            tableView.getColumns().add(column);
        }

        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

        while (rs.next()) {
            ObservableList<String> row = FXCollections.observableArrayList();

            for (int i = 1; i <= columnCount; i++) {
                String value = rs.getString(i);

                if (value == null)
                    value = "NULL";

                row.add(value);
            }
            data.add(row);
        }
        tableView.setItems(data);
        rs.close();

        if (result.stmt != null)
            result.stmt.close();
    }

    public static void showMessage(
            TableView<ObservableList<String>> tableView,
            QueryResult result) {

        tableView.getColumns().clear();
        tableView.getItems().clear();

        TableColumn<ObservableList<String>, String> column = new TableColumn<>("Result");

        column.setCellValueFactory( data -> new SimpleStringProperty(data.getValue().get(0)));
        tableView.getColumns().add(column);
        ObservableList<String> row = FXCollections.observableArrayList();

        row.add(result.message);
        tableView.getItems().add(row);

        if (result.stmt != null) {
            try {
                result.stmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}