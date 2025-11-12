# Documentación de Endpoints API

Este documento contiene la lista de todos los endpoints REST del backend, incluyendo método HTTP, ruta, descripción,
parámetros, body, respuesta y roles autorizados.
Está pensado para servir como referencia rápida para desarrollo frontend, testing y mantenimiento de la API.

## Controladores disponibles

El sistema cuenta con los siguientes controladores:

- `CategoryController`: Gestión de categorías de productos.
- `ProductController`: Gestión de productos.
- `DocumentController`: Gestión de documentos.
- `CustomerController`: Gestión de clientes.
- `CartController`: Gestión de carritos de compras.
- `CartItemController`: Gestión de items en el carrito.
- `OrderController`: Gestión de pedidos.
- `OrderItemController`: Gestión de items de pedidos.
- `SupplierController`: Gestión de proveedores.
- `PurchaseController`: Gestión de compras a proveedores.
- `PurchaseItemController`: Gestión de items de compras.
- `PaymentController`: Gestión de pagos.
- `NotificationController`: Gestión de notificaciones.
- `UserAuthController`: Gestión de autenticación de usuarios.
- `UserController`: Gestión de usuarios.

Cada controlador expone endpoints REST que permiten realizar operaciones CRUD y otras acciones específicas según el
recurso.

### CategoryController

| Método HTTP | Ruta                         | Descripción                                               | Parámetros                  | Body                | Response                             | Roles Autorizados |
|-------------|------------------------------|-----------------------------------------------------------|-----------------------------|---------------------|--------------------------------------|-------------------|
| POST        | /api/categories              | Crear una nueva categoría                                 | Ninguno                     | `CreateCategoryDto` | `CategoryResponseDto` (201 CREATED)  | ADMIN, MANAGER    |
| GET         | /api/categories/{id}         | Obtener categoría por ID                                  | `id` (UUID)                 | Ninguno             | `CategoryResponseDto` (200 OK)       | Todos             |
| GET         | /api/categories/name/{name}  | Obtener categoría por nombre                              | `name` (String)             | Ninguno             | `CategoryResponseDto` (200 OK)       | Todos             |
| GET         | /api/categories              | Obtener todas las categorías (opcional filtro por status) | `status` (String, opcional) | Ninguno             | List<`CategoryResponseDto`> (200 OK) | Todos             |
| PUT         | /api/categories/{id}         | Actualizar descripción de la categoría                    | `id` (UUID)                 | `UpdateCategoryDto` | `CategoryResponseDto` (200 OK)       | ADMIN, MANAGER    |
| DELETE      | /api/categories/{id}         | Eliminar categoría (soft delete)                          | `id` (UUID)                 | Ninguno             | `MessageResponseDto` (200 OK)        | ADMIN, MANAGER    |
| PUT         | /api/categories/{id}/restore | Restaurar categoría eliminada                             | `id` (UUID)                 | Ninguno             | `CategoryResponseDto` (200 OK)       | ADMIN, MANAGER    |

### ProductController

| Método HTTP | Ruta                                | Descripción                              | Parámetros                         | Body                         | Response                   | Roles autorizados |
|-------------|-------------------------------------|------------------------------------------|------------------------------------|------------------------------|----------------------------|-------------------|
| POST        | `/api/products`                     | Crear un nuevo producto                  | Ninguno                            | `CreateProductDto`           | `ProductResponseDto`       | ADMIN, MANAGER    |
| GET         | `/api/products/{id}`                | Obtener producto por ID                  | `id` (UUID)                        | Ninguno                      | `ProductResponseDto`       | Todos             |
| GET         | `/api/products/sku/{sku}`           | Obtener producto por SKU                 | `sku` (String)                     | Ninguno                      | `ProductResponseDto`       | Todos             |
| GET         | `/api/products`                     | Obtener todos los productos o por estado | `status` (opcional, ProductStatus) | Ninguno                      | List<`ProductResponseDto`> | Todos             |
| GET         | `/api/products/{id}/availability`   | Verificar disponibilidad de stock        | `id` (UUID), `quantity` (int)      | Ninguno                      | `CheckProductResponseDto`  | ADMIN, MANAGER    |
| PUT         | `/api/products/{id}`                | Actualizar campos del producto           | `id` (UUID)                        | `UpdateFieldsProductDto`     | `ProductResponseDto`       | ADMIN, MANAGER    |
| PUT         | `/api/products/{id}/categories`     | Actualizar categorías del producto       | `id` (UUID)                        | `UpdateProductCategoriesDto` | `ProductResponseDto`       | ADMIN, MANAGER    |
| PUT         | `/api/products/{id}/decrease-stock` | Disminuir stock de un producto           | `id` (UUID)                        | `ModifyStockRequestDto`      | `MessageResponseDto`       | ADMIN, MANAGER    |
| PUT         | `/api/products/{id}/increase-stock` | Incrementar stock de un producto         | `id` (UUID)                        | `ModifyStockRequestDto`      | `MessageResponseDto`       | ADMIN, MANAGER    |
| DELETE      | `/api/products/{id}`                | Eliminar producto (soft delete)          | `id` (UUID)                        | Ninguno                      | `MessageResponseDto`       | ADMIN, MANAGER    |
| PUT         | `/api/products/{id}/restore`        | Restaurar producto eliminado             | `id` (UUID)                        | Ninguno                      | `ProductResponseDto`       | ADMIN, MANAGER    |

