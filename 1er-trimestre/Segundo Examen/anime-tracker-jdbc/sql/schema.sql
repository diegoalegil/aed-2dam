CREATE TABLE IF NOT EXISTS series (
    codigo    TEXT    PRIMARY KEY NOT NULL,
    titulo    TEXT    NOT NULL,
    genero    TEXT    NOT NULL,
    episodios INTEGER NOT NULL,
    nota      REAL    NOT NULL
);
