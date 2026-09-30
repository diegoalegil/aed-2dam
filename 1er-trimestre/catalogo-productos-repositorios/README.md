# Catálogo de productos con repositorios

Proyecto Maven limpio para realizar el ejercicio.

## Paquetes preparados

- `com.ejemplo.catalogo.model`
- `com.ejemplo.catalogo.repository`
- `com.ejemplo.catalogo.repository.file`
- `com.ejemplo.catalogo.repository.file.csv`

No se incluye ninguna clase Java resuelta.

## Estado

En curso. `CsvRepository` carga el CSV en la lista `productos` al crearse y `findAll()` devuelve esa lista.

Todavía no compila:

- El constructor llama a `load()`, pero el método se llama `find()`.
- En `delete()` hay que cambiar `Productop ->` por `p ->` y añadir el `return`.
- `saveAll` todavía no existe.

## Para practicar

Próxima clase: lunes 28/09/2026.

- [ ] Hacer `saveAll` para escribir la lista `productos` en el CSV.
- [ ] Completar `findById`, `create`, `update` y `delete` usando la lista cargada y `saveAll`.
- [ ] Refactorizar `CsvRepository`.
- [ ] Usar clases abstractas: sacar lo común de los repositorios de ficheros a una clase abstracta y dejar en `CsvRepository` solo lo del CSV.
