package es.ejemplo.autoescuela.orm.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "alumnos")
public class AlumnoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alumno")
    private Integer idAlumno;
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "email")
    private String email;
    @Column(name = "fecha_alta")
    private String fechaAlta;
    @Column(name = "id_profesor")
    private Integer idProfesor;
    @Column(name = "activo")
    private Integer activo;

    // TODO: relaciones (ver README, apartado 3)

    public AlumnoEntity() {}
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
