# SQL Admin Tool

Basic desktop tool for working with and monitoring PostgreSQL databases.

The project presents my interpretation of a simple database administration panel for executing SQL queries, viewing query results, and displaying basic database statistics such as latency, active connections, and table sizes.

The application allows multiple databases to be saved and managed from a single interface. Each database connection is handled separately, allowing the user to connect to and disconnect from different databases independently.

Overview

The application works as a manager for separate database sessions, allowing multiple databases to be connected and monitored from a single interface.

Connections

* Serialized list of saved database connections
* Adding and removing saved connections
* Editing existing connections
* Connecting to and disconnecting from individual databases

SQL Editor

* Query editor for executing SQL commands on a selected database
* Table for displaying query results or information about modified rows
* Database tree showing tables and their hierarchy

Dashboard

* Basic database information
* Number of active connections
* Query latency
* Cache hit ratio
* Table sizes
* Additional database statistics

Query History

* List of executed queries across all connected databases
* Filters for specific queries by:
    * Execution date
    * Database name
    * Query type

Technologies

* Java 21
* JavaFX 21
* JDBC
* PostgreSQL
* Eclipse IDE

Architecture

The application is divided into three main parts:

* Database — manages database sessions, connections, and SQL execution logic.
* Dashboard — collects and presents database statistics in a dedicated monitoring view.
* UI — built with FXML and CSS, with controllers responsible for handling user interaction and coordinating application views.

Connections and Threads

Each database session is handled independently. When a connection is established, the application creates a dedicated worker thread for that session. SQL queries are placed in a queue and processed asynchronously by the worker.

The Dashboard uses a separate periodic refresh mechanism to collect database statistics every second.

Closing a connection terminates its associated worker thread and releases the database connection.

Running the Application

1. Prepare a PostgreSQL database. Docker can be used for this purpose.
2. Start the application.
3. Add a database connection using the connection dialog.
4. Connect to the database and start using the SQL Editor.

No additional configuration is required beyond a running PostgreSQL database and valid connection credentials.

Development Notes

AI Usage

AI tools were used during development as a programming aid, including code review, debugging, technical explanations, implementation suggestions, and generation of repetitive parts of the code.

Generated code was reviewed, tested, modified, and integrated manually. AI was used as a development assistant rather than as a tool to generate the complete application.

Project purpose

The project was created specifically as a portfolio project to demonstrate practical knowledge of Java, JavaFX, JDBC, PostgreSQL, database management, and basic multithreading.