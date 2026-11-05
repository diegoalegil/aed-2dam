package es.ejemplo.taller.dao;

import es.ejemplo.taller.modelo.Vehiculo;
import es.ejemplo.taller.orm.config.OrmFactory;
import es.ejemplo.taller.orm.entity.VehiculoEntity;
import es.ejemplo.taller.orm.mapper.VehiculoMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.sql.*;
import java.util.*;
import java.math.BigDecimal;

public class VehiculoDao {
    private final String url;
    private final EntityManagerFactory managerFactory;

    public VehiculoDao(String url) {
        this.url = url;
        this.managerFactory = OrmFactory.crear(url);
    }

    public List<Vehiculo> listar() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select v from VehiculoEntity v order by v.matricula",
                            VehiculoEntity.class)
                    .getResultStream()
                    .map(VehiculoMapper::toDomain)
                    .toList();
        }
    }

    public Optional<Vehiculo> buscarPorId(String id) {

        if (id == null || id.isEmpty()) {
            return Optional.empty();
        }
        try (var em = managerFactory.createEntityManager()) {
            return Optional.ofNullable(em.find(VehiculoEntity.class, id))
                    .map(VehiculoMapper::toDomain);

        }
    }

    public int insertar(Vehiculo dato) {

        if (dato == null || dato.getMatricula() == null || dato.getMatricula().isBlank()) {
            return 0;
        }

        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                em.persist(VehiculoMapper.toEntity(dato));
                tx.commit();
                return 1;
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                return 0;
            }
        }
    }

    public int actualizar(Vehiculo dato) {

        if (dato == null) {
            return 0;
        }

        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                VehiculoEntity v = em.find(VehiculoEntity.class, dato.getMatricula());
                if (v == null) {
                    tx.commit();
                    return 0;
                }
                v.setMarca(dato.getMarca());
                v.setModelo(dato.getModelo());
                v.setAnio(dato.getAnio());
                v.setIdCliente(dato.getIdCliente());
                tx.commit();
                return 1;
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                return 0;
            }
        }
    }

    public int eliminar(String id) {

        if (id == null || id.isEmpty()) {
            return 0;
        }

        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                VehiculoEntity v = em.find(VehiculoEntity.class, id);
                if (v == null) {
                    tx.commit();
                    return 0;
                }
                em.remove(v);
                tx.commit();
                return 1;
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                return 0;
            }
        }
    }


    public List<Vehiculo> buscarPorCliente(Integer id) {

        if (id == null || id < 0) {
            return new ArrayList<>();
        }

        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("SELECT v FROM VehiculoEntity v WHERE v.idCliente = :id",
                            VehiculoEntity.class)
                    .setParameter("idCliente", id)
                    .getResultStream()
                    .map(VehiculoMapper::toDomain)
                    .toList();

        }
    }

    public List<Vehiculo> listarPorMarca(String marca) {

        if (marca == null || marca.isBlank()) {
            return new ArrayList<>();
        }

        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select v from VehiculoEntity v where v.marca =:marca", VehiculoEntity.class)
                    .setParameter("marca", marca)
                    .getResultStream()
                    .map(VehiculoMapper::toDomain)
                    .toList();
        }
    }
}