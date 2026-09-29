/*
 * Executes a SQL query and prepares its result for display.
 */

package Database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class QueryExecutor {

    public QueryResult execute(Connection conn, String sql) throws SQLException {

        Statement stmt = conn.createStatement();
        boolean isResultSet = stmt.execute(sql);
        QueryResult result = new QueryResult();

        if(isResultSet) {
            result.hasTable = true;
            result.resultSet = stmt.getResultSet();
        }
        else {
            result.hasTable = false;
            result.message ="Rows affected: " + stmt.getUpdateCount();
        }
        return result;
    }
}