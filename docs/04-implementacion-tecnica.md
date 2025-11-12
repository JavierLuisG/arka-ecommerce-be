# Implementación técnica

## 1. Introducción

Este documento detalla cómo se materializan las decisiones arquitectónicas del proyecto Arka Backend en la
implementación técnica. Cubre la elección de tecnologías, la configuración del entorno, las estrategias de persistencia,
seguridad, manejo de errores y pruebas, así como las prácticas y convenciones adoptadas para garantizar calidad,
mantenibilidad y facilidad de despliegue.

## 2. Tecnologías y dependencias

El proyecto utiliza un stack moderno basado en **Spring Boot 3.5.6** y **Java 21**, garantizando compatibilidad,
seguridad y rendimiento.  
A continuación se describen las dependencias más relevantes y el motivo de su elección.

### Gestión del proyecto y compilación

- **Gradle**: herramienta de automatización que simplifica la gestión de dependencias y tareas del ciclo de vida.
- **Spring Dependency Management Plugin**: asegura la coherencia y estabilidad de las versiones utilizadas en el
  proyecto.

### Framework principal y entorno de ejecución

- **Spring Boot 3.5.6**: base del proyecto, permite crear aplicaciones monolíticas con configuración mínima y alta
  productividad.
- **Java 21 (LTS)**: versión estable con soporte a largo plazo, mejoras de rendimiento y nuevas características del
  lenguaje.

### Persistencia y base de datos

- **Spring Data JPA**: facilita el acceso a datos y reduce la complejidad del código al trabajar con entidades y
  repositorios.
- **PostgreSQL**: base de datos relacional robusta, estable y con amplia compatibilidad con el ecosistema Spring.
- **Flyway**: gestiona las migraciones de base de datos, asegurando consistencia en los entornos de desarrollo y
  producción.

### Seguridad y autenticación

- **Spring Security**: proporciona un marco integral para manejar autenticación, autorización y protección frente a
  ataques comunes.
- **JJWT (io.jsonwebtoken)**: permite la creación y validación de tokens JWT, asegurando un flujo de autenticación
  seguro y escalable.

### Validación y mensajería

- **Spring Boot Starter Validation**: permite validar datos de entrada mediante anotaciones, garantizando integridad y
  consistencia.
- **Spring Boot Starter Mail**: habilita el envío de correos electrónicos automatizados, útil para notificaciones y
  confirmaciones.

### Utilidades

- **Lombok**: reduce el código repetitivo generando automáticamente constructores, getters, setters y otros métodos
  comunes.

### Pruebas

- **Spring Boot Starter Test**: incluye herramientas para pruebas unitarias e integración dentro del ecosistema Spring.
- **Spring Security Test**: permite probar endpoints protegidos simulando autenticaciones y roles.
- **JUnit Platform Launcher**: ejecuta las pruebas con compatibilidad para múltiples motores de test.

---

### Tabla resumen de dependencias

| Dependencia                    | Versión        | Propósito principal                |
|--------------------------------|----------------|------------------------------------|
| Spring Boot Starter Actuator   | 3.5.6          | Monitoreo y métricas               |
| Spring Boot Starter Data JPA   | 3.5.6          | Persistencia de datos              |
| Spring Boot Starter Validation | 3.5.6          | Validación de datos                |
| Spring Boot Starter Web        | 3.5.6          | Creación de servicios REST         |
| Spring Boot Starter Mail       | 3.5.6          | Envío de correos electrónicos      |
| Spring Boot Starter Security   | 3.5.6          | Autenticación y autorización       |
| JJWT (api, impl, jackson)      | 0.12.7         | Manejo de tokens JWT               |
| Flyway Core                    | 11.15.0        | Migraciones de base de datos       |
| Flyway Database PostgreSQL     | 11.15.0        | Soporte específico para PostgreSQL |
| Lombok                         | Última estable | Reducción de código repetitivo     |
| PostgreSQL Driver              | Última estable | Conexión con la base de datos      |
| Spring Boot Starter Test       | 3.5.6          | Pruebas unitarias e integración    |
| Spring Security Test           | 3.5.6          | Pruebas de seguridad               |
| JUnit Platform Launcher        | 1.10+          | Ejecución de pruebas               |

## 3. Configuración del entorno

El entorno de ejecución se gestiona mediante perfiles y archivos de configuración que permiten adaptar el comportamiento
de la aplicación según el contexto (desarrollo o producción).

Cada archivo YAML define parámetros clave como conexión a la base de datos, puertos, logs y credenciales externas. Las
variables sensibles se manejan mediante variables de entorno para mantener la seguridad y la portabilidad.

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

### Descripción general:

- ***application.yml*** ->
  Configuración base compartida por todos los perfiles.

