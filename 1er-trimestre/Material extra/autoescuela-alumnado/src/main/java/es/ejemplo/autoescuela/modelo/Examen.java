package es.ejemplo.autoescuela.modelo;

public class Examen {
    private Integer idExamen;
    private Integer idAlumno;
    private String tipo;
    private String fecha;
    private String resultado;
    private Integer errores;
    public Examen() {}
    public Integer getIdExamen() { return idExamen; }
    public void setIdExamen(Integer value) { this.idExamen = value; }
    public Integer getIdAlumno() { return idAlumno; }
    public void setIdAlumno(Integer value) { this.idAlumno = value; }
    public String getTipo() { return tipo; }
    public void setTipo(String value) { this.tipo = value; }
    public String getFecha() { return fecha; }
    public void setFecha(String value) { this.fecha = value; }
    public String getResultado() { return resultado; }
    public void setResultado(String value) { this.resultado = value; }
    public Integer getErrores() { return errores; }
    public void setErrores(Integer value) { this.errores = value; }
}
