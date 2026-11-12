package es.ejemplo.cinefest.dao;

import es.ejemplo.cinefest.modelo.Sala;
import es.ejemplo.cinefest.orm.config.OrmFactory;
import es.ejemplo.cinefest.orm.entity.SalaEntity;
import es.ejemplo.cinefest.orm.mapper.SalaMapper;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;


public class SalaDao {

    private final EntityManagerFactory emf;

    public SalaDao(String jdbcUrl) {
        this.emf = OrmFactory.crear(jdbcUrl);
    }

    private static final Logger LOG = Logger.getLogger(SalaDao.class.getName());

    // Usar transacciones y rollback en operaciones de escritura.
    public List<Sala> listar() {
        try (var em = emf.createEntityManager()) {
            return em.createQuery("select sa from SalaEntity sa order by sa.idSala", SalaEntity.class)
                    .getResultStream()
                    .map(SalaMapper::desdeEntidad)
                    .toList();
        } catch (RuntimeException ex) {
            LOG.warning("listar: " + ex);
            return List.of();
        }
    }

    public Optional<Sala> buscarPorId(int id) {

        if (id <= 0) return Optional.empty();

        try (var em = emf.createEntityManager()) {
            return Optional.ofNullable(em.find(SalaEntity.class, id))
                    .map(SalaMapper::desdeEntidad);
        } catch (RuntimeException ex) {
            LOG.warning("buscarPorId: " + ex);
            return Optional.empty();
        }
    }

    public int insertar(Sala dato) {

        if (dato == null || dato.getNombre() == null || dato.getNombre().isBlank()
                || dato.getAforo() == null || dato.getAforo() <= 0
                || dato.getIdSede() == null) return 0;
        try (var em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                SalaEntity e = SalaMapper.aEntidad(dato);
                e.setIdSala(null);
                em.persist(e);
                tx.commit();
                return e.getIdSala();
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                LOG.warning("insertar: " + ex);
                return 0;
            }
        }
    }

    public int actualizar(Sala dato) {

        if (dato == null || dato.getIdSala() == null
                || dato.getNombre() == null || dato.getNombre().isBlank()
                || dato.getAforo() == null || dato.getAforo() <= 0
                || dato.getIdSede() == null)
            return 0;

        try (var em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                SalaEntity e = em.find(SalaEntity.class, dato.getIdSala());
                if (e == null) {
                    tx.rollback();
                    return 0;
                }
                e.setNombre(dato.getNombre());
                e.setAforo(dato.getAforo());
                e.setIdSede(dato.getIdSede());
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
                SalaEntity e = em.find(SalaEntity.class, id);
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

    // C06 del README
    public List<Object[]> salasConSede() {

        try (var em = emf.createEntityManager()) {
            return em.createQuery("select se.nombre, sa.nombre, sa.aforo from SalaEntity sa "
                            + "join SedeEntity se on se.idSede = sa.idSede order by se.nombre, sa.nombre", Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            LOG.warning("salasConSede: " + ex);
            return List.of();
        }
    }

    // C28 del README
    public List<Sala> salasSinSesiones() {

        try (var em = emf.createEntityManager()) {
            return em.createQuery("select sa from SalaEntity sa join SedeEntity se on se.idSede = sa.idSede "
                            + "where not exists (select s from SesionEntity s where s.idSala = sa.idSala and s.estado = 'PROGRAMADA') "
                            + "order by se.nombre, sa.nombre", SalaEntity.class)
                    .getResultStream()
                    .map(SalaMapper::desdeEntidad)
                    .toList();
        } catch (RuntimeException ex) {
            LOG.warning("salasSinSesiones: " + ex);
            return List.of();
        }
    }

    public List<Object[]> ocupacionPorSala() {

        try (var em = emf.createEntityManager()) {
            return em.createQuery("select sa.nombre, sa.aforo, count(e.idEntrada) from SalaEntity sa "
                            + "left join SesionEntity s on s.idSala = sa.idSala "
                            + "left join EntradaEntity e on e.idSesion = s.idSesion and e.estado in ('PAGADA','USADA') "
                            + "group by sa.idSala, sa.nombre, sa.aforo order by sa.idSala", Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            LOG.warning("ocupacionPorSala: " + ex);
            return List.of();
        }
    }

}
