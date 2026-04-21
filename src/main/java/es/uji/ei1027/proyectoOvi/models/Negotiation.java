package es.uji.ei1027.proyectoOvi.models;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class Negotiation {
    private String negotiation_Id;
    private String status;
    private String recordofcommunications;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
    private String list_id;

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
        return recordofcommunications;
    }

    public void setRecordOfComunications(String recordOfComunications) {
        this.recordofcommunications = recordOfComunications;
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

    public String getListId() {
        return list_id;
    }

    public void setListId(String listId) {
        this.list_id = listId;
    }

    @Override
    public String toString() {
        return "Negotiation{" +
                "negotiation_Id='" + negotiation_Id + '\'' +
                ", status='" + status + '\'' +
                ", recordOfComunications='" + recordofcommunications + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
