import asyncio
import uuid
from fastapi import FastAPI, Request, HTTPException, Header, Response
import httpx

app = FastAPI()

# Service URLs
AUTH_SERVICE_URL = "http://localhost:3000"
NOTE_SERVICE_URL = "http://localhost:4000"  # Note-taking service
CALENDAR_SERVICE_URL = "http://localhost:8080"  # Calendar service

# Semaphore to limit concurrent tasks (e.g., max 10 concurrent requests)
MAX_CONCURRENT_TASKS = 10  # Set to 3 for demonstration purposes
semaphore = asyncio.Semaphore(MAX_CONCURRENT_TASKS)

TASK_TIMEOUT = 5  # Timeout for each task

# Task counter
task_counter = 0

HEALTH_CHECK_INTERVAL = 10

# Global variable to store health statuses
service_status = {
    "auth_service": False,
    "note_service": False,
    "calendar_service": False,
}

# Task timeout function with concurrency logging
async def run_with_timeout(coroutine, timeout, num):
    global task_counter

    # Log the task number
    # print(f"Making request {num}")

    try:
        result = await asyncio.wait_for(coroutine, timeout=timeout)

        # After the request, check if semaphore is locked (limit reached)
        if semaphore.locked():
            print("Concurrency limit reached, waiting...")

        return result

    except asyncio.TimeoutError:
        raise HTTPException(status_code=508, detail="Request timed out")


# Forwarding request to Google OAuth
@app.get("/api/google/authorize")
async def google_authorize():
    global task_counter
    async with semaphore:
        task_num = task_counter
        task_counter += 1
        async with httpx.AsyncClient() as client:
            try:
                response = await run_with_timeout(
                    client.get(f"{CALENDAR_SERVICE_URL}/api/google/authorize", follow_redirects=False), TASK_TIMEOUT,
                    task_num
                )
                if response.status_code == 302:
                    location = response.headers.get('Location')
                    return Response(status_code=302, headers={'Location': location})
                return response.json()
            except HTTPException as e:
                return Response(content=str(e.detail), status_code=e.status_code, media_type="application/json")


# OAuth2 Callback
@app.post("/oauth2/callback")
async def oauth2_callback(request: Request, authorization: str = Header(None)):
    global task_counter
    async with semaphore:
        task_num = task_counter
        task_counter += 1
        query_params = request.query_params
        headers = {"Authorization": authorization}

        async with httpx.AsyncClient() as client:
            try:
                response = await run_with_timeout(
                    client.post(f"{CALENDAR_SERVICE_URL}/oauth2/callback", params=query_params, headers=headers),
                    TASK_TIMEOUT, task_num
                )
                return Response(content=response.content, status_code=response.status_code,
                                media_type="application/json")
            except HTTPException as e:
                return Response(content=str(e.detail), status_code=e.status_code, media_type="application/json")


# Link notes to events
@app.post("/api/notes/link")
async def link_note_to_event(eventId: str, noteId: str, authorization: str = Header(None)):
    global task_counter
    async with semaphore:
        task_num = task_counter
        task_counter += 1
        headers = {"Authorization": authorization}
        url = f"{CALENDAR_SERVICE_URL}/api/notes/link?eventId={eventId}&noteId={noteId}"

        async with httpx.AsyncClient() as client:
            try:
                response = await run_with_timeout(client.post(url, headers=headers), TASK_TIMEOUT, task_num)
                return Response(content=response.content, status_code=response.status_code,
                                media_type="application/json")
            except HTTPException as e:
                return Response(content=str(e.detail), status_code=e.status_code, media_type="application/json")


# Delete note link
@app.delete("/api/notes/link")
async def delete_note_link(eventId: uuid.UUID, noteId: uuid.UUID, authorization: str = Header(None)):
    global task_counter
    async with semaphore:
        task_num = task_counter
        task_counter += 1
        headers = {"Authorization": authorization}
        params = {"eventId": eventId, "noteId": noteId}

        async with httpx.AsyncClient() as client:
            try:
                response = await run_with_timeout(
                    client.delete(f"{CALENDAR_SERVICE_URL}/api/notes/link", params=params, headers=headers),
                    TASK_TIMEOUT, task_num)
                return Response(content=response.content, status_code=response.status_code,
                                media_type="application/json")
            except HTTPException as e:
                return Response(content=str(e.detail), status_code=e.status_code, media_type="application/json")


