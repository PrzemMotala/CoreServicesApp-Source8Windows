package com.przemekm.coreservicesapp.datamodel;

/**
 * Model class to hold parameters needed
 * for report generation.
 *
 * @author Przemysław Motała
 */
public class ReportParams {
    private ReportType reportType;
    private String clientId;

    /**
     * This constructor creates a {@link ReportParams} object
     * with specified {@link ReportType} and client's ID parameters.
     *
     * @param reportType type of report.
     * @param clientId chosen client's ID.
     */
    public ReportParams(final ReportType reportType, final String clientId) {
        this.reportType = reportType;
        this.clientId = clientId;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public String getClientId() {
        return clientId;
    }
}
