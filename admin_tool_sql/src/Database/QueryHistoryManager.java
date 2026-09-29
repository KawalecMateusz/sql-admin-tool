/*
 * Handles loading and saving the query history.
 */

package Database;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class QueryHistoryManager {

	private static final String FILE = "query_history.dat";
	
	public static void save(List<QueryHistory> history) {
		try(ObjectOutputStream out = 
				new ObjectOutputStream(
						new FileOutputStream(FILE))){
			out.writeObject(history);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public static List<QueryHistory> load(){
		
		try(ObjectInputStream in = 
				new ObjectInputStream(
						new FileInputStream(FILE))){
			return (List<QueryHistory>) in.readObject();
		}
		catch(Exception e) {
			return new ArrayList<>();
		}
	}
}
