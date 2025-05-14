package com.tourguide.tours;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;

public class Main extends Application {
    private VBox sideNavContent; // Container for the content of the side nav
    private ScrollPane sideNavScrollPane; // ScrollPane for the side nav
    private StackPane rootStackPane; // Use StackPane as the root
    private MapController mapController;
    private final List<String> locations = Arrays.asList("maseru", "thaba","tsehlanyane"); // Add more locations here

    @Override
    public void start(Stage primaryStage) {
        try {
            BorderPane rootBorderPane = new BorderPane();

            // Top Navigation
            HBox topNav = new HBox(10);
            topNav.setPadding(new Insets(10));
            topNav.setAlignment(Pos.CENTER_LEFT);
            Button homeButton = new Button("Home");
            // Add more top navigation buttons here
            topNav.getChildren().addAll(homeButton);
            rootBorderPane.setTop(topNav);

            // Side Navigation
            sideNavContent = new VBox(10); // Initialize the container for side nav content
            sideNavContent.setPadding(new Insets(10));
            sideNavContent.setPrefWidth(350);

            // Populate sidebar with location previews
            for (String location : locations) {
                String imageName = "/map/images/" + location + "_thumb.jpg"; // Use thumbnails
                Image image = new Image(getClass().getResourceAsStream(imageName));
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(300);
                imageView.setPreserveRatio(true);
                imageView.setStyle("-fx-cursor: hand;"); // Indicate it's clickable

                Label nameLabel = new Label(location.toUpperCase());
                nameLabel.setStyle("-fx-font-weight: bold;");
                nameLabel.setAlignment(Pos.CENTER);

                VBox locationPreview = new VBox(5, imageView, nameLabel);
                locationPreview.setAlignment(Pos.CENTER);
                locationPreview.setStyle("-fx-padding: 10px; -fx-cursor: hand;");


                // Load location info on click
                final String currentLocation = location;
                locationPreview.setOnMouseClicked((MouseEvent event) -> {
                    mapController.showLocationInfo(currentLocation);
                });
                imageView.setOnMouseClicked((MouseEvent event) -> {
                    mapController.showLocationInfo(currentLocation);
                });
                nameLabel.setOnMouseClicked((MouseEvent event) -> {
                    mapController.showLocationInfo(currentLocation);
                });

                sideNavContent.getChildren().add(locationPreview);
            }

            sideNavScrollPane = new ScrollPane(sideNavContent); // Wrap the content in a ScrollPane
            sideNavScrollPane.setFitToWidth(true); // Make the scroll pane width match the content width
            sideNavScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Show vertical scrollbar only when needed
            sideNavScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Never show horizontal scrollbar

            rootBorderPane.setLeft(sideNavScrollPane); // Set the ScrollPane as the left node

            // Map View
            mapController = new MapController(sideNavContent); // Pass the content container to MapController
            WebView mapView = mapController.getMapView();
            rootBorderPane.setCenter(mapView);

            // Wrap the BorderPane in a ScrollPane (for the overall window if needed)
            ScrollPane overallScrollPane = new ScrollPane(rootBorderPane);
            overallScrollPane.setFitToWidth(true);
            overallScrollPane.setFitToHeight(true); // Ensure the map can take full height

            // Create the StackPane and add the ScrollPane (containing the map layout)
            rootStackPane = new StackPane(overallScrollPane);

            Scene scene = new Scene(rootStackPane, 1200, 800);
            scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());

            primaryStage.setTitle("Lesotho Tour Guide");
            primaryStage.setScene(scene);
            primaryStage.show();

            // Store the rootStackPane in the MapController for overlaying the video
            mapController.setRootStackPane(rootStackPane);

            // Optionally load the first location by default
//            if (!locations.isEmpty()) {
//                mapController.showLocationInfo(locations.get(0));
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}