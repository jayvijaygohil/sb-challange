# GitPeek

GitPeek is a simple Android application that allows users to search for Github users and view their repositories.

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Dependency Injection**: Koin
- **Network**: Retrofit & OkHttp
- **Asynchronous**: Coroutines & Flow
- **Navigation**: Jetpack Navigation
- **Image Loading**: Coil
- **State Management**: Orbit MVI

## Architecture

The project follows the principles of **Clean Architecture** and uses the **MVI** (Model-View-Intent) pattern using **Orbit MVI**.

It is divided into three main layers:
- **Presentation**: UI components, ViewModels, and Navigation.
- **Domain**: Business logic, Use Cases, and Repository interfaces.
- **Data**: Repository implementations, API services, and data mappers.