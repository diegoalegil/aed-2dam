package com.ejemplo.catalogo.repository.file.csv;

import com.ejemplo.catalogo.model.Producto;
import com.ejemplo.catalogo.model.ProductosXml;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class XmlProductoRepository extends AbstractProductoRepository {
    private final XmlMapper mapper;
    public XmlProductoRepository(Path path) {
        super(path);
        mapper = new XmlMapper();
        list = readAll();
    }

    @Override
    protected void writeAll(List<Producto> elements) {
        try {
            ProductosXml productosXml = new ProductosXml();
            productosXml.setProductos(elements);
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(getPath().toFile(), productosXml);

        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo guardar " + getPath(), e);
        }
    }

    @Override
    protected List<Producto> readAll() {

        try {
            if (Files.size(getPath()) == 0) {
                return list;
            }
            ProductosXml productosXml = mapper.readValue(getPath().toFile(), ProductosXml.class);
            list.clear();
            list.addAll(productosXml.getProductos());
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo cargar " + getPath(), e);
        }
        return list;
    }
}
