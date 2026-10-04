1. Introduction
2. Project Scope and Functionality
3. Application Architecture
4. Connection Management
5. SQL Execution
6. Dashboard and Monitoring
7. Query History
8. Multithreading
9. JDBC Resource Management
10. Problems and Solutions
11. Testing
12. Environment and Installation
13. Summary
14. AI Usage



1. Introduction

SQL Admin Tool is a simple desktop application for working with PostgreSQL databases. The application is based on two main functionalities: executing SQL queries and presenting database information through a monitoring dashboard.

The SQL Editor allows users to execute queries and view their results in a table or as plain text. The Dashboard presents basic database information using numerical values, tables, and charts.

The main purpose of creating this application was to experiment with building a simple tool for basic database analysis, while providing an easy way to access and monitor different databases from a single interface.

The application is primarily written in Java and uses JavaFX for the graphical user interface, along with JDBC for database communication.

2. Project Scope and Functionality

2.1 Connection Handling

The application includes a system for adding, removing, and editing database connections. Users can save their database connection data locally and reuse it until the connection is manually removed. The connection list is serialized and stored in a .dat file.

When a database connection is established, the application creates a separate session handled by its own worker thread. This allows different database sessions to operate independently without blocking each other.

2.2 SQL Editor

The first main part of the application is a classic SQL editor. The user can write and execute SQL queries and receive the result either as a table or as plain text containing information about modified rows.

SQL queries are executed asynchronously by a dedicated worker thread, preventing longer-running queries from blocking the main application interface. Queries are placed in a queue and processed by the worker assigned to the current database session.

For easier navigation through the database structure, the SQL Editor also includes a tree view showing the database hierarchy and its tables.

2.3 Dashboard

The second main part of the application is the Dashboard, which presents selected database and connection statistics.

It includes information such as:

* Server uptime
* Table sizes
* Cache hit ratio
* Query latency
* Active and idle database connections
* Additional database statistics

Information is presented in different forms depending on the type of data. Simple values are displayed as numbers, lists of connections and table sizes are displayed in tables, and statistics that change over time are presented using charts.

The Dashboard is refreshed every second for the currently connected database session, providing an up-to-date overview of its state.

2.4 Query History

The application includes a query history stored locally in a .dat file. It allows the user to review previously executed queries across different database sessions.

The history can be filtered by:

* Database name
* Execution date
* Query type

2.5 Add / Edit Connection

The Add/Edit Connection dialog provides a simple interface for creating and modifying saved database connections.

The user can enter the required connection information, save it for later use, and connect to the database again without having to enter the same information each time.


3. Application Architecture

The application is divided into several cooperating components responsible for the user interface, database connections and SQL execution, dashboard monitoring, and query history.

At a high level, the application can be represented as:

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
                           ▼                 ▼
                    ConnectionManager    PostgreSQL
                           │
                  ┌────────┼────────┐
                  ▼        ▼        ▼
               Worker A Worker B Worker C
                  │        │        │
                  ▼        ▼        ▼
               Database Database Database

3.1 UI Layer

The user interface is implemented using JavaFX, FXML, and CSS.

FXML files define the structure of individual views, while controllers handle user interaction and coordinate operations between the interface and the application logic.

The main UI components include:

* Main window
* SQL Editor
* Dashboard
* Query History
* Add/Edit Connection dialog

Controllers do not directly implement all database operations. Instead, they delegate specific tasks to dedicated classes such as connection managers, workers, database loaders, and history managers.

The JavaFX Application Thread is responsible for updating the interface. Results produced by background database operations are returned to the UI using JavaFX mechanisms such as Platform.runLater().

3.2 Database Layer

The Database layer contains the main logic responsible for PostgreSQL connections and SQL execution.

The central components include:

* ConnectionManager — manages database sessions and the currently active connection.
* ConnectionConfig — stores configuration and current state of a database connection.
* DBConnection — handles establishing and closing database connections.
* ConnectionWorker — processes SQL tasks for a specific database session.
* SQLTask — represents a query submitted to a worker.
* QueryResult — stores the result of an executed query.
* ConnectionStorage — saves and loads serialized connection configurations.
* SchemaLoader — loads the database structure used by the SQL Editor.

Connection flow

When the user connects to a database, the application creates a JDBC connection and associates it with a ConnectionConfig. A dedicated ConnectionWorker is then created for the session.

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

