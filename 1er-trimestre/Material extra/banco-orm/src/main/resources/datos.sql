-- Datos de ejemplo del banco (para la demo y la pestaña Database de IntelliJ).
-- Los tests NO usan este fichero: crean una base temporal vacía con schema.sql.
-- Saldos coherentes con los movimientos. Fechas ISO YYYY-MM-DD.
PRAGMA foreign_keys = ON;

INSERT INTO sucursales (codigo, nombre, ciudad) VALUES
  ('SUC-001', 'Madrid Centro', 'Madrid'),
  ('SUC-002', 'Valencia Puerto', 'Valencia'),
  ('SUC-003', 'Sevilla Triana', 'Sevilla');

INSERT INTO empleados (dni, nombre, puesto, salario, id_sucursal) VALUES
  ('50123456Q', 'Laura Sánchez Ruiz', 'DIRECTOR', 3200.00, 1),
  ('50234567Z', 'Javier Moreno Díaz', 'GESTOR', 2100.00, 1),
  ('50345678N', 'Elena Navarro Gil', 'CAJERO', 1650.00, 1),
  ('51456789Q', 'Pablo Ortega Vidal', 'GESTOR', 2050.00, 2),
  ('51567890G', 'Marta Iglesias Romero', 'DIRECTOR', 3100.00, 2),
  ('52678901T', 'Sergio Castro León', 'GESTOR', 1980.50, 3);

-- Miguel (6) no tiene gestor. Raúl (8) no tiene cuentas.
INSERT INTO clientes (dni, nombre, email, telefono, fecha_alta, id_gestor) VALUES
  ('12345678Z', 'Ana Martín López', 'ana.martin@ejemplo.es', '611223344', '2026-01-12', 2),
  ('23456789D', 'Carlos Pérez Gómez', 'carlos.perez@ejemplo.es', '622334455', '2026-01-20', 2),
  ('34567890V', 'Lucía Fernández Torres', 'lucia.fernandez@ejemplo.es', NULL, '2026-02-03', 4),
  ('45678901G', 'David Romero Sanz', 'david.romero@ejemplo.es', '633445566', '2026-02-17', 4),
  ('56789012B', 'Sofía Jiménez Molina', 'sofia.jimenez@ejemplo.es', '644556677', '2026-03-05', 6),
  ('67890123B', 'Miguel Álvarez Prieto', 'miguel.alvarez@ejemplo.es', '655667788', '2026-03-05', NULL),
  ('78901234X', 'Paula Herrera Campos', 'paula.herrera@ejemplo.es', '666778899', '2026-04-22', 6),
  ('89012345E', 'Raúl Domínguez Vega', 'raul.dominguez@ejemplo.es', '677889900', '2026-05-30', 2);

-- La cuenta 6 tiene saldo 0 y la 8 está BLOQUEADA.
INSERT INTO cuentas (iban, id_cliente, id_sucursal, tipo, saldo, fecha_apertura, estado) VALUES
  ('ES2598760101111000000011', 1, 1, 'CORRIENTE', 1634.75, '2026-01-12', 'ACTIVA'),
  ('ES6798760101151000000029', 1, 1, 'AHORRO', 3060.00, '2026-01-15', 'ACTIVA'),
  ('ES7298760101131000000037', 2, 1, 'NOMINA', 3200.00, '2026-01-20', 'ACTIVA'),
  ('ES2998760202102000000045', 3, 2, 'CORRIENTE', 917.50, '2026-02-03', 'ACTIVA'),
  ('ES3198760202192000000053', 4, 2, 'NOMINA', 1874.50, '2026-02-17', 'ACTIVA'),
  ('ES3698760202172000000061', 4, 2, 'AHORRO', 0.00, '2026-02-20', 'ACTIVA'),
  ('ES3198760303813000000079', 5, 3, 'CORRIENTE', 965.50, '2026-03-05', 'ACTIVA'),
  ('ES6098760303883000000087', 6, 3, 'CORRIENTE', 400.00, '2026-03-06', 'BLOQUEADA'),
  ('ES6598760303863000000095', 7, 3, 'NOMINA', 1245.25, '2026-04-22', 'ACTIVA'),
  ('ES0898760303853000000103', 7, 3, 'AHORRO', 750.00, '2026-04-22', 'ACTIVA');