---

### DocumentController

| Método HTTP | Ruta                             | Descripción                               | Parámetros                          | Body                 | Response                    | Roles autorizados |
|-------------|----------------------------------|-------------------------------------------|-------------------------------------|----------------------|-----------------------------|-------------------|
| GET         | `/api/documents/{id}`            | Obtener documento por ID                  | `id` (UUID)                         | Ninguno              | `DocumentResponseDto`       | ADMIN, MANAGER    |
| GET         | `/api/documents/number/{number}` | Obtener documento por número              | `number` (String)                   | Ninguno              | `DocumentResponseDto`       | ADMIN, MANAGER    |
| GET         | `/api/documents`                 | Obtener todos los documentos o por estado | `status` (opcional, DocumentStatus) | Ninguno              | List<`DocumentResponseDto`> | ADMIN, MANAGER    |
| PUT         | `/api/documents/{id}`            | Actualizar documento                      | `id` (UUID)                         | `DocumentRequestDto` | `DocumentResponseDto`       | ADMIN             |

### CustomerController

| Método HTTP | Ruta                             | Descripción                                                        | Parámetros                          | Body                      | Response                    | Roles autorizados           |
|-------------|----------------------------------|--------------------------------------------------------------------|-------------------------------------|---------------------------|-----------------------------|-----------------------------|
| POST        | `/api/customers`                 | Crear un nuevo cliente                                             | Ninguno                             | `CreateCustomerDto`       | `CustomerResponseDto`       | ADMIN, CUSTOMER             |
| GET         | `/api/customers/{id}`            | Obtener cliente por ID (solo propietario o roles)                  | `id` (UUID)                         | Ninguno                   | `CustomerResponseDto`       | ADMIN, MANAGER, Propietario |
| GET         | `/api/customers/user/{userId}`   | Obtener cliente por ID de usuario (solo propietario o roles)       | `userId` (UUID)                     | Ninguno                   | `CustomerResponseDto`       | ADMIN, MANAGER, Propietario |
| GET         | `/api/customers/number/{number}` | Obtener cliente por número de documento (solo propietario o roles) | `number` (String)                   | Ninguno                   | `CustomerResponseDto`       | ADMIN, MANAGER, Propietario |
| GET         | `/api/customers`                 | Obtener todos los clientes o por estado                            | `status` (opcional, CustomerStatus) | Ninguno                   | List<`CustomerResponseDto`> | ADMIN, MANAGER              |
| PUT         | `/api/customers/{id}`            | Actualizar información de cliente (solo propietario o ADMIN)       | `id` (UUID)                         | `UpdateFieldsCustomerDto` | `CustomerResponseDto`       | ADMIN, Propietario          |
| DELETE      | `/api/customers/{id}`            | Eliminar cliente (soft delete, solo propietario o ADMIN)           | `id` (UUID)                         | Ninguno                   | `MessageResponseDto`        | ADMIN, Propietario          |
| PUT         | `/api/customers/{id}/restore`    | Restaurar cliente eliminado (solo propietario o ADMIN)             | `id` (UUID)                         | Ninguno                   | `CustomerResponseDto`       | ADMIN, Propietario          |

