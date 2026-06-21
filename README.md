# Kurdish Satellite Finder (دۆزەرەوەی مانگی دەستکرد)

A production-grade Android application for satellite dish alignment, localized in Kurdish (Sorani). Built with modern Android technologies following Clean Architecture principles.

## 📱 Features

- **Accurate Calculations**: Real-time calculation of Azimuth, Elevation, and LNB Skew.
- **Kurdish First**: Fully localized in Sorani Kurdish for a native user experience.
- **Compass Guidance**: Visual compass mode to help users align their dishes accurately.
- **Satellite Database**: Offline-first database with major satellites like Türksat, Nilesat, and Hotbird.
- **Modern UI**: Beautiful Material 3 design with RTL support.
- **Clean Architecture**: Highly maintainable and testable codebase (Data, Domain, Presentation layers).

## 🛠 Tech Stack

- **Kotlin**: Primary programming language.
- **Jetpack Compose**: Modern toolkit for building native UI.
- **Hilt (Dagger)**: Dependency injection for cleaner code and easier testing.
- **Room**: Offline database for storing satellite data.
- **Navigation Compose**: Type-safe navigation between screens.
- **Coroutines & Flow**: Asynchronous programming and reactive data streams.
- **MVVM Architecture**: Separation of concerns for better maintainability.

## 📐 Architecture

The project follows **Clean Architecture**:

- **`domain`**: Contains business logic, models, and repository interfaces.
- **`data`**: Implements data sources (Room database, API) and repository implementations.
- **`presentation`**: UI layer containing Compose screens, ViewModels, and navigation logic.
- **`core`**: Common utilities and shared components.

## 🌍 Localization

Primary Language: **Central Kurdish (Sorani)**
Secondary Language: **English**

## 🚀 Getting Started

1. Clone the repository.
2. Open in Android Studio (Ladybug or later).
3. Build and run on an Android device or emulator.

## 📸 Screenshots

## 📄 License

This project is licensed under the MIT License.
