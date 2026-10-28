package es.ejemplo.taller.orm.entity;

// Guía §8: entidad JPA de la tabla vehiculos
// TODO: anotaciones de la clase (entidad y nombre real de la tabla)
public class VehiculoEntity {

    // TODO: clave primaria de texto que pone la aplicación (no se autogenera)
    private String matricula;

    // TODO: nombre real de la columna de cada atributo
    private String marca;
    private String modelo;
    private Integer anio;

    // TODO: FK que escribe la columna id_cliente
    private Integer idCliente;

    // TODO: relación muchos-a-uno con el cliente, de solo lectura y carga diferida
    private ClienteEntity cliente;

    // TODO: constructor vacío (lo necesita JPA), constructor, getters y setters
}
