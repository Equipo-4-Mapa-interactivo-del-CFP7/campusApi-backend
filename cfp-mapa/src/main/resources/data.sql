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

    (7, 'Carpinteria', NULL, 'TALLER', 'SECTOR_2', 2050, 420, TRUE, 'ACTIVO'),
    (8, 'Taller de bicicleteria', NULL, 'TALLER', 'SECTOR_2', 2070, 330, TRUE, 'ACTIVO'),

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

    (20, 'Aula 1', NULL, 'AULA', 'SECTOR_4', 2730, 365, TRUE, 'ACTIVO'),
    (21, 'Aula 2', NULL, 'AULA', 'SECTOR_4', 2830, 365, TRUE, 'ACTIVO'),
    (22, 'Aula 3 - SUM', NULL, 'AULA', 'SECTOR_4', 2940, 365, TRUE, 'ACTIVO'),
    (23, 'Aula 4', NULL, 'AULA', 'SECTOR_4', NULL, NULL, TRUE, 'ACTIVO'),
    (24, 'Aula 5', NULL, 'AULA', 'SECTOR_4', NULL, NULL, TRUE, 'ACTIVO'),
    (25, 'Buffet', NULL, 'BUFFET', 'SECTOR_4', 2685, 455, TRUE, 'ACTIVO'),
    (26, 'Gastronomia A', NULL, 'TALLER', 'SECTOR_4', NULL, NULL, TRUE, 'ACTIVO'),
    (27, 'Gastronomia B', NULL, 'TALLER', 'SECTOR_4', NULL, NULL, TRUE, 'ACTIVO'),
    (28, 'Gastronomia C', NULL, 'TALLER', 'SECTOR_4', NULL, NULL, TRUE, 'ACTIVO'),

    (29, 'Preceptoria EPS', NULL, 'OFICINA', 'SECTOR_4', 2900, 450, TRUE, 'ACTIVO'),
    (30, 'EPS - IFTS N5', NULL, 'OFICINA', 'SECTOR_4', 2810, 450, TRUE, 'ACTIVO'),

    (31, 'Bano Sector 4 (grande)', NULL, 'BANIO_MIXTO', 'SECTOR_4', NULL, NULL, TRUE, 'ACTIVO'),
    (32, 'Bano Sector 4 (mediano)', NULL, 'BANIO_MIXTO', 'SECTOR_4', NULL, NULL, TRUE, 'ACTIVO'),

    (33, 'Entrada Principal CFP7', NULL, 'ENTRADA_PRINCIPAL_CFP', 'ENTRADAS', 1910, 1040, TRUE, 'ACTIVO'),
    (34, 'Dragones', NULL, 'ENTRADA_PRINCIPAL_PREDIO', 'ENTRADAS', NULL, NULL, TRUE, 'ACTIVO'),
    (35, 'Ramsay', NULL, 'ENTRADA_SECUNDARIA', 'ENTRADAS', NULL, NULL, TRUE, 'ACTIVO'),
    (36, 'Juramento', NULL, 'ENTRADA_SECUNDARIA', 'ENTRADAS', NULL, NULL, TRUE, 'ACTIVO'),
    (37, 'Echeverria', NULL, 'ENTRADA_SECUNDARIA', 'ENTRADAS', NULL, NULL, TRUE, 'ACTIVO'),
    (38, 'Olazabal', NULL, 'ENTRADA_SECUNDARIA', 'ENTRADAS', NULL, NULL, TRUE, 'ACTIVO'),

    (39, 'Pasillo Sector 1 - Secretaria/Informes', NULL, 'PUNTO_DE_PASO', 'SECTOR_1', 2460, 745, TRUE, 'ACTIVO'),
    (40, 'Pasillo Sector 1 - Orientacion/Personal', NULL, 'PUNTO_DE_PASO', 'SECTOR_1', 2440, 620, TRUE, 'ACTIVO'),
    (41, 'Pasillo Sector 1 - Oficina de Estdudiantes/Talleres Dinamicos', NULL, 'PUNTO_DE_PASO', 'SECTOR_1', 2425, 535, TRUE, 'ACTIVO'),
    (42, 'Pasillo Sector 1 - Labo Info B', NULL, 'PUNTO_DE_PASO', 'SECTOR_1', 2415, 435, TRUE, 'ACTIVO'),
    (43, 'Pasillo Sector 1 - Espacio Tecno', NULL, 'PUNTO_DE_PASO', 'SECTOR_1', 2400, 320, TRUE, 'ACTIVO'),
    (44, 'Pasillo Principal Sector 3', NULL, 'PUNTO_DE_PASO', 'SECTOR_3', NULL, NULL, TRUE, 'ACTIVO'),
    (45, 'Pasillo Lateral Sector 3', NULL, 'PUNTO_DE_PASO', 'SECTOR_3', NULL, NULL, TRUE, 'ACTIVO'),
    (46, 'Pasillo Principal Sector 4', NULL, 'PUNTO_DE_PASO', 'SECTOR_4', NULL, NULL, TRUE, 'ACTIVO'),
    (47, 'Pasillo Lockers Sector 4', NULL, 'PUNTO_DE_PASO', 'SECTOR_4', NULL, NULL, TRUE, 'ACTIVO'),

    (48, 'Patio entre Sector 2 y 3', NULL, 'PATIO', 'SECTOR_2', NULL, NULL, TRUE, 'ACTIVO'),
    (49, 'Patio comunicante Sector 3 y 4', NULL, 'PATIO', 'SECTOR_3', NULL, NULL, TRUE, 'ACTIVO'),
    (50, 'Frente CFP7', NULL, 'PUNTO_DE_PASO', 'ENTRADAS', 2455, 830, TRUE, 'ACTIVO'),
    (51, 'Rampa Principal', NULL, 'PUNTO_DE_PASO', 'ENTRADAS', 1900, 950, TRUE, 'ACTIVO'),
    (52, 'Pasillo Principal - Entrada', NULL, 'PUNTO_DE_PASO', 'ENTRADAS', 1900, 850, TRUE, 'ACTIVO'),
    (53, 'Pasillo Principal - Patio', NULL, 'PUNTO_DE_PASO', 'ENTRADAS', 2115, 850, TRUE, 'ACTIVO'),
    (54, 'Pasillo Principal - Frente CFP7', NULL, 'PUNTO_DE_PASO', 'ENTRADAS', 2455, 850, TRUE, 'ACTIVO');