INSERT INTO transferencias (iban_origen, iban_destino, importe, fecha, concepto) VALUES
  ('ES2598760101111000000011', 'ES2998760202102000000045', 120.00, '2026-02-14', 'Regalo de cumpleaños'),
  ('ES7298760101131000000037', 'ES2598760101111000000011', 300.00, '2026-03-01', 'Devolución de préstamo'),
  ('ES3198760202192000000053', 'ES3198760303813000000079', 75.50, '2026-03-20', 'Cena de grupo'),
  ('ES3198760303813000000079', 'ES6798760101151000000029', 60.00, '2026-04-10', 'Entradas de concierto'),
  ('ES6598760303863000000095', 'ES0898760303853000000103', 500.00, '2026-05-02', 'Ahorro mensual'),
  ('ES2598760101111000000011', 'ES6598760303863000000095', 45.25, '2026-05-15', 'Material deportivo');

INSERT INTO movimientos (iban, tipo, importe, fecha, concepto) VALUES
  ('ES2598760101111000000011', 'INGRESO', 1500.00, '2026-01-15', 'Ingreso inicial'),
  ('ES6798760101151000000029', 'INGRESO', 3000.00, '2026-01-16', 'Traspaso de ahorro'),
  ('ES7298760101131000000037', 'INGRESO', 1850.00, '2026-01-31', 'Nómina enero'),
  ('ES2998760202102000000045', 'INGRESO', 800.00, '2026-02-05', 'Ingreso inicial'),
  ('ES2598760101111000000011', 'TRANSFERENCIA_SALIENTE', 120.00, '2026-02-14', 'Regalo de cumpleaños'),
  ('ES2998760202102000000045', 'TRANSFERENCIA_ENTRANTE', 120.00, '2026-02-14', 'Regalo de cumpleaños'),
  ('ES7298760101131000000037', 'INGRESO', 1850.00, '2026-02-28', 'Nómina febrero'),
  ('ES3198760202192000000053', 'INGRESO', 2100.00, '2026-02-28', 'Nómina febrero'),
  ('ES7298760101131000000037', 'TRANSFERENCIA_SALIENTE', 300.00, '2026-03-01', 'Devolución de préstamo'),
  ('ES2598760101111000000011', 'TRANSFERENCIA_ENTRANTE', 300.00, '2026-03-01', 'Devolución de préstamo'),
  ('ES7298760101131000000037', 'RETIRADA', 200.00, '2026-03-02', 'Cajero automático'),
  ('ES3198760303813000000079', 'INGRESO', 950.00, '2026-03-06', 'Ingreso inicial'),
  ('ES6098760303883000000087', 'INGRESO', 400.00, '2026-03-08', 'Ingreso inicial'),
  ('ES3198760202192000000053', 'RETIRADA', 150.00, '2026-03-10', 'Cajero automático'),
  ('ES3198760202192000000053', 'TRANSFERENCIA_SALIENTE', 75.50, '2026-03-20', 'Cena de grupo'),
  ('ES3198760303813000000079', 'TRANSFERENCIA_ENTRANTE', 75.50, '2026-03-20', 'Cena de grupo'),
  ('ES2998760202102000000045', 'COMISION', 2.50, '2026-03-31', 'Comisión de mantenimiento'),
  ('ES3198760303813000000079', 'TRANSFERENCIA_SALIENTE', 60.00, '2026-04-10', 'Entradas de concierto'),
  ('ES6798760101151000000029', 'TRANSFERENCIA_ENTRANTE', 60.00, '2026-04-10', 'Entradas de concierto'),
  ('ES0898760303853000000103', 'INGRESO', 250.00, '2026-04-23', 'Ingreso inicial'),
  ('ES6598760303863000000095', 'INGRESO', 1700.00, '2026-04-30', 'Nómina abril'),
  ('ES6598760303863000000095', 'TRANSFERENCIA_SALIENTE', 500.00, '2026-05-02', 'Ahorro mensual'),
  ('ES0898760303853000000103', 'TRANSFERENCIA_ENTRANTE', 500.00, '2026-05-02', 'Ahorro mensual'),
  ('ES2598760101111000000011', 'TRANSFERENCIA_SALIENTE', 45.25, '2026-05-15', 'Material deportivo'),
  ('ES6598760303863000000095', 'TRANSFERENCIA_ENTRANTE', 45.25, '2026-05-15', 'Material deportivo');
