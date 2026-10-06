# Catálogo de productos con repositorios

Proyecto Maven limpio para realizar el ejercicio.

## Paquetes preparados

- `com.ejemplo.catalogo.model`
- `com.ejemplo.catalogo.repository`
- `com.ejemplo.catalogo.repository.file`
- `com.ejemplo.catalogo.repository.file.csv`
- `com.ejemplo.catalogo.repository.database`
- `com.ejemplo.catalogo.configuration`

## Estado

Versión del 06/10/2026:

- `IRepository<T, ID>`: interfaz genérica con las operaciones CRUD.
- `IProductoRepository`: `IRepository<Producto, Long>` para productos.
- `Identificable<ID>`: interfaz con `id()`. La implementa `Producto`, que tiene `Long id`.
- `AbstractFileRepository<T, ID>`: clase abstracta genérica con lo común (ruta del fichero, lista `list`, `findAll`, `findById`, `create`, `update` y `delete`). Cada formato implementa `readAll()` y `writeAll()`. Los ids se comparan con `equals`.
- `AbstractProductoRepository`: `AbstractFileRepository<Producto, Long>` para productos.
- `CsvProductoRepository`: lee y escribe `data/productos.csv` a mano, con `split` y `Files.write`. La versión con Apache Commons CSV está comentada para hacerla después.
- `JsonProductoRepository` y `XmlProductoRepository`: adaptados a `readAll()` y `writeAll()`. Si el fichero está vacío, no lo leen.
- `AppConfiguration`: lee y escribe `data/app.properties` y crea el repositorio de CSV, JSON o XML según `storage.format`, con la ruta de `storage.path`.
- SQLite: base de datos `data/app.db` con la tabla `producto`, dependencia `sqlite-jdbc` y `DatabaseInitializer` empezado.

## Para practicar

- [x] Hacer `saveAll` para escribir la lista `productos` en el fichero.
- [x] Completar `findById`, `create`, `update` y `delete` usando la lista cargada y `saveAll`.
- [x] Refactorizar `CsvRepository`.
- [x] Usar clases abstractas: `AbstractRepository` con lo común y `CsvRepository` y `JsonRepository` con lo de cada formato.
- [x] Generalizar la interfaz y la clase abstracta con genéricos (`IRepository<T, ID>` y `AbstractFileRepository<T, ID>`).
- [x] Adaptar `JsonProductoRepository` y `XmlProductoRepository` a `readAll()` y `writeAll()`.
- [x] Mini proyecto: arrancar la aplicación eligiendo el repositorio (CSV, JSON o XML) según `storage.format` y `storage.path` de `app.properties`.
- [ ] Hacer `readAll()` y `writeAll()` del CSV con Apache Commons CSV (`CSVParser` y `CSVPrinter`).
- [ ] Terminar `DatabaseInitializer`: comprobar si existe la base de datos, conectar, operar y cerrar siempre la conexión.
