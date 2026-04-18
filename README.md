## **CloudGarden (Succulent-as-a-Service)**

### **A Cloud-Native Simulation of Resource Lifecycle Management**



---



Cloud Garden is a full-stack application that gamifies the concept of "Day 2 Operations" in cloud infrastructures.



It bridges the gap between my interest in plants and my desire to understand **Cloud Resource Management**. In this application, a "Succulent" is not just a plant, it is a mock service with state, health metric, and lifecycle requirements.



If you don't maintain the resource (i.e. water), the background worker detects the degradation, updates the state to wilting, and eventually terminates it, simulating service failure in a distributed system.



---



**Architecture**

This project follows a Single-Deployment Unit pattern:

* **Frontend**: React (SPA) for the Dashboard.
* **Backend**: Java Spring Boot for State Management and API logic.
* **Infrastructure**: Dockerized and orchestrated via Kubernetes (Minikube).



**Tech Stack**

* **Frontend**: React, Tailwind CSS, Axios
* **Backend**: Java 17, Spring Boot, Spring Scheduler, H2 Database
* **DevOps**: Docker, Kubernetes (Minikube), Maven


---



Each feature is designed to map to real-world concept in Event Mesh management:

| Garden Feature | Cloud Engineering Concept |
|----------------|---------------------------|
| **Planting a new Succulent** | **Provisioning:** Instantiating a new service instance via API. |
| **Watering the Plant** | **Day 2 Ops:** Performing maintenance to reset TTL/Health checks. |
| **The "Thirst" Scheduler** | **Background Worker:** A cron job simulating resource consumption/decay. |
| **Wilting/Dead/Zombie States** | **Observability:** Visualizing service degradation on the dashboard. |
| **Kubernetes Deployment** | **Self-Healing:** Testing pod resilience and orchestration. |



---



## Getting Started

### Running Locally (Development)

#### Backend
```powershell
cd backend
mvn clean package
java -jar target/cloud-garden-backend-0.0.1-SNAPSHOT.jar
```
The backend will start on `http://localhost:8080`
- API URL: `http://localhost:8080/api/plants`
- H2 Console: `http://localhost:8080/h2-console`

#### Frontend
```powershell
cd frontend
npm install
npm start
```
The frontend will start on `http://localhost:3000`

### Running with Docker

The application is containerized in a single Docker image using a multi-stage build that includes both the React frontend and Spring Boot backend.

#### Build the Docker Image
```powershell
docker build -t cloudgarden:latest .
```

#### Run the Container
```powershell
docker run -p 80:80 -p 8080:8080 cloudgarden:latest
```

Access the application at:
- **Frontend**: `http://localhost`
- **Backend API**: `http://localhost:8080/api/plants`
- **H2 Console** (dev only): `http://localhost:8080/h2-console`

#### Docker Architecture
- **Multi-stage build**: Optimized image size with separate build and runtime stages
- **nginx**: Serves the React frontend static files on port 80
- **Spring Boot**: Runs the backend API on port 8080
- **Supervisor**: Manages both processes in a single container

#### Health Check
The container includes a health check that monitors the nginx service:
```powershell
docker ps  # Check container health status
```

#### View Logs
```powershell
docker logs <container-id>
```

### Running with Kubernetes (Minikube)

Deploy the application to a local Kubernetes cluster using Minikube for orchestration and self-healing capabilities.

