package es.ejemplo.autoescuela.modelo;

public class Vehiculo {
    private String matricula;
    private String marca;
    private String modelo;
    private String tipo;
    private Integer anio;
    public Vehiculo() {}
    public String getMatricula() { return matricula; }
    public void setMatricula(String value) { this.matricula = value; }
    public String getMarca() { return marca; }
    public void setMarca(String value) { this.marca = value; }
    public String getModelo() { return modelo; }
    public void setModelo(String value) { this.modelo = value; }
    public String getTipo() { return tipo; }
    public void setTipo(String value) { this.tipo = value; }
    public Integer getAnio() { return anio; }
    public void setAnio(Integer value) { this.anio = value; }
}
