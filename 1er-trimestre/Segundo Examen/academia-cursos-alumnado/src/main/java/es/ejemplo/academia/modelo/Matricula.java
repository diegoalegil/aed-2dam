package es.ejemplo.academia.modelo;

import java.math.BigDecimal;

/** Entidad Matricula (tabla matriculas): un alumno apuntado a un curso. Clase Java mutable. */
public class Matricula {
    private Integer id;
    private Integer idAlumno;
    private Integer idCurso;
    private String fecha;
    private BigDecimal importe;

    /** Crea una matrícula vacía. */
    public Matricula() { }

    /**
     * Crea una matrícula con todos sus datos.
     *
     * @param id       identificador (null si todavía no está guardada)
     * @param idAlumno id del alumno (clave foránea a alumnos)
     * @param idCurso  id del curso (clave foránea a cursos)
     * @param fecha    fecha de la matrícula en formato ISO AAAA-MM-DD
     * @param importe  importe pagado, mayor o igual que 0
     */
    public Matricula(Integer id, Integer idAlumno, Integer idCurso, String fecha, BigDecimal importe) {
        this.id = id;
        this.idAlumno = idAlumno;
        this.idCurso = idCurso;
        this.fecha = fecha;
        this.importe = importe;
    }

    /** @return el identificador de la matrícula */
    public Integer getId() { return id; }

    /** @param id el nuevo identificador */
    public void setId(Integer id) { this.id = id; }

    /** @return el id del alumno matriculado */
    public Integer getIdAlumno() { return idAlumno; }

    /** @param idAlumno el nuevo id de alumno */
    public void setIdAlumno(Integer idAlumno) { this.idAlumno = idAlumno; }

    /** @return el id del curso */
    public Integer getIdCurso() { return idCurso; }

    /** @param idCurso el nuevo id de curso */
    public void setIdCurso(Integer idCurso) { this.idCurso = idCurso; }

    /** @return la fecha de la matrícula (AAAA-MM-DD) */
    public String getFecha() { return fecha; }

    /** @param fecha la nueva fecha (AAAA-MM-DD) */
    public void setFecha(String fecha) { this.fecha = fecha; }

    /** @return el importe pagado */
    public BigDecimal getImporte() { return importe; }

    /** @param importe el nuevo importe */
    public void setImporte(BigDecimal importe) { this.importe = importe; }

    /** @return la matrícula en forma de texto, para depurar */
    @Override
    public String toString() {
        return "Matricula{id=" + id + ", idAlumno=" + idAlumno + ", idCurso=" + idCurso + ", fecha='" + fecha
                + "', importe=" + importe + "}";
    }
}
