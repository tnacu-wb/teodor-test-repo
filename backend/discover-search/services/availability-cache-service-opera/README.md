# Availability Cache Service (Opera)

This Spring Boot microservice provides a high-performance caching layer for hotel availability and pricing data. It enables clients to perform complex searches quickly and efficiently.

## ✨ Key Features

* **Multi-Hotel Availability**: Search for availability across multiple hotels in a single request.
* **Price-Based Searches**: Find the least expensive hotels by room type or the best prices based on a specific location.
* **Hotel Price Calendar**: Retrieve the best prices for a single hotel over a given period.
* **Price Finder**: View daily prices for availabilities for up to 365 days in the future.

---

## 🛠️ Tech Stack

* ☕ **Java 25**
* 🌱 **Spring Boot 4.x** & **Spring Cloud** (with Lombok)
* 📦 **Maven 3+**
* 🐳 **Docker** & **Docker Compose**

---

## 🚀 Getting Started

Follow these instructions to get the project up and running on your local machine for development and testing purposes.

### Prerequisites

* **Java Development Kit (JDK) 25** or later.
* **Apache Maven 3+**.
* **Docker Desktop** installed and running.

### 1. Running Services with Docker Compose (Recommended) and then Run your Spring Boot Application

This is the fastest way to get the service and its dependencies (like databases or message brokers) running. For a more detailed guide on the automated local setup (already available in this codebase), please refer to the **[Confluence documentation](https://whitbreadis.atlassian.net/wiki/spaces/BE/pages/5245829582/Availability+Cache+Service+Automating+Local+Setup+for+with+Docker+and+Enhanced+Profiles)** and its subpages.

1.  Navigate to the Docker setup directory:
    ```bash
    cd infrastructure/docker-local
    ```
2.  Bring up the entire application stack:
    ```bash
    docker-compose up
    ```
3. Now start your application directly from IntelliJ or your preferred IDE with `local` profile.

### 2. Do the manual setup (WB legacy way) and run your Spring Boot Application

You can also run the service directly without containerizing it, but need to install all dependencies locally on your system (restrictions apply; based on your office policy).

#### 🆜 From IntelliJ IDEA
Simply run the main application class (`AvailabilityCacheServiceOperaApplication`). Make sure to set the active Spring profile to `local` in your run configuration.

Note: The `local` profile is configured to run the service in isolation by disabling most external integrations and the configuration service.

#### 📺 From the Terminal
Execute the following Maven command from the project's root directory:
```bash
    mvn spring-boot:run -Dspring.profiles.active=local
```

### 🌶️ Lombok
This project uses Project Lombok to reduce boilerplate code. If you are using an IDE like IntelliJ IDEA or Eclipse, make sure you have the Lombok plugin installed and that annotation processing is enabled.

### ▩ Diagrams
The project contains architecture diagrams generated with PlantUML. To regenerate them after making changes, run the following Maven command or a given hotel over a period of time | `/search/hotels/{hotelCode}/calendars` | https://app.swaggerhub.com/apis/whitbread/Availability-Cache-API/1.0.4#/Single%20Hotel%20Price%20Details/getBestHotelRoomPrice |

```bash
    mvn plantuml:generate
```