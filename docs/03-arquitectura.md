# Arquitectura del Sistema

## 1. Introducción general

El sistema **Arka Backend** se construye bajo una arquitectura **limpia y modular**, siguiendo principios de **Clean
Architecture** y **DDD (Domain Driven Design)**.  
El objetivo es mantener una separación clara entre las capas del dominio, la lógica de aplicación, la infraestructura y
los recursos compartidos, asegurando **bajo acoplamiento**, **alta cohesión** y una estructura **fácilmente escalable y
mantenible**.

Cada capa tiene responsabilidades bien definidas y se comunica a través de interfaces (puertos y adaptadores) que
permiten la independencia entre el dominio de negocio y la infraestructura técnica.

---

## 2. Estructura de paquetes

A continuación, se presenta la estructura principal del proyecto ubicada dentro
de `src/main/java/com.store.arka.backend`, junto con los recursos de configuración y pruebas.

```bash
src/
├── main/
│   ├── java/
│   │   └── com/store/arka/backend/
│   │       ├── application/
│   │       │   ├── port/
│   │       │   │   ├── in/
│   │       │   │   └── out/
│   │       │   └── service/
│   │       ├── domain/
│   │       │   ├── enums/
│   │       │   ├── exception/
│   │       │   └── model/
│   │       ├── infrastructure/
│   │       │   ├── exception/
│   │       │   ├── persistence/
│   │       │   │   ├── adapter/
│   │       │   │   ├── entity/
│   │       │   │   ├── mapper/
│   │       │   │   ├── repository/
│   │       │   │   └── updater/
│   │       │   ├── security/
│   │       │   │   └── jwt/
│   │       │   └── web/
│   │       │     ├── controller/
│   │       │     ├── dto/
│   │       │     │   ├── cart/
│   │       │     │   ├── category/
│   │       │     │   ├── customer/
│   │       │     │   ├── notification/
│   │       │     │   ├── order/
│   │       │     │   ├── payment/
│   │       │     │   ├── product/
│   │       │     │   ├── purchase/
│   │       │     │   ├── supplier/
│   │       │     │   └── user/
│   │       │     └── mapper/
│   │       ├── shared/
│   │       │   ├── security/
│   │       │   └── util/
│   │       └── ArkaBackendApplication.java
│   └── resources/
│     ├── db.migration/
│     ├── application.yml
│     ├── application-dev.yml
│     └── application-prod.yml
└── test/
```

## 3. Capas principales del sistema

El proyecto se estructura bajo el enfoque de **Arquitectura Limpia**, separando responsabilidades en tres capas
principales que garantizan independencia, mantenibilidad y escalabilidad.

```bash
src/
├── main/
│   ├── java/
│   │   └── com/store/arka/backend/
│   │       ├── application/
│   │       ├── domain/
│   │       ├── infrastructure/
│   │       ├── shared/
│   │       └── ArkaBackendApplication.java
│   └── resources/
└── test/
```

