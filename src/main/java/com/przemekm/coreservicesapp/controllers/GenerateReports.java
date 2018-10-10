package com.przemekm.coreservicesapp.controllers;

import com.przemekm.coreservicesapp.database.H2Database;
import com.przemekm.coreservicesapp.datamodel.ReportParams;
import com.przemekm.coreservicesapp.datamodel.ReportType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;

import java.util.Arrays;

/**
 * This class is a controller for the dialog window of the application,
 * which allows user to create reports.
 *
 * @author Przemysław Motała
 */
public class GenerateReports {
    /*
        These annotated fields are assigned inside of the generateReports.fxml.
        They represent different parts of the layout.
     */
    @FXML
    private ComboBox<ReportType> reportTypeBox;
    @FXML
    private ComboBox<String> clientIdBox;
    @FXML
    private CheckBox clientIdCheckBox;

    private ObservableList<ReportType> reportList
            = FXCollections.observableArrayList();
    private ObservableList<String> clientIdList
            = FXCollections.observableArrayList();

    /**
     * This method is called when {@link GenerateReports} window
     * is being created.
     * <p>
     * Its purpose is to add listener to the
     * {@link #clientIdCheckBox}. If it is selected,
     * the {@link #clientIdBox} is set as enabled and
     * the user is allowed to choose a {@code clientId}
     * from the list.
     * <p>
     * The {@link #reportList} is populated with all items in
     * the {@link ReportType} enum class. The {@link #clientIdList}
     * is populated with data retrieved from the database
     * with use of {@link H2Database#getClientIdData()} method.
     *
     * @see CheckBox
     * @see ComboBox
     */
    public void initialize() {
        clientIdCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                clientIdBox.setDisable(false);
                clientIdBox.getSelectionModel().selectFirst();
            } else {
                clientIdBox.setDisable(true);
                clientIdBox.getSelectionModel().clearSelection();
            }
        });

        reportTypeBox.setItems(reportList);
        reportList.addAll(Arrays.asList(ReportType.values()));

        clientIdBox.setItems(clientIdList);
        clientIdList.addAll(H2Database.getInstance().getClientIdData());

        reportTypeBox.getSelectionModel().selectFirst();
    }

    /**
     * This method creates a new {@link ReportParams} object
     * with the parameters selected in the {@link #reportTypeBox}
     * and {@link #clientIdBox}.
     *
     * @return {@link ReportParams} object with data needed for creation of the report.
     * @see ReportParams
     */
    public ReportParams getReportParams() {
        return new ReportParams(
                reportTypeBox.getSelectionModel().getSelectedItem(),
                clientIdBox.getSelectionModel().getSelectedItem());
    }
}
