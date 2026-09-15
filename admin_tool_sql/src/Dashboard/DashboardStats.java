/*
 * Stores statistics used by the dashboard.
 */

package Dashboard;

public class DashboardStats {
	
	private int connectionsActive;
	private int connectionsIdle;
	private double cacheHit;
	private int deadlocks;
	private long latency;
    
    public int getConnectionsActive() {
        return connectionsActive;
    }

    public void setConnectionsActive(int connections) {
        this.connectionsActive = connections;
    }
    
    public int getConnectionsIdle() {
        return connectionsIdle;
    }

    public void setConnectionsIdle(int connections) {
        this.connectionsIdle = connections;
    }

    public double getCacheHit() {
        return cacheHit;
    }

    public void setCacheHit(double cacheHit) {
        this.cacheHit = cacheHit;
    }

    public int getDeadlocks() {
        return deadlocks;
    }

    public void setDeadlocks(int deadlocks) {
        this.deadlocks = deadlocks;
    }

    public long getLatency() {
        return latency;
    }

    public void setLatency(long latency) {
        this.latency = latency;
    }
}
