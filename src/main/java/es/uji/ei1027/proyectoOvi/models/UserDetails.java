package es.uji.ei1027.proyectoOvi.models;

public class UserDetails {
    private String dni;
    private String username;
    private String role;

    public UserDetails() {}

    public UserDetails(String dni, String username, String role) {
        this.dni = dni;
        this.username = username;
        this.role = role;
    }

    // Getters y Setters
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}