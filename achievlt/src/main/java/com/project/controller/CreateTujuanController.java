package com.project.controller;

import com.project.Main;
import com.project.model.Tujuan;
import com.project.service.TujuanService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*; // Import semua kontrol
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CreateTujuanController {

    @FXML private TextField namaTujuanField;
    @FXML private DatePicker deadlinePicker;
    @FXML private VBox roadGoalsContainer;
    @FXML private Label errorMessageLabel;

    private TujuanService tujuanService;
    private int loggedInUserId;
    private HomeController homeController;

    // List untuk menyimpan referensi ke HBox setiap road goal entry
    private List<HBox> roadGoalEntries = new ArrayList<>();

    public CreateTujuanController() {
        tujuanService = new TujuanService();
    }

    public void setLoggedInUserId(int userId) {
        this.loggedInUserId = userId;
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    @FXML
    public void initialize() {
        addRoadGoalField(); // Tambahkan satu field secara default
    }

    @FXML
    private void addRoadGoalField() {
        HBox roadGoalEntry = new HBox(10);
        roadGoalEntry.setPrefHeight(40.0); // Adjust height for ComboBox
        roadGoalEntry.setStyle("-fx-alignment: CENTER_LEFT;");

        TextField roadGoalField = new TextField();
        roadGoalField.setPromptText("Deskripsi langkah...");
        roadGoalField.setPrefWidth(250); // Smaller width to accommodate other controls
        roadGoalField.getStyleClass().add("text-field");
        HBox.setHgrow(roadGoalField, javafx.scene.layout.Priority.ALWAYS);

        ComboBox<String> priorityComboBox = new ComboBox<>();
        priorityComboBox.getItems().addAll("LOW", "MEDIUM", "HIGH");
        priorityComboBox.setValue("LOW"); // Default priority
        priorityComboBox.setPrefWidth(90);

        // Dynamic input field for additional info based on priority
        StackPane additionalInfoPane = new StackPane(); // Use StackPane to swap fields
        additionalInfoPane.setPrefWidth(120);
        additionalInfoPane.setPrefHeight(30);

        DatePicker dueDateField = new DatePicker();
        dueDateField.setPromptText("Due Date");
        dueDateField.getStyleClass().add("date-picker");
        dueDateField.setVisible(false); // Hidden by default
        dueDateField.setManaged(false); // Not take space when hidden

        TextField estimatedDaysField = new TextField();
        estimatedDaysField.setPromptText("Estimasi hari");
        estimatedDaysField.getStyleClass().add("text-field");
        estimatedDaysField.setVisible(false);
        estimatedDaysField.setManaged(false);

        TextField notesField = new TextField();
        notesField.setPromptText("Catatan");
        notesField.getStyleClass().add("text-field");
        notesField.setVisible(true); // Default for LOW priority
        notesField.setManaged(true);

        additionalInfoPane.getChildren().addAll(dueDateField, estimatedDaysField, notesField);

        // Listener for priority changes
        priorityComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            dueDateField.setVisible(false);
            dueDateField.setManaged(false);
            estimatedDaysField.setVisible(false);
            estimatedDaysField.setManaged(false);
            notesField.setVisible(false);
            notesField.setManaged(false);

            if ("HIGH".equals(newVal)) {
                dueDateField.setVisible(true);
                dueDateField.setManaged(true);
            } else if ("MEDIUM".equals(newVal)) {
                estimatedDaysField.setVisible(true);
                estimatedDaysField.setManaged(true);
            } else { // LOW or default
                notesField.setVisible(true);
                notesField.setManaged(true);
            }
        });
        // Trigger listener initially to set default field (notesField)
        priorityComboBox.fireEvent(new ActionEvent());

        Button removeButton = new Button("-");
        removeButton.setStyle("-fx-background-color: #FFD6D6; -fx-background-radius: 50%; -fx-min-width: 30px; -fx-min-height: 30px;");
        removeButton.setOnAction(e -> {
            roadGoalsContainer.getChildren().remove(roadGoalEntry);
            roadGoalEntries.remove(roadGoalEntry);
        });

        roadGoalEntry.getChildren().addAll(roadGoalField, priorityComboBox, additionalInfoPane, removeButton);
        roadGoalsContainer.getChildren().add(roadGoalEntry);
        roadGoalEntries.add(roadGoalEntry); // Add to the list
    }

    @FXML
    private void handleSaveTujuan(ActionEvent event) {
        String namaTujuan = namaTujuanField.getText();
        LocalDate deadline = deadlinePicker.getValue();

        if (namaTujuan.isEmpty()) {
            errorMessageLabel.setText("Nama tujuan tidak boleh kosong.");
            return;
        }

        List<Map<String, Object>> roadGoalDataList = new ArrayList<>();
        for (HBox entryHBox : roadGoalEntries) { // Iterate through stored HBox references
            TextField descriptionField = (TextField) entryHBox.getChildren().get(0); // Assuming TextField is first
            ComboBox<String> priorityComboBox = (ComboBox<String>) entryHBox.getChildren().get(1); // Assuming ComboBox is second
            StackPane additionalInfoPane = (StackPane) entryHBox.getChildren().get(2); // Assuming StackPane is third

            String description = descriptionField.getText().trim();
            String priority = priorityComboBox.getValue();

            if (!description.isEmpty()) {
                Map<String, Object> roadGoalData = new HashMap<>();
                roadGoalData.put("description", description);
                roadGoalData.put("priority", priority);

                // Extract additional info based on priority
                if ("HIGH".equals(priority)) {
                    DatePicker dueDateField = (DatePicker) additionalInfoPane.getChildren().get(0); // First child is dueDate
                    roadGoalData.put("dueDate", dueDateField.getValue());
                } else if ("MEDIUM".equals(priority)) {
                    TextField estimatedDaysField = (TextField) additionalInfoPane.getChildren().get(1); // Second child is estimatedDays
                    try {
                        roadGoalData.put("estimatedDays", Integer.parseInt(estimatedDaysField.getText()));
                    } catch (NumberFormatException e) {
                        errorMessageLabel.setText("Estimasi hari harus angka.");
                        return;
                    }
                } else if ("LOW".equals(priority)) {
                    TextField notesField = (TextField) additionalInfoPane.getChildren().get(2); // Third child is notes
                    roadGoalData.put("notes", notesField.getText());
                }
                roadGoalDataList.add(roadGoalData);
            }
        }

        if (roadGoalDataList.isEmpty()) {
            errorMessageLabel.setText("Setidaknya satu langkah (road goal) harus diisi.");
            return;
        }

        Tujuan newTujuan = tujuanService.createTujuan(loggedInUserId, namaTujuan, deadline, roadGoalDataList);

        if (newTujuan != null) {
            System.out.println("Tujuan berhasil disimpan: " + newTujuan.getNamaTujuan());
            errorMessageLabel.setText("Tujuan berhasil disimpan!");
            if (homeController != null) {
                homeController.refreshTujuanList();
            } else {
                System.err.println("HomeController reference is null. Cannot refresh list.");
            }
            handleCancel(event);
        } else {
            errorMessageLabel.setText("Gagal menyimpan tujuan.");
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Homepage.fxml"));
            Parent root = loader.load();
            HomeController controller = loader.getController();
            controller.setLoggedInUserId(loggedInUserId);
            controller.initialize();

            Parent currentRoot = Main.getPrimaryStage().getScene().getRoot();
            currentRoot.setOpacity(1);
            currentRoot.setScaleX(1.0);
            currentRoot.setScaleY(1.0);

            FadeTransition fade = new FadeTransition(Duration.millis(300), currentRoot);
            fade.setFromValue(1);
            fade.setToValue(0);

            ScaleTransition scale = new ScaleTransition(Duration.millis(300), currentRoot);
            scale.setFromX(1.0);
            scale.setFromY(1.0);
            scale.setToX(0.8);
            scale.setToY(0.8);

            fade.setOnFinished(e -> {
                try {
                    Main.getPrimaryStage().getScene().setRoot(root);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            fade.play();
            scale.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}