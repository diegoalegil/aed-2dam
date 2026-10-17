# Anime tracker: Java 17 + SQLite + JDBC + JUnit 5

Proyecto de práctica con una tabla, `series`, para guardar las series de anime que sigues.

## Estructura

```
config/ConexionBD.java          conexión y creación de la tabla
dao/SerieDao.java               CRUD y consultas de la tabla series
validation/SerieValidator.java  validación de una serie (con una regex sencilla)
service/SerieService.java       reglas de negocio: usa el DAO y el validador
model/Serie.java                ya hecho
exception/AplicationException   ya hecho
sql/schema.sql                  sentencia de creación de la tabla
```

## Orden recomendado

1. `ConexionBD`: sin esto no funciona nada, porque **todos** los tests de DAO y de service lo usan.
2. `SerieDao`: primero el CRUD (`listar`, `buscarPorCodigo`, `insertar`, `actualizar`, `eliminar`).
3. `SerieDao`: después las consultas (`buscarPorTitulo`, `topPorNota`, `generosConAlMenos`).
4. `SerieValidator`.
5. `SerieService`.

## Reglas

- No cambies las firmas de los métodos públicos ni los nombres de las clases: los tests no compilarían.
- Lee el comentario de cada método: dice exactamente lo que se espera, incluidos órdenes y casos raros.
- Cada test crea su propia base en una carpeta temporal **que no existe al empezar**.

## Tests

```sh
mvn test
mvn -Dtest=ConexionBDTest test
```

33 tests: 5 de conexión, 12 de DAO, 10 del validador y 6 del service.
