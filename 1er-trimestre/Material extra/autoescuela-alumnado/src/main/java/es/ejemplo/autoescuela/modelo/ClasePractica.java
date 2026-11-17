package es.ejemplo.autoescuela.modelo;

import java.math.BigDecimal;

public class ClasePractica {
    private Integer idClase;
    private Integer idAlumno;
    private Integer idProfesor;
    private String matricula;
    private String fecha;
    private String hora;
    private Integer duracionMin;
    private BigDecimal precio;
    private String estado;
    public ClasePractica() {}
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
