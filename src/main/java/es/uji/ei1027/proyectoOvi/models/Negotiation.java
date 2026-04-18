package es.uji.ei1027.proyectoOvi.models;

import java.util.Date;

public class Negotiation {
    private String negotiation_Id;
    private String status;
    private String recordOfComunications;
    private Date startDate;
    private Date endDate;


    public String getNegotiation_Id() {
        return negotiation_Id;
    }

    public void setNegotiation_Id(String negotiation_Id) {
        this.negotiation_Id = negotiation_Id;
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
                "negotiation_Id='" + negotiation_Id + '\'' +
                ", status='" + status + '\'' +
                ", recordOfComunications='" + recordOfComunications + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
