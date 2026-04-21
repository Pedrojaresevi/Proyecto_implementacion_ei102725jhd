package es.uji.ei1027.proyectoOvi.models;

import java.util.Date;

public class AssignmentRequest {
    private String request_Id;
    private Date requestDate;
    private String typeOfService;
    private Date requiredStartAvailability;
    private Date requiredEndAvailability;
    private String serviceLocation;
    private String requiredTraining;
    private String requiredExperience;
    private String requiredSkills;
    private String pap_patiId;
    private String oviUserId;

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

    public String getServiceLocation() {
        return serviceLocation;
    }

    public void setServiceLocation(String serviceLocation) {
        this.serviceLocation = serviceLocation;
    }

    public String getPap_patiId() {
        return pap_patiId;
    }

    public void setPap_patiId(String pap_patiId) {
        this.pap_patiId = pap_patiId;
    }

    public String getOviUserId() {
        return oviUserId;
    }

    public void setOviUserId(String oviUserId) {
        this.oviUserId = oviUserId;
    }

    public String getRequest_Id() {
        return request_Id;
    }

    public void setRequest_Id(String request_Id) {
        this.request_Id = request_Id;
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
                ", pap_patiId='" + pap_patiId + '\'' +
                ", oviUserId='" + oviUserId + '\'' +
                '}';
    }
}
