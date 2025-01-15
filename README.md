# URL Shortener Built with Kotlin and Spring Boot WebFlux

## Project Overview

This is a simple URL shortener application built using **Kotlin** and **Spring Boot WebFlux**. The app allows users to register, log in, create shortened URLs, and set expiry dates for the URLs. Expired URLs are automatically removed from the database.

### Key Features:
- **User Registration & Login**: Allows users to create an account and log in.
- **Short URL Creation**: Users can generate shortened URLs from long URLs.
- **URL Expiry**: Users can set an expiry date for the shortened URLs. Expired URLs will be automatically deleted from the database.
- **Built with Kotlin & Spring Boot WebFlux**: Uses reactive programming to handle requests efficiently.

### Technologies Used:
- Kotlin
- Spring Boot WebFlux
- PostgreSQL (for URL storage)
- JWT (for user authentication)

---

## Prerequisites

Before running the project, ensure that you have the following installed:

- **Java 17** (or later)
- **Gradle**
- **PostgreSQL** (or another relational database if preferred)
- **Postman** (for testing endpoints, optional)

---

## Setup Instructions

### 1. Clone the Repository

Clone the repository to your local machine:

```bash
git clone https://github.com/AdeshinaFalade/url-shortener.git
```



### 2. Create the `application.properties` File

To run the application, create the `application.properties` file in the `src/main/resources` folder. This file contains sensitive information like database connection details and JWT secrets, so make sure to configure it with your own values.

Here’s an example `application.properties` file:

```properties
# Database connection
spring.r2dbc.url=r2dbc:postgresql://localhost
spring.r2dbc.username=your_db_username
spring.r2dbc.password=your_db_password

# SQL initialization
spring.sql.init.schema-locations=classpath:/db/migration/schema.sql
spring.sql.init.data-locations=classpath:/db/migration/data.sql
spring.sql.init.mode=always

# JWT secret and expiration
jwt.secret=your_jwt_secret
jwt.expiration=600000
```

### 3. Build the Project

Navigate to the root of the project and build the project using Gradle. Open a terminal in your project directory and run the following command:

```bash
./gradlew build
```

### 4. Run the Application

After the build process completes successfully, you can run the application. Use the following Gradle command to start the application:

```bash
./gradlew bootRun
```


