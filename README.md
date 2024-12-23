# TrailTracker

**TrailTracker** is a fitness tracking app designed for outdoor enthusiasts, providing a seamless way to monitor and improve physical performance. Built using Jetpack Compose, Hilt, and Kotlin Multiplatform, TrailTracker utilizes GPS tracking to measure distance covered, speed, and duration during sessions. It also features real-time graphing and visualizations for monitoring progress over time.

### Features
- **Real-Time GPS Tracking**: Track distance, speed, and route with precise GPS monitoring.
- **Progress Visualizations**: Interactive charts display **overall**, **weekly**, and **daily** metrics, allowing users to analyze trends and improvements.
- **Customizable Notifications**: Stay informed with foreground notifications during active tracking sessions, showing session details and progress.
- **Session Summary and History**: View summaries of each session, including color-coded routes based on speed, with storage options in Firebase for cross-device access.
- **Offline-First Mode**: Stores session data locally, syncing to Firebase when network is available, so you can track runs seamlessly without worrying about connectivity.

TrailTracker empowers users to measure their physical activities, identify trends, and achieve fitness goals efficiently.

## Tech Stack

- **Kotlin**: Primary programming language used to develop the app, allowing for efficient code sharing across platforms.
- **Jetpack Compose**: Used for building responsive and modern UI with a declarative approach.
- **Kotlin Multiplatform (KMP)**: Enables sharing code between Android and iOS platforms, reducing development time and maintaining consistency.
- **Dagger Hilt**: Dependency injection framework to manage and provide dependencies across components.
- **Firebase**: 
  - **Firebase Authentication**: Manages secure user authentication.
  - **Firebase Firestore**: Stores and syncs user data in real-time.
  - **Firebase Storage**: Stores session images and other media.
- **WorkManager**: Handles background tasks reliably, especially for syncing data between Room database and Firebase.
- **Navigation Component**: Manages in-app navigation across multiple screens.
- **Coil**: Asynchronous image loading library to display images and manage caching.
- **Retrofit**: Makes API requests and handles network operations with a concise syntax.
- **Room Database**: Local storage solution to save session data and sync with Firebase for offline capabilities.
- **Coroutines & Flows**: Supports asynchronous operations, ensuring smooth user experience by handling background tasks efficiently.

This stack enables TrailTracker to have a highly modular and responsive design, streamlined background processing, and robust data handling for seamless tracking and analysis.


## Screenshots
<p align="center">
  <img src="https://github.com/user-attachments/assets/7657070d-8cbc-49b4-94ff-330dc1286fd2" width="200" style="margin-right: 80px;" alt="Image 1">
  <img src="https://github.com/user-attachments/assets/0cf36678-9850-4e2b-b2c0-768ccf772d7d" width="200" style="margin-right: 80px;" alt="Image 2">
  <img src="https://github.com/user-attachments/assets/300cea69-8a69-4b2a-b4c0-9ca6756d9071" width="200" style="margin-right: 80px;" alt="Image 3">
  <img src="https://github.com/user-attachments/assets/30a96811-6801-4c88-9fea-2fac891591d9" width="200" style="margin-right: 80px;" alt="Image 4">
  <img src="https://github.com/user-attachments/assets/945a5ad8-f005-4a3b-9aca-b236125fcfa1" width="200" style="margin-right: 80px;" alt="Image 5">
  <img src="https://github.com/user-attachments/assets/95912a97-495c-434f-818e-05a7a23fd977" width="200" style="margin-right: 80px;" alt="Image 6">
  <img src="https://github.com/user-attachments/assets/759d2acd-d24d-4243-984a-ca24e418181c" width="200" alt="Image 7">
</p>
