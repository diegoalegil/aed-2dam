package es.ejemplo.taller.orm.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "mecanicos")
public class MecanicoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mecanico")
    private Integer idMecanico;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "especialidad", nullable = false)
    private String especialidad;

    @Column(name = "tarifa_hora", nullable = false)
    private BigDecimal tarifaHora;

    protected MecanicoEntity() {} // Necesario para JPA

    public MecanicoEntity(Integer idMecanico, String nombre,
                          String especialidad, BigDecimal tarifaHora) {
        this.idMecanico = idMecanico;
        this.nombre = nombre;
        this.especialidad = especialidad;
        this.tarifaHora = tarifaHora;
    }

    public Integer getIdMecanico() {
        return idMecanico;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public BigDecimal getTarifaHora() {
        return tarifaHora;
    }

    public void setTarifaHora(BigDecimal tarifaHora) {
        this.tarifaHora = tarifaHora;
    }
}
