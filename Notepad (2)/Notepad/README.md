# JavaFX Notepad Application

A fully functional notepad application built with JavaFX that demonstrates various Java programming concepts.

## Features

- **File Operations**: Create new files, open existing files, save, and save as
- **Text Editing**: Cut, copy, paste, and select all text
- **Keyboard Shortcuts**: Standard shortcuts for common operations (Ctrl+N, Ctrl+O, Ctrl+S, etc.)
- **Unsaved Changes Detection**: Prompts to save changes before closing
- **About Dialog**: Information about the application

## Java Concepts Demonstrated

### Object-Oriented Programming (OOP)
- **Classes and Objects**: The application is structured as a class with instance variables
- **Inheritance**: Extends `Application` class from JavaFX
- **Encapsulation**: Private fields with controlled access through methods
- **Method Overriding**: Overrides `start()` method from `Application` class

### JavaFX GUI Components
- **Stage and Scene**: Window management and scene graph
- **Layout Management**: `BorderPane` for organizing UI components
- **Controls**: `TextArea`, `MenuBar`, `Menu`, `MenuItem`, `Button`
- **Event Handling**: Lambda expressions for event handlers
- **Property Binding**: `textProperty()` listener for tracking modifications

### File I/O Operations
- **FileChooser**: Native file selection dialogs
- **NIO.2 API**: Modern file I/O using `java.nio.file` package
- **Exception Handling**: Try-catch blocks for file operations
- **File Paths**: Working with absolute and relative file paths

### Advanced Java Concepts
- **Lambda Expressions**: Used for event handlers and property listeners
- **Functional Interfaces**: EventHandler and ChangeListener interfaces
- **Optional**: Using `Optional<ButtonType>` for dialog results
- **Java Collections**: List operations for menu items

### User Interface Design
- **Menu Systems**: Hierarchical menu structure
- **Keyboard Accelerators**: Key combinations for quick access
- **Dialogs**: Alert dialogs for user confirmation and information
- **Window Management**: Close request handling and confirmation

## Project Structure

```
AP- LAB APPS/
├── pom.xml                          # Maven build configuration
├── README.md                        # This file
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── notepad/
        │           └── NotepadApp.java  # Main application class
        └── resources/                # Resource files (empty)
```

## Requirements

- Java 17 or higher
- Maven 3.6 or higher
- JavaFX 21.0.1 (automatically managed by Maven)

## Building the Project

```bash
mvn clean compile
```

## Running the Application

### Using Maven

```bash
mvn javafx:run
```

### Using Java directly

First, build the project:

```bash
mvn clean package
```

Then run with Java (module path required for JavaFX):

```bash
java --module-path "path/to/javafx/lib" --add-modules javafx.controls,javafx.fxml -cp target/classes com.notepad.NotepadApp
```

## Usage Instructions

1. **Creating a New File**: Click `File > New` or press `Ctrl+N`
2. **Opening a File**: Click `File > Open` or press `Ctrl+O`
3. **Saving a File**: Click `File > Save` or press `Ctrl+S`
4. **Saving As**: Click `File > Save As` or press `Ctrl+Shift+S`
5. **Text Editing**: Use the Edit menu or standard keyboard shortcuts
6. **Exiting**: Click `File > Exit` or close the window (will prompt for unsaved changes)

## Keyboard Shortcuts

- `Ctrl+N` - New file
- `Ctrl+O` - Open file
- `Ctrl+S` - Save file
- `Ctrl+Shift+S` - Save as
- `Ctrl+X` - Cut
- `Ctrl+C` - Copy
- `Ctrl+V` - Paste
- `Ctrl+A` - Select all

## Technical Details

### Maven Configuration
- Uses JavaFX Maven plugin for running JavaFX applications
- Configured for Java 17 compatibility
- Includes JavaFX controls and FXML dependencies

### Error Handling
- Graceful handling of file I/O exceptions
- User-friendly error messages via alert dialogs
- Confirmation dialogs for destructive operations

### State Management
- Tracks current file path
- Monitors document modification state
- Updates window title to reflect current state

## Future Enhancements

Potential features to add for further learning:
- Find and replace functionality
- Undo/Redo operations
- Multiple document interface (tabs)
- Syntax highlighting
- Recent files list
- Font customization
- Word count and statistics
- Print functionality

## Learning Outcomes

This project demonstrates:
- How to structure a JavaFX application
- Implementation of common desktop application features
- Proper error handling and user feedback
- Clean code practices and documentation
- Integration of multiple Java APIs (JavaFX, NIO, Collections)
