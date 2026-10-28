package es.ejemplo.taller.orm.entity;

import java.math.BigDecimal;

// Guía §9.3: entidad JPA de la tabla piezas_reparacion
// TODO: anotaciones de la clase (entidad y nombre real de la tabla)
public class PiezaReparacionEntity {

    // TODO: clave primaria autogenerada por la BBDD y nombre real de su columna
    private Integer idPieza;

    // TODO: FK que escribe la columna id_reparacion
    private Integer idReparacion;

    // TODO: nombre real de la columna de cada atributo
    private String nombre;
    private Integer cantidad;
    private BigDecimal precioUnitario;

    // TODO: relación muchos-a-uno con la reparación, de solo lectura y carga diferida
    private ReparacionEntity reparacion;

    // TODO: constructor vacío (lo necesita JPA), constructor, getters y setters
}
