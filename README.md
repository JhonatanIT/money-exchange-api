

# Money Exchange API

A reactive Spring Boot application for currency exchange operations with JWT authentication.

## Features

- Currency exchange rate management
- Secure authentication using JWT
- Reactive endpoints using Spring WebFlux
- H2 in-memory database with R2DBC
- OpenAPI documentation (Swagger UI)

## Technologies

- Java
- Spring Boot
- Spring Security WebFlux
- Spring Data R2DBC
- H2 Database
- JWT Authentication
- Maven
- OpenAPI 3.0

## Getting Started

### Prerequisites

- JDK 17 or higher
- Maven 3.6 or higher

### Installation

1. Clone the repository:
```bash
git clone https://github.com/JhonatanIT/money-exchange-api.git
```

2. Navigate to the project directory:
```bash
cd money-exchange-api
```

3. Build the project:
```bash
mvn clean install
```

4. Run the application:
```bash
mvn spring-boot:run
```

The application will start on port 8080.

## API Documentation

Access the Swagger UI documentation at:
- http://localhost:8080/swagger-ui.html

API documentation in JSON format:
- http://localhost:8080/api-docs

## Database

H2 Console is available at:
- http://localhost:8080/h2-console

Credentials:
- JDBC URL: `jdbc:h2:mem:exchangedb`
- Username: `sa`
- Password: `` (empty)

## API Endpoints

### Public Endpoints
- `POST /api/auth/**` - Authentication endpoints
- `GET /api/exchange-rates/**` - View exchange rates

### Protected Endpoints
- `POST /api/exchange` - Perform currency exchange (Authenticated users)
- `POST /api/exchange-rates/**` - Manage exchange rates (Admin only)
- `PUT /api/exchange-rates/**` - Update exchange rates (Admin only)
- `DELETE /api/exchange-rates/**` - Delete exchange rates (Admin only)

## Security

The application uses JWT (JSON Web Token) for authentication. Include the JWT token in the Authorization header for protected endpoints:

```
Authorization: Bearer <your-token>
```

## Configuration

Key application properties:

- Server port: 8080
- H2 Database: In-memory
- JWT expiration: 24 hours
- JWT secret: Configured in application.properties
