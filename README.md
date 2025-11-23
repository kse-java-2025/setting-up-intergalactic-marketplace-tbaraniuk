# Intergalactic Marketplace Backend 🚀 🪐

A Spring Boot application for managing intergalactic products and categories. This project demonstrates advanced backend patterns including **AOP-based Feature Toggles** and isolated integration testing using **WireMock**.

## 🛠 Technology Stack

* **Java 17**
* **Spring Boot 3.x**
* **Gradle** (Build Tool)
* **Lombok** (Boilerplate reduction)
* **MapStruct** (DTO Mapping)
* **WireMock** (External Service Mocking for Integration Tests)
* **Spring AOP** (Aspect-Oriented Programming)

## ✨ Key Features

### 1. Product Management
Robust CRUD operations for products and categories (e.g., "Space Gadgets", "Laptops"). Supports pagination and filtering.

### 2. External Recommendations
Integrates with an external **Recommendation Service** to fetch suggested products.
* Uses `RestClient` for communication.
* Integration tests use **WireMock** to simulate the external API without needing a real server running.

### 3. Custom Feature Toggles (AOP)
Implements a custom annotation-based feature toggle system to manage functionality at runtime without changing code.

* **Annotation:** `@FeatureToggle(FeatureToggles.RECOMMENDATIONS)`
* **Strategies:**
    * **Graceful Degradation:** Returns an empty list/object if the feature is disabled (e.g., Recommendations).
    * **Fail Fast:** Throws an exception if disabled (e.g., expensive AI operations).

**Configuration (`application.yml`):**
```yaml
features:
  recommendations:
    enabled: true  # Set to false to return empty recommendations (Graceful Degradation)
  ai_description:
    enabled: false # Set to false to block endpoint calls (Fail Fast)

## 📂 Project Structure

Based on the source code organization:

```text
├── .github/workflows        # CI/CD configurations (pull_request.yml)
├── gradle                   # Gradle wrapper and custom scripts
│   ├── jacoco.gradle        # Code coverage configuration
│   └── test.gradle          # Test logging and configuration
├── scripts                  # Utility scripts (Docker/WireMock stubs)
└── src
    ├── main
    │   ├── java/com/example/intergalactic_marketplace
    │   │   ├── config          # Configuration beans (RestClient)
    │   │   ├── domain          # Domain Entities
    │   │   ├── dto             # Immutable Data Transfer Objects (@Value)
    │   │   ├── featuretoggle   # Custom Annotations, Aspects, and Exceptions
    │   │   ├── service         # Business Logic interfaces and implementations
    │   │   ├── web             # REST Controllers & Global Exception Handler
    │   │   └── IntergalacticMarketplaceApplication.java
    │   └── resources           # application.yml, API specs
    └── test                    # Integration Tests (IT) and Unit Tests