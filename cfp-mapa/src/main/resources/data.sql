DELETE FROM conexiones;
DELETE FROM espacios;

INSERT INTO espacios
(id, nombre, descripcion, tipo, sector, coordenadax, coordenaday, accesible, estado)
VALUES
    -- =================
    -- ESPACIOS SECTOR 1
    -- =================
    (1, 'Electricidad', NULL, 'TALLER', 'SECTOR_1', NULL, NULL, TRUE, 'ACTIVO'),
    (2, 'Herreria', NULL, 'TALLER', 'SECTOR_1', NULL, NULL, TRUE, 'ACTIVO'),
    (3, 'Climatizacion', NULL, 'TALLER', 'SECTOR_1', NULL, NULL, TRUE, 'ACTIVO'),
    (4, 'Serigrafia', NULL, 'TALLER', 'SECTOR_1', NULL, NULL, TRUE, 'ACTIVO'),
    (5, 'Baño Sector 1 (grande)', NULL, 'BANIO_MIXTO', 'SECTOR_1', NULL, NULL, TRUE, 'ACTIVO'),
    (6, 'Baño Sector 1 (chico)', NULL, 'BANIO_MIXTO', 'SECTOR_1', NULL, NULL, TRUE, 'ACTIVO'),

    -- ====================
    -- 2. ESPACIOS SECTOR 2
    -- ====================
    (7, 'Carpinteria', NULL, 'TALLER', 'SECTOR_2', 2050, 420, TRUE, 'ACTIVO'),
    (8, 'Taller de bicicleteria', NULL, 'TALLER', 'SECTOR_2', 2080, 330, TRUE, 'ACTIVO'),

    -- ====================
    -- 3. ESPACIOS SECTOR 3
    -- ====================
    (9, 'Informes', NULL, 'OFICINA', 'SECTOR_3', 2380, 710, TRUE, 'ACTIVO'),
    (10, 'Regencia', NULL, 'OFICINA', 'SECTOR_3', 2280, 710, TRUE, 'ACTIVO'),
    (11, 'Secretaria - Direccion', NULL, 'OFICINA', 'SECTOR_3', 2485, 745, TRUE, 'ACTIVO'),
    (12, 'Orientación', NULL, 'OFICINA', 'SECTOR_3', 2465, 610, TRUE, 'ACTIVO'),
    (13, 'Oficina de estudiantes', NULL, 'OFICINA', 'SECTOR_3', 2455, 515, TRUE, 'ACTIVO'),
    (14, 'Sala de personal', NULL, 'OFICINA', 'SECTOR_3', 2400, 555, TRUE, 'ACTIVO'),
    (15, 'Área de talleres dinamicos', NULL, 'TALLER', 'SECTOR_3', 2380, 470, TRUE, 'ACTIVO'),
    (16, 'Laboratorio de Informatica A', NULL, 'LABORATORIO', 'SECTOR_3', 2400, 650, TRUE, 'ACTIVO'),
    (17, 'Laboratorio de Informatica B', NULL, 'LABORATORIO', 'SECTOR_3', 2455, 355, TRUE, 'ACTIVO'),
    (18, 'Archivo institucional', NULL, 'OFICINA', 'SECTOR_3', NULL, NULL, TRUE, 'ACTIVO'),
    (19, 'Espacio tecnologico multidisciplinar', NULL, 'TALLER', 'SECTOR_3', 2425, 270, TRUE, 'ACTIVO'),

    -- ====================
    -- 4. ESPACIOS SECTOR 4
    -- ====================
    (20, 'Aula 1', NULL, 'AULA', 'SECTOR_4', 2735, 365, TRUE, 'ACTIVO'),
    (21, 'Aula 2', NULL, 'AULA', 'SECTOR_4', 2830, 365, TRUE, 'ACTIVO'),
    (22, 'Aula 3 - SUM', NULL, 'AULA', 'SECTOR_4', 2940, 365, TRUE, 'ACTIVO'),
    (23, 'Aula 4', NULL, 'AULA', 'SECTOR_4', 3070, 365, TRUE, 'ACTIVO'),
    (24, 'Aula 5', NULL, 'AULA', 'SECTOR_4', 3165, 365, TRUE, 'ACTIVO'),
    (25, 'Buffet', NULL, 'BUFFET', 'SECTOR_4', 2685, 455, TRUE, 'ACTIVO'),
    (26, 'Gastronomia A', NULL, 'TALLER', 'SECTOR_4', 3315, 365, TRUE, 'ACTIVO'),
    (27, 'Gastronomia B', NULL, 'TALLER', 'SECTOR_4', 3355, 465, TRUE, 'ACTIVO'),
    (28, 'Gastronomia C', NULL, 'TALLER', 'SECTOR_4', 3250, 465, TRUE, 'ACTIVO'),
    (29, 'Preceptoria EPS', NULL, 'OFICINA', 'SECTOR_4', 2900, 465, TRUE, 'ACTIVO'),
    (30, 'EPS - IFTS N5', NULL, 'OFICINA', 'SECTOR_4', 2810, 465, TRUE, 'ACTIVO'),
    (31, 'Baño Sector 4 MUJERES', NULL, 'BANIO_FEMENINO', 'SECTOR_4', 2990, 485, TRUE, 'ACTIVO'),
    (32, 'Baño Sector 4 HOMBRES', NULL, 'BANIO_MASCULINO', 'SECTOR_4', 3100, 465, TRUE, 'ACTIVO'),

    -- ======================
    -- 5. ENTRADAS EXTERIORES
    -- ======================
    (33, 'Entrada Principal CFP7', NULL, 'ENTRADA_PRINCIPAL_CFP', 'ENTRADAS', 1960, 1040, TRUE, 'ACTIVO'),
    (34, 'Dragones', NULL, 'ENTRADA_PRINCIPAL_PREDIO', 'ENTRADAS', NULL, NULL, TRUE, 'ACTIVO'),
    (35, 'Ramsay', NULL, 'ENTRADA_SECUNDARIA', 'ENTRADAS', NULL, NULL, TRUE, 'ACTIVO'),
    (36, 'Juramento', NULL, 'ENTRADA_SECUNDARIA', 'ENTRADAS', NULL, NULL, TRUE, 'ACTIVO'),
    (37, 'Echeverria', NULL, 'ENTRADA_SECUNDARIA', 'ENTRADAS', NULL, NULL, TRUE, 'ACTIVO'),
    (38, 'Olazabal', NULL, 'ENTRADA_SECUNDARIA', 'ENTRADAS', NULL, NULL, TRUE, 'ACTIVO'),

    -- ==========================
    -- 6. PUNTOS DE PASO SECTOR 3
    -- ==========================
    (39, 'Pasillo Sector 3 - Secretaria/Informes y Regencia', NULL, 'PUNTO_DE_PASO', 'SECTOR_3', 2460, 745, TRUE, 'ACTIVO'),
    (40, 'Pasillo Sector 3 - Orientacion/Personal', NULL, 'PUNTO_DE_PASO', 'SECTOR_3', 2440, 620, TRUE, 'ACTIVO'),
    (41, 'Pasillo Sector 3 - Oficina de Estudiantes/Talleres Dinamicos', NULL, 'PUNTO_DE_PASO', 'SECTOR_3', 2425, 535, TRUE, 'ACTIVO'),
    (42, 'Pasillo Sector 3 - Labo Info B', NULL, 'PUNTO_DE_PASO', 'SECTOR_3', 2415, 435, TRUE, 'ACTIVO'),
    (43, 'Pasillo Sector 3 - Espacio Tecno', NULL, 'PUNTO_DE_PASO', 'SECTOR_3', 2400, 320, TRUE, 'ACTIVO'),
    (44, 'Entrada Principal Sector 4', NULL, 'PUNTO_DE_PASO', 'SECTOR_3', 2675, 415, TRUE, 'ACTIVO'),
    (45, 'Pasillo Secundario Sector 3', NULL, 'PUNTO_DE_PASO', 'SECTOR_3', 2590, 435, TRUE, 'ACTIVO'),

    -- ==========================
    -- 7. PUNTOS DE PASO SECTOR 4
    -- ==========================
    (46, 'Pasillo Principal Sector 4', NULL, 'PUNTO_DE_PASO', 'SECTOR_4', 2675, 415, TRUE, 'ACTIVO'),
    (47, 'Pasillo Baño Sector 4', NULL, 'PUNTO_DE_PASO', 'SECTOR_4', 2970, 485, TRUE, 'ACTIVO'),

    -- ==============================
    -- 8. PUNTOS DE PASO SECTOR 2 Y 3
    -- ==============================
    (48, 'Patio Sector 2 - Carpinteria/Bicicletas', NULL, 'PATIO', 'SECTOR_2', 2115, 420, TRUE, 'ACTIVO'),
    (49, 'Pasillo Principal - Patio Sector 3', NULL, 'PATIO', 'SECTOR_3', 2760, 850, TRUE, 'ACTIVO'),
    (50, 'Frente CFP7', NULL, 'PUNTO_DE_PASO', 'SECTOR_3', 2455, 830, TRUE, 'ACTIVO'),

    -- ==========================
    -- 9. PUNTOS DE PASO ENTRADAS
    -- ==========================
    (51, 'Rampa Principal', NULL, 'PUNTO_DE_PASO', 'ENTRADAS', 1950, 950, TRUE, 'ACTIVO'),
    (52, 'Pasillo Principal - Entrada', NULL, 'PUNTO_DE_PASO', 'ENTRADAS', 1950, 850, TRUE, 'ACTIVO'),
    (53, 'Pasillo Principal - Patio Sector 2', NULL, 'PUNTO_DE_PASO', 'ENTRADAS', 2115, 850, TRUE, 'ACTIVO'),
    (54, 'Pasillo Principal - Frente CFP7', NULL, 'PUNTO_DE_PASO', 'ENTRADAS', 2455, 850, TRUE, 'ACTIVO'),

    -- =============================================================
    -- PUNTOS DE PASO RESTANTES SECTOR 2-3 Y PUNTOS DE PASO SECTOR 4
    -- =============================================================
    (55, 'Patio Sector 2', NULL, 'PATIO', 'SECTOR_2', 2115, 685, TRUE, 'ACTIVO'),
    (56, 'Patio entre Sector 3 y 4', NULL, 'PATIO', 'SECTOR_3', 2630, 435, TRUE, 'ACTIVO'),
    (57, 'Pasillo Sector 4 - Aula 2/EPS', NULL, 'PUNTO_DE_PASO', 'SECTOR_4', 2815, 415, TRUE, 'ACTIVO'),
    (58, 'Pasillo Sector 4 - Aula 3/Preceptoria', NULL, 'PUNTO_DE_PASO', 'SECTOR_4', 2900, 415, TRUE, 'ACTIVO'),
    (59, 'Pasillo Sector 4 - Lockers', NULL, 'PUNTO_DE_PASO', 'SECTOR_4', 2960, 445, TRUE, 'ACTIVO'),
    (60, 'Pasillo Sector 4 - Aula 4/Baño', NULL, 'PUNTO_DE_PASO', 'SECTOR_4', 2955, 415, TRUE, 'ACTIVO'),
    (61, 'Pasillo Sector 4 - Aula 5', NULL, 'PUNTO_DE_PASO', 'SECTOR_4', 3100, 415, TRUE, 'ACTIVO'),
    (62, 'Pasillo Sector 4 - Gastronomia C', NULL, 'PUNTO_DE_PASO', 'SECTOR_4', 3200, 415, TRUE, 'ACTIVO'),
    (63, 'Pasillo Sector 4 - Gastronomia A/B', NULL, 'PUNTO_DE_PASO', 'SECTOR_4', 3335, 415, TRUE, 'ACTIVO');