The ConnectionManager keeps track of active sessions and determines which connection is currently selected by the user.

3.3 SQL Execution

SQL execution uses a producer-consumer approach based on BlockingQueue.

The SQL controller creates an SQLTask containing the query and a callback for processing the result. The task is placed in the queue belonging to the active ConnectionWorker.

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

The worker waits for new tasks using BlockingQueue.take(). This means that it does not continuously poll the queue while no query is waiting.

For queries returning a result set, the QueryResult contains the ResultSet and its associated Statement. The UI layer later passes this data to TableBuilder, which creates the table columns and rows.

For queries that modify data, the result contains information about the number of affected rows instead of a result set.

3.4 Multiple Database Sessions

Each active database connection has its own ConnectionWorker.

ConnectionManager
       │
       ├── Database A
       │      └── ConnectionWorker A
       │
       ├── Database B
       │      └── ConnectionWorker B
       │
       └── Database C
              └── ConnectionWorker C

This allows the application to maintain several independent database sessions. A query submitted to one worker does not use the worker assigned to another database.

When a session is disconnected, its worker is stopped using the worker’s shutdown mechanism and the associated JDBC connection is closed.

3.5 Dashboard Layer

The Dashboard consists of a controller and several classes responsible for collecting specific types of database information.

The main components include:

* ServerInfoLoader — retrieves server information.
* StatsCollector — collects general database statistics.
* ActiveConnectionLoader — retrieves active and idle connections.
* TableInfoLoader — retrieves table information and sizes.
* ChartDataLoader — retrieves data used by monitoring charts.
* ChartHistory — stores historical chart samples.
* DashboardSample — represents a single set of collected statistics.

The Dashboard controller uses a JavaFX Timeline to trigger a refresh every second.

JavaFX Timeline
      │
      │ every 1 second
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

The collected data is then passed to JavaFX controls such as labels, tables, and charts.

Unlike SQL Editor queries, Dashboard queries are executed directly during the refresh process. The implementation assumes that these queries are lightweight enough to complete quickly and therefore keeps the refresh mechanism simple.

3.6 Query History

Query history is handled by QueryHistory and QueryHistoryManager.

After a query is submitted, information about the query and its database session is stored in a QueryHistory object.

Executed Query
      │
      ▼
QueryHistory
      │
      ▼
QueryHistoryManager
      │
      ▼
Serialized .dat file

The History controller loads the stored entries and applies filters based on database name, execution date, and query type.

3.7 Connection Storage

Saved connection configurations are handled separately from active JDBC sessions.

The application serializes connection configurations into a local .dat file. The serialized object contains the information required to recreate a database connection, while runtime objects such as the active JDBC Connection and worker are not stored as persistent session data.

This separates the saved connection configuration from the temporary state of an active database session.

3.8 Application Shutdown and Resource Handling

Database resources are explicitly closed when a session is disconnected.

The shutdown process is responsible for stopping the worker associated with the session, closing the JDBC connection, and updating the state of the application.

The worker can be blocked while waiting for a task in BlockingQueue.take(). Calling interrupt() wakes the worker and allows it to leave the queue-waiting state and terminate its execution loop.

JDBC resources such as ResultSet and Statement are closed after the result has been processed by the UI.

3.9 Main Architectural Decisions

The architecture uses separate worker threads per database session rather than one central database worker. This keeps database sessions independent and prevents a long-running query in one session from occupying the worker responsible for another session.

The Dashboard uses a simpler periodic refresh mechanism because its operations consist primarily of lightweight read-only queries executed once per second.

The application therefore combines synchronous JavaFX UI handling with asynchronous SQL execution where longer-running operations are expected.

4. Connection Management

Database connections are managed by the ConnectionManager, which keeps track of available database sessions and the currently active session.

4.1 Connection Configuration

The ConnectionConfig class stores the configuration of a database connection, including information required to establish a JDBC connection.

Saved connection configurations can be added, edited, and removed by the user. The configurations are serialized and stored locally in a .dat file, allowing them to be reused after restarting the application.

Runtime objects such as the active JDBC Connection and the associated ConnectionWorker are not stored as part of the persistent configuration.

4.2 Establishing a Connection

When the user connects to a saved database, DBConnection creates the JDBC connection using the information stored in ConnectionConfig.