- ***application-dev.yml*** ->
  Parámetros orientados al entorno local, con base de datos y servicios de prueba.

- ***application-prod.yml*** ->Configuración para producción, optimizada para seguridad, rendimiento y despliegue
  remoto.

## 4. Persistencia de datos

La capa de persistencia gestiona toda la comunicación entre el dominio y la base de datos.
Se implementa utilizando Spring Data JPA, que simplifica el acceso a datos mediante repositorios y entidades mapeadas.

### Componentes principales:

- ***Entity*** -> Representa las tablas de la base de datos mediante clases del dominio persistentes.

- ***Repository*** -> Interfaces que heredan de JpaRepository para realizar operaciones CRUD sin necesidad de código SQL
  explícito.

- ***Mapper*** -> Transforma entidades en modelos de dominio o DTOs, asegurando la separación entre capas.

- ***Adapter*** -> Implementa los puertos de salida definidos en application.port.out, estableciendo el puente entre el
  dominio y la infraestructura.

- ***Flyway (db.migatrion/)*** -> Controla la versión y evolución del esquema de base de datos mediante scripts SQL
  automáticos.

> Este enfoque garantiza independencia entre la lógica de negocio y los detalles técnicos de la persistencia,
> facilitando pruebas y mantenibilidad.

## 5. Seguridad

El sistema implementa un esquema de seguridad basado en Spring Security con JWT (JSON Web Token) para la autenticación y
autorización de usuarios.

### Componentes clave:

- ***JwtService*** -> Genera, valida y gestiona los tokens JWT utilizados en cada petición autenticada.

- ***JwtAuthFilter*** -> Intercepta las solicitudes entrantes para verificar la validez del token antes de permitir el
  acceso a los recursos protegidos.

- ***SecurityConfig*** -> Define las reglas de acceso, rutas públicas, políticas de autorización y configuración del
  filtro de seguridad.

- ***UserAuthService/UserAuthController*** -> Gestionan el inicio de sesión y el registro de usuarios, devolviendo un
  token JWT al autenticarse correctamente.

- ***UserDetailsImpl y UserRole*** -> Permiten controlar los permisos y privilegios según el rol del usuario (por
  ejemplo, ADMIN o CUSTOMER).

> Este enfoque asegura que solo los usuarios autenticados y autorizados puedan acceder a los recursos según su rol,
> manteniendo la integridad y confidencialidad del sistema.

## 6. Manejo de excepciones y validaciones

El sistema implementa un manejo centralizado de errores y validaciones para garantizar respuestas consistentes y una
experiencia controlada ante fallos.

### Componentes principales:

- ***GlobalExceptionHandler*** ->
  Clase central que captura y gestiona las excepciones lanzadas en la aplicación. Retorna respuestas personalizadas con
  el código HTTP y mensaje apropiado.

- ***Excepciones personalizadas (domain.exception)*** ->
  Representan errores específicos del negocio, como recursos no encontrados, estados inválidos o conflictos de datos.

- ***Validaciones con Jakarta Validation*** ->
  Se aplican anotaciones como @NotNull, @NotBlank, @Size, @Email, entre otras, dentro de los DTOs para garantizar la
  integridad de los datos de entrada.

- ***ErrorResponseDto y ErrorListResponseDto*** ->
  Estructuran las respuestas de error, proporcionando información clara y legible al cliente.

> Este enfoque promueve la claridad, mantenibilidad y robustez, evitando comportamientos inesperados ante errores o
> datos inválidos.

## 7. Pruebas

Las pruebas del sistema se realizaron de forma manual utilizando Postman, con el objetivo de verificar el correcto
funcionamiento de los endpoints y la coherencia de las respuestas del backend.

### Enfoque adoptado:

- ***Colección de pruebas en Postman*** ->
  Se diseñaron y ejecutaron solicitudes HTTP (GET, POST, PUT, DELETE) para validar los flujos principales de cada módulo
  del sistema: productos, usuarios, órdenes, clientes, proveedores y autenticación.

- ***Validación de respuestas*** ->
  Se verificaron códigos de estado HTTP, tiempos de respuesta, formato del cuerpo (JSON) y mensajes de error.

- ***Pruebas de autenticación*** ->
  Se comprobó la generación y uso del token JWT, validando los permisos según roles y asegurando el acceso restringido a
  endpoints protegidos.

> Este método permitió garantizar que todas las funcionalidades principales del backend respondieran correctamente antes
> del despliegue en entorno productivo.

## Consideraciones finales

La arquitectura del backend de Project Arka prioriza la claridad, mantenibilidad y escalabilidad.
Las decisiones técnicas adoptadas garantizan un sistema modular, seguro y adaptable a futuras ampliaciones sin
comprometer su estabilidad.