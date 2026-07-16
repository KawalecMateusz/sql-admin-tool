package Dashboard;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class StatsCollector {


    public DashboardStats collect(Connection connection) {

        DashboardStats stats = new DashboardStats();

        try {

            Statement stmt = connection.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT count(*) FROM pg_stat_activity WHERE state='active'");
            if(rs.next()) {
                stats.setConnectionsActive(rs.getInt(1));
            }
            
            rs = stmt.executeQuery("SELECT count(*) FROM pg_stat_activity WHERE state='idle'");
            if(rs.next()) {
                stats.setConnectionsIdle(rs.getInt(1));
            }

            rs = stmt.executeQuery("SELECT round((sum(blks_hit) * 100.0) /(sum(blks_hit)+sum(blks_read)),2)FROM pg_stat_database");
            if(rs.next()) {
                stats.setCacheHit(rs.getDouble(1));
            }

            rs = stmt.executeQuery("SELECT sum(deadlocks)FROM pg_stat_database");
            if(rs.next()) {
                stats.setDeadlocks(rs.getInt(1));
            }

            long start = System.currentTimeMillis();
            stmt.execute("SELECT 1");
            long end = System.currentTimeMillis();
            stats.setLatency(end-start);

        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return stats;
    }
}