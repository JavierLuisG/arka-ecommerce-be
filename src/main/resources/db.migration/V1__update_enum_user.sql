-- Esta migración actualiza los roles permitidos en la tabla users

-- (1) Normalizamos los valores a mayúsculas
UPDATE users
SET role = UPPER(role)
WHERE role IS NOT NULL AND role <> UPPER(role);

-- (2) Eliminamos la constraint anterior
ALTER TABLE users
  DROP CONSTRAINT IF EXISTS users_role_check;

-- (3) Creamos la nueva constraint con los nuevos roles válidos
ALTER TABLE users
  ADD CONSTRAINT users_role_check
  CHECK (role IN ('ADMIN', 'MANAGER', 'PURCHASES', 'CUSTOMER'));