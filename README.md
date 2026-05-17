# Daily Sugar Consumption Tracking System

This project is a Spring Boot web application for tracking daily sugar consumption. It provides user registration, login, a protected dashboard, and CRUD actions for sugar-consumption entries. The application uses MySQL for persistence, Thymeleaf for server-side rendering, and Spring Security for authentication.

## Architecture Overview

The application follows a classic layered Spring Boot architecture:

### 1. Entry Point and Bootstrapping

- The main application entry point is the Spring Boot class in `src/main/java/com/example/sistem_pencatatan_konsumsi_gula_harian/SistemPencatatanKonsumsiGulaHarianApplication.java`.
- Spring Boot starts the web server, wires the beans, connects to MySQL, and loads the schema from `src/main/resources/schema.sql` during startup.

### 2. Security Layer

- `SecurityConfig` defines the authentication rules.
- Public routes: `/login`, `/register`, and static assets such as `/css/**`, `/js/**`, and `/images/**`.
- All other routes require authentication.
- Login uses a custom form page, and logout clears the session and redirects to `/login?logout`.
- Passwords are encoded with `BCryptPasswordEncoder`.

### 3. Web / Controller Layer

The controllers handle HTTP requests and prepare data for the Thymeleaf views.

- `AuthController`
  - Serves the login page.
  - Handles registration form rendering and submission.
  - Validates password confirmation and rejects duplicate usernames.
- `DashboardController`
  - Serves the dashboard at `/` and `/dashboard`.
  - Resolves the selected date, prevents future-date selection, and loads the authenticated user’s consumption summary.
  - Prepares the dashboard model with the daily total, consumption list, status, and form data.
- `SugarConsumptionController`
  - Handles add, edit, delete, and fragment loading for sugar-consumption entries.
  - Returns Thymeleaf fragment content for the edit form.
  - Uses flash attributes for success and error messages.

### 4. Service Layer

The service layer contains the business rules.

- `AuthService`
  - Registers new users.
  - Checks whether a username already exists.
  - Encodes passwords before saving.
  - Implements `UserDetailsService` so Spring Security can load users during login.
  - Assigns `ROLE_USER` to authenticated users.
- `SugarConsumptionService`
  - Adds, updates, deletes, and queries sugar-consumption data.
  - Validates input values before saving.
  - Rejects negative amounts and future timestamps.
  - Aggregates consumption per day.
  - Compares the daily total against the application limit of 50 grams and marks the day as normal or above limit.

### 5. Persistence Layer

- `UserRepository` and `SugarConsumptionRepository` extend Spring Data JPA repositories.
- They provide query methods for:
  - Finding users by username.
  - Checking whether a username already exists.
  - Finding consumption entries within a date range.
  - Finding or deleting a consumption entry owned by a specific user.

### 6. Domain Model

The main entities are:

- `User`
  - Represents registered application users.
  - Has a one-to-many relation to sugar consumption entries.
- `SugarConsumption`
  - Stores the consumption amount, description, timestamp, and owner user.

Supporting DTOs keep the web layer separate from the persistence model:

- `RegisterForm`
- `SugarConsumptionForm`
- `DailyConsumptionDetail`

An enum named `ConsumptionStatus` represents the daily consumption state.

### 7. View Layer

The UI is rendered with Thymeleaf templates under `src/main/resources/templates`:

- `login.html`
- `register.html`
- `dashboard.html`
- `fragments/consumption-form-fragment.html`

Static frontend assets are stored under `src/main/resources/static`.

### 8. Database Layer

The project uses MySQL 8.4.

- `users` stores login and profile data.
- `sugar_consumptions` stores each logged consumption entry and references `users.user_id` through a foreign key.

The schema is defined in `src/main/resources/schema.sql`, and Hibernate is configured to validate the schema rather than recreate it.

## Project Structure

The important folders in the repository are:

- `src/main/java/.../config` - security and application configuration.
- `src/main/java/.../controllers` - web endpoints and page flow.
- `src/main/java/.../services` - business rules and aggregation logic.
- `src/main/java/.../repositories` - Spring Data JPA access to MySQL.
- `src/main/java/.../entities` - JPA entities mapped to database tables.
- `src/main/java/.../dtos` - request and response models used by the web layer.
- `src/main/java/.../enums` - shared enum values.
- `src/main/resources/templates` - Thymeleaf pages and fragments.
- `src/main/resources/static` - static CSS, JavaScript, and image assets.
- `src/main/resources/schema.sql` - database schema initialization script.
- `src/test/java/...` - unit and integration tests.

## Runtime Configuration

The local application configuration is defined in `src/main/resources/application.properties`:

- Application name: `sistem-pencatatan-konsumsi-gula-harian`
- MySQL URL: `jdbc:mysql://localhost:3307/gula_db`
- Username: `root`
- Password: `root_password`
- Hibernate mode: `validate`
- SQL initialization: enabled

Important note: the app expects MySQL to be available before it starts.

## How to Run

### Prerequisites

- Java 21
- Maven wrapper included in the repository
- Docker and Docker Compose

### 1. Run the Database with Docker

This repository uses Docker Compose to start only the MySQL database.

1. Make sure the environment variables required by `docker-compose.yml` are available:
   - `MYSQL_DATABASE`
   - `MYSQL_USER`
   - `MYSQL_PASSWORD`
   - `MYSQL_ROOT_PASSWORD`
2. Start the database:

```bash
docker-compose up -d
```

3. The database will be exposed on port `3307` on your machine and mapped to port `3306` inside the container.

### 2. Run the Application with Java / Maven

After MySQL is running, start the Spring Boot app from the project root:

```bash
./mvnw spring-boot:run
```

If you prefer to build first and then run the generated JAR:

```bash
./mvnw clean package
java -jar target/*.jar
```

### 3. Access the App

Open the application in your browser after startup and use the login or registration page to begin.

## Data and Business Rules

- New users can register from the `/register` page.
- Passwords are stored as BCrypt hashes.
- The dashboard shows sugar consumption for a selected day.
- Consumption entries cannot use future timestamps.
- The daily total is compared against a 50-gram limit.
- Records belong to the authenticated user only.

## Tests

The project includes unit and integration tests under `src/test/java`.

Run the full test suite with:

```bash
./mvnw test
```

## Notes

- The repository currently contains the MySQL Compose setup, but no application Docker image or application container definition.
- The schema is initialized from SQL, so if you need a clean local database you may need to remove the Docker volume before starting MySQL again.
- `HELP.md` contains the same run sequence and is kept as a reference document.