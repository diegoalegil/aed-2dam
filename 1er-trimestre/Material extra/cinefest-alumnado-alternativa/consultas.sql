-- CINEFEST. Completa C01 a C30. No se entregan las soluciones.
-- Alias usados: s sesiones, p peliculas, sa salas, se sedes, es espectadores, e entradas, v valoraciones.
-- Entradas «válidas» = estado PAGADA o USADA (las CANCELADA no cuentan ni ocupan plaza).
-- Regla de oro: si el enunciado dice «incluso/incluyendo sin ...», LEFT JOIN y COUNT(columna de la tabla de la derecha), no COUNT(*).

-- C01: Películas ordenadas alfabéticamente
-- README -> patrón: orden alfabético -> ORDER BY titulo; sin JOIN ni WHERE, solo una tabla
SELECT titulo, genero, anio
FROM peliculas
ORDER BY titulo;

-- C02: Espectadores activos por antigüedad
-- README -> patrón: «activos» -> WHERE activo = 1 (1 es activo, 0 no); «por antigüedad» -> ORDER BY fecha_alta y desempate por nombre
SELECT nombre, fecha_alta
FROM espectadores
WHERE activo = 1
ORDER BY fecha_alta, nombre;

-- C03: Películas de un género
-- README -> patrón: «de un género» -> WHERE genero = ? (en el DAO sería un parámetro, aquí va el valor fijo 'Drama')
SELECT titulo, pais, anio
FROM peliculas
WHERE genero = 'Drama'
ORDER BY titulo;

-- C04: Sesiones entre dos fechas
-- README -> patrón: «entre dos fechas» -> BETWEEN, que incluye las dos fechas; las fechas son texto 'AAAA-MM-DD' y se ordenan bien como texto
SELECT fecha, hora, id_sesion, precio
FROM sesiones
WHERE fecha BETWEEN '2026-11-13' AND '2026-11-14'
ORDER BY fecha, hora, id_sesion;

-- C05: Géneros distintos
-- README -> patrón: «distintos» -> SELECT DISTINCT, quita los repetidos
SELECT DISTINCT genero
FROM peliculas
ORDER BY genero;

-- C06: Salas con su sede
-- README -> patrón: «con su sede» -> JOIN salas con sedes por la FK id_sede; alias sa y se para no confundir las dos tablas
SELECT se.nombre AS sede, sa.nombre AS sala, sa.aforo
FROM salas sa
JOIN sedes se ON se.id_sede = sa.id_sede
ORDER BY sede, sala;

-- C07: Sesiones con película, sala y sede
-- README -> patrón: «película, sala y sede» -> tres JOIN seguidos: sesiones->peliculas, sesiones->salas, salas->sedes (la sesión no tiene sede propia)
SELECT s.id_sesion, p.titulo AS pelicula, se.nombre AS sede, sa.nombre AS sala, s.fecha, s.hora
FROM sesiones s
JOIN peliculas p ON p.id_pelicula = s.id_pelicula
JOIN salas sa ON sa.id_sala = s.id_sala
JOIN sedes se ON se.id_sede = sa.id_sede
ORDER BY s.id_sesion;

-- C08: Entradas con espectador y película
-- README -> patrón: «con espectador y película» -> JOIN entradas->espectadores y entradas->sesiones->peliculas (la entrada llega a la película pasando por la sesión)
SELECT e.id_entrada, es.nombre AS espectador, p.titulo AS pelicula, e.estado
FROM entradas e
JOIN espectadores es ON es.id_espectador = e.id_espectador
JOIN sesiones s ON s.id_sesion = e.id_sesion
JOIN peliculas p ON p.id_pelicula = s.id_pelicula
ORDER BY e.id_entrada;

-- C09: Espectadores que nunca compraron entradas
-- README -> patrón: «nunca compraron» -> NOT EXISTS con subconsulta correlacionada: espectadores sin ninguna fila en entradas (también valdría LEFT JOIN ... IS NULL)
SELECT es.nombre, es.email
FROM espectadores es
WHERE NOT EXISTS (SELECT 1 FROM entradas e WHERE e.id_espectador = es.id_espectador)
ORDER BY es.nombre;

-- C10: Valoraciones con datos de película y espectador
-- README -> patrón: «datos de película y espectador» -> JOIN valoraciones->peliculas y valoraciones->espectadores
SELECT p.titulo AS pelicula, es.nombre AS espectador, v.puntuacion
FROM valoraciones v
JOIN peliculas p ON p.id_pelicula = v.id_pelicula
JOIN espectadores es ON es.id_espectador = v.id_espectador
ORDER BY pelicula, espectador;

