package es.uji.ei1027.proyectoOvi.models;

import java.util.Date;

public class Negotiation {
    private String status;
    private String recordOfComunications;
    private Date startDate;
    private Date endDate;
    private String pap_patiId;
    private long requestId;


    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public String getPap_patiId() {
        return pap_patiId;
    }

    public void setPap_patiId(String pap_patiId) {
        this.pap_patiId = pap_patiId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRecordOfComunications() {
        return recordOfComunications;
    }

    public void setRecordOfComunications(String recordOfComunications) {
        this.recordOfComunications = recordOfComunications;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "Negotiation{" +
                "requestId='" + requestId + '\'' +
                ", pap_patiId='" + pap_patiId + '\'' +
                ", status='" + status + '\'' +
                ", recordOfComunications='" + recordOfComunications + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
