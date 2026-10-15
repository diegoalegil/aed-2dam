# Repaso de SQLite

Actividad de repaso para el segundo examen: 30 consultas sobre una base de datos SQLite con dos tablas, `Productos(id, nombre, precio)` y `Vehiculos(matricula, marca, modelo, anio)`.

## Qué hay en la carpeta

- `ENUNCIADO.md`: los 30 ejercicios con el resultado que tiene que salir en cada uno.
- `respuestas_alumno.sql`: mis respuestas.
- `corregir.py`: el autocorrector. Compara el resultado de cada consulta con el esperado (columnas, filas y orden).
- `codelearn_modelos.sqlite3`: la base de datos. `crear_base_de_datos.sql` la vuelve a crear desde cero.

Para corregir:

```bash
python3 corregir.py respuestas_alumno.sql
```

Para probar consultas a mano:

```bash
sqlite3 -header -column codelearn_modelos.sqlite3
```

## Cómo voy

20/30 (ejercicios 01-20).

## Mis errores

Errores reales que me salieron en la terminal y la consulta con la que me quedé. No están todos los intentos, solo los que me enseñaron algo. El 01, el 10 y el 12 me salieron a la primera.

### 02. Los cinco vehículos más recientes

```sql
SELECT matricula, marca, modelo, anio FROM Vehiculo ORDER BY DESC LIMIT 5;
-- Parse error: no such table: Vehiculo

SELECT matricula, marca, modelo, anio FROM Vehiculos ORDER BY DESC LIMIT 5;
-- Parse error: no such column: DESC

SELECT matricula, marca, modelo, anio FROM Vehiculos ORDER BY matricula DESC LIMIT 5;
-- salían Hondas de 2016, 2009... no eran los más recientes
```

Ordené por matrícula porque en España las matrículas más altas suelen ser coches más nuevos, pero aquí los datos son inventados. Lo reciente lo dice `anio`.

```sql
SELECT matricula, marca, modelo, anio FROM Vehiculos ORDER BY anio DESC LIMIT 5;
```

### 03. Productos con precio mayor de 100

```sql
SELECT id, nombre, precio FROM Productos WHERE precio > 100;;
-- error SQL: You can only execute one statement at a time.

SELECT id, nombre, precio FROM Productos WHERE precio > 100 LIMIT 8;
-- el resultado no coincide (obtenidas 8 filas, esperadas 8)
```

Hay 67 productos de más de 100 y la tabla solo enseña 8: los 8 más caros.

```sql
SELECT id, nombre, precio FROM Productos WHERE precio > 100 ORDER BY precio DESC LIMIT 8;
```

### 04. Vehículos de la marca Toyota

```sql
SELECT matricula, marca, modelo, anio FROM Vehiculos LIMIT marca 'Toyota';
-- Parse error: near "'Toyota'": syntax error

SELECT matricula, marca, modelo, anio FROM Vehiculos LIMIT marca ON 'Toyota';
-- Parse error: near "ON": syntax error
```

`LIMIT` solo lleva un número. Para filtrar es `WHERE`.

```sql
SELECT matricula, marca, modelo, anio FROM Vehiculos WHERE marca = 'Toyota' ORDER BY anio DESC LIMIT 8;
```

### 05. Productos cuyo nombre contenga Pro

```sql
SELECT id, nombre, precio FROM Productos WHERE nombre ILIKE %pro%;
-- Parse error: near "ILIKE": syntax error

SELECT id, nombre, precio FROM Productos WHERE nombre ILIKE '%Pro%';
-- Parse error: near "ILIKE": syntax error
```

`ILIKE` es de PostgreSQL (lo usábamos en 1º); en SQLite no existe y `LIKE` ya ignora las mayúsculas. Me lo aprobó sin `ORDER BY`, aunque sin `ORDER BY` el orden no está garantizado.

```sql
SELECT id, nombre, precio FROM Productos WHERE nombre LIKE '%Pro%' LIMIT 8;
```

### 06. Productos con precio entre 20 y 50

```sql
SELECT id, nombre, precio FROM Productos WHERE precio BETWEEN 20 AND 50 LIMIT 8;
-- salían 8 de los 10, pero se colaba el Ratón Compacto (48.76)
```

