package com.przemekm.coreservicesapp.controllers;

import com.przemekm.coreservicesapp.database.H2Database;
import com.przemekm.coreservicesapp.datamodel.Order;
import com.przemekm.coreservicesapp.datamodel.Report;
import com.przemekm.coreservicesapp.datamodel.ReportParams;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * This class is a controller for the main window of the application.
 *
 * @author Przemysław Motała
 */
public class MainWindow {
    /*
        These annotated fields are assigned inside of the mainWindow.fxml.
        They represent different parts of the layout.
     */
    @FXML
    private BorderPane mainPane;
    @FXML
    private TableView<Order> ordersTable;
    @FXML
    private ListView<Report<?>> reportsList;
    @FXML
    private TextFlow consoleArea;
    @FXML
    private Button generateButton;
    @FXML
    private Label tempLabel;

    private VBox containerBox = new VBox();
    private TextArea reportDisplayArea = new TextArea();
    private VBox saveButtonBox = new VBox();
    private Button saveButton = new Button("SAVE");

    private ObservableList<Order> loadedOrders
            = FXCollections.observableArrayList();
    private ObservableList<Report<?>> createdReports
            = FXCollections.observableArrayList();

    /**
     * This parameter defines the width of a single column in {@link TableView}.
     *
     * @see TableColumn#setPrefWidth(double)
     */
    private static final int COLUMN_WIDTH = 70;

    /**
     * This parameter defines the size of text inside the report display panel.
     *
     * @see Font
     */
    private static final int REPORT_TEXT_SIZE = 15;

    /**
     * This parameter defines the size of text inside the console panel.
     *
     * @see Font
     */
    private static final int CONSOLE_TEXT_SIZE = 15;

    /**
     * This parameter defines the size of padding around the save button.
     *
     * @see Insets
     */
    private static final int SAVE_BUTTON_PADDING = 10;

    /**
     * This parameter specifies the list of tags included in an order.
     */
    private static final List<String> TAGS_LIST = new ArrayList<>(
            Arrays.asList("clientId", "requestId", "name", "quantity", "price"));

