package es.ejemplo.cinefest.dao;

import es.ejemplo.cinefest.modelo.Valoracion;
import es.ejemplo.cinefest.orm.config.OrmFactory;
import es.ejemplo.cinefest.orm.entity.ValoracionEntity;
import es.ejemplo.cinefest.orm.mapper.ValoracionMapper;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;


public class ValoracionDao {

    private final EntityManagerFactory emf;

    public ValoracionDao(String jdbcUrl) {
        this.emf = OrmFactory.crear(jdbcUrl);
    }

    private static final Logger LOG = Logger.getLogger(ValoracionDao.class.getName());

    // Usar transacciones y rollback en operaciones de escritura.
    public List<Valoracion> listar() {
        try (var em = emf.createEntityManager()) {
            return em.createQuery("select v from ValoracionEntity v order by v.idValoracion", ValoracionEntity.class)
                    .getResultStream()
                    .map(ValoracionMapper::desdeEntidad)
                    .toList();
        } catch (RuntimeException ex) {
            LOG.warning("listar: " + ex);
            return List.of();
        }
    }

    public Optional<Valoracion> buscarPorId(int id) {

        if (id <= 0) return Optional.empty();

        try (var em = emf.createEntityManager()) {
            return Optional.ofNullable(em.find(ValoracionEntity.class, id))
                    .map(ValoracionMapper::desdeEntidad);
        } catch (RuntimeException ex) {
            LOG.warning("buscarPorId: " + ex);
            return Optional.empty();
        }
    }

    public int insertar(Valoracion dato) {

        if (dato == null || dato.getFecha() == null || dato.getFecha().isBlank()
                || dato.getIdPelicula() == null || dato.getIdEspectador() == null
                || dato.getPuntuacion() == null || dato.getPuntuacion() < 1 || dato.getPuntuacion() > 5) return 0;
        try (var em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                ValoracionEntity e = ValoracionMapper.aEntidad(dato);
                e.setIdValoracion(null);
                em.persist(e);
                tx.commit();
                return e.getIdValoracion();
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                LOG.warning("insertar: " + ex);
                return 0;
            }
        }
    }

    public int actualizar(Valoracion dato) {

        if (dato == null || dato.getIdValoracion() == null
                || dato.getFecha() == null || dato.getFecha().isBlank()
                || dato.getIdPelicula() == null || dato.getIdEspectador() == null
                || dato.getPuntuacion() == null || dato.getPuntuacion() < 1 || dato.getPuntuacion() > 5)
            return 0;

        try (var em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                ValoracionEntity e = em.find(ValoracionEntity.class, dato.getIdValoracion());
                if (e == null) {
                    tx.rollback();
                    return 0;
                }
                e.setIdPelicula(dato.getIdPelicula());
                e.setIdEspectador(dato.getIdEspectador());
                e.setPuntuacion(dato.getPuntuacion());
                e.setComentario(dato.getComentario());
                e.setFecha(dato.getFecha());
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
                ValoracionEntity e = em.find(ValoracionEntity.class, id);
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

    // C10 del README
    public List<Object[]> valoracionesConDetalles() {

        try (var em = emf.createEntityManager()) {
            return em.createQuery("select p.titulo, es.nombre, v.puntuacion from ValoracionEntity v "
                            + "join PeliculaEntity p on p.idPelicula = v.idPelicula "
                            + "join EspectadorEntity es on es.idEspectador = v.idEspectador "
                            + "order by p.titulo, es.nombre", Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            LOG.warning("valoracionesConDetalles: " + ex);
            return List.of();
        }
    }

    public List<Object[]> peliculasSinValoraciones() {

        try (var em = emf.createEntityManager()) {
            return em.createQuery("select p.idPelicula, p.titulo from PeliculaEntity p "
                            + "where not exists (select v from ValoracionEntity v where v.idPelicula = p.idPelicula) "
                            + "order by p.idPelicula", Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            LOG.warning("peliculasSinValoraciones: " + ex);
            return List.of();
        }
    }

    public List<Object[]> valoracionesPorPelicula(int idPelicula) {

        if (idPelicula <= 0) return List.of();

        try (var em = emf.createEntityManager()) {
            return em.createQuery("select es.nombre, v.puntuacion, v.comentario from ValoracionEntity v "
                            + "join EspectadorEntity es on es.idEspectador = v.idEspectador "
                            + "where v.idPelicula = :id order by es.nombre", Object[].class)
                    .setParameter("id", idPelicula)
                    .getResultList();
        } catch (RuntimeException ex) {
            LOG.warning("valoracionesPorPelicula: " + ex);
            return List.of();
        }
    }

    public List<Object[]> estadisticasPuntuaciones() {

        try (var em = emf.createEntityManager()) {
            return em.createQuery("select v.puntuacion, count(v.idValoracion) from ValoracionEntity v "
                            + "group by v.puntuacion order by v.puntuacion", Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            LOG.warning("estadisticasPuntuaciones: " + ex);
            return List.of();
        }
    }

}
