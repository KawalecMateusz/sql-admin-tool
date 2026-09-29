/*
 * Runs database queries for a connection using a dedicated worker thread.
 */

package Database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ConnectionWorker extends Thread {
    private Connection connection;
    private boolean running = true;
    private BlockingQueue<SQLTask> sqlQueue = new LinkedBlockingQueue<>();
    
    public ConnectionWorker(ConnectionConfig config, Connection connection) {
        this.connection = connection;
        setName("DB-Worker-" + config.getName());
    }
    
    public Connection getConnection() {
        return connection;
    }
    
    public boolean isConnected() {
        return connection != null;
    }
    
    @Override
    public void run() {
        while(running) {
            try {
                SQLTask task = sqlQueue.take();
                Statement stmt = connection.createStatement();
                boolean result = stmt.execute(task.getsql());
                QueryResult queryresult;
                
                if(result) {
                    ResultSet rs = stmt.getResultSet();
                    queryresult = new QueryResult(true, rs, stmt, null);
                }
                else {
                    int count = stmt.getUpdateCount();
                    queryresult = new QueryResult(false, null, stmt, "Rows affected: " + count);
                }
                task.complete(queryresult);
                
            } catch(InterruptedException e) {
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public void stopWorker() {
        running = false;
        interrupt();
    }
    
    public void executeSQL(SQLTask task) {
        sqlQueue.add(task);
    }
}