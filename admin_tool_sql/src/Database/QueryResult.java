package Database;

import java.sql.ResultSet;

public class QueryResult {

    public boolean hasTable;
    public ResultSet resultSet;
    public String message;

    public QueryResult() {

    }
    
    public QueryResult(boolean hasTable, ResultSet resultSet, String message) {
        this.hasTable = hasTable;
        this.resultSet = resultSet;
        this.message = message;
    }
}