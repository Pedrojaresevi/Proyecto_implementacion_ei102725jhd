package es.uji.ei1027.proyectoOvi.models;

import java.util.Date;

public class AssignmentRequest {
    private Date requestDate;
    private String typeOfService;
    private String requiredAvailability;
    private String serviceLocation;
    private String specificPreferences;
    private String listOfProposedCandidates;
    private long requestId;
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

    public String getRequiredAvailability() {
        return requiredAvailability;
    }

    public void setRequiredAvailability(String requiredAvailability) {
        this.requiredAvailability = requiredAvailability;
    }

    public String getServiceLocation() {
        return serviceLocation;
    }

    public void setServiceLocation(String serviceLocation) {
        this.serviceLocation = serviceLocation;
    }

    public String getSpecificPreferences() {
        return specificPreferences;
    }

    public void setSpecificPreferences(String specificPreferences) {
        this.specificPreferences = specificPreferences;
    }

    public String getListOfProposedCandidates() {
        return listOfProposedCandidates;
    }

    public void setListOfProposedCandidates(String listOfProposedCandidates) {
        this.listOfProposedCandidates = listOfProposedCandidates;
    }

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

    public String getOviUserId() {
        return oviUserId;
    }

    public void setOviUserId(String oviUserId) {
        this.oviUserId = oviUserId;
    }

    @Override
    public String toString() {
        return "AssignmentRequest{" +
                "requestDate=" + requestDate +
                ", typeOfService='" + typeOfService + '\'' +
                ", requiredAvailability='" + requiredAvailability + '\'' +
                ", serviceLocation='" + serviceLocation + '\'' +
                ", specificPreferences='" + specificPreferences + '\'' +
                ", listOfProposedCandidates='" + listOfProposedCandidates + '\'' +
                ", requestId=" + requestId +
                ", pap_patiId='" + pap_patiId + '\'' +
                ", oviUserId='" + oviUserId + '\'' +
                '}';
    }
}