-- C11: Número de películas por género
-- README -> patrón: «número de ... por género» -> GROUP BY genero y COUNT(*); cada fila del resultado es un género
SELECT genero, COUNT(*) AS total_peliculas
FROM peliculas
GROUP BY genero
ORDER BY genero;

-- C12: Número de salas y aforo total por sede, incluso sin salas
-- README -> patrón: «incluso sin salas» -> LEFT JOIN y COUNT(sa.id_sala) (cuenta la columna de salas, no *, para que una sede sin salas dé 0); COALESCE pone 0 donde SUM daría NULL
SELECT se.nombre AS sede, COUNT(sa.id_sala) AS total_salas, COALESCE(SUM(sa.aforo), 0) AS aforo_total
FROM sedes se
LEFT JOIN salas sa ON sa.id_sede = se.id_sede
GROUP BY se.id_sede, se.nombre
ORDER BY sede;

-- C13: Ranking de espectadores por entradas no canceladas
-- README -> patrón: «ranking» -> ORDER BY total DESC; «no canceladas» -> estado IN ('PAGADA','USADA') dentro del ON del LEFT JOIN, no en un WHERE, para no perder espectadores sin entradas
SELECT es.nombre AS espectador, COUNT(e.id_entrada) AS total_entradas
FROM espectadores es
LEFT JOIN entradas e ON e.id_espectador = es.id_espectador AND e.estado IN ('PAGADA','USADA')
GROUP BY es.id_espectador, es.nombre
ORDER BY total_entradas DESC, espectador;

-- C14: Películas con al menos dos valoraciones y su media
-- README -> patrón: «al menos dos» -> HAVING COUNT(...) >= 2 (HAVING filtra grupos, WHERE filtra filas); «media» -> ROUND(AVG(...), 2); JOIN normal porque solo cuentan películas con valoraciones
SELECT p.titulo AS pelicula, COUNT(v.id_valoracion) AS numero_valoraciones, ROUND(AVG(v.puntuacion), 2) AS media
FROM peliculas p
JOIN valoraciones v ON v.id_pelicula = p.id_pelicula
GROUP BY p.id_pelicula, p.titulo
HAVING COUNT(v.id_valoracion) >= 2
ORDER BY media DESC, pelicula;

-- C15: Plazas vendidas y libres por sesión
-- README -> patrón: «vendidas y libres» -> libres = aforo - COUNT(entradas válidas); LEFT JOIN con el estado en el ON para que una sesión sin ventas salga con 0 vendidas
SELECT s.id_sesion, sa.aforo, COUNT(e.id_entrada) AS vendidas, sa.aforo - COUNT(e.id_entrada) AS libres
FROM sesiones s
JOIN salas sa ON sa.id_sala = s.id_sala
LEFT JOIN entradas e ON e.id_sesion = s.id_sesion AND e.estado IN ('PAGADA','USADA')
GROUP BY s.id_sesion, sa.aforo
ORDER BY s.id_sesion;

-- C16: Películas sin sesiones
-- README -> patrón: «sin sesiones» -> NOT EXISTS: películas sin ninguna fila en sesiones
SELECT p.titulo
FROM peliculas p
WHERE NOT EXISTS (SELECT 1 FROM sesiones s WHERE s.id_pelicula = p.id_pelicula)
ORDER BY p.titulo;

-- C17: Sesiones programadas sin ventas válidas
-- README -> patrón: «programadas» -> s.estado = 'PROGRAMADA'; «sin ventas válidas» -> NOT EXISTS de entradas PAGADA o USADA de esa sesión
SELECT s.id_sesion, p.titulo AS pelicula, s.fecha
FROM sesiones s
JOIN peliculas p ON p.id_pelicula = s.id_pelicula
WHERE s.estado = 'PROGRAMADA' AND NOT EXISTS (SELECT 1 FROM entradas e WHERE e.id_sesion = s.id_sesion AND e.estado IN ('PAGADA','USADA'))
ORDER BY s.id_sesion;

-- C18: Sesiones agotadas, sin contar cancelaciones
-- README -> patrón: «agotadas» -> HAVING COUNT(entradas válidas) >= aforo; «sin contar cancelaciones» -> WHERE e.estado IN ('PAGADA','USADA')
SELECT s.id_sesion, p.titulo AS pelicula, sa.aforo
FROM sesiones s
JOIN peliculas p ON p.id_pelicula = s.id_pelicula
JOIN salas sa ON sa.id_sala = s.id_sala
JOIN entradas e ON e.id_sesion = s.id_sesion
WHERE e.estado IN ('PAGADA','USADA')
GROUP BY s.id_sesion, p.titulo, sa.aforo
HAVING COUNT(e.id_entrada) >= sa.aforo
ORDER BY s.id_sesion;

