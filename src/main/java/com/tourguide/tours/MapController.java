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
import javafx.scene.paint.Color;
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
    private String currentQuizLocation = null;
    private int currentQuestionIndex = -1;
    private Map<String, String> userAnswers = new HashMap<>();
    private List<Question> currentQuestions;
    private Label feedbackLabel = new Label();
    private VBox optionsBox = new VBox(5);
    private Button nextButton = new Button("Next Question");
    private VBox quizContainer = new VBox(10);

    private MediaPlayer currentVideoPlayer;
    private MediaView currentMediaView;
    private VBox currentVideoControls;
    private Button fullscreenVideoButton;
    private boolean isVideoFullscreen = false;
    private double originalVideoWidth;
    private double originalVideoHeight;
    private StackPane rootStackPane;
    private int videoViewIndex = -1;
    private int videoControlsIndex = -1;
    private String currentVideoPath = null;
    private ImageView currentFullscreenImageView;
    private Button exitFullscreenImageButton;
    private boolean isImageFullscreen = false;

    private final Map<String, List<Question>> locationQuizzes = Map.of(
            "maseru", List.of(
                    new Question("What is the capital of Lesotho?", new String[]{"Maseru", "Thaba-Tseka", "Mafeteng"}, "Maseru"),
                    new Question("Which river flows through Maseru?", new String[]{"Orange River", "Caledon River", "Malibamat'so River"}, "Caledon River"),
                    new Question("What is the main airport near Maseru?", new String[]{"Moshoeshoe I International Airport", "Mejametalana Airport", "Leribe Airport"}, "Moshoeshoe I International Airport")
            ),
            "quthing", List.of(
                    new Question("Quthing District is most famous for its abundance of what prehistoric discovery?", new String[]{"Ancient pottery", "Dinosaur footprints", "Early human tools"}, "Dinosaur footprints"),
                    new Question("What is the capital town of the Quthing District, also known as Moyeni?", new String[]{"Mokhotlong", "Quthing", "Thaba-Tseka"}, "Quthing"),
                    new Question("Which historical site in Quthing served as a refuge during the Basotho-Boer War and is now a museum?", new String[]{"Morija Museum", "Masitise Cave House", "Liphofung Cave"}, "Masitise Cave House")
            ),
            "thaba_bosiu", List.of(
                    new Question("Which mountain is associated with the founding of Lesotho?", new String[]{"Thaba-Bosiu", "Qiloane", "Drakensberg"}, "Thaba-Bosiu"),
                    new Question("Who was the founder of Lesotho?", new String[]{"Letsie I", "Moshoeshoe I", "Seeiso"}, "Moshoeshoe I"),
                    new Question("What is the significance of Thaba-Bosiu?", new String[]{"Royal palace", "Historical fortress", "Mining center"}, "Historical fortress")
            ),
            "tsehlanyane", List.of(
                    new Question("In which mountain range is Ts'ehlanyane National Park located?", new String[]{"Drakensberg", "Maluti Mountains", "Lebombo Mountains"}, "Maluti Mountains"),
                    new Question("What is a prominent feature of Ts'ehlanyane's landscape?", new String[]{"Vast grasslands", "Indigenous forest", "High-altitude bamboo"}, "High-altitude bamboo"),
                    new Question("Which river system has its source in the Ts'ehlanyane area?", new String[]{"Senqu River", "Mohokare River", "Malibamat'so River"}, "Malibamat'so River"),
                    new Question("What type of accommodation is primarily found within Ts'ehlanyane National Park?", new String[]{"Luxury hotels", "Self-catering chalets", "Backpacker hostels"}, "Self-catering chalets")
            ),
            "liphofung", List.of(
                    new Question("What is Liphofung primarily known for?", new String[]{"Diamond mining", "Ancient rock paintings", "Large bat colonies"}, "Ancient rock paintings"),
                    new Question("Which King of Lesotho is historically linked to Liphofung Cave?", new String[]{"Letsie II", "Moshoeshoe I", "David Mohohlo"}, "Moshoeshoe I"),
                    new Question("What type of rock shelter is Liphofung Cave?", new String[]{"Limestone cave", "Sandstone overhang", "Volcanic tunnel"}, "Sandstone overhang")
            ),
            "sanipass", List.of(
                    new Question("Which two countries does Sani Pass connect?", new String[]{"Lesotho and South Africa", "Lesotho and Eswatini", "South Africa and Namibia"}, "Lesotho and South Africa"),
                    new Question("What is the typical vehicle requirement for driving up Sani Pass?", new String[]{"Any sedan car", "High-clearance 4x4 vehicle", "Motorcycle only"}, "High-clearance 4x4 vehicle"),
                    new Question("What is the name of the famous pub located at the top of Sani Pass on the South African side?", new String[]{"The Highest Pub in Africa", "Sani Top Tavern", "Mountain View Inn"}, "The Highest Pub in Africa")
            ),
            "maletsunyane", List.of(
                    new Question("Near which town is Maletsunyane Falls primarily located?", new String[]{"Maseru", "Semonkong", "Qacha's Nek"}, "Semonkong"),
                    new Question("Maletsunyane Falls is famous for being one of the highest of what type of waterfall in Africa?", new String[]{"Tiered waterfalls", "Block waterfalls", "Single-drop waterfalls"}, "Single-drop waterfalls"),
                    new Question("Which river plunges over the cliff to form Maletsunyane Falls?", new String[]{"Senqunyane River", "Orange River", "Makhaleng River"}, "Senqunyane River")
            ),
            "pioneermall", List.of(
                    new Question("In which city is Pioneer Mall located?", new String[]{"Maseru", "Mafeteng", "Hlotse"}, "Maseru"),
                    new Question("Compared to Maseru Mall, is Pioneer Mall generally considered larger or smaller?", new String[]{"Larger", "Smaller", "About the same size"}, "Smaller"),
                    new Question("What type of establishment would you typically find in Pioneer Mall?", new String[]{"Heavy industrial factories", "Residential apartments", "Retail stores and restaurants"}, "Retail stores and restaurants")
            )
    );

    public MapController(VBox infoDisplay) {
        this.infoDisplay = infoDisplay;
        mapView = new javafx.scene.web.WebView();
        engine = mapView.getEngine();

        engine.load(getClass().getResource("/map/map.html").toExternalForm());

        engine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == javafx.concurrent.Worker.State.SUCCEEDED) {
                netscape.javascript.JSObject win = (netscape.javascript.JSObject) engine.executeScript("window");
                win.setMember("javaConnector", this);
            }
        });

        nextButton.setOnAction(event -> {
            if (currentQuestionIndex < currentQuestions.size() - 1) {
                currentQuestionIndex++;
                displayQuestion();
            } else {
                displayResults();
            }
        });
        nextButton.setVisible(false);

        audioTimeSlider = new Slider();
        HBox audioTimeLabels = new HBox(5, currentAudioTimeLabel, new Label("/"), durationAudioLabel);
        audioTimeLabels.setAlignment(Pos.CENTER);
        HBox audioButtonControls = new HBox(10, playPauseAudioButton, stopAudioButton);
        audioButtonControls.setAlignment(Pos.CENTER);
        audioControlsContainer.getChildren().addAll(audioTimeLabels, audioTimeSlider, audioButtonControls);
        audioControlsContainer.setAlignment(Pos.CENTER);
        audioControlsContainer.setVisible(false);

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
            currentQuizLocation = location;
            currentQuestionIndex = -1;
            userAnswers.clear();
            feedbackLabel.setText("");
            optionsBox.getChildren().clear();
            nextButton.setVisible(false);
            quizContainer.getChildren().clear();
            isVideoFullscreen = false;
            videoViewIndex = -1;
            videoControlsIndex = -1;
            currentVideoPath = "/media/" + location + "_video.mp4";

            String imageName = "/map/images/" + location + ".jpg";
            String audioPath = "/media/" + location + "_audio.mp3";

            Image image = new Image(getClass().getResourceAsStream(imageName));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(330);
            imageView.setPreserveRatio(true);
            imageView.setStyle("-fx-cursor: hand;"); // Make it look clickable
            imageView.setOnMouseClicked(event -> showFullscreenImage(image));

            Label nameLabel = new Label(location.toUpperCase());
            nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

            Label descriptionLabel = new Label();
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
            playVideoButton.setOnAction(event -> loadAndPlayVideoFullscreen());

            Button AudioGuideButton = new Button("Start Audio Guide");
            AudioGuideButton.setMaxWidth(Double.MAX_VALUE);
            AudioGuideButton.setOnAction(event -> playAudio(audioPath));

            Button quizButton = new Button("Start Quiz");
            quizButton.setMaxWidth(Double.MAX_VALUE);
            quizButton.setOnAction(event -> startQuiz(location));

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

            exitFullscreenImageButton = new Button("Exit Fullscreen");
            exitFullscreenImageButton.setOnAction(event -> exitFullscreenImage());
            exitFullscreenImageButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");

            VBox fullscreenLayout = new VBox(10, currentFullscreenImageView, exitFullscreenImageButton);
            fullscreenLayout.setAlignment(Pos.CENTER);
            StackPane.setAlignment(fullscreenLayout, Pos.CENTER);
            fullscreenLayout.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8);"); // Darker background
            fullscreenLayout.setId("fullscreenImageOverlay");

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

            Slider videoTimeSlider = new Slider();
            HBox videoTimeLabels = new HBox(5);
            videoTimeLabels.setAlignment(Pos.CENTER);
            Label currentVideoTimeLabel = new Label("0:00");
            Label durationVideoLabel = new Label("0:00");
            videoTimeLabels.getChildren().addAll(currentVideoTimeLabel, new Label("/"), durationVideoLabel);

            HBox videoButtonControls = new HBox(10);
            videoButtonControls.setAlignment(Pos.CENTER);
            Button playPauseVideoButton = new Button("Play");
            Button stopVideoButton = new Button("Stop");
            fullscreenVideoButton = new Button("Exit Fullscreen");
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
                videoTimeSlider.setValue(0);
                currentVideoTimeLabel.setText("0:00");
            });

            currentVideoControls = new VBox(5);
            currentVideoControls.getChildren().addAll(videoTimeLabels, videoTimeSlider, videoButtonControls);
            currentVideoControls.setAlignment(Pos.CENTER);

            VBox fullscreenOverlay = new VBox(10, currentMediaView, currentVideoControls);
            fullscreenOverlay.setAlignment(Pos.CENTER);
            StackPane.setAlignment(fullscreenOverlay, Pos.CENTER);
            fullscreenOverlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
            fullscreenOverlay.setId("fullscreenVideoOverlay");

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

    private void startQuiz(String location) {
        currentQuestionIndex = 0;
        currentQuestions = locationQuizzes.get(location);
        if (currentQuestions != null && !currentQuestions.isEmpty()) {
            quizContainer.getChildren().clear();
            displayQuestion();
            infoDisplay.getChildren().add(quizContainer);
        } else {
            Label noQuizLabel = new Label("No quiz available for " + location);
            infoDisplay.getChildren().add(noQuizLabel);
        }
    }

    private void displayQuestion() {
        quizContainer.getChildren().clear();
        feedbackLabel.setText("");
        optionsBox.getChildren().clear();
        nextButton.setVisible(false);

        if (currentQuestionIndex >= 0 && currentQuestionIndex < currentQuestions.size()) {
            Question currentQuestion = currentQuestions.get(currentQuestionIndex);
            Label questionLabel = new Label(currentQuestion.getQuestion());
            questionLabel.setStyle("-fx-font-weight: bold; -fx-padding: 0 0 5 0;");

            for (String option : currentQuestion.getOptions()) {
                Button optionButton = new Button(option);
                optionButton.setMaxWidth(Double.MAX_VALUE);
                optionButton.setOnAction(event -> {
                    String selectedAnswer = optionButton.getText();
                    userAnswers.put(currentQuestion.getQuestion(), selectedAnswer);
                    if (selectedAnswer.equals(currentQuestion.getAnswer())) {
                        feedbackLabel.setText("Correct!");
                        feedbackLabel.setTextFill(Color.GREEN);
                    } else {
                        feedbackLabel.setText("Incorrect. Correct answer: " + currentQuestion.getAnswer());
                        feedbackLabel.setTextFill(Color.RED);
                    }
                    optionsBox.getChildren().forEach(node -> node.setDisable(true));
                    nextButton.setText(currentQuestionIndex < currentQuestions.size() - 1 ? "Next Question" : "Submit Quiz");
                    nextButton.setVisible(true);
                });
                optionsBox.getChildren().add(optionButton);
            }

            quizContainer.getChildren().addAll(questionLabel, optionsBox, feedbackLabel, nextButton);
        }
    }

    private void displayResults() {
        quizContainer.getChildren().clear();
        int score = 0;
        for (Question q : currentQuestions) {
            String userAnswer = userAnswers.get(q.getQuestion());
            Label resultLabel = new Label(q.getQuestion() + " - Your answer: " + userAnswer +
                    (userAnswer != null && userAnswer.equals(q.getAnswer()) ? " (Correct)" : " (Incorrect, Correct answer: " + q.getAnswer() + ")"));
            quizContainer.getChildren().add(resultLabel);
            if (userAnswer != null && userAnswer.equals(q.getAnswer())) {
                score++;
            }
        }
        Label finalScoreLabel = new Label("Your final score: " + score + " out of " + currentQuestions.size());
        finalScoreLabel.setStyle("-fx-font-weight: bold; -fx-padding: 0 0 10 0;");

        Button tryAgainButton = new Button("Try Again");
        tryAgainButton.setMaxWidth(Double.MAX_VALUE);
        tryAgainButton.setOnAction(event -> startQuiz(currentQuizLocation));

        Button exitButton = new Button("Exit Quiz");
        exitButton.setMaxWidth(Double.MAX_VALUE);
        exitButton.setOnAction(event -> {
            quizContainer.getChildren().clear();
            // Optionally, you could re-display the initial location info here if needed
        });

        quizContainer.getChildren().addAll(finalScoreLabel, tryAgainButton, exitButton);
    }

    public javafx.scene.web.WebView getMapView() {
        return mapView;
    }
}