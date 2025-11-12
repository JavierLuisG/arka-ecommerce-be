# Overview

## 1. Proyecto

**Nombre:** arka-backend  
**Descripción breve:** Backend monolítico para Arka, empresa colombiana distribuidora de accesorios para PC, orientado a
gestionar ventas, inventario y compras de manera automatizada.

## 2. Problema a resolver

Arka enfrenta desafíos operativos por el crecimiento de su negocio:

- Gestión manual del inventario y control de stock insuficiente.
- Incidentes de sobreventa debido a alta concurrencia de pedidos.
- Falta de reportes de compras y ventas que dificultan la toma de decisiones.
- Necesidad de autogestión para clientes y reducción de carritos abandonados.
- Escalabilidad limitada para expandirse a otros países de Latinoamérica.

El sistema busca optimizar procesos, automatizar ventas, controlar inventario y facilitar la gestión de compras,
mejorando la experiencia de clientes y reduciendo tiempos de operación.

## 3. Alcance funcional actual

El proyecto implementa un **MVP funcional** con los siguientes módulos:

- **Clientes:** CRUD y gestión de documentación asociada.
- **Productos y Categorías:** CRUD, stock y relaciones con proveedores.
- **Carrito de Compras:** creación, modificación y gestión de estados (activo, abandonado, checkout).
- **Órdenes:** creación, confirmación, pago, envío y entrega, con control de estados.
- **Pagos:** integración con distintos métodos, control de estados y reintentos.
- **Compras a proveedores:** creación, confirmación, recepción y cierre de compras.
- **Roles y Seguridad:** ADMIN, MANAGER, PURCHASES y CUSTOMER con JWT para autenticación y autorización.

## 4. Tecnologías y stack

- **Lenguaje:** Java 21
- **Framework:** Spring Boot 3.5.6
- **Seguridad:** Spring Security + JWT (JJWT)
- **Persistencia:** Spring Data JPA, PostgreSQL
- **Migraciones de BD:** Flyway
- **Validaciones:** Spring Validation
- **Correo:** Spring Boot Mail
- **Pruebas:** Spring Boot Test, JUnit, Spring Security Test
- **Otras:** Lombok para reducción de boilerplate, Actuator para monitoreo

## 5. Estado de despliegue

Actualmente el proyecto **no está desplegado**. Se planea dockerización y despliegue en **AWS**.

## 6. Roles implementados

- **ADMIN:** gestión completa del sistema.
- **MANAGER:** supervisión de procesos y reportes.
- **PURCHASES:** gestión de compras y proveedores.
- **CUSTOMER:** gestión de su propio carrito y órdenes.

## 7. Repositorio

[GitHub - Arka E-commerce Backend](https://github.com/JavierLuisG/arka-ecommerce-be)

## 8. Audiencia

El documento está orientado a **stakeholders** y **equipo técnico**, permitiendo comprensión de los objetivos, alcance y
estado actual del sistema.  
