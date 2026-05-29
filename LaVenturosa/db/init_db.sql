-- Script SQL
CREATE TABLE IF NOT EXISTS medicion (
    id BIGSERIAL PRIMARY KEY, 
    variable TEXT NOT NULL,
    valor FLOAT8,
    fecha_hora TIMESTAMP,
    estado TEXT,
    punto_monitoreo TEXT
);

CREATE TABLE IF NOT EXISTS umbral (
    id BIGSERIAL PRIMARY KEY, 
    variable VARCHAR(255) NOT NULL,
    punto_monitoreo TEXT,
    min_critico FLOAT8,
    min_advertencia FLOAT8,
    max_advertencia FLOAT8,
    max_critico FLOAT8
);

CREATE TABLE IF NOT EXISTS configuracion_alarma (
    id BIGSERIAL PRIMARY KEY, 
    email_destinatario VARCHAR(255) NOT NULL,
    nivel_notificacion VARCHAR(100),
    activo BOOL
);
