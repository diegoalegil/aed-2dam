package aula.tareas.repository;

import aula.tareas.api.TareaRequest;
import aula.tareas.orm.config.OrmFactory;
import aula.tareas.orm.entity.TareaEntity;
import aula.tareas.orm.mapper.TareaMapper;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("repositoryOrm")
public class TareaRepositoryOrm implements ITareaRepository {
    private final EntityManagerFactory emf;
    private static final String url = "jdbc:sqlite:data/tareas.sqlite3";

    public TareaRepositoryOrm() {
        this(url);
    }

    public TareaRepositoryOrm(String jdbcUrl) {
        emf = OrmFactory.crear(jdbcUrl);
    }

    @Override
    public TareaRequest create(String titulo) {
        try (var em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                TareaEntity entidad = new TareaEntity();
                entidad.setTitulo(titulo.strip());
                em.persist(entidad);
                tx.commit();
                return TareaMapper.desdeEntidad(entidad);
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                throw ex;
            }
        }
    }

    @Override
    public boolean delete(long identificador) {
        try (var em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                TareaEntity entidad = em.find(TareaEntity.class, (int) identificador);
                if (entidad == null) {
                    tx.rollback();
                    return false;
                }
                em.remove(entidad);
                tx.commit();
                return true;
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                throw ex;
            }
        }
    }

    @Override
    public List<TareaRequest> findAll() {
        try (var em = emf.createEntityManager()) {
            return em.createQuery("select t from TareaEntity t order by t.id", TareaEntity.class)
                    .getResultStream()
                    .map(TareaMapper::desdeEntidad)
                    .toList();
        }
    }

    @Override
    public TareaRequest findById(long identificador) {
        try (var em = emf.createEntityManager()) {
            TareaEntity entidad = em.find(TareaEntity.class, (int) identificador);
            if (entidad == null) {
                return null;
            }
            return TareaMapper.desdeEntidad(entidad);
        }
    }

    @Override
    public TareaRequest update(TareaRequest tarea) {
        try (var em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                TareaEntity entidad = em.find(TareaEntity.class, (int) tarea.id());
                if (entidad == null) {
                    tx.rollback();
                    return null;
                }
                entidad.setTitulo(tarea.titulo());
                tx.commit();
                return TareaMapper.desdeEntidad(entidad);
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                throw ex;
            }
        }
    }
}
