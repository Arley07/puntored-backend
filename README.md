## Endpoints

### POST /api/auth/token
Devuelve un token Bearer para autenticación

### GET /api/suppliers
Requiere header: `Authorization: Bearer {token}`
Devuelve lista de proveedores

# Compilar y ejecutar
mvn clean install
mvn spring-boot:run



# Gestión de Transacciones - Prueba Técnica Puntored

Este módulo registra en base de datos **todas las transacciones** enviadas al endpoint `/api/buy`, incluyendo:
- **Exitosas** → `SUCCESS`
- **Fallidas** → `FAILED` (por validaciones internas o errores del API de Puntored)

---

## 📦 Requisitos Previos

- **Java 17** o superior
- **Maven 3.8+**
- **MySQL 8+**
- **Spring Boot** (ya incluido en el proyecto)
- Acceso al API de Puntored (`x-api-key` y URL)

---

## ⚙️ Configuración de Base de Datos

En `src/main/resources/application.properties` ya está configurada la conexión a MySQL:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/puntored?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
