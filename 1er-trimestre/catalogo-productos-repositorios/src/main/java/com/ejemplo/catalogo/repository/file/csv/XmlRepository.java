package com.ejemplo.catalogo.repository.file.csv;

import com.ejemplo.catalogo.model.Producto;
import com.ejemplo.catalogo.model.ProductosXml;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class XmlRepository extends AbstractRepository{
    private final XmlMapper mapper;
    public XmlRepository(Path path) {
        super(path);
        mapper = new XmlMapper();
        productos = load();
    }

    @Override
    public void saveAll(List<Producto> items) {
        Path temporal = null;
        try {
            ProductosXml productosXml = new ProductosXml();
            productosXml.setProductos(productos);
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(getPath().toFile(), productosXml);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo guardar " + getPath(), e);
        } finally {
        }
    }

    @Override
    public List<Producto> load() {

        try {
            ProductosXml data = mapper.readValue(path.toFile(), ProductosXml.class);
            productos.clear();
            productos.addAll(data.getProductos());
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo cargar " + getPath(), e);
        }
        return productos;
    }
}
