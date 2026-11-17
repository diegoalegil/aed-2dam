package es.ejemplo.banco.modelo;

/**
 * Cliente del banco (dominio, sin anotaciones JPA).
 * Es mutable porque {@code create} le copia el id que genera la base de datos.
 */
public class Cliente {

    private Integer idCliente;
    private String dni;
    private String nombre;
    private String email;
    private String telefono;
    private String fechaAlta;
    private Integer idGestor;

    public Cliente() {}

    public Cliente(Integer idCliente, String dni, String nombre, String email,
                   String telefono, String fechaAlta, Integer idGestor) {
        this.idCliente = idCliente;
        this.dni = dni;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.fechaAlta = fechaAlta;
        this.idGestor = idGestor;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(String fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public Integer getIdGestor() {
        return idGestor;
    }

    public void setIdGestor(Integer idGestor) {
        this.idGestor = idGestor;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "idCliente=" + idCliente +
                ", dni='" + dni + '\'' +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", telefono='" + telefono + '\'' +
                ", fechaAlta='" + fechaAlta + '\'' +
                ", idGestor=" + idGestor +
                '}';
    }
}