- ***application/*** ->
  Encapsula la lógica de aplicación. Define los casos de uso del sistema mediante los servicios y los puertos de entrada
  y salida que permiten la comunicación con otras capas.

- ***domain/*** ->
  Concentra el núcleo del negocio. Contiene las entidades, enumeraciones y excepciones que representan las reglas
  esenciales del dominio sin depender de detalles técnicos externos.

- ***infrastructure/*** ->
  Gestiona las implementaciones técnicas necesarias para que la aplicación funcione: persistencia, controladores web,
  seguridad y manejo de excepciones. Esta capa conecta el dominio con el entorno externo del sistema.

- ***shared/*** ->
  Contiene utilidades y componentes transversales, como validaciones, funciones de seguridad y herramientas de soporte
  que pueden ser reutilizadas por distintas capas sin generar dependencias circulares.

## 4. Subdivisiones internas de cada capa

A continuación se detallan las subcarpetas principales dentro de cada capa del proyecto y su propósito general.

### **application**

```bash
src/
├── main/
│   ├── java/
│   │   └── com/store/arka/backend/
│   │       ├── application/
│   │       │   ├── port/
│   │       │   │   ├── in/
│   │       │   │   └── out/
│   │       │   └── service/
│   │       ├── domain/
│   │       ├── infrastructure/
│   │       ├── shared/
│   │       └── ArkaBackendApplication.java
│   └── resources/
└── test/
```

- ***application/port/in/*** ->
  Define los **casos de uso** del sistema mediante interfaces que exponen la lógica de aplicación a los controladores u
  otras capas externas.

- ***application/port/out/*** ->
  Declara las **interfaces de salida** que permiten la comunicación con servicios externos, repositorios o sistemas de
  terceros.

- ***application/service/*** ->
  Contiene las **implementaciones de los casos de uso**, orquestando la interacción entre el dominio y las dependencias
  externas.

---

### **domain**

```bash
src/
├── main/
│   ├── java/
│   │   └── com/store/arka/backend/
│   │       ├── application/
│   │       ├── domain/
│   │       │   ├── enums/
│   │       │   ├── exception/
│   │       │   └── model/
│   │       ├── infrastructure/
│   │       ├── shared/
│   │       └── ArkaBackendApplication.java
│   └── resources/
└── test/
```

- ***domain/entity/*** ->
  Agrupa las **entidades del núcleo del negocio**, modelando los objetos principales del dominio y sus reglas.

- ***domain/enums/*** ->
  Define los **valores constantes o enumeraciones** que representan estados, roles o categorías dentro del dominio.

- ***domain/exception/*** ->
  Centraliza las **excepciones específicas del dominio**, garantizando una gestión coherente de errores.

---

### **infrastructure**

```bash
src/
├── main/
│   ├── java/
│   │   └── com/store/arka/backend/
│   │       ├── application/
│   │       ├── domain/
│   │       ├── infrastructure/
│   │       │   ├── exception/
│   │       │   ├── persistence/
│   │       │   │   ├── adapter/
│   │       │   │   ├── entity/
│   │       │   │   ├── mapper/
│   │       │   │   ├── repository/
│   │       │   │   └── updater/
│   │       │   ├── security/
│   │       │   │   └── jwt/
│   │       │   └── web/
│   │       │     ├── controller/
│   │       │     ├── dto/
│   │       │     │   ├── cart/
│   │       │     │   ├── category/
│   │       │     │   ├── customer/
│   │       │     │   ├── notification/
│   │       │     │   ├── order/
│   │       │     │   ├── payment/
│   │       │     │   ├── product/
│   │       │     │   ├── purchase/
│   │       │     │   ├── supplier/
│   │       │     │   └── user/
│   │       │     └── mapper/
│   │       ├── shared/
│   │       └── ArkaBackendApplication.java
│   └── resources/
└── test/
```

- ***infrastructure/exception/*** ->
  Maneja las excepciones globales y las respuestas de error del sistema.

- ***infrastructure/persistence/*** ->
  Implementa la capa de acceso y conexión a la base de datos.
    - ***adapter/*** ->
      Contiene los adaptadores que conectan la aplicación con la persistencia.
    - ***entity/***  ->
      Define las entidades JPA que representan las tablas del sistema.
    - ***mapper/*** ->
      Realiza la conversión entre entidades y modelos de dominio.
    - ***repository/*** ->
      Implementa los repositorios que gestionan operaciones CRUD.
    - ***updater/*** ->
      Contiene clases que actualizan entidades de forma controlada.

- ***infrastructure/security/***  
  Configura la seguridad, autenticación y autorización del sistema.
    - ***jwt/*** ->
      Gestiona la creación, validación y filtrado de tokens JWT.

- ***infrastructure/web/*** ->
  Expone la API y gestiona la comunicación HTTP.
    - ***controller/*** ->
      Define los controladores REST que manejan las peticiones.
    - ***dto/*** ->
      Contiene los objetos de transferencia de datos (DTO) agrupados por módulo, cada uno con su correspondiente request
      y response.
    - ***mapper/*** ->
      Convierte entre DTOs y modelos de dominio o entidades.

---

### **shared**

```bash
src/
├── main/
│   ├── java/
│   │   └── com/store/arka/backend/
│   │       ├── application/
│   │       ├── domain/
│   │       ├── infrastructure/
│   │       ├── shared/
│   │       │   ├── security/
│   │       │   └── util/
│   │       └── ArkaBackendApplication.java
│   └── resources/
└── test/
```

- **shared/security/** ->  
  Contiene **utilidades y funciones ** para la autenticacion de usuarios.

- **shared/util/** ->
  Incluye **utilidades y funciones de soporte** reutilizables entre capas, como validadores o formateadores.

---

### **resources**

Contiene los archivos de configuración y recursos necesarios para la ejecución del sistema.

```bash
src/
├── main/
│   ├── java/
│   │   └── com/store/arka/backend/
│   └── resources/
│     ├── db.migration/
│     ├── application.yml
│     ├── application-dev.yml
│     └── application-prod.yml
└── test/
```

- ***resources/db.migration/*** ->
  Almacena los scripts de migración de base de datos gestionados por Flyway o similar.

- ***resources/application.yml*** ->
  Configuración principal del sistema, incluyendo propiedades comunes a todos los entornos.

- ***resources/application-dev.yml*** ->
  Configuración específica para el entorno de desarrollo (por ejemplo, base de datos local o logs extendidos).

- ***resources/application-prod.yml*** ->
  Configuración para el entorno de producción, optimizada para rendimiento y seguridad.

## 5. Flujo de ejecución de una petición

El flujo de una petición dentro del sistema sigue el principio de **dependencias dirigidas hacia el dominio**,
garantizando que las capas internas no dependan de detalles externos.

1. **Controlador (Infrastructure → Web)**  
   Recibe la solicitud HTTP y la envía al caso de uso correspondiente.

2. **Caso de uso / Servicio (Application → Service)**  
   Ejecuta la lógica de aplicación y coordina las operaciones necesarias.

3. **Puerto de salida y Adaptador (Application → Port.Out / Infrastructure → Adapter)**  
   Define e implementa la comunicación con recursos externos, como la base de datos.

4. **Persistencia (Infrastructure → Persistence)**  
   Gestiona las operaciones CRUD mediante repositorios y mapeadores.

5. **Dominio (Domain)**  
   Aplica las reglas de negocio y valida las entidades del sistema.

6. **Respuesta**  
   Los datos retornan hacia el controlador, se transforman en DTOs y se envían al cliente.

> Este recorrido garantiza una separación clara de responsabilidades, facilita las pruebas unitarias y promueve la
> mantenibilidad a largo plazo del sistema.

## 6. Ventajas del enfoque

La arquitectura adoptada ofrece beneficios clave para la sostenibilidad y evolución del proyecto:

- **Mantenibilidad:** Las responsabilidades están claramente separadas, facilitando la comprensión y modificación del
  código.
- **Escalabilidad:** Permite incorporar nuevas funcionalidades o módulos sin afectar la estructura existente.
- **Reutilización:** Los componentes pueden ser utilizados en diferentes contextos sin generar dependencias
  innecesarias.
- **Testabilidad:** La independencia entre capas favorece la creación de pruebas unitarias y de integración.
- **Flexibilidad tecnológica:** Es posible reemplazar implementaciones técnicas (por ejemplo, la base de datos o el
  framework web) sin alterar la lógica del dominio.
- **Claridad estructural:** La organización en capas mejora la legibilidad y estandariza el desarrollo entre equipos.  
