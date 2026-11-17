package es.ejemplo.autoescuela.orm.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "profesores")
public class ProfesorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_profesor")
    private Integer idProfesor;
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "dni")
    private String dni;
    @Column(name = "telefono")
    private String telefono;
    @Column(name = "activo")
    private Integer activo;

    // TODO: relaciones (ver README, apartado 3)

    public ProfesorEntity() {}
    public Integer getIdProfesor() { return idProfesor; }
    public void setIdProfesor(Integer value) { this.idProfesor = value; }
    public String getNombre() { return nombre; }
    public void setNombre(String value) { this.nombre = value; }
    public String getDni() { return dni; }
    public void setDni(String value) { this.dni = value; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String value) { this.telefono = value; }
    public Integer getActivo() { return activo; }
    public void setActivo(Integer value) { this.activo = value; }
}
