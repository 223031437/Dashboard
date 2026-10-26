# SQL Server Monitoring Dashboard

## 1. Project Overview

The SQL Server Monitoring Dashboard is a web-based application designed to monitor the performance, health, and activity of a Microsoft SQL Server database.

The dashboard collects information from SQL Server, processes it through a backend application, and presents the results through an interactive graphical user interface. It helps database administrators (DBAs) identify performance problems, investigate database activity, and make informed decisions about database maintenance and optimisation.

The project connects to a Microsoft SQL Server instance and provides a central location for viewing important monitoring information, including database performance statistics, query activity, indexes, sessions, and storage usage.

### Main Objectives

- Monitor SQL Server performance and database health.
- Identify slow-running queries and potential bottlenecks.
- Detect blocking sessions and database waiting tasks.
- Identify missing indexes that could improve query performance.
- Monitor disk I/O, memory usage, and database activity.
- Present monitoring information using charts, tables, and summary cards.
- Help database administrators investigate problems and recommend solutions.

## 2. Technologies and Programming Languages

The project uses a frontend, backend, and database architecture.

| Technology | Purpose |
|---|---|
| HTML5 | Provides the structure of the web interface. |
| CSS3 | Controls the dashboard layout, colours, styling, and responsiveness. |
| JavaScript | Handles frontend interactions and dynamic content. |
| Java | Implements backend logic and communicates with SQL Server. |
| SQL / T-SQL | Retrieves monitoring information from Microsoft SQL Server. |
| React | Builds reusable interface components and dashboard pages. |
| Vite | Runs the frontend development server and builds the React application. |
| Spring Boot | Provides the backend REST API and monitoring services. |
| Microsoft SQL Server | Supplies database performance and operational information. |
| JDBC / Microsoft JDBC Driver | Enables Java to communicate with SQL Server. |

### Supporting Libraries

- **Axios:** Sends HTTP requests from React to the Spring Boot API.
- **Recharts:** Displays charts and graphs, including pie charts and bar charts.
- **Lucide React:** Provides icons for dashboard navigation, metrics, and actions.
- **ESLint:** Helps identify potential JavaScript and React code problems.

## 3. Development Tools Required

| Tool | Purpose |
|---|---|
| Visual Studio Code | Develops and maintains the React frontend. |
| IntelliJ IDEA | Develops, runs, and debugs the Spring Boot backend. |
| Microsoft SQL Server | Hosts the database server being monitored. |
| SQL Server Management Studio (SSMS) | Executes SQL queries, inspects databases, and investigates server performance. |
| Java Development Kit (JDK) | Compiles and runs the Java backend. |
| Node.js and npm | Install frontend dependencies and run the Vite development server. |
| Maven | Builds the Spring Boot project, according to its configured build system. |
| Git | Tracks source code changes and supports version control. |
| Web browser | Displays and tests the dashboard. |

## 4. System Architecture

The application follows a three-layer architecture.

```text
+----------------------------------+
|        FRONTEND                  |
|       React + Vite               |
|                                  |
|  Dashboard Cards                 |
|  Charts and Graphs               |
|  Monitoring Tables               |
|  Alerts and Status Indicators    |
+----------------+-----------------+
                 |
                 | HTTP / REST API
                 | JSON responses
                 v
+----------------------------------+
|          BACKEND                 |
|        Java + Spring Boot        |
|                                  |
|  REST Controllers                |
|  Monitoring Services             |
|  SQL Query Execution             |
|  Data Processing                 |
+----------------+-----------------+
                 |
                 | JDBC
                 v
+----------------------------------+
|          DATABASE                |
|       Microsoft SQL Server       |
|                                  |
|  System Dynamic Management Views |
|  Database and Server Statistics  |
|  Query and Session Information   |
+----------------------------------+
```

### How the Architecture Works

1. The frontend displays the monitoring dashboard in a web browser.
2. React sends HTTP requests to the Spring Boot backend.
3. The backend executes authorised monitoring queries against SQL Server.
4. SQL Server returns the requested monitoring information.
5. Spring Boot processes the results and returns them as JSON.
6. React receives the JSON data and updates the dashboard.
7. Charts, tables, metric cards, and status indicators present the results to the user.

