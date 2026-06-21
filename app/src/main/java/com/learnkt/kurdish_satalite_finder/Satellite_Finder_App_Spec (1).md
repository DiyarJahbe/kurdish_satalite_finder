# Satellite Finder App — Kotlin Jetpack Compose (Clean Architecture) Full Specification

## 1. Project Overview
Build a production-grade Android satellite dish alignment app using:
- Kotlin
- Jetpack Compose
- MVVM
- Clean Architecture
- Material 3

The app helps users align satellite dishes (e.g., Türksat, Nilesat, Hotbird) using:
- GPS location
- Compass sensors
- Satellite orbit calculations
- AR/camera guidance (optional)
- Offline database + online sync

---

## 2. Core Goal
A premium Kurdish-first satellite alignment tool that provides:
- Accurate azimuth, elevation, and LNB skew calculations
- Step-by-step dish setup guidance
- Real satellite and transponder data (offline + sync)
- Beautiful modern UI
- Compass + AR guidance mode

---

## 3. Tech Stack
- Kotlin
- Jetpack Compose (Material 3)
- MVVM architecture
- Clean Architecture (Data / Domain / Presentation)
- Hilt (DI)
- Room (offline DB)
- Retrofit (remote data)
- Coroutines + Flow
- DataStore (preferences)
- CameraX (AR mode optional)
- SensorManager (compass/orientation)
- Location Services

---

## 4. Architecture Layers

### Presentation Layer
- Compose UI screens
- ViewModels
- UI State models
- Navigation
- Theme system

### Domain Layer
- Use cases
- Business logic
- Satellite calculation engine
- Repository interfaces

### Data Layer
- Room database
- API services
- DTOs
- Repository implementations
- Sync system

---

## 5. App Features

### Home Dashboard
- Satellite shortcuts
- Favorites preview
- Location status
- Quick actions:
  - Compass mode
  - AR mode
  - Setup wizard
  - Transponders
  - Receiver helper

### Satellite List
- Search satellites
- Filters (favorites, visible satellites)
- Sorting options

### Satellite Detail
- Azimuth / Elevation / Skew
- Visibility status
- Favorite toggle
- Open compass / AR mode
- View transponders

### Compass Mode
- Live device heading
- Target azimuth arrow
- “Turn left/right” guidance
- Calibration support

### AR Mode
- Camera preview
- Overlay satellite direction
- Visual guidance markers

### Favorites
- Save satellites locally
- Quick access

### Transponders
- Frequency list
- Polarization (H/V)
- Symbol rate
- Copy/share

### Dish Setup Wizard
Step-by-step guide:
1. Choose satellite
2. Get location
3. Show angles
4. Compass guidance
5. Fine tuning steps

### Manual Location
- GPS auto-detect
- Manual coordinates
- City selection

### Receiver Helper
- LNB setup guide
- Scan settings
- Frequency examples

### Signal Helper
- Teaching mode (strength vs quality)
- Alignment tips

---

## 6. Satellite Calculation Engine

Must compute:
- Azimuth
- Elevation
- LNB Skew

Based on:
- User latitude/longitude
- Satellite orbital longitude

Must use real geostationary orbit math.

---

## 7. Offline Database
Room database stores:
- Satellites
- Transponders
- Favorites
- Locations
- Cached guides

Supports offline usage fully.

---

## 8. Remote Data Strategy
- Use real public/official satellite frequency sources where possible
- Example: operator frequency lists
- Sync system with local cache fallback
- No fake APIs

---

## 9. UI/UX Design
- Material 3 design system
- Colorful modern UI
- Kurdish-first language
- Smooth animations
- Card-based layout
- Easy navigation
- Dark mode support

---

## 10. Localization
Primary language:
- Kurdish (Sorani)

Secondary:
- English

All UI must use string resources.

---

## 11. Sensor Usage
- Compass (azimuth)
- Accelerometer (tilt)
- Magnetometer
- Rotation vector sensor

---

## 12. Navigation Screens
- Home
- Satellite List
- Satellite Detail
- Compass
- AR Mode
- Favorites
- Transponders
- Setup Wizard
- Settings

---

## 13. Clean Architecture Structure

data/
domain/
presentation/
core/

Feature-based modules allowed.

---

## 14. Output Requirements for AI Code Generator

When generating code:
1. Show architecture first
2. Show full file tree
3. Generate files step-by-step:
   - Gradle setup
   - DI
   - Database
   - Domain models
   - Use cases
   - ViewModels
   - UI screens
   - Navigation
   - Theme
4. Ensure full buildable project

---

## 15. Important Rules
- No fake signal data
- No hardcoded APIs unless seed data
- Clean separation of layers
- Real math for satellite angles
- Highly reusable Compose components
- Production-level structure

---

## 16. Example Satellites
- Türksat 42E
- Nilesat 7W
- Hotbird 13E
- Astra 19E

---

## 17. Final Goal
A premium, production-ready satellite alignment Android app:
- Beautiful UI
- Accurate calculations
- Offline support
- Kurdish-first UX
- Advanced architecture
