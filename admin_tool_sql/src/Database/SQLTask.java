package Database;

import java.util.function.Consumer;

public class SQLTask {

	private String sql;
	private Consumer<QueryResult> callback;
	
	public SQLTask(String sql, Consumer<QueryResult> callback) {
		this.sql=sql;
		this.callback=callback;
	}
	
	public String getsql() {
		return sql;
	}
	
	public void complete(QueryResult result) {
		callback.accept(result);
	}
}
