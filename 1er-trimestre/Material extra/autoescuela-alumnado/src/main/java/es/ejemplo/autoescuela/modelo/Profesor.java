package es.ejemplo.autoescuela.modelo;

public class Profesor {
    private Integer idProfesor;
    private String nombre;
    private String dni;
    private String telefono;
    private Integer activo;
    public Profesor() {}
    public Integer getIdProfesor() { return idProfesor; }
    public void setIdProfesor(Integer value) { this.idProfesor = value; }
    public String getNombre() { return nombre; }
    public void setNombre(String value) { this.nombre = value; }
    public String getDni() { return dni; }
    public void setDni(String value) { this.dni = value; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String value) { this.telefono = value; }
    public Integer getActivo() { return activo; }
    public void setActivo(Integer value) { this.activo = value; }
}
