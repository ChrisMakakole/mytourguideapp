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
    private VBox sideNavContent;
    private ScrollPane sideNavScrollPane;
    private StackPane rootStackPane;
    private MapController mapController;
    private final List<String> locations = Arrays.asList("maseru","quthing", "thaba_bosiu","tsehlanyane","liphofung","pioneermall","sanipass","maletsunyane");

    @Override
    public void start(Stage primaryStage) {
        try {
            BorderPane rootBorderPane = new BorderPane();


            HBox topNav = new HBox(10);
            topNav.setPadding(new Insets(10));
            topNav.setAlignment(Pos.CENTER_LEFT);
            topNav.setId("topNav");
            Button homeButton = new Button("Home");
            Label Title = new Label("Lesotho Tour Guide");
            Title.getStyleClass().add("Title-label");
            homeButton.getStyleClass().add("button");

            homeButton.setOnAction(event -> {

                sideNavContent.getChildren().clear();


                thumbnailContent();


            });
            topNav.getChildren().addAll(homeButton,Title);
            rootBorderPane.setTop(topNav);


            sideNavContent = new VBox(10);
            sideNavContent.setPadding(new Insets(10));
            sideNavContent.setPrefWidth(350);
            sideNavContent.setId("sideNavContent");

            thumbnailContent();

            sideNavScrollPane = new ScrollPane(sideNavContent);
            sideNavScrollPane.setFitToWidth(true);
            sideNavScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            sideNavScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            sideNavScrollPane.getStyleClass().add("scroll-pane");

            rootBorderPane.setLeft(sideNavScrollPane);


            mapController = new MapController(sideNavContent);
            WebView mapView = mapController.getMapView();
            rootBorderPane.setCenter(mapView);


            ScrollPane overallScrollPane = new ScrollPane(rootBorderPane);
            overallScrollPane.setFitToWidth(true);
            overallScrollPane.setFitToHeight(true);
            overallScrollPane.getStyleClass().add("scroll-pane");


            rootStackPane = new StackPane(overallScrollPane);
            rootStackPane.getStyleClass().add("root");

            Scene scene = new Scene(rootStackPane, 1200, 800);
            scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());

            primaryStage.setTitle("Lesotho Tour Guide");
            primaryStage.setScene(scene);
            primaryStage.show();

            mapController.setRootStackPane(rootStackPane);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void thumbnailContent() {

        for (String location : locations) {
            String imageName = "/map/images/" + location + "_thumb.jpg";
            Image image = new Image(getClass().getResourceAsStream(imageName));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(250);
            imageView.setPreserveRatio(true);
            imageView.setStyle("-fx-cursor: hand;");
            imageView.getStyleClass().add("image-view");

            Label nameLabel = new Label(location.toUpperCase());
            nameLabel.setStyle("-fx-font-weight: bold;");
            nameLabel.setAlignment(Pos.CENTER);
            nameLabel.getStyleClass().add("label");

            VBox locationPreview = new VBox(5, imageView, nameLabel);
            locationPreview.setAlignment(Pos.CENTER);
            locationPreview.setStyle("-fx-padding: 10px; -fx-cursor: hand;");
            locationPreview.getStyleClass().add("location-preview");

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
    }

    public static void main(String[] args) {
        Application.launch(Main.class, args);
        System.out.println("Application started");
        launch(args);
    }
}