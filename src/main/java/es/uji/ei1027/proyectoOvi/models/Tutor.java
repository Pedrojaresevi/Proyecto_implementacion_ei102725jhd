package es.uji.ei1027.proyectoOvi.models;

public class Tutor {
    private String dni;
    private String name;
    private String email;
    private String status;

    public String getDni() {
        return dni;
    }

    public void setDni(String id) {
        this.dni = id;
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

    @Override
    public String toString() {
        return "Tutor{" +
                "id='" + dni + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
    public int compareTo(Tutor altre) {
        return this.getDni().compareTo(altre.getDni());
    }
}