INSERT INTO conexiones
(id, espacio_origen_id, espacio_destino_id, tipo_transito, distancia, ancho, cumple_ley962, accesible, estado)
VALUES
    -- =========================================================================
    -- 1. ENTRADAS EXTERIORES (34 al 38) ➔ ENTRADA PRINCIPAL (33)
    -- =========================================================================
    (1,  34, 33, 'EXTERIOR', 1.00,  3.00, TRUE, TRUE, 'ACTIVA'),
    (2,  35, 33, 'EXTERIOR', 1.00,  3.00, TRUE, TRUE, 'ACTIVA'),
    (3,  36, 33, 'EXTERIOR', 1.00,  3.00, TRUE, TRUE, 'ACTIVA'),
    (4,  37, 33, 'EXTERIOR', 1.00,  3.00, TRUE, TRUE, 'ACTIVA'),
    (5,  38, 33, 'EXTERIOR', 1.00,  3.00, TRUE, TRUE, 'ACTIVA'),

    -- =========================================================================
    -- 2. ENTRADA PRINCIPAL (33) ➔ RAMPA (51) ➔ PASILLO ENTRADA (52)
    -- =========================================================================
    (6,  33, 51, 'PASILLO',  2.00,  6.00, TRUE, TRUE, 'ACTIVA'),
    (7,  51, 52, 'PASILLO',  2.00,  4.00, TRUE, TRUE, 'ACTIVA'),

    -- =========================================================================
    -- 3. PASILLO PRINCIPAL LONGITUDINAL (52 ➔ 53 ➔ 54 ➔ 50)
    -- =========================================================================
    (8,  52, 53, 'PASILLO',  2.00,  6.00, TRUE, TRUE, 'ACTIVA'), -- Pasillo Entrada (52) a Pasillo Patio (53)
    (9,  53, 54, 'PASILLO',  2.50,  5.00, TRUE, TRUE, 'ACTIVA'), -- Pasillo Patio (53) a Pasillo Frente (54)
    (10, 54, 50, 'EXTERIOR', 2.50,  2.00, TRUE, TRUE, 'ACTIVA'), -- Pasillo Frente (54) a Frente CFP7 (50)

    -- =========================================================================
    -- 4. INGRESO Y RECORRIDO SECUENCIAL DEL SECTOR 1 (50 ➔ 39 ➔ 40 ➔ 41 ➔ 42 ➔ 43)
    -- =========================================================================
    (11, 50, 39, 'PASILLO',  1.67,  3.00, TRUE,  TRUE, 'ACTIVA'), -- Frente CFP7 (50) ingresa a Pasillo 39
    (12, 39, 40, 'PASILLO',  1.67,  5.00, FALSE, TRUE, 'ACTIVA'), -- Sec/Inf (39) a Ori/Pers (40)
    (13, 40, 41, 'PASILLO',  1.67,  5.00, FALSE, TRUE, 'ACTIVA'), -- Ori/Pers (40) a Of. Estudiantes (41)
    (14, 41, 42, 'PASILLO',  1.67,  5.00, FALSE, TRUE, 'ACTIVA'), -- Of. Estudiantes (41) a Labo Info B (42)
    (15, 42, 43, 'PASILLO',  1.67,  5.00, FALSE, TRUE, 'ACTIVA'), -- Labo Info B (42) a fin de pasillo (43)

    -- =========================================================================
    -- 5. AULAS, TALLERES Y BAÑOS DE SECTOR 1 (IDs 1 al 6, y 17: Labo Info B)
    -- =========================================================================
    (16, 39, 1,  'PUERTA',   1.00,  2.00, TRUE,  TRUE, 'ACTIVA'), -- Taller Electricidad (1)
    (17, 39, 5,  'PUERTA',   1.00,  1.50, TRUE,  TRUE, 'ACTIVA'), -- Baño (5)
    (18, 40, 2,  'PUERTA',   1.00,  2.00, TRUE,  TRUE, 'ACTIVA'), -- Taller 2
    (19, 40, 6,  'PUERTA',   1.00,  1.50, TRUE,  TRUE, 'ACTIVA'), -- Baño (6)
    (20, 41, 3,  'PUERTA',   1.00,  2.00, TRUE,  TRUE, 'ACTIVA'), -- Taller 3
    (21, 42, 4,  'PUERTA',   1.00,  2.00, TRUE,  TRUE, 'ACTIVA'), -- Taller 4
    (22, 42, 17, 'PUERTA',   1.00,  2.00, TRUE,  TRUE, 'ACTIVA'), -- **Laboratorio Info B (17) ÚNICAMENTE desde pasillo 42**

    -- =========================================================================
    -- 6. CONEXIÓN HACIA PATIOS Y SECTORES 2/3 (Desde Pasillo Patio 53)
    -- =========================================================================
    (23, 53, 48, 'EXTERIOR', 3.00,  5.00, TRUE,  TRUE, 'ACTIVA'), -- Pasillo Patio (53) a Patio Central (48)
    (24, 48, 49, 'EXTERIOR', 3.00,  4.00, TRUE,  TRUE, 'ACTIVA'), -- Patio Central (48) a Patio Lateral (49)
    (25, 48, 7,  'PUERTA',   1.50,  2.00, TRUE,  TRUE, 'ACTIVA'), -- Taller Sector 2 (7) en Patio 48
    (26, 48, 8,  'PUERTA',   1.50,  2.00, TRUE,  TRUE, 'ACTIVA'), -- Taller Sector 2 (8) en Patio 48

    -- =========================================================================
    -- 7. SECTOR 3 (Pasillos 44 y 45, y espacios 9 al 16, 18, 19)
    -- =========================================================================
    (27, 48, 44, 'EXTERIOR', 3.00,  6.00, TRUE,  TRUE, 'ACTIVA'), -- Patio Central (48) a Pasillo Sector 3 (44)
    (28, 44, 45, 'PASILLO',  2.00,  4.00, TRUE,  TRUE, 'ACTIVA'), -- Pasillo 44 a Pasillo Lateral 45
    (29, 44, 9,  'PUERTA',   1.00,  1.50, TRUE,  TRUE, 'ACTIVA'), -- Informes (9)
    (30, 44, 10, 'PUERTA',   1.00,  1.50, TRUE,  TRUE, 'ACTIVA'), -- Oficina 10
    (31, 44, 11, 'PUERTA',   1.00,  1.50, TRUE,  TRUE, 'ACTIVA'), -- Secretaria (11)
    (32, 44, 12, 'PUERTA',   1.00,  2.00, TRUE,  TRUE, 'ACTIVA'), -- Taller/Lab 12
    (33, 44, 13, 'PUERTA',   1.00,  2.00, TRUE,  TRUE, 'ACTIVA'), -- Taller/Lab 13
    (34, 45, 14, 'PUERTA',   1.00,  2.00, TRUE,  TRUE, 'ACTIVA'), -- Taller/Lab 14
    (35, 45, 15, 'PUERTA',   1.00,  2.00, TRUE,  TRUE, 'ACTIVA'), -- Taller/Lab 15
    (36, 45, 16, 'PUERTA',   1.00,  2.00, TRUE,  TRUE, 'ACTIVA'), -- Taller/Lab 16
    (37, 45, 18, 'PUERTA',   1.00,  2.00, TRUE,  TRUE, 'ACTIVA'), -- Taller/Lab 18 (Se saltea 17 a propósito)
    (38, 45, 19, 'PUERTA',   1.00,  2.00, TRUE,  TRUE, 'ACTIVA'), -- Taller/Lab 19

    -- =========================================================================
    -- 8. SECTOR 4 - PASILLO PRINCIPAL 46 (Espacios 20 al 25, 31, 32)
    -- =========================================================================
    (39, 53, 46, 'PASILLO',  2.00,  4.00, TRUE,  TRUE, 'ACTIVA'), -- Pasillo Patio (53) a Pasillo Sector 4 (46)
    (40, 46, 47, 'PASILLO',  2.00,  3.00, TRUE,  TRUE, 'ACTIVA'), -- Pasillo 46 a Pasillo Lockers 47
    (41, 46, 20, 'PUERTA',   1.00,  2.00, TRUE,  TRUE, 'ACTIVA'),
    (42, 46, 21, 'PUERTA',   1.00,  2.00, TRUE,  TRUE, 'ACTIVA'),
    (43, 46, 22, 'PUERTA',   1.00,  2.00, TRUE,  TRUE, 'ACTIVA'),
    (44, 46, 23, 'PUERTA',   1.00,  2.00, TRUE,  TRUE, 'ACTIVA'),
    (45, 46, 24, 'PUERTA',   1.00,  2.00, TRUE,  TRUE, 'ACTIVA'),
    (46, 46, 25, 'PUERTA',   1.00,  2.00, TRUE,  TRUE, 'ACTIVA'),
    (47, 46, 31, 'PUERTA',   1.00,  2.00, TRUE,  TRUE, 'ACTIVA'),
    (48, 46, 32, 'PUERTA',   1.00,  2.00, TRUE,  TRUE, 'ACTIVA'),

    -- =========================================================================
    -- 9. SECTOR 4 - PASILLO LOCKERS 47 (Espacios Gastronomía y EPS: 26 al 30)
    -- =========================================================================
    (49, 47, 26, 'PUERTA',   1.00,  2.00, TRUE,  TRUE, 'ACTIVA'),
    (50, 47, 27, 'PUERTA',   1.00,  2.00, TRUE,  TRUE, 'ACTIVA'),
    (51, 47, 28, 'PUERTA',   1.00,  2.00, TRUE,  TRUE, 'ACTIVA'),
    (52, 47, 29, 'PUERTA',   1.00,  2.00, TRUE,  TRUE, 'ACTIVA'),
    (53, 47, 30, 'PUERTA',   1.00,  2.00, TRUE,  TRUE, 'ACTIVA');