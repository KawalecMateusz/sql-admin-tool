# 1. Introduction

SQL Admin Tool is a desktop application for working with and monitoring PostgreSQL databases. The application focuses on two main areas: executing SQL queries and presenting selected database information through a monitoring dashboard.

The SQL Editor allows users to execute SQL queries and display their results as tables or messages containing information about modified rows. The Dashboard presents selected database and connection statistics using numerical values, tables, and charts.

The application was created as a portfolio project to explore database management, JDBC communication, JavaFX application development, and basic multithreading.

The application is primarily written in Java and uses JavaFX for the graphical user interface and JDBC for communication with PostgreSQL.

# 2. Project Scope and Functionality

## 2.1 Connection Handling

The application supports adding, editing, and removing saved database connections. Connection configurations are serialized and stored locally in a `.dat` file, allowing them to be reused after restarting the application.

Each active database connection is treated as an independent session with its own JDBC connection and worker thread.

## 2.2 SQL Editor

The SQL Editor allows the user to write and execute SQL queries against the currently selected database.

Depending on the query result, the application displays either:

* a table containing returned data;
* a message containing the number of affected rows.

SQL execution is handled asynchronously by a dedicated ConnectionWorker associated with the active database session.

The SQL Editor also contains a tree view representing the structure of the connected database, making it easier to navigate available tables while writing queries.

## 2.3 Dashboard

The Dashboard provides selected PostgreSQL database and connection statistics, including:

* Server uptime
* Active and idle connections
* Query latency
* Cache hit ratio
* Table sizes
* Additional database statistics

Different types of information are displayed using appropriate JavaFX controls. Simple values are displayed as numbers, lists are displayed in tables, and changing statistics are represented using charts.

The Dashboard is refreshed approximately every second for the currently active database session.

## 2.4 Query History

Executed queries are stored in a local `.dat` file.

The history contains queries from different database sessions and can be filtered by:

* Database name
* Execution date
* Query type

## 2.5 Add / Edit Connection

The Add/Edit Connection dialog provides an interface for creating and modifying saved database connection configurations.

The stored configuration can later be used to reconnect to the database without entering the same connection information again.

# 3. Application Architecture

The application is divided into several cooperating components responsible for the user interface, database connections and SQL execution, dashboard monitoring, and query history.

At a high level, the architecture can be represented as:

                         JavaFX Application
                                │
              ┌─────────────────┼─────────────────┐
              │                 │                 │
              ▼                 ▼                 ▼
          UI / FXML        Database Layer    Dashboard Layer
              │                 │                 │
        Controllers             │                 │
              │                 │                 │
              └────────────┬────┴────────────┬────┘
                           ▼                 │
                    ConnectionManager        │
                           │                 │
                  ┌────────┼────────┐        │
                  ▼        ▼        ▼        │
               Session A Session B Session C │
                  │        │        │        │
                  ▼        ▼        ▼        │
               Worker A Worker B Worker C    │
                  │        │        │        │
                  │        │        │        │
             JDBC Conn. JDBC Conn. JDBC Conn.│
                  │        │        │        │
                  └────────┼─────────────────┘
                           │        
                           ▼        
                        PostgreSQL

## 3.1 UI Layer

The user interface is implemented using JavaFX, FXML, and CSS.

FXML files define the structure of individual views, while controllers handle user interaction and coordinate operations between the interface and application logic.

The main UI components include:

* Main window
* SQL Editor
* Dashboard
* Query History
* Add/Edit Connection dialog

Controllers delegate database-related operations to dedicated classes such as connection managers, workers, database loaders, and history managers.

JavaFX UI updates are performed on the JavaFX Application Thread. Results from asynchronous SQL execution are passed back to the UI using Platform.runLater().

## 3.2 Database Layer

The Database layer contains the main logic responsible for PostgreSQL connections and SQL execution.

The main components include:

* ConnectionManager — manages database sessions and the currently active connection.
* ConnectionConfig — stores configuration and runtime state of a database connection.
* DBConnection — handles establishing and closing JDBC connections.
* ConnectionWorker — processes SQL tasks for a specific database session.
* SQLTask — represents a query submitted to a worker.
* QueryResult — stores the result of an executed query.
* ConnectionStorage — saves and loads serialized connection configurations.
* SchemaLoader — loads the database structure used by the SQL Editor.

### Connection Flow

When a database is connected, a JDBC connection is created using the configuration stored in ConnectionConfig. A dedicated ConnectionWorker is then created for the session.

```
User
 │
 ▼
Connection Dialog
 │
 ▼
ConnectionConfig
 │
 ▼
DBConnection
 │
 ▼
JDBC Connection
 │
 ▼
ConnectionWorker  
```

