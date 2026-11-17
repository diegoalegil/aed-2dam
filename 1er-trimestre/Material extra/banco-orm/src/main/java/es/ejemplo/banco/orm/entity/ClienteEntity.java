package es.ejemplo.banco.orm.entity;

// README §4: entidad JPA de la tabla clientes (mira schema.sql)
public class ClienteEntity {

    private Integer idCliente;
    private String dni;
    private String nombre;
    private String email;
    private String telefono;
    private String fechaAlta;
    private Integer idGestor;

    // TODO: anotaciones JPA, constructor protegido, constructor completo, getters y setters
}
