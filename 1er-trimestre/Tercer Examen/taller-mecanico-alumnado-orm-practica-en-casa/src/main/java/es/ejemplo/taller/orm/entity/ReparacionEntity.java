package es.ejemplo.taller.orm.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reparaciones")
public class ReparacionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reparacion")
    private Integer idReparacion;

    @Column(name = "matricula")
    private String matricula;

    @Column(name = "id_mecanico")
    private Integer idMecanico;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "fecha")
    private String fecha;

    @Column(name = "horas_trabajo")
    private BigDecimal horasTrabajo;

    @Column(name = "estado")
    private String estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matricula", insertable = false, updatable = false)
    private VehiculoEntity vehiculo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mecanico", insertable = false, updatable = false)
    private MecanicoEntity mecanico;

    @OneToMany(mappedBy = "reparacion", fetch = FetchType.LAZY)
    private List<PiezaReparacionEntity> piezas = new ArrayList<>();

    protected ReparacionEntity() {} // Necesario para JPA

    public ReparacionEntity(Integer idReparacion, String matricula, Integer idMecanico, String descripcion,
                            String fecha, BigDecimal horasTrabajo, String estado) {
        this.idReparacion = idReparacion;
        this.matricula = matricula;
        this.idMecanico = idMecanico;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.horasTrabajo = horasTrabajo;
        this.estado = estado;
    }

    public Integer getIdReparacion() {
        return idReparacion;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public Integer getIdMecanico() {
        return idMecanico;
    }

    public void setIdMecanico(Integer idMecanico) {
        this.idMecanico = idMecanico;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getHorasTrabajo() {
        return horasTrabajo;
    }

    public void setHorasTrabajo(BigDecimal horasTrabajo) {
        this.horasTrabajo = horasTrabajo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public VehiculoEntity getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(VehiculoEntity vehiculo) {
        this.vehiculo = vehiculo;
    }

    public MecanicoEntity getMecanico() {
        return mecanico;
    }

    public void setMecanico(MecanicoEntity mecanico) {
        this.mecanico = mecanico;
    }
}