En la tabla los precios van subiendo: son los 8 más baratos del rango.

```sql
SELECT id, nombre, precio FROM Productos WHERE precio BETWEEN 20 AND 50 ORDER BY precio LIMIT 8;
```

### 07. Marcas sin repetir

```sql
SELECT matricula, marca, modelo, anio FROM Vehiculos DISTINC marcas;
-- Parse error: near "marcas": syntax error

SELECT matricula, marca, modelo, anio FROM Vehiculos DISTINCT marcas;
-- Parse error: near "DISTINCT": syntax error
```

`DISTINCT` va pegado al `SELECT` y solo con la columna que no quiero repetida (`marca`, sin s).

```sql
SELECT DISTINCT marca FROM Vehiculos ORDER BY marca;
```

### 08. Vehículos de dos marcas

```sql
SELECT matricula, marca, modelo, anio FROM Vehiculos WHERE marca IN 'Ford';
-- Parse error: no such table: Ford

SELECT matricula, marca, modelo, anio FROM Vehiculos WHERE marca IN ('Ford', 'Toyota') ORDER BY matricula LIMIT 8;
-- salían 8 Toyota: sus matrículas empiezan por 1000 y las de Ford por 1030

SELECT matricula, marca, modelo, anio FROM Vehiculos WHERE marca IN ('Ford', 'Toyota') ORDER BY anio DESC LIMIT 8;
-- se mezclaban Toyota y Ford
```

La lista del `IN` va entre paréntesis. Primero ordeno por marca (Ford va antes que Toyota) y dentro de cada marca por año.

```sql
SELECT matricula, marca, modelo, anio FROM Vehiculos WHERE marca IN ('Ford', 'Toyota') ORDER BY marca, anio DESC LIMIT 8;
```

### 09. Contar productos

```sql
SELECT id, nombre, precio AS 'total_productos' FROM Productos COUNT(*);
-- Parse error: near "(": syntax error

SELECT id, nombre, precio AS 'total_productos' FROM Producto;
-- Parse error: no such table: Producto

SELECT COUNT(*) AS 'total_productos' FROM Productos COUNT(*);
-- Parse error: near "(": syntax error
```

`COUNT(*)` va solo en el `SELECT`.

```sql
SELECT COUNT(*) AS 'total_productos' FROM Productos;
```

### 11. Precio mínimo, máximo y medio

```sql
SELECT MIN(precio) AS minimo, MAX(precio) AS maximo, ROUND(AVG(precio,2)) AS media FROM Productos;
-- Parse error: wrong number of arguments to function AVG()

SELECT MIN(precio) AS minimo, MAX(precio) AS maximo, ROUND(AVG(precio,2) AS media FROM Productos;
-- Parse error: near "AS": syntax error
```

El `2` es de `ROUND`, no de `AVG`: `ROUND( AVG(precio) , 2)`.

```sql
SELECT MIN(precio) AS minimo, MAX(precio) AS maximo, ROUND(AVG(precio),2) AS media FROM Productos;
```

### 13. Suma de todos los precios

```sql
SELECT RODUN(SUM(precio),2) FROM Productos;
-- Parse error: no such function: RODUN
```

```sql
SELECT ROUND(SUM(precio),2) FROM Productos;
```

### 14. Productos con precio de al menos 50

Primero puse `precio > 50` y daba lo mismo porque ningún producto cuesta 50 justos, pero "al menos 50" es `>= 50`.

```sql
SELECT COUNT(*) FROM Productos WHERE precio >= 50;
```

### 15. Vehículos de cada marca

```sql
SELECT marca, COUNT(marca) FROM Vehiculos;
-- Toyota | 100   (una sola fila: sin GROUP BY cuenta los 100 juntos)
```

```sql
SELECT marca, COUNT(marca) AS cantidad FROM Vehiculos GROUP BY marca ORDER BY marca ;
```

### 16. Años con más vehículos

```sql
SELECT anio, COUNT(marca) AS cantidad FROM Vehiculos, GROUP BY anio ORDER BY anio DESC;
-- Parse error: near "GROUP": syntax error

SELECT anio, COUNT(*) AS cantidad FROM Vehiculos GROUP BY anio ORDER BY anio DESC LIMIT 8;
-- se colaban 2023, 2022... que solo tienen 4 coches
```

