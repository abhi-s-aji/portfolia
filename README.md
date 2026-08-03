# Portfolia

> A native Android application for organizing software projects, development resources, secure developer secrets, and professional portfolio assets in a fully offline environment.

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
![Platform](https://img.shields.io/badge/Platform-Android-3DDC84)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4)
![Min SDK](https://img.shields.io/badge/Min%20SDK-24-success)
![Compile SDK](https://img.shields.io/badge/Compile%20SDK-35-success)
![Architecture](https://img.shields.io/badge/Architecture-MVVM%20%2B%20Clean-orange)
![Offline](https://img.shields.io/badge/Offline-First-brightgreen)
![Status](https://img.shields.io/badge/Status-Active%20Development-blue)

---

## Repository Information

| Property                  | Value                                                |
| :------------------------ | :--------------------------------------------------- |
| **Project**               | Portfolia                                            |
| **Platform**              | Android                                              |
| **Application Type**      | Native Android Application                           |
| **Programming Language**  | Kotlin 2.0.21                                        |
| **UI Framework**          | Jetpack Compose with Material 3                      |
| **Architecture**          | Layered MVVM following Clean Architecture principles |
| **Minimum SDK**           | 24                                                   |
| **Compile SDK**           | 35                                                   |
| **Build System**          | Android Gradle Plugin 8.7.3 (Gradle Kotlin DSL)      |
| **License**               | Apache License 2.0                                   |
| **Repository Visibility** | Public                                               |
| **Development Status**    | Active Development                                   |
| **Maintainer**            | Abhi S Aji                                           |

### Maintainer

| Platform     | Profile                                     |
| :----------- | :------------------------------------------ |
| **GitHub**   | https://github.com/abhi-s-aji               |
| **LinkedIn** | https://www.linkedin.com/in/abhi-s-aji-eden |

---

## About

Portfolia is an open-source native Android application designed to provide a centralized workspace for developers to manage software projects, reusable resources, secure credentials, code snippets, and professional portfolio information within a single offline-first experience.

The application is built using modern Android development technologies, including Kotlin, Jetpack Compose, Material 3, Room, DataStore, Kotlin Coroutines, and StateFlow. Its architecture follows a layered MVVM approach with Clean Architecture principles, emphasizing maintainability, scalability, and clear separation of responsibilities.

Portfolia operates entirely on-device. All application data is stored locally using Room and DataStore, with no reliance on remote services, analytics platforms, or third-party tracking libraries. The application remains fully functional without an internet connection.

Repository documentation reflects the implemented codebase and is maintained alongside development to ensure technical accuracy, consistency, and long-term maintainability.

---

## Repository Principles

Development of Portfolia is guided by the following engineering principles:

* Documentation reflects the implemented codebase.
* Features are documented only after implementation.
* Offline-first architecture by design.
* Privacy is treated as a default requirement.
* Security-sensitive operations leverage Android platform security components.
* Maintainable architecture is preferred over unnecessary complexity.
* Repository organization and coding conventions remain consistent.
* Source code and documentation evolve together.
* Dependencies are selected conservatively and updated deliberately.
* Long-term maintainability takes precedence over short-term convenience.

---

## Table of Contents

### Introduction

* [Overview](#overview)
* [Philosophy](#philosophy)
* [Why Portfolia](#why-portfolia)

### Application

* [Core Features](#core-features)
* [Technology Stack](#technology-stack)
* [Application Architecture](#application-architecture)
* [Project Structure](#project-structure)
* [Database Overview](#database-overview)
* [Data Flow](#data-flow)

### Development

* [Development Setup](#development-setup)
* [Development Guidelines](#development-guidelines)
* [Code Style](#code-style)
* [Documentation Standards](#documentation-standards)

### Quality & Security

* [Security](#security)
* [Privacy](#privacy)
* [Performance](#performance)
* [Accessibility](#accessibility)
* [Localization](#localization)
* [Offline Support](#offline-support)

### Community

* [Contributing](#contributing)
* [Issue Reporting](#issue-reporting)
* [Pull Request Guidelines](#pull-request-guidelines)
* [Branch Strategy](#branch-strategy)

### Project

* [Roadmap](#roadmap)
* [License](#license)
* [Author](#author)

---

## Overview

Portfolia is an open-source native Android application that provides developers with a centralized workspace for managing software projects, development resources, reusable code snippets, secure credentials, and professional portfolio information.

The application is designed around an offline-first architecture, ensuring that all core functionality remains available without an internet connection. Project data, developer profiles, references, snippets, preferences, and secure information are stored locally on the device using Room and DataStore.

Built with Kotlin and Jetpack Compose, Portfolia follows a layered MVVM architecture with Clean Architecture principles to promote maintainability, scalability, and a clear separation of responsibilities between presentation, business logic, and data persistence.

Repository documentation reflects the current implementation and is maintained alongside the source code to provide an accurate technical reference for contributors and users.

---

## Philosophy

Portfolia is developed with an emphasis on long-term maintainability, predictable behavior, and transparent engineering practices.

The project follows several core principles:

* Offline-first application design.
* Local ownership of user data.
* Privacy by default.
* Maintainable and modular architecture.
* Clear separation of application layers.
* Consistent user experience.
* Documentation that reflects the implemented codebase.
* Incremental development with measurable improvements.

Technical decisions are made with an emphasis on simplicity, readability, and long-term sustainability rather than unnecessary complexity.

---

## Why Portfolia

Modern developers often rely on multiple applications to manage projects, reusable snippets, reference links, API credentials, and professional portfolio information. Switching between these tools introduces unnecessary fragmentation and context switching.

Portfolia consolidates these workflows into a single native Android application while maintaining a fully offline operating model. The application combines project organization, developer resources, secure credential storage, reusable snippets, and portfolio management without requiring cloud synchronization or external services.

By storing application data locally and integrating Android platform capabilities such as Jetpack Compose, Room, DataStore, AndroidX Biometric, and the Storage Access Framework, Portfolia provides a cohesive developer workspace that prioritizes performance, privacy, and reliability.

---

## Core Features

Portfolia is organized into five primary functional areas, each accessible through the application's bottom navigation.

### Projects Hub

The Projects Hub serves as the central workspace for managing software projects.

Key capabilities include:

* Create and manage software projects.
* Categorize projects for easier organization.
* Store project descriptions and technology stacks.
* Attach live demonstration URLs.
* Link GitHub repositories.
* Link LinkedIn project posts.
* Browse projects using a filterable card-based interface.
* Persist project information locally using Room Database.

---

### References & Snippets

A unified workspace for storing developer resources and reusable code.

#### Reference Links

Organize frequently used resources by storing:

* Documentation links
* Articles
* Learning resources
* API references
* Project resources
* Categorized bookmarks
* Group labels
* Personal notes

#### Code Snippets

Store reusable source code with language-aware organization.

Supported languages include:

* Kotlin
* Bash
* SQL
* Docker
* JSON
* YAML
* Other

Features include:

* Monospaced code rendering.
* One-tap copy to clipboard.
* Automatic clipboard cleanup after 30 seconds.
* Language-based categorization.
* Optional contextual descriptions.

---

### Developer Secret Vault

A secure workspace for managing sensitive development information.

Capabilities include:

* Biometric authentication using Android BiometricPrompt.
* Android KeyStore integration for authentication.
* Store API keys.
* Store environment variables.
* Store configuration endpoints.
* Associate secrets with individual projects.
* Export environment variables as `.env` files.
* Export secrets as JSON.
* Base64 encoding for local persistence.
* Biometric re-authentication before protected export operations.

---

### Developer Profile

A portfolio-oriented developer identity dashboard.

The profile includes:

* Developer name.
* Professional title.
* Biography.
* Contact information.
* GitHub profile.
* LinkedIn profile.
* Experience summary.
* Project statistics.
* Commit statistics.
* Uptime indicator.
* Avatar support.
* QR code generation for profile sharing.
* Native Android share sheet integration.

---

### Settings

Application configuration and maintenance tools.

Available settings include:

* Light theme.
* Dark theme.
* System theme.
* Dynamic accent color selection.
* JSON database backup.
* JSON database restoration.
* Application data reset.
* Developer contact information.
* Features Guide.
* About Portfolia.

---

## Technology Stack

The following table reflects the technologies currently used by the Portfolia codebase.

| Category                  | Implementation                                                         |
| :------------------------ | :--------------------------------------------------------------------- |
| **Programming Language**  | Kotlin 2.0.21                                                          |
| **UI Framework**          | Jetpack Compose with Material 3 Design (BOM 2024.12.01)                |
| **Architecture Pattern**  | Layered MVVM with Clean Architecture principles                        |
| **State Management**      | Kotlin StateFlow with `collectAsState()`                               |
| **Navigation**            | Navigation Compose 2.8.5                                               |
| **Local Database**        | Room 2.6.1                                                             |
| **User Preferences**      | DataStore Preferences 1.1.1                                            |
| **Lifecycle**             | `lifecycle-viewmodel-compose` 2.8.7, `lifecycle-runtime-compose` 2.8.7 |
| **Background Processing** | Kotlin Coroutines                                                      |
| **Image Loading**         | Coil Compose 2.7.0                                                     |
| **QR Code Generation**    | ZXing Core 3.5.3                                                       |
| **Serialization**         | Gson 2.11.0                                                            |
| **Browser Integration**   | AndroidX Browser 1.8.0                                                 |
| **Biometric Security**    | AndroidX Biometric 1.1.0                                               |
| **Icon Library**          | Material Icons Extended (BOM managed)                                  |
| **Annotation Processing** | Kotlin KAPT                                                            |
| **Build System**          | Gradle with Kotlin DSL (`build.gradle.kts`)                            |
| **Android Gradle Plugin** | 8.7.3                                                                  |
| **JVM Target**            | 17 (built locally using JDK 21)                                        |
| **Minimum SDK**           | Android 7.0 (API 24)                                                   |
| **Compile SDK**           | Android 15 (API 35)                                                    |
| **Networking**            | None — Fully Offline Architecture                                      |
| **Analytics**             | None — No telemetry, analytics, or tracking SDKs                       |
| **Version Control**       | Git                                                                    |

---

## Application Architecture

Portfolia follows a layered MVVM architecture with clear separation between presentation, business logic, and persistence. The architecture is designed to keep UI rendering independent from data management while supporting a reactive user experience.

### High-Level Architecture

```text
Presentation
      │
      ▼
Domain
      │
      ▼
Data
```

### Presentation Layer

Responsible for rendering the user interface and handling user interactions.

Components include:

* Jetpack Compose screens
* Reusable UI components
* Navigation graph
* Material 3 theming
* StateFlow collection using `collectAsState()`
* Animated screen transitions using `tween(220)` and `FastOutSlowInEasing`

---

### Domain Layer

The domain layer contains application logic responsible for transforming persistent entities into UI-friendly state representations.

Responsibilities include:

* Business logic
* Reactive state transformation
* ViewModel coordination
* UI state management

---

### Data Layer

The data layer manages all local persistence.

Primary components include:

* Room Database (`portfolia_db`)
* Room DAOs
* Room Entities
* DataStore Preferences
* TypeConverters
* Local repository implementations

No remote data sources or cloud synchronization are used.

---

## Project Structure

The repository is organized to separate user interface components, persistence, navigation, theming, and reusable resources.

```text
Portfolia/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   ├── ui/
│   │       │   │   ├── screens/
│   │       │   │   ├── components/
│   │       │   │   ├── navigation/
│   │       │   │   └── theme/
│   │       │   │
│   │       │   ├── data/
│   │       │   │   ├── dao/
│   │       │   │   ├── entities/
│   │       │   │   ├── datastore/
│   │       │   │   ├── converters/
│   │       │   │   └── database/
│   │       │   │
│   │       │   └── viewmodel/
│   │       │
│   │       ├── res/
│   │       └── AndroidManifest.xml
│   │
│   ├── build.gradle.kts
│   └── proguard-rules.pro
│
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── LICENSE
└── README.md
```

The project structure is organized to encourage modularity, maintainability, and a clear separation of responsibilities across the application.

---

## Database Overview

Portfolia persists all application data locally using Room Database.

| Property                | Value                                                     |
| :---------------------- | :-------------------------------------------------------- |
| **Database Name**       | `portfolia_db`                                            |
| **Database Version**    | 6                                                         |
| **ORM**                 | Room 2.6.1                                                |
| **Migration Strategy**  | `fallbackToDestructiveMigration()` *(development builds)* |
| **Preferences Storage** | DataStore Preferences                                     |
| **Type Converters**     | `Converters.kt` (`List<String>` serialization)            |

### Active Entities

| Entity            | Purpose                                  |
| :---------------- | :--------------------------------------- |
| `projects`        | Stores software project information.     |
| `user_profile`    | Stores developer profile information.    |
| `reference_links` | Stores categorized developer resources.  |
| `dev_secrets`     | Stores Base64-encoded developer secrets. |
| `code_snippets`   | Stores reusable source code snippets.    |

A detailed schema reference for each entity, DAO, and migration strategy is maintained alongside the source code as the project evolves.

---

## Data Flow

Portfolia follows a unidirectional data flow built on Kotlin Coroutines, StateFlow, Room, and Jetpack Compose. User interactions propagate through the presentation layer, business logic is processed within ViewModels, and persistent data is managed locally through Room and DataStore.

### Application Data Flow

```text id="uwu5qm"
User Interaction
        │
        ▼
Jetpack Compose UI
        │
        ▼
AndroidViewModel
        │
        ▼
StateFlow
        │
        ▼
Business Logic
        │
        ▼
Room DAO / DataStore
        │
        ▼
Room Database
        │
        ▼
Reactive State Update
        │
        ▼
Compose Recomposition
```

### Flow Description

1. User actions originate from Jetpack Compose screens.
2. Events are forwarded to the appropriate `AndroidViewModel`.
3. ViewModels execute application logic and coordinate persistence.
4. Room DAOs perform database operations against `portfolia_db`.
5. DataStore manages application preferences such as theme mode and accent color.
6. Updated state is emitted through `StateFlow`.
7. Compose observes state using `collectAsState()`, triggering automatic recomposition when data changes.

This architecture provides a predictable, reactive programming model while maintaining a clear separation between UI rendering and persistent data management.

---

## Development Setup

### Requirements

| Requirement           | Version                     |
| :-------------------- | :-------------------------- |
| Android Studio        | Ladybug (2024.2.1) or newer |
| JDK                   | 17                          |
| Kotlin                | 2.0.21                      |
| Android Gradle Plugin | 8.7.3                       |
| Compile SDK           | 35                          |
| Minimum SDK           | 24                          |
| Gradle                | Wrapper Managed             |
| Git                   | Latest Stable               |

### Clone the Repository

```bash
git clone https://github.com/abhi-s-aji/portfolia.git
cd portfolia
```

### Open the Project

Open the project in Android Studio and allow Gradle to complete project synchronization.

### Build the Project

```bash
./gradlew assembleDebug
```

### Install on a Connected Device

```bash
./gradlew installDebug
```

### Run Static Analysis

```bash
./gradlew lint
```

The project targets JVM 17 while being compiled locally using JDK 21. Android Studio automatically uses the Gradle Wrapper configuration provided by the repository.

---

## Development Guidelines

The project follows a documentation-first and maintainability-focused development process.

General expectations include:

* Keep changes focused on a single objective.
* Prefer small, reviewable pull requests.
* Maintain a clear separation of responsibilities.
* Keep documentation synchronized with implementation.
* Remove unused code rather than leaving commented sections.
* Avoid introducing undocumented behavior.
* Use descriptive names for classes, functions, variables, and resources.
* Keep dependencies to the minimum required.

Repository changes should improve readability, maintainability, and long-term stability.

---

## Code Style

Portfolia follows established Android development conventions.

Primary references include:

* Kotlin Coding Conventions
* Android Developers Style Guide
* Jetpack Compose API Guidelines

General expectations:

* Prefer immutable data where practical.
* Keep composables focused on a single responsibility.
* Write self-explanatory code before relying on comments.
* Use meaningful naming throughout the project.
* Keep files reasonably sized and logically organized.
* Maintain consistent formatting across the repository.

Consistency is prioritized over personal formatting preferences.

---

## Documentation Standards

Documentation is maintained alongside the source code to ensure technical accuracy.

Documentation should:

* Reflect the current implementation.
* Avoid speculative or future-facing descriptions.
* Remain concise and technically precise.
* Be updated as part of implementation changes.
* Use consistent terminology throughout the repository.

Repository documentation is considered part of the project and is reviewed alongside source code changes.

---

## Security

Portfolia follows a local-first security model and leverages Android platform security components to protect sensitive developer information.

### Biometric Authentication

The Developer Secret Vault is protected using AndroidX Biometric (`androidx.biometric:biometric:1.1.0`).

Authentication is automatically requested:

* When entering the Developer Secret Vault.
* Before exporting environment variables as `.env` files.
* Before copying JSON exports containing sensitive data.

The application supports:

* `BIOMETRIC_STRONG`
* `DEVICE_CREDENTIAL` fallback

`BiometricManager.canAuthenticate()` is used to determine device capability before displaying the authentication prompt.

### Secret Storage

Developer secrets are Base64-encoded before being persisted within the local Room database.

Current implementation provides local obfuscation of sensitive values.

Future iterations may introduce native encryption backed by Android KeyStore for additional protection.

### Clipboard Hygiene

Sensitive content copied from the Secret Vault or Code Snippets is automatically removed from the system clipboard after approximately 30 seconds.

Clipboard cleanup is performed using Kotlin Coroutines with clipboard content verification before clearing.

### Security Principles

Portfolia is developed with the following security objectives:

* Minimize exposure of sensitive information.
* Authenticate privileged operations.
* Store application data locally.
* Avoid unnecessary external dependencies.
* Document security-relevant implementation changes.

---

## Privacy

Portfolia follows a local-first privacy model.

Application data remains on the user's device unless the user explicitly chooses to export it.

The application:

* Does not transmit user data.
* Does not include analytics SDKs.
* Does not include advertising SDKs.
* Does not include crash reporting services.
* Does not perform background synchronization.
* Does not depend on cloud services.

Projects, references, code snippets, developer profiles, secrets, and preferences are stored locally using Room Database and DataStore.

Export operations use the Android Storage Access Framework, allowing users to choose the destination for exported files.

---

## Performance

Portfolia is designed around local data access and reactive state management to provide a responsive user experience.

Current implementation emphasizes:

* Jetpack Compose rendering.
* Reactive StateFlow updates.
* Local Room persistence.
* Kotlin Coroutines for asynchronous work.
* Efficient Compose recomposition.

Because the application does not perform network requests, core functionality remains available without network latency.

---

## Accessibility

Accessibility is considered during application development.

Current implementation follows standard Android accessibility practices where applicable, including:

* Material 3 components.
* Platform text scaling support.
* Native Android interaction patterns.

Accessibility improvements will continue as the project evolves.

---

## Localization

The current implementation targets English.

Additional language support may be introduced in future revisions as localization resources become available.

---

## Offline Support

Portfolia is designed as a fully offline application.

All primary functionality operates without an internet connection, including:

* Project management
* Reference links
* Code snippets
* Developer Secret Vault
* Developer profile
* QR code generation
* Settings and preferences
* Local backup and restore

The application does not require network connectivity for normal operation and declares no networking dependencies for its core features.

---

## Contributing

Contributions are welcome.

Before submitting changes:

* Fork the repository.
* Create a dedicated branch.
* Keep changes focused and well-scoped.
* Update documentation when implementation changes.
* Verify the project builds successfully.
* Submit a pull request for review.

Repository maintainers may request revisions before merging.

---

## Issue Reporting

GitHub Issues are used for bug reports, feature requests, and documentation improvements.

When opening an issue, include:

* A clear summary.
* Steps to reproduce.
* Expected behavior.
* Actual behavior.
* Environment information.
* Relevant screenshots or logs, when applicable.

Please search existing issues before creating a new report.

---

## Pull Request Guidelines

Pull requests should remain focused, readable, and easy to review.

Before submitting:

* Ensure the project builds successfully.
* Remove debugging code.
* Remove unused imports.
* Update documentation where necessary.
* Keep commits logically organized.

Reviews focus on:

* Correctness
* Readability
* Maintainability
* Documentation quality
* Repository consistency

---

## Branch Strategy

| Branch      | Purpose                    |
| ----------- | -------------------------- |
| `main`      | Stable production branch   |
| `develop`   | Primary development branch |
| `feature/*` | New features               |
| `fix/*`     | Bug fixes                  |
| `release/*` | Release preparation        |
| `hotfix/*`  | Production hotfixes        |

---

## Roadmap

The roadmap reflects areas of ongoing development.

* [ ] Expand project documentation.
* [ ] Improve architecture documentation.
* [ ] Enhance backup and restore capabilities.
* [ ] Continue accessibility improvements.
* [ ] Expand localization support.
* [ ] Introduce comprehensive automated testing.
* [ ] Continue performance optimizations.
* [ ] Strengthen local data protection mechanisms.
* [ ] Improve long-term maintainability.

---

## License

Portfolia is licensed under the Apache License, Version 2.0.

See the LICENSE file for the complete license text.

### Source File Header

/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2026 Abhi S Aji
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

---

## Author

### Abhi S Aji

Maintainer of the Portfolia project.

* GitHub: https://github.com/abhi-s-aji
* LinkedIn: https://www.linkedin.com/in/abhi-s-aji-eden

---

© 2026 Abhi S Aji

Licensed under the Apache License, Version 2.0.
