# Notes App 

A simple and modern Android Notes Application developed in Kotlin.

The application implements full **CRUD (Create, Read, Update, Delete)** functionality using a local **Room Database**, allowing users to create, manage, update, and delete notes entirely offline.

---

## Features

### Core CRUD Operations

* Create new notes
* Read and display saved notes
* Update existing notes
* Delete unwanted notes

### Additional Features

* Pin important notes
* Live note searching
* Random color-coded note cards
* List view and Grid view layouts
* Offline data persistence using Room Database
* Fast note transfer using Parcelable
* Material Design 3 user interface

---

## Technologies Used

* Kotlin
* Android Studio
* Room Database
* RecyclerView
* Material Design 3 Components
* Kotlin Coroutines
* Parcelable
* ConstraintLayout

---

## Application Architecture

### MainActivity

The main screen of the application is:

* Displays all saved notes
* Supports list/grid layout switching
* Provides search functionality
* Filters pinned notes
* Launches the note editor

### NoteTakerActivity

The note editor screen, where users can:

* Create new notes
* Edit existing notes
* Save changes
* Navigate back to the home screen

### Room Database Components

#### Notes.kt

Defines the Note entity and data model.

#### NotesDao.kt

Contains database operations:

* Insert Note
* Retrieve Notes
* Update Note
* Delete Note

#### RoomDB.kt

Singleton Room Database instance used throughout the application.

---

## User Interface

### Home Screen

* RecyclerView displaying all notes
* Floating Action Button (FAB) for note creation
* Search bar for quick filtering
* Toolbar menu options
* Empty-state message when no notes exist

### Note Editor Screen

* Title input field
* Content input field
* Save button
* Back button
* Simple and distraction-free layout

---

## Database Structure

Each note stores:

| Field      | Description            |
| ---------- | ---------------------- |
| id         | Unique note identifier |
| title      | Note title             |
| notes      | Note content           |
| color_code | Card color             |
| date       | Creation/update date   |
| isStarred  | Pin status             |

---

## How to Run

### Prerequisites

* Android Studio
* Android SDK
* Kotlin support enabled

### Installation

1. Clone the repository:

```bash
git clone https://github.com/sovithyea/crud_note_app
```

2. Open the project in Android Studio.

3. Allow Gradle to sync dependencies.

4. Run the application on:

   * Android Emulator
   * Physical Android Device

---

## Design Decisions

The Notes App was chosen because it naturally demonstrates all CRUD operations while remaining practical and user-friendly.

Key design goals included:

* Simple navigation
* Offline functionality
* Fast performance
* Clean UI
* Easy note organization

To improve usability, large labeled pill buttons were implemented instead of small toolbar icons after user testing showed improved task completion speed and user satisfaction.

---

## Usability Investigation

A usability study compared:

### Version A

Toolbar icon controls

### Version B

Large labeled pill buttons

Results from six participants showed:

| Metric                  | Toolbar Icons | Pill Buttons |
| ----------------------- | ------------- | ------------ |
| Average Completion Time | 37.2s         | 26.3s        |
| Average Ease Rating     | 3.0 / 5       | 4.8 / 5      |

### Conclusion

Pill buttons were:

* Easier to discover
* Faster to use
* More accessible for new users

Therefore, the pill button design was adopted in the final application.

---

## Future Improvements

Potential enhancements include:

* Dark mode support
* Categories and tags
* Cloud synchronization
* Backup and restore functionality
* Rich text formatting
* Reminder notifications

---

## Learning Outcomes

This project strengthened skills in:

* Android application development
* Kotlin programming
* Room Database implementation
* RecyclerView customization
* Material Design principles
* User-centered design
* Usability testing

---

## Author

**Sovithyea Prach**

---

## Repository

GitHub Repository:

https://github.com/sovithyea/crud_note_app

---

## References

* Android Room Database Documentation
* Android RecyclerView Documentation
* Material Design 3 Guidelines
* Kotlin Coroutines Documentation
* Android Layouts and Views Documentation
* Android Intents Documentation
* Coolors Color Palette Generator
