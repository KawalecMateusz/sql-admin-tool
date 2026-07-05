package Database;

import java.util.ArrayList;
import java.util.List;

public class ConnectionManager {

    private final List<ConnectionConfig> sessions = new ArrayList<>();
    private ConnectionConfig active;

    public void addSession(ConnectionConfig config) {
        sessions.add(config);
        active = config;
    }

    public void removeActive() {
        if (active != null) {
            try {
                if (active.getConnection() != null)
                    active.getConnection().close();
            } catch (Exception ignored) {}

            sessions.remove(active);
            active = sessions.isEmpty() ? null : sessions.get(0);
        }
    }

    public void removeSession(ConnectionConfig config) {
        try {
            if (config.getConnection() != null)
                config.getConnection().close();
        } catch (Exception ignored) {}

        sessions.remove(config);

        if (active == config) {
            active = sessions.isEmpty() ? null : sessions.get(0);
        }
    }

    public List<ConnectionConfig> getSessions() {
        return sessions;
    }

    public ConnectionConfig getActive() {
        return active;
    }

    public void setActive(ConnectionConfig config) {
        this.active = config;
    }
    
    public void setSessions(List<ConnectionConfig> loaded) {
        sessions.clear();
        sessions.addAll(loaded);

        active = sessions.isEmpty() ? null : sessions.get(0);
    }
}