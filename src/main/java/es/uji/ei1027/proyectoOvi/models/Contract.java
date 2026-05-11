package es.uji.ei1027.proyectoOvi.models;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class Contract {
    private String contract_Id;
    private String request_Id;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
    private String status = "in progress";
    private String placeWhereThePDFIsGonnaBeSaved;
    private String pappati_id;

    public String getContract_Id() {
        return contract_Id;
    }

    public void setContract_Id(String contract_Id) {
        this.contract_Id = contract_Id;
    }

    public String getRequest_Id() {
        return request_Id;
    }

    public void setRequest_Id(String request_Id) {
        this.request_Id = request_Id;
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

    public String getPappati_id() {
        return pappati_id;
    }

    public void setPappati_id(String pappati_id) {
        this.pappati_id = pappati_id;
    }

    @Override
    public String toString() {
        return "Contract{" +
                "contract_Id=" + contract_Id +
                ", request_Id=" + request_Id +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status='" + status + '\'' +
                ", placeWhereThePDFIsGonnaBeSaved='" + placeWhereThePDFIsGonnaBeSaved + '\'' +
                ", pappati_id='" + pappati_id + '\'' +
                '}';
    }
}
