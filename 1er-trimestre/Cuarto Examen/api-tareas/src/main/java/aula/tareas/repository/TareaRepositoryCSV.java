package aula.tareas.repository;

import aula.tareas.modelo.Tarea;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class TareaRepositoryCSV implements ITareaRepository {
    private final AtomicLong secuencia = new AtomicLong();
    private final ConcurrentSkipListMap<Long, Tarea> tareas = new ConcurrentSkipListMap<>();
    private final Path path;
    private final CSVFormat inputFormat = CSVFormat.DEFAULT.builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .build();
    private final CSVFormat outputFormat = CSVFormat.DEFAULT.builder()
            .setHeader("id", "titulo")
            .build();

    public TareaRepositoryCSV() {
        path = Path.of("data", "tareas.csv");
    }

    @Override
    public Tarea create(String titulo) {
        long id = secuencia.incrementAndGet();
        var tarea = new Tarea(id, titulo.strip());
        tareas.put(id, tarea);
        return tarea;
    }

    @Override
    public boolean delete(Long identificador) {
        return tareas.remove(identificador) != null; //No se si el != de null esta bien
    }

    @Override
    public List<Tarea> findAll() {
        if (Files.notExists(path)) {
            return new ArrayList<>();
        }
        List<Tarea> result = new ArrayList<>();
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
             CSVParser parser = inputFormat.parse(reader)) {
            for (CSVRecord row : parser) {
                result.add(new Tarea(
                        Long.parseLong(row.get("id")),
                        row.get("titulo")));
            }
        } catch (IOException e) {
            return List.of();
        }
        return result;
    }

    @Override
    public Tarea findById(Long identificador) {
        return tareas.get(identificador);
    }

    @Override
    public Tarea update(Tarea tarea) {

        return null;
    }


}
