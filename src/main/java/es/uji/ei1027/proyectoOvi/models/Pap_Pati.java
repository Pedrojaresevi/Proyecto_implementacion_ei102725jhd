package es.uji.ei1027.proyectoOvi.models;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class Pap_Pati {
    private String name;
    private String surname;
    private String dni;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateOfBirth;
    private String address;
    private String phone;
    private String email;
    private String specificTraining;
    private String typeOfExperience;
    private String curriculumVitae;
    private String status;
    private String password;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
    private String geographicMobility;
    private String skills;
    private String assistant_type;

    public Pap_Pati() {
    }

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getGeographicMobility() {
        return geographicMobility;
    }

    public void setGeographicMobility(String geographicMobility) {
        this.geographicMobility = geographicMobility;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getAssistant_type() {
        return assistant_type;
    }

    public void setAssistant_type(String assistant_type) {
        this.assistant_type = assistant_type;
    }

    @Override
    public String toString() {
        return "Pap_Pati{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", dni='" + dni + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", specificTraining='" + specificTraining + '\'' +
                ", typeOfExperience='" + typeOfExperience + '\'' +
                ", curriculumVitae='" + curriculumVitae + '\'' +
                ", status='" + status + '\'' +
                ", password='" + password + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", geographicMobility='" + geographicMobility + '\'' +
                ", skills='" + skills + '\'' +
                ", assistant_type='" + assistant_type + '\'' +
                '}';
    }
}
