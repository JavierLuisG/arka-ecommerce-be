# Historias de Usuario, Módulos y Priorización

## 1. Historias de Usuario (HU)

En esta sección se presentan las **14 Historias de Usuario (HU)** que definen los requerimientos funcionales y
expectativas principales del sistema Arka. Cada HU está descrita de manera independiente, mostrando su propósito, rol
involucrado, criterios de aceptación y prioridad, permitiendo entender claramente las funcionalidades clave que el
sistema debe cumplir para soportar la gestión de inventario, ventas, compras, seguridad y reportes.

### HU1 - Registrar productos y categorías del sistema

| Campo                       | Descripción                                                                                                                                                                                                                                                                                                                  |
|-----------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Prioridad**               | Alta                                                                                                                                                                                                                                                                                                                         |
| **Rol**                     | Administrador del sistema                                                                                                                                                                                                                                                                                                    |
| **Qué quiere**              | Registrar categorías y productos con sus características principales                                                                                                                                                                                                                                                         |
| **Para qué**                | Mantener organizado el inventario y permitir su gestión dentro de la aplicación                                                                                                                                                                                                                                              |
| **Criterios de aceptación** | - Crear, editar y eliminar categorías. <br> - Registrar productos con nombre, descripción, precio, stock inicial, estado y categoría asociada. <br> - Categorías vinculadas correctamente con productos (Many-to-Many). <br> - Validación de datos y persistencia en base de datos. <br> - Confirmación de registro exitoso. |

### HU2 - Actualizar y auditar stock de productos

| Campo                       | Descripción                                                                                                                                                                                                                     |
|-----------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Prioridad**               | Alta                                                                                                                                                                                                                            |
| **Rol**                     | Administrador del sistema                                                                                                                                                                                                       |
| **Qué quiere**              | Actualizar la cantidad disponible de los productos                                                                                                                                                                              |
| **Para qué**                | Mantener inventario correcto y evitar sobreventas                                                                                                                                                                               |
| **Criterios de aceptación** | - Modificar stock de un producto existente. <br> - No permitir valores negativos ni actualizaciones sin validación. <br> - Cambios reflejados inmediatamente en la base de datos. <br> - Confirmación de actualización exitosa. |

### HU3 - Registrar clientes con su documentación

| Campo                       | Descripción                                                                                                                                                                                                                                                                                                                                            |
|-----------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Prioridad**               | Alta                                                                                                                                                                                                                                                                                                                                                   |
| **Rol**                     | Responsable del sistema                                                                                                                                                                                                                                                                                                                                |
| **Qué quiere**              | Registrar clientes junto con su tipo y número de documento                                                                                                                                                                                                                                                                                             |
| **Para qué**                | Identificar de forma única a cada cliente y asociarlo a futuras operaciones de compra                                                                                                                                                                                                                                                                  |
| **Criterios de aceptación** | - Registrar diferentes tipos de documentos (DNI, NIT, RUT, etc.) y sus números. <br> - Vinculación uno a uno entre cliente y documento. <br> - Almacenar datos básicos: nombre, correo, teléfono, dirección, ciudad y país. <br> - Validación de campos requeridos antes de guardar. <br> - Persistencia correcta en tablas `documents` y `customers`. |

### HU4 - Flujo de pedido de cliente (carrito, orden y detalle)

| Campo                       | Descripción                                                                                                                                                                                                                                                                                                                                                                                              |
|-----------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Prioridad**               | Alta                                                                                                                                                                                                                                                                                                                                                                                                     |
| **Rol**                     | Cliente del sistema                                                                                                                                                                                                                                                                                                                                                                                      |
| **Qué quiere**              | Agregar productos a un carrito y generar una orden con su detalle                                                                                                                                                                                                                                                                                                                                        |
| **Para qué**                | Realizar un pedido con los productos seleccionados                                                                                                                                                                                                                                                                                                                                                       |
| **Criterios de aceptación** | - Crear un carrito asociado a un cliente existente. <br> - Agregar, actualizar o eliminar productos según disponibilidad. <br> - Confirmar pedido y generar orden con detalle (`Order` y `OrderItem`). <br> - Total calculado a partir de precios y cantidades. <br> - Descontar stock de productos incluidos. <br> - Almacenar información de carrito, orden y detalle coherentemente en base de datos. |