# Fetch events from Google Calendar
@app.get("/api/events/fetch")
async def fetch_events(authorization: str = Header(None)):
    global task_counter
    async with semaphore:
        task_num = task_counter
        task_counter += 1
        headers = {"Authorization": authorization}
        async with httpx.AsyncClient() as client:
            try:
                response = await run_with_timeout(
                    client.get(f"{CALENDAR_SERVICE_URL}/api/events/fetch", headers=headers), TASK_TIMEOUT, task_num)
                return response.json()
            except HTTPException as e:
                return Response(content=str(e.detail), status_code=e.status_code, media_type="application/json")


# Retrieve event-note links by user
@app.get("/api/notes/links/user")
async def get_links_by_user(authorization: str = Header(None)):
    global task_counter
    async with semaphore:
        task_num = task_counter
        task_counter += 1
        headers = {"Authorization": authorization}
        async with httpx.AsyncClient() as client:
            try:
                response = await run_with_timeout(
                    client.get(f"{CALENDAR_SERVICE_URL}/api/notes/links/user", headers=headers), TASK_TIMEOUT, task_num)
                return response.json()
            except HTTPException as e:
                return Response(content=str(e.detail), status_code=e.status_code, media_type="application/json")


# Retrieve event-note links by event
@app.get("/api/notes/links/event")
async def get_links_by_event(eventId: str, authorization: str = Header(None)):
    global task_counter
    async with semaphore:
        task_num = task_counter
        task_counter += 1
        headers = {"Authorization": authorization}
        async with httpx.AsyncClient() as client:
            try:
                response = await run_with_timeout(
                    client.get(f"{CALENDAR_SERVICE_URL}/api/notes/links/event", params={"eventId": eventId},
                               headers=headers), TASK_TIMEOUT, task_num)
                return response.json()
            except HTTPException as e:
                return Response(content=str(e.detail), status_code=e.status_code, media_type="application/json")


# Retrieve event-note links by note
@app.get("/api/notes/links/note")
async def get_links_by_note(noteId: str, authorization: str = Header(None)):
    global task_counter
    async with semaphore:
        task_num = task_counter
        task_counter += 1
        headers = {"Authorization": authorization}
        async with httpx.AsyncClient() as client:
            try:
                response = await run_with_timeout(
                    client.get(f"{CALENDAR_SERVICE_URL}/api/notes/links/note", params={"noteId": noteId},
                               headers=headers), TASK_TIMEOUT, task_num)
                return response.json()
            except HTTPException as e:
                return Response(content=str(e.detail), status_code=e.status_code, media_type="application/json")


########################################################################################################################
# Note Service Endpoints

# Endpoint to create a note
@app.post("/note/create")
async def create_note(request: Request, authorization: str = Header(None)):
    global task_counter
    async with semaphore:
        task_num = task_counter
        task_counter += 1
        headers = {"Authorization": authorization}
        async with httpx.AsyncClient() as client:
            try:
                response = await run_with_timeout(
                    client.post(f"{NOTE_SERVICE_URL}/api/createNote", json=await request.json(), headers=headers),
                    TASK_TIMEOUT, task_num
                )
                return Response(content=response.content, status_code=response.status_code,
                                media_type="application/json")
            except httpx.HTTPError as e:
                raise HTTPException(status_code=502, detail="Error forwarding request to Note service")


# Endpoint to delete a note
@app.delete("/note/{noteName}")
async def delete_note(noteName: str, authorization: str = Header(None)):
    global task_counter
    async with semaphore:
        task_num = task_counter
        task_counter += 1
        headers = {"Authorization": authorization}
        async with httpx.AsyncClient() as client:
            try:
                response = await run_with_timeout(
                    client.delete(f"{NOTE_SERVICE_URL}/note/{noteName}", headers=headers),
                    TASK_TIMEOUT, task_num
                )
                return Response(content=response.content, status_code=response.status_code,
                                media_type="application/json")
            except httpx.HTTPError as e:
                raise HTTPException(status_code=502, detail="Error forwarding request to Note service")


