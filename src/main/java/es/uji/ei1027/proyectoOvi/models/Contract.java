package es.uji.ei1027.proyectoOvi.models;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class Contract {
    private String contractId;
    private String requestId;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
    private String status;
    private String placeWhereThePDFIsGonnaBeSaved;
    private String pap_patiID;

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPlaceWhereThePDFIsGonnaBeSaved() {
        return placeWhereThePDFIsGonnaBeSaved;
    }

    public void setPlaceWhereThePDFIsGonnaBeSaved(String placeWhereThePDFIsGonnaBeSaved) {
        this.placeWhereThePDFIsGonnaBeSaved = placeWhereThePDFIsGonnaBeSaved;
    }

    public String getPap_patiID() {
        return pap_patiID;
    }

    public void setPap_patiID(String pap_patiID) {
        this.pap_patiID = pap_patiID;
    }

    @Override
    public String toString() {
        return "Contract{" +
                "contractId=" + contractId +
                ", requestId=" + requestId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status='" + status + '\'' +
                ", placeWhereThePDFIsGonnaBeSaved='" + placeWhereThePDFIsGonnaBeSaved + '\'' +
                ", pap_patiID='" + pap_patiID + '\'' +
                '}';
    }
}
