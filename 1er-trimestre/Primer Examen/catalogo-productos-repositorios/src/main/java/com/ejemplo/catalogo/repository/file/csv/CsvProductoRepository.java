package com.ejemplo.catalogo.repository.file.csv;

import com.ejemplo.catalogo.model.Producto;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CsvProductoRepository extends AbstractFileRepository<Producto, Long> implements IProductoRepository {

    private final CSVFormat inputFormat = CSVFormat.DEFAULT.builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .get();

    private final CSVFormat outputFormat = CSVFormat.DEFAULT.builder()
            .setHeader("id", "nombre", "precio")
            .get();

    public CsvProductoRepository(Path path) {
        super(path);
        list = readAll();
    }

    @Override
    protected List<Producto> readAll() {
        /**try (Reader reader = Files.newBufferedReader(getPath(), StandardCharsets.UTF_8)) {

         } catch (IOException e) {
         throw new RuntimeException(e);
         }**/

        // Versión a mano
        List<Producto> productos = new ArrayList<>();
        try {

            List<String> lineas = Files.readAllLines(getPath(), StandardCharsets.UTF_8);


            for (int i = 1; i < lineas.size(); i++) {

                String[] partes = lineas.get(i).split(",");


                Long id = Long.parseLong(partes[0]);
                String nombre = partes[1];
                double precio = Double.parseDouble(partes[2]);


                productos.add(new Producto(id, nombre, precio));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return productos;
    }

    @Override
    protected void writeAll(List<Producto> elements) {
        /*try (Writer writer = Files.newBufferedWriter(getPath(), StandardCharsets.UTF_8);
             CSVPrinter printer = new CSVPrinter(writer, outputFormat)) {
            for (Producto p : elements) {
                printer.printRecord(p.id(), p.nombre(), p.precio());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/

        // Versión a mano
        List<String> lineas = new ArrayList<>();
        lineas.add("id,nombre,precio");


        for (Producto p : elements) {
            lineas.add(p.id() + "," + p.nombre() + "," + p.precio());
        }

        )
        try {
            Files.write(getPath(), lineas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

