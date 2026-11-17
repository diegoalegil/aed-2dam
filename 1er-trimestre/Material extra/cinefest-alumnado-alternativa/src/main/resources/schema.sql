-- Esquema de CinefEst (SQLite). Se ejecuta al crear la base de datos; CREATE TABLE IF NOT EXISTS evita error si la tabla ya existe.
-- Tipos SQLite y su Java: INTEGER -> Integer, TEXT -> String, REAL -> Double (fechas y horas van como TEXT, "2026-11-13").
-- Orden de creación: primero las tablas sin claves foráneas (sedes, peliculas, espectadores) y luego las que las usan.

-- Activa las claves foráneas: SQLite las ignora por defecto, así que sin esto REFERENCES no comprueba nada
PRAGMA foreign_keys = ON;

-- Tabla sedes: los cines del festival (modelo Sede)
CREATE TABLE IF NOT EXISTS sedes (
 -- PK: clave primaria; AUTOINCREMENT = la base de datos genera el id sola (Java: Integer)
 id_sede INTEGER PRIMARY KEY AUTOINCREMENT,
 -- NOT NULL: obligatorio; UNIQUE: no puede haber dos sedes con el mismo nombre (Java: String)
 nombre TEXT NOT NULL UNIQUE,
 -- NOT NULL: obligatorio (Java: String)
 municipio TEXT NOT NULL
);

-- Tabla salas: cada sala pertenece a una sede (modelo Sala)
CREATE TABLE IF NOT EXISTS salas (
 -- PK autogenerada (Java: Integer)
 id_sala INTEGER PRIMARY KEY AUTOINCREMENT,
 -- nombre obligatorio (Java: String); es único solo dentro de su sede, ver UNIQUE del final
 nombre TEXT NOT NULL,
 -- aforo obligatorio (Java: Integer); CHECK: debe ser mayor que 0, si no la base rechaza el INSERT o UPDATE
 aforo INTEGER NOT NULL CHECK (aforo > 0),
 -- FK: id_sede debe existir en sedes (Java: Integer); ON DELETE RESTRICT: no se puede borrar una sede que tenga salas
 id_sede INTEGER NOT NULL REFERENCES sedes(id_sede) ON DELETE RESTRICT,
 -- UNIQUE de varias columnas: no puede repetirse la pareja (sede, nombre); dos sedes sí pueden tener una "Sala 1"
 UNIQUE(id_sede,nombre)
);

-- Tabla peliculas (modelo Pelicula)
CREATE TABLE IF NOT EXISTS peliculas (
 -- PK autogenerada (Java: Integer)
 id_pelicula INTEGER PRIMARY KEY AUTOINCREMENT,
 -- título y género obligatorios (Java: String)
 titulo TEXT NOT NULL, genero TEXT NOT NULL,
 -- duración en minutos (Java: Integer); CHECK: mayor que 0
 duracion_min INTEGER NOT NULL CHECK (duracion_min > 0),
 -- país (Java: String) y año de estreno (Java: Integer), obligatorios
 pais TEXT NOT NULL, anio INTEGER NOT NULL
);

-- Tabla sesiones: una película proyectada en una sala a una fecha y hora (modelo Sesion)
CREATE TABLE IF NOT EXISTS sesiones (
 -- PK autogenerada (Java: Integer)
 id_sesion INTEGER PRIMARY KEY AUTOINCREMENT,
 -- FK a peliculas (Java: Integer); RESTRICT: no se puede borrar una película que tenga sesiones
 id_pelicula INTEGER NOT NULL REFERENCES peliculas(id_pelicula) ON DELETE RESTRICT,
 -- FK a salas (Java: Integer); RESTRICT: no se puede borrar una sala que tenga sesiones
 id_sala INTEGER NOT NULL REFERENCES salas(id_sala) ON DELETE RESTRICT,
 -- fecha y hora como texto obligatorio (Java: String), por ejemplo "2026-11-13" y "20:30"
 fecha TEXT NOT NULL, hora TEXT NOT NULL,
 -- precio (Java: Double, REAL = decimal); CHECK: 0 o más, nunca negativo
 precio REAL NOT NULL CHECK (precio >= 0),
 -- estado (Java: String); DEFAULT: si no se indica en el INSERT vale 'PROGRAMADA'; CHECK: solo uno de los tres valores
 estado TEXT NOT NULL DEFAULT 'PROGRAMADA' CHECK (estado IN ('PROGRAMADA','CANCELADA','FINALIZADA')),
 -- UNIQUE de tres columnas: una sala no puede tener dos sesiones el mismo día a la misma hora
 UNIQUE(id_sala,fecha,hora)
);

