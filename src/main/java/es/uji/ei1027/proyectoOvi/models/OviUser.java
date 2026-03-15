package es.uji.ei1027.proyectoOvi.models;

import java.util.Date;

public class OviUser {
    private String id;
    private String name;
    private String address;
    private String email;
    private String entityThatIsInvolved;
    private String typeOfFunctionalDiversity;
    private Date dateOfAcceptance;
    private String status;
    private String userAndPassword;
    private String tutorId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEntityThatIsInvolved() {
        return entityThatIsInvolved;
    }

    public void setEntityThatIsInvolved(String entityThatIsInvolved) {
        this.entityThatIsInvolved = entityThatIsInvolved;
    }

    public String getTypeOfFunctionalDiversity() {
        return typeOfFunctionalDiversity;
    }

    public void setTypeOfFunctionalDiversity(String typeOfFunctionalDiversity) {
        this.typeOfFunctionalDiversity = typeOfFunctionalDiversity;
    }

    public Date getDateOfAcceptance() {
        return dateOfAcceptance;
    }

    public void setDateOfAcceptance(Date dateOfAcceptance) {
        this.dateOfAcceptance = dateOfAcceptance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserAndPassword() {
        return userAndPassword;
    }

    public void setUserAndPassword(String userAndPassword) {
        this.userAndPassword = userAndPassword;
    }

    public String getTutorId() {
        return tutorId;
    }

    public void setTutorId(String tutorId) {
        this.tutorId = tutorId;
    }

    @Override
    public String toString() {
        return "OviUser{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", email='" + email + '\'' +
                ", entityThatIsInvolved='" + entityThatIsInvolved + '\'' +
                ", typeOfFunctionalDiversity='" + typeOfFunctionalDiversity + '\'' +
                ", dateOfAcceptance=" + dateOfAcceptance +
                ", status='" + status + '\'' +
                ", userAndPassword='" + userAndPassword + '\'' +
                ", tutorId='" + tutorId + '\'' +
                '}';
    }
}
