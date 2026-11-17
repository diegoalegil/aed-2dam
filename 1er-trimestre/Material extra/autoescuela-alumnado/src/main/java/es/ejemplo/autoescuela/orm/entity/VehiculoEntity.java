package es.ejemplo.autoescuela.orm.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "vehiculos")
public class VehiculoEntity {
    @Id
    @Column(name = "matricula")
    private String matricula;
    @Column(name = "marca")
    private String marca;
    @Column(name = "modelo")
    private String modelo;
    @Column(name = "tipo")
    private String tipo;
    @Column(name = "anio")
    private Integer anio;

    // TODO: relaciones (ver README, apartado 3)

    public VehiculoEntity() {}
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
