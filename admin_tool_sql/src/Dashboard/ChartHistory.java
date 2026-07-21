package Dashboard;

import java.util.LinkedList;
import java.util.List;

public class ChartHistory {

	private static final int MAX_POINTS=30;
	private final LinkedList<DashboardSample> history = new LinkedList<>();
	
	public void add(DashboardSample sample) {
		
		history.add(sample);
		
		if(history.size() > MAX_POINTS) {
			history.removeFirst();
		}
	}
	
	public List<DashboardSample> getHistory(){
		return history;
	}
	
	public void clear(){
	    history.clear();
	}
}