-- Tabla espectadores (modelo Espectador)
CREATE TABLE IF NOT EXISTS espectadores (
 -- PK autogenerada (Java: Integer)
 id_espectador INTEGER PRIMARY KEY AUTOINCREMENT,
 -- nombre obligatorio (Java: String); email obligatorio y UNIQUE: no puede haber dos espectadores con el mismo email
 nombre TEXT NOT NULL, email TEXT NOT NULL UNIQUE,
 -- fecha de alta como texto (Java: String)
 fecha_alta TEXT NOT NULL,
 -- activo (Java: Integer): 1 = activo, 0 = no (SQLite no tiene boolean); DEFAULT 1; CHECK: solo 0 o 1
 activo INTEGER NOT NULL DEFAULT 1 CHECK (activo IN (0,1))
);

-- Tabla entradas: la compra de una entrada de un espectador para una sesión (modelo Entrada)
CREATE TABLE IF NOT EXISTS entradas (
 -- PK autogenerada (Java: Integer)
 id_entrada INTEGER PRIMARY KEY AUTOINCREMENT,
 -- FK a sesiones (Java: Integer); RESTRICT: no se puede borrar una sesión con entradas
 id_sesion INTEGER NOT NULL REFERENCES sesiones(id_sesion) ON DELETE RESTRICT,
 -- FK a espectadores (Java: Integer); RESTRICT: no se puede borrar un espectador con entradas
 id_espectador INTEGER NOT NULL REFERENCES espectadores(id_espectador) ON DELETE RESTRICT,
 -- fecha de compra como texto (Java: String)
 fecha_compra TEXT NOT NULL,
 -- estado (Java: String); DEFAULT 'PAGADA'; CHECK: PAGADA, USADA o CANCELADA. Una entrada cancelada sigue en la tabla
 estado TEXT NOT NULL DEFAULT 'PAGADA' CHECK (estado IN ('PAGADA','USADA','CANCELADA')),
 -- UNIQUE: un espectador solo puede tener una fila por sesión (aunque esté cancelada no se puede volver a insertar)
 UNIQUE(id_sesion,id_espectador)
);

-- Tabla valoraciones: puntuación de un espectador a una película (modelo Valoracion)
CREATE TABLE IF NOT EXISTS valoraciones (
 -- PK autogenerada (Java: Integer)
 id_valoracion INTEGER PRIMARY KEY AUTOINCREMENT,
 -- FK a peliculas (Java: Integer); RESTRICT: no se puede borrar una película valorada
 id_pelicula INTEGER NOT NULL REFERENCES peliculas(id_pelicula) ON DELETE RESTRICT,
 -- FK a espectadores (Java: Integer); RESTRICT: no se puede borrar un espectador que ha valorado
 id_espectador INTEGER NOT NULL REFERENCES espectadores(id_espectador) ON DELETE RESTRICT,
 -- puntuación (Java: Integer); CHECK BETWEEN 1 AND 5: incluye el 1 y el 5
 puntuacion INTEGER NOT NULL CHECK (puntuacion BETWEEN 1 AND 5),
 -- comentario sin NOT NULL: es opcional y puede ser null (Java: String); fecha obligatoria como texto (Java: String)
 comentario TEXT, fecha TEXT NOT NULL,
 -- UNIQUE: un espectador solo puede valorar una vez cada película
 UNIQUE(id_pelicula,id_espectador)
);