Sobraba la coma después de `Vehiculos`. "Con más vehículos" es ordenar por la cantidad, y como hay 12 años empatados con 5, hace falta desempatar por año.

```sql
SELECT anio, COUNT(*) AS cantidad FROM Vehiculos GROUP BY anio ORDER BY cantidad DESC, anio DESC LIMIT 10;
```

### 17. Año más antiguo y más reciente por marca

```sql
SELECT marca, MIN(anio) AS anio_minimo, MAX(anio) AS anio_max FROM Vehiculos ORDER BY marca;
-- Toyota | 2005 | 2026   (otra vez una sola fila: faltaba el GROUP BY)
```

```sql
SELECT marca, MIN(anio) AS anio_minimo, MAX(anio) AS anio_max FROM Vehiculos GROUP BY marca ORDER BY marca;
```

### 18. Productos por tramos de precio

Este me costó: era la primera vez que veía `CASE` (el `if / else if / else` de SQL). Se agrupa por la columna inventada `tramo`, y como el orden alfabético no sirve ("25..." va antes que "Menos..."), cada tramo se ordena por su producto más barato.

```sql
SELECT CASE WHEN precio < 25 THEN 'Menos de 25' WHEN precio < 75 THEN '25 a menos de 75' ELSE '75 o más' END AS tramo, COUNT(*) AS cantidad FROM Productos GROUP BY tramo ORDER BY MIN(precio);
```

### 19. Marcas con al menos cinco vehículos

```sql
SELECT marca, COUNT(*) AS cantidad FROM Vehiculos GROUP BY cantidad HAVING > 5;
-- Parse error: near ">": syntax error

SELECT marca, COUNT(*) AS cantidad FROM Vehiculos GROUP BY cantidad HAVING 5;
-- Parse error: aggregate functions are not allowed in the GROUP BY clause

SELECT marca, COUNT(*) AS cantidad FROM Vehiculos HAVING cantidad > 5 GROUP BY marca;
-- Parse error: near "GROUP": syntax error
```

Primero se hacen los grupos (`GROUP BY marca`) y después se filtran (`HAVING` con la condición entera).

```sql
SELECT marca, COUNT(*) AS cantidad FROM Vehiculos GROUP BY marca HAVING cantidad >= 5;
```

### 20. Años con al menos tres vehículos

```sql
SELECT anios, COUNT(*) AS cantidad FROM Vehiculos GROUP BY anio HAVING cantidad >= 3;
-- Parse error: no such column: anios

SELECT anio, COUNT(*) AS cantidad FROM Vehiculos GROUP BY anio HAVING cantidad >= 3 ORDER BY anio DESC LIMIT 8;
-- se colaban 2023, 2022... con 4 coches

SELECT anio, COUNT(*) AS cantidad FROM Vehiculos GROUP BY anio HAVING cantidad >= 3 ORDER BY cantidad DESC LIMIT 8;
-- 8 filas, y la tabla tiene 10
```

Es el 16 con un `HAVING`: 10 filas y desempate por año.

```sql
SELECT anio, COUNT(*) AS cantidad FROM Vehiculos GROUP BY anio HAVING cantidad >= 3 ORDER BY cantidad DESC, anio DESC LIMIT 10;
```

## Lo que me llevo

- El enunciado dice qué filtrar; la tabla del resultado dice cuántas filas (`LIMIT`) y en qué orden (`ORDER BY`).
- Para saber el orden busco la columna que nunca cambia de dirección. Si baja, `DESC`.
- "Los N más ___" = `ORDER BY ___ DESC LIMIT N`. "Al menos" = `>=`.
- Orden de las partes: `SELECT → FROM → WHERE → GROUP BY → HAVING → ORDER BY → LIMIT`.
- Una sola fila con una marca cualquiera = falta el `GROUP BY`.
- Se agrupa por la columna que va al lado del `COUNT` en la tabla.
- `WHERE` filtra filas; `HAVING` filtra grupos.
- Si hay empates, hay que desempatar con otra columna.
- `no such table/column/function` = nombre mal escrito. `near "X": syntax error` = algo mal colocado justo antes de X.
