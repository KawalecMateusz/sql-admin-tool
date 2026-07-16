package UI;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import Database.ConnectionManager;
import Database.ConnectionConfig;

import Dashboard.ServerInfo;
import Dashboard.ServerInfoLoader;
import Dashboard.StatsCollector;
import Dashboard.DashboardStats;

public class DashboardController {

	@FXML
	private Label name;
	
	@FXML
	private Label database;
	
	@FXML
	private Label address;
	
	@FXML
	private Label port;
	
	@FXML
	private Label version;
	
	@FXML
	private Label uptime;
	
	@FXML
	private Label connectionActive;
	
	@FXML
	private Label connectionIdle;

	@FXML
	private Label latency;

	@FXML
	private Label cache;

	@FXML
	private Label deadlock;
	
	private final ServerInfoLoader loader = new ServerInfoLoader();
	private final StatsCollector collector = new StatsCollector();
	
	private ConnectionManager manager;
	
	public void setManager(ConnectionManager manager) {
	    this.manager = manager;
	}
	
	public void refreshDashboard(){

	    ConnectionConfig config = manager.getActive();

	    if(config == null)
	        return;

	    if(config.getConnection() == null)
	        return;

	    try{

	        ServerInfo info = loader.load(config);
	        DashboardStats stats =collector.collect(config.getConnection());
	        
	        name.setText(info.getName());
	        database.setText(info.getDatabase());
	        address.setText(info.getHost());
	        port.setText(String.valueOf(info.getPort()));
	        version.setText(formatVersion(info.getVersion()));
	        uptime.setText(formatUptime(info.getUptime()));
	        
	        connectionActive.setText(String.valueOf(stats.getConnectionsActive()));
	        connectionIdle.setText(String.valueOf(stats.getConnectionsIdle()));
	        latency.setText(stats.getLatency()+" ms");
	        cache.setText(stats.getCacheHit()+"%");
	        deadlock.setText(String.valueOf(stats.getDeadlocks()));
	    }
	    catch(Exception e){
	        e.printStackTrace();
	    }
	}
	
	private String formatVersion(String version){

	    if(version == null)
	        return "-";

	    String[] parts = version.split(" ");

	    for(String part : parts){
	        if(part.matches("\\d+\\.\\d+.*")){
	            return part;
	        }
	    }

	    return version;
	}
	
	private String formatUptime(String uptime){

	    if(uptime == null)
	        return "-";

	    String days = "";
	    String time = "";

	    if(uptime.contains("days")){
	        String[] split = uptime.split("days");
	        days = split[0].trim();
	        time = split[1].trim();
	    }
	    else if(uptime.contains("day")){
	        String[] split = uptime.split("day");
	        days = split[0].trim();
	        time = split[1].trim();
	    }
	    else {
	        time = uptime.trim();
	    }
	    String[] t = time.split(":");

	    if(t.length >= 2){
	        return days + "d " + t[0] + "h " + t[1] + "m";
	    }

	    return uptime;
	}
}
