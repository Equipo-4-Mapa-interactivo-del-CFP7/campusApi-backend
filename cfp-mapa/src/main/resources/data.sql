DELETE FROM conexiones;
DELETE FROM espacios;

INSERT INTO espacios
(id, nombre, descripcion, tipo, sector, coordenadax, coordenaday, accesible, estado)
VALUES
    (1, 'Electricidad', NULL, 'TALLER', 'SECTOR_1', NULL, NULL, TRUE, 'ACTIVO'),
    (2, 'Herreria', NULL, 'TALLER', 'SECTOR_1', NULL, NULL, TRUE, 'ACTIVO'),
    (3, 'Climatizacion', NULL, 'TALLER', 'SECTOR_1', NULL, NULL, TRUE, 'ACTIVO'),
    (4, 'Serigrafia', NULL, 'TALLER', 'SECTOR_1', NULL, NULL, TRUE, 'ACTIVO'),
    (5, 'Bano Sector 1 (grande)', NULL, 'BANIO_MIXTO', 'SECTOR_1', NULL, NULL, TRUE, 'ACTIVO'),
    (6, 'Bano Sector 1 (chico)', NULL, 'BANIO_MIXTO', 'SECTOR_1', NULL, NULL, TRUE, 'ACTIVO'),

    (7, 'Carpinteria', NULL, 'TALLER', 'SECTOR_2', 1925, 725, TRUE, 'ACTIVO'),
    (8, 'Taller de bicicleteria', NULL, 'TALLER', 'SECTOR_2', NULL, NULL, TRUE, 'ACTIVO'),

    (9, 'Informes - Regencia', NULL, 'OFICINA', 'SECTOR_3', NULL, NULL, TRUE, 'ACTIVO'),
    (10, 'Secretaria - Direccion', NULL, 'OFICINA', 'SECTOR_3', 2545, 385, TRUE, 'ACTIVO'),
    (11, 'Oficina de estudiantes', NULL, 'OFICINA', 'SECTOR_3', 2530, 580, TRUE, 'ACTIVO'),
    (12, 'Sala de personal', NULL, 'OFICINA', 'SECTOR_3', 2310, 525, TRUE, 'ACTIVO'),
    (13, 'Área de talleres dinamicos', NULL, 'TALLER', 'SECTOR_3', 2290, 650, TRUE, 'ACTIVO'),
    (14, 'Laboratorio de Informatica A', NULL, 'LABORATORIO', 'SECTOR_3', 2300, 430, TRUE, 'ACTIVO'),
    (15, 'Laboratorio de Informatica B', NULL, 'LABORATORIO', 'SECTOR_3', 2505, 730, TRUE, 'ACTIVO'),
    (16, 'Archivo institucional', NULL, 'OFICINA', 'SECTOR_3', NULL, NULL, TRUE, 'ACTIVO'),
    (17, 'Espacio tecnologico multidisciplinar', NULL, 'TALLER', 'SECTOR_3', 2405, 735, TRUE, 'ACTIVO'),

    (18, 'Aula 1', NULL, 'AULA', 'SECTOR_4', 2715, 835, TRUE, 'ACTIVO'),
    (19, 'Aula 2', NULL, 'AULA', 'SECTOR_4', 2815, 835, TRUE, 'ACTIVO'),
    (20, 'Aula 3 - SUM', NULL, 'AULA', 'SECTOR_4', 2925, 835, TRUE, 'ACTIVO'),
    (21, 'Aula 4', NULL, 'AULA', 'SECTOR_4', NULL, NULL, TRUE, 'ACTIVO'),
    (22, 'Aula 5', NULL, 'AULA', 'SECTOR_4', NULL, NULL, TRUE, 'ACTIVO'),

    (23, 'Gastronomia A', NULL, 'TALLER', 'SECTOR_4', NULL, NULL, TRUE, 'ACTIVO'),
    (24, 'Gastronomia B', NULL, 'TALLER', 'SECTOR_4', NULL, NULL, TRUE, 'ACTIVO'),
    (25, 'Gastronomia C', NULL, 'TALLER', 'SECTOR_4', NULL, NULL, TRUE, 'ACTIVO'),

    (26, 'Preceptoria EPS', NULL, 'OFICINA', 'SECTOR_4', 2905, 620, TRUE, 'ACTIVO'),
    (27, 'EPS - IFTS N5', NULL, 'OFICINA', 'SECTOR_4', NULL, NULL, TRUE, 'ACTIVO'),

    (28, 'Bano Sector 4 (grande)', NULL, 'BANIO_MIXTO', 'SECTOR_4', NULL, NULL, TRUE, 'ACTIVO'),
    (29, 'Bano Sector 4 (mediano)', NULL, 'BANIO_MIXTO', 'SECTOR_4', NULL, NULL, TRUE, 'ACTIVO'),

    (30, 'Entrada Principal CFP7', NULL, 'ENTRADA_PRINCIPAL_CFP', 'ENTRADAS', 2455, 200, TRUE, 'ACTIVO'),
    (31, 'Dragones', NULL, 'ENTRADA_PRINCIPAL_PREDIO', 'ENTRADAS', NULL, NULL, TRUE, 'ACTIVO'),
    (32, 'Ramsay', NULL, 'ENTRADA_SECUNDARIA', 'ENTRADAS', NULL, NULL, TRUE, 'ACTIVO'),
    (33, 'Juramento', NULL, 'ENTRADA_SECUNDARIA', 'ENTRADAS', NULL, NULL, TRUE, 'ACTIVO'),
    (34, 'Echeverria', NULL, 'ENTRADA_SECUNDARIA', 'ENTRADAS', NULL, NULL, TRUE, 'ACTIVO'),
    (35, 'Olazabal', NULL, 'ENTRADA_SECUNDARIA', 'ENTRADAS', NULL, NULL, TRUE, 'ACTIVO'),

    (36, 'Pasillo Principal Sector 1', NULL, 'PUNTO_DE_PASO', 'SECTOR_1', NULL, NULL, TRUE, 'ACTIVO'),
    (37, 'Pasillo Principal Sector 3', NULL, 'PUNTO_DE_PASO', 'SECTOR_3', NULL, NULL, TRUE, 'ACTIVO'),
    (38, 'Pasillo Lateral Sector 3', NULL, 'PUNTO_DE_PASO', 'SECTOR_3', NULL, NULL, TRUE, 'ACTIVO'),
    (39, 'Pasillo Principal Sector 4', NULL, 'PUNTO_DE_PASO', 'SECTOR_4', NULL, NULL, TRUE, 'ACTIVO'),
    (40, 'Pasillo Lockers Sector 4', NULL, 'PUNTO_DE_PASO', 'SECTOR_4', NULL, NULL, TRUE, 'ACTIVO'),

    (41, 'Patio entre Sector 2 y 3', NULL, 'PATIO', 'SECTOR_2', NULL, NULL, TRUE, 'ACTIVO'),
    (42, 'Patio comunicante Sector 3 y 4', NULL, 'PATIO', 'SECTOR_3', NULL, NULL, TRUE, 'ACTIVO'),
    (43, 'Frente CFP7', NULL, 'PUNTO_DE_PASO', 'ENTRADAS', 2455, 200, TRUE, 'ACTIVO');

