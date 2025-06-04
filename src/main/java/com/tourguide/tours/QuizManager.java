package com.tourguide.tours;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizManager {
    private VBox infoDisplay; // The VBox where quiz UI will be displayed
    private String currentQuizLocation = null;
    private int currentQuestionIndex = -1;
    private Map<String, String> userAnswers = new HashMap<>();
    private List<Question> currentQuestions;
    private Label feedbackLabel = new Label();
    private VBox optionsBox = new VBox(5);
    private Button nextButton = new Button("Next Question");
    private VBox quizContainer = new VBox(10); // Container for all quiz elements

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

    public QuizManager(VBox infoDisplay) {
        this.infoDisplay = infoDisplay;

        // Apply CSS ID to the main quiz container
        quizContainer.setId("quizContainer");

        nextButton.setOnAction(event -> {
            if (currentQuestionIndex < currentQuestions.size() - 1) {
                currentQuestionIndex++;
                displayQuestion();
            } else {
                displayResults();
            }
        });
        nextButton.setVisible(false); // Initially hidden
        nextButton.getStyleClass().add("quiz-action-button"); // Apply CSS class
    }

    public void startQuiz(String location) {
        this.currentQuizLocation = location;
        currentQuestionIndex = 0;
        userAnswers.clear();
        feedbackLabel.setText("");
        optionsBox.getChildren().clear();
        nextButton.setVisible(false);
        quizContainer.getChildren().clear(); // Clear previous quiz content

        currentQuestions = locationQuizzes.get(location);
        if (currentQuestions != null && !currentQuestions.isEmpty()) {
            displayQuestion();
            // Add the quizContainer to the infoDisplay, ensuring it's visible
            if (!infoDisplay.getChildren().contains(quizContainer)) {
                infoDisplay.getChildren().add(quizContainer);
            }
        } else {
            Label noQuizLabel = new Label("No quiz available for " + location);
            noQuizLabel.getStyleClass().add("label"); // Apply general label style
            quizContainer.getChildren().add(noQuizLabel);
            if (!infoDisplay.getChildren().contains(quizContainer)) {
                infoDisplay.getChildren().add(quizContainer);
            }
        }
    }

    private void displayQuestion() {
        quizContainer.getChildren().clear();
        feedbackLabel.setText("");
        optionsBox.getChildren().clear();
        nextButton.setVisible(false);
        optionsBox.getStyleClass().add("options-box");

        if (currentQuestionIndex >= 0 && currentQuestionIndex < currentQuestions.size()) {
            Question question = currentQuestions.get(currentQuestionIndex);
            Label questionLabel = new Label(question.getQuestion());
            questionLabel.getStyleClass().add("question-label");
            questionLabel.setWrapText(true);

            for (String option : question.getOptions()) {
                Button optionButton = new Button(option);
                optionButton.setMaxWidth(Double.MAX_VALUE);
                optionButton.getStyleClass().add("options-box-button");
                optionButton.setOnAction(event -> {
                    String selectedAnswer = optionButton.getText();
                    userAnswers.put(question.getQuestion(), selectedAnswer);
                    if (selectedAnswer.equals(question.getAnswer())) {
                        feedbackLabel.setText("Correct!");
                        feedbackLabel.setTextFill(Color.GREEN);
                    } else {
                        feedbackLabel.setText("Incorrect. Correct answer: " + question.getAnswer());
                        feedbackLabel.setTextFill(Color.RED);
                    }
                    feedbackLabel.getStyleClass().add("feedback-label");
                    optionsBox.getChildren().forEach(node -> node.setDisable(true)); // Disable all option buttons
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
            Label resultLabel = new Label(q.getQuestion() + "\n - Your answer: " + (userAnswer != null ? userAnswer : "Not answered") +
                    (userAnswer != null && userAnswer.equals(q.getAnswer()) ? " (Correct)" : " (Incorrect, Correct answer: " + q.getAnswer() + ")"));
            resultLabel.setWrapText(true);
            resultLabel.getStyleClass().add("results-label");
            quizContainer.getChildren().add(resultLabel);
            if (userAnswer != null && userAnswer.equals(q.getAnswer())) {
                score++;
            }
        }
        Label finalScoreLabel = new Label("Your final score: " + score + " out of " + currentQuestions.size());
        finalScoreLabel.getStyleClass().add("final-score-label");

        Button tryAgainButton = new Button("Try Again");
        tryAgainButton.setMaxWidth(Double.MAX_VALUE);
        tryAgainButton.getStyleClass().add("quiz-action-button");
        tryAgainButton.setOnAction(event -> startQuiz(currentQuizLocation));

        Button exitButton = new Button("Exit Quiz");
        exitButton.setMaxWidth(Double.MAX_VALUE);
        exitButton.getStyleClass().add("quiz-action-button");
        exitButton.setOnAction(event -> clearQuizUI());

        quizContainer.getChildren().addAll(finalScoreLabel, tryAgainButton, exitButton);
    }

    // we Call this method from MapController when changing location or clearing info
    public void clearQuizUI() {
        quizContainer.getChildren().clear();
        if (infoDisplay.getChildren().contains(quizContainer)) {
            infoDisplay.getChildren().remove(quizContainer);
        }
    }
}