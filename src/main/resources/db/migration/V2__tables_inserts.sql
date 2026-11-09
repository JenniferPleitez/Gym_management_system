-- INSERTAR DATOS:----------------------------------------------------------------------------------- 
-- Inserciones para Membresias
INSERT INTO Membresias (nombre, duracion, precio) VALUES
('Diaria', INTERVAL '1 day', 1.50),
('Quincenal', INTERVAL '15 days', 10.00),
('Mensual', INTERVAL '30 days', 15.00),
('Trimestral', INTERVAL '90 days', 43.50),
('Anual', INTERVAL '365 days', 170.00);

-- Inserciones para Usuarios
INSERT INTO Usuarios (nombre, apellido, fecha_nacimiento, fecha_inicio_membresia, fecha_fin_membresia, id_membresia) VALUES
('Juan', 'Pereira', '1995-06-15', '2025-01-01', '2025-04-01', 4),
('Maria', 'Lopez', '1998-04-22', '2025-01-15', '2025-04-15', 2),
('Carlos', 'Gomez', '2000-09-30', '2025-02-01', '2026-02-01', 3), 
('Ana', 'González', '1988-03-10', '2025-01-01', '2025-02-01', 2), 
('Luis', 'Martínez', '1995-12-12', '2025-03-01', '2026-03-01', 3),
('Sofía', 'Hernández', '1993-01-10', '2025-02-01', '2025-08-01', 4), 
('Pedro', 'López', '1990-11-20', '2025-03-01', '2025-09-01', 5), 
('Lucia', 'Ramírez', '1985-05-12', '2025-04-01', '2025-10-01', 1), 
('Diego', 'Fernández', '2001-07-15', '2025-01-15', '2025-07-15', 4), 
('Isabel', 'Núñez', '1997-03-30', '2025-02-05', '2025-08-05', 2);

-- Inserciones para Productos
INSERT INTO Productos (nombre, descripcion, precio, cantidad_disponible) VALUES
('Proteína Whey', 'Suplemento de proteína en polvo', 40.00, 20),
('Scoop de proteína', 'Un scoop de suplemento de proteína en polvo Whey', 1.50, 30),
('Camiseta Deportiva', 'Camiseta transpirable para entrenar', 15.00, 50),
('Creatina Monohidratada', 'Suplemento de creatina para rendimiento', 25.00, 30),
('Scoop de creatina', 'Suplemento de creatina para rendimiento', 1.50, 40),
('Barra de Proteína', 'Snack energético para deportistas', 1.00, 100),
('Toalla Deportiva', 'Toalla absorbente de microfibra', 10.00, 20),
('Botella de Agua 1 L', 'Botella reutilizable de 1 litro', 9.70, 30),
('Botella de Agua 2 L', 'Botella reutilizable de 2 litros', 12.60, 17),
('Guantes Deportivos', 'Guantes acolchados para pesas', 20.00, 25),
('Cinturón de Levantamiento', 'Cinturón ajustable para entrenamiento', 30.00, 15),
('500 g de magnesio en polvo', 'Polvo para absorber el sudor en las palmas de las manos', 10.00, 10);

-- Inserciones para Ventas
INSERT INTO Ventas (cantidad, fecha, id_usuario, id_producto) VALUES
(2, '2025-01-05', 1, 1),
(1, '2025-01-10', 2, 2),
(3, '2025-02-15', 3, 3),
(1, '2025-02-20', 4, 4),
(2, '2025-03-01', 5, 5),
(1, '2025-03-15', 6, 6),
(3, '2025-03-20', 7, 7),
(1, '2025-04-01', 8, 8),
(2, '2025-04-02', 9, 9),
(1, '2025-04-05', 10, 10);


-- Inserciones para Horarios_por_dia  
INSERT INTO Horarios_por_dia (dia, hora_apertura, hora_cierre) VALUES
('Lunes', '06:00:00', '22:00:00'),
('Martes', '06:00:00', '22:00:00'),
('Miércoles', '06:00:00', '22:00:00'),
('Jueves', '06:00:00', '22:00:00'),
('Viernes', '06:00:00', '22:00:00'),
('Sábado', '06:00:00', '18:00:00'),
('Domingo', '06:00:00', '12:00:00');

-- Inserciones para Asistencia_general
INSERT INTO Asistencia_general (fecha, hora_entrada, id_horario, id_usuario) VALUES
('2025-01-05', '08:00:00', 1, 1),
('2025-01-10', '10:00:00', 2, 2),
('2025-02-15', '18:00:00', 3, 3),
('2025-02-20', '07:30:00', 4, 4),
('2025-03-01', '09:00:00', 5, 5),
('2025-03-10', '08:15:00', 6, 6),
('2025-03-20', '07:50:00', 7, 7),
('2025-04-01', '06:10:00', 1, 8),
('2025-04-02', '11:00:00', 2, 9),
('2025-04-03', '07:45:00', 3, 10);

-- Inserciones para Clases_aerobicas 
INSERT INTO Clases_aerobicas (capacidad, id_horario, hora_inicio, hora_fin) VALUES
(20, 1, '08:00:00', '09:00:00'),
(15, 2, '18:00:00', '19:00:00'),
(18, 3, '07:00:00', '08:00:00'),
(20, 4, '09:00:00', '10:00:00'),
(25, 5, '17:00:00', '18:00:00'),
(15, 6, '10:00:00', '11:00:00'),
(10, 7, '06:30:00', '07:30:00'),
(22, 1, '16:00:00', '17:00:00'),
(18, 2, '15:00:00', '16:00:00'),
(20, 3, '11:00:00', '12:00:00');


-- Inserciones para Asistencia_clases_aerobicas
INSERT INTO Asistencia_clases_aerobicas (fecha, id_clase, id_usuario) VALUES
('2025-01-05', 1, 1),
('2025-01-15', 2, 2),
('2025-02-09', 3, 3),
('2025-03-21', 4, 4),
('2025-05-05', 5, 5),
('2025-05-15', 6, 6),
('2025-06-07', 7, 7),
('2025-07-24', 8, 8),
('2025-08-01', 9, 9),
('2025-09-25', 10, 10);