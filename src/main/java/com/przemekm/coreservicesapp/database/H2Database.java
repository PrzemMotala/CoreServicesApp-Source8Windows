package com.przemekm.coreservicesapp.database;

import com.przemekm.coreservicesapp.datamodel.Order;
import com.przemekm.coreservicesapp.datamodel.Report;
import com.przemekm.coreservicesapp.datamodel.ReportParams;
import com.przemekm.coreservicesapp.datamodel.ReportType;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class to handle all H2 database connections and
 * queries executions.
 *
 * @author Przemysław Motała
 */
public final class H2Database {
    private static H2Database instance = new H2Database();
    private static Connection connection;

    /*
        These parameters define driver, connection,
        username and password for the H2 database.
     */
    private static final String DB_DRIVER = "org.h2.Driver";
    private static final String DB_CONNECTION = "jdbc:h2:mem:test";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";

    /**
     * This method returns a singleton instance
     * of the {@link H2Database} class.
     *
     * @return instance of {@link H2Database} class.
     */
    public static H2Database getInstance() {
        return instance;
    }

    /**
     * This constructor creates a connection with H2 database
     * via {@link #getConnection()} method  call and constructs
     * an empty data table with use of {@link #createTable()} method.
     *
     * @see #getConnection()
     * @see #createTable()
     */
    private H2Database() {
        connection = getConnection();
        createTable();
    }

    /**
     * This method creates a new table {@code ORDERS}
     * with five specified columns and an auto-incrementing
     * primary key column. Each column represents one
     * field from the {@link Order} class.
     *
     * @see Order
     * @see Statement#execute(String)
     */
    private void createTable() {
        Statement statement;

        try {
            statement = connection.createStatement();
            statement.execute("CREATE TABLE ORDERS"
                    + "(id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                    + "clientId VARCHAR(6), "
                    + "requestId BIGINT, "
                    + "name VARCHAR(255), "
                    + "quantity INT, "
                    + "price DECIMAL(12,2))");
            statement.close();
        } catch (SQLException e) {
            System.out.println("Couldn't execute the query!");
            e.printStackTrace();
        }
    }

    /**
     * This method truncates the table {@code ORDERS}
     * and restarts the primary key value with 1.
     *
     * @see Statement#execute(String)
     */
    public void clearTable() {
        Statement statement;

        try {
            statement = connection.createStatement();
            statement.execute("TRUNCATE TABLE ORDERS");
            statement.execute("ALTER TABLE ORDERS ALTER COLUMN id RESTART WITH 1");
            statement.close();
        } catch (SQLException e) {
            System.out.println("Couldn't execute the query!");
            e.printStackTrace();
        }
    }

    /**
     * This method retrieves all data stored in the H2 database
     * and loads it into {@link ArrayList} of {@link Order} items.
     *
     * @return {@link ArrayList} of all {@link Order}
     * items retrieved from the database.
     * @see ArrayList
     * @see Order
     */
    public List<Order> getAllData() {
        List<Order> ordersList = new ArrayList<>();
        Statement statement;

        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM ORDERS");
            Order order;
            while (resultSet.next()) {
                order = new Order(
                        resultSet.getString("clientId"),
                        resultSet.getLong("requestId"),
                        resultSet.getString("name"),
                        resultSet.getInt("quantity"),
                        resultSet.getBigDecimal("price"));
                ordersList.add(order);
            }
            statement.close();
        } catch (SQLException e) {
            System.out.println("Couldn't execute the query!");
            e.printStackTrace();
        }

        return ordersList;
    }

    /**
     * This method retrieves all {@code clientId} data
     * stored in the H2 database and loads it into {@link ArrayList}
     * of {@link String} items.
     *
     * @return {@link ArrayList} of all {@code clientId} data retrieved from the database as {@link String} items.
     */
    public List<String> getClientIdData() {
        List<String> list = new ArrayList<>();
        Statement statement;

        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT clientId FROM ORDERS");
            while (resultSet.next()) {
                String currClientId = resultSet.getString(("clientId"));
                if (!list.contains(currClientId)) {
                    list.add(resultSet.getString("clientId"));
                }
            }
            statement.close();
        } catch (SQLException e) {
            System.out.println("Couldn't execute the query!");
            e.printStackTrace();
        }

        Collections.sort(list);

        return list;
    }

    /**
     * This method retrieves data from the H2 database
     * based on the specified {@link ReportParams}.
     *
     * @param reportParams an object containing parameters needed for creation of the database query.
     * @return {@link Report} with the type of {@link ArrayList} of {@link Order} items, or
     * {@link Report} with the type of {@link String} if the {@link ReportType} parameter is not
     * equal to {@link ReportType#ORDERS_LIST}.
     * @see ReportParams
     * @see ReportType
     */
    public Report<?> getQueryData(final ReportParams reportParams) {
        Report<?> report = new Report<>();
        StringBuilder sqlQueryBuilder = new StringBuilder(reportParams.getReportType().getSqlQuery());
        Statement statement;

        try {
            statement = connection.createStatement();
            if (!(reportParams.getClientId() == null)) {
                sqlQueryBuilder.append(" WHERE clientId = '").append(reportParams.getClientId()).append("'");
            }
            ResultSet resultSet = statement.executeQuery(sqlQueryBuilder.toString());
            if (!(reportParams.getReportType() == ReportType.ORDERS_LIST)) {
                Report<String> reportString = new Report<>();
                resultSet.next();

                reportString.setReportData(resultSet.getString(1));
                report = reportString;
            } else {
                Report<List<Order>> reportList = new Report<>();
                List<Order> ordersList = new ArrayList<>();
                Order order;
                while (resultSet.next()) {
                    order = new Order(
                            resultSet.getString("clientId"),
                            resultSet.getLong("requestId"),
                            resultSet.getString("name"),
                            resultSet.getInt("quantity"),
                            resultSet.getBigDecimal("price"));
                    ordersList.add(order);
                }
                reportList.setReportData(ordersList);
                report = reportList;
            }
            statement.close();
        } catch (SQLException e) {
            System.out.println("Couldn't execute the query!");
            e.printStackTrace();
        }

        if (!(reportParams.getClientId() == null)) {
            report.setReportName(reportParams.getReportType().getName() + " (clientId: " + reportParams.getClientId() + ")");
        } else {
            report.setReportName(reportParams.getReportType().getName());
        }

        return report;
    }

    /**
     * This method creates a connection with H2 database.
     *
     * @return a connection (session) with the database.
     * @see Connection
     */
    private Connection getConnection() {
        Connection databaseConnection = null;
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println("org.h2.Driver class not found!");
            e.printStackTrace();
        }
        try {
            databaseConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            System.out.println("Couldn't connect with database!");
            e.printStackTrace();
        }
        return databaseConnection;
    }

    /**
     * This method inserts the {@link Order} data
     * into the {@code ORDERS} table of H2 database.
     *
     * @param order an order to be saved in the database.
     * @see Order
     */
    public void saveData(final Order order) {
        Statement statement;

        try {
            statement = connection.createStatement();
            statement.execute("INSERT INTO ORDERS "
                    + "VALUES (default, '"
                    + order.getClientId() + "', "
                    + order.getRequestId() + ", '"
                    + order.getName() + "', "
                    + order.getQuantity() + ", "
                    + order.getPrice() + ")");
            statement.close();
        } catch (SQLException e) {
            System.out.println("Couldn't execute the query!");
            e.printStackTrace();
        }
    }

    /**
     * This method closes the connection with H2 database.
     *
     * @see Connection
     */
    public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            System.out.println("Couldn't close the connection");
            e.printStackTrace();
        }
    }
}