INSERT INTO conexiones
(id, espacio_origen_id, espacio_destino_id, tipo_transito, distancia, ancho, cumple_ley962, accesible, estado)
VALUES
    (1,31,30,'EXTERIOR',15,2.50,TRUE,TRUE,'ACTIVA'),
    (2,32,30,'EXTERIOR',18,2.50,TRUE,TRUE,'ACTIVA'),
    (3,33,30,'EXTERIOR',20,2.50,TRUE,TRUE,'ACTIVA'),
    (4,34,30,'EXTERIOR',25,2.50,TRUE,TRUE,'ACTIVA'),
    (5,35,30,'EXTERIOR',22,2.50,TRUE,TRUE,'ACTIVA'),

    (6,30,36,'PASILLO',8,1.67,FALSE,TRUE,'ACTIVA'),
    (7,30,41,'EXTERIOR',10,2.50,TRUE,TRUE,'ACTIVA'),

    (8,36,1,'PASILLO',4,1.67,FALSE,TRUE,'ACTIVA'),
    (9,36,2,'PASILLO',5,1.67,FALSE,TRUE,'ACTIVA'),
    (10,36,3,'PASILLO',6,1.67,FALSE,TRUE,'ACTIVA'),
    (11,36,4,'PASILLO',7,1.67,FALSE,TRUE,'ACTIVA'),
    (12,36,5,'PASILLO',4,1.67,FALSE,TRUE,'ACTIVA'),
    (13,36,6,'PASILLO',5,1.67,FALSE,TRUE,'ACTIVA'),

    (14,41,7,'EXTERIOR',6,3.00,TRUE,TRUE,'ACTIVA'),
    (15,41,8,'EXTERIOR',5,3.00,TRUE,TRUE,'ACTIVA'),

    (16,41,37,'EXTERIOR',5,1.50,FALSE,TRUE,'ACTIVA'),

    (17,37,9,'PASILLO',2,1.50,FALSE,TRUE,'ACTIVA'),
    (18,37,10,'PASILLO',2,1.50,FALSE,TRUE,'ACTIVA'),
    (19,37,11,'PASILLO',3,1.50,FALSE,TRUE,'ACTIVA'),
    (20,37,12,'PASILLO',3,1.50,FALSE,TRUE,'ACTIVA'),
    (21,37,13,'PASILLO',4,1.50,FALSE,TRUE,'ACTIVA'),
    (22,37,14,'PASILLO',5,1.50,FALSE,TRUE,'ACTIVA'),
    (23,37,15,'PASILLO',6,1.50,FALSE,TRUE,'ACTIVA'),
    (24,37,16,'PASILLO',3,1.50,FALSE,TRUE,'ACTIVA'),

    (25,37,38,'PASILLO',4,1.47,FALSE,TRUE,'ACTIVA'),
    (26,38,17,'PASILLO',2,1.47,FALSE,TRUE,'ACTIVA'),
    (27,37,42,'EXTERIOR',5,4.00,TRUE,TRUE,'ACTIVA'),
    (28,42,39,'EXTERIOR',5,4.00,TRUE,TRUE,'ACTIVA'),
    (29,39,18,'PASILLO',3,1.58,FALSE,TRUE,'ACTIVA'),
    (30,39,19,'PASILLO',4,1.58,FALSE,TRUE,'ACTIVA'),
    (31,39,20,'PASILLO',5,1.58,FALSE,TRUE,'ACTIVA'),
    (32,39,21,'PASILLO',6,1.58,FALSE,TRUE,'ACTIVA'),
    (33,39,22,'PASILLO',7,1.58,FALSE,TRUE,'ACTIVA'),

    (34,39,23,'PASILLO',4,1.58,FALSE,TRUE,'ACTIVA'),
    (35,39,24,'PASILLO',5,1.58,FALSE,TRUE,'ACTIVA'),
    (36,39,25,'PASILLO',6,1.58,FALSE,TRUE,'ACTIVA'),

    (37,39,26,'PASILLO',3,1.58,FALSE,TRUE,'ACTIVA'),
    (38,39,27,'PASILLO',4,1.58,FALSE,TRUE,'ACTIVA'),
    (39,39,28,'PASILLO',5,1.58,FALSE,TRUE,'ACTIVA'),
    (40,39,29,'PASILLO',6,1.58,FALSE,TRUE,'ACTIVA'),

    (41,39,40,'PASILLO',2,1.08,FALSE,TRUE,'ACTIVA');