# Endpoint to get records from a note
@app.get("/note/{noteName}/records")
async def get_note_records(noteName: str, authorization: str = Header(None)):
    global task_counter
    async with semaphore:
        task_num = task_counter
        task_counter += 1
        headers = {"Authorization": authorization}
        async with httpx.AsyncClient() as client:
            try:
                response = await run_with_timeout(
                    client.get(f"{NOTE_SERVICE_URL}/note/{noteName}/records", headers=headers),
                    TASK_TIMEOUT, task_num
                )
                return Response(content=response.content, status_code=response.status_code,
                                media_type="application/json")
            except httpx.HTTPError as e:
                raise HTTPException(status_code=502, detail="Error forwarding request to Note service")


# Endpoint to update a record in a note
@app.put("/note/{noteName}/{recordId}")
async def update_note_record(noteName: str, recordId: uuid.UUID, request: Request, authorization: str = Header(None)):
    global task_counter
    async with semaphore:
        task_num = task_counter
        task_counter += 1
        headers = {"Authorization": authorization}
        async with httpx.AsyncClient() as client:
            try:
                response = await run_with_timeout(
                    client.put(f"{NOTE_SERVICE_URL}/note/{noteName}/{recordId}", json=await request.json(),
                               headers=headers),
                    TASK_TIMEOUT, task_num
                )
                return Response(content=response.content, status_code=response.status_code,
                                media_type="application/json")
            except httpx.HTTPError as e:
                raise HTTPException(status_code=502, detail="Error forwarding request to Note service")


########################################################################################################################
# Auth Register User
@app.post("/auth/register")
async def register_user(request: Request):
    global task_counter
    async with semaphore:
        task_num = task_counter
        task_counter += 1
        async with httpx.AsyncClient() as client:
            try:
                response = await run_with_timeout(
                    client.post(f"{AUTH_SERVICE_URL}/auth/register", json=await request.json()), TASK_TIMEOUT, task_num
                )
                return Response(content=response.content, status_code=response.status_code,
                                media_type="application/json")
            except httpx.HTTPError as e:
                raise HTTPException(status_code=502, detail="Error forwarding request to Auth service")


# Auth Login User
@app.post("/auth/login")
async def login_user(request: Request):
    global task_counter
    async with semaphore:
        task_num = task_counter
        task_counter += 1
        async with httpx.AsyncClient() as client:
            try:
                response = await run_with_timeout(
                    client.post(f"{AUTH_SERVICE_URL}/auth/login", json=await request.json()), TASK_TIMEOUT, task_num
                )
                return Response(content=response.content, status_code=response.status_code,
                                media_type="application/json")
            except httpx.HTTPError as e:
                raise HTTPException(status_code=502, detail="Error forwarding request to Auth service")


# Auth Reset Password
@app.put("/auth/resetPassword")
async def reset_password(request: Request, authorization: str = Header(None)):
    global task_counter
    async with semaphore:
        task_num = task_counter
        task_counter += 1
        headers = {"Authorization": authorization}
        async with httpx.AsyncClient() as client:
            try:
                response = await run_with_timeout(
                    client.put(f"{AUTH_SERVICE_URL}/auth/resetPassword", json=await request.json(), headers=headers),
                    TASK_TIMEOUT, task_num
                )
                return Response(content=response.content, status_code=response.status_code,
                                media_type="application/json")
            except httpx.HTTPError as e:
                raise HTTPException(status_code=502, detail="Error forwarding request to Auth service")


########################################################################################################################
# Health check endpoint for the gateway
@app.get("/health")
async def health():
    return {"status": "OK"}


# Periodically ping the services for health checks
async def periodic_health_check():
    async with httpx.AsyncClient() as client:
        while True:
            # Check auth service
            try:
                response = await client.get(f"{AUTH_SERVICE_URL}/api/health")
                service_status["auth_service"] = response.status_code == 200
            except httpx.RequestError:
                service_status["auth_service"] = False
            # Check note service
            try:
                response = await client.get(f"{NOTE_SERVICE_URL}/api/health")
                service_status["note_service"] = response.status_code == 200
            except httpx.RequestError:
                service_status["note_service"] = False

            # Check calendar service
            try:
                response = await client.get(f"{CALENDAR_SERVICE_URL}/api/health")
                service_status["calendar_service"] = response.status_code == 200
            except httpx.RequestError:
                service_status["calendar_service"] = False

            print(service_status)

            # Wait for the next health check interval
            await asyncio.sleep(HEALTH_CHECK_INTERVAL)


########################################################################################################################
# Start the periodic health check
@app.on_event("startup")
async def on_startup():
    asyncio.create_task(periodic_health_check())
