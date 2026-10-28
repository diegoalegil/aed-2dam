package es.ejemplo.taller.orm.entity;

import java.math.BigDecimal;

// Guía §9.2: entidad JPA de la tabla reparaciones
// TODO: anotaciones de la clase (entidad y nombre real de la tabla)
public class ReparacionEntity {

    // TODO: clave primaria autogenerada por la BBDD y nombre real de su columna
    private Integer idReparacion;

    // TODO: FK que escriben las columnas matricula e id_mecanico
    private String matricula;
    private Integer idMecanico;

    // TODO: nombre real de la columna de cada atributo
    private String descripcion;
    private String fecha;
    private BigDecimal horasTrabajo;
    private String estado;

    // TODO: relaciones muchos-a-uno con el vehículo y con el mecánico, de solo lectura y carga diferida
    private VehiculoEntity vehiculo;
    private MecanicoEntity mecanico;

    // TODO: constructor vacío (lo necesita JPA), constructor, getters y setters
}
