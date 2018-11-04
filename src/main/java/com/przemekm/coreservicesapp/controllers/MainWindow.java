package com.przemekm.coreservicesapp.controllers;

import com.przemekm.coreservicesapp.database.H2Database;
import com.przemekm.coreservicesapp.datamodel.*;

import com.przemekm.coreservicesapp.utilities.CSVFileLoader;
import com.przemekm.coreservicesapp.utilities.FileLoader;
import com.przemekm.coreservicesapp.utilities.XMLFileLoader;
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
import javafx.scene.control.TextField;
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

import javax.xml.parsers.DocumentBuilderFactory;
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

    private static Text textToDisplay = new Text("");

    public static void setTextToDisplay(String text) {
        textToDisplay.setText("");
        textToDisplay.setText(text);
    }

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

        textToDisplay.textProperty().addListener((observable, oldValue, newValue) -> {
            Text text = new Text(textToDisplay.getText());
            text.setFont(new Font("Arial", CONSOLE_TEXT_SIZE));
            consoleArea.getChildren().add(text);
        });
    }

    /**
     * This method opens the {@link FileChooser} dialog
     * used for loading files with orders.
     * <p>
     * The allowed file types are CSV and XML.
     * User can choose multiple files at once.
     * After loading the files, the {@link #loadDataFromDatabase()} method is called.
     *
     * @see #loadDataFromDatabase()
     */
    @FXML
    public void loadOrdersDialog() {
        boolean isFileGood = false; //Used as a boolean to disabled/enable parts of the layout.
        FileChooser chooser = new FileChooser();
        FileLoader fileLoader;

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
                if (file.getName()
                        .substring(file
                                .getName()
                                .lastIndexOf("."))
                        .equals(".csv")) {
                    /*
                        loadCSV(File) returns boolean value, which indicates if
                        the file with proper file type has or doesn't have data of orders.
                     */
                    fileLoader = new CSVFileLoader();
                    isFileGood = fileLoader.load(file);
                } else if (file.getName()
                        .substring(file
                                .getName()
                                .lastIndexOf("."))
                        .equals(".xml")) {
                    /*
                        loadXML(File) returns boolean value, which indicates if
                        the file with proper file type has or doesn't have data of orders.
                     */
                    fileLoader = new XMLFileLoader();
                    isFileGood = fileLoader.load(file);
                } else {
                    /*
                        Sends a text message to the console.
                     */
                    setTextToDisplay("Wrong file type of file "
                            + file.getName()
                            + System.lineSeparator());
                }

                /*
                    If the isFileGood boolean is set as "true",
                    elements of layout are set as enabled.
                 */
                if (isFileGood) {
                    ordersTable.setDisable(false);
                    reportsList.setDisable(false);
                    generateButton.setDisable(false);
                } else {
                    ordersTable.setDisable(true);
                    reportsList.setDisable(true);
                    generateButton.setDisable(true);
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
}