After a successful connection, a ConnectionWorker is created for the database session. The worker is associated with the corresponding ConnectionConfig and is responsible for processing SQL tasks submitted to that session.

The ConnectionManager keeps track of the active sessions and determines which database is currently selected by the user.

4.3 Multiple Database Sessions

The application supports multiple database connections at the same time. Each active database session has its own JDBC connection and its own ConnectionWorker.

This allows sessions to operate independently. Selecting another database changes the active session used by the interface without replacing the other active connections.

4.4 Disconnecting

When the user disconnects from a database, the associated worker is stopped and the JDBC connection is closed.

The application then updates the interface to reflect the new connection state. Views depending on an active database connection, such as the SQL Editor and Dashboard, are refreshed accordingly.


5. SQL Execution

SQL queries are executed through the SQL Editor. The execution process uses a task queue and a dedicated worker associated with the currently active database session.

5.1 Creating an SQL Task

When the user executes a query, SQLController retrieves the SQL text from the editor and creates an SQLTask.

The task contains the SQL query and a callback used to process the result after execution.

The task is then submitted to the ConnectionWorker associated with the active database session.

5.2 Query Queue

Each ConnectionWorker contains a BlockingQueue<SQLTask>.

The worker waits for a new task using BlockingQueue.take(). When a task becomes available, the worker removes it from the queue and executes the SQL query.

This separates submitting a query from its actual execution and prevents the SQL operation from being performed directly by the UI event handler.

5.3 Executing SQL

The worker creates a JDBC Statement and executes the SQL query.

The result depends on the type of SQL operation:

* Queries returning data produce a ResultSet.
* Queries modifying database data produce information about the number of affected rows.

The result is stored in a QueryResult object.

For queries returning a ResultSet, the QueryResult also keeps the associated Statement, which is required while the result is being processed.

5.4 Displaying Results

After the SQL operation is completed, the result is passed back to the UI.

For queries returning data, TableBuilder creates the columns and rows of the JavaFX TableView.

For queries that modify data, the interface displays a message containing the number of affected rows.

The JavaFX interface is updated using Platform.runLater(), ensuring that the UI update is performed on the JavaFX Application Thread.

5.5 Query History

After a query is submitted, information about the query and the active database session is added to the query history.

The history entry contains information such as the database name, database session, SQL text, and detected query type.


6. Database Structure

The SQL Editor contains a tree view representing the structure of the currently connected database.

The database structure is loaded by the SchemaLoader class.

6.1 Loading the Structure

When the database structure needs to be displayed, SchemaLoader queries PostgreSQL for information about the available database objects.

The returned information is converted into a hierarchy of elements represented by the application’s tree model.

The structure allows the user to navigate through the database and view its tables directly from the SQL Editor.

6.2 Database Tree

The database structure is displayed using a JavaFX TreeView.

The tree represents the hierarchy of the database and its tables, making it easier to navigate the available objects while writing SQL queries.

6.3 Refreshing the Structure

The database tree can be refreshed when the SQL Editor is opened or when the active database connection changes.

This ensures that the displayed structure corresponds to the currently selected database session rather than remaining associated with a previously connected database.


7. Dashboard and Monitoring

The Dashboard provides a separate view for displaying selected PostgreSQL database and connection statistics.

The collected information is divided into numerical values, tables, and charts depending on the type of data.

7.1 Dashboard Components

The Dashboard uses several dedicated classes for collecting different types of information:

* ServerInfoLoader — retrieves information about the PostgreSQL server.
* StatsCollector — collects general database statistics.
* ActiveConnectionLoader — retrieves information about active and idle connections.
* TableInfoLoader — retrieves information about database tables and their sizes.
* ChartDataLoader — collects data used by the monitoring charts.

The DashboardController coordinates these components and updates the JavaFX controls with the collected data.

7.2 Displayed Information

The Dashboard displays information such as:

* Server uptime
* Active connections
* Idle connections
* Query latency
* Cache hit ratio
* Table sizes
* Other selected PostgreSQL statistics

Simple values are displayed using labels, while lists of connections and tables are presented using JavaFX tables.

Statistics that change over time are represented using charts.

7.3 Periodic Refresh

The Dashboard uses a JavaFX Timeline to trigger a refresh approximately every second.

During a refresh, the Dashboard collects the current database statistics and updates the displayed values, tables, and charts.

The refresh is performed for the currently active database session.