-- C19: Películas con más ventas válidas que la media de las nueve películas (incluidas las de cero ventas)
-- README -> patrón: «más que la media» -> HAVING COUNT(...) > (subconsulta con la media); la media es total de ventas válidas / número de películas, con * 1.0 para que la división no sea entera; LEFT JOIN para que cuenten las de cero ventas
SELECT p.titulo AS pelicula, COUNT(e.id_entrada) AS entradas_validas
FROM peliculas p
LEFT JOIN sesiones s ON s.id_pelicula = p.id_pelicula
LEFT JOIN entradas e ON e.id_sesion = s.id_sesion AND e.estado IN ('PAGADA','USADA')
GROUP BY p.id_pelicula, p.titulo
HAVING COUNT(e.id_entrada) > (SELECT COUNT(*) * 1.0 / (SELECT COUNT(*) FROM peliculas) FROM entradas WHERE estado IN ('PAGADA','USADA'))
ORDER BY entradas_validas DESC, pelicula;

-- C20: Espectadores con entradas válidas en al menos dos sedes
-- README -> patrón: «al menos dos sedes» -> COUNT(DISTINCT sa.id_sede) >= 2 en el HAVING; la sede se alcanza por entradas->sesiones->salas
SELECT es.nombre AS espectador, COUNT(DISTINCT sa.id_sede) AS sedes_distintas
FROM espectadores es
JOIN entradas e ON e.id_espectador = es.id_espectador
JOIN sesiones s ON s.id_sesion = e.id_sesion
JOIN salas sa ON sa.id_sala = s.id_sala
WHERE e.estado IN ('PAGADA','USADA')
GROUP BY es.id_espectador, es.nombre
HAVING COUNT(DISTINCT sa.id_sede) >= 2
ORDER BY sedes_distintas DESC, espectador;

-- C21: Top 3 de películas por recaudación de entradas válidas
-- README -> patrón: «Top 3» -> ORDER BY recaudacion DESC y LIMIT 3; «recaudación» -> SUM(s.precio) de las entradas válidas
SELECT p.titulo AS pelicula, COUNT(e.id_entrada) AS entradas_validas, SUM(s.precio) AS recaudacion
FROM peliculas p
JOIN sesiones s ON s.id_pelicula = p.id_pelicula
JOIN entradas e ON e.id_sesion = s.id_sesion
WHERE e.estado IN ('PAGADA','USADA')
GROUP BY p.id_pelicula, p.titulo
ORDER BY recaudacion DESC, pelicula
LIMIT 3;

-- C22: Recaudación por sede, incluyendo sedes sin ventas
-- README -> patrón: «incluyendo sedes sin ventas» -> cadena de LEFT JOIN sedes->salas->sesiones->entradas y COALESCE(SUM(...), 0); el CASE solo suma el precio cuando hay entrada
SELECT se.nombre AS sede, COALESCE(SUM(CASE WHEN e.id_entrada IS NOT NULL THEN s.precio END), 0) AS recaudacion
FROM sedes se
LEFT JOIN salas sa ON sa.id_sede = se.id_sede
LEFT JOIN sesiones s ON s.id_sala = sa.id_sala
LEFT JOIN entradas e ON e.id_sesion = s.id_sesion AND e.estado IN ('PAGADA','USADA')
GROUP BY se.id_sede, se.nombre
ORDER BY recaudacion DESC, sede;

-- C23: Ventas diarias, excluyendo entradas canceladas
-- README -> patrón: «diarias» -> GROUP BY e.fecha_compra; «excluyendo canceladas» -> WHERE estado IN ('PAGADA','USADA'); el total es SUM(s.precio) de la sesión
SELECT e.fecha_compra, COUNT(e.id_entrada) AS entradas_validas, SUM(s.precio) AS total
FROM entradas e
JOIN sesiones s ON s.id_sesion = e.id_sesion
WHERE e.estado IN ('PAGADA','USADA')
GROUP BY e.fecha_compra
ORDER BY e.fecha_compra;

-- C24: Historial de entradas de un espectador
-- README -> patrón: «historial de un espectador» -> WHERE e.id_espectador = ? (aquí el valor fijo 1); aquí NO se filtra por estado porque se quieren ver también las canceladas
SELECT p.titulo AS pelicula, s.fecha AS fecha_sesion, e.estado AS estado_entrada, s.precio
FROM entradas e
JOIN sesiones s ON s.id_sesion = e.id_sesion
JOIN peliculas p ON p.id_pelicula = s.id_pelicula
WHERE e.id_espectador = 1
ORDER BY fecha_sesion, pelicula;