---

### CartController

| Método HTTP | Ruta                                                       | Descripción                                                   | Parámetros                      | Body                          | Response                | Roles autorizados           |
|-------------|------------------------------------------------------------|---------------------------------------------------------------|---------------------------------|-------------------------------|-------------------------|-----------------------------|
| POST        | `/api/carts`                                               | Crear un nuevo carrito                                        | `customerId` (UUID)             | `CreateCartDto`               | `CartResponseDto`       | ADMIN, Propietario          |
| GET         | `/api/carts/{id}`                                          | Obtener carrito por ID (solo propietario o roles)             | `id` (UUID)                     | Ninguno                       | `CartResponseDto`       | ADMIN, MANAGER, Propietario |
| GET         | `/api/carts`                                               | Obtener todos los carritos o por estado                       | `status` (opcional, CartStatus) | Ninguno                       | List<`CartResponseDto`> | ADMIN, MANAGER              |
| GET         | `/api/carts/customer/{customerId}`                         | Obtener todos los carritos de un cliente                      | `customerId` (UUID)             | Ninguno                       | List<`CartResponseDto`> | ADMIN, MANAGER              |
| GET         | `/api/carts/items/product/{productId}`                     | Obtener todos los carritos que contienen un producto          | `productId` (UUID)              | Ninguno                       | List<`CartResponseDto`> | ADMIN, MANAGER              |
| PUT         | `/api/carts/{id}/product/{productId}/add-item`             | Agregar ítem al carrito (solo propietario o ADMIN)            | `id` (UUID), `productId` (UUID) | `UpdateQuantityToCartItemDto` | `CartResponseDto`       | ADMIN, Propietario          |
| PUT         | `/api/carts/{id}/product/{productId}/update-item-quantity` | Actualizar cantidad de un ítem (solo propietario o ADMIN)     | `id` (UUID), `productId` (UUID) | `UpdateQuantityToCartItemDto` | `CartResponseDto`       | ADMIN, Propietario          |
| PUT         | `/api/carts/{id}/product/{productId}/remove-item`          | Remover ítem del carrito (solo propietario o ADMIN)           | `id` (UUID), `productId` (UUID) | Ninguno                       | `CartResponseDto`       | ADMIN, Propietario          |
| PUT         | `/api/carts/{id}/empty-items`                              | Vaciar todos los ítems del carrito (solo propietario o ADMIN) | `id` (UUID)                     | Ninguno                       | `CartResponseDto`       | ADMIN, Propietario          |
| PUT         | `/api/carts/{id}/checkout`                                 | Confirmar carrito y crear orden (solo propietario o ADMIN)    | `id` (UUID)                     | Ninguno                       | `MessageResponseDto`    | ADMIN, Propietario          |
| DELETE      | `/api/carts/{id}`                                          | Eliminar carrito (solo propietario o ADMIN)                   | `id` (UUID)                     | Ninguno                       | `MessageResponseDto`    | ADMIN, Propietario          |

### CartItemController

| Método HTTP | Ruta                                  | Descripción                                            | Parámetros         | Body    | Response                    | Roles autorizados |
|-------------|---------------------------------------|--------------------------------------------------------|--------------------|---------|-----------------------------|-------------------|
| GET         | `/api/cart-items/{id}`                | Obtener ítem del carrito por ID                        | `id` (UUID)        | Ninguno | `CartItemResponseDto`       | ADMIN, MANAGER    |
| GET         | `/api/cart-items`                     | Obtener todos los ítems del carrito                    | Ninguno            | Ninguno | List<`CartItemResponseDto`> | ADMIN, MANAGER    |
| GET         | `/api/cart-items/product/{productId}` | Obtener todos los ítems del carrito por ID de producto | `productId` (UUID) | Ninguno | List<`CartItemResponseDto`> | ADMIN, MANAGER    |

---

### OrderController

