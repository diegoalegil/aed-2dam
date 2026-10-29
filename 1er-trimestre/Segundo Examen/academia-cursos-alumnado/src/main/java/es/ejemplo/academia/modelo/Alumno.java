package es.ejemplo.academia.modelo;

/** Entidad Alumno (tabla alumnos). Clase Java mutable con constructores y accesores. */
public class Alumno {
    private Integer id;
    private String nombre;
    private String email;
    private String telefono;

    /** Crea un alumno vacío. */
    public Alumno() { }

    /**
     * Crea un alumno con todos sus datos.
     *
     * @param id       identificador (null si todavía no está guardado)
     * @param nombre   nombre completo
     * @param email    correo electrónico, único
     * @param telefono teléfono, puede ser null
     */
    public Alumno(Integer id, String nombre, String email, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
    }

    /** @return el identificador del alumno */
    public Integer getId() { return id; }

    /** @param id el nuevo identificador */
    public void setId(Integer id) { this.id = id; }

    /** @return el nombre del alumno */
    public String getNombre() { return nombre; }

    /** @param nombre el nuevo nombre */
    public void setNombre(String nombre) { this.nombre = nombre; }

    /** @return el correo electrónico del alumno */
    public String getEmail() { return email; }

    /** @param email el nuevo correo electrónico */
    public void setEmail(String email) { this.email = email; }

    /** @return el teléfono del alumno (puede ser null) */
    public String getTelefono() { return telefono; }

    /** @param telefono el nuevo teléfono (puede ser null) */
    public void setTelefono(String telefono) { this.telefono = telefono; }

    /** @return el alumno en forma de texto, para depurar */
    @Override
    public String toString() {
        return "Alumno{id=" + id + ", nombre='" + nombre + "', email='" + email
                + "', telefono='" + telefono + "'}";
    }
}
