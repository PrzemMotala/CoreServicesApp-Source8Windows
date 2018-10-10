package com.przemekm.coreservicesapp.datamodel;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {
    private String data[] = {"Test12",
            String.valueOf(Long.MAX_VALUE),
            "Test Aęć 123",
            String.valueOf(Integer.MAX_VALUE),
            "12345678.90"};

    @Test
    @DisplayName("This data array should get accepted")
    void shouldAcceptData() {
        new Order(data);
    }

    @Test
    @DisplayName("ClientId should be an alphanumeric String without spaces, not longer than 6 characters")
    void clientIdTest() {
        assertAll(
                () -> {
                    String dataTest[] = Arrays.copyOf(data, data.length);
                    dataTest[0] = "Te st1";//Includes spaces
                    assertThrows(IllegalArgumentException.class, () -> new Order(dataTest));
                    },
                () -> {
                    String dataTest[] = Arrays.copyOf(data, data.length);
                    dataTest[0] = "@#$%^&*";//Not alphanumerical
                    assertThrows(IllegalArgumentException.class, () -> new Order(dataTest));
                    },
                () -> {
                    String dataTest[] = Arrays.copyOf(data, data.length);
                    dataTest[0] = "1234567890";//Longer than 6 characters
                    assertThrows(IllegalArgumentException.class, () -> new Order(dataTest));
                    }
        );
    }

    @Test
    @DisplayName("RequestId should be a type of Long")
    void requestIdTest() {
        String dataTest[] = Arrays.copyOf(data, data.length);
        assertAll(
                () -> {
                    dataTest[1] = "Testing @#$";//Letters
                    assertThrows(IllegalArgumentException.class, () -> new Order(dataTest));
                },
                () -> {
                    dataTest[1] = "9223372036854775808";//Bigger than Long.MAX_VALUE
                    assertThrows(IllegalArgumentException.class, () -> new Order(dataTest));
                },
                () -> {
                    dataTest[1] = null;//Null value
                    assertThrows(IllegalArgumentException.class, () -> new Order(dataTest));
                }
        );
    }

    @Test
    @DisplayName("Name should be an alphanumeric String with spaces, not longer than 255 characters")
    void nameTest() {
        String dataTest[] = Arrays.copyOf(data, data.length);
        assertAll(
                () -> {
                    StringBuilder stringBuilder = new StringBuilder(256);
                    for (int i = 0; i < 256; i++){
                        stringBuilder.append("1");
                    }
                    dataTest[2] = stringBuilder.toString();//256 characters
                    assertThrows(IllegalArgumentException.class, () -> new Order(dataTest));
                },
                () -> {
                    dataTest[2] = "@#$%^&*";//Not alphanumerical
                    assertThrows(IllegalArgumentException.class, () -> new Order(dataTest));
                }
        );
    }

    @Test
    @DisplayName("Quantity should be a type of Integer")
    void quantityTest() {
        String dataTest[] = Arrays.copyOf(data, data.length);
        assertAll(
                () -> {
                    dataTest[3] = "Testing @#$";//Letters
                    assertThrows(IllegalArgumentException.class, () -> new Order(dataTest));
                },
                () -> {
                    dataTest[3] = "2147483648";//Bigger than Integer.MAX_VALUE
                    assertThrows(IllegalArgumentException.class, () -> new Order(dataTest));
                }
        );
    }

    @Test
    @DisplayName("Price should be a type of BigDecimal with double precision and total number of digits equal to 12")
    void priceTest() {
        String dataTest[] = Arrays.copyOf(data, data.length);
        assertAll(
                () -> {
                    dataTest[4] = "Testing @#$";//Letters
                    assertThrows(IllegalArgumentException.class, () -> new Order(dataTest));
                },
                () -> {
                    dataTest[4] = "12345678901.23";//Bigger than 12 digits
                    assertThrows(IllegalArgumentException.class, () -> new Order(dataTest));
                },
                () -> {
                    dataTest[4] = "123456789.012";//Wrong precision
                    assertThrows(IllegalArgumentException.class, () -> new Order(dataTest));
                }
        );
    }
}