package es.ejemplo.autoescuela.modelo;

public class Alumno {
    private Integer idAlumno;
    private String nombre;
    private String email;
    private String fechaAlta;
    private Integer idProfesor;
    private Integer activo;
    public Alumno() {}
    public Integer getIdAlumno() { return idAlumno; }
    public void setIdAlumno(Integer value) { this.idAlumno = value; }
    public String getNombre() { return nombre; }
    public void setNombre(String value) { this.nombre = value; }
    public String getEmail() { return email; }
    public void setEmail(String value) { this.email = value; }
    public String getFechaAlta() { return fechaAlta; }
    public void setFechaAlta(String value) { this.fechaAlta = value; }
    public Integer getIdProfesor() { return idProfesor; }
    public void setIdProfesor(Integer value) { this.idProfesor = value; }
    public Integer getActivo() { return activo; }
    public void setActivo(Integer value) { this.activo = value; }
}
