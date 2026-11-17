PRAGMA foreign_keys = ON;
CREATE TABLE sucursales (
  id_sucursal INTEGER PRIMARY KEY AUTOINCREMENT,
  codigo TEXT NOT NULL UNIQUE,
  nombre TEXT NOT NULL,
  ciudad TEXT NOT NULL);
CREATE TABLE empleados (
  id_empleado INTEGER PRIMARY KEY AUTOINCREMENT,
  dni TEXT NOT NULL UNIQUE,
  nombre TEXT NOT NULL,
  puesto TEXT NOT NULL CHECK (puesto IN ('CAJERO','GESTOR','DIRECTOR')),
  salario NUMERIC NOT NULL CHECK (salario >= 0),
  id_sucursal INTEGER NOT NULL REFERENCES sucursales(id_sucursal) ON DELETE RESTRICT);
CREATE TABLE clientes (
  id_cliente INTEGER PRIMARY KEY AUTOINCREMENT,
  dni TEXT NOT NULL UNIQUE,
  nombre TEXT NOT NULL,
  email TEXT NOT NULL UNIQUE,
  telefono TEXT,
  fecha_alta TEXT NOT NULL,
  id_gestor INTEGER REFERENCES empleados(id_empleado) ON DELETE SET NULL);
CREATE TABLE cuentas (
  iban TEXT PRIMARY KEY,
  id_cliente INTEGER NOT NULL REFERENCES clientes(id_cliente) ON DELETE RESTRICT,
  id_sucursal INTEGER NOT NULL REFERENCES sucursales(id_sucursal) ON DELETE RESTRICT,
  tipo TEXT NOT NULL CHECK (tipo IN ('CORRIENTE','AHORRO','NOMINA')),
  saldo NUMERIC NOT NULL DEFAULT 0 CHECK (saldo >= 0),
  fecha_apertura TEXT NOT NULL,
  estado TEXT NOT NULL DEFAULT 'ACTIVA' CHECK (estado IN ('ACTIVA','BLOQUEADA','CERRADA')));
CREATE TABLE movimientos (
  id_movimiento INTEGER PRIMARY KEY AUTOINCREMENT,
  iban TEXT NOT NULL REFERENCES cuentas(iban) ON DELETE RESTRICT,
  tipo TEXT NOT NULL CHECK (tipo IN ('INGRESO','RETIRADA','TRANSFERENCIA_ENTRANTE','TRANSFERENCIA_SALIENTE','COMISION')),
  importe NUMERIC NOT NULL CHECK (importe > 0),
  fecha TEXT NOT NULL,
  concepto TEXT);
CREATE TABLE transferencias (
  id_transferencia INTEGER PRIMARY KEY AUTOINCREMENT,
  iban_origen TEXT NOT NULL REFERENCES cuentas(iban) ON DELETE RESTRICT,
  iban_destino TEXT NOT NULL REFERENCES cuentas(iban) ON DELETE RESTRICT,
  importe NUMERIC NOT NULL CHECK (importe > 0),
  fecha TEXT NOT NULL,
  concepto TEXT,
  CHECK (iban_origen <> iban_destino));
