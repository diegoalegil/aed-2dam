package es.ejemplo.autoescuela.dao;

import es.ejemplo.autoescuela.modelo.Alumno;
import es.ejemplo.autoescuela.orm.config.OrmFactory;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;

public class AlumnoDao {

    public AlumnoDao(String jdbcUrl) {  }

    // Usar transacciones y rollback en operaciones de escritura.
    public List<Alumno> listar() {
        // TODO: implementar conforme al contrato del README.md.
        return List.of();
    }
    public Optional<Alumno> buscarPorId(int id) {
        // TODO: implementar conforme al contrato del README.md.
        return Optional.empty();
    }
    public int insertar(Alumno dato) {
        // TODO: implementar conforme al contrato del README.md.
        return 0;
    }
    public int actualizar(Alumno dato) {
        // TODO: implementar conforme al contrato del README.md.
        return 0;
    }
    public int eliminar(int id) {
        // TODO: implementar conforme al contrato del README.md.
        return 0;
    }
    public Optional<Alumno> buscarPorEmail(String email) {
        // TODO: implementar conforme al contrato del README.md.
        return Optional.empty();
    }
    public List<Alumno> listarActivos() {
        // TODO: implementar conforme al contrato del README.md.
        return List.of();
    }
    public List<Alumno> buscarPorNombre(String texto) {
        // TODO: implementar conforme al contrato del README.md.
        return List.of();
    }
    public List<Object[]> alumnosConTutor() {
        // TODO: implementar conforme al contrato del README.md.
        return List.of();
    }
    public List<Alumno> alumnosSinClases() {
        // TODO: implementar conforme al contrato del README.md.
        return List.of();
    }
    public List<Object[]> alumnosConRealizadas(int minimo) {
        // TODO: implementar conforme al contrato del README.md.
        return List.of();
    }
    public List<Alumno> listosParaPractico() {
        // TODO: implementar conforme al contrato del README.md.
        return List.of();
    }
    public List<Object[]> masClasesQueLaMedia() {
        // TODO: implementar conforme al contrato del README.md.
        return List.of();
    }
    public List<Object[]> gastoPorAlumno() {
        // TODO: implementar conforme al contrato del README.md.
        return List.of();
    }
    public List<Object[]> gastoMayorQue(double minimo) {
        // TODO: implementar conforme al contrato del README.md.
        return List.of();
    }
    public int darDeBaja(int idAlumno) {
        // TODO: implementar conforme al contrato del README.md.
        return 0;
    }
    
}