ConnectionManager keeps track of the active sessions and determines which database is currently selected by the user.

## 3.3 SQL Execution

SQL execution uses a producer-consumer approach based on BlockingQueue.

The SQL controller creates an SQLTask containing the query and a callback for processing the result. The task is placed in the queue belonging to the active ConnectionWorker.



```
SQLController
      │
      ▼
   SQLTask
      │
      ▼
 BlockingQueue
      │
      ▼
ConnectionWorker
      │
      ▼
 JDBC Statement
      │
      ▼
 PostgreSQL
      │
      ▼
 QueryResult
      │
      ▼
Platform.runLater()
      │
      ▼
JavaFX TableView
```

The worker waits for new tasks using BlockingQueue.take(), avoiding continuous polling while the queue is empty.

For queries returning a result set, QueryResult contains the ResultSet and its associated Statement. The UI passes these objects to TableBuilder, which creates the table columns and rows.

For queries modifying database data, the result contains information about the number of affected rows instead of a result set.

## 3.4 Multiple Database Sessions

Each active database connection has its own ConnectionWorker.

```
ConnectionManager
       │
       ├── Session A
       │     ├── JDBC Connection
       │     └── ConnectionWorker
       │
       ├── Session B
       │     ├── JDBC Connection
       │     └── ConnectionWorker
       │
       └── Session C
             ├── JDBC Connection
             └── ConnectionWorker
```
A query submitted to one session is therefore processed by the worker assigned to that session and does not use the worker of another database.

When a session is disconnected, its worker is stopped and the associated JDBC connection is closed.

## 3.5 Dashboard Layer

The Dashboard consists of a controller and several classes responsible for collecting specific types of database information.

The main components include:

* ServerInfoLoader — retrieves server information.
* StatsCollector — collects general database statistics.
* ActiveConnectionLoader — retrieves active and idle connections.
* TableInfoLoader — retrieves table information and sizes.
* ChartDataLoader — retrieves data used by monitoring charts.
* ChartHistory — stores historical chart samples.
* DashboardSample — represents a single set of collected statistics.

The Dashboard controller uses a JavaFX Timeline to trigger a refresh approximately every second.

```
JavaFX Timeline
      │
      │ every ~1 second
      ▼
refreshDashboard()
      │
      ├── ServerInfoLoader
      ├── StatsCollector
      ├── ActiveConnectionLoader
      ├── TableInfoLoader
      └── ChartDataLoader
                 │
                 ▼
             PostgreSQL
```
The collected data is then passed to JavaFX labels, tables, and charts.

Unlike SQL Editor queries, Dashboard queries are executed directly during the refresh process.

## 3.6 Query History

Query history is handled by QueryHistory and QueryHistoryManager.

After a query is submitted, information about the query and its database session is stored in a QueryHistory object.

```
Executed Query
      │
      ▼
QueryHistory
      │
      ▼
QueryHistoryManager
      │
      ▼
Serialized `.dat` file
```

The History controller loads the stored entries and applies filters based on database name, execution date, and query type.

## 3.7 Connection Storage

Saved connection configurations are handled separately from active JDBC sessions.

The application serializes connection configurations into a local `.dat` file. Runtime objects such as the active JDBC Connection and ConnectionWorker are not serialized.

This separates persistent connection configuration from temporary runtime state.

## 3.8 Application Shutdown and Resource Handling

When a database session is disconnected, the associated worker is stopped and the JDBC connection is closed.

A worker may be blocked while waiting for a task in BlockingQueue.take(). Calling interrupt() wakes the worker and allows it to leave the waiting state and terminate its execution loop.

JDBC resources such as ResultSet and Statement are closed after the result has been processed.

# 4. Connection Management

Database connections are managed by ConnectionManager, which keeps track of available database sessions and the currently active session.

## 4.1 Connection Configuration

ConnectionConfig stores the information required to establish a JDBC connection.

Saved configurations can be added, edited, and removed by the user. They are serialized and stored locally in a `.dat` file.

Runtime objects such as the active JDBC Connection and associated ConnectionWorker are not stored as part of the persistent configuration.

## 4.2 Establishing a Connection

When the user connects to a saved database, DBConnection creates the JDBC connection using the information stored in ConnectionConfig.

After a successful connection, a ConnectionWorker is created for the database session.

ConnectionManager keeps track of active sessions and determines which database is currently selected by the interface.

## 4.3 Multiple Database Sessions

The application supports multiple database connections at the same time.

Each active session has its own:

* JDBC connection
* ConnectionConfig
* ConnectionWorker

Selecting another database changes the active session used by the interface without closing the other active connections.

## 4.4 Disconnecting

