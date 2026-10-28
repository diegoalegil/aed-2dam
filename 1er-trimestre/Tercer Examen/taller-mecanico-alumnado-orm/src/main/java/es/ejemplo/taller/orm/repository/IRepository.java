package es.ejemplo.taller.orm.repository;

import java.util.List;
import java.util.Optional;

// Guía §12: contrato genérico del repositorio (T = tipo de dominio, ID = tipo de la clave)
public interface IRepository<T, ID> {

    List<T> findAll();

    Optional<T> findById(ID id);

    void create(T entity);

    boolean update(T entity);

    boolean delete(ID id);
}
