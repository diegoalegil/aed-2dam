package es.ejemplo.cinefest.dao;

import es.ejemplo.cinefest.modelo.Sede;
import es.ejemplo.cinefest.orm.config.OrmFactory;
import es.ejemplo.cinefest.orm.entity.SedeEntity;
import es.ejemplo.cinefest.orm.mapper.SedeMapper;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class SedeDao {

    private final EntityManagerFactory emf;

    public SedeDao(String jdbcUrl) {
        this.emf = OrmFactory.crear(jdbcUrl);
    }

    private static final Logger LOG = Logger.getLogger(SedeDao.class.getName());

    // Usar transacciones y rollback en operaciones de escritura.
    public List<Sede> listar() {
        try (var em = emf.createEntityManager()) {
            return em.createQuery("select s from SedeEntity s order by s.idSede", SedeEntity.class)
                    .getResultStream()
                    .map(SedeMapper::desdeEntidad)
                    .toList();
        } catch (RuntimeException ex) {
            LOG.warning("listar: " + ex);
            return List.of();
        }
    }

    public Optional<Sede> buscarPorId(int id) {

        if (id <= 0) return Optional.empty();

        try (var em = emf.createEntityManager()) {
            return Optional.ofNullable(em.find(SedeEntity.class, id))
                    .map(SedeMapper::desdeEntidad);
        } catch (RuntimeException ex) {
            LOG.warning("buscarPorId: " + ex);
            return Optional.empty();
        }
    }

    public int insertar(Sede dato) {

        if (dato == null || dato.getNombre() == null || dato.getNombre().isBlank()
                || dato.getMunicipio() == null || dato.getMunicipio().isBlank()) return 0;
        try (var em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                SedeEntity e = SedeMapper.aEntidad(dato);
                e.setIdSede(null);
                em.persist(e);
                tx.commit();
                return e.getIdSede();
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                LOG.warning("insertar: " + ex);
                return 0;
            }
        }
    }

    public int actualizar(Sede dato) {

        if (dato == null || dato.getIdSede() == null || dato.getNombre() == null || dato.getNombre().isBlank() || dato.getMunicipio() == null || dato.getMunicipio().isBlank())
            return 0;

        try (var em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                SedeEntity e = em.find(SedeEntity.class, dato.getIdSede());
                if (e == null) {
                    tx.rollback();
                    return 0;
                }
                e.setNombre(dato.getNombre());
                e.setMunicipio(dato.getMunicipio());
                tx.commit();
                return 1;
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                LOG.warning("actualizar: " + ex);
                return 0;
            }
        }
    }

    public int eliminar(int id) {

        if (id <= 0) return 0;

        try (var em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                SedeEntity e = em.find(SedeEntity.class, id);
                if (e == null) {
                    tx.rollback();
                    return 0;
                }
                em.remove(e);
                tx.commit();
                return 1;
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                LOG.warning("eliminar: " + ex);
                return 0;
            }
        }
    }

    public List<Object[]> salasPorSede(int idSede) {
        if (idSede <= 0) return List.of();
        try (var em = emf.createEntityManager()) {
            return em.createQuery("select sa.nombre, sa.aforo from SalaEntity sa "
                            + "where sa.idSede = :id order by sa.nombre", Object[].class)
                    .setParameter("id", idSede)
                    .getResultList();
        } catch (RuntimeException ex) {
            LOG.warning("salasPorSede: " + ex);
            return List.of();
        }
    }

    public List<Object[]> aforoTotalPorSede() {
        try (var em = emf.createEntityManager()) {
            return em.createQuery("select se.nombre, count(sa.idSala), coalesce(sum(sa.aforo), 0) "
                            + "from SedeEntity se left join SalaEntity sa on sa.idSede = se.idSede "
                            + "group by se.idSede, se.nombre order by se.nombre", Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            LOG.warning("aforoTotalPorSede: " + ex);
            return List.of();
        }
    }

}
