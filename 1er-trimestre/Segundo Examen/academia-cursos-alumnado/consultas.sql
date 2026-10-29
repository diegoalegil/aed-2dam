-- Consultas para practicar en SQLite antes de pasarlas a Java (no puntúan).
-- Ejecútalas sobre datos/academia.sqlite3 (IntelliJ: pestaña Database) y comprueba el resultado.
-- Donde pone 1, 'ana', 'BASICO' o 120.50, en Java irá una ?.

-- 1. Todos los alumnos, ordenados por id.

-- 2. Alumnos cuyo nombre contiene 'ana' sin distinguir mayúsculas de minúsculas, ordenados por nombre y, en empate, por id.
--    (Con los datos de ejemplo salen Ana Torres y Mariana López.)

-- 3. Cursos de nivel 'BASICO', del más barato al más caro y, con el mismo precio, por id.

-- 4. Cursos con precio menor o igual que 120.50 (incluido), del más caro al más barato y, con el mismo precio, por id.

-- 5. Cursos en los que está matriculado el alumno 1, ordenados por título (JOIN de cursos con matriculas).

-- 6. Matrículas del alumno 1, ordenadas por fecha y, con la misma fecha, por id.

-- 7. Total pagado por el alumno 1 (suma de importes). Pruébala también con el alumno 5, que no tiene matrículas: tiene que dar 0, no NULL.

-- 8. Título de cada curso y número de alumnos matriculados, incluidos los cursos sin matrículas (con 0), ordenados por id del curso.
