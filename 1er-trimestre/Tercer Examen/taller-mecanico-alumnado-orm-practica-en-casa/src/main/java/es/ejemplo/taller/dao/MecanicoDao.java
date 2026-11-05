package es.ejemplo.taller.dao;

import es.ejemplo.taller.modelo.Mecanico;
import es.ejemplo.taller.orm.config.OrmFactory;
import es.ejemplo.taller.orm.entity.MecanicoEntity;
import es.ejemplo.taller.orm.entity.VehiculoEntity;
import es.ejemplo.taller.orm.mapper.MecanicoMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.sql.*;
import java.util.*;
import java.math.BigDecimal;

public class MecanicoDao {
    private final String url;
    private final EntityManagerFactory managerFactory;

    public MecanicoDao(String url) {
        this.url = url;
        this.managerFactory = OrmFactory.crear(url);
    }

    public List<Mecanico> listar() {

        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select m from MecanicoEntity m order by m.idMecanico",
                            MecanicoEntity.class)
                    .getResultStream()
                    .map(MecanicoMapper::toDomain)
                    .toList();

        }
    }

    public Optional<Mecanico> buscarPorId(Integer id) {

        if (id == null || id < 0) {
            return Optional.empty();
        }

        try (var em = managerFactory.createEntityManager()) {
            return Optional.ofNullable(em.find(MecanicoEntity.class, id))
                    .map(MecanicoMapper::toDomain);

        }
    }

    public int insertar(Mecanico dato) {

        if (dato == null) {
            return 0;
        }

        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                MecanicoEntity entidad = MecanicoMapper.toEntity(dato);
                em.persist(entidad);
                em.flush();
                dato.setIdMecanico(entidad.getIdMecanico());
                tx.commit();
                return dato.getIdMecanico();
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                return 0;
            }
        }
    }

    public int actualizar(Mecanico dato) {

        if (dato == null) {
            return 0;
        }

        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                MecanicoEntity m = em.find(MecanicoEntity.class, dato.getIdMecanico());
                if (m == null) {
                    tx.commit();
                    return 0;
                }
                m.setNombre(dato.getNombre());
                m.setEspecialidad(dato.getEspecialidad());
                m.setTarifaHora(dato.getTarifaHora());
                tx.commit();
                return 1;
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                return 0;
            }

        }
    }

    public int eliminar(Integer id) {

        if (id == null || id < 0) {
            return 0;
        }

        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                MecanicoEntity m = em.find(MecanicoEntity.class, id);
                if (m == null) {
                    tx.commit();
                    return 0;
                }
                em.remove(m);
                tx.commit();
                return 1;
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                return 0;
            }

        }
    }

    public List<Mecanico> buscarPorEspecialidad(String especialidad) {

        if (especialidad == null || especialidad.isEmpty()) {
            return new ArrayList<>();
        }
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select m from MecanicoEntity m where m.especialidad = :especialidad", MecanicoEntity.class)
                    .setParameter("especialidad", especialidad)
                    .getResultStream()
                    .map(MecanicoMapper::toDomain)
                    .toList();


        }
    }
}
