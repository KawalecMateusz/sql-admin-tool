/*
 * Stores the result of an executed SQL query.
 */

package Database;

import java.sql.ResultSet;
import java.sql.Statement;

public class QueryResult {

    public boolean hasTable;
    public ResultSet resultSet;
    public Statement stmt;
    public String message;

    public QueryResult() {

    }
    
    public QueryResult(boolean hasTable, ResultSet resultSet, Statement stmt, String message) {
        this.hasTable = hasTable;
        this.resultSet = resultSet;
        this.stmt = stmt;
        this.message = message;
    }
}