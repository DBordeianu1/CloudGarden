#### **Cloud Garden (Succulent-as-a-Service)**

###### **A Cloud-Native Simulation of Resource Lifecycle Management**



---



Cloud Garden is a full-stack application that gamifies the concept of "Day 2 Operations" in cloud infrastructures.



It bridges the gap between my interest in plants and my desire to understand **Cloud Resource Management**. In this application, a "Succulent" is not just a plant, it is a mock service with state, health metrics, and lifecycle requirements.



If you don't maintain the resource (i.e. water, soil), the background worker detects the degradation, updates the state to wilting, and eventually terminates it, simulating service failure in a distributed system.



---



**Architecture**

This project follows a Single-Deployment Unit pattern:

* **Frontend**: React (SPA) for the Dashboard.
* **Backend**: Java Spring Boot for State Management and API logic.
* **Infrastructure**: Dockerized and orchestrated via Kubernetes (Minikube).



**Teck Stack**

* **Frontend**: React, CSS Grid, Axios
* **Backend**: Java, Spring Boot, Spring Scheduler
* **DevOps**: Docker, Kubernetes (Minikube), Maven



---



Each feature is designed to map to real-world concept in Event Mesh management:

| Garden Feature | Cloud Engineering Concept |
|----------------|---------------------------|
| **Planting a new Succulent** | **Provisioning:** Instantiating a new service instance via API. |
| **Watering the Plant** | **Day 2 Ops:** Performing maintenance to reset TTL/Health checks. |
| **The "Thirst" Scheduler** | **Background Worker:** A cron job simulating resource consumption/decay. |
| **Wilting/Dead States** | **Observability:** Visualizing service degradation on the dashboard. |
| **Kubernetes Deployment** | **Self-Healing:** Testing pod resilience and orchestration. |