| Método HTTP | Ruta                                                        | Descripción                                                           | Parámetros                       | Body                           | Response                 | Roles autorizados           |
|-------------|-------------------------------------------------------------|-----------------------------------------------------------------------|----------------------------------|--------------------------------|--------------------------|-----------------------------|
| GET         | `/api/orders/{id}`                                          | Obtener orden por ID (solo propietario o roles)                       | `id` (UUID)                      | Ninguno                        | `OrderResponseDto`       | ADMIN, MANAGER, Propietario |
| GET         | `/api/orders`                                               | Obtener todas las órdenes o por estado                                | `status` (opcional, OrderStatus) | Ninguno                        | List<`OrderResponseDto`> | ADMIN, MANAGER              |
| GET         | `/api/orders/customer/{customerId}`                         | Obtener todas las órdenes de un cliente                               | `customerId` (UUID)              | Ninguno                        | List<`OrderResponseDto`> | ADMIN, MANAGER, Propietario |
| GET         | `/api/orders/items/product/{productId}`                     | Obtener todas las órdenes que contienen un producto                   | `productId` (UUID)               | Ninguno                        | List<`OrderResponseDto`> | ADMIN, MANAGER              |
| PUT         | `/api/orders/{id}/product/{productId}/add-item`             | Agregar ítem a la orden (solo propietario o ADMIN)                    | `id` (UUID), `productId` (UUID)  | `UpdateQuantityToOrderItemDto` | `OrderResponseDto`       | ADMIN, Propietario          |
| PUT         | `/api/orders/{id}/product/{productId}/update-item-quantity` | Actualizar cantidad de un ítem en la orden (solo propietario o ADMIN) | `id` (UUID), `productId` (UUID)  | `UpdateQuantityToOrderItemDto` | `OrderResponseDto`       | ADMIN, Propietario          |
| PUT         | `/api/orders/{id}/product/{productId}/remove-item`          | Remover ítem de la orden (solo propietario o ADMIN)                   | `id` (UUID), `productId` (UUID)  | Ninguno                        | `OrderResponseDto`       | ADMIN, Propietario          |
| PUT         | `/api/orders/{id}/confirm`                                  | Confirmar la orden (disminuye stock, crea notificación)               | `id` (UUID)                      | Ninguno                        | `MessageResponseDto`     | ADMIN, Propietario          |
| PUT         | `/api/orders/{id}/pay`                                      | Pagar la orden (actualiza estado y notificación)                      | `id` (UUID)                      | Ninguno                        | `MessageResponseDto`     | ADMIN, Propietario          |
| PUT         | `/api/orders/{id}/shipped`                                  | Marcar orden como enviada                                             | `id` (UUID)                      | Ninguno                        | `MessageResponseDto`     | ADMIN, MANAGER              |
| PUT         | `/api/orders/{id}/deliver`                                  | Marcar orden como entregada                                           | `id` (UUID)                      | Ninguno                        | `MessageResponseDto`     | ADMIN, MANAGER              |
| PUT         | `/api/orders/{id}/cancel`                                   | Cancelar la orden (aumenta stock, crea notificación)                  | `id` (UUID)                      | Ninguno                        | `MessageResponseDto`     | ADMIN, Propietario          |

### OrderItemController

| Método HTTP | Ruta                                   | Descripción                                          | Parámetros         | Body    | Response                     | Roles autorizados |
|-------------|----------------------------------------|------------------------------------------------------|--------------------|---------|------------------------------|-------------------|
| GET         | `/api/order-items/{id}`                | Obtener un OrderItem por ID                          | `id` (UUID)        | Ninguno | `OrderItemResponseDto`       | ADMIN, MANAGER    |
| GET         | `/api/order-items`                     | Obtener todos los OrderItems                         | Ninguno            | Ninguno | List<`OrderItemResponseDto`> | ADMIN, MANAGER    |
| GET         | `/api/order-items/product/{productId}` | Obtener todos los OrderItems asociados a un producto | `productId` (UUID) | Ninguno | List<`OrderItemResponseDto`> | ADMIN, MANAGER    |

---

### SupplierController