7.4 Chart History

The Dashboard stores previously collected chart values using ChartHistory.

A single collection of statistics is represented by DashboardSample. These samples allow the application to display changes in selected database statistics over time.

7.5 Handling Disconnection

When there is no active database connection, the Dashboard does not continue displaying statistics from the previously connected database.

Instead, the displayed values, tables, history, and charts are cleared and replaced with their default state.

This prevents information from a previously connected database from remaining visible after switching to an inactive session.



8. Query History

The application stores information about executed SQL queries in a local query history.

The history is handled mainly by the QueryHistory and QueryHistoryManager classes.

8.1 History Entries

Each executed query is represented by a QueryHistory object.

The stored information includes:

* Database session name
* Database name
* SQL query
* Query type
* Execution date

This allows queries from different database sessions to be stored in one history.

8.2 Persistent Storage

Query history is serialized and stored locally in a .dat file.

QueryHistoryManager is responsible for saving and loading the history, allowing previously executed queries to remain available after restarting the application.

8.3 Filtering

The History view provides filtering options that allow the user to narrow the displayed entries.

Queries can be filtered by:

* Database name
* Execution date
* Query type

The filters make it easier to find a specific query when the history contains entries from multiple database sessions.


9. Multithreading

Multithreading is used primarily for SQL execution. The application assigns a dedicated worker thread to each active database session.

9.1 Connection Workers

Each ConnectionConfig can have an associated ConnectionWorker.

The worker is responsible for processing SQL tasks submitted to its database session.

For example, when three database sessions are active, the application can have three independent workers:

Database A → ConnectionWorker A
Database B → ConnectionWorker B
Database C → ConnectionWorker C

A query submitted to one session is therefore processed by the worker assigned to that session.

9.2 BlockingQueue

The worker uses a BlockingQueue<SQLTask> to receive SQL tasks.

The main loop waits for a task using:

SQLTask task = sqlQueue.take();

take() blocks the worker while the queue is empty. When a new task is added, the worker receives it and continues with SQL execution.

This avoids continuously polling the queue while there are no queries to execute.

9.3 Returning Results to the UI

SQL execution takes place outside the JavaFX Application Thread.

After the worker finishes the query, the result is passed back to the SQL controller. Platform.runLater() is then used to perform the required JavaFX UI updates.

This allows the database operation and the interface update to remain separate.

9.4 Stopping a Worker

When a database session is disconnected, its worker is stopped.

The worker’s running flag is changed to false and interrupt() is called. If the worker is currently waiting inside BlockingQueue.take(), the interruption wakes it and allows the thread to leave its waiting state and terminate.

The associated JDBC connection is then closed as part of the disconnect process.

9.5 Dashboard Refresh

The Dashboard uses a different mechanism.

Its periodic refresh is controlled by a JavaFX Timeline and occurs approximately once per second. Dashboard data collection is performed directly during the refresh rather than being submitted to the SQL worker queue.

This keeps the Dashboard implementation separate from the asynchronous SQL execution mechanism.


10. JDBC and Resource Management

JDBC is used as the communication layer between the application and PostgreSQL.

The application creates JDBC connections for active database sessions and uses JDBC objects to execute SQL queries and retrieve their results.

10.1 JDBC Connection

When a database session is connected, DBConnection creates a JDBC Connection using the configuration stored in ConnectionConfig.

The connection remains associated with the session until the user disconnects from the database.

10.2 Statement and ResultSet

SQL queries are executed using JDBC Statement objects.

Depending on the result of Statement.execute(), the application either retrieves a ResultSet or obtains the number of affected rows.

For queries returning data, the Statement and ResultSet are passed through QueryResult so that the result can be processed by the UI.

TableBuilder reads the ResultSet, creates the required table columns and rows, and then closes the ResultSet and associated Statement.

For queries that do not return a result set, the Statement is closed after the affected-row information has been processed.

10.3 Closing Database Connections

When a database session is disconnected, the associated worker is stopped and the JDBC Connection is closed.

This releases the resources associated with the active database session.

10.4 Persistent and Runtime State

The application separates persistent connection configuration from temporary runtime objects.

ConnectionConfig can be serialized and stored locally, while runtime objects such as the active JDBC Connection and ConnectionWorker are marked as transient.

As a result, restarting the application restores the saved connection configuration without attempting to serialize the active database connection or worker thread.