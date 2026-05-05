package es.uji.ei1027.proyectoOvi.models;

public class Tutor {
    private String dni;
    private String name;
    private String email;
    private String status;
    private String password;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    @Override
    public String toString() {
        return "Tutor{" +
                "dni='" + dni + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", status='" + status + '\'' +
                ", userAndPassword='" + password + '\'' +
                '}';
    }

    public int compareTo(Tutor altre) {
        return this.getDni().compareTo(altre.getDni());
    }
}
