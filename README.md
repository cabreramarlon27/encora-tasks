# Task Manager Application

This is a simple task manager application built with Spring Boot (Backend) and Angular (Frontend). It allows users to:

- Sign up for an account.
- Log in to manage their tasks.
- Create, read, update, and delete tasks.
- Filter and sort tasks by status, due date, etc.

## Project Structure

The project consists of two main parts:

- **`backend/task-manager-service`**: This directory contains the Spring Boot backend application.
- **`frontend/task-manager-app`**: This directory contains the Angular frontend application.

## Running the Application with Docker Compose

The easiest way to run the application is using Docker Compose. Follow these steps:

1. **Prerequisites:**
    - Make sure you have Docker and Docker Compose installed on your system.

2. **Navigate to the Project Root:**
   ```bash
   cd /path/to/your/project
   ```

3. **Start the Application:**
   ```bash
   docker-compose up
   ```

This command will build and start both the backend and frontend applications in separate Docker containers. The frontend will be accessible at `http://localhost:4200`, and the backend will be accessible at `http://localhost:8080`.

## Running the Applications Separately

If you prefer to run the backend and frontend applications separately, you can follow the instructions below.

### Backend (Spring Boot)

**Technologies Used:**

- Java
- Spring Boot
- Spring Security (JWT Authentication)
- Spring Data MongoDB
- MongoDB

**Running the Backend:**

1. **Prerequisites:**
    - Java 17 or higher
    - Maven
    - MongoDB (running locally or remotely)

2. **Build the Project:**
   ```bash
   cd backend/task-manager-service
   mvn clean install
   ```

3. **Run the Application:**
   ```bash
   mvn spring-boot:run
   ```
   The backend will start on `http://localhost:8080`.

**API Endpoints:**

## API Endpoints

### Authentication

| HTTP Method | Endpoint           | Description                                  |
|-------------|--------------------|----------------------------------------------|
| POST        | /api/auth/signup  | Register a new user                         |
| POST        | /api/auth/signin  | Authenticate and get a JWT token            |
| POST        | /api/auth/refresh | Refresh the JWT token                       |

### Users

| HTTP Method | Endpoint                      | Description                                                              |
|-------------|-------------------------------|--------------------------------------------------------------------------|
| PATCH        | /api/users/me/notifications    | Update the user's notification preferences (enabled/disabled)           |
| GET         | /api/users/me/notifications    | Get the user's current notification status (enabled/disabled)            |
| GET         | /api/users/me/                | Get the user's information                                              |

### Tasks

| HTTP Method | Endpoint      | Description                                                              |
|-------------|----------------|--------------------------------------------------------------------------|
| GET         | /api/tasks     | Get a list of tasks for the authenticated user                          |
| GET         | /api/tasks/{id} | Get a specific task by ID (only accessible to the task owner)            |
| POST        | /api/tasks     | Create a new task (authenticated user is set as the owner)              |
| PUT         | /api/tasks/{id} | Update an existing task (only accessible to the task owner)             |
| DELETE      | /api/tasks/{id} | Delete a task (only accessible to the task owner)                        |

### WebSocket Endpoints

| Protocol    | Endpoint                  | Description                                                                 |
|-------------|---------------------------|-----------------------------------------------------------------------------|
| WebSocket   | /topic/taskNotifications | Receive real-time task notifications (e.g., task overdue, due soon)        |

**Technologies Used:**

- TypeScript
- Angular
- Angular Material (UI)
- Angular HTTP Client
- JWT (JSON Web Token) for Authentication

**Running the Frontend:**

1. **Prerequisites:**
    - Node.js and npm

2. **Install Dependencies:**
   ```bash
   cd frontend/task-manager-app
   npm install
   ```

3. **Run the Application:**
   ```bash
   ng serve
   ```
   The frontend will start on `http://localhost:4200`.

**Key Features:**

- **User Authentication:** Secure user registration and login with JWT authentication.
- **Task Management:** Create, view, edit, and delete tasks.
- **User-Specific Tasks:** Users can only manage their own tasks.

**Future Improvements:**

- **Task Prioritization:** Implement a system for prioritizing tasks.
- **User Interface Enhancements:** Improve the overall look and feel of the application.

This README provides a basic overview of the project. For more detailed information, refer to the code and comments within the backend and frontend directories.