package com.przemekm.coreservicesapp.datamodel;

import java.util.Objects;

/**
 * Model class to hold report information.
 *
 * @param <T> type of data stored in report.
 * @author Przemysław Motała
 */
public class Report<T> {
    private String reportName;
    private T reportData;

    public String getReportName() {
        return reportName;
    }

    public void setReportName(final String reportName) {
        this.reportName = reportName;
    }

    public T getReportData() {
        return reportData;
    }

    public void setReportData(final T reportData) {
        this.reportData = reportData;
    }

    @Override
    public String toString() {
        return reportName;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof Report<?>) {
            Report<?> otherReport = (Report<?>) obj;
            return reportName.equals(otherReport.reportName)
                    && reportData.equals(otherReport.reportData);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(reportName, reportData);
    }
}
