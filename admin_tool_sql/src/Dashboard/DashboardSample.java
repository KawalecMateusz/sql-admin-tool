package Dashboard;

public class DashboardSample {

	private long queries;
	private long rowsReturned;
	private long rowsModified;
	private double latency;
	
	public long getQueries() {
		return queries;
	}
	public void setQueries(long queries) {
		this.queries=queries;
	}
	
	public long getRowsReturned() {
		return rowsReturned;
	}
	public void setRowsReturned(long rowsReturned) {
		this.rowsReturned=rowsReturned;
	}
	
	public long getRowsModified() {
		return rowsModified;
	}
	public void setRowsModified(long rowsModified) {
		this.rowsModified=rowsModified;
	}
	
	public double getLatency() {
		return latency;
	}
	public void setLatency(double latency) {
		this.latency=latency;
	}
}
