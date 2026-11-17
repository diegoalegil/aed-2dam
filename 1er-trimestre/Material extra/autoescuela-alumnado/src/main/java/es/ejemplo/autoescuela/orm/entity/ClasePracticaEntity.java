package es.ejemplo.autoescuela.orm.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "clases_practicas")
public class ClasePracticaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_clase")
    private Integer idClase;
    @Column(name = "id_alumno")
    private Integer idAlumno;
    @Column(name = "id_profesor")
    private Integer idProfesor;
    @Column(name = "matricula")
    private String matricula;
    @Column(name = "fecha")
    private String fecha;
    @Column(name = "hora")
    private String hora;
    @Column(name = "duracion_min")
    private Integer duracionMin;
    @Column(name = "precio")
    private BigDecimal precio;
    @Column(name = "estado")
    private String estado;

    // TODO: relaciones (ver README, apartado 3)

    public ClasePracticaEntity() {}
    public Integer getIdClase() { return idClase; }
    public void setIdClase(Integer value) { this.idClase = value; }
    public Integer getIdAlumno() { return idAlumno; }
    public void setIdAlumno(Integer value) { this.idAlumno = value; }
    public Integer getIdProfesor() { return idProfesor; }
    public void setIdProfesor(Integer value) { this.idProfesor = value; }
    public String getMatricula() { return matricula; }
    public void setMatricula(String value) { this.matricula = value; }
    public String getFecha() { return fecha; }
    public void setFecha(String value) { this.fecha = value; }
    public String getHora() { return hora; }
    public void setHora(String value) { this.hora = value; }
    public Integer getDuracionMin() { return duracionMin; }
    public void setDuracionMin(Integer value) { this.duracionMin = value; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal value) { this.precio = value; }
    public String getEstado() { return estado; }
    public void setEstado(String value) { this.estado = value; }
}
