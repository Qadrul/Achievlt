package com.project.controller;

import com.project.Main;
import com.project.model.Tujuan;
import com.project.service.TujuanService;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;

public class HomeController {

    @FXML
    private ListView<Tujuan> tujuanListView;

    @FXML
    private Button createTujuanButton;

    private TujuanService tujuanService;
    private int loggedInUserId;
    private ObservableList<Tujuan> tujuanObservableList;

    public HomeController() {
        tujuanService = new TujuanService();
        tujuanObservableList = FXCollections.observableArrayList();
    }

    // Dipanggil secara manual setelah user ID di-set
    @FXML
    public void initialize() {
        if (loggedInUserId != 0) { // Pastikan userId sudah diset sebelum memuat data
            loadTujuanData();
            setupListView();
        } else {
            System.err.println("User ID belum diatur di HomeController!");
            // Handle error or redirect to login
        }
    }

    public void setLoggedInUserId(int userId) {
        this.loggedInUserId = userId;
    }

    private void loadTujuanData() {
        tujuanObservableList.clear();
        tujuanObservableList.addAll(tujuanService.getTujuanByUserId(loggedInUserId));
        System.out.println("Loaded " + tujuanObservableList.size() + " tujuan for user " + loggedInUserId);
    }

    private void setupListView() {
        tujuanListView.setItems(tujuanObservableList);
        tujuanListView.setCellFactory(lv -> new ListCell<Tujuan>() {
            @Override
            protected void updateItem(Tujuan tujuan, boolean empty) {
                super.updateItem(tujuan, empty);
                if (empty || tujuan == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(10); // Spacing 10px
                    hbox.setPadding(new javafx.geometry.Insets(10, 15, 10, 15));
                    hbox.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 5);");
                    VBox.setVgrow(hbox, Priority.ALWAYS); // Allow it to grow vertically

                    Label namaTujuanLabel = new Label(tujuan.getNamaTujuan());
                    namaTujuanLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");

                    ProgressBar progressBar = new ProgressBar(tujuan.getProgressPercentage());
                    progressBar.setPrefWidth(150); // Fixed width for progress bar
                    progressBar.setPrefHeight(10); // Fixed height
                    progressBar.setStyle("-fx-accent: #FFD6D6;"); // Custom color for progress

                    Label progressPercentageLabel = new Label(String.format("%.0f%%", tujuan.getProgressPercentage() * 100));
                    progressPercentageLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");

                    VBox textAndProgress = new VBox(5);
                    textAndProgress.getChildren().addAll(namaTujuanLabel, progressBar);

                    HBox.setHgrow(textAndProgress, Priority.ALWAYS); // Allow text and progress to take available space

                    hbox.getChildren().addAll(textAndProgress, progressPercentageLabel);
                    hbox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                    HBox.setMargin(progressPercentageLabel, new javafx.geometry.Insets(0, 0, 0, 10)); // Margin for percentage label

                    setGraphic(hbox);
                }
            }
        });

        // Handle item click to go to detail page
        tujuanListView.setOnMouseClicked(event -> {
            Tujuan selectedTujuan = tujuanListView.getSelectionModel().getSelectedItem();
            if (selectedTujuan != null) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailTujuan.fxml"));
                    Parent root = loader.load();
                    DetailTujuanController detailController = loader.getController();
                    detailController.setTujuan(selectedTujuan); // Pass the selected Tujuan object
                    detailController.setLoggedInUserId(loggedInUserId); // Pass user ID to DetailController
                    detailController.initialize(); // Manually call initialize

                    // Apply Slide Left transition
                    root.setTranslateX(Main.getPrimaryStage().getWidth());

                    TranslateTransition slide = new TranslateTransition(Duration.millis(300), root);
                    slide.setFromX(Main.getPrimaryStage().getWidth());
                    slide.setToX(0);
                    slide.play();

                    Main.getPrimaryStage().getScene().setRoot(root);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @FXML
    private void handleBackToLogin(ActionEvent event) {
        try {
            // Apply Fade transition for going back to login
            Parent currentRoot = Main.getPrimaryStage().getScene().getRoot();
            FadeTransition fade = new FadeTransition(Duration.millis(500), currentRoot);
            fade.setFromValue(1);
            fade.setToValue(0);
            fade.setOnFinished(e -> {
                try {
                    Main.setRoot("Login");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            });
            fade.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCreateTujuan(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CreateTujuan.fxml"));
            Parent root = loader.load();
            CreateTujuanController createController = loader.getController();
            createController.setLoggedInUserId(loggedInUserId); // Pass user ID
            createController.setHomeController(this); // Pass reference to Home Controller

            // Apply Scale + Fade transition
            root.setOpacity(0);
            root.setScaleX(0.8);
            root.setScaleY(0.8);

            FadeTransition fade = new FadeTransition(Duration.millis(300), root);
            fade.setFromValue(0);
            fade.setToValue(1);

            ScaleTransition scale = new ScaleTransition(Duration.millis(300), root);
            scale.setFromX(0.8);
            scale.setFromY(0.8);
            scale.setToX(1.0);
            scale.setToY(1.0);

            fade.play();
            scale.play();

            Main.getPrimaryStage().getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void refreshTujuanList() {
        Platform.runLater(() -> {
            loadTujuanData();
        });
    }
}