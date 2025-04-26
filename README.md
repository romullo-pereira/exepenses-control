## Description
Expense Control is a financial management application designed to help users track their expenses, manage categories, and authenticate securely using JWT. The application is built with modern technologies, ensuring scalability, security, and ease of use.

## Project Structure
The project follows a modular structure to separate concerns and improve maintainability:

```
src/
├── main/
│   ├── kotlin/
│   │   ├── com.romullo.pereira.expensensecontrol/
│   │   │   ├── domain/
│   │   │   │   ├── exception/       # Custom exceptions
│   │   │   │   ├── model/           # Domain models (e.g., User, LoginRequest)
│   │   │   │   ├── service/         # Business logic (e.g., AuthService)
│   │   │   ├── infrastructure/
│   │   │   │   ├── config/          # Configuration files (e.g., logging)
│   │   │   │   ├── persistence/     # Repositories (e.g., UserRepository)
│   │   │   │   ├── security/        # Security-related classes (e.g., JWT)
│   │   │   ├── application/         # Controllers and REST endpoints
│   ├── resources/
│       ├── application.yml          # Application configuration
├── test/
    ├── kotlin/                      # Unit and integration tests
```

## Technologies
The project uses the following technologies:
- **Kotlin**: Primary programming language.
- **Spring Boot**: Framework for building the application.
    - Spring Security: For authentication and authorization.
    - Spring Data MongoDB: For database interactions.
    - Spring Web: For building REST APIs.
- **MongoDB**: NoSQL database for storing user and expense data.
- **JWT (JSON Web Tokens)**: For secure authentication.
- **Gradle**: Build and dependency management tool.
- **Testcontainers**: For integration testing with MongoDB and Kafka.

## Setup
Follow these steps to set up the project locally:

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd expense-control
   ```

2. **Install dependencies**:
   Ensure you have Java 21 and Gradle installed. Then, run:
   ```bash
   ./gradlew build
   ```

3. **Configure the application**:
   Update the `application.yml` file in the `resources` directory with your MongoDB connection details and other environment-specific configurations.

4. **Run the application**:
   Start the application using:
   ```bash
   ./gradlew bootRun
   ```

5. **Access the application**:
   The application will be available at `http://localhost:8080`.

## Testing
The project includes unit and integration tests. To run the tests, execute:

```bash
./gradlew test
```

### Test Features
- **Unit Tests**: Validate individual components like services and utilities.
- **Integration Tests**: Test the interaction between components using Testcontainers for MongoDB and Kafka.

Test reports will be generated in the `build/reports/tests` directory.