When the user disconnects from a database, the associated worker is stopped and the JDBC connection is closed.

The application then updates the interface and refreshes views that depend on the active connection, including the SQL Editor and Dashboard.

# 5. SQL Execution

SQL queries are executed through the SQL Editor using a task queue and a dedicated worker associated with the currently active database session.

## 5.1 Creating an SQL Task

SQLController retrieves the SQL text from the editor and creates an SQLTask.

The task contains the SQL query and a callback used to process the result after execution.

The task is then submitted to the ConnectionWorker associated with the active database session.

## 5.2 Query Queue

Each ConnectionWorker contains a `BlockingQueue<SQLTask>`.

The worker waits for a task using:

`SQLTask task = sqlQueue.take();`

When a task becomes available, the worker removes it from the queue and executes the SQL query.

This separates query submission from database execution and prevents the SQL operation from being performed directly by the UI event handler.

## 5.3 Executing SQL

The worker creates a JDBC Statement and executes the SQL query.

Depending on the result of Statement.execute():

* Queries returning data produce a ResultSet.
* Queries modifying database data produce the number of affected rows.

The result is stored in a QueryResult object.

For queries returning a ResultSet, the QueryResult also keeps the associated Statement, which remains required while the result is processed.

## 5.4 Displaying Results

After execution is completed, the result is passed back to the UI.

For queries returning data, TableBuilder creates the columns and rows of the JavaFX TableView.

For queries modifying data, the interface displays a message containing the number of affected rows.

Platform.runLater() is used to perform the JavaFX UI update on the JavaFX Application Thread.

## 5.5 Query History

After a query is submitted, information about the query and active database session is added to the query history.

A history entry contains information such as:

* Database session name
* Database name
* SQL text
* Query type
* Execution date

# 6. Database Structure

The SQL Editor contains a tree view representing the structure of the currently connected database.

The database structure is loaded by SchemaLoader.

## 6.1 Loading the Structure

SchemaLoader queries PostgreSQL for information about available database objects and converts the returned information into the hierarchy used by the application.

## 6.2 Database Tree

The structure is displayed using a JavaFX TreeView.

The tree represents the database hierarchy and its tables, providing a convenient way to navigate database objects while writing SQL queries.

## 6.3 Refreshing the Structure

The database tree is refreshed when the SQL Editor is opened or when the active database connection changes.

This keeps the displayed structure associated with the currently selected database session.

# 7. Dashboard and Monitoring

The Dashboard provides a separate view for displaying selected PostgreSQL database and connection statistics.

The collected information is divided into numerical values, tables, and charts depending on the type of data.

## 7.1 Dashboard Components

The Dashboard uses dedicated classes for collecting different types of information:

* ServerInfoLoader — retrieves information about the PostgreSQL server.
* StatsCollector — collects general database statistics.
* ActiveConnectionLoader — retrieves active and idle connections.
* TableInfoLoader — retrieves database table information and sizes.
* ChartDataLoader — collects data used by monitoring charts.

DashboardController coordinates these components and updates the JavaFX controls.

## 7.2 Displayed Information

The Dashboard displays information such as:

* Server uptime
* Active connections
* Idle connections
* Query latency
* Cache hit ratio
* Table sizes
* Other selected PostgreSQL statistics

Simple values are displayed using labels, lists of connections and tables are displayed using JavaFX tables, and changing statistics are represented using charts.

## 7.3 Periodic Refresh

The Dashboard uses a JavaFX Timeline to trigger a refresh approximately every second.

During a refresh, the Dashboard collects the current database statistics and updates the displayed values, tables, and charts for the active database session.

## 7.4 Chart History

Previously collected chart values are stored using ChartHistory.

A single collection of statistics is represented by DashboardSample. These samples are used to display changes in selected statistics over time.

## 7.5 Handling Disconnection

When there is no active database connection, the Dashboard clears the previously displayed statistics.

Values, tables, history, and charts are reset to their default state.

This prevents information from a previously connected database from remaining visible after switching to an inactive session.

# 8. Query History

The application stores information about executed SQL queries in a local query history.

The history is handled by QueryHistory and QueryHistoryManager.

## 8.1 History Entries

Each executed query is represented by a QueryHistory object.

The stored information includes:

* Database session name
* Database name
* SQL query
* Query type
* Execution date

This allows queries from different database sessions to be stored in one history.

## 8.2 Persistent Storage

Query history is serialized and stored locally in a `.dat` file.

QueryHistoryManager is responsible for saving and loading the history, allowing previously executed queries to remain available after restarting the application.

## 8.3 Filtering

The History view allows entries to be filtered by:

* Database name
* Execution date
* Query type

