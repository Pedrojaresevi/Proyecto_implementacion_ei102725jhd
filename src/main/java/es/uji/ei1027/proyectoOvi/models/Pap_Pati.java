package es.uji.ei1027.proyectoOvi.models;

import java.util.Date;

public class Pap_Pati {
    private String name;
    private String surname;
    private String dni;
    private Date dateOfBirth;
    private String address;
    private long phone;
    private String email;
    private String specificTraining;
    private String typeOfExperience;
    private String curriculumVitae;
    private String status;
    private String userAndPassword;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSpecificTraining() {
        return specificTraining;
    }

    public void setSpecificTraining(String specificTraining) {
        this.specificTraining = specificTraining;
    }

    public String getTypeOfExperience() {
        return typeOfExperience;
    }

    public void setTypeOfExperience(String typeOfExperience) {
        this.typeOfExperience = typeOfExperience;
    }

    public String getCurriculumVitae() {
        return curriculumVitae;
    }

    public void setCurriculumVitae(String curriculumVitae) {
        this.curriculumVitae = curriculumVitae;
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

    @Override
    public String toString() {
        return "Pap_Pati{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", dni='" + dni + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", address='" + address + '\'' +
                ", phone=" + phone +
                ", email='" + email + '\'' +
                ", specificTraining='" + specificTraining + '\'' +
                ", typeOfExperience='" + typeOfExperience + '\'' +
                ", curriculumVitae='" + curriculumVitae + '\'' +
                ", status='" + status + '\'' +
                ", userAndPassword='" + userAndPassword + '\'' +
                '}';
    }
}
