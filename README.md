# Real-Time Collaborative Note-Taking App

A system designed to facilitate collaborative note-taking and calendar management with friends, featuring real-time updates, user authentication, and integration with Google Calendar. It uses microservices architecture to provide a scalable and resilient environment for seamless user interactions and data synchronization.

### Setup and Deployment

In order to run the project you'll have to get all the docker images and then:
1. Run all the Docker Containers:
```
docker-compose up -d
```

2. Then access all the endpoints through the gataway on the port:
```
http://0.0.0.0:8000
```

3. Start by First! registering an account on the endpoint:
```
http://0.0.0.0:8000/auth/register
```
with the following body format:

```json
{
  "username": "grig",
  "password": "grig"
}
```

4. After you registered succesully and got your token you can use that token to access any other endpoint.

### Application Suitability
* **Scalability Requirements:** The app needs to handle high volumes of concurrent users, especially during peak times when multiple users might be editing notes, logging in, or syncing with external calendars. Microservices allow each component to be scaled independently based on its demand, ensuring that the entire system remains responsive under load.
* **Decoupled Development and Deployment:** The app has distinct functionalities such as user authentication, real-time note collaboration, and calendar integration. By separating these functions into microservices, 2 students can work on different features simultaneously without interfering with each other. This leads to faster development and more efficient updates.
* **Technology Diversity:** Microservices architecture enables using the best-suited technologies for each service. For instance, NodeJS with MongoDB is optimal for handling authentication processes, while Neo4j provides a flexible and efficient solution for real-time data interactions in the note-taking service. Using Java for the calendar integration ensures compatibility and robust API management.

**Example:** 
* **Google Docs:** Manages real-time collaboration by segmenting various services responsible for document editing, user permissions, and synchronization. The use of microservices allows Google Docs to handle multiple concurrent edits while maintaining document integrity and performance.

##
### Service Boundaries

