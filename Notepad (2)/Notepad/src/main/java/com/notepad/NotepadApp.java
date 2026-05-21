package com.notepad;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Main JavaFX application class for the Notepad application.
 * This class demonstrates JavaFX application lifecycle and window management.
 */
public class NotepadApp extends Application {
    
    private Stage primaryStage;
    private TextArea textArea;
    private String currentFilePath = null;
    private boolean isModified = false;
    private Label statusLabel;
    private Label lineColLabel;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("JavaFX Notepad");
        
        // Create main layout
        BorderPane root = new BorderPane();
        
        // Create menu bar
        MenuBar menuBar = createMenuBar();
        
        // Create toolbar
        ToolBar toolBar = createToolBar();
        
        // Create text area
        textArea = new TextArea();
        textArea.setWrapText(true);
        textArea.setStyle("-fx-font-family: 'Monospace'; -fx-font-size: 14px;");
        
        // Track modifications
        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!isModified) {
                isModified = true;
                updateWindowTitle();
            }
            updateStatus();
        });
        
        // Track caret position for line/column display
        textArea.caretPositionProperty().addListener((observable, oldValue, newValue) -> {
            updateLineColumn();
        });
        
        // Create status bar
        HBox statusBar = createStatusBar();
        
        // Create top container for menu bar and toolbar
        VBox topContainer = new VBox(menuBar, toolBar);
        topContainer.setFillWidth(true);
        
        // Add components to layout
        root.setTop(topContainer);
        root.setCenter(textArea);
        root.setBottom(statusBar);
        
        // Create scene
        Scene scene = new Scene(root, 800, 600);
        
        // Load and apply CSS styles
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        
        // Set scene and show stage
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Handle window close request
        primaryStage.setOnCloseRequest(event -> {
            if (!confirmClose()) {
                event.consume();
            }
        });
    }
    
    /**
     * Creates the menu bar with File, Edit, and Help menus.
     * Demonstrates JavaFX Menu and MenuItem components.
     */
    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        
        // File Menu
        Menu fileMenu = new Menu("File");
        
        MenuItem newItem = new MenuItem("New");
        newItem.setAccelerator(KeyCombination.keyCombination("Ctrl+N"));
        newItem.setOnAction(e -> newFile());
        
        MenuItem openItem = new MenuItem("Open");
        openItem.setAccelerator(KeyCombination.keyCombination("Ctrl+O"));
        openItem.setOnAction(e -> openFile());
        
        MenuItem saveItem = new MenuItem("Save");
        saveItem.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));
        saveItem.setOnAction(e -> saveFile());
        
        MenuItem saveAsItem = new MenuItem("Save As");
        saveAsItem.setAccelerator(KeyCombination.keyCombination("Ctrl+Shift+S"));
        saveAsItem.setOnAction(e -> saveFileAs());
        
        SeparatorMenuItem separator1 = new SeparatorMenuItem();
        
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> {
            if (confirmClose()) {
                primaryStage.close();
            }
        });
        
        fileMenu.getItems().addAll(newItem, openItem, saveItem, saveAsItem, separator1, exitItem);
        
        // Edit Menu
        Menu editMenu = new Menu("Edit");
        
        MenuItem cutItem = new MenuItem("Cut");
        cutItem.setAccelerator(KeyCombination.keyCombination("Ctrl+X"));
        cutItem.setOnAction(e -> textArea.cut());
        
        MenuItem copyItem = new MenuItem("Copy");
        copyItem.setAccelerator(KeyCombination.keyCombination("Ctrl+C"));
        copyItem.setOnAction(e -> textArea.copy());
        
        MenuItem pasteItem = new MenuItem("Paste");
        pasteItem.setAccelerator(KeyCombination.keyCombination("Ctrl+V"));
        pasteItem.setOnAction(e -> textArea.paste());
        
        SeparatorMenuItem separator2 = new SeparatorMenuItem();
        
        MenuItem selectAllItem = new MenuItem("Select All");
        selectAllItem.setAccelerator(KeyCombination.keyCombination("Ctrl+A"));
        selectAllItem.setOnAction(e -> textArea.selectAll());
        
        editMenu.getItems().addAll(cutItem, copyItem, pasteItem, separator2, selectAllItem);
        
        // Help Menu
        Menu helpMenu = new Menu("Help");
        
        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(e -> showAboutDialog());
        
        helpMenu.getItems().add(aboutItem);
        
        menuBar.getMenus().addAll(fileMenu, editMenu, helpMenu);
        
        return menuBar;
    }
    
    /**
     * Creates the toolbar with common action buttons.
     */
    private ToolBar createToolBar() {
        ToolBar toolBar = new ToolBar();
        toolBar.setStyle("-fx-background-color: #313244; -fx-border-color: #45475a; -fx-border-width: 0 0 1 0;");
        
        // New button
        Button newBtn = createStyledButton("New", "📄");
        newBtn.setOnAction(e -> newFile());
        
        // Open button
        Button openBtn = createStyledButton("Open", "📂");
        openBtn.setOnAction(e -> openFile());
        
        // Save button
        Button saveBtn = createStyledButton("Save", "💾");
        saveBtn.setOnAction(e -> saveFile());
        
        // Separator
        Separator separator1 = new Separator();
        separator1.getStyleClass().add("toolbar-separator");
        
        // Cut button
        Button cutBtn = createStyledButton("Cut", "✂️");
        cutBtn.setOnAction(e -> textArea.cut());
        
        // Copy button
        Button copyBtn = createStyledButton("Copy", "📋");
        copyBtn.setOnAction(e -> textArea.copy());
        
        // Paste button
        Button pasteBtn = createStyledButton("Paste", "📌");
        pasteBtn.setOnAction(e -> textArea.paste());
        
        // Separator
        Separator separator2 = new Separator();
        separator2.getStyleClass().add("toolbar-separator");
        
        // About button
        Button aboutBtn = createStyledButton("About", "ℹ️");
        aboutBtn.setOnAction(e -> showAboutDialog());
        
        toolBar.getItems().addAll(newBtn, openBtn, saveBtn, separator1, cutBtn, copyBtn, pasteBtn, separator2, aboutBtn);
        
        return toolBar;
    }
    
    /**
     * Creates a styled button for the toolbar.
     */
    private Button createStyledButton(String text, String emoji) {
        Button button = new Button(emoji + " " + text);
        button.setStyle("-fx-background-color: #45475a; -fx-text-fill: #cdd6f4; -fx-background-radius: 6; -fx-padding: 8 16 8 16; -fx-font-size: 12px; -fx-font-weight: 600; -fx-cursor: hand;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #585b70; -fx-text-fill: #f5e0dc; -fx-background-radius: 6; -fx-padding: 8 16 8 16; -fx-font-size: 12px; -fx-font-weight: 600; -fx-cursor: hand; -fx-scale-x: 1.05; -fx-scale-y: 1.05;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #45475a; -fx-text-fill: #cdd6f4; -fx-background-radius: 6; -fx-padding: 8 16 8 16; -fx-font-size: 12px; -fx-font-weight: 600; -fx-cursor: hand;"));
        button.setOnMousePressed(e -> button.setStyle("-fx-background-color: #313244; -fx-text-fill: #cdd6f4; -fx-background-radius: 6; -fx-padding: 8 16 8 16; -fx-font-size: 12px; -fx-font-weight: 600; -fx-cursor: hand; -fx-scale-x: 0.98; -fx-scale-y: 0.98;"));
        return button;
    }
    
    /**
     * Creates the status bar with line/column and character count information.
     */
    private HBox createStatusBar() {
        HBox statusBar = new HBox();
        statusBar.setStyle("-fx-background-color: #313244; -fx-border-color: #45475a; -fx-border-width: 1 0 0 0; -fx-padding: 8 16 8 16;");
        statusBar.setSpacing(20);
        statusBar.setPadding(new Insets(8, 16, 8, 16));
        
        // Line and column label
        lineColLabel = new Label("Line: 1, Column: 1");
        lineColLabel.setStyle("-fx-text-fill: #a6adc8; -fx-font-size: 11px;");
        
        // Status label (shows file info)
        statusLabel = new Label("Ready");
        statusLabel.setStyle("-fx-text-fill: #a6adc8; -fx-font-size: 11px;");
        
        // Character count label
        Label charCountLabel = new Label();
        charCountLabel.setStyle("-fx-text-fill: #a6adc8; -fx-font-size: 11px;");
        charCountLabel.textProperty().bind(
            javafx.beans.binding.Bindings.createStringBinding(
                () -> "Characters: " + textArea.getLength(),
                textArea.lengthProperty()
            )
        );
        
        // Word count label
        Label wordCountLabel = new Label();
        wordCountLabel.setStyle("-fx-text-fill: #a6adc8; -fx-font-size: 11px;");
        wordCountLabel.textProperty().bind(
            javafx.beans.binding.Bindings.createStringBinding(
                () -> "Words: " + countWords(textArea.getText()),
                textArea.textProperty()
            )
        );
        
        HBox.setHgrow(statusLabel, Priority.ALWAYS);
        
        statusBar.getChildren().addAll(lineColLabel, statusLabel, charCountLabel, wordCountLabel);
        
        return statusBar;
    }
    
    /**
     * Updates the status bar with current file information.
     */
    private void updateStatus() {
        if (currentFilePath == null) {
            statusLabel.setText("Untitled" + (isModified ? " *" : ""));
        } else {
            String fileName = new java.io.File(currentFilePath).getName();
            statusLabel.setText(fileName + (isModified ? " *" : ""));
        }
    }
    
    /**
     * Updates the line and column display based on caret position.
     */
    private void updateLineColumn() {
        int caretPos = textArea.getCaretPosition();
        String text = textArea.getText();
        
        int line = 1;
        int column = 1;
        
        for (int i = 0; i < caretPos; i++) {
            if (text.charAt(i) == '\n') {
                line++;
                column = 1;
            } else {
                column++;
            }
        }
        
        lineColLabel.setText("Line: " + line + ", Column: " + column);
    }
    
    /**
     * Counts the number of words in the text.
     */
    private int countWords(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        String[] words = text.trim().split("\\s+");
        return words.length;
    }
    
    /**
     * Creates a new file by clearing the text area.
     */
    private void newFile() {
        if (isModified && !confirmClose()) {
            return;
        }
        
        textArea.clear();
        currentFilePath = null;
        isModified = false;
        updateWindowTitle();
    }
    
    /**
     * Opens a file using FileChooser.
     * Demonstrates JavaFX FileChooser and file I/O operations.
     */
    private void openFile() {
        if (isModified && !confirmClose()) {
            return;
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Text Files", "*.txt"),
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        
        java.io.File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            try {
                String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
                textArea.setText(content);
                currentFilePath = file.getAbsolutePath();
                isModified = false;
                updateWindowTitle();
            } catch (java.io.IOException e) {
                showAlert("Error", "Could not open file: " + e.getMessage());
            }
        }
    }
    
    /**
     * Saves the current file.
     */
    private void saveFile() {
        if (currentFilePath == null) {
            saveFileAs();
        } else {
            try {
                java.nio.file.Files.write(
                    java.nio.file.Paths.get(currentFilePath),
                    textArea.getText().getBytes()
                );
                isModified = false;
                updateWindowTitle();
            } catch (java.io.IOException e) {
                showAlert("Error", "Could not save file: " + e.getMessage());
            }
        }
    }
    
    /**
     * Saves the file with a new name/location using FileChooser.
     */
    private void saveFileAs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File As");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Text Files", "*.txt"),
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        
        java.io.File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            try {
                java.nio.file.Files.write(
                    file.toPath(),
                    textArea.getText().getBytes()
                );
                currentFilePath = file.getAbsolutePath();
                isModified = false;
                updateWindowTitle();
            } catch (java.io.IOException e) {
                showAlert("Error", "Could not save file: " + e.getMessage());
            }
        }
    }
    
    /**
     * Shows an about dialog with application information.
     * Demonstrates JavaFX Alert dialogs.
     */
    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("JavaFX Notepad");
        alert.setContentText("A simple notepad application built with JavaFX.\n\n" +
                           "Features:\n" +
                           "- Create, open, and save text files\n" +
                           "- Basic text editing operations\n" +
                           "- Keyboard shortcuts for common operations\n\n" +
                           "This application demonstrates:\n" +
                           "- JavaFX GUI components\n" +
                           "- Event handling\n" +
                           "- File I/O operations\n" +
                           "- Object-oriented programming concepts");
        alert.showAndWait();
    }
    
    /**
     * Updates the window title based on current state.
     */
    private void updateWindowTitle() {
        String title = "JavaFX Notepad";
        if (currentFilePath != null) {
            title += " - " + new java.io.File(currentFilePath).getName();
        }
        if (isModified) {
            title += " *";
        }
        primaryStage.setTitle(title);
        updateStatus();
    }
    
    /**
     * Confirms closing the application if there are unsaved changes.
     * @return true if the application should close, false otherwise
     */
    private boolean confirmClose() {
        if (!isModified) {
            return true;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Unsaved Changes");
        alert.setHeaderText("Do you want to save changes?");
        alert.setContentText("You have unsaved changes. Do you want to save them before closing?");
        
        ButtonType saveButton = new ButtonType("Save");
        ButtonType dontSaveButton = new ButtonType("Don't Save");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        
        alert.getButtonTypes().setAll(saveButton, dontSaveButton, cancelButton);
        
        java.util.Optional<ButtonType> result = alert.showAndWait();
        
        if (result.isPresent()) {
            if (result.get() == saveButton) {
                saveFile();
                return !isModified; // Only close if save was successful
            } else if (result.get() == dontSaveButton) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Shows an alert dialog with the given title and message.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Main method to launch the JavaFX application.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
