package es.ejemplo.taller.orm.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

// Guía §14: CRUD común con EntityManager (T = dominio, E = entidad, ID = clave)
public abstract class AbstractOrmRepository<T, E, ID> implements IRepository<T, ID> {

    protected final EntityManagerFactory factory;
    protected final Class<E> entityClass;
    protected final Function<T, E> toEntity;
    protected final Function<E, T> toDomain;
    protected final Function<T, ID> domainId;
    protected final BiConsumer<T, E> onPersist;

    protected AbstractOrmRepository(EntityManagerFactory factory, Class<E> entityClass,
                                    Function<T, E> toEntity, Function<E, T> toDomain,
                                    Function<T, ID> domainId, BiConsumer<T, E> onPersist) {
        this.factory = factory;
        this.entityClass = entityClass;
        this.toEntity = toEntity;
        this.toDomain = toDomain;
        this.domainId = domainId;
        this.onPersist = onPersist;
    }

    // Escritura: abre un EntityManager con transacción y deshace los cambios si algo falla
    protected <R> R write(Function<EntityManager, R> action) {
        // TODO
        throw new UnsupportedOperationException("Pendiente de implementar");
    }

    // Lectura: abre un EntityManager sin transacción
    protected <R> R read(Function<EntityManager, R> action) {
        // TODO
        throw new UnsupportedOperationException("Pendiente de implementar");
    }

    @Override
    public List<T> findAll() {
        // TODO
        return List.of();
    }

    @Override
    public Optional<T> findById(ID id) {
        // TODO
        return Optional.empty();
    }

    @Override
    public void create(T entity) {
        // TODO
    }

    @Override
    public boolean update(T entity) {
        // TODO
        return false;
    }

    @Override
    public boolean delete(ID id) {
        // TODO
        return false;
    }
}
