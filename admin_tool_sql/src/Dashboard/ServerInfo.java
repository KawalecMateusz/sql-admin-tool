package Dashboard;

public class ServerInfo {

	private String name;
	private String host;
	private int port;
	private String database;
	
	private String version;
	private String uptime;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name=name;
	}
	
	public String getHost() {
		return host;
	}
	
	public void setHost(String host) {
		this.host=host;
	}
	
	public int getPort() {
		return port;
	}
	
	public void setPort(int port) {
		this.port=port;
	}
	
	public String getDatabase() {
		return database;
	}
	
	public void setDatabase(String database) {
		this.database=database;
	}
	
	public String getVersion() {
        return version;

    }

    public void setVersion(String version) {
        this.version = version;

    }

    public String getUptime() {
        return uptime;

    }

    public void setUptime(String uptime) {
        this.uptime = uptime;

    }
}
