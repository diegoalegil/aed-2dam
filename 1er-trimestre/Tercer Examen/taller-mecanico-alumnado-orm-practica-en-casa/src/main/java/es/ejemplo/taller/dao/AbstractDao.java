package es.ejemplo.taller.dao;

import es.ejemplo.taller.orm.config.OrmFactory;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

abstract class AbstractDao<T,E,ID> {

    private final EntityManagerFactory managerFactory;

    public AbstractDao(String url){
        managerFactory = OrmFactory.crear(url);
    }

    // Esto lo pone cada DAO hijo
    protected abstract Class<E> getClaseEntidad();
    protected abstract T toDomain(E entidad);

    protected List<T> findAll(){
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select e from " + getClaseEntidad().getSimpleName() + " e", getClaseEntidad())
                    .getResultStream()
                    .map(this::toDomain)
                    .toList();
        }
    }
}
