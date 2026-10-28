package es.ejemplo.taller.orm.entity;

import java.math.BigDecimal;

// Guía §9.1: entidad JPA de la tabla mecanicos
// TODO: anotaciones de la clase (entidad y nombre real de la tabla)
public class MecanicoEntity {

    // TODO: clave primaria autogenerada por la BBDD y nombre real de su columna
    private Integer idMecanico;

    // TODO: nombre real de la columna de cada atributo
    private String nombre;
    private String especialidad;
    private BigDecimal tarifaHora;

    // TODO: constructor vacío (lo necesita JPA), constructor con todos los campos, getters y setters
}
