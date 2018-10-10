package com.przemekm.coreservicesapp.datamodel;

/**
 * Enum class used as a container of
 * different SQL queries.
 *
 * @author Przemysław Motała
 */
public enum ReportType {
    /**
     * Query to get the total amount of orders.
     */
    ORDERS_AMOUNT("Total amount of orders", "SELECT COUNT(id) FROM ORDERS"),
    /**
     * Query to get the total price of orders.
     */
    TOTAL_PRICE("Total price of orders", "SELECT SUM(price) FROM ORDERS"),
    /**
     * Query to get the list of all orders.
     */
    ORDERS_LIST("List of all orders", "SELECT * FROM ORDERS"),
    /**
     * Query to get the average price of order.
     */
    AVERAGE_PRICE("Average price of order", "SELECT CAST(AVG(price) AS DECIMAL(12,2)) FROM ORDERS");

    private String name;
    private String sqlQuery;

    /**
     * This constructor creates a {@link ReportType} object
     * with specified name and SQL query parameters.
     *
     * @param name SQL query's name.
     * @param sqlQuery SQL query.
     */
    ReportType(final String name, final String sqlQuery) {
        this.name = name;
        this.sqlQuery = sqlQuery;
    }

    public String getName() {
        return name;
    }

    public String getSqlQuery() {
        return sqlQuery;
    }

    @Override
    public String toString() {
        return name;
    }
}
