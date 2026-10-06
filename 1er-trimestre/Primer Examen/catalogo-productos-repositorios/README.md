# Catálogo de productos con repositorios

Proyecto Maven limpio para realizar el ejercicio.

## Paquetes preparados

- `com.ejemplo.catalogo.model`
- `com.ejemplo.catalogo.repository`
- `com.ejemplo.catalogo.repository.file`
- `com.ejemplo.catalogo.repository.file.csv`

## Estado

Versión de la clase del 01/10/2026 (refactorización a genéricos, a medias):

- `IRepository<T, ID>`: interfaz genérica con las operaciones CRUD.
- `IProductoRepository`: `IRepository<Producto, Long>` para productos.
- `Identificable<ID>`: interfaz con `id()`. La implementa `Producto`, que ahora tiene `Long id`.
- `AbstractFileRepository<T, ID>`: clase abstracta genérica con lo común (ruta del fichero, lista `list`, `findAll`, `findById`, `create`, `update` y `delete`). Cada formato implementa `readAll()` y `writeAll()`.
- `AbstractProductoRepository`: `AbstractFileRepository<Producto, Long>` para productos.
- `CsvProductoRepository`: lee y escribe `data/productos.csv` con Apache Commons CSV, ya con `readAll()` y `writeAll()`.
- `JsonProductoRepository` y `XmlProductoRepository`: renombrados, pero todavía usan `load()`, `saveAll()` y `productos`, así que el proyecto aún no compila.
- `AppConfiguration`: lee y escribe `data/app.properties` (antes `app.propierties`) y, si `storage.format` es `json`, crea un `JsonProductoRepository` con la ruta de `storage.path`.

## Para practicar

- [x] Hacer `saveAll` para escribir la lista `productos` en el fichero.
- [x] Completar `findById`, `create`, `update` y `delete` usando la lista cargada y `saveAll`.
- [x] Refactorizar `CsvRepository`.
- [x] Usar clases abstractas: `AbstractRepository` con lo común y `CsvRepository` y `JsonRepository` con lo de cada formato.
- [x] Generalizar la interfaz y la clase abstracta con genéricos (`IRepository<T, ID>` y `AbstractFileRepository<T, ID>`).
- [ ] Adaptar `JsonProductoRepository` y `XmlProductoRepository` a `readAll()` y `writeAll()`.
- [ ] Mini proyecto: arrancar la aplicación eligiendo el repositorio (CSV, JSON o XML) según `storage.format` y `storage.path` de `app.properties`. De momento solo está el caso JSON.