| Método HTTP | Ruta                                             | Descripción                                                | Parámetros               | Body          | Response                    | Roles autorizados         |
|-------------|--------------------------------------------------|------------------------------------------------------------|--------------------------|---------------|-----------------------------|---------------------------|
| POST        | `/api/suppliers`                                 | Crear un nuevo proveedor                                   | Ninguno                  | `SupplierDto` | `SupplierResponseDto`       | ADMIN, PURCHASES          |
| GET         | `/api/suppliers/{id}`                            | Obtener proveedor por ID                                   | `id` (UUID)              | Ninguno       | `SupplierResponseDto`       | ADMIN, PURCHASES, MANAGER |
| GET         | `/api/suppliers/email/{email}`                   | Obtener proveedor por email                                | `email`                  | Ninguno       | `SupplierResponseDto`       | ADMIN, PURCHASES, MANAGER |
| GET         | `/api/suppliers/tax-id/{taxId}`                  | Obtener proveedor por NIT/Tax ID                           | `taxId`                  | Ninguno       | `SupplierResponseDto`       | ADMIN, PURCHASES, MANAGER |
| GET         | `/api/suppliers`                                 | Obtener todos los proveedores (opcional filtro por status) | `status` (opcional)      | Ninguno       | List<`SupplierResponseDto`> | ADMIN, PURCHASES, MANAGER |
| GET         | `/api/suppliers/product/{productId}`             | Obtener proveedores de un producto                         | `productId` (UUID)       | Ninguno       | List<`SupplierResponseDto`> | ADMIN, PURCHASES, MANAGER |
| PUT         | `/api/suppliers/{id}`                            | Actualizar campos de un proveedor                          | `id` (UUID)              | `SupplierDto` | `SupplierResponseDto`       | ADMIN, PURCHASES          |
| PUT         | `/api/suppliers/{id}/product/{productId}/add`    | Agregar un producto al proveedor                           | `id`, `productId` (UUID) | Ninguno       | `SupplierResponseDto`       | ADMIN, PURCHASES          |
| PUT         | `/api/suppliers/{id}/product/{productId}/remove` | Remover un producto del proveedor                          | `id`, `productId` (UUID) | Ninguno       | `SupplierResponseDto`       | ADMIN, PURCHASES          |
| DELETE      | `/api/suppliers/{id}`                            | Eliminación lógica de proveedor                            | `id` (UUID)              | Ninguno       | `MessageResponseDto`        | ADMIN, PURCHASES          |
| PUT         | `/api/suppliers/{id}/restore`                    | Restaurar proveedor eliminado                              | `id` (UUID)              | Ninguno       | `SupplierResponseDto`       | ADMIN, PURCHASES          |

### PurchaseController

| Método HTTP | Ruta                                                           | Descripción                                            | Parámetros               | Body                              | Response                    | Roles autorizados         |
|-------------|----------------------------------------------------------------|--------------------------------------------------------|--------------------------|-----------------------------------|-----------------------------|---------------------------|
| POST        | `/api/purchases`                                               | Crear una nueva compra                                 | Ninguno                  | `CreatePurchaseDto`               | `PurchaseResponseDto`       | ADMIN, PURCHASES          |
| GET         | `/api/purchases/{id}`                                          | Obtener compra por ID                                  | `id` (UUID)              | Ninguno                           | `PurchaseResponseDto`       | ADMIN, PURCHASES, MANAGER |
| GET         | `/api/purchases`                                               | Obtener todas las compras (opcional filtro por status) | `status` (opcional)      | Ninguno                           | List<`PurchaseResponseDto`> | ADMIN, PURCHASES, MANAGER |
| GET         | `/api/purchases/supplier/{supplierId}`                         | Obtener todas las compras de un proveedor              | `supplierId` (UUID)      | Ninguno                           | List<`PurchaseResponseDto`> | ADMIN, PURCHASES, MANAGER |
| GET         | `/api/purchases/items/product/{productId}`                     | Obtener todas las compras que incluyen un producto     | `productId` (UUID)       | Ninguno                           | List<`PurchaseResponseDto`> | ADMIN, PURCHASES, MANAGER |
| PUT         | `/api/purchases/{id}/product/{productId}/add-item`             | Agregar un item a la compra                            | `id`, `productId` (UUID) | `UpdateQuantityToPurchaseItemDto` | `PurchaseResponseDto`       | ADMIN, PURCHASES          |
| PUT         | `/api/purchases/{id}/product/{productId}/update-item-quantity` | Actualizar cantidad de un item en la compra            | `id`, `productId` (UUID) | `UpdateQuantityToPurchaseItemDto` | `PurchaseResponseDto`       | ADMIN, PURCHASES          |
| PUT         | `/api/purchases/{id}/product/{productId}/remove-item`          | Remover un item de la compra                           | `id`, `productId` (UUID) | Ninguno                           | `PurchaseResponseDto`       | ADMIN, PURCHASES          |
| PUT         | `/api/purchases/{id}/confirm`                                  | Confirmar la compra                                    | `id` (UUID)              | Ninguno                           | `MessageResponseDto`        | ADMIN, PURCHASES          |
| PUT         | `/api/purchases/{id}/receive`                                  | Recibir la compra                                      | `id` (UUID)              | `ReceivePurchaseDto`              | `MessageResponseDto`        | ADMIN, PURCHASES          |
| PUT         | `/api/purchases/{id}/close`                                    | Cerrar la compra                                       | `id` (UUID)              | Ninguno                           | `MessageResponseDto`        | ADMI                      |

