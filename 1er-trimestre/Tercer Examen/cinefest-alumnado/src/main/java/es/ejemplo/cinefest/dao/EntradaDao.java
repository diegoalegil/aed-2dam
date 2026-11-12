package es.ejemplo.cinefest.dao;

import es.ejemplo.cinefest.modelo.Entrada;
import es.ejemplo.cinefest.orm.config.OrmFactory;
import es.ejemplo.cinefest.orm.entity.EntradaEntity;
import es.ejemplo.cinefest.orm.mapper.EntradaMapper;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;


public class EntradaDao {

    private final EntityManagerFactory emf;

    public EntradaDao(String jdbcUrl) {
        this.emf = OrmFactory.crear(jdbcUrl);
    }

    private static final Logger LOG = Logger.getLogger(EntradaDao.class.getName());

    // Usar transacciones y rollback en operaciones de escritura.
    public List<Entrada> listar() {
        try (var em = emf.createEntityManager()) {
            return em.createQuery("select e from EntradaEntity e order by e.idEntrada", EntradaEntity.class)
                    .getResultStream()
                    .map(EntradaMapper::desdeEntidad)
                    .toList();
        } catch (RuntimeException ex) {
            LOG.warning("listar: " + ex);
            return List.of();
        }
    }

    public Optional<Entrada> buscarPorId(int id) {

        if (id <= 0) return Optional.empty();

        try (var em = emf.createEntityManager()) {
            return Optional.ofNullable(em.find(EntradaEntity.class, id))
                    .map(EntradaMapper::desdeEntidad);
        } catch (RuntimeException ex) {
            LOG.warning("buscarPorId: " + ex);
            return Optional.empty();
        }
    }

    public int insertar(Entrada dato) {

        if (dato == null || dato.getIdSesion() == null || dato.getIdEspectador() == null
                || dato.getFechaCompra() == null || dato.getFechaCompra().isBlank()
                || (dato.getEstado() != null && !"PAGADA".equals(dato.getEstado()) && !"USADA".equals(dato.getEstado()) && !"CANCELADA".equals(dato.getEstado()))) return 0;
        try (var em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                EntradaEntity e = EntradaMapper.aEntidad(dato);
                e.setIdEntrada(null);
                if (e.getEstado() == null) e.setEstado("PAGADA");
                em.persist(e);
                tx.commit();
                return e.getIdEntrada();
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                LOG.warning("insertar: " + ex);
                return 0;
            }
        }
    }

    public int actualizar(Entrada dato) {

        if (dato == null || dato.getIdEntrada() == null
                || dato.getIdSesion() == null || dato.getIdEspectador() == null
                || dato.getFechaCompra() == null || dato.getFechaCompra().isBlank()
                || (!"PAGADA".equals(dato.getEstado()) && !"USADA".equals(dato.getEstado()) && !"CANCELADA".equals(dato.getEstado())))
            return 0;

        try (var em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                EntradaEntity e = em.find(EntradaEntity.class, dato.getIdEntrada());
                if (e == null) {
                    tx.rollback();
                    return 0;
                }
                e.setIdSesion(dato.getIdSesion());
                e.setIdEspectador(dato.getIdEspectador());
                e.setFechaCompra(dato.getFechaCompra());
                e.setEstado(dato.getEstado());
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
                EntradaEntity e = em.find(EntradaEntity.class, id);
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

    // C08 del README
    public List<Object[]> entradasConDetalles() {

        try (var em = emf.createEntityManager()) {
            return em.createQuery("select e.idEntrada, es.nombre, p.titulo, e.estado "
                            + "from EntradaEntity e join EspectadorEntity es on es.idEspectador = e.idEspectador "
                            + "join SesionEntity s on s.idSesion = e.idSesion "
                            + "join PeliculaEntity p on p.idPelicula = s.idPelicula "
                            + "order by e.idEntrada", Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            LOG.warning("entradasConDetalles: " + ex);
            return List.of();
        }
    }

    // C24 del README
    public List<Object[]> entradasPorEspectador(int idEspectador) {

        if (idEspectador <= 0) return List.of();

        try (var em = emf.createEntityManager()) {
            return em.createQuery("select p.titulo, s.fecha, e.estado, s.precio "
                            + "from EntradaEntity e join SesionEntity s on s.idSesion = e.idSesion "
                            + "join PeliculaEntity p on p.idPelicula = s.idPelicula "
                            + "where e.idEspectador = :id order by s.fecha, p.titulo", Object[].class)
                    .setParameter("id", idEspectador)
                    .getResultList();
        } catch (RuntimeException ex) {
            LOG.warning("entradasPorEspectador: " + ex);
            return List.of();
        }
    }

    // C23 del README
    public List<Object[]> ventasPorDia() {

        try (var em = emf.createEntityManager()) {
            return em.createQuery("select e.fechaCompra, count(e.idEntrada), sum(s.precio) "
                            + "from EntradaEntity e join SesionEntity s on s.idSesion = e.idSesion "
                            + "where e.estado in ('PAGADA','USADA') "
                            + "group by e.fechaCompra order by e.fechaCompra", Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            LOG.warning("ventasPorDia: " + ex);
            return List.of();
        }
    }

    public int cancelarEntrada(int idEntrada) {

        if (idEntrada <= 0) return 0;

        try (var em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                EntradaEntity e = em.find(EntradaEntity.class, idEntrada);
                if (e == null) {
                    tx.rollback();
                    return 0;
                }
                if ("CANCELADA".equals(e.getEstado())) {
                    tx.rollback();
                    return 0;
                }
                e.setEstado("CANCELADA");
                tx.commit();
                return 1;
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                LOG.warning("cancelarEntrada: " + ex);
                return 0;
            }
        }
    }

}