The filters make it easier to find specific queries when the history contains entries from multiple database sessions.

# 9. Multithreading

Multithreading is used primarily for SQL execution. The application assigns a dedicated worker thread to each active database session.

## 9.1 Connection Workers

Each ConnectionConfig can have an associated ConnectionWorker.

The worker processes SQL tasks submitted to its database session.

For example:

```
Database A → ConnectionWorker A
Database B → ConnectionWorker B
Database C → ConnectionWorker C
```
A query submitted to one session is processed by the worker assigned to that session.

## 9.2 BlockingQueue

The worker uses a `BlockingQueue<SQLTask>` to receive SQL tasks.

The main loop waits for a task using:


`SQLTask task = sqlQueue.take();`

`take()` blocks the worker while the queue is empty. When a new task is added, the worker receives it and continues with SQL execution.

This avoids continuously polling the queue when there are no queries to execute.

## 9.3 Returning Results to the UI

SQL execution takes place outside the JavaFX Application Thread.

After the worker finishes the query, the result is passed back to the SQL controller. `Platform.runLater()` is then used to perform the required JavaFX UI updates.

## 9.4 Stopping a Worker

When a database session is disconnected, its worker is stopped.

The worker’s running flag is changed to false and `interrupt()` is called. If the worker is waiting inside BlockingQueue.take(), the interruption wakes it and allows the thread to leave its waiting state and terminate.

The associated JDBC connection is then closed as part of the disconnect process.

## 9.5 Dashboard Refresh

The Dashboard uses a different mechanism.

Its periodic refresh is controlled by a JavaFX Timeline and occurs approximately once per second. Dashboard data collection is performed directly during the refresh rather than being submitted to the SQL worker queue.

# 10. JDBC and Resource Management

JDBC is used as the communication layer between the application and PostgreSQL.

The application creates JDBC connections for active database sessions and uses JDBC objects to execute SQL queries and retrieve their results.

## 10.1 JDBC Connection

When a database session is connected, DBConnection creates a JDBC Connection using the configuration stored in ConnectionConfig.

The connection remains associated with the session until the user disconnects from the database.

## 10.2 Statement and ResultSet

SQL queries are executed using JDBC Statement objects.

Depending on the result of Statement.execute(), the application either retrieves a ResultSet or obtains the number of affected rows.

For queries returning data, the Statement and ResultSet are passed through QueryResult so that the result can be processed by the UI.

TableBuilder reads the ResultSet, creates the required table columns and rows, and then closes the ResultSet and associated Statement.

For queries that do not return a result set, the Statement is closed after the affected-row information has been processed.

## 10.3 Closing Database Connections

When a database session is disconnected, the associated worker is stopped and the JDBC Connection is closed.

This releases the resources associated with the active database session.

## 10.4 Persistent and Runtime State

The application separates persistent connection configuration from temporary runtime objects.

ConnectionConfig can be serialized and stored locally, while runtime objects such as the active JDBC Connection and ConnectionWorker are marked as transient.

Restarting the application therefore restores the saved connection configuration without attempting to serialize the active database connection or worker thread.

# 11. Environment and Installation

The application was developed using Eclipse IDE and is primarily written in Java. JavaFX is used for the graphical user interface, while JDBC is used to connect the application to PostgreSQL databases.

## 11.1 Requirements

The application requires:

* Java 21
* JavaFX 21
* PostgreSQL
* PostgreSQL JDBC Driver

Docker is optional and can be used as a convenient environment for running a PostgreSQL database.

## 11.2 Running the Application

To run the application:

1. Open the project in Eclipse.
2. Build the project on the target machine.
3. Start the application.
4. Add a database connection using the connection dialog.
5. Enter the required PostgreSQL connection information.
6. Connect to the database.

The SQL Editor and Dashboard can then be used with the connected database.

No additional application configuration is required beyond a running PostgreSQL database and valid connection credentials.

# 12. Summary

SQL Admin Tool is a portfolio project focused on database management and monitoring using Java, JavaFX, JDBC, and PostgreSQL.

The application supports multiple independent database sessions, asynchronous SQL execution using a dedicated worker thread per session, local persistence of connection configurations and query history, database structure navigation, and a monitoring Dashboard with numerical statistics, tables, and charts.

The multithreading implementation uses a BlockingQueue for SQL tasks and Platform.runLater() for returning results to the JavaFX interface. Database and JDBC resources are explicitly released when sessions are disconnected.

The architecture was designed for the scope of the project rather than for large-scale database environments. The resulting implementation provides a relatively simple structure while demonstrating practical use of JavaFX, JDBC, PostgreSQL, asynchronous task processing, serialization, and basic multithreading.