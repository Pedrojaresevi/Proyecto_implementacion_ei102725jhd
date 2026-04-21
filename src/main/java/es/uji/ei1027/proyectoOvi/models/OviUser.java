package es.uji.ei1027.proyectoOvi.models;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class OviUser {
    private String dni;
    private String name;
    private String address;
    private String email;
    private String entityThatIsInvolved;
    private String typeOfFunctionalDiversity;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateOfAcceptance;
    private String userAndPassword;
    private String status;
    private String lifePlan;
    private String tutor_id;

    public OviUser() {
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
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

    public String getUserAndPassword() {
        return userAndPassword;
    }

    public void setUserAndPassword(String userAndPassword) {
        this.userAndPassword = userAndPassword;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLifePlan() {
        return lifePlan;
    }

    public void setLifePlan(String lifePlan) {
        this.lifePlan = lifePlan;
    }

    public String getTutor_id() {
        return tutor_id;
    }

    public void setTutor_id(String tutor_id) {
        this.tutor_id = tutor_id;
    }

    @Override
    public String toString() {
        return "OviUser{" +
                "dni='" + dni + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", email='" + email + '\'' +
                ", entityThatIsInvolved='" + entityThatIsInvolved + '\'' +
                ", typeOfFunctionalDiversity='" + typeOfFunctionalDiversity + '\'' +
                ", dateOfAcceptance=" + dateOfAcceptance +
                ", userAndPassword='" + userAndPassword + '\'' +
                ", status='" + status + '\'' +
                ", lifePlan='" + lifePlan + '\'' +
                ", tutor_id='" + tutor_id + '\'' +
                '}';
    }

    public int compareTo(OviUser altre) {
        return this.getDni().compareTo(altre.getDni());
    }
}
