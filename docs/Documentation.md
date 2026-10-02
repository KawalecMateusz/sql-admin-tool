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