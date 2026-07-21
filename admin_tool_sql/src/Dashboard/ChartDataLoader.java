package Dashboard;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

public class ChartDataLoader {

	public DashboardSample load(Connection connection)  throws Exception{
		DashboardSample sample = new DashboardSample();
		
		long start = System.nanoTime();
		
		try (Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT xact_commit, tup_returned, tup_inserted+tup_updated+tup_deleted FROM pg_stat_database WHERE pg_stat_database.datname=current_database()")){

		if(rs.next()) {
			sample.setQueries(rs.getLong(1));
			sample.setRowsReturned(rs.getLong(2));
			sample.setRowsModified(rs.getLong(3));
		}
		
		long stop = System.nanoTime();
		
		sample.setLatency((stop-start)/1000000.0);
		return sample;
		}
	}
}
