# Real-Time Collaborative Note-Taking App

### Application Suitability
* **Scalability Requirements:** The app needs to handle high volumes of concurrent users, especially during peak times when multiple users might be editing notes, logging in, or syncing with external calendars. Microservices allow each component to be scaled independently based on its demand, ensuring that the entire system remains responsive under load.
* **Decoupled Development and Deployment:** The app has distinct functionalities such as user authentication, real-time note collaboration, and calendar integration. By separating these functions into microservices, 2 students can work on different features simultaneously without interfering with each other. This leads to faster development and more efficient updates.
* **Technology Diversity:** Microservices architecture enables using the best-suited technologies for each service. For instance, NodeJS with MongoDB is optimal for handling authentication processes, while Neo4j provides a flexible and efficient solution for real-time data interactions in the note-taking service. Using Java for the calendar integration ensures compatibility and robust API management.

**Example:** 
* **Google Docs:** Manages real-time collaboration by segmenting various services responsible for document editing, user permissions, and synchronization. The use of microservices allows Google Docs to handle multiple concurrent edits while maintaining document integrity and performance.

##
### Service Boundaries

![image](https://github.com/user-attachments/assets/09a0cd93-f66e-480c-8ddc-90ca73808951)

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

#### **1. Authentication Service (NodeJS, MongoDB)**

**Endpoints:**

1. **`POST /register`**
   - **Purpose**: Register a new user.
   - **Request Format (JSON)**:
     ```json
     {
       "username": "string",
       "email": "string",
       "password": "string"
     }
     ```
   - **Response**:
     ```json
     {
       "message": "User registered successfully",
       "userId": "string"
     }
     ```

2. **`POST /login`**
   - **Purpose**: Authenticate user and issue a JWT token.
   - **Request Format (JSON)**:
     ```json
     {
       "email": "string",
       "password": "string"
     }
     ```
   - **Response**:
     ```json
     {
       "token": "string"
     }
     ```

3. **`POST /logout`**
   - **Purpose**: Log out the user by invalidating the JWT token.
   - **Request Format (JSON)**:
     ```json
     {
       "token": "string"
     }
     ```
   - **Response**:
     ```json
     {
       "message": "Logout successful"
     }
     ```

4. **`POST /reset-password`**
   - **Purpose**: Reset the user’s password.
   - **Request Format (JSON)**:
     ```json
     {
       "email": "string",
       "newPassword": "string"
     }
     ```
   - **Response**:
     ```json
     {
       "message": "Password reset successful"
     }
     ```

#### **2. Real-Time Note-Taking Service (NodeJS, GraphQL)**

**Endpoints:**

1. **`POST /notes`**
   - **Purpose**: Create a new note.
   - **Request Body (JSON)**:
     ```json
     {
       "title": "string",
       "content": "string"
     }
     ```
   - **Response**:
     ```json
     {
       "id": "string",
       "title": "string",
       "content": "string",
       "createdAt": "timestamp"
     }
     ```

2. **`PUT /notes/{id}`**
   - **Purpose**: Update an existing note.
   - **Request Parameters**: 
     - `id` (Path Parameter): The ID of the note to be updated.
   - **Request Body (JSON)**:
     ```json
     {
       "content": "string"
     }
     ```
   - **Response**:
     ```json
     {
       "id": "string",
       "title": "string",
       "content": "string",
       "updatedAt": "timestamp"
     }
     ```

3. **`GET /users/{userId}/notes`**
   - **Purpose**: Retrieve all notes for a user.
   - **Request Parameters**: 
     - `userId` (Path Parameter): The ID of the user whose notes are being retrieved.
   - **Response**:
     ```json
     {
       "notes": [
         {
           "id": "string",
           "title": "string",
           "content": "string",
           "updatedAt": "timestamp"
         }
       ]
     }
     ```

4. **`DELETE /notes/{id}`**
   - **Purpose**: Delete an existing note.
   - **Request Parameters**: 
     - `id` (Path Parameter): The ID of the note to be deleted.
   - **Response**:
     ```json
     {
       "message": "Note deleted successfully"
     }
     ```

#### **3. Calendar Integration Service (Java, PostgreSQL)**

**Endpoints:**

1. **`POST /sync-calendar`**
   - **Purpose**: Sync notes with Google Calendar events.
   - **Request Format (JSON)**:
     ```json
     {
       "userId": "string",
       "notes": ["string"]
     }
     ```
   - **Response**:
     ```json
     {
       "message": "Calendar synced successfully"
     }
     ```

2. **`POST /create-event`**
   - **Purpose**: Create a new calendar event based on a note.
   - **Request Format (JSON)**:
     ```json
     {
       "noteId": "string",
       "eventDetails": {
         "title": "string",
         "startTime": "timestamp",
         "endTime": "timestamp"
       }
     }
     ```
   - **Response**:
     ```json
     {
       "eventId": "string",
       "message": "Event created successfully"
     }
     ```

3. **`PUT /update-event`**
   - **Purpose**: Update an existing calendar event when a linked note is edited.
   - **Request Format (JSON)**:
     ```json
     {
       "eventId": "string",
       "updatedDetails": {
         "title": "string",
         "startTime": "timestamp",
         "endTime": "timestamp"
       }
     }
     ```
   - **Response**:
     ```json
     {
       "message": "Event updated successfully"
     }
     ```

4. **`DELETE /delete-event`**
   - **Purpose**: Delete a calendar event when a linked note is removed.
   - **Request Format (JSON)**:
     ```json
     {
       "eventId": "string"
     }
     ```
   - **Response**:
     ```json
     {
       "message": "Event deleted successfully"
     }
     ```

##
### Deployment and Scaling

The App will use Docker for deployment. Each microservice runs in its own container, managed by Docker Compose, which handles scaling and automatic restarts. Services like Authentication and Note-Taking are deployed with replicas to manage load and ensure reliability. Databases and a cache service are also containerized to optimize performance.
