## **Cloud Garden (Succulent-as-a-Service)**

###### **A Cloud-Native Simulation of Resource Lifecycle Management**



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

# Check deployment status
kubectl get deployments

# Check service status
kubectl get services

# Delete deployment and service
kubectl delete -f k8s-deployment.yaml
kubectl delete -f k8s-service.yaml
```

