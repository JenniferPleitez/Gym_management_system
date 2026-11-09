-- 0. Extensiones (ejecutar como superuser)
-- CREATE EXTENSION IF NOT EXISTS "pgcrypto"; ESTE NO SE PUEDE EJECUTAR AQUÍ (CON EL USER DE FLYWAY), SOLO UN SUPERUSER CON PRIVILEGIOS DE CREATE A NIVEL DE DB NO DE SCHEMA

-- Este Enum es para los tipos de action al momento de auditar las acciones hechas por los usuarios.
CREATE TYPE action_log AS ENUM (
    'RESET_PASSWORD',
    'VERIFY_EMAIL',
    'RESEND_TOKEN',
    'LOGIN_FAIL',
    'LOGIN',
    'LOGOUT',
    'CREATE_USER',
    'UPDATE_USER',
    'DELETE_USER',
    'REFRESH_ROTATE',
    'LOGOUT_ALL',
    'REFRESH_TOKEN_REUSE',
    'ROTATE_REFRESH_PARALLEL',
    'ACCOUNT_DISABLED',
    'ACCOUNT_STATUS_CHANGED',
    'REQUEST_RESET_PASSWORD',
	'SEND_VERIFICATION',
	'RESET_PASSWORD_ADMIN',
	'EMAIL_UPDATE_REQUESTED',
	'ACCOUNT_ENABLED',
	'ACCOUNT_LOCKED',
	'ACCOUNT_UNLOCKED'
);

CREATE TYPE auth_provider AS ENUM (
	'LOCAL',
    'GOOGLE',
    'FACEBOOK',
    'GITHUB'
);

-- Creación de la tabla donde se van a almacenar las Membresias.
-- Nada más almacena los planes disponibles para pagar en el gimnasio y sus precios.
CREATE TABLE Membresias(
id_membresia INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
nombre VARCHAR(30), -- Ex: Anual, mensual, etc...
duracion INTERVAL NOT NULL, -- De cuanto es la membresía (Ex: 15 días, 1 mes, 1 año...).
anulada BOOLEAN NOT NULL DEFAULT FALSE, -- Este campo es opcional y se utiliza para marcar una membresia como anulada sin eliminarlo de la base de datos.
precio NUMERIC(5, 2)
);

