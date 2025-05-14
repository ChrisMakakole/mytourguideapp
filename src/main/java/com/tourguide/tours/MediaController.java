package com.tourguide.tours;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.media.MediaView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.geometry.Pos;

public class MediaController {
    private static MediaPlayer currentPlayer;

    public static void playMedia(String location, Runnable onFinished) {
        String audioPath = "/media/" + location + "_audio.mp3";
        String videoPath = "/media/" + location + "_video.mp4";

        Media audio = new Media(MediaController.class.getResource(audioPath).toExternalForm());
        MediaPlayer audioPlayer = new MediaPlayer(audio);

        audioPlayer.setOnEndOfMedia(() -> {
            playVideo(videoPath, onFinished);
        });

        audioPlayer.play();
        if (currentPlayer != null && currentPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            currentPlayer.stop();
        }
        currentPlayer = audioPlayer;
    }

    private static void playVideo(String videoPath, Runnable onFinished) {
        Media media = new Media(MediaController.class.getResource(videoPath).toExternalForm());
        MediaPlayer player = new MediaPlayer(media);
        MediaView view = new MediaView(player);

        Button playButton = new Button("Play");
        Button pauseButton = new Button("Pause");
        Button stopButton = new Button("Stop");

        playButton.setOnAction(event -> {
            if (player.getStatus() != MediaPlayer.Status.PLAYING) {
                player.play();
            }
        });

        pauseButton.setOnAction(event -> {
            if (player.getStatus() == MediaPlayer.Status.PLAYING) {
                player.pause();
            }
        });

        stopButton.setOnAction(event -> {
            player.stop();
        });

        HBox controls = new HBox(10, playButton, pauseButton, stopButton);
        controls.setAlignment(Pos.CENTER);

        VBox root = new VBox(10, view, controls);
        Stage stage = new Stage();
        stage.setTitle("Video Tour");
        stage.setScene(new Scene(root, 800, 550)); // Increased height to accommodate controls
        stage.show();

        player.setOnEndOfMedia(() -> {
            stage.close();
            onFinished.run();
        });

        player.play();
        if (currentPlayer != null && currentPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            currentPlayer.stop();
        }
        currentPlayer = player;
    }
}