The dashboard uses monitoring queries, including SQL Server system views and Dynamic Management Views (DMVs), to retrieve relevant performance information.

## 5. What the Dashboard Shows

### 5.1 Server Overview

The overview section provides general information about the monitored SQL Server instance.

It can display:

- Server name.
- SQL Server version.
- SQL Server edition.
- Database currently selected for the connection.
- Server time.
- SQL Server startup time.
- General server and monitoring status.

This section helps the administrator identify which SQL Server instance is being monitored.

### 5.2 Performance Summary Cards

Summary cards provide a quick view of important monitoring metrics.

Examples include:

- Active database sessions.
- Blocking sessions.
- Long-running queries.
- Missing index recommendations.
- Database storage usage.
- SQL Server Agent job status.

The availability of each metric depends on the backend endpoint and the permissions granted to the monitoring account.

### 5.3 Charts and Graphs

Charts help administrators understand database information visually.

| Visualisation | Information displayed |
|---|---|
| Pie chart | Distribution of selected categories, such as session states or job statuses. |
| Bar chart | Comparison of query durations, database sizes, or other performance metrics. |
| Line chart | Changes in performance measurements over time, when historical data is collected. |
| Metric cards | Key values such as active sessions and the number of blocking sessions. |
| Data tables | Detailed records of queries, indexes, sessions, jobs, and other monitoring results. |

Historical trend charts require the application to collect and store measurements over time; a single live SQL Server query does not automatically provide a historical trend.

### 5.4 Database and Memory Monitoring

This section helps administrators investigate resource usage.

It may display:

- Database sizes and available storage information.
- Memory-related performance statistics.
- Buffer and cache-related measurements.
- Resource usage indicators where supported by the monitoring queries.

These measurements help identify potential resource constraints and guide further investigation.

### 5.5 Long-Running and Expensive Queries

The query monitoring section helps identify SQL statements that may require investigation.

It can include:

- Query text or an available query identifier.
- Execution duration.
- Execution counts.
- CPU usage statistics.
- Logical reads and other available performance measurements.

Administrators can use this information to investigate inefficient queries, review execution plans, and determine whether query or index optimisation is appropriate.

### 5.6 Active and Blocking Sessions

The sessions section displays information about database connections and activity.

It may include:

- Active sessions.
- Session identifiers.
- Current requests.
- Blocking sessions and blocked requests.
- Wait types and wait durations.

Blocking occurs when one session prevents another session from accessing a resource it needs. The dashboard helps the administrator identify the affected sessions and investigate the cause.

### 5.7 Missing Indexes

The missing-index section displays recommendations generated by SQL Server's missing-index DMVs.

It can show:

- Database and table names.
- Equality columns.
- Inequality columns.
- Included columns.
- Estimated improvement or average user impact.
- User seeks and scans.

These recommendations are starting points for investigation rather than instructions to create every suggested index. The administrator should consider existing indexes, write overhead, storage costs, execution plans, and workload requirements before making changes.

### 5.8 SQL Server Agent Jobs

This section monitors scheduled SQL Server Agent jobs, where the service and permissions support it.

It may display:

- Job names.
- Job status.
- Last execution outcome.
- Execution history.
- Failed or unsuccessful jobs.

This helps administrators identify scheduled tasks that require attention.

### 5.9 Wait Statistics

Wait statistics help identify what SQL Server sessions are waiting for while processing work.

The dashboard may present:

- Wait types.
- Wait durations.
- Wait counts.
- Related performance indicators.

Wait statistics should be interpreted in context because some waits are normal and do not necessarily indicate a problem.

### 5.10 Disk I/O and Backups

The monitoring design also includes storage and backup-related information.

**Disk I/O monitoring** can help investigate read and write activity, latency, and possible storage bottlenecks.

**Backup monitoring** can help review available backup history, last backup times, and backup outcomes where the required records and permissions are available.

These features help administrators investigate storage performance and verify whether expected backup activity has occurred.

## 6. How to Run the Project

### Prerequisites

Ensure that the following are installed and configured:

- Java Development Kit.
- Node.js and npm.
- IntelliJ IDEA.
- Visual Studio Code.
- Microsoft SQL Server.
- SQL Server Management Studio.
- The project's configured Gradle or Maven build tool.

### Step 1: Start SQL Server

Start the SQL Server service and confirm that the target SQL Server instance is accessible.

Check that the monitoring database connection details and authentication settings are correct.

### Step 2: Configure the Backend

Open the backend project in IntelliJ IDEA.

Check the `application.properties` file, or the corresponding YAML configuration file, for the database connection settings.

Example configuration:

```properties
spring.application.name=SQL_Dashboard

spring.datasource.url=jdbc:sqlserver://DESKTOP-DOVLTGN;databaseName=master;encrypt=true;trustServerCertificate=true
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver

server.port=8080
```

The JDBC URL above is an example based on the project's SQL Server connection. Authentication must also be configured according to the selected authentication method.

### Step 3: Start the Spring Boot Backend

Run the main Spring Boot application class in IntelliJ IDEA.

The backend is configured to use port `8080`.

When startup succeeds, the REST API can receive requests from the frontend.

### Step 4: Start the React Frontend

Open the frontend project folder in Visual Studio Code.

Open the terminal and execute:

```bash
npm install
npm run dev
```

Vite will start the frontend development server, normally at:

```text
http://localhost:5173/
```

Open this address in a web browser to view the dashboard.

### Step 5: Test the Connection

Confirm that:

1. The frontend opens successfully.
2. The Spring Boot backend is running.
3. The backend can connect to SQL Server.
4. The API returns monitoring data.
5. The frontend displays the returned data.
6. Charts and tables show appropriate results.
7. Errors are handled when a request fails or data is unavailable.

## 7. Security Considerations

The dashboard should be designed primarily for monitoring, not for making unapproved changes to the database.

Recommended security measures include:

- Use a dedicated SQL Server monitoring account.
- Apply the principle of least privilege.
- Grant only the permissions required by the monitoring queries.
- Protect database credentials and avoid committing secrets to Git.
- Use authenticated access to the dashboard when required.
- Validate API inputs and handle errors safely.
- Avoid exposing sensitive query text or database information to unauthorised users.
- Use secure connection settings appropriate to the deployment environment.

The monitoring account may need specific server-state or database-state permissions to access certain DMVs. Permissions should be verified against the SQL Server version and the queries used.

## 8. Limitations and Future Improvements

Possible future improvements include:

- Automatic periodic refreshing of monitoring data.
- Historical performance tracking and trend analysis.
- Configurable alerts for unusual performance conditions.
- Filters for databases, sessions, and query activity.
- Exporting monitoring results to CSV or PDF.
- Improved mobile and responsive layouts.
- More detailed query execution-plan analysis.
- Monitoring multiple SQL Server instances.
- Recording and tracking recommendations made by administrators.
- Role-based access control for different dashboard users.

## 9. Expected Benefits

The monitoring dashboard provides a central interface for observing SQL Server activity and identifying areas that require investigation.

It can help database administrators:

- Detect possible performance bottlenecks.
- Investigate slow queries and blocking sessions.
- Review missing-index recommendations.
- Monitor database and storage resources.
- Check SQL Server Agent job outcomes.
- Review available backup information.
- Make evidence-based recommendations for performance improvement.

The dashboard supports DBA decision-making. It does not automatically guarantee that a database issue will be resolved; the administrator must validate the evidence and implement appropriate corrective actions.

## 10. Conclusion

The SQL Server Monitoring Dashboard combines React, Java, Spring Boot, and Microsoft SQL Server to provide a graphical interface for database monitoring.

React and Vite provide the frontend, Spring Boot handles API requests and monitoring logic, and SQL Server supplies the performance and operational information. Supporting tools such as Visual Studio Code, IntelliJ IDEA, SQL Server Management Studio, and Git support development, testing, and maintenance.

The overall goal is to make SQL Server monitoring more accessible by presenting technical database information through organised metric cards, charts, graphs, and tables.

This project also provides practical experience in database administration, SQL performance analysis, backend development, API integration, and frontend visualisation.
