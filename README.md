# Draftly - All-in-One Document Viewer

<img width="569" height="289" alt="banner" src="https://github.com/user-attachments/assets/514af264-7834-430b-8e7f-85f42c15067f" />


**Draftly** is a modern and powerful Android application designed to provide a seamless document viewing experience. Whether it's a critical business report, a complex spreadsheet, or a creative presentation, Draftly ensures you can access and view your documents with ease and speed.

## 🚀 Key Features

-   **Wide Format Support**: Seamlessly view PDF, Microsoft Word (DOC/DOCX), Excel (XLS/XLSX), and PowerPoint (PPT/PPTX) files.
-   **Modern UI/UX**: Built entirely with **Jetpack Compose**, featuring a clean, responsive, and intuitive Material 3 design.
-   **High Performance**: Optimized document loading using the **Apache POI** engine for office documents and native PDF rendering.
-   **Deep Integration**: Handles document viewing requests from other apps (Email, File Managers, etc.).
-   **Recent Files**: Quickly pick up where you left off with a persistent history of recently opened documents.
-   **Dark Mode**: A beautiful dark theme that’s easy on the eyes.
-   **Privacy Focused**: Works entirely on-device with no external server processing of your documents.

## 🛠️ Tech Stack

-   **Language**: [Kotlin](https://kotlinlang.org/)
-   **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3)
-   **Architecture**: MVVM (Model-View-ViewModel)
-   **Processing Engine**: [Apache POI](https://poi.apache.org/) (for Word, Excel, PPT)
-   **Database**: [Room](https://developer.android.com/training/data-storage/room) (for Recent Files)
-   **Data Storage**: [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) (for Settings)
-   **Image Loading**: [Coil](https://coil-kt.github.io/coil/)
-   **Async Operations**: [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html)

## 📦 Getting Started

### Prerequisites

-   Android Studio Iguana or newer.
-   Android SDK Level 26+.
-   Gradle 8.0+.

### Installation

1.  Clone the repository:
    ```bash
    git clone https://github.com/yourusername/Draftly.git
    ```
2.  Open the project in **Android Studio**.
3.  Sync the project with Gradle files.
4.  Run the app on an emulator or a physical device (Minimum SDK 26).

## 📂 Project Structure

-   `app/src/main/java/com/docviewer/allinone/`
    -   `data/`: Repositories, DAOs, and Data Models.
    -   `ui/`: Compose Screens, ViewModels, and Theme.
    -   `viewer/`: Document processing logic and viewer factories.

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

*Made with ❤️ for a better document viewing experience.*