-- C25: Informe por fecha de sesión, incluidos días sin ventas
-- README -> patrón: «por fecha de sesión» -> GROUP BY s.fecha; «incluidos días sin ventas» -> LEFT JOIN a entradas y COALESCE; COUNT(DISTINCT CASE ...) cuenta solo las sesiones programadas sin repetir
SELECT s.fecha, COUNT(DISTINCT CASE WHEN s.estado = 'PROGRAMADA' THEN s.id_sesion END) AS sesiones_programadas, COUNT(e.id_entrada) AS entradas_validas, COALESCE(SUM(CASE WHEN e.id_entrada IS NOT NULL THEN s.precio END), 0) AS recaudacion
FROM sesiones s
LEFT JOIN entradas e ON e.id_sesion = s.id_sesion AND e.estado IN ('PAGADA','USADA')
GROUP BY s.fecha
ORDER BY s.fecha;

-- C26: Media y cantidad de valoraciones por película, incluyendo películas sin valorar
-- README -> patrón: «incluyendo películas sin valorar» -> LEFT JOIN valoraciones; COUNT(v.id_valoracion) da 0 y AVG da NULL para las que no tienen
SELECT p.titulo AS pelicula, COUNT(v.id_valoracion) AS numero_valoraciones, ROUND(AVG(v.puntuacion), 2) AS media
FROM peliculas p
LEFT JOIN valoraciones v ON v.id_pelicula = p.id_pelicula
GROUP BY p.id_pelicula, p.titulo
ORDER BY pelicula;

-- C27: Títulos, sesiones y ventas válidas por género
-- README -> patrón: «por género» -> GROUP BY p.genero; COUNT(DISTINCT ...) en películas y sesiones porque los LEFT JOIN repiten filas; LEFT JOIN para no perder géneros sin sesiones
SELECT p.genero, COUNT(DISTINCT p.id_pelicula) AS titulos, COUNT(DISTINCT s.id_sesion) AS sesiones, COUNT(e.id_entrada) AS entradas_validas
FROM peliculas p
LEFT JOIN sesiones s ON s.id_pelicula = p.id_pelicula
LEFT JOIN entradas e ON e.id_sesion = s.id_sesion AND e.estado IN ('PAGADA','USADA')
GROUP BY p.genero
ORDER BY p.genero;

-- C28: Salas sin sesiones programadas
-- README -> patrón: «sin sesiones programadas» -> NOT EXISTS con s.estado = 'PROGRAMADA' dentro de la subconsulta; las sesiones canceladas no cuentan
SELECT se.nombre AS sede, sa.nombre AS sala
FROM salas sa
JOIN sedes se ON se.id_sede = sa.id_sede
WHERE NOT EXISTS (SELECT 1 FROM sesiones s WHERE s.id_sala = sa.id_sala AND s.estado = 'PROGRAMADA')
ORDER BY sede, sala;

-- C29: Registro atómico de entrada con control de aforo
-- README -> patrón: «atómico» -> BEGIN IMMEDIATE (bloquea la escritura en SQLite) y las comprobaciones y el INSERT dentro de la misma transacción; así nadie vende la última plaza entre medias
BEGIN IMMEDIATE;
SELECT COUNT(*) AS sesion_programada
FROM sesiones
WHERE id_sesion = 1 AND estado = 'PROGRAMADA';
SELECT COUNT(*) AS espectador_activo
FROM espectadores
WHERE id_espectador = 8 AND activo = 1;
SELECT COUNT(*) AS entrada_existente
FROM entradas
WHERE id_sesion = 1 AND id_espectador = 8;
SELECT sa.aforo - COUNT(e.id_entrada) AS libres
FROM sesiones s
JOIN salas sa ON sa.id_sala = s.id_sala
LEFT JOIN entradas e ON e.id_sesion = s.id_sesion AND e.estado IN ('PAGADA','USADA')
WHERE s.id_sesion = 1
GROUP BY s.id_sesion, sa.aforo;
-- si falla alguna comprobación: ROLLBACK
INSERT INTO entradas (id_sesion, id_espectador, fecha_compra, estado)
VALUES (1, 8, '2026-11-10', 'PAGADA');
COMMIT;

-- C30: Cancelación de entrada y liberación de plaza
-- README -> patrón: «cancelación» -> UPDATE del estado a 'CANCELADA', nunca DELETE (debe quedar registro); la plaza se libera sola porque las libres solo cuentan PAGADA y USADA
BEGIN IMMEDIATE;
SELECT COUNT(*) AS entrada_sin_cancelar
FROM entradas
WHERE id_entrada = 1 AND estado <> 'CANCELADA';
-- si no existe o ya está CANCELADA: ROLLBACK
UPDATE entradas
SET estado = 'CANCELADA'
WHERE id_entrada = 1;
COMMIT;
