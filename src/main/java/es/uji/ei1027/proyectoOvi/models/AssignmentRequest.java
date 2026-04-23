package es.uji.ei1027.proyectoOvi.models;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class AssignmentRequest {
    private String request_Id;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date requestDate;
    private String typeOfService;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date requiredStartAvailability;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date requiredEndAvailability;
    private String serviceLocation;
    private String requiredTraining;
    private String requiredExperience;
    private String requiredSkills;
    private String oviuser_id;
    private String status;

    public AssignmentRequest() {
    }

    public String getRequest_Id() {
        return request_Id;
    }

    public void setRequest_Id(String request_Id) {
        this.request_Id = request_Id;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public String getTypeOfService() {
        return typeOfService;
    }

    public void setTypeOfService(String typeOfService) {
        this.typeOfService = typeOfService;
    }

    public Date getRequiredStartAvailability() {
        return requiredStartAvailability;
    }

    public void setRequiredStartAvailability(Date requiredStartAvailability) {
        this.requiredStartAvailability = requiredStartAvailability;
    }

    public Date getRequiredEndAvailability() {
        return requiredEndAvailability;
    }

    public void setRequiredEndAvailability(Date requiredEndAvailability) {
        this.requiredEndAvailability = requiredEndAvailability;
    }

    public String getServiceLocation() {
        return serviceLocation;
    }

    public void setServiceLocation(String serviceLocation) {
        this.serviceLocation = serviceLocation;
    }

    public String getRequiredTraining() {
        return requiredTraining;
    }

    public void setRequiredTraining(String requiredTraining) {
        this.requiredTraining = requiredTraining;
    }

    public String getRequiredExperience() {
        return requiredExperience;
    }

    public void setRequiredExperience(String requiredExperience) {
        this.requiredExperience = requiredExperience;
    }

    public String getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(String requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public String getOviuser_id() {
        return oviuser_id;
    }

    public void setOviuser_id(String oviuser_id) {
        this.oviuser_id = oviuser_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "AssignmentRequest{" +
                "request_Id='" + request_Id + '\'' +
                ", requestDate=" + requestDate +
                ", typeOfService='" + typeOfService + '\'' +
                ", requiredStartAvailability=" + requiredStartAvailability +
                ", requiredEndAvailability=" + requiredEndAvailability +
                ", serviceLocation='" + serviceLocation + '\'' +
                ", requiredTraining='" + requiredTraining + '\'' +
                ", requiredExperience='" + requiredExperience + '\'' +
                ", requiredSkills='" + requiredSkills + '\'' +
                ", oviuser_id='" + oviuser_id + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
