package com.przemekm.coreservicesapp.datamodel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Model class to hold order information.
 *
 * @author Przemysław Motała
 */
public final class Order {
    private String clientId;
    private long requestId;
    private String name;
    private int quantity;
    private BigDecimal price;

    /**
     * This parameter specifies the list of parameters included in an order.
     */
    private static final List<String> PARAMS_LIST = new ArrayList<>(
            Arrays.asList("clientId", "requestId", "name", "quantity", "price"));

    /**
     * This constructor creates {@link Order} object
     * with data provided as an array of {@link String} items.
     * <p>
     * Data validation is performed via
     * use of {@link #checkData(String[])} method.
     *
     * @param data array of order's parameters.
     * @see #checkData(String[])
     */
    public Order(final String[] data) {
        if (checkData(data)) {
            clientId = data[PARAMS_LIST.indexOf("clientId")];
            requestId = Long.parseLong(data[PARAMS_LIST.indexOf("requestId")]);
            name = data[PARAMS_LIST.indexOf("name")];
            quantity = Integer.parseInt(data[PARAMS_LIST.indexOf("quantity")]);
            price = new BigDecimal(data[PARAMS_LIST.indexOf("price")]);
        } else {
            throw new IllegalArgumentException("Arguments are not valid!");
        }
    }

    /**
     * This constructor creates {@link Order} object
     * with data provided as individual order's parameters.
     * <p>
     * Data validation is performed via
     * use of {@link #checkData(String[])} method.
     *
     * @param clientId client's ID.
     * @param requestId request's ID.
     * @param name order's name.
     * @param quantity order's quantity.
     * @param price order's price.
     * @see #checkData(String[])
     */
    public Order(final String clientId,
                 final long requestId,
                 final String name,
                 final int quantity,
                 final BigDecimal price) {
        String[] data = new String[] {clientId,
                Long.toString(requestId),
                name, Integer.toString(quantity),
                price.toString()};

        if (checkData(data)) {
            this.clientId = clientId;
            this.requestId = requestId;
            this.name = name;
            this.quantity = quantity;
            this.price = price;
        } else {
            throw new IllegalArgumentException("Arguments are not valid!");
        }
    }

    public String getClientId() {
        return clientId;
    }

    public Long getRequestId() {
        return requestId;
    }

    public String getName() {
        return name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    /**
     * This method checks the validity of loaded data.
     * <p>
     * There are five elements included in an order, as specified in {@link #PARAMS_LIST}.
     * Each of them is validated with a regular expression. The {@code requestId}, {@code quantity}
     * and {@code price} values are checked with use of {@link #isStringLong(String)}, {@link #isStringInt(String)} and
     * {@link #isStringBigDecimal(String)} methods to see if they can be parsed into the corresponding types.
     * <p>
     * The data fields are validated as follows:
     * <pre>
     *  {@code clientId} - should be an alphanumeric {@link String} without spaces,
     *              not longer than 6 characters.
     *  {@code requestId} - should be a type of {@link Long}.
     *  {@code name} - should be an alphanumeric {@link String} with spaces,
     *          not longer than 255 characters.
     *  {@code quantity} - should be a type of {@link Integer}.
     *  {@code price} - should be a type of {@link BigDecimal} with double precision and total
     *           number of digits equal to 12.
     * </pre>
     *
     * @param data an array of type {@link String} which contains data of loaded order.
     * @return {@code true} if the provided {@code data} is valid.
     * @see #isStringLong(String)
     * @see #isStringInt(String)
     * @see #isStringBigDecimal(String)
     */
    private boolean checkData(final String[] data) {
        if (data.length != PARAMS_LIST.size()) {
            return false;
        } else {
            for (String paramName : PARAMS_LIST) {
                if (data[PARAMS_LIST.indexOf(paramName)] == null) {
                    return false;
                }
            }

            if (data[PARAMS_LIST.indexOf("clientId")]
                    .matches("^[a-zA-Z0-9ĄąĆćĘęŁłŃńÓóŚśŹźŻż]{1,6}$")
                    && isStringLong(data[PARAMS_LIST.indexOf("requestId")])
                    && data[PARAMS_LIST.indexOf("name")]
                    .trim()
                    .matches("^[a-zA-Z0-9ĄąĆćĘęŁłŃńÓóŚśŹźŻż ]{1,255}$")
                    && isStringInt(data[PARAMS_LIST.indexOf("quantity")])
                    && isStringBigDecimal(data[PARAMS_LIST.indexOf("price")])) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method checks if a provided {@link String}
     * can be parsed into a {@link Long} type.
     *
     * @param string a value to be checked.
     * @return {@code true} if a {@code string} is valid.
     */
    private boolean isStringLong(final String string) {
        if (string.matches("^[0-9]+$")) {
            try {
                Long.parseLong(string);
            } catch (NumberFormatException e) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * This method checks if a provided {@link String}
     * can be parsed into a {@link Integer} type.
     *
     * @param string a value to be checked.
     * @return {@code true} if a {@code string} is valid.
     */
    private boolean isStringInt(final String string) {
        if (string.matches("^[0-9]+$")) {
            try {
                Integer.parseInt(string);
            } catch (NumberFormatException e) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * This method checks if a provided {@link String}
     * can be parsed into a {@link BigDecimal} type.
     *
     * @param string a value to be checked.
     * @return {@code true} if a {@code string} is valid.
     */
    private boolean isStringBigDecimal(String string) {
        /*
            This part of code removes the leading zeros.
         */
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) != '0') {
                if ((string.charAt(i) == '.')
                        && (i != 0)) {
                    string = string.substring(i - 1);
                    break;
                } else {
                    string = string.substring(i);
                    break;
                }
            }
        }
        if (string.matches("^[0-9]{1,10}.[0-9]{2}$")) {
            try {
                new BigDecimal(string);
            } catch (NumberFormatException e) {
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return clientId
                + "," + requestId
                + "," + name
                + "," + quantity
                + "," + price;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof Order) {
            Order otherOrder = (Order) obj;
            return clientId.equals(otherOrder.clientId)
                    && (requestId == otherOrder.requestId)
                    && name.equals(otherOrder.name)
                    && (quantity == otherOrder.quantity)
                    && price.equals(otherOrder.price);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, requestId, name, quantity, price);
    }
}
