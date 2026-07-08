package Database;

import java.io.Serializable;
import java.time.LocalDateTime;

public class QueryHistory implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String database;
	private String sql;
	private String type;
	private LocalDateTime date;
	
	public QueryHistory(String name, String database, String sql, String type) {
		this.name=name;
		this.database=database;
		this.sql=sql;
		this.type=type;
		this.date=LocalDateTime.now();
	}
	
	public String getName() { return name; }
	public String getDatabase() { return database; }
	public String getSql() { return sql; }
	public String getType() { return type; }
	public LocalDateTime getDate() { return date; }
}
