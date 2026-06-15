package es.uji.ei1027.proyectoOvi.models;

import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalTime;

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
    private LocalTime hora;
    // Campo que va a la Base de Datos
    private String emisorDni;

    // Campo que NO va a la Base de datos, solo para enseñar el nombre en HTML
    private String emisorNombre;

    private String interlocutorName;

    public String getInterlocutorName() {
        return interlocutorName;
    }

    public void setInterlocutorName(String interlocutorName) {
        this.interlocutorName = interlocutorName;
    }

    public String getEmisorDni() {
        return emisorDni;
    }

    public void setEmisorDni(String emisorDni) {
        this.emisorDni = emisorDni;
    }

    public String getEmisorNombre() {
        return emisorNombre;
    }

    public void setEmisorNombre(String emisorNombre) {
        this.emisorNombre = emisorNombre;
    }

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

    public LocalTime getHora() { return hora; }

    public void setHora(LocalTime hora) { this.hora = hora; }

    @Override
    public String toString() {
        return "Negotiation{" +
                "negotiation_Id='" + negotiation_Id + '\'' +
                ", status='" + status + '\'' +
                ", recordOfComunications='" + recordofcommunications + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", hora=" + hora +
                ", emisorDNI=" + emisorDni +
                ", emisorNombre=" + emisorNombre +
                '}';
    }
}
