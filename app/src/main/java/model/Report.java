package model;

public class Report {
    private String userID;
    private String reportMessage;

    public Report(String userID, String reportMessage) {
        this.userID = userID;
        this.reportMessage = reportMessage;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getReportMessage() {
        return reportMessage;
    }

    public void setReportMessage(String reportMessage) {
        this.reportMessage = reportMessage;
    }
}
