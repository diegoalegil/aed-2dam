package es.ejemplo.academia.modelo;

import java.math.BigDecimal;

/** Entidad Curso (tabla cursos). Clase Java mutable con constructores y accesores. */
public class Curso {
    private Integer id;
    private String titulo;
    private String nivel;
    private Integer horas;
    private BigDecimal precio;

    /** Crea un curso vacío. */
    public Curso() { }

    /**
     * Crea un curso con todos sus datos.
     *
     * @param id     identificador (null si todavía no está guardado)
     * @param titulo título del curso, único
     * @param nivel  BASICO, MEDIO o AVANZADO
     * @param horas  duración en horas, mayor que 0
     * @param precio precio del curso, mayor o igual que 0
     */
    public Curso(Integer id, String titulo, String nivel, Integer horas, BigDecimal precio) {
        this.id = id;
        this.titulo = titulo;
        this.nivel = nivel;
        this.horas = horas;
        this.precio = precio;
    }

    /** @return el identificador del curso */
    public Integer getId() { return id; }

    /** @param id el nuevo identificador */
    public void setId(Integer id) { this.id = id; }

    /** @return el título del curso */
    public String getTitulo() { return titulo; }

    /** @param titulo el nuevo título */
    public void setTitulo(String titulo) { this.titulo = titulo; }

    /** @return el nivel del curso: BASICO, MEDIO o AVANZADO */
    public String getNivel() { return nivel; }

    /** @param nivel el nuevo nivel: BASICO, MEDIO o AVANZADO */
    public void setNivel(String nivel) { this.nivel = nivel; }

    /** @return la duración del curso en horas */
    public Integer getHoras() { return horas; }

    /** @param horas la nueva duración en horas */
    public void setHoras(Integer horas) { this.horas = horas; }

    /** @return el precio del curso */
    public BigDecimal getPrecio() { return precio; }

    /** @param precio el nuevo precio */
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    /** @return el curso en forma de texto, para depurar */
    @Override
    public String toString() {
        return "Curso{id=" + id + ", titulo='" + titulo + "', nivel='" + nivel + "', horas=" + horas
                + ", precio=" + precio + "}";
    }
}
