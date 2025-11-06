To-Do List Application

Overview

This To-Do List Android application allows users to efficiently manage their daily tasks. It provides functionality to add, edit, delete, and mark tasks as completed. Tasks are organized based on their due dates — Today, Tomorrow, and Upcoming — and users can also filter them by completion status or specific dates.

🛠️ Setup and Installation Instructions

Prerequisites
    1. Android Studio (latest version recommended)
    2. Gradle (automatically managed by Android Studio)
    3. JDK 17 or higher
    4. Internet connection for dependency downloads
Steps to Run the Project
    1. Clone or download the project from the repository:
       git clone https://github.com/your-repo/todoapp.git
    2. Open the project in Android Studio.
    3. Allow Android Studio to sync Gradle and download dependencies.
    4. Connect an Android emulator or physical device.
    5. Click Run ▶ to build and launch the app.


Architecture & Platform Choice

The app uses MVVM architecture for clean separation of concerns and easy testing.
      1. Model: Task data class and TaskRepository for data management.
      2. ViewModel: HomeViewModel,AddEditViewModel for handling logic and UI state with StateFlow.
      3. View: Composable UI built with Jetpack Compose.
      4. Hilt DI: Simplifies dependency injection.
      5. Navigation Component: Handles screen transitions.
Why Kotlin & Compose:
      Modern, concise, reactive, and ideal for scalable Android app development.

Challenges & Solutions
      1. Challenge: Writing tests for the first time.
      2. Solution: Learned JUnit and Espresso testing through tutorials, practiced testing repository and ViewModel logic, and gradually built confidence in testing.


Installation instructions 

Step 1: Enable installations from unknown sources 

For modern Android versions (Oreo and newer):
  1. Go to Settings > Apps & notifications. 
  2. Tap Special app access > Install unknown apps. 
  3. Select the app you'll use to install the APK (e.g., Chrome or "Files") and toggle Allow from this source to on. 
  
For older Android versions (up to Marshmallow):
  1. Go to Settings > Security. 
  2. Check the box for Unknown sources and tap OK. 

Step 2: Install the APK file 

  1. Download the APK file to your Android device. 
  2. Open your file manager app (it might be called "Files," "My Files," or "File Browser") and navigate to where you saved the APK file, likely your "Downloads" folder. 
  3. Tap the APK file to begin the installation process. 
  4. Review the requested permissions and tap Install. 
  5. Once the installation is complete, you can tap Open to launch the app. 
