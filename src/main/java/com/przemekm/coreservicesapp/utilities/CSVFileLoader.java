package com.przemekm.coreservicesapp.utilities;

import com.przemekm.coreservicesapp.controllers.MainWindow;
import com.przemekm.coreservicesapp.database.H2Database;
import com.przemekm.coreservicesapp.datamodel.Order;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class CSVFileLoader implements FileLoader {
    /**
     * This method loads data from the CSV file.
     * <p>
     * Each line of proper data is saved in a H2 database
     * with use of {@link H2Database#saveData(Order)} method.
     * If the file has no proper data or one of the lines is in a wrong format,
     * an {@link IllegalArgumentException} is caught and a message is displayed.
     * <p>
     * It is assumed that the format of CSV file is as follows:
     * <pre>
     * {@code
     *      Client_Id,Request_Id,Name,Quantity,Price
     *      String,long,String,int,BigDecimal
     * }
     * </pre>
     *
     * @param file the {@link File} to read from.
     * @return {@code true} if the file has at least one line of data in proper format.
     * @see H2Database#saveData(Order)
     * @see BufferedReader
     * @see InputStreamReader
     * @see FileInputStream
     */
    @Override
    public boolean load(File file) {
        boolean isFileNotEmpty = false;

        try (BufferedReader reader
                     = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;

            //Skip the first line (CSV headers).
            line = reader.readLine();

            while ((line = reader.readLine()) != null) {
                if (line.length() > 0) {
                    String[] data = line.split(",");

                    try {
                        H2Database.getInstance()
                                .saveData(new Order(data));
                        isFileNotEmpty = true;
                    } catch (IllegalArgumentException e) {
                        MainWindow.setTextToDisplay("Line \""
                                + line
                                + "\" skipped - wrong format!"
                                + System.lineSeparator());
                    }
                }
            }

            if (!isFileNotEmpty) {
                MainWindow.setTextToDisplay("No suitable lines found in CSV file "
                        + file.getName() + "!"
                        + System.lineSeparator());
                return false;
            } else {
                MainWindow.setTextToDisplay("CSV file "
                        + file.getName()
                        + " loaded successfully!" + System.lineSeparator());
                return true;
            }
        } catch (IOException e) {
            System.out.println("Couldn't read the file!");
            e.printStackTrace();
            return false;
        }
    }
}
