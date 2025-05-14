package com.tourguide.tours;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class QuizManager {
    public static void launchQuiz(String location) {
        Question q;
        if (location.equals("maseru")) {
            q = new Question("What is the capital of Lesotho?",
                    new String[]{"Maseru", "Thaba-Tseka", "Mafeteng"}, "Maseru");
        } else {
            q = new Question("Which mountain is associated with the founding of Lesotho?",
                    new String[]{"Thaba-Bosiu", "Qiloane", "Drakensberg"}, "Thaba-Bosiu");
        }

        Alert quiz = new Alert(AlertType.CONFIRMATION);
        quiz.setTitle("Quiz");
        quiz.setHeaderText(q.getQuestion());

        ButtonType[] options = new ButtonType[q.getOptions().length];
        for (int i = 0; i < options.length; i++) {
            options[i] = new ButtonType(q.getOptions()[i]);
        }

        quiz.getButtonTypes().setAll(options);
        Optional<ButtonType> result = quiz.showAndWait();

        if (result.isPresent() && result.get().getText().equals(q.getAnswer())) {
            showResult("Correct!", "Well done!");
        } else {
            showResult("Wrong", "The correct answer was: " + q.getAnswer());
        }
    }

    private static void showResult(String title, String message) {
        Alert result = new Alert(AlertType.INFORMATION);
        result.setTitle(title);
        result.setHeaderText(null);
        result.setContentText(message);
        result.showAndWait();
    }
}
