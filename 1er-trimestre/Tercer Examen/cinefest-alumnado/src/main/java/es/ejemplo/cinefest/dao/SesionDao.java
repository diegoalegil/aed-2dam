package es.ejemplo.cinefest.dao;

import es.ejemplo.cinefest.modelo.Sesion;
import es.ejemplo.cinefest.orm.config.OrmFactory;
import es.ejemplo.cinefest.orm.entity.EntradaEntity;
import es.ejemplo.cinefest.orm.entity.EspectadorEntity;
import es.ejemplo.cinefest.orm.entity.SalaEntity;
import es.ejemplo.cinefest.orm.entity.SesionEntity;
import es.ejemplo.cinefest.orm.mapper.SesionMapper;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class SesionDao {

    private final EntityManagerFactory emf;

    public SesionDao(String jdbcUrl) {
        this.emf = OrmFactory.crear(jdbcUrl);
    }

    private static final Logger LOG = Logger.getLogger(SesionDao.class.getName());

    // Usar transacciones y rollback en operaciones de escritura.
    public List<Sesion> listar() {
        try (var em = emf.createEntityManager()) {
            return em.createQuery("select s from SesionEntity s order by s.idSesion", SesionEntity.class)
                    .getResultStream()
                    .map(SesionMapper::desdeEntidad)
                    .toList();
        } catch (RuntimeException ex) {
            LOG.warning("listar: " + ex);
            return List.of();
        }
    }

    public Optional<Sesion> buscarPorId(int id) {

        if (id <= 0) return Optional.empty();

        try (var em = emf.createEntityManager()) {
            return Optional.ofNullable(em.find(SesionEntity.class, id))
                    .map(SesionMapper::desdeEntidad);
        } catch (RuntimeException ex) {
            LOG.warning("buscarPorId: " + ex);
            return Optional.empty();
        }
    }

    public int insertar(Sesion dato) {

        if (dato == null || dato.getIdPelicula() == null || dato.getIdSala() == null
                || dato.getFecha() == null || dato.getFecha().isBlank()
                || dato.getHora() == null || dato.getHora().isBlank()
                || dato.getPrecio() == null || dato.getPrecio() < 0
                || (dato.getEstado() != null && !"PROGRAMADA".equals(dato.getEstado()) && !"CANCELADA".equals(dato.getEstado()) && !"FINALIZADA".equals(dato.getEstado()))) return 0;
        try (var em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                SesionEntity e = SesionMapper.aEntidad(dato);
                e.setIdSesion(null);
                if (e.getEstado() == null) e.setEstado("PROGRAMADA");
                em.persist(e);
                tx.commit();
                return e.getIdSesion();
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                LOG.warning("insertar: " + ex);
                return 0;
            }
        }
    }

    public int actualizar(Sesion dato) {

        if (dato == null || dato.getIdSesion() == null
                || dato.getIdPelicula() == null || dato.getIdSala() == null
                || dato.getFecha() == null || dato.getFecha().isBlank()
                || dato.getHora() == null || dato.getHora().isBlank()
                || dato.getPrecio() == null || dato.getPrecio() < 0
                || (!"PROGRAMADA".equals(dato.getEstado()) && !"CANCELADA".equals(dato.getEstado()) && !"FINALIZADA".equals(dato.getEstado())))
            return 0;

        try (var em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                SesionEntity e = em.find(SesionEntity.class, dato.getIdSesion());
                if (e == null) {
                    tx.rollback();
                    return 0;
                }
                e.setIdPelicula(dato.getIdPelicula());
                e.setIdSala(dato.getIdSala());
                e.setFecha(dato.getFecha());
                e.setHora(dato.getHora());
                e.setPrecio(dato.getPrecio());
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
                SesionEntity e = em.find(SesionEntity.class, id);
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

    // C07 del README
    public List<Object[]> sesionesConDetalles() {

        try (var em = emf.createEntityManager()) {
            return em.createQuery("select s.idSesion, p.titulo, se.nombre, sa.nombre, s.fecha, s.hora "
                            + "from SesionEntity s join PeliculaEntity p on p.idPelicula = s.idPelicula "
                            + "join SalaEntity sa on sa.idSala = s.idSala "
                            + "join SedeEntity se on se.idSede = sa.idSede "
                            + "order by s.idSesion", Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            LOG.warning("sesionesConDetalles: " + ex);
            return List.of();
        }
    }

    // C04 del README
    public List<Object[]> sesionesPorFecha(String fecha) {

        if (fecha == null || fecha.isBlank()) return List.of();

        try (var em = emf.createEntityManager()) {
            return em.createQuery("select s.fecha, s.hora, s.idSesion, s.precio from SesionEntity s "
                            + "where s.fecha = :fecha order by s.fecha, s.hora, s.idSesion", Object[].class)
                    .setParameter("fecha", fecha)
                    .getResultList();
        } catch (RuntimeException ex) {
            LOG.warning("sesionesPorFecha: " + ex);
            return List.of();
        }
    }

    // C17 del README
    public List<Object[]> sesionesSinVentas() {

        try (var em = emf.createEntityManager()) {
            return em.createQuery("select s.idSesion, p.titulo, s.fecha "
                            + "from SesionEntity s join PeliculaEntity p on p.idPelicula = s.idPelicula "
                            + "where s.estado = 'PROGRAMADA' and not exists "
                            + "(select e from EntradaEntity e where e.idSesion = s.idSesion and e.estado in ('PAGADA','USADA')) "
                            + "order by s.idSesion", Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            LOG.warning("sesionesSinVentas: " + ex);
            return List.of();
        }
    }

    // C15 del README
    public List<Object[]> disponibilidadSesiones() {

        try (var em = emf.createEntityManager()) {
            return em.createQuery("select s.idSesion, sa.aforo, count(e.idEntrada), sa.aforo - count(e.idEntrada) "
                            + "from SesionEntity s join SalaEntity sa on sa.idSala = s.idSala "
                            + "left join EntradaEntity e on e.idSesion = s.idSesion and e.estado in ('PAGADA','USADA') "
                            + "group by s.idSesion, sa.aforo order by s.idSesion", Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            LOG.warning("disponibilidadSesiones: " + ex);
            return List.of();
        }
    }

    // C18 del README
    public List<Object[]> sesionesAgotadas() {

        try (var em = emf.createEntityManager()) {
            return em.createQuery("select s.idSesion, p.titulo, sa.aforo "
                            + "from SesionEntity s join PeliculaEntity p on p.idPelicula = s.idPelicula "
                            + "join SalaEntity sa on sa.idSala = s.idSala "
                            + "join EntradaEntity e on e.idSesion = s.idSesion "
                            + "where e.estado in ('PAGADA','USADA') "
                            + "group by s.idSesion, p.titulo, sa.aforo having count(e.idEntrada) >= sa.aforo "
                            + "order by s.idSesion", Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            LOG.warning("sesionesAgotadas: " + ex);
            return List.of();
        }
    }

    // C21 del README
    public List<Object[]> rankingRecaudacion() {

        try (var em = emf.createEntityManager()) {
            return em.createQuery("select p.titulo, count(e.idEntrada), sum(s.precio) "
                            + "from PeliculaEntity p join SesionEntity s on s.idPelicula = p.idPelicula "
                            + "join EntradaEntity e on e.idSesion = s.idSesion "
                            + "where e.estado in ('PAGADA','USADA') "
                            + "group by p.idPelicula, p.titulo order by sum(s.precio) desc, p.titulo", Object[].class)
                    .setMaxResults(3)
                    .getResultList();
        } catch (RuntimeException ex) {
            LOG.warning("rankingRecaudacion: " + ex);
            return List.of();
        }
    }

    // C22 del README
    public List<Object[]> ingresosPorSede() {

        try (var em = emf.createEntityManager()) {
            return em.createQuery("select se.nombre, coalesce(sum(case when e.idEntrada is not null then s.precio end), 0) "
                            + "from SedeEntity se left join SalaEntity sa on sa.idSede = se.idSede "
                            + "left join SesionEntity s on s.idSala = sa.idSala "
                            + "left join EntradaEntity e on e.idSesion = s.idSesion and e.estado in ('PAGADA','USADA') "
                            + "group by se.idSede, se.nombre "
                            + "order by coalesce(sum(case when e.idEntrada is not null then s.precio end), 0) desc, se.nombre", Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            LOG.warning("ingresosPorSede: " + ex);
            return List.of();
        }
    }

    // C25 del README
    public List<Object[]> informeDiario() {

        try (var em = emf.createEntityManager()) {
            return em.createQuery("select s.fecha, count(distinct case when s.estado = 'PROGRAMADA' then s.idSesion end), "
                            + "count(e.idEntrada), coalesce(sum(case when e.idEntrada is not null then s.precio end), 0) "
                            + "from SesionEntity s "
                            + "left join EntradaEntity e on e.idSesion = s.idSesion and e.estado in ('PAGADA','USADA') "
                            + "group by s.fecha order by s.fecha", Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            LOG.warning("informeDiario: " + ex);
            return List.of();
        }
    }

    // C29 del README
    public int venderEntrada(int idSesion, int idEspectador, String fechaCompra) {

        if (idSesion <= 0 || idEspectador <= 0 || fechaCompra == null || fechaCompra.isBlank()) return 0;

        try (var em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                em.createQuery("update SesionEntity s set s.estado = s.estado where s.idSesion = :id")
                        .setParameter("id", idSesion)
                        .executeUpdate();
                SesionEntity s = em.find(SesionEntity.class, idSesion);
                if (s == null || !"PROGRAMADA".equals(s.getEstado())) {
                    tx.rollback();
                    return 0;
                }
                EspectadorEntity es = em.find(EspectadorEntity.class, idEspectador);
                if (es == null || es.getActivo() != 1) {
                    tx.rollback();
                    return 0;
                }
                Long ya = em.createQuery("select count(e) from EntradaEntity e "
                                + "where e.idSesion = :sesion and e.idEspectador = :espectador", Long.class)
                        .setParameter("sesion", idSesion)
                        .setParameter("espectador", idEspectador)
                        .getSingleResult();
                if (ya > 0) {
                    tx.rollback();
                    return 0;
                }
                SalaEntity sa = em.find(SalaEntity.class, s.getIdSala());
                Long vendidas = em.createQuery("select count(e) from EntradaEntity e "
                                + "where e.idSesion = :sesion and e.estado in ('PAGADA','USADA')", Long.class)
                        .setParameter("sesion", idSesion)
                        .getSingleResult();
                if (vendidas >= sa.getAforo()) {
                    tx.rollback();
                    return 0;
                }
                EntradaEntity e = new EntradaEntity();
                e.setIdSesion(idSesion);
                e.setIdEspectador(idEspectador);
                e.setFechaCompra(fechaCompra);
                e.setEstado("PAGADA");
                em.persist(e);
                tx.commit();
                return 1;
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                LOG.warning("venderEntrada: " + ex);
                return 0;
            }
        }
    }

}
