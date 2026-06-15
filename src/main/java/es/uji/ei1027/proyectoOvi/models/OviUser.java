package es.uji.ei1027.proyectoOvi.models;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class OviUser {
    private String dni;
    private String name;
    private String address;
    private String email;
    private String entityThatIsInvolved;
    private String typeOfFunctionalDiversity;

    private String password;
    private String status;
    private String lifePlan;
    private String tutor_id;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;
    private String rejectReason;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
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
                ", password='" + password + '\'' +
                ", status='" + status + '\'' +
                ", lifePlan='" + lifePlan + '\'' +
                ", tutor_id='" + tutor_id + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", rejectReason='" + rejectReason + '\'' +
                '}';
    }

    public int compareTo(OviUser altre) {
        return this.getDni().compareTo(altre.getDni());
    }
}