### PurchaseItemController

| Método HTTP | Ruta                                      | Descripción                                               | Parámetros         | Body    | Response                        | Roles autorizados         |
|-------------|-------------------------------------------|-----------------------------------------------------------|--------------------|---------|---------------------------------|---------------------------|
| GET         | `/api/purchase-items/{id}`                | Obtener un item de compra por ID                          | `id` (UUID)        | Ninguno | `PurchaseItemResponseDto`       | ADMIN, PURCHASES, MANAGER |
| GET         | `/api/purchase-items`                     | Obtener todos los items de compra                         | Ninguno            | Ninguno | List<`PurchaseItemResponseDto`> | ADMIN, PURCHASES, MANAGER |
| GET         | `/api/purchase-items/product/{productId}` | Obtener todos los items de compra asociados a un producto | `productId` (UUID) | Ninguno | List<`PurchaseItemResponseDto`> | ADMIN, PURCHASES, MANAGER |

---

### PaymentController

| Método HTTP | Ruta                               | Descripción                                                       | Parámetros                               | Body                     | Response                   | Roles autorizados           |
|-------------|------------------------------------|-------------------------------------------------------------------|------------------------------------------|--------------------------|----------------------------|-----------------------------|
| POST        | `/api/payments`                    | Crear un nuevo pago para una orden confirmada                     | `orderId` en DTO                         | `CreatePaymentDto`       | `PaymentResponseDto`       | ADMIN, Propietario          |
| GET         | `/api/payments/{id}`               | Obtener un pago por su ID (acceso seguro)                         | `id` (UUID)                              | Ninguno                  | `PaymentResponseDto`       | ADMIN, MANAGER, Propietario |
| GET         | `/api/payments/order/{orderId}`    | Obtener el pago asociado a una orden                              | `orderId` (UUID)                         | Ninguno                  | `PaymentResponseDto`       | ADMIN, MANAGER, Propietario |
| GET         | `/api/payments`                    | Obtener todos los pagos con filtros opcionales de método y estado | `method` (opcional), `status` (opcional) | Ninguno                  | List<`PaymentResponseDto`> | ADMIN, MANAGER              |
| PUT         | `/api/payments/{id}/confirm`       | Confirmar un pago, manejando expiración o fallo de monto          | `id` (UUID)                              | Ninguno                  | `PaymentResponseDto`       | ADMIN, CUSTOMER             |
| PUT         | `/api/payments/{id}/change-method` | Cambiar el método de pago de un pago existente                    | `id` (UUID)                              | `UpdatePaymentMethodDto` | `PaymentResponseDto`       | ADMIN, CUSTOMER             |
| PUT         | `/api/payments/{id}/pay-again`     | Reintentar el pago, marcándolo como pendiente                     | `id` (UUID)                              | Ninguno                  | `PaymentResponseDto`       | ADMIN, CUSTOMER             |

---

### NotificationController

