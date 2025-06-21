# Authentication Service

A Spring Boot-based authentication service with JWT token support.

## Features

- User registration and login
- JWT token authentication
- Password change functionality
- Document upload capability
- PostgreSQL database integration

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL database

## Database Setup

1. Create a PostgreSQL database named `cex_db`
2. Create a user `cex_user` with password `SecretPass123`
3. Grant all privileges on `cex_db` to `cex_user`

```sql
CREATE DATABASE cex_db;
CREATE USER cex_user WITH PASSWORD 'SecretPass123';
GRANT ALL PRIVILEGES ON DATABASE cex_db TO cex_user;
```

## Running the Application

1. Navigate to the project directory:
   ```bash
   cd eri_branch
   ```

2. Run the application:
   ```bash
   mvn spring-boot:run
   ```

   Or if you have an IDE like IntelliJ IDEA:
   - Open the project
   - Run `AuthApplication.java`

3. The application will start on `http://localhost:8080`

## API Endpoints

### Public Endpoints (No Authentication Required)

- `GET /health` - Health check
- `POST /register` - User registration
- `POST /login` - User login

### Protected Endpoints (Authentication Required)

- `POST /editPassword` - Change password
- `POST /users/{id}/uploadDocument` - Upload document
- `GET /userId` - Get current user ID

## Testing the Application

### 1. Health Check
```bash
curl http://localhost:8080/health
```
Expected response: `"Auth Service is running!"`

### 2. Register a User
```bash
curl -X POST http://localhost:8080/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "firstName": "Test",
    "lastName": "User",
    "email": "test@example.com"
  }'
```

### 3. Login
```bash
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```
Expected response: `{"token": "eyJhbGciOiJIUzI1NiJ9..."}`

### 4. Use Protected Endpoint
```bash
curl -X GET http://localhost:8080/userId \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Default Test User

The application automatically creates a test user on startup:
- Username: `testuser`
- Password: `password123`

## Configuration

The application configuration is in `src/main/resources/application.properties`:

- Database connection settings
- JWT secret and expiration
- File upload settings
- Logging configuration

## Troubleshooting

1. **Database Connection Error**: Make sure PostgreSQL is running and the database/user are created
2. **Port Already in Use**: Change the port in `application.properties` or stop other services
3. **JWT Token Issues**: Check that the JWT secret is properly configured

## Project Structure

```
src/main/java/com/example/auth/
├── AuthApplication.java          # Main application class
├── config/
│   ├── SecurityConfig.java       # Spring Security configuration
│   └── DataInitializer.java      # Test data initialization
├── domain/
│   ├── User.java                 # User entity
│   └── data/                     # Enums
├── repository/
│   └── UserRepository.java       # Data access layer
├── service/
│   ├── AuthService.java          # Business logic
│   └── CustomUserDetailsService.java # Spring Security integration
├── security/
│   └── JwtAuthFilter.java        # JWT authentication filter
└── web/
    ├── AuthController.java       # REST endpoints
    └── exception/
        └── GlobalExceptionHandler.java # Error handling
``` 