-- Tabla de cuentas / autenticación
CREATE TABLE Accounts (
  id_account INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
  public_id UUID NOT NULL DEFAULT gen_random_uuid(),
  email VARCHAR(100) UNIQUE NOT NULL,
  password_hash VARCHAR(255), --NOT NULL, -- bcrypt
  enabled BOOLEAN NOT NULL DEFAULT TRUE,
  locked BOOLEAN NOT NULL DEFAULT FALSE,
  profile_completed BOOLEAN NOT NULL DEFAULT FALSE,
  token_version INTEGER NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL,
  last_login TIMESTAMP WITH TIME ZONE,
  -- campos para recuperación/confirmación:
  email_verified BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE Auth_providers (
	id_auth_provider INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
	account_id INTEGER REFERENCES accounts(id_account) ON DELETE CASCADE,
	provider auth_provider NOT NULL, -- Ejemplo: (LOCAL, GOOGLE, APPLE, GITHUB)
	provider_id VARCHAR(100),
	created_at TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL
);

CREATE TABLE audit_logs (
	id_audit_log INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
	account_id INTEGER REFERENCES accounts(id_account) ON DELETE CASCADE,
	action_log action_log NOT NULL,
	subject VARCHAR(50),
	created_at TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL,
	ip INET NOT NULL,
	user_agent VARCHAR(200) NOT NULL,
	details VARCHAR(255)
);

-- Tabla de roles
CREATE TABLE Roles (
  id_role INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
  role_name VARCHAR(35) UNIQUE NOT NULL -- ej: ROLE_USER, ROLE_ADMIN
);

-- Tabla puente (many-to-many)
CREATE TABLE Account_Roles (
  id_account INTEGER REFERENCES Accounts(id_account) ON DELETE CASCADE,
  id_role INTEGER REFERENCES Roles(id_role) ON DELETE CASCADE,
  PRIMARY KEY (id_account, id_role)
);

-- CREATE verification tokens table
CREATE TABLE account_verification_tokens (
  id_account_verification_token INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
  account_id INTEGER NOT NULL REFERENCES accounts(id_account) ON DELETE CASCADE,
  token_hash VARCHAR(128) NOT NULL,
  expires_at TIMESTAMPTZ NOT NULL,
  used BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_account_verif_expires ON account_verification_tokens(expires_at);

-- CREATE password reset tokens table
CREATE TABLE password_reset_tokens (
  id_password_reset_token INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
  account_id INTEGER NOT NULL REFERENCES accounts(id_account) ON DELETE CASCADE,
  token_hash VARCHAR(128) NOT NULL,
  expires_at TIMESTAMPTZ NOT NULL,
  used BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_password_reset_expires ON password_reset_tokens(expires_at);

CREATE TABLE refresh_tokens (
	id_refresh_token INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
	account_id INTEGER NOT NULL REFERENCES accounts(id_account) ON DELETE CASCADE,
	token_hash VARCHAR(128) NOT NULL,
	expires_at TIMESTAMPTZ NOT NULL,
	revoked BOOLEAN NOT NULL DEFAULT FALSE,
	replaced_by_token_id INTEGER REFERENCES refresh_tokens(id_refresh_token),
	device_info VARCHAR(255) NOT NULL,
	created_by_ip VARCHAR(255),
	created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Creación de la tabla donde se van a almacenar los Usuarios.
-- Es la tabla dedicada a llevar el control de todas las personas que han pagado el gimnasio.
CREATE TABLE Usuarios(
id_usuario INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
nombre VARCHAR(40),
apellido VARCHAR(40),
fecha_nacimiento DATE,
fecha_inicio_membresia DATE, -- Nos indica cuando empezó la membresía de este usuario (Ex: 24/08/2025).
fecha_fin_membresia DATE, -- Es para saber cuando vence la membresía que pagó (Ex: vence el 24/09/2025).
anulada BOOLEAN NOT NULL DEFAULT FALSE, -- Este campo es opcional y se utiliza para marcar un usuario como anulado sin eliminarlo de la base de datos.
id_membresia INTEGER REFERENCES Membresias(id_membresia) NOT NULL, -- Nos indica que tipo de membresia ha pagado el usuario.
id_account INTEGER REFERENCES Accounts (id_account) -- Nos indica la cuenta que pertenece al usuario.
);

-- Creación de la tabla donde se van a almacenar los Productos.
-- Almacena todos los productos que se venden en el gimnasio (como camisas, centros, proteína, creatina, galletas, etc...).
CREATE TABLE Productos(
id_producto INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
nombre VARCHAR(100) NOT NULL,
descripcion TEXT NOT NULL,
precio NUMERIC(5, 2) NOT NULL,
cantidad_disponible INTEGER NOT NULL, -- Nos indica la cantidad en existencia de un "X" producto (Ex: quedan 10 camisas).
anulada BOOLEAN NOT NULL DEFAULT FALSE -- Este campo es opcional y se utiliza para marcar un Producto como anulada sin eliminarla de la base de datos.
);

-- Creación de la tabla donde se van a almacenar las Ventas
-- Almacena todos las ventas que se han realizado en el gimnasio (como si se vendieron 2 galletas, 4 camisas, etc...).
CREATE TABLE Ventas(
id_venta INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL, -- Es GENERATED BY DEFAULT ya que le pasamos el Id manualmente desde Java.
cantidad INTEGER CHECK (cantidad > 0) NOT NULL, -- La cantidad de cosas que compró un usuario (Ex: 2 scoops de proteína).
fecha DATE NOT NULL, -- La fecha de cuando fue realizada la compra.
total NUMERIC(5, 2) CHECK (total >= 0) NOT NULL, -- El costo total de todo lo comprado (Ex: Si compró 2 scoops de proteína [0.75 x 2], se almacenaría $1.50)
anulada BOOLEAN NOT NULL DEFAULT FALSE, -- Este campo es opcional y se utiliza para marcar una venta como anulada sin eliminarla de la base de datos.
id_usuario INTEGER REFERENCES Usuarios(id_usuario) NOT NULL, -- Nos hace referencia a que usuario ha realizado esta compra.
id_producto INTEGER REFERENCES Productos(id_producto) NOT NULL, -- Nos indica cual fue el producto que fue comprado.
PRIMARY KEY (id_venta, fecha),
UNIQUE (id_venta, fecha)
)PARTITION BY RANGE (fecha);  -- PARTICIÓN PARA LAS VENTAS MES A MES

-- SELECT * FROM ventas;

-- PARTICIONES MES POR MES:
CREATE TABLE ventas_enero PARTITION OF Ventas FOR VALUES FROM ('2025-01-01') TO ('2025-02-01');
CREATE TABLE ventas_febrero PARTITION OF Ventas FOR VALUES FROM ('2025-02-01') TO ('2025-03-01');
CREATE TABLE ventas_marzo PARTITION OF Ventas FOR VALUES FROM ('2025-03-01') TO ('2025-04-01');
CREATE TABLE ventas_abril PARTITION OF Ventas FOR VALUES FROM('2025-04-01') TO ('2025-05-01');
CREATE TABLE ventas_mayo PARTITION OF Ventas FOR VALUES FROM('2025-05-01') TO ('2025-06-01');
CREATE TABLE ventas_junio PARTITION OF Ventas FOR VALUES FROM('2025-06-01') TO ('2025-07-01');
CREATE TABLE ventas_julio PARTITION OF Ventas FOR VALUES FROM('2025-07-01') TO ('2025-08-01');
CREATE TABLE ventas_agosto PARTITION OF Ventas FOR VALUES FROM('2025-08-01') TO ('2025-09-01');
CREATE TABLE ventas_septiembre PARTITION OF Ventas FOR VALUES FROM('2025-09-01') TO ('2025-10-01');
CREATE TABLE ventas_octubre PARTITION OF Ventas FOR VALUES FROM('2025-10-01') TO ('2025-11-01');
CREATE TABLE ventas_noviembre PARTITION OF Ventas FOR VALUES FROM('2025-11-01') TO ('2025-12-01');
CREATE TABLE ventas_diciembre PARTITION OF Ventas FOR VALUES FROM ('2025-12-01') TO ('2026-01-01');

-- Creación de la tabla donde se van a almacenar los Horarios de cada día del gimnasio (Ex: Lunes 6:00am - 10:00pm).
CREATE TABLE Horarios_por_dia(
id_horario INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
dia VARCHAR(10), -- Se almacena: Lunes, Martes, etc...
hora_apertura TIME, -- La hora que se abre un "X" día (Ex: Lunes se abre a las 6:00am).
hora_cierre TIME, -- La hora que se cierra un "X" día (Ex: Lunes se cierra a las 10:00pm).
anulada BOOLEAN NOT NULL DEFAULT FALSE -- Este campo es opcional y se utiliza para marcar un horario por día como anulado sin eliminarlo de la base de datos.
);

-- Creación de la tabla donde se van a almacenar las asistencias de todos los clientes del gimnasio.
-- Ex: Juan Pereira estuvo presente un lunes 15/03/2022 y entró a las 11:59:17 am, saliendo a la 1:48:12 pm.
CREATE TABLE Asistencia_general(
id_asistencia INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
fecha DATE NOT NULL, 
hora_entrada TIME, -- La hora a la que X usuario ingresó al gimnasio.
anulada BOOLEAN NOT NULL DEFAULT FALSE, -- Este campo es opcional y se utiliza para marcar una asistencia como anulada sin eliminarla de la base de datos.
id_horario INTEGER REFERENCES Horarios_por_dia(id_horario) NOT NULL, -- Para saber que día fue (Lunes, Martes... Y qué horario había ese día).
id_usuario INTEGER REFERENCES Usuarios(id_usuario) NOT NULL, -- Para saber a qué usuario pertenece la asistencia.
PRIMARY KEY (id_asistencia, fecha),
UNIQUE (id_asistencia, fecha)
)PARTITION BY RANGE (fecha); -- PARA PARTICIONAR

-- Mes de enero (concluye hasta el 31 de enero pero se pone el 01 de febrero para poder incluirlo, el 01 de febrero no se incluye en esta partición… y así con las demás particiones).
CREATE TABLE Asistencia_general_m1_2025 PARTITION OF Asistencia_general  
FOR VALUES FROM ('2025-01-01') TO ('2025-02-01');

-- Mes de febrero (incluye hasta  el 28 de febrero)
CREATE TABLE Asistencia_general_m2_2025 PARTITION OF Asistencia_general  
FOR VALUES FROM ('2025-02-01') TO ('2025-03-01');

-- Mes de marzo (incluye hasta el 31 de marzo)
CREATE TABLE Asistencia_general_m3_2025 PARTITION OF Asistencia_general  
FOR VALUES FROM ('2025-03-01') TO ('2025-04-01');

-- Mes de abril (incluye hasta el 30 de abril)
CREATE TABLE Asistencia_general_m4_2025 PARTITION OF Asistencia_general  
FOR VALUES FROM ('2025-04-01') TO ('2025-05-01');

--Mes de mayo (incluye hasta el 31 de mayo)
CREATE TABLE Asistencia_general_m5_2025 PARTITION OF Asistencia_general  
FOR VALUES FROM ('2025-05-01') TO ('2025-06-01');

--Mes de junio (incluye hasta el 30 de junio)
CREATE TABLE Asistencia_general_m6_2025 PARTITION OF Asistencia_general  
FOR VALUES FROM ('2025-06-01') TO ('2025-07-01');

--Mes de julio (incluye hasta  el 31 de julio)
CREATE TABLE Asistencia_general_m7_2025 PARTITION OF Asistencia_general  
FOR VALUES FROM ('2025-07-01') TO ('2025-08-01');

--Mes de agosto (incluye hasta el 31 de agosto)
CREATE TABLE Asistencia_general_m8_2025 PARTITION OF Asistencia_general  
FOR VALUES FROM ('2025-08-01') TO ('2025-09-01');

--Mes de septiembre (incluye hasta  el 30 de septiembre)
CREATE TABLE Asistencia_general_m9_2025 PARTITION OF Asistencia_general  
FOR VALUES FROM ('2025-09-01') TO ('2025-10-01');

--Mes de octubre (incluye hasta el 31 de octubre)
CREATE TABLE Asistencia_general_m10_2025 PARTITION OF Asistencia_general  
FOR VALUES FROM ('2025-10-01') TO ('2025-11-01');

--Mes de noviembre (incluye hasta el 30 de de noviembre)
CREATE TABLE Asistencia_general_m11_2025 PARTITION OF Asistencia_general  
FOR VALUES FROM ('2025-11-01') TO ('2025-12-01');

--Mes de diciembre (incluye hasta el 31 de diciembre)
CREATE TABLE Asistencia_general_m12_2025 PARTITION OF Asistencia_general  
FOR VALUES FROM ('2025-12-01') TO ('2026-01-01');

-- Creación de la tabla donde se almacenan las clases aeróbicas programadas en el gimnasio. 
-- Ex: Clase con capacidad para 20 personas el lunes 15/03/2022 de 08:00 a 09:00 am.
CREATE TABLE Clases_aerobicas(
id_clase INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
capacidad INTEGER,
id_horario INTEGER REFERENCES Horarios_por_dia (id_horario) NOT NULL,  -- Para relacionar cada clase con un horario específico.
hora_inicio TIME,
hora_fin TIME
);

-- Creación de la tabla para almacenar las asistencias a las clases aeróbicas del gimnasio. -- Ex: Juan Pereira asistió a la clase de Zumba el lunes 15/03/2022.
CREATE TABLE Asistencia_clases_aerobicas(
id_asistencia_aerobica INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
fecha DATE NOT NULL, 
anulada BOOLEAN NOT NULL DEFAULT FALSE, -- Este campo es opcional y se utiliza para marcar una asistencia como anulada sin eliminarla de la base de datos.
id_clase INTEGER REFERENCES Clases_aerobicas (id_clase) NOT NULL, -- Relación con la clase aeróbica asistida.
id_usuario INTEGER REFERENCES Usuarios (id_usuario) NOT NULL -- Relación con el usuario que asistió.
);

-- VISTAS----------------------------------------------------------------------------------------------------- USUARIOS ACTIVOS
-- La vista devolverá todos los usuarios, tanto aquellos con membresías activas (saldrán como TRUE en la columna membresia_activa) como los que ya se les venció (saldrán con FALSE en la columna membresia_activa).
CREATE OR REPLACE VIEW Usuarios_Activos AS  
SELECT 
    u.id_usuario,
    u.nombre,
    u.apellido,
    u.fecha_inicio_membresia,
    u.fecha_fin_membresia,
    m.nombre AS tipo_membresia,
    m.duracion,
  	(u.fecha_fin_membresia >= CURRENT_DATE) AS membresia_activa
FROM 
    Usuarios u
JOIN 
    Membresias m ON u.id_membresia = m.id_membresia;


-- PROBAR LA VISTA
-- Consulta de la vista "Usuarios_Activos"
--SELECT * FROM Usuarios_Activos;

--  FILTRA RESULTADOS ESPECÍFICOS:
-- Mostrar solo los usuarios con membresía vencida
--SELECT * FROM Usuarios_Activos WHERE membresia_activa= FALSE;

-- Mostrar sólo los usuarios con membresía activa
-- SELECT * FROM Usuarios_Activos WHERE membresia_activa= TRUE;


---------------------------------------------------------------------------------------------------------------------------STOCK BAJO

-- VISTA PARA SABER LOS PRODUCTOS QUE EL STOCK SEA BAJO (QUE QUEDAN MENOS DE 10 DISPONIBLES)
CREATE VIEW Productos_stock_bajo AS
SELECT
id_producto,
nombre, 
cantidad_disponible,
(cantidad_disponible * 100 / (SELECT MAX(cantidad_disponible) FROM PRODUCTOS)
) AS porcentaje_stock
FROM Productos 
WHERE cantidad_disponible <= 10;

-- PARA PROBARLA:
-- SELECT * FROM Productos_stock_bajo;
-- INSERT INTO Ventas (cantidad, fecha, total, id_usuario, id_producto) VALUES
-- (41, '2025-01-31', 254.26, 2, 2);

--------------------------------------------------------------------------------- USUARIO POR MEMBRESIA Y MONTO GENERADO POR MEMBRESIA
-- VISTA PARA Usuarios por membresía:
CREATE OR REPLACE VIEW Usuarios_por_membresia AS
SELECT M.nombre AS membresia,
		M.precio,
		COUNT(U.id_usuario) AS cantidad_usuarios,
		COUNT(U.id_usuario) * M.precio AS monto_total_generado
FROM Usuarios U
JOIN Membresias M ON U.id_membresia = M.id_membresia
GROUP BY M.nombre, M.precio;

-- PARA PROBARLA
-- SELECT * FROM Usuarios_por_membresia;

------------------------------------------------------------------------------------------------------------------------------------------
-- FUNCIONES: 
------------------------------------------------------------------------- CALCULAR AUTOMATICAMENTE LA FECHA DEL FIN DE UNA MEMBRESIA.
CREATE OR REPLACE FUNCTION calcular_fecha_fin_membresia()
RETURNS TRIGGER AS $$
DECLARE
	dur INTERVAL;
BEGIN

	-- Solo recalcular si es un INSERT o si se modificó fecha_inicio_membresia o id_membresia
	IF(TG_OP = 'INSERT') OR
	(NEW.fecha_inicio_membresia IS DISTINCT FROM OLD.fecha_inicio_membresia) OR
	(NEW.id_membresia IS DISTINCT FROM OLD.id_membresia) THEN

	-- Obtener duración en días desde la tabla Membresias
	SELECT duracion INTO dur
	FROM Membresias
	WHERE id_membresia = NEW.id_membresia;

	-- Calcular nueva fecha de fin
	NEW.fecha_fin_membresia := NEW.fecha_inicio_membresia + dur;

	END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- CREAR EL TRIGGER QUE EJECUTE ESTA FUNCIÓN:
CREATE OR REPLACE TRIGGER calcular_fecha_fin_membresia_trigger
BEFORE INSERT OR UPDATE ON Usuarios
FOR EACH ROW EXECUTE FUNCTION calcular_fecha_fin_membresia();

-- Para ver todos los usuarios y mirar si se actualizo correctamente.
-- SELECT * FROM Usuarios;

-- Probar insertar un nuevo usuario para ver si calcula su fecha fin correctamente.
-- INSERT INTO Usuarios (nombre, apellido, fecha_nacimiento, email, fecha_inicio_membresia, id_membresia) 
-- VALUES ('Kevin', 'Martinez', '2005-09-24', 'KevinMar@gmail.com', '2025-07-29', 2);

-- Probar actualizar un usuario existente (actualizando su fecha inicia de membresia o cambiando su id_membresia)
-- UPDATE Usuarios
-- SET fecha_inicio_membresia = '2025-08-01', -- Le ponemos su nueva fecha de inicio en el 1 de agosto de 2025.
--	id_membresia = 4 -- Cambios su membresia de 15 días a 3 meses.
-- WHERE id_usuario = 11;

---------------------------------------------------------------------- ------------------------------------- ACTUALIZAR STOCK
-- TRIGGER Y FUNCIÓN TRIGGER PARA ACTUALIZAR EL STOCK AUTOMATICAMENTE LUEGO DE VENDER UN PRODUCTO
-- FUNCIÓN QUE RETORNA UN TRIGGER
CREATE OR REPLACE FUNCTION actualizar_stock()
RETURNS TRIGGER AS $$
BEGIN

IF(SELECT cantidad_disponible FROM Productos WHERE id_producto = NEW.id_producto) < NEW.cantidad
THEN RAISE EXCEPTION 'Lo sentimos, pero no contamos con % existencias del producto.', NEW.cantidad;
END IF;

  UPDATE Productos 
  SET cantidad_disponible = cantidad_disponible - NEW.cantidad
  WHERE id_producto = NEW.id_producto;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- CREAR EL TRIGGER
CREATE TRIGGER trg_actualizar_stock
AFTER INSERT ON Ventas
FOR EACH ROW EXECUTE FUNCTION actualizar_stock();

--SELECT * FROM Productos; para ver los productos actualizados.

----------------------------------------------------------------------CALCULAR TOTAL DE UNA VENTA
-- Funcion para calcular automáticamente el total de ventas —----------------------------------------
CREATE OR REPLACE FUNCTION calcular_total_ventas()
RETURNS TRIGGER AS $$
BEGIN  -- Calcular el total usando la cantidad y el precio del producto
	NEW.total := NEW.cantidad * (SELECT precio FROM Productos WHERE id_producto = NEW.id_producto);
	RETURN NEW;
END;
$$ LANGUAGE plpgsql;
-- Crear el trigger para ejecutar la función antes del INSERT
CREATE TRIGGER trg_calcular_total BEFORE INSERT OR UPDATE ON Ventas FOR EACH ROW EXECUTE FUNCTION calcular_total_ventas();
--Para probar:
-- Insertar en Ventas (el trigger calculará automáticamente el total)
--INSERT INTO Ventas (cantidad, fecha, id_usuario, id_producto) VALUES (3, '2025-02-01', 1, 5); -- Compra de 3 scoops de proteína
--select * from Ventas; -- para verificar
---------------------------------------------------------------------------------------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------------------------REGISTRAR ASISTENCIA 
-- (NO LA USAMOS ACTUALMENTE, PORQUE DELEGAMOS ESTA LÓGICA A JAVA.)
-- Funcion que registra una asistencia automaticamente simplemente escribiendo el id del usuario y del horario en que llegó.
-- CREATE OR REPLACE FUNCTION registrar_asistencia (func_id_usuario INTEGER, func_id_horario INTEGER) 
--RETURNS VOID AS $$
--BEGIN
--IF NOT EXISTS( --Valida que el usuario tenga una membresia activa antes de registrar la asistencia
--	SELECT 1 FROM Usuarios u WHERE u.id_usuario = func_id_usuario AND u.fecha_fin_membresia >= CURRENT_DATE -- 1 hace referencia al primer parámetro de la función
--) THEN 
--	RAISE EXCEPTION 'El usuario no tiene una membresía activa';
--END IF;

-- Inserta en la tabla principal para que PostgreSQL maneje la partición automáticamente
--    INSERT INTO Asistencia_general (fecha, hora_entrada, id_horario, id_usuario)
--    VALUES (CURRENT_DATE, CURRENT_TIME, func_id_horario, func_id_usuario);
--END;
--$$ LANGUAGE plpgsql;

--SELECT registrar_asistencia(3, 1); -- 3 es id del usuario y el 1 el id del horario
-- SELECT * FROM Asistencia_general; -- para revisar que sí se registró la asistencia

---------------------------------------------------------------------------------------------------------------------------------------------------------------------

--INDICES: ------------------------------------------------------------------------------------ INDICE EMAIL ID
-- localizan rápidamente el registro sin leer toda la tabla, reduciendo el tiempo de respuesta de varios segundos a milisegundos.
--Índice compuesto que optimiza las consultas tanto por el identificador único (id_usuario) como por el correo (email).
-- CREATE INDEX idx_usuario_email_id 
-- ON Usuarios (email, id_usuario);

--consultas para el índice idx_usuario_email_id
-- Búsqueda por correo electrónico
--SELECT * FROM Usuarios WHERE email = 'ana.gonzalez@email.com';

-- Búsqueda por id de usuario
-- SELECT * FROM Usuarios WHERE id_usuario = 1;

-- Combinación de criterios (si fuera útil)
--SELECT * FROM Usuarios WHERE email = 'juan.pereira@example.com' AND id_usuario = 1;
------------------------------------------------------------------------------------ OBTENER STOCK
 --función para obtener la cantidad_disponible de un producto
 CREATE OR REPLACE FUNCTION obtener_stock_producto(id_producto INTEGER)
 RETURNS INTEGER AS $$
 DECLARE
 	stock INTEGER;
 BEGIN
	-- Consultar la cantidad disponible del producto
	SELECT cantidad_disponible INTO stock
	FROM Productos WHERE Productos.id_producto = obtener_stock_producto.id_producto;
	-- Si el producto no existe, devolver un error
    IF NOT FOUND THEN
	RAISE EXCEPTION 'EL producto con id % no existe ', id_producto;
	END IF;
	RETURN stock;
 END;
 $$ LANGUAGE plpgsql;
 
-- Llamar a la función para obtener el stock de un producto específico
-- SELECT obtener_stock_producto(1); -- Cambia el 1 por el id del producto que se desea consultar

---------------------------------------------------------------------- ACTUALIZAR STOCK Y VALIDARLO AUTOMÁTICAMENTE
-- Su función es el poder modificar el stock actual de un producto. (Ex: si teniamos 0 de X producto, pero ahora llenamos existencias de nuevo,
-- mandamos a llamar al método y pasamos como parámetro el id del producto y su nuevo stock)
CREATE OR REPLACE FUNCTION actualizar_stock(v_id_producto INTEGER, v_nuevo_stock INTEGER) 
RETURNS VOID AS $$
BEGIN
    -- Validar ANTES de cualquier operación
    IF v_nuevo_stock < 0 THEN
        RAISE EXCEPTION 'No puedes asignar valores negativos al stock.';
    END IF;

    -- Actualizar el stock si pasa la validación
    UPDATE Productos 
    SET cantidad_disponible = v_nuevo_stock
    WHERE id_producto = v_id_producto;
END;
$$ LANGUAGE plpgsql;

-- PARA PROBARLO:
-- SELECT * FROM Productos;
-- SELECT actualizar_stock(1, 25);

-- PROCEDIMIENTOS ALMACENADOS------------------------------------------------------------------- CREAR PARTICIONES DINÁMICAMENTE (ASISTENCIAS)
CREATE OR REPLACE PROCEDURE crear_particiones_asistencias(v_fecha_inicio DATE, v_numero_de_meses INT)
LANGUAGE plpgsql AS $$
	DECLARE
		mes DATE;
		i INT;
	BEGIN
	
		FOR i IN 0..(v_numero_de_meses - 1) LOOP
				mes := v_fecha_inicio + INTERVAL '1 month' * i;
		EXECUTE format(
			'CREATE TABLE IF NOT EXISTS asistencias_%s PARTITION OF Asistencia_general FOR VALUES FROM (%L) TO (%L)', 
				to_char(mes, 'YYYY_MM'),
				mes,
				mes + INTERVAL '1 month'
		);
		RAISE NOTICE 'Partición "asistencias_%s" creada correctamente.', to_char(mes, 'YYYY_MM');
	END LOOP;
	END;
	$$;

-- Para probarlo:
-- CALL crear_particiones_asistencias('2026-01-01', 6);
-- El primer parámetro es cuando empiza la primer partición y el segundo el número de particiones que se harán en meses. (Ex: si se pone 12, 
-- Se harán particiones para los 12 meses)

------------------------------------------------------------------------------------------- CREAR PARTICIONES DINÁMICAMENTE (VENTAS)
CREATE OR REPLACE PROCEDURE crear_particiones_ventas(v_fecha_inicio DATE, v_numero_de_meses INT)
LANGUAGE plpgsql AS $$
	DECLARE
		mes DATE;
		i INT;
	BEGIN
	
		FOR i IN 0..(v_numero_de_meses - 1) LOOP
				mes := v_fecha_inicio + INTERVAL '1 month' * i;
		EXECUTE format(
			'CREATE TABLE IF NOT EXISTS ventas_%s PARTITION OF Ventas FOR VALUES FROM (%L) TO (%L)', 
				to_char(mes, 'YYYY_MM'),
				mes,
				mes + INTERVAL '1 month'
		);
		RAISE NOTICE 'Partición "ventas_%s" creada correctamente.', to_char(mes, 'YYYY_MM');
	END LOOP;
	END;
	$$;

-- Para probarlo:
-- CALL crear_particiones_ventas('2026-01-01', 6);
-- El primer parámetro es cuando empiza la primer partición y el segundo el número de particiones que se harán en meses. (Ex: si se pone 12, 
-- Se harán particiones para los 12 meses)



-- INSERTAR DATOS:----------------------------------------------------------------------------------- 
-- Inserciones para Roles
INSERT INTO Roles (role_name) VALUES 
('USER'),
('ADMIN'),
('SUPER_ADMIN');

INSERT INTO Accounts(email, password_hash, enabled, locked, token_version, last_login, email_verified) VALUES
('admin@gym.com', '$2a$10$gozyVie/mXRfsKA44Vsu0OWw3OFYvG76JR.1XoysPcbD9uYknvW4a', TRUE, FALSE, 0, null, TRUE);

INSERT INTO auth_providers(account_id, provider, provider_id) VALUES
('1', 'LOCAL', null);

INSERT INTO account_roles(id_account, id_role) VALUES
(1, 3);
