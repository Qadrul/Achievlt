package com.project.controller;

import com.project.Main;
import com.project.model.*; // Import semua model
import com.project.service.TujuanService;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane; // Untuk menampilkan/menyembunyikan input editing
import javafx.util.Duration;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DetailTujuanController {

    @FXML private Label namaTujuanLabel;
    @FXML private Label deadlineLabel;
    @FXML private ProgressBar progressBar;
    @FXML private Label progressPercentageLabel;
    @FXML private VBox roadGoalsContainer;
    @FXML private Button editModeButton;
    @FXML private Button addRoadGoalFab;

    private Tujuan selectedTujuan;
    private TujuanService tujuanService;
    private int loggedInUserId;
    private boolean isEditMode = false;

    // List untuk menyimpan referensi ke TextField untuk road goal baru saat edit mode
    private List<RoadGoalEditEntry> editEntries = new ArrayList<>();

    public DetailTujuanController() {
        tujuanService = new TujuanService();
    }

    public void setTujuan(Tujuan tujuan) {
        this.selectedTujuan = tujuan;
    }

    public void setLoggedInUserId(int userId) {
        this.loggedInUserId = userId;
    }

    @FXML
    public void initialize() {
        if (selectedTujuan != null) {
            namaTujuanLabel.setText(selectedTujuan.getNamaTujuan());
            if (selectedTujuan.getDeadline() != null) {
                deadlineLabel.setText("Deadline: " + selectedTujuan.getDeadline().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));
            } else {
                deadlineLabel.setText("Deadline: Belum ditentukan");
            }
            updateRoadGoalsDisplay();
        }
    }

    private void updateRoadGoalsDisplay() {
        roadGoalsContainer.getChildren().clear();
        editEntries.clear(); // Clear old edit entries

        if (selectedTujuan.getRoadGoals() != null) {
            for (RoadGoal roadGoal : selectedTujuan.getRoadGoals()) {
                addRoadGoalToDisplay(roadGoal);
            }
        }
        updateProgressBar();
    }

    private void addRoadGoalToDisplay(RoadGoal roadGoal) {
        HBox hbox = new HBox(10);
        hbox.setStyle("-fx-alignment: CENTER_LEFT; -fx-padding: 5 0;");
        HBox.setHgrow(hbox, Priority.ALWAYS);

        if (isEditMode) {
            TextField descriptionField = new TextField(roadGoal.getDeskripsi());
            descriptionField.getStyleClass().add("text-field");
            HBox.setHgrow(descriptionField, Priority.ALWAYS);

            Label priorityLabel = new Label(roadGoal.getPriorityType()); // Show priority type
            priorityLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + roadGoal.getPriorityColor() + ";");
            priorityLabel.setPrefWidth(60);

            // Dynamic field for additional info
            StackPane additionalInfoPane = new StackPane();
            additionalInfoPane.setPrefWidth(120);
            additionalInfoPane.setPrefHeight(30);

            // Create fields but set visibility based on type
            DatePicker dueDateField = new DatePicker();
            dueDateField.setPromptText("Due Date");
            dueDateField.getStyleClass().add("date-picker");
            if (roadGoal instanceof HighPriorityRoadGoal) {
                dueDateField.setValue(((HighPriorityRoadGoal) roadGoal).getDueDate());
                dueDateField.setVisible(true);
                dueDateField.setManaged(true);
            } else {
                dueDateField.setVisible(false);
                dueDateField.setManaged(false);
            }

            TextField estimatedDaysField = new TextField();
            estimatedDaysField.setPromptText("Estimasi hari");
            estimatedDaysField.getStyleClass().add("text-field");
            if (roadGoal instanceof MediumPriorityRoadGoal) {
                estimatedDaysField.setText(String.valueOf(((MediumPriorityRoadGoal) roadGoal).getEstimatedDays()));
                estimatedDaysField.setVisible(true);
                estimatedDaysField.setManaged(true);
            } else {
                estimatedDaysField.setVisible(false);
                estimatedDaysField.setManaged(false);
            }

            TextField notesField = new TextField();
            notesField.setPromptText("Catatan");
            notesField.getStyleClass().add("text-field");
            if (roadGoal instanceof LowPriorityRoadGoal) {
                notesField.setText(((LowPriorityRoadGoal) roadGoal).getNotes());
                notesField.setVisible(true);
                notesField.setManaged(true);
            } else {
                notesField.setVisible(false);
                notesField.setManaged(false);
            }
            additionalInfoPane.getChildren().addAll(dueDateField, estimatedDaysField, notesField); // Add all, visibility will control

            Button deleteButton = new Button("-");
            deleteButton.setStyle("-fx-background-color: #FFD6D6; -fx-background-radius: 50%; -fx-min-width: 30px; -fx-min-height: 30px;");
            deleteButton.setOnAction(e -> {
                tujuanService.deleteRoadGoal(roadGoal.getId());
                selectedTujuan.getRoadGoals().remove(roadGoal);
                updateRoadGoalsDisplay();
            });

            hbox.getChildren().addAll(descriptionField, priorityLabel, additionalInfoPane, deleteButton);
            roadGoalsContainer.getChildren().add(hbox);

            // Add change listeners for existing road goals during edit mode
            descriptionField.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal && !descriptionField.getText().equals(roadGoal.getDeskripsi())) {
                    roadGoal.setDeskripsi(descriptionField.getText());
                    tujuanService.updateRoadGoal(roadGoal);
                }
            });
            // Listeners for specific fields
            if (roadGoal instanceof HighPriorityRoadGoal) {
                dueDateField.valueProperty().addListener((obs, oldVal, newVal) -> {
                    ((HighPriorityRoadGoal) roadGoal).setDueDate(newVal);
                    tujuanService.updateRoadGoal(roadGoal);
                });
            } else if (roadGoal instanceof MediumPriorityRoadGoal) {
                estimatedDaysField.focusedProperty().addListener((obs, oldVal, newVal) -> {
                    if (!newVal) {
                        try {
                            ((MediumPriorityRoadGoal) roadGoal).setEstimatedDays(Integer.parseInt(estimatedDaysField.getText()));
                            tujuanService.updateRoadGoal(roadGoal);
                        } catch (NumberFormatException ex) { /* handle error */ }
                    }
                });
            } else if (roadGoal instanceof LowPriorityRoadGoal) {
                notesField.focusedProperty().addListener((obs, oldVal, newVal) -> {
                    if (!newVal) {
                        ((LowPriorityRoadGoal) roadGoal).setNotes(notesField.getText());
                        tujuanService.updateRoadGoal(roadGoal);
                    }
                });
            }

        } else { // View Mode
            CheckBox checkBox = new CheckBox(roadGoal.getDeskripsi());
            checkBox.setSelected(roadGoal.isCompleted());
            checkBox.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333;");
            HBox.setHgrow(checkBox, Priority.ALWAYS);

            Label infoLabel = new Label(roadGoal.getAdditionalInfo());
            infoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666; -fx-font-style: italic;");

            Label priorityLabel = new Label(roadGoal.getPriorityType());
            priorityLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + roadGoal.getPriorityColor() + ";");

            checkBox.setOnAction(e -> {
                roadGoal.setCompleted(checkBox.isSelected());
                tujuanService.updateRoadGoal(roadGoal);
                updateProgressBar();
            });
            hbox.getChildren().addAll(checkBox, infoLabel, priorityLabel);
            hbox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            roadGoalsContainer.getChildren().add(hbox);
        }
    }

    private void updateProgressBar() {
        double progress = selectedTujuan.getProgressPercentage();
        progressBar.setProgress(progress);
        progressPercentageLabel.setText(String.format("%.0f%%", progress * 100));
    }

    @FXML
    private void toggleEditMode() {
        isEditMode = !isEditMode;
        editModeButton.setText(isEditMode ? "Selesai Edit" : "Edit Mode");
        addRoadGoalFab.setVisible(isEditMode);
        addRoadGoalFab.setManaged(isEditMode); // Also manage visibility to save space

        // Ketika keluar dari edit mode, simpan perubahan yang ada pada field yang diedit
        if (!isEditMode) {
            // Save existing edited road goals (listeners handle this already, but explicit save might be needed for unsaved changes)
            // Or if you only want to save on "Selesai Edit" button click, iterate through 'selectedTujuan.getRoadGoals()'
            // and manually trigger updates for their properties.
            // For now, rely on individual field listeners.

            // Handle and save newly added road goals
            saveNewRoadGoals();
        }
        updateRoadGoalsDisplay(); // Re-render to show updated state (e.g., checkboxes vs textfields)
    }

    @FXML
    private void addRoadGoal() {
        // This adds new road goal entries in edit mode
        HBox hbox = new HBox(10);
        hbox.setStyle("-fx-alignment: CENTER_LEFT; -fx-padding: 5 0;");
        HBox.setHgrow(hbox, Priority.ALWAYS);

        TextField newDescriptionField = new TextField();
        newDescriptionField.setPromptText("Deskripsi langkah baru...");
        newDescriptionField.getStyleClass().add("text-field");
        HBox.setHgrow(newDescriptionField, Priority.ALWAYS);

        ComboBox<String> newPriorityComboBox = new ComboBox<>();
        newPriorityComboBox.getItems().addAll("LOW", "MEDIUM", "HIGH");
        newPriorityComboBox.setValue("LOW");
        newPriorityComboBox.setPrefWidth(90);

        StackPane newAdditionalInfoPane = new StackPane();
        newAdditionalInfoPane.setPrefWidth(120);
        newAdditionalInfoPane.setPrefHeight(30);

        DatePicker newDueDateField = new DatePicker();
        newDueDateField.setPromptText("Due Date");
        newDueDateField.getStyleClass().add("date-picker");
        newDueDateField.setVisible(false);
        newDueDateField.setManaged(false);

        TextField newEstimatedDaysField = new TextField();
        newEstimatedDaysField.setPromptText("Estimasi hari");
        newEstimatedDaysField.getStyleClass().add("text-field");
        newEstimatedDaysField.setVisible(false);
        newEstimatedDaysField.setManaged(false);

        TextField newNotesField = new TextField();
        newNotesField.setPromptText("Catatan");
        newNotesField.getStyleClass().add("text-field");
        newNotesField.setVisible(true);
        newNotesField.setManaged(true);

        newAdditionalInfoPane.getChildren().addAll(newDueDateField, newEstimatedDaysField, newNotesField);

        newPriorityComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            newDueDateField.setVisible(false);
            newDueDateField.setManaged(false);
            newEstimatedDaysField.setVisible(false);
            newEstimatedDaysField.setManaged(false);
            newNotesField.setVisible(false);
            newNotesField.setManaged(false);

            if ("HIGH".equals(newVal)) {
                newDueDateField.setVisible(true);
                newDueDateField.setManaged(true);
            } else if ("MEDIUM".equals(newVal)) {
                newEstimatedDaysField.setVisible(true);
                newEstimatedDaysField.setManaged(true);
            } else { // LOW or default
                newNotesField.setVisible(true);
                newNotesField.setManaged(true);
            }
        });
        newPriorityComboBox.fireEvent(new ActionEvent());

        Button removeButton = new Button("-");
        removeButton.setStyle("-fx-background-color: #FFD6D6; -fx-background-radius: 50%; -fx-min-width: 30px; -fx-min-height: 30px;");
        removeButton.setOnAction(e -> {
            roadGoalsContainer.getChildren().remove(hbox);
            editEntries.removeIf(entry -> entry.getHBox() == hbox); // Remove from tracking list
        });

        hbox.getChildren().addAll(newDescriptionField, newPriorityComboBox, newAdditionalInfoPane, removeButton);
        roadGoalsContainer.getChildren().add(hbox);
        editEntries.add(new RoadGoalEditEntry(hbox, newDescriptionField, newPriorityComboBox, newDueDateField, newEstimatedDaysField, newNotesField));
    }

    private void saveNewRoadGoals() {
        Iterator<RoadGoalEditEntry> iterator = editEntries.iterator();
        while (iterator.hasNext()) {
            RoadGoalEditEntry entry = iterator.next();
            String description = entry.getDescriptionField().getText().trim();
            String priority = entry.getPriorityComboBox().getValue();

            if (!description.isEmpty()) {
                RoadGoal newRoadGoal;
                switch (priority) {
                    case "HIGH":
                        newRoadGoal = tujuanService.createRoadGoal(selectedTujuan.getId(), description, priority, entry.getDueDateField().getValue(), null, null);
                        break;
                    case "MEDIUM":
                        Integer estimatedDays = null;
                        try {
                            estimatedDays = Integer.parseInt(entry.getEstimatedDaysField().getText());
                        } catch (NumberFormatException e) { /* handle error */ }
                        newRoadGoal = tujuanService.createRoadGoal(selectedTujuan.getId(), description, priority, null, estimatedDays, null);
                        break;
                    case "LOW":
                        newRoadGoal = tujuanService.createRoadGoal(selectedTujuan.getId(), description, priority, null, null, entry.getNotesField().getText());
                        break;
                    default:
                        newRoadGoal = tujuanService.createRoadGoal(selectedTujuan.getId(), description, "LOW", null, null, null);
                        break;
                }
                if (newRoadGoal != null) {
                    selectedTujuan.addRoadGoal(newRoadGoal);
                }
            }
        }
        editEntries.clear(); // Clear list after saving
    }

    // Helper class to hold references to dynamically created fields for new road goals
    private static class RoadGoalEditEntry {
        private final HBox hBox;
        private final TextField descriptionField;
        private final ComboBox<String> priorityComboBox;
        private final DatePicker dueDateField;
        private final TextField estimatedDaysField;
        private final TextField notesField;

        public RoadGoalEditEntry(HBox hBox, TextField descriptionField, ComboBox<String> priorityComboBox, DatePicker dueDateField, TextField estimatedDaysField, TextField notesField) {
            this.hBox = hBox;
            this.descriptionField = descriptionField;
            this.priorityComboBox = priorityComboBox;
            this.dueDateField = dueDateField;
            this.estimatedDaysField = estimatedDaysField;
            this.notesField = notesField;
        }

        public HBox getHBox() { return hBox; }
        public TextField getDescriptionField() { return descriptionField; }
        public ComboBox<String> getPriorityComboBox() { return priorityComboBox; }
        public DatePicker getDueDateField() { return dueDateField; }
        public TextField getEstimatedDaysField() { return estimatedDaysField; }
        public TextField getNotesField() { return notesField; }
    }


    @FXML
    private void handleBackToHome(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Homepage.fxml"));
            Parent root = loader.load();
            HomeController controller = loader.getController();
            controller.setLoggedInUserId(loggedInUserId);
            controller.initialize();

            Parent currentRoot = Main.getPrimaryStage().getScene().getRoot();
            currentRoot.setTranslateX(0);

            TranslateTransition slide = new TranslateTransition(Duration.millis(300), currentRoot);
            slide.setFromX(0);
            slide.setToX(Main.getPrimaryStage().getWidth());

            slide.setOnFinished(e -> {
                try {
                    Main.getPrimaryStage().getScene().setRoot(root);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            slide.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}