| Método HTTP | Ruta                                             | Descripción                                                          | Parámetros                                               | Body    | Response                        | Roles autorizados / Validación |
|-------------|--------------------------------------------------|----------------------------------------------------------------------|----------------------------------------------------------|---------|---------------------------------|--------------------------------|
| GET         | `/api/notifications/{id}`                        | Obtener una notificación por ID                                      | `id` (UUID)                                              | Ninguno | `NotificationResponseDto`       | ADMIN, MANAGER                 |
| GET         | `/api/notifications`                             | Obtener todas las notificaciones, opcionalmente filtradas por estado | `status` (opcional)                                      | Ninguno | List<`NotificationResponseDto`> | ADMIN, MANAGER                 |
| GET         | `/api/notifications/order/{orderId}`             | Obtener todas las notificaciones de una orden                        | `orderId` (UUID)                                         | Ninguno | List<`NotificationResponseDto`> | ADMIN, MANAGER                 |
| GET         | `/api/notifications/customer/{customerId}`       | Obtener todas las notificaciones de un cliente                       | `customerId` (UUID)                                      | Ninguno | List<`NotificationResponseDto`> | ADMIN, MANAGER, Propietario    |
| GET         | `/api/notifications/type/{type}`                 | Obtener todas las notificaciones por tipo                            | `type` (NotificationType)                                | Ninguno | List<`NotificationResponseDto`> | ADMIN, MANAGER                 |
| GET         | `/api/notifications/type/{type}/status/{status}` | Obtener todas las notificaciones por tipo y estado                   | `type` (NotificationType), `status` (NotificationStatus) | Ninguno | List<`NotificationResponseDto`> | ADMIN, MANAGER                 |
| PUT         | `/api/notifications/{id}/mark-read`              | Marcar una notificación como leída                                   | `id` (UUID)                                              | Ninguno | `MessageResponseDto`            | ADMIN, Propietario             |

---

### UserAuthController

| Método HTTP | Ruta                 | Descripción                  | Parámetros | Body          | Response          | Roles autorizados / Validación |
|-------------|----------------------|------------------------------|------------|---------------|-------------------|--------------------------------|
| POST        | `/api/auth/register` | Registrar un nuevo usuario   | Ninguno    | `RegisterDto` | `AuthResponseDto` | Público (sin autenticación)    |
| POST        | `/api/auth/login`    | Autenticarse y obtener token | Ninguno    | `LoginDto`    | `AuthResponseDto` | Público (sin autenticación)    |

### UserController

| Método HTTP | Ruta                             | Descripción                                           | Parámetros                             | Body                | Response                | Roles autorizados / Validación         |
|-------------|----------------------------------|-------------------------------------------------------|----------------------------------------|---------------------|-------------------------|----------------------------------------|
| GET         | `/api/users/{id}`                | Obtener usuario por ID                                | `id` (UUID)                            | Ninguno             | `UserResponseDto`       | ADMIN, MANAGER, PURCHASES, Propietario |
| GET         | `/api/users/username/{userName}` | Obtener usuario por username                          | `userName`                             | Ninguno             | `UserResponseDto`       | ADMIN, MANAGER, PURCHASES, Propietario |
| GET         | `/api/users/email/{email}`       | Obtener usuario por email                             | `email`                                | Ninguno             | `UserResponseDto`       | ADMIN, MANAGER, PURCHASES, Propietario |
| GET         | `/api/users`                     | Obtener todos los usuarios filtrando por rol o estado | `role` (opcional), `status` (opcional) | Ninguno             | List<`UserResponseDto`> | ADMIN, MANAGER                         |
| PUT         | `/api/users/{id}/username`       | Actualizar username de un usuario                     | `id` (UUID)                            | `UpdateUserNameDto` | `UserResponseDto`       | ADMIN, Propietario                     |
| PUT         | `/api/users/{id}/email`          | Actualizar email de un usuario                        | `id` (UUID)                            | `UpdateEmailDto`    | `UserResponseDto`       | ADMIN, Propietario                     |
| PUT         | `/api/users/{id}/password`       | Actualizar contraseña de un usuario                   | `id` (UUID)                            | `UpdatePasswordDto` | `UserResponseDto`       | ADMIN, Propietario                     |
| DELETE      | `/api/users/{id}`                | Eliminación suave de un usuario                       | `id` (UUID)                            | Ninguno             | `MessageResponseDto`    | ADMIN, Propietario                     |
| PUT         | `/api/users/{id}/restore`        | Restaurar un usuario eliminado                        | `id` (UUID)                            | Ninguno             | `UserResponseDto`       | ADMIN                                  |