### HU5 - Modificación y cancelación de órdenes

| Campo                       | Descripción                                                                                                                                                                                                                                                                                                                                                |
|-----------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Prioridad**               | Alta                                                                                                                                                                                                                                                                                                                                                       |
| **Rol**                     | Cliente del sistema                                                                                                                                                                                                                                                                                                                                        |
| **Qué quiere**              | Modificar o cancelar una orden pendiente                                                                                                                                                                                                                                                                                                                   |
| **Para qué**                | Corregir errores o desistir del pedido antes de confirmación                                                                                                                                                                                                                                                                                               |
| **Criterios de aceptación** | - Solo modificar/cancelar órdenes con estado creado o pendiente. <br> - Permitir agregar/eliminar productos y ajustar cantidades. <br> - Restablecer stock al eliminar productos o cancelar orden. <br> - Actualizar información en base de datos (`orders` y `order_items`). <br> - Confirmación de modificación/cancelación mediante respuesta estándar. |

### HU6 - Notificación de cambios de estado de pedido

| Campo                       | Descripción                                                                                                                                                                                                                                                                   |
|-----------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Prioridad**               | Media                                                                                                                                                                                                                                                                         |
| **Rol**                     | Cliente del sistema                                                                                                                                                                                                                                                           |
| **Qué quiere**              | Recibir notificación cuando cambie el estado de su pedido                                                                                                                                                                                                                     |
| **Para qué**                | Mantener informado sobre el progreso de su orden                                                                                                                                                                                                                              |
| **Criterios de aceptación** | - Generar notificación cada vez que cambie el estado de la orden. <br> - Vincular notificaciones con la orden correspondiente. <br> - Almacenar información en `notifications`. <br> - Enviar correo al cliente con el nuevo estado. <br> - Confirmar registro/envío exitoso. |

### HU7 - Gestión de proveedores

| Campo                       | Descripción                                                                                                                                                                                                                                                                                                                   |
|-----------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Prioridad**               | Media                                                                                                                                                                                                                                                                                                                         |
| **Rol**                     | Responsable del sistema                                                                                                                                                                                                                                                                                                       |
| **Qué quiere**              | Registrar, consultar y administrar proveedores                                                                                                                                                                                                                                                                                |
| **Para qué**                | Mantener actualizada la información de abastecimiento                                                                                                                                                                                                                                                                         |
| **Criterios de aceptación** | - Crear, editar y eliminar proveedores. <br> - Almacenar información básica: nombre, contacto, teléfono, correo, dirección. <br> - Relacionar proveedores con productos suministrados. <br> - Persistencia en base de datos (`suppliers` y tabla intermedia). <br> - Confirmación de operaciones mediante respuesta estándar. |

### HU8 - Registrar compras a proveedores

| Campo                       | Descripción                                                                                                                                                                                                                                                                    |
|-----------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Prioridad**               | Media                                                                                                                                                                                                                                                                          |
| **Rol**                     | Responsable del sistema                                                                                                                                                                                                                                                        |
| **Qué quiere**              | Registrar compras realizadas a proveedores                                                                                                                                                                                                                                     |
| **Para qué**                | Actualizar inventario y mantener control de abastecimiento                                                                                                                                                                                                                     |
| **Criterios de aceptación** | - Crear compra asociada a proveedor existente. <br> - Incluir uno o varios productos con cantidad y precio. <br> - Incrementar stock de productos según cantidades compradas. <br> - Guardar información en `purchases` y `purchase_items`. <br> - Confirmar creación exitosa. |

