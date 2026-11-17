CREATE TABLE IF NOT EXISTS plataforma (
    codigo     TEXT    PRIMARY KEY NOT NULL,
    nombre     TEXT    NOT NULL,
    fabricante TEXT    NOT NULL,
    anio       INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS videojuego (
    id           INTEGER PRIMARY KEY NOT NULL,
    titulo       TEXT    NOT NULL,
    precio       REAL    NOT NULL,
    multijugador INTEGER NOT NULL,
    nota         INTEGER NOT NULL,
    plataforma   TEXT    NOT NULL
);
