package ies.puerto;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.lang.System.in;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class LectorRecursos {
    static void main() {
        //buscar el fichero
        Path path = Path.of("resources", "csv.txt"); // no hace nada de existir, simplemente te crea una ruta
        String ruta = path.toAbsolutePath().toString();

        System.out.println("Ruta al fichero" + ruta);

        File file = new File(ruta); // File verifica si existe
        if (file.exists()) {
            System.out.println("Fichero existe");
        } else {
            System.out.println("El fichero no exite");
        }
        // Pathe s un puntero, pero File es el que te dice si existe la ruta

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String linea = reader.readLine();
            String[] valor = linea.split(",");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Reader in = null;
        try {
             Reader in = new FileReader(path.toAbsolutePath().toString());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader(HEADERS) // cuando construye la lista : olvida la priemra linea
                .setSkipHeaderRecord(true)
                .build();
        Iterable<CSVRecord> records = csvFormat.parse(in);
//================================================================================================
        //URL url = LectorRecursos.class.getClassLoader().getResource("archivos.txt");
        //System.out.println("Path dentro de Resources"+ url.getPath().toString());
        // Files.readAllLines() - puedes añadir un path, und file o url (preferencia profe file)
    }
}