### HU9 - Procesar pagos asociados a órdenes

| Campo                       | Descripción                                                                                                                                                                                                                         |
|-----------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Prioridad**               | Media                                                                                                                                                                                                                               |
| **Rol**                     | Cliente del sistema                                                                                                                                                                                                                 |
| **Qué quiere**              | Realizar pago de una orden generada                                                                                                                                                                                                 |
| **Para qué**                | Completar proceso de compra y confirmar pedido                                                                                                                                                                                      |
| **Criterios de aceptación** | - Asociar pago a orden existente. <br> - Registrar monto, método y estado (éxito/fallo). <br> - Actualizar orden a pagado si éxito. <br> - Revertir a pendiente/cancelado si falla. <br> - Guardar información en tabla `payments`. |

### HU10 - Autenticación, registro y roles de usuario

| Campo                       | Descripción                                                                                                                                                                                                                            |
|-----------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Prioridad**               | Media                                                                                                                                                                                                                                  |
| **Rol**                     | Usuario del sistema                                                                                                                                                                                                                    |
| **Qué quiere**              | Registrarse e iniciar sesión en la aplicación                                                                                                                                                                                          |
| **Para qué**                | Acceder a funcionalidades según su rol                                                                                                                                                                                                 |
| **Criterios de aceptación** | - Registrar nuevos usuarios con credenciales. <br> - Generar token JWT al autenticarse. <br> - Roles: ADMIN, MANAGER, PURCHASES, CUSTOMER. <br> - Guardar usuario y rol en base de datos. <br> - Validar credenciales antes de acceso. |

### HU11 - Autorización y seguridad en endpoints

| Campo                       | Descripción                                                                                                                                                                                                                                                            |
|-----------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Prioridad**               | Media                                                                                                                                                                                                                                                                  |
| **Rol**                     | Sistema                                                                                                                                                                                                                                                                |
| **Qué quiere**              | Controlar acceso a endpoints según rol del usuario autenticado                                                                                                                                                                                                         |
| **Para qué**                | Proteger información y permitir solo operaciones autorizadas                                                                                                                                                                                                           |
| **Criterios de aceptación** | - Configurar restricciones de acceso por roles. <br> - Validar token JWT en cada solicitud. <br> - Rechazar peticiones sin autorización con código de error. <br> - Centralizar seguridad mediante Spring Security. <br> - Registrar intentos de acceso no autorizado. |

### HU12 - Identificar carritos abandonados

| Campo                       | Descripción                                                                                                                                                                                                                                                                           |
|-----------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Prioridad**               | Baja                                                                                                                                                                                                                                                                                  |
| **Rol**                     | Responsable del sistema                                                                                                                                                                                                                                                               |
| **Qué quiere**              | Identificar carritos no completados como órdenes                                                                                                                                                                                                                                      |
| **Para qué**                | Analizar comportamiento de clientes y promover recuperación                                                                                                                                                                                                                           |
| **Criterios de aceptación** | - Listar carritos abandonados o sin orden tras cierto tiempo. <br> - Visualizar información básica de cliente y productos añadidos. <br> - Actualizar automáticamente estado de carrito. <br> - Persistir información en `carts`. <br> - Permitir consulta para análisis/seguimiento. |

### HU13 - Generar reportes de productos por abastecer

| Campo                       | Descripción                                                                                                                                                                                                                                                                                                      |
|-----------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Prioridad**               | Baja                                                                                                                                                                                                                                                                                                             |
| **Rol**                     | Administrador o encargado de inventario                                                                                                                                                                                                                                                                          |
| **Qué quiere**              | Generar reportes de productos con stock bajo                                                                                                                                                                                                                                                                     |
| **Para qué**                | Planificar compras y mantener inventario adecuado                                                                                                                                                                                                                                                                |
| **Criterios de aceptación** | - Identificar productos con stock debajo de umbral configurable. <br> - Registrar cambios de stock en `StockHistory`. <br> - Generar reportes CSV o PDF con: nombre producto, stock actual, umbral, fecha de último movimiento, responsable. <br> - Mostrar fecha de generación y usuario que emitió el reporte. |

