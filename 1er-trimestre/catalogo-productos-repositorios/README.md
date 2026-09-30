# Catálogo de productos con repositorios

Proyecto Maven limpio para realizar el ejercicio.

## Paquetes preparados

- `com.ejemplo.catalogo.model`
- `com.ejemplo.catalogo.repository`
- `com.ejemplo.catalogo.repository.file`
- `com.ejemplo.catalogo.repository.file.csv`

## Estado

Versión de la clase del 30/09/2026:

- `IRepository`: interfaz con las operaciones CRUD.
- `AbstractRepository`: clase abstracta con lo común (ruta del fichero, lista `productos`, `findAll`, `findById`, `create`, `update` y `delete`). Cada formato implementa `load()` y `saveAll()`.
- `CsvRepository`: lee y escribe `data/productos.csv` con Apache Commons CSV.
- `JsonRepository`: lee y escribe `data/productos.json` con Jackson.
- `XmlRepository`: lee y escribe XML con Jackson XML usando la clase envolvente `ProductosXml` (raíz `<productos>` con un `<producto>` por elemento).
- `AppConfiguration`: lee y escribe `data/app.propierties` con `Properties` (`storage.format` y `storage.path`).

## Para practicar

- [x] Hacer `saveAll` para escribir la lista `productos` en el fichero.
- [x] Completar `findById`, `create`, `update` y `delete` usando la lista cargada y `saveAll`.
- [x] Refactorizar `CsvRepository`.
- [x] Usar clases abstractas: `AbstractRepository` con lo común y `CsvRepository` y `JsonRepository` con lo de cada formato.
- [ ] Mini proyecto: arrancar la aplicación eligiendo el repositorio (CSV, JSON o XML) según `storage.format` y `storage.path` de `app.propierties`.
