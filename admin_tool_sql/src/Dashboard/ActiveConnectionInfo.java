package Dashboard;

public class ActiveConnectionInfo {

    private int pid;
    private String user;
    private String database;
    private String state;
    private String application;
    private String client;


    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }


    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }


    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }


    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }


    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }


    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }
}