### HU14 - Generar reportes de ventas semanales

| Campo                       | Descripción                                                                                                                                                                                                                                                                                     |
|-----------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Prioridad**               | Baja                                                                                                                                                                                                                                                                                            |
| **Rol**                     | Administrador o gerente de ventas                                                                                                                                                                                                                                                               |
| **Qué quiere**              | Generar reportes semanales de órdenes completadas                                                                                                                                                                                                                                               |
| **Para qué**                | Analizar rendimiento de ventas, productos más vendidos y clientes más activos                                                                                                                                                                                                                   |
| **Criterios de aceptación** | - Incluir órdenes enviadas o pagadas en rango de fechas. <br> - Mostrar total de ventas, número de órdenes, productos más vendidos, clientes frecuentes. <br> - Generar reportes CSV o PDF. <br> - Registrar fecha y usuario que generó reporte. <br> - Solo accesible a roles ADMIN o MANAGER. |

---

## 2. Módulos del Sistema

El sistema está organizado en **módulos funcionales** que agrupan las historias de usuario según su área de
responsabilidad:

### Módulo 1: Gestión de Inventario y Clientes

- **Descripción:** Permite crear y administrar productos, categorías y clientes con sus documentos, manteniendo el
  inventario actualizado y organizado.
- **Historias de Usuario:** HU1, HU2, HU3

### Módulo 2: Gestión de Ventas (Flujo de Pedido y Pagos)

- **Descripción:** Maneja el ciclo de compra del cliente, desde el carrito hasta la orden, incluyendo modificaciones,
  cancelaciones, notificaciones y pagos.
- **Historias de Usuario:** HU4, HU5, HU6, HU9

### Módulo 3: Gestión de Compras y Proveedores

- **Descripción:** Administra proveedores y las compras realizadas a ellos, actualizando stock y manteniendo control del
  abastecimiento.
- **Historias de Usuario:** HU7, HU8

### Módulo 4: Seguridad y Control de Acceso

- **Descripción:** Gestiona la autenticación, roles y autorización de usuarios, protegiendo los endpoints y garantizando
  acceso según privilegios.
- **Historias de Usuario:** HU10, HU11

### Módulo 5: Reportes y Seguimiento

- **Descripción:** Permite generar reportes de productos por abastecer, reportes de ventas semanales e identificar
  carritos abandonados para análisis y seguimiento.
- **Historias de Usuario:** HU12, HU13, HU14

---

## 3. Priorización de Historias de Usuario

La **priorización** permite organizar el desarrollo según el impacto y la urgencia de cada historia de usuario. Facilita
asignar recursos y planificar releases.

| Historia de Usuario                                       | Prioridad |
|-----------------------------------------------------------|-----------|
| HU1 Registrar productos y categorías del sistema          | Alta      |
| HU2 Actualizar y auditar stock de productos               | Alta      |
| HU3 Registrar clientes con su documentación               | Alta      |
| HU4 Flujo de pedido de cliente (carrito, orden y detalle) | Alta      |
| HU5 Modificación y cancelación de órdenes                 | Alta      |
| HU6 Notificación de cambios de estado de pedido           | Media     |
| HU7 Gestión de proveedores                                | Media     |
| HU8 Registrar compras a proveedores                       | Media     |
| HU9 Procesar pagos asociados a órdenes                    | Media     |
| HU10 Autenticación, registro y roles de usuario           | Media     |
| HU11 Autorización y seguridad en endpoints                | Media     |
| HU12 Identificar carritos abandonados                     | Baja      |
| HU13 Generar reportes de productos por abastecer          | Baja      |
| HU14 Generar reportes de ventas semanales                 | Baja      |