![image](https://github.com/user-attachments/assets/0009ae26-8cf1-4fa5-8e0f-952119689541)

* **Authentication Service:** Manages user authentication, authorization, and session management.
* **Real-Time Note-Taking Service:** Manages note creation, updates, and real-time collaboration among users.
* **Calendar Integration Service:** Syncs notes with Google Calendar to link events and reminders with user notes.

##
### Technology Stack and Communication Patterns

* Authentication Service:
  * Language/Framework: NodeJS, Express
  * Database: MongoDB
  * Communication: REST API for synchronous requests between services.

* Note-Taking Service:
  * Language/Framework: NodeJS, Express
  * Database: Neo4j.
  * Communication: WebSocket for real-time updates; REST for other requests.

* Calendar Service:
  * Language/Framework: Java, Spring Boot
  * Database: PostgreSQL
  *  Communication: REST API to integrate with Google Calendar and sync data.
 
##
### Data Managment

**Endpoints:**


### Auth Service Endpoints

#### 1. Login
- **Path:** `/auth/login`
- **Method:** `POST`
- **Description:** Allows a user to log in to the system.
- **Body:**
  ```json
  {
    "username": "user123",
    "password": "pass123"
  }
  ```
- **Response:** On success, returns a JWT token.
  ```json
  {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
  ```

#### 2. Register
- **Path:** `/auth/register`
- **Method:** `POST`
- **Description:** Registers a new user.
- **Body:**
  ```json
  {
    "username": "newuser",
    "password": "newpass123"
  }
  ```
- **Response:**
  ```json
  {
    "message": "User registered successfully",
    "userId": "12345"
  }
  ```

#### 3. Reset Password
- **Path:** `/auth/resetPassword`
- **Method:** `PUT`
- **Description:** Allows a user to reset their password.
- **Headers:** `Authorization: Bearer <JWT token>`
- **Body:**
  ```json
  {
    "password": "newpassword123"
  }
  ```
- **Response:**
  ```json
  {
    "message": "Password reset successfully"
  }
  ```

---

### Note Service Endpoints

#### 1. Create Note
- **Path:** `/note/create`
- **Method:** `POST`
- **Description:** Creates a new note.
- **Headers:** `Authorization: Bearer <JWT token>`
- **Body:**
  ```json
  {
    "noteName": "My First Note"
  }
  ```
- **Response:**
  ```json
  {
    "message": "Note created successfully",
    "noteId": "note123"
  }
  ```

#### 2. Get All Records
- **Path:** `/note/{noteName}/records`
- **Method:** `GET`
- **Description:** Retrieves all records from a specific note.
- **Headers:** `Authorization: Bearer <JWT token>`
- **Response:**
  ```json
  [
    {
      "recordId": "record1",
      "content": "This is the first record",
      "createdAt": "2024-10-22T12:34:56Z"
    },
    {
      "recordId": "record2",
      "content": "This is the second record",
      "createdAt": "2024-10-23T08:22:31Z"
    }
  ]
  ```

#### 3. Delete Note
- **Path:** `/note/{noteName}`
- **Method:** `DELETE`
- **Description:** Deletes a specific note.
- **Headers:** `Authorization: Bearer <JWT token>`
- **Response:**
  ```json
  {
    "message": "Note deleted successfully"
  }
  ```

#### 4. Update Record
- **Path:** `/note/{noteName}/{recordId}`
- **Method:** `PUT`
- **Description:** Updates a record within a specific note.
- **Headers:** `Authorization: Bearer <JWT token>`
- **Body:**
  ```json
  {
    "newContent": "Updated content for the record"
  }
  ```
- **Response:**
  ```json
  {
    "message": "Record updated successfully"
  }
  ```

---

### Calendar Service Endpoints

#### 1. Link Note to Event
- **Path:** `/api/notes/link`
- **Method:** `POST`
- **Description:** Links a note to a calendar event.
- **Headers:** `Authorization: Bearer <JWT token>`
- **Body:**
  ```json
  {
    "eventId": "event123",
    "noteId": "note123"
  }
  ```
- **Response:**
  ```json
  {
    "message": "Note linked to event successfully"
  }
  ```

#### 2. Retrieve Event-Note Links by User
- **Path:** `/api/notes/links/user`
- **Method:** `GET`
- **Description:** Retrieves all event-note links associated with the authenticated user.
- **Headers:** `Authorization: Bearer <JWT token>`
- **Response:**
  ```json
  [
    {
      "eventId": "event123",
      "noteId": "note123",
      "createdAt": "2024-10-22T12:34:56Z"
    },
    {
      "eventId": "event456",
      "noteId": "note789",
      "createdAt": "2024-10-23T08:22:31Z"
    }
  ]
  ```

#### 3. Retrieve Event-Note Links by Event
- **Path:** `/api/notes/links/event`
- **Method:** `GET`
- **Description:** Retrieves all notes linked to a specific event.
- **Headers:** `Authorization: Bearer <JWT token>`
- **Query Parameters:**
  - `eventId`: UUID of the event
- **Response:**
  ```json
  [
    {
      "noteId": "note123",
      "createdAt": "2024-10-22T12:34:56Z"
    },
    {
      "noteId": "note789",
      "createdAt": "2024-10-23T08:22:31Z"
    }
  ]
  ```

#### 4. Retrieve Event-Note Links by Note
- **Path:** `/api/notes/links/note`
- **Method:** `GET`
- **Description:** Retrieves all events linked to a specific note.
- **Headers:** `Authorization: Bearer <JWT token>`
- **Query Parameters:**
  - `noteId`: UUID of the note
- **Response:**
  ```json
  [
    {
      "eventId": "event123",
      "createdAt": "2024-10-22T12:34:56Z"
    },
    {
      "eventId": "event456",
      "createdAt": "2024-10-23T08:22:31Z"
    }
  ]
  ```

#### 5. Google OAuth Authorization
- **Path:** `/api/google/authorize`
- **Method:** `GET`
- **Description:** Initiates the Google OAuth flow to authorize access to Google Calendar.
- **Response:** Redirects to Google’s OAuth authorization page.

#### 6. Google OAuth Callback
- **Path:** `/oauth2/callback`
- **Method:** `POST`
- **Description:** Callback endpoint for Google OAuth, handling the token exchange.
- **Headers:** `Authorization: Bearer <JWT token>`
- **Query Parameters:**
  - `code`: Authorization code from Google OAuth
- **Response:**
  ```json
  {
    "message": "OAuth token successfully obtained",
    "accessToken": "ya29.A0ARrdaM..."
  }
  ```

#### 7. Fetch Events from Google Calendar
- **Path:** `/api/events/fetch`
- **Method:** `GET`
- **Description:** Fetches events from the user's Google Calendar.
- **Headers:** `Authorization: Bearer <JWT token>`
- **Response:**
  ```json
  [
    {
      "eventId": "event123",
      "title": "Meeting with team",
      "start": "2024-10-22T10:00:00Z",
      "end": "2024-10-22T11:00:00Z"
    },
    {
      "eventId": "event456",
      "title": "Doctor Appointment",
      "start": "2024-10-23T15:30:00Z",
      "end": "2024-10-23T16:00:00Z"
    }
  ]
  ```

---


##
### Deployment and Scaling

The App will use Docker for deployment. Each microservice runs in its own container, managed by Docker Compose, which handles scaling and automatic restarts. Services like Authentication and Note-Taking are deployed with replicas to manage load and ensure reliability. Databases and a cache service are also containerized to optimize performance.