INSERT INTO conexiones
(id, espacio_origen_id, espacio_destino_id, tipo_transito, distancia, ancho, cumple_ley962, accesible, estado)
VALUES
    -- ===========================================
    -- 1. ENTRADAS EXTERIORES ➔ ENTRADA PRINCIPAL
    -- ===========================================
    (1,  34, 33, 'EXTERIOR', 1.00,  3.00, TRUE, TRUE, 'ACTIVA'),
    (2,  35, 33, 'EXTERIOR', 1.00,  3.00, TRUE, TRUE, 'ACTIVA'),
    (3,  36, 33, 'EXTERIOR', 1.00,  3.00, TRUE, TRUE, 'ACTIVA'),
    (4,  37, 33, 'EXTERIOR', 1.00,  3.00, TRUE, TRUE, 'ACTIVA'),
    (5,  38, 33, 'EXTERIOR', 1.00,  3.00, TRUE, TRUE, 'ACTIVA'),

    -- =================================================
    -- 2. ENTRADA PRINCIPAL ➔ RAMPA  ➔ PASILLO ENTRADA
    -- =================================================
    (6,  33, 51, 'PASILLO',  2.00,  6.00, TRUE, TRUE, 'ACTIVA'),
    (7,  51, 52, 'PASILLO',  2.00,  4.00, TRUE, TRUE, 'ACTIVA'),

    -- ===================================================
    -- 3. PASILLO PRINCIPAL LONGITUDINAL -> PATIO SECTOR 3
    -- ===================================================
    (8, 52, 53, 'PASILLO', 2.00, 6.00, TRUE, TRUE, 'ACTIVA'),
    (9, 53, 54, 'PASILLO', 2.50, 5.00, TRUE, TRUE, 'ACTIVA'),
    (10, 54, 50, 'EXTERIOR', 2.50, 2.00, TRUE, TRUE, 'ACTIVA'),
    (11, 54, 49, 'EXTERIOR', 2.50, 6.00, TRUE, TRUE, 'ACTIVA'),
    (12, 49, 56, 'EXTERIOR', 2.50, 6.00, TRUE, TRUE, 'ACTIVA'),

    -- ==================================================
    -- 4. INGRESO -> RECORRIDO PASILLO PRINCIPAL SECTOR 3
    -- ==================================================
    (13, 50, 39, 'PASILLO', 1.67, 3.00, TRUE,  TRUE, 'ACTIVA'),
    (14, 39, 40, 'PASILLO', 1.67, 5.00, FALSE, TRUE, 'ACTIVA'),
    (15, 40, 41, 'PASILLO', 1.67, 5.00, FALSE, TRUE, 'ACTIVA'),
    (16, 41, 42, 'PASILLO', 1.67, 5.00, FALSE, TRUE, 'ACTIVA'),
    (17, 42, 43, 'PASILLO', 1.67, 5.00, FALSE, TRUE, 'ACTIVA'),

    -- ===================================================
    -- 5. CONEXIONES PASILLO PRINCIPAL SECTOR 3 -> PUERTAS
    -- ===================================================
    (18,39,11,'PUERTA',1.00,2.00,TRUE,TRUE,'ACTIVA'),
    (19,39,9,'PUERTA',1.00,2.00,TRUE,TRUE,'ACTIVA'),
    (20,39,16,'PUERTA',1.00,2.00,TRUE,TRUE,'ACTIVA'),
    (21,9,10,'PUERTA',1.00,2.00,TRUE,TRUE,'ACTIVA'),
    (22,40,12,'PUERTA',1.00,2.00,TRUE,TRUE,'ACTIVA'),
    (23,40,14,'PUERTA',1.00,2.00,TRUE,TRUE,'ACTIVA'),
    (24,41,13,'PUERTA',1.00,2.00,TRUE,TRUE,'ACTIVA'),
    (25,41,15,'PUERTA',1.00,2.00,TRUE,TRUE,'ACTIVA'),
    (26,42,17,'PUERTA',1.00,2.00, TRUE,TRUE,'ACTIVA'),
    (27,45,42,'PASILLO',6.00, 5.00, TRUE, TRUE, 'ACTIVA'),
    (28,43,19,'PUERTA',1.00,2.00, TRUE,TRUE,'ACTIVA'),

    -- ================================================
    -- 6. CONEXIONES PATIO SECTOR 3 -> PUERTAS/SECTOR 4
    -- ================================================
    (29,56,45,'PASILLO',1.00,4.00,TRUE,TRUE,'ACTIVA'),
    (30,56,25,'PUERTA',1.00,4.00,TRUE,TRUE,'ACTIVA'),
    (31,56,46,'PUERTA',1.00,4.00,TRUE,TRUE,'ACTIVA'),

    -- ==============================
    -- 7. CONEXIONES PASILLO SECTOR 4
    -- ==============================
    (32,46,57,'PASILLO',1.00,4.00,TRUE,TRUE,'ACTIVA'),
    (33,57,58,'PASILLO',1.00,4.00,TRUE,TRUE,'ACTIVA'),
    (34,58,60,'PASILLO',1.00,4.00,TRUE,TRUE,'ACTIVA'),
    (35,60,59,'PASILLO',1.00,3.00,TRUE,TRUE,'ACTIVA'),
    (36,59,47,'PASILLO',1.00,3.00,TRUE,TRUE,'ACTIVA'),
    (37,60,61,'PASILLO',1.00,4.00,TRUE,TRUE,'ACTIVA'),
    (38,61,62,'PASILLO',1.00,4.00,TRUE,TRUE,'ACTIVA'),
    (39,62,63,'PASILLO',1.00,4.00,TRUE,TRUE,'ACTIVA'),

    -- ================================================
    -- 8. CONEXIONES PASILLO SECTOR 4 -> AULAS SECTOR 4
    -- ================================================
    (40,46,20,'PUERTA',1.00,3.00,TRUE,TRUE,'ACTIVA'),
    (41,47,31,'PUERTA',1.00,3.00,TRUE,TRUE,'ACTIVA'),
    (42,57,21,'PUERTA',1.00,3.00,TRUE,TRUE,'ACTIVA'),
    (43,57,30,'PUERTA',1.00,3.00,TRUE,TRUE,'ACTIVA'),
    (44,58,22,'PUERTA',1.00,3.00,TRUE,TRUE,'ACTIVA'),
    (45,58,29,'PUERTA',1.00,3.00,TRUE,TRUE,'ACTIVA'),
    (46,61,23,'PUERTA',1.00,3.00,TRUE,TRUE,'ACTIVA'),
    (47,61,32,'PUERTA',1.00,3.00,TRUE,TRUE,'ACTIVA'),
    (48,62,24,'PUERTA',1.00,3.00,TRUE,TRUE,'ACTIVA'),
    (49,62,28,'PUERTA',1.00,3.00,TRUE,TRUE,'ACTIVA'),
    (50,63,26,'PUERTA',1.00,3.00,TRUE,TRUE,'ACTIVA'),
    (51,63,27,'PUERTA',1.00,3.00,TRUE,TRUE,'ACTIVA'),

    -- ===================================================
    -- 9. CONEXIONES PASILLO PRINCIPAL ENTRADA -> SECTOR 2
    -- ===================================================
    (52,53,55,'EXTERIOR',1.00,4.00,TRUE,TRUE,'ACTIVA'),
    (53,55,48,'EXTERIOR',1.00,3.00,TRUE,TRUE,'ACTIVA'),
    (54,48,7,'EXTERIOR',1.00,3.00,TRUE,TRUE,'ACTIVA'),
    (55,48,8,'EXTERIOR',1.00,4.00,TRUE,TRUE,'ACTIVA');