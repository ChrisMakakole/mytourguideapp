package com.tourguide.tours;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapController {
    private javafx.scene.web.WebView mapView;
    private javafx.scene.web.WebEngine engine;
    private VBox infoDisplay;
    private MediaPlayer audioPlayer;
    private Slider audioTimeSlider;
    private Label currentAudioTimeLabel = new Label("0:00");
    private Label durationAudioLabel = new Label("0:00");
    private Button playPauseAudioButton = new Button("Play");
    private Button stopAudioButton = new Button("Stop");
    private VBox audioControlsContainer = new VBox(5);

    private MediaPlayer currentVideoPlayer;
    private MediaView currentMediaView;
    private VBox currentVideoControls;
    private Button fullscreenVideoButton;
    private boolean isVideoFullscreen = false;
    private double originalVideoWidth; // Not used currently, but kept for potential future use
    private double originalVideoHeight; // Not used currently, but kept for potential future use
    private StackPane rootStackPane;
    private int videoViewIndex = -1; // Not directly used for adding/removing, but for conceptual ordering
    private int videoControlsIndex = -1; // Not directly used for adding/removing, but for conceptual ordering
    private String currentVideoPath = null;
    private ImageView currentFullscreenImageView;
    private Button exitFullscreenImageButton;
    private boolean isImageFullscreen = false;

    // Instance of QuizManager
    private QuizManager quizManager;

    public MapController(VBox infoDisplay) {
        this.infoDisplay = infoDisplay;
        this.infoDisplay.setId("infoDisplay"); // Set ID for CSS
        mapView = new javafx.scene.web.WebView();
        engine = mapView.getEngine();

        // Initialize QuizManager, passing the infoDisplay so it can manage its UI
        this.quizManager = new QuizManager(infoDisplay);

        engine.load(getClass().getResource("/map/map.html").toExternalForm());

        engine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == javafx.concurrent.Worker.State.SUCCEEDED) {
                netscape.javascript.JSObject win = (netscape.javascript.JSObject) engine.executeScript("window");
                win.setMember("javaConnector", this);
            }
        });

        audioTimeSlider = new Slider();
        audioTimeSlider.getStyleClass().add("slider"); // Apply slider style
        HBox audioTimeLabels = new HBox(5, currentAudioTimeLabel, new Label("/"), durationAudioLabel);
        audioTimeLabels.setAlignment(Pos.CENTER);
        currentAudioTimeLabel.getStyleClass().add("label"); // Apply label style
        durationAudioLabel.getStyleClass().add("label"); // Apply label style

        HBox audioButtonControls = new HBox(10, playPauseAudioButton, stopAudioButton);
        audioButtonControls.setAlignment(Pos.CENTER);
        playPauseAudioButton.getStyleClass().add("button"); // Apply button style
        stopAudioButton.getStyleClass().add("button"); // Apply button style

        audioControlsContainer.getChildren().addAll(audioTimeLabels, audioTimeSlider, audioButtonControls);
        audioControlsContainer.setAlignment(Pos.CENTER);
        audioControlsContainer.setVisible(false);
        audioControlsContainer.setId("audioControlsContainer"); // Set ID for CSS

        playPauseAudioButton.setOnAction(event -> {
            if (audioPlayer != null) {
                if (audioPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                    audioPlayer.pause();
                    playPauseAudioButton.setText("Play");
                } else {
                    audioPlayer.play();
                    playPauseAudioButton.setText("Pause");
                }
            }
        });

        stopAudioButton.setOnAction(event -> {
            if (audioPlayer != null) {
                audioPlayer.stop();
                audioPlayer.seek(Duration.ZERO);
                playPauseAudioButton.setText("Play");
                audioTimeSlider.setValue(0);
                currentAudioTimeLabel.setText("0:00");
            }
        });

        audioTimeSlider.setOnMousePressed(event -> {
            if (audioPlayer != null) {
                audioPlayer.pause();
            }
        });
        audioTimeSlider.setOnMouseReleased(event -> {
            if (audioPlayer != null) {
                audioPlayer.seek(Duration.seconds(audioTimeSlider.getValue()));
                audioPlayer.play();
                playPauseAudioButton.setText("Pause");
            }
        });
    }

    public void setRootStackPane(StackPane rootStackPane) {
        this.rootStackPane = rootStackPane;
    }

    public void showLocationInfo(String location) {
        Platform.runLater(() -> {
            infoDisplay.getChildren().clear();
            stopAudio();
            audioControlsContainer.setVisible(false);
            // Clear any previous quiz UI elements if they were added
            quizManager.clearQuizUI();


            isVideoFullscreen = false;
            videoViewIndex = -1;
            videoControlsIndex = -1;
            currentVideoPath = "/media/" + location + "_video.mp4";

            String imageName = "/map/images/" + location + ".jpg";
            String audioPath = "/media/" + location + "_audio.mp3";

            Image image = new Image(getClass().getResourceAsStream(imageName));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(320);
            imageView.setPreserveRatio(true);
            imageView.setStyle("-fx-cursor: hand;"); // Make it look clickable
            imageView.getStyleClass().add("info-display-image-view"); // Apply CSS class
            imageView.setOnMouseClicked(event -> showFullscreenImage(image));

            Label nameLabel = new Label(location.toUpperCase());
            nameLabel.getStyleClass().addAll("info-display-label", "name-label"); // Apply CSS classes

            Label descriptionLabel = new Label();
            descriptionLabel.getStyleClass().addAll("info-display-label", "description-label"); // Apply CSS classes
            switch(location)
            {
                case "maseru":
                    descriptionLabel.setText("Maseru is the vibrant capital city of Lesotho, nestled along the Caledon River. " +
                            "It serves as the economic and administrative heart of the mountain kingdom. " +
                            "Visitors can explore local markets, historical sites, and experience a blend " +
                            "of modern and traditional Basotho culture.");
                    break;
                case "quthing":
                    descriptionLabel.setText("Quthing District, also known as Moyeni, is located in southern Lesotho. " +
                            "It's particularly renowned for its rich palaeontological heritage, " +
                            "featuring numerous dinosaur footprints. The district also boasts " +
                            "the unique Masitise Cave House museum.");
                    break;
                case "thaba_bosiu":
                    descriptionLabel.setText("Thaba-Bosiu is a historical mountain plateau and a national shrine of great " +
                            "significance to the Basotho people. It served as the impregnable stronghold and capital of " +
                            "King Moshoeshoe I during the 19th century. ");
                    break;
                case "tsehlanyane":
                    descriptionLabel.setText("Ts'ehlanyane National Park is a pristine wilderness located in the Maluti " +
                            "Mountains of northern Lesotho. It is characterized by its high-altitude indigenous forest, " +
                            "unique to the region, and stunning mountain scenery.");
                    break;
                case "liphofung":
                    descriptionLabel.setText("Liphofung Cave and Cultural Historical Site is a significant sandstone rock " +
                            "shelter located in Lesotho. It is renowned for its well-preserved San rock paintings, offering" +
                            " a glimpse into ancient hunter-gatherer life.");
                    break;
                case "sanipass":
                    descriptionLabel.setText("Sani Pass is an iconic, dramatic mountain pass connecting Lesotho with KwaZulu-Natal, " +
                            "South Africa. It is famous for its challenging winding gravel road, only safely traversable by 4x4 vehicles." +
                            " At its summit lies \"The Highest Pub in Africa,\" offering breathtaking views.");
                    break;
                case "maletsunyane":
                    descriptionLabel.setText("Maletsunyane Falls is one of Africa's highest single-drop waterfalls, plunging 192 meters " +
                            "into a gorge. Located near the town of Semonkong in Lesotho, the falls are a spectacular natural wonder.");
                    break;
                case "pioneermall":
                    descriptionLabel.setText("Pioneer Mall is a prominent shopping center situated in Maseru, the capital city of Lesotho. " +
                            "It offers a convenient retail experience with a variety of shops, restaurants, and services.");
                    break;
                default:
                    descriptionLabel.setText("no info");

            }
            descriptionLabel.setWrapText(true);

            Button playVideoButton = new Button("Play Video");
            playVideoButton.setMaxWidth(Double.MAX_VALUE);
            playVideoButton.getStyleClass().add("button"); // Apply general button style
            playVideoButton.setOnAction(event -> loadAndPlayVideoFullscreen());

            Button AudioGuideButton = new Button("Start Audio Guide");
            AudioGuideButton.setMaxWidth(Double.MAX_VALUE);
            AudioGuideButton.getStyleClass().add("button"); // Apply general button style
            AudioGuideButton.setOnAction(event -> playAudio(audioPath));

            Button quizButton = new Button("Start Quiz");
            quizButton.setMaxWidth(Double.MAX_VALUE);
            quizButton.getStyleClass().add("button"); // Apply general button style
            quizButton.setOnAction(event -> quizManager.startQuiz(location)); // Delegate to QuizManager

            // Store potential indices (though not used for image in this implementation)
            int imageIndex = infoDisplay.getChildren().indexOf(imageView);
            int descriptionIndex = infoDisplay.getChildren().indexOf(descriptionLabel);
            videoViewIndex = Math.max(imageIndex, descriptionIndex) + 1;
            videoControlsIndex = videoViewIndex + 1;

            infoDisplay.getChildren().addAll(nameLabel, imageView, descriptionLabel, playVideoButton, AudioGuideButton, quizButton, audioControlsContainer);
            infoDisplay.setPadding(new Insets(10));
            infoDisplay.setSpacing(10);
        });
    }

    private void showFullscreenImage(Image image) {
        if (image != null && rootStackPane != null) {
            currentFullscreenImageView = new ImageView(image);
            currentFullscreenImageView.setPreserveRatio(true);
            currentFullscreenImageView.setFitWidth(rootStackPane.getWidth() * 0.9); // Adjust as needed
            currentFullscreenImageView.setFitHeight(rootStackPane.getHeight() * 0.9); // Adjust as needed
            currentFullscreenImageView.getStyleClass().add("image-view"); // Apply general image-view style

            exitFullscreenImageButton = new Button("Exit Fullscreen");
            exitFullscreenImageButton.setOnAction(event -> exitFullscreenImage());
            exitFullscreenImageButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px;"); // Keep inline or move to CSS if many
            exitFullscreenImageButton.getStyleClass().add("button"); // Apply general button style

            VBox fullscreenLayout = new VBox(10, currentFullscreenImageView, exitFullscreenImageButton);
            fullscreenLayout.setAlignment(Pos.CENTER);
            StackPane.setAlignment(fullscreenLayout, Pos.CENTER);
            fullscreenLayout.getStyleClass().add("fullscreen-overlay"); // Apply general overlay style
            fullscreenLayout.setId("fullscreenImageOverlay"); // Set ID for CSS

            rootStackPane.getChildren().removeIf(node -> node.getId() != null && node.getId().equals("fullscreenImageOverlay"));
            rootStackPane.getChildren().add(fullscreenLayout);

            isImageFullscreen = true;
        }
    }

    private void exitFullscreenImage() {
        if (rootStackPane != null && isImageFullscreen) {
            rootStackPane.getChildren().removeIf(node -> node.getId() != null && node.getId().equals("fullscreenImageOverlay"));
            currentFullscreenImageView = null;
            exitFullscreenImageButton = null;
            isImageFullscreen = false;
        }
    }

    private void loadAndPlayVideoFullscreen() {
        if (currentVideoPath != null && rootStackPane != null) {
            Media videoMedia = new Media(getClass().getResource(currentVideoPath).toExternalForm());
            currentVideoPlayer = new MediaPlayer(videoMedia);
            currentMediaView = new MediaView(currentVideoPlayer);
            currentMediaView.setFitWidth(rootStackPane.getWidth() * 0.8);
            currentMediaView.setFitHeight(-1);
            currentMediaView.getStyleClass().add("media-view"); // Apply media-view style

            Slider videoTimeSlider = new Slider();
            videoTimeSlider.getStyleClass().add("slider"); // Apply slider style
            HBox videoTimeLabels = new HBox(5);
            videoTimeLabels.setAlignment(Pos.CENTER);
            Label currentVideoTimeLabel = new Label("0:00");
            Label durationVideoLabel = new Label("0:00");
            currentVideoTimeLabel.getStyleClass().add("label"); // Apply label style
            durationVideoLabel.getStyleClass().add("label"); // Apply label style
            videoTimeLabels.getChildren().addAll(currentVideoTimeLabel, new Label("/"), durationVideoLabel);

            HBox videoButtonControls = new HBox(10);
            videoButtonControls.setAlignment(Pos.CENTER);
            Button playPauseVideoButton = new Button("Play");
            Button stopVideoButton = new Button("Stop");
            fullscreenVideoButton = new Button("Exit Fullscreen");

            playPauseVideoButton.getStyleClass().add("button"); // Apply general button style
            stopVideoButton.getStyleClass().add("button"); // Apply general button style
            fullscreenVideoButton.getStyleClass().add("button"); // Apply general button style

            videoButtonControls.getChildren().addAll(playPauseVideoButton, stopVideoButton, fullscreenVideoButton);

            currentVideoPlayer.setOnReady(() -> {
                Duration totalDuration = currentVideoPlayer.getMedia().getDuration();
                durationVideoLabel.setText(formatDuration(totalDuration));
                videoTimeSlider.setMax(totalDuration.toSeconds());
                currentVideoPlayer.play();
                playPauseVideoButton.setText("Pause");
            });

            currentVideoPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                if (!videoTimeSlider.isValueChanging()) {
                    videoTimeSlider.setValue(newValue.toSeconds());
                    currentVideoTimeLabel.setText(formatDuration(newValue));
                }
            });

            videoTimeSlider.setOnMousePressed(event -> currentVideoPlayer.pause());
            videoTimeSlider.setOnMouseReleased(event -> {
                currentVideoPlayer.seek(Duration.seconds(videoTimeSlider.getValue()));
                currentVideoPlayer.play();
                playPauseVideoButton.setText("Pause");
            });

            playPauseVideoButton.setOnAction(event -> {
                if (currentVideoPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                    currentVideoPlayer.pause();
                    playPauseVideoButton.setText("Play");
                } else {
                    currentVideoPlayer.play();
                    playPauseVideoButton.setText("Pause");
                }
            });

            stopVideoButton.setOnAction(event -> {
                currentVideoPlayer.stop();
                currentVideoPlayer.seek(Duration.ZERO);
                playPauseVideoButton.setText("Play");
                videoTimeSlider.setValue(0);
                currentVideoTimeLabel.setText("0:00");
            });

            currentVideoControls = new VBox(5);
            currentVideoControls.getChildren().addAll(videoTimeLabels, videoTimeSlider, videoButtonControls);
            currentVideoControls.setAlignment(Pos.CENTER);
            currentVideoControls.setId("currentVideoControls"); // Set ID for CSS

            VBox fullscreenOverlay = new VBox(10, currentMediaView, currentVideoControls);
            fullscreenOverlay.setAlignment(Pos.CENTER);
            StackPane.setAlignment(fullscreenOverlay, Pos.CENTER);
            fullscreenOverlay.getStyleClass().add("fullscreen-overlay"); // Apply general overlay style
            fullscreenOverlay.setId("fullscreenVideoOverlay"); // Set ID for CSS

            rootStackPane.getChildren().removeIf(node -> node.getId() != null && node.getId().equals("fullscreenVideoOverlay"));
            rootStackPane.getChildren().add(fullscreenOverlay);

            fullscreenVideoButton.setOnAction(event -> exitFullscreenVideo());
            isVideoFullscreen = true;
        }
    }

    private void exitFullscreenVideo() {
        if (currentMediaView != null && currentVideoControls != null && rootStackPane != null && isVideoFullscreen) {
            rootStackPane.getChildren().removeIf(node -> node.getId() != null && node.getId().equals("fullscreenVideoOverlay"));
            currentVideoPlayer.stop();
            currentVideoPlayer.dispose();
            currentVideoPlayer = null;
            currentMediaView = null;
            currentVideoControls = null;
            fullscreenVideoButton = null;
            isVideoFullscreen = false;
        }
    }

    private void playAudio(String audioPath) {
        stopAudio();

        Media audioMedia = new Media(getClass().getResource(audioPath).toExternalForm());
        audioPlayer = new MediaPlayer(audioMedia);

        audioPlayer.setOnReady(() -> {
            Duration totalDuration = audioPlayer.getMedia().getDuration();
            durationAudioLabel.setText(formatDuration(totalDuration));
            audioTimeSlider.setMax(totalDuration.toSeconds());
            audioControlsContainer.setVisible(true);
            playPauseAudioButton.setText("Play");
        });

        audioPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            if (!audioTimeSlider.isValueChanging()) {
                audioTimeSlider.setValue(newValue.toSeconds());
                currentAudioTimeLabel.setText(formatDuration(newValue));
            }
        });

        playPauseAudioButton.setOnAction(event -> {
            if (audioPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                audioPlayer.pause();
                playPauseAudioButton.setText("Play");
            } else {
                audioPlayer.play();
                playPauseAudioButton.setText("Pause");
            }
        });

        stopAudioButton.setOnAction(event -> {
            audioPlayer.stop();
            audioPlayer.seek(Duration.ZERO);
            playPauseAudioButton.setText("Play");
            audioTimeSlider.setValue(0);
            currentAudioTimeLabel.setText("0:00");
        });

        audioPlayer.play();
        playPauseAudioButton.setText("Pause");
    }

    private void stopAudio() {
        if (audioPlayer != null) {
            audioPlayer.stop();
            audioPlayer.dispose();
            audioPlayer = null;
        }
    }

    private String formatDuration(Duration duration) {
        int intDuration = (int) Math.floor(duration.toSeconds());
        int minutes = intDuration / 60;
        int seconds = intDuration - minutes * 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    public javafx.scene.web.WebView getMapView() {
        return mapView;
    }
}