/*
 * Controls the dashboard view by loading database statistics and updating
 * server information, connection tables, and monitoring charts.
 */

package UI;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.LineChart;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import Database.ConnectionManager;
import Database.ConnectionConfig;

import Dashboard.ServerInfo;
import Dashboard.ServerInfoLoader;
import Dashboard.StatsCollector;
import Dashboard.DashboardStats;
import Dashboard.TableInfoLoader;
import Dashboard.TableInfo;
import Dashboard.ActiveConnectionLoader;
import Dashboard.ActiveConnectionInfo;
import Dashboard.ChartDataLoader;
import Dashboard.DashboardSample;
import Dashboard.ChartHistory;

public class DashboardController {

	@FXML private Label name;
	@FXML private Label database;
	@FXML private Label address;
	@FXML private Label port;
	@FXML private Label version;
	@FXML private Label uptime;
	@FXML private Label connectionActive;
	@FXML private Label connectionIdle;
	@FXML private Label latency;
	@FXML private Label cache;
	@FXML private Label deadlock;
	
	@FXML private TableView<ActiveConnectionInfo> activeConnections;
	@FXML private TableColumn<ActiveConnectionInfo, Integer> pidColumn;
	@FXML private TableColumn<ActiveConnectionInfo, String> userColumn;
	@FXML private TableColumn<ActiveConnectionInfo, String> databaseColumn;
	@FXML private TableColumn<ActiveConnectionInfo,String> clientColumn;
	@FXML private TableColumn<ActiveConnectionInfo, String> stateColumn;
	
	@FXML private TableView<TableInfo> tablesSize;
	@FXML private TableColumn<TableInfo,String> tableNameColumn;
	@FXML private TableColumn<TableInfo,String> tableSizeColumn;
	
	@FXML private LineChart<String, Number> latencyChart;
	@FXML private LineChart<String, Number> queriesChart;
	@FXML private LineChart<String, Number> rowsModifiedChart;
	@FXML private LineChart<String, Number> rowsReturnedChart;
	
	private final ServerInfoLoader loader = new ServerInfoLoader();
	private final StatsCollector collector = new StatsCollector();
	private final TableInfoLoader tableLoader = new TableInfoLoader();
	private final ActiveConnectionLoader connectionLoader = new ActiveConnectionLoader();
	private final ChartDataLoader chartLoader = new ChartDataLoader();
	private final ChartHistory history = new ChartHistory();
	
	private Timeline refreshTimeline;
	private ConnectionManager manager;
	
	public void setManager(ConnectionManager manager) {
	    this.manager = manager;
	    history.clear();
	    
	    if(refreshTimeline != null) {
	    	refreshTimeline.play();
	    }
	}
	
	@FXML
	public void initialize() {
	    tableNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
	    tableSizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
	    pidColumn.setCellValueFactory(new PropertyValueFactory<>("pid"));
	    userColumn.setCellValueFactory(new PropertyValueFactory<>("user"));
	    databaseColumn.setCellValueFactory(new PropertyValueFactory<>("database"));
	    stateColumn.setCellValueFactory(new PropertyValueFactory<>("state"));
	    clientColumn.setCellValueFactory( new PropertyValueFactory<>("client"));
	    
	    refreshTimeline = new Timeline(
	            new KeyFrame(Duration.seconds(1), e -> {
	                if(manager != null && manager.getActive() != null && manager.getActive().getConnection() != null){
	                    refreshDashboard();
	                }
	            })
	    );
	    refreshTimeline.setCycleCount(Timeline.INDEFINITE);
	}
	
	public void refreshDashboard(){
	    ConnectionConfig config = manager.getActive();

	    if(config == null || config.getConnection() == null) {
	        clearDashboard();
	        return;
	    }

	    try{
	        ServerInfo info = loader.load(config);
	        DashboardStats stats = collector.collect(config.getConnection());
	        DashboardSample sample = chartLoader.load(config.getConnection());
	        
	        history.add(sample);
	        
	        List<TableInfo> tables = tableLoader.load(config.getConnection());
	        List<ActiveConnectionInfo> connections = connectionLoader.load(config.getConnection());

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
	        
	        if(activeConnections != null) activeConnections.getItems().setAll(connections);
	        if(tablesSize != null) tablesSize.getItems().setAll(tables);
	        
	        refreshCharts();
	    }
	    catch(Exception e){
	        e.printStackTrace();
	    }
	}
	
	private String formatVersion(String version){
	    if(version == null) return "-";

	    String[] parts = version.split(" ");

	    for(String part : parts){
	        if(part.matches("\\d+\\.\\d+.*")) return part;
	    }

	    return version;
	}
	
	private String formatUptime(String uptime){
	    if(uptime == null) return "-";

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
	    else time = uptime.trim();
	    
	    String[] t = time.split(":");

	    if(t.length >= 2) return days + "d " + t[0] + "h " + t[1] + "m";

	    return uptime;
	}
	
	private void refreshCharts(){

	    latencyChart.getData().clear();
	    queriesChart.getData().clear();
	    rowsModifiedChart.getData().clear();
	    rowsReturnedChart.getData().clear();

	    XYChart.Series<String, Number> latencySeries = new XYChart.Series<>();
	    XYChart.Series<String, Number> queriesSeries = new XYChart.Series<>();
	    XYChart.Series<String, Number> modifiedSeries = new XYChart.Series<>();
	    XYChart.Series<String, Number> returnedSeries = new XYChart.Series<>();
	    
	    latencySeries.setName("Latency");
	    queriesSeries.setName("Queries");
	    modifiedSeries.setName("Rows modified");
	    returnedSeries.setName("Rows returned");
	    
	    int i = 1;

	    for(DashboardSample sample : history.getHistory()){
	        String point = String.valueOf(i++);

	        latencySeries.getData().add(new XYChart.Data<>(point, sample.getLatency()));
	        queriesSeries.getData().add(new XYChart.Data<>(point, sample.getQueries()));
	        modifiedSeries.getData().add(new XYChart.Data<>(point, sample.getRowsModified()));
	        returnedSeries.getData().add(new XYChart.Data<>(point, sample.getRowsReturned()));
	    }
	    
	    latencyChart.getData().add(latencySeries);
	    queriesChart.getData().add(queriesSeries);
	    rowsModifiedChart.getData().add(modifiedSeries);
	    rowsReturnedChart.getData().add(returnedSeries);
	}
	
	private void clearDashboard() {
	    name.setText("-");
	    database.setText("-");
	    address.setText("-");
	    port.setText("-");
	    version.setText("-");
	    uptime.setText("-");
	    connectionActive.setText("-");
	    connectionIdle.setText("-");
	    latency.setText("-");
	    cache.setText("-");
	    deadlock.setText("-");

	    activeConnections.getItems().clear();
	    tablesSize.getItems().clear();

	    history.clear();

	    latencyChart.getData().clear();
	    queriesChart.getData().clear();
	    rowsModifiedChart.getData().clear();
	    rowsReturnedChart.getData().clear();
	}
}