#### Prerequisites
- Docker Desktop installed and running
- Minikube installed ([Installation Guide](https://minikube.sigs.k8s.io/docs/start/))
- kubectl installed

#### Initial Setup

**Step 1: Start Minikube**
```powershell
minikube start --driver=docker
```

**Step 2: Build the Docker Image**
```powershell
docker build -t cloudgarden:latest .
```

**Step 3: Load Image into Minikube**
```powershell
minikube image load cloudgarden:latest
```

**Step 4: Deploy to Kubernetes**
```powershell
kubectl apply -f k8s-deployment.yaml
kubectl apply -f k8s-service.yaml
```

**Step 5: Check Pod Status**
```powershell
kubectl get pods
```
Wait until STATUS shows `Running` and READY shows `1/1`

**Step 6: Start Minikube Tunnel** (keep this terminal open)
```powershell
minikube tunnel
```

**Step 7: Get Service URLs** (in a separate terminal)
```powershell
minikube service cloudgarden-service --url
```

Access the frontend URL shown in the output to use the application.

#### Restarting After Stopping

If you've stopped Minikube and want to restart the application:

```powershell
# Start Minikube
minikube start --driver=docker

# Deploy (if not already deployed)
kubectl apply -f k8s-deployment.yaml
kubectl apply -f k8s-service.yaml

# Check pod status
kubectl get pods

# Start tunnel (in dedicated terminal)
minikube tunnel

# Get URLs (in separate terminal)
minikube service cloudgarden-service --url
```

#### Updating After Code Changes

If you modify the application code:

```powershell
# Rebuild Docker image
docker build -t cloudgarden:latest .

# Delete old deployment
kubectl delete deployment cloudgarden

# Remove old image from Minikube
minikube image rm cloudgarden:latest

# Load new image
minikube image load cloudgarden:latest

# Redeploy
kubectl apply -f k8s-deployment.yaml

# Wait for pod to be ready
kubectl get pods
```

Your tunnel will still work with the new deployment.

#### Stopping Everything

```powershell
# Stop tunnel (Ctrl+C in tunnel terminal)

# Stop Minikube
minikube stop
```

#### Useful Commands

```powershell
# View pod logs
kubectl logs -l app=cloudgarden --all-containers=true --tail=100

# Stream logs in real-time
kubectl logs -l app=cloudgarden --all-containers=true -f

# Check deployment status
kubectl get deployments

# Check service status
kubectl get services

# Delete deployment and service
kubectl delete -f k8s-deployment.yaml
kubectl delete -f k8s-service.yaml
```

---

## **UI Testing... for an agentic feel!**

This section covers two related but distinct things: 
> 1) using Claude Code to explore the CloudGarden UI via the Playwright MCP server
> 2) then translating similar interactions as an automated Playwright E2E test suite

</details>

<details>
<summary>Playwright MCP (Agentic Exploration)</summary>

The [Playwright MCP server](https://github.com/microsoft/playwright-mcp) gives Claude Code a live browser it can control directly: clicking buttons, filling forms, reading page state (without any pre-written test scripts). 

This was used to let Claude Code navigate CloudGarden the same way a user would: planting succulents, watering them, and observing status changes on the dashboard. Think of it as a test driven by natural language rather than code.

</details>

</details>

<details>
<summary>Automated E2E Tests</summary>

The Playwright test suite in `frontend/src/e2e/cloudgarden.spec.js` reproduces those agentic interactions as repeatable, automated tests. It covers the core plant lifecycle:

| Test | What it verifies |
|------|-----------------|
| Empty garden on load | Dashboard shows zero plants on a clean start |
| Plant a new succulent | Modal flow works end-to-end and card appears with HEALTHY status |
| Header stats after planting | "Total Plants" and "Healthy" counters update correctly |
| Water a plant | Water button resets water level to 100% and status stays HEALTHY |
| Delete a plant | Confirmation modal works and stats reset to zero |

Tests that require a plant to exist set it up directly via the backend API rather than through the UI: this keeps each test independent, so a UI bug in one flow does not cascade and break unrelated tests. Cleanup also happens via API, regardless of whether the test passed or failed.

#### Prerequisite: CloudGarden is running locally as described [here](https://github.com/DBordeianu1/CloudGarden#running-locally-development)

#### Running the Tests

Install browser binaries once:
```powershell
cd frontend
npx playwright install
```

Standard run (headed, results in terminal):
```powershell
npm run test:e2e
```

Interactive UI mode (recommended for watching tests execute step by step):
```powershell
npx playwright test --ui
```

Debug mode (step through one action at a time):
```powershell
npx playwright test --debug
```
</details>
