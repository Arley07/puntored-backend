## 📦 Requisitos Previos

- **Java 17** o superior
- **Maven 3.8+**
- **MySQL 8+**
- **Spring Boot** (ya incluido en el proyecto)
- Acceso al API de Puntored (`x-api-key` y URL)

---
# 📌 Puntored Backend

Backend en **Spring Boot** para la gestión de recargas y transacciones con integración a Puntored.  
Incluye autenticación JWT, manejo de usuarios con roles, y persistencia en MySQL.

# Compilar y ejecutar en local modo dev
mvn clean install
mvn spring-boot:run "-Dspring-boot.run.profiles=dev"

## Funcionalidades

- Autenticación mediante **JWT** (`/api/auth/login`).
- Gestión de usuarios con roles (`ROLE_ADMIN`).
- Registro de transacciones exitosas y fallidas (`SUCCESS` / `FAILED`).
- Creación de transacciones manuales.
- Integración con el API de Puntored (`/api/buy`).
- Listado y filtrado de transacciones.
- Soft delete de transacciones.


## ⚙️ Configuración de Base de Datos

En `src/main/resources/application-dev.yml` ya está configurada la conexión a MySQL:

spring.datasource.url=jdbc:mysql://localhost:3306/puntored?useSSL=false&serverTimezone=UTC
spring.datasource.username=your_user
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect


## Usuario Administrador por Defecto
Estos valores pueden cambiarse como varaibles de entorno en `src\main\resources\application-dev.yml`
Usuario: admin
Contraseña: admin123*
Rol: ROLE_ADMIN