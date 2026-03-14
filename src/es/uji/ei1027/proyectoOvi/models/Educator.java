package es.uji.ei1027.proyectoOvi.models;

public class Educator {
    private String dni;
    private String name;
    private String surname;
    private String id;
    private String address;
    private long phone;
    private String email;
    private String areaOfExperience;
    private String status;
    private String userAndPassword;

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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public String getAreaOfExperience() {
        return areaOfExperience;
    }

    public void setAreaOfExperience(String areaOfExperience) {
        this.areaOfExperience = areaOfExperience;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Educator{" +
                "dni='" + dni + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", id='" + id + '\'' +
                ", address='" + address + '\'' +
                ", phone=" + phone +
                ", email='" + email + '\'' +
                ", areaOfExperience='" + areaOfExperience + '\'' +
                ", status='" + status + '\'' +
                ", userAndPassword='" + userAndPassword + '\'' +
                '}';
    }
}
