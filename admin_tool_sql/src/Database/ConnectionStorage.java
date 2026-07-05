package Database;

import java.io.*;
import java.util.List;

public class ConnectionStorage {

    private static final String FILE = "connections.dat";

    public static void save(List<ConnectionConfig> configs) {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(FILE))) {

            oos.writeObject(configs);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static List<ConnectionConfig> load() {
        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(FILE))) {

            return (List<ConnectionConfig>) ois.readObject();

        } catch (Exception e) {
            return new java.util.ArrayList<>();
        }
    }
}