    /**
     * This method is called when {@link MainWindow} is being created.
     * <p>
     * Its purpose is to call the {@link H2Database#getInstance()} method,
     * set the parameters of {@link #ordersTable} columns
     * and populate a {@link #containerBox} with specified elements.
     * <p>
     * A listener is added to the {@link ListView} of reports.
     * When you select a {@link Report}, the {@link #tempLabel} is
     * replaced with {@link #containerBox} in the {@link #mainPane}
     * and the selected {@link Report} is loaded with
     * {@link #displayReport(Report)} method. Also, a specified action
     * is added to the {@link #saveButton}, which occurs when
     * the button is clicked - the method {@link #saveReport(Report)} is called.
     *
     * @see H2Database#getInstance()
     * @see TableColumn
     * @see TableView
     * @see VBox
     * @see #displayReport(Report)
     * @see #saveReport(Report)
     */
    public void initialize() {
        //Calls the H2Database constructor.
        H2Database.getInstance();

        /*
            Sets width of all the columns inside TableView.
            Disables the ability to resize and sort columns.
            Binds the field name of Order class with the specified column.
         */
        TableColumn<Order, String> clientIdCol = new TableColumn<>("ClientId");
        clientIdCol.setPrefWidth(COLUMN_WIDTH);
        clientIdCol.setResizable(false);
        clientIdCol.setSortable(false);
        clientIdCol.setCellValueFactory(
                new PropertyValueFactory<>("clientId"));

        TableColumn<Order, Long> requestIdCol = new TableColumn<>("RequestId");
        requestIdCol.setPrefWidth(COLUMN_WIDTH);
        requestIdCol.setResizable(false);
        requestIdCol.setSortable(false);
        requestIdCol.setCellValueFactory(
                new PropertyValueFactory<>("requestId"));

        TableColumn<Order, String> nameCol = new TableColumn<>("Name");
        nameCol.setPrefWidth(COLUMN_WIDTH);
        nameCol.setResizable(false);
        nameCol.setSortable(false);
        nameCol.setCellValueFactory(
                new PropertyValueFactory<>("name"));

        TableColumn<Order, Integer> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setPrefWidth(COLUMN_WIDTH);
        quantityCol.setResizable(false);
        quantityCol.setSortable(false);
        quantityCol.setCellValueFactory(
                new PropertyValueFactory<>("quantity"));

        TableColumn<Order, BigDecimal> priceCol = new TableColumn<>("Price");
        priceCol.setPrefWidth(COLUMN_WIDTH);
        priceCol.setResizable(false);
        priceCol.setSortable(false);
        priceCol.setCellValueFactory(
                new PropertyValueFactory<>("price"));

        /*
            Connects the TableView with ObservableList of loaded Orders,
            enabling it to automatically update
            whenever an item is added to the list.
            Adds all of the created columns to the TableView.
            Connects the TableView with ObservableList of created Reports.
         */
        ordersTable.setItems(loadedOrders);
        ordersTable.getColumns().addAll(Arrays.asList(
                clientIdCol,
                requestIdCol,
                nameCol,
                quantityCol,
                priceCol));
        reportsList.setItems(createdReports);

        /*
            Sets the font of TextArea where Reports are displayed.
            Disables editing of said TextArea.
            Sets the vertical grow priority of TextArea.
         */
        reportDisplayArea.setFont(new Font("Arial", REPORT_TEXT_SIZE));
        reportDisplayArea.setEditable(false);
        VBox.setVgrow(reportDisplayArea, Priority.ALWAYS);

        /*
            Sets the padding and alignment of the save Button.
         */
        saveButtonBox.setPadding(new Insets(SAVE_BUTTON_PADDING));
        saveButtonBox.setAlignment(Pos.BOTTOM_RIGHT);

        /*
            Adds a save Button to a VBox.
            Adds save Button's VBox and Report's TextArea to the container VBox.
         */
        saveButtonBox.getChildren().add(saveButton);
        containerBox.getChildren().add(reportDisplayArea);
        containerBox.getChildren().add(saveButtonBox);

        /*
            Adds a listener to the ListView of reports.
         */
        reportsList.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (!reportsList.getSelectionModel().isEmpty()) {
                        mainPane.setCenter(containerBox);
                        displayReport(newValue);
                    } else {
                        mainPane.setCenter(tempLabel);
                    }
                }
        );

        /*
            Adds a specified action to the save Button,
            which occurs when the button is clicked.
         */
        saveButton.setOnAction(event ->
                saveReport(reportsList.getSelectionModel().getSelectedItem()));
    }

    /**
     * This method opens the {@link FileChooser} dialog
     * used for loading files with orders.
     * <p>
     * The allowed file types are CSV and XML.
     * Depending on which one is chosen, two methods
     * are being called: {@link #loadCSV(File)} or {@link #loadXML(File)}.
     * User can choose multiple files at once.
     * After loading the files, the {@link #loadDataFromDatabase()} method is called.
     *
     * @see #loadDataFromDatabase()
     * @see #loadCSV(File)
     * @see #loadXML(File)
     */
    @FXML
    public void loadOrdersDialog() {
        boolean isFileGood = false; //Used as a boolean to disabled/enable parts of the layout.
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(
                new FileChooser.
                        ExtensionFilter("Allowed types", "*.csv", "*.xml"));
        List<File> files = chooser.
                showOpenMultipleDialog(mainPane.getScene().getWindow());
        if (files != null) {
            /*
                Before loading new files, the database, console and
                lists of orders and reports are cleared.
             */
            H2Database.getInstance().clearTable();
            loadedOrders.clear();
            createdReports.clear();
            consoleArea.getChildren().clear();

            /*
                Iterates over all of the selected files.
             */
            for (File file : files) {
                /*
                    If the isFileGood boolean is set as "false",
                    elements of layout are set as disabled.
                 */
                if (!isFileGood) {
                    ordersTable.setDisable(true);
                    reportsList.setDisable(true);
                    generateButton.setDisable(true);
                }

                if (file.getName()
                        .substring(file
                                .getName()
                                .lastIndexOf("."))
                        .equals(".csv")) {
                    /*
                        loadCSV(File) returns boolean value, which indicates if
                        the file with proper file type has or doesn't have data of orders.
                     */
                    if (loadCSV(file)) {
                        isFileGood = true;
                    }
                } else if (file.getName()
                        .substring(file
                                .getName()
                                .lastIndexOf("."))
                        .equals(".xml")) {
                    /*
                        loadXML(File) returns boolean value, which indicates if
                        the file with proper file type has or doesn't have data of orders.
                     */
                    if (loadXML(file)) {
                        isFileGood = true;
                    }
                } else {
                    /*
                        Sends a text message to the console.
                     */
                    Text text = new Text("Wrong file type of file "
                            + file.getName()
                            + System.lineSeparator());
                    text.setFill(Color.RED);
                    text.setFont(new Font("Arial", CONSOLE_TEXT_SIZE));
                    consoleArea.getChildren().add(text);
                }

                /*
                    If the isFileGood boolean is set as "true",
                    elements of layout are set as enabled.
                 */
                if (isFileGood) {
                    ordersTable.setDisable(false);
                    reportsList.setDisable(false);
                    generateButton.setDisable(false);
                }
            }
            loadDataFromDatabase();
        }
    }

    /**
     * Creates a dialog window used for generating reports.
     * <p>
     * When the user clicks the "OK" button, the {@link GenerateReports}
     * controller class is instantiated and the {@link GenerateReports#getReportParams()} method
     * is called. The received {@link ReportParams} are then
     * passed into the {@link H2Database#getQueryData(ReportParams)} method,
     * which generates the chosen report.
     *
     * @see Dialog
     * @see FXMLLoader
     * @see Optional
     * @see GenerateReports
     * @see ReportParams
     * @see H2Database#getQueryData(ReportParams)
     */
    @FXML
    public void generateReportsDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainPane.getScene().getWindow());
        dialog.setTitle("Generate reports");
        dialog.setHeaderText("Select which report you want to generate");

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/generateReports.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog!");
            e.printStackTrace();
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> clickResult = dialog.showAndWait();

        if (clickResult.isPresent() && clickResult.get() == ButtonType.OK) {
            GenerateReports generateReports = fxmlLoader.getController();
            ReportParams reportParams = generateReports.getReportParams();
            Report<?> report = H2Database.getInstance()
                    .getQueryData(reportParams);

            /*
                If a chosen report has been created already, a message is displayed.
             */
            if (!createdReports.contains(report)) {
                createdReports.add(report);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.setTitle("Error");
                alert.setContentText("Report \""
                        + report.getReportName()
                        + "\" has been created already!");
                alert.showAndWait();
            }
        }
    }

    /**
     * Displays a report chosen from the {@link #reportsList}.
     * <p>
     * If the data in the {@code report} is an instance of {@link ArrayList}, then it's
     * displayed as list of contained {@link Order} entities. Else, if the data in the
     * {@code report} is an instance of {@link String}, it's displayed as
     * a label and a single value.
     *
     * @param selectedReport the report chosen by the user from the {@link #reportsList}.
     * @see Order
     * @see Report
     */
    private void displayReport(final Report<?> selectedReport) {
        reportDisplayArea.clear();
        if (selectedReport.getReportData() instanceof ArrayList<?>) {
            @SuppressWarnings("unchecked")
            List<Order> data = (ArrayList<Order>) selectedReport.getReportData();
            reportDisplayArea.setText("Client_Id,Request_Id,Name,Quantity,Price"
                    + System.lineSeparator());
            for (int i = 0; i < data.size(); i++) {
                Order order = data.get(i);
                if (i == (data.size() - 1)) {
                    reportDisplayArea.appendText(order.toString());
                } else {
                    reportDisplayArea.appendText(order.toString()
                            + System.lineSeparator());
                }
            }
        } else if (selectedReport.getReportData() instanceof String) {
            String data = (String) selectedReport.getReportData();
            reportDisplayArea.setText(selectedReport.getReportName()
                    + ":"
                    + System.lineSeparator());
            reportDisplayArea.appendText(data);
        } else {
            System.out.println("Error: wrong data format!");
            reportDisplayArea.clear();
        }
    }

    /**
     * This method saves a report chosen from the {@link #reportsList}.
     * <p>
     * A {@link FileChooser} dialog is opened and the user can type the name of a new file.
     * The file type is set as CSV. The content of the file is same as the one displayed with
     * method {@link #displayReport(Report)}.
     *
     * @param selectedReport the report chosen by the user from the {@link #reportsList}.
     * @see FileChooser
     * @see Order
     * @see Report
     */
    private void saveReport(final Report<?> selectedReport) {
        FileChooser chooser = new FileChooser();
        chooser.setInitialFileName(selectedReport.getReportName()
                .replace(" ", "_").concat(".csv"));
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV", "*.csv")
        );
        File file = chooser.showSaveDialog(mainPane.getScene().getWindow());

        if (file != null) {
            try (BufferedWriter fileWriter
                         = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
                if (selectedReport.getReportData() instanceof ArrayList<?>) {
                    @SuppressWarnings("unchecked")
                    List<Order> data = (ArrayList<Order>) selectedReport.getReportData();
                    fileWriter.write("Client_Id,Request_Id,Name,Quantity,Price");
                    fileWriter.newLine();
                    for (int i = 0; i < data.size(); i++) {
                        Order order = data.get(i);
                        if (i == (data.size() - 1)) {
                            fileWriter.write(order.toString());
                        } else {
                            fileWriter.write(order.toString());
                            fileWriter.newLine();
                        }
                    }
                } else if (selectedReport.getReportData() instanceof String) {
                    String data = (String) selectedReport.getReportData();
                    fileWriter.write(selectedReport.getReportName() + ":");
                    fileWriter.newLine();
                    fileWriter.write(data);
                } else {
                    System.out.println("Error: wrong data format!");
                }
            } catch (IOException e) {
                System.out.println("Couldn't save the file!");
                e.printStackTrace();
            }
        }
    }

    /**
     * This method loads all data from the database into the {@link #loadedOrders} list.
     *
     * @see H2Database#getAllData()
     */
    private void loadDataFromDatabase() {
        loadedOrders.addAll(H2Database.getInstance().getAllData());
    }

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
    private boolean loadCSV(final File file) {
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
                        Text text = new Text("Line \""
                                + line
                                + "\" skipped - wrong format!"
                                + System.lineSeparator());
                        text.setFont(new Font("Arial", CONSOLE_TEXT_SIZE));
                        text.setFill(Color.RED);
                        consoleArea.getChildren().add(text);
                    }
                }
            }

            if (!isFileNotEmpty) {
                Text text = new Text("No suitable lines found in CSV file "
                        + file.getName() + "!"
                        + System.lineSeparator());
                text.setFont(new Font("Arial", CONSOLE_TEXT_SIZE));
                text.setFill(Color.RED);
                consoleArea.getChildren().add(text);
                return false;
            } else {
                Text text = new Text("CSV file "
                        + file.getName()
                        + " loaded successfully!" + System.lineSeparator());
                text.setFont(new Font("Arial", CONSOLE_TEXT_SIZE));
                consoleArea.getChildren().add(text);
                return true;
            }
        } catch (IOException e) {
            System.out.println("Couldn't read the file!");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * This method loads data from the XML file.
     * <p>
     * Each line of proper data is saved in a H2 database
     * with use of {@link H2Database#saveData(Order)} method.
     * If the file has missing tags inside {@code <request>} tag or the loaded data is in a wrong format,
     * an {@link IllegalArgumentException} is caught and a message is displayed.
     * <p>
     * It is assumed that the format of XML file is as follows:
     * <pre>
     * {@code
     *     <requests>
     *         <request>
     *             <clientId>String</clientId>
     *             <requestId>long</requestId>
     *             <name>String</name>
     *             <quantity>int</quantity>
     *             <price>BigDecimal</price>
     *         </request>
     *     </requests>
     * }
     * </pre>
     *
     * @param file the {@link File} to read from.
     * @return {@code true} if the file has at least one batch of data in proper format.
     * @see H2Database#saveData(Order)
     * @see DocumentBuilderFactory
     * @see Document
     */
    private boolean loadXML(final File file) {
        boolean isFileNotEmpty = false;

        String[] data = new String[TAGS_LIST.size()];
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        Document document = null;

        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            document = documentBuilder.parse(file);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.out.println("Couldn't parse the file!");
            e.printStackTrace();
        }

        NodeList nodeList = document.getElementsByTagName("request");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                StringBuilder dataBuilder = new StringBuilder();

                for (int j = 0; j < TAGS_LIST.size(); j++) {
                    try {
                        data[j] = element.getElementsByTagName(TAGS_LIST.get(j))
                                .item(0)
                                .getTextContent();
                    } catch (NullPointerException e) {
                        /*
                            Occurs when one of the tags in <request> tag is missing.
                         */
                        data[j] = "";
                    }
                    if (!(j == (TAGS_LIST.size() - 1))) {
                        dataBuilder.append(data[j]).append(",");
                    } else {
                        dataBuilder.append(data[j]);
                    }
                }

                try {
                    H2Database.getInstance()
                            .saveData(new Order(data));
                    isFileNotEmpty = true;
                } catch (IllegalArgumentException e) {
                    Text text = new Text("Line \"" + dataBuilder.toString()
                            + "\" skipped - wrong format!"
                            + System.lineSeparator());
                    text.setFont(new Font("Arial", CONSOLE_TEXT_SIZE));
                    text.setFill(Color.RED);
                    consoleArea.getChildren().add(text);
                }
            }
        }

        if (!isFileNotEmpty) {
            Text text = new Text("No suitable lines found in XML file "
                    + file.getName() + "!"
                    + System.lineSeparator());
            text.setFont(new Font("Arial", CONSOLE_TEXT_SIZE));
            text.setFill(Color.RED);
            consoleArea.getChildren().add(text);
            return false;
        } else {
            Text text = new Text("XML file "
                    + file.getName()
                    + " loaded successfully!" + System.lineSeparator());
            text.setFont(new Font("Arial", CONSOLE_TEXT_SIZE));
            consoleArea.getChildren().add(text);
            return true;
        }
    }
}
