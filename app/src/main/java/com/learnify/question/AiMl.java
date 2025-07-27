package com.learnify.question;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.*;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.learnify.R;

import java.util.*;

public class AiMl extends AppCompatActivity {

    private TextView tvQuestion, tvScore, tvDifficulty, tvTimer;
    private RadioGroup radioGroupOptions;
    private RadioButton rbOption1, rbOption2, rbOption3, rbOption4;
    private Button btnSubmit, btnPowerUp, btnExtendTime;
    private ProgressBar progressBar;

    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private boolean answered = false;
    private String correctAnswerText = "";
    private String currentDifficulty = "";

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;
    private static final long EASY_TIME = 15000;
    private static final long MEDIUM_TIME = 30000;
    private static final long HARD_TIME = 60000;
    private static final long TIME_EXTENSION = 10000;

    private boolean isTimerRunning = false;
    private boolean isPowerUpUsed = false;
    private boolean isExtendUsed = false;
    private boolean isFiftyCooldown = false;
    private boolean isExtendCooldown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ai_ml);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        tvQuestion = findViewById(R.id.tvQuestion);
        tvScore = findViewById(R.id.tvScore);
        tvDifficulty = findViewById(R.id.tvDifficultyLevel);
        tvTimer = findViewById(R.id.tvTimer);
        radioGroupOptions = findViewById(R.id.radioGroupOptions);
        rbOption1 = findViewById(R.id.rbOption1);
        rbOption2 = findViewById(R.id.rbOption2);
        rbOption3 = findViewById(R.id.rbOption3);
        rbOption4 = findViewById(R.id.rbOption4);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnPowerUp = findViewById(R.id.btnFiftyFifty);
        btnExtendTime = findViewById(R.id.btnExtendTimer);
        progressBar = findViewById(R.id.progressBar);

        questionList = generateQuestions();
        Collections.shuffle(questionList);
        loadQuestion();

        btnSubmit.setOnClickListener(v -> {
            if (!answered) {
                int selectedId = radioGroupOptions.getCheckedRadioButtonId();
                if (selectedId == -1) {
                    Toast.makeText(this, "Please select an option", Toast.LENGTH_SHORT).show();
                    return;
                }

                answered = true;
                RadioButton selectedButton = findViewById(selectedId);
                String selectedText = selectedButton.getText().toString();
                highlightAnswers(selectedText);

                if (selectedText.equals(correctAnswerText)) {
                    score++;
                }

                if (currentQuestionIndex == questionList.size() - 1) {
                    btnSubmit.setText("Finish");
                } else {
                    btnSubmit.setText("Next");
                }

            } else {
                currentQuestionIndex++;
                if (currentQuestionIndex < questionList.size()) {
                    loadQuestion();
                } else {
                    showScore();
                }
            }
        });

        btnPowerUp.setOnClickListener(v -> {
            if (!isPowerUpUsed && !isFiftyCooldown) {
                isPowerUpUsed = true;
                eliminateTwoWrongOptions();
                btnPowerUp.setEnabled(false);
                isFiftyCooldown = true;

                new Handler().postDelayed(() -> {
                    isFiftyCooldown = false;
                }, 10000); // 10 sec cooldown
            } else {
                Toast.makeText(this, "Power-Up cooling down...", Toast.LENGTH_SHORT).show();
            }
        });

        btnExtendTime.setOnClickListener(v -> {
            if (!isExtendUsed && !isExtendCooldown) {
                isExtendUsed = true;
                timeLeftInMillis += TIME_EXTENSION;
                countDownTimer.cancel();
                startTimer(timeLeftInMillis);
                btnExtendTime.setEnabled(false);
                isExtendCooldown = true;

                new Handler().postDelayed(() -> {
                    isExtendCooldown = false;
                }, 10000); // 10 sec cooldown
            } else {
                Toast.makeText(this, "Extend Timer cooling down...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<Question> generateQuestions() {
        List<Question> questions = new ArrayList<>();

        // AI/ML - Easy
        questions.add(new Question("What does AI stand for?",
                Arrays.asList("Artificial Intelligence", "Automatic Input", "Advanced Interface", "Automated Intelligence"), 0, "Easy"));

        questions.add(new Question("Which of the following is an application of AI?",
                Arrays.asList("Face Recognition", "Web Hosting", "Disk Partitioning", "File Compression"), 0, "Easy"));

        questions.add(new Question("What does ML stand for in the context of AI?",
                Arrays.asList("Machine Learning", "Manual Logic", "Multi Language", "Meta Learning"), 0, "Easy"));

        questions.add(new Question("Which language is most commonly used in ML?",
                Arrays.asList("Python", "JavaScript", "C#", "HTML"), 0, "Easy"));

        questions.add(new Question("Which of the following is a supervised learning algorithm?",
                Arrays.asList("Linear Regression", "K-Means", "PCA", "Apriori"), 0, "Easy"));

        // AI/ML - Medium
        questions.add(new Question("What is overfitting in machine learning?",
                Arrays.asList("Model fits training data too well and performs poorly on new data",
                        "Model fails to fit training data",
                        "Model predicts accurately on all data",
                        "Model learns from noise"), 0, "Medium"));

        questions.add(new Question("Which ML model is used for classification tasks?",
                Arrays.asList("Decision Tree", "Linear Regression", "K-Means", "PCA"), 0, "Medium"));

        questions.add(new Question("Which library is used for deep learning in Python?",
                Arrays.asList("TensorFlow", "Pandas", "NumPy", "Matplotlib"), 0, "Medium"));

        questions.add(new Question("What is the purpose of the activation function in neural networks?",
                Arrays.asList("Add non-linearity", "Store data", "Filter data", "Increase accuracy"), 0, "Medium"));

        questions.add(new Question("Which of these is an example of unsupervised learning?",
                Arrays.asList("Clustering", "Classification", "Regression", "Recommendation"), 0, "Medium"));

        // AI/ML - Hard
        questions.add(new Question("What does the softmax function do in a neural network?",
                Arrays.asList("Converts logits to probabilities", "Minimizes loss", "Calculates gradients", "Optimizes weights"), 0, "Hard"));

        questions.add(new Question("What is the vanishing gradient problem?",
                Arrays.asList("Gradients become too small to update weights effectively",
                        "Gradients explode during backpropagation",
                        "Learning rate becomes zero",
                        "Neurons vanish from the model"), 0, "Hard"));

        questions.add(new Question("Which algorithm is commonly used in reinforcement learning?",
                Arrays.asList("Q-learning", "Linear Regression", "Naive Bayes", "SVM"), 0, "Hard"));

        questions.add(new Question("In Natural Language Processing, what does 'tokenization' refer to?",
                Arrays.asList("Splitting text into smaller units like words or phrases",
                        "Encoding data",
                        "Removing stop words",
                        "Training a model"), 0, "Hard"));

        questions.add(new Question("Which type of neural network is best suited for sequence data?",
                Arrays.asList("RNN", "CNN", "DNN", "GAN"), 0, "Hard"));

        // AI/ML Coding Questions
        questions.add(new Question("Which Python library is commonly used for data manipulation?",
                Arrays.asList("Pandas", "TensorFlow", "OpenCV", "Seaborn"), 0, "Medium"));

        questions.add(new Question("How do you import the NumPy library in Python?",
                Arrays.asList("import numpy as np", "include numpy", "require 'numpy'", "load numpy"), 0, "Easy"));

        questions.add(new Question("Which function is used to split data into training and testing sets in scikit-learn?",
                Arrays.asList("train_test_split()", "split_data()", "data_split()", "model_split()"), 0, "Medium"));

        questions.add(new Question("What does the following line do? model.fit(X, y)",
                Arrays.asList("Trains the model", "Splits the data", "Evaluates the model", "Saves the model"), 0, "Easy"));

        questions.add(new Question("Which parameter in scikit-learn's DecisionTreeClassifier limits overfitting?",
                Arrays.asList("max_depth", "shuffle", "random_state", "verbose"), 0, "Hard"));

        questions.add(new Question("What will this return: `np.zeros((2,3))`?",
                Arrays.asList("A 2x3 matrix filled with zeros", "A 3x2 matrix filled with ones", "An empty array", "A list with 5 elements"), 0, "Easy"));

        questions.add(new Question("Which method is used to visualize a confusion matrix in seaborn?",
                Arrays.asList("heatmap()", "plot()", "bar()", "conf_matrix()"), 0, "Medium"));

        questions.add(new Question("What’s the output of `model.predict(X_test)`?",
                Arrays.asList("Predicted labels", "Accuracy score", "Loss value", "Training data"), 0, "Easy"));

        questions.add(new Question("Which Python package is commonly used for computer vision tasks?",
                Arrays.asList("OpenCV", "scikit-learn", "Numpy", "XGBoost"), 0, "Medium"));

        questions.add(new Question("Which optimizer is widely used in training deep neural networks?",
                Arrays.asList("Adam", "SGD", "RMSprop", "All of the above"), 3, "Hard"));

        Collections.shuffle(questions); // Optional: Randomize questions
        return questions;
    }



    private void loadQuestion() {
        // Cancel the previous timer if still running
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        answered = false;
        progressBar.setVisibility(View.GONE);
        tvScore.setVisibility(View.GONE);
        radioGroupOptions.clearCheck();
        resetOptionColors();

        rbOption1.setVisibility(View.VISIBLE);
        rbOption2.setVisibility(View.VISIBLE);
        rbOption3.setVisibility(View.VISIBLE);
        rbOption4.setVisibility(View.VISIBLE);

        if (!isFiftyCooldown) {
            btnPowerUp.setEnabled(true);
            isPowerUpUsed = false;
        }

        if (!isExtendCooldown) {
            btnExtendTime.setEnabled(true);
            isExtendUsed = false;
        }

        btnSubmit.setText("Submit");

        Question question = questionList.get(currentQuestionIndex);
        tvQuestion.setText(question.getQuestionText());

        List<String> options = question.getOptions();
        correctAnswerText = options.get(question.getCorrectAnswerIndex());

        rbOption1.setText(options.get(0));
        rbOption2.setText(options.get(1));
        rbOption3.setText(options.get(2));
        rbOption4.setText(options.get(3));

        currentDifficulty = question.getDifficulty();
        tvDifficulty.setText("Difficulty: " + currentDifficulty);

        long questionTime = getTimeForDifficulty(); // ← fresh time
        startTimer(questionTime);
    }


    private void startTimer(long timeInMillis) {
        timeLeftInMillis = timeInMillis;

        progressBar.setMax((int) timeInMillis);
        progressBar.setProgress((int) timeInMillis);

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimer();
                progressBar.setProgress((int) timeLeftInMillis);
            }

            @Override
            public void onFinish() {
                timeLeftInMillis = 0;
                updateTimer();
                progressBar.setProgress(0);
                Toast.makeText(AiMl.this, "Time's up!", Toast.LENGTH_SHORT).show();
                answered = true;
                highlightAnswers("");

                // Check if it's the last question
                if (currentQuestionIndex == questionList.size() - 1) {
                    // If it's the last question, stop the timer
                    cancelTimer();
                    btnSubmit.setText("Finish");
                } else {
                    // Otherwise, go to the next question
                    btnSubmit.setText("Next");
                }
            }
        }.start();
        isTimerRunning = true;
    }

    private void cancelTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        isTimerRunning = false;
    }



    private void updateTimer() {
        int seconds = (int) (timeLeftInMillis / 1000);
        tvTimer.setText(String.format("%02d:%02d", seconds / 60, seconds % 60));
    }

    private long getTimeForDifficulty() {
        switch (currentDifficulty) {
            case "Easy":
                return EASY_TIME;
            case "Medium":
                return MEDIUM_TIME;
            case "Hard":
                return HARD_TIME;
            default:
                return EASY_TIME;
        }
    }

    private void resetOptionColors() {
        rbOption1.setTextColor(Color.WHITE);
        rbOption2.setTextColor(Color.WHITE);
        rbOption3.setTextColor(Color.WHITE);
        rbOption4.setTextColor(Color.WHITE);
    }

    private void highlightAnswers(String selectedText) {
        RadioButton[] buttons = {rbOption1, rbOption2, rbOption3, rbOption4};

        for (RadioButton btn : buttons) {
            String text = btn.getText().toString();
            if (text.equals(correctAnswerText)) {
                btn.setTextColor(Color.GREEN);
            } else if (text.equals(selectedText)) {
                btn.setTextColor(Color.RED);
            } else {
                btn.setTextColor(Color.WHITE);
            }
        }
    }

    private void showScore() {
        cancelTimer();
        tvScore.setText("Score: " + score + "/" + questionList.size());
        tvScore.setVisibility(View.VISIBLE);
        btnSubmit.setEnabled(false);
        Toast.makeText(this, "Quiz Finished!", Toast.LENGTH_LONG).show();
    }

    private void eliminateTwoWrongOptions() {
        List<RadioButton> options = new ArrayList<>(Arrays.asList(rbOption1, rbOption2, rbOption3, rbOption4));
        Collections.shuffle(options);
        int eliminated = 0;
        for (RadioButton option : options) {
            if (!option.getText().equals(correctAnswerText) && eliminated < 2) {
                option.setVisibility(View.INVISIBLE);
                eliminated++;
            }
        }
    }

    static class Question {
        private final String questionText;
        private final List<String> options;
        private final int correctAnswerIndex;
        private final String difficulty;

        public Question(String questionText, List<String> options, int correctAnswerIndex, String difficulty) {
            this.questionText = questionText;
            this.options = options;
            this.correctAnswerIndex = correctAnswerIndex;
            this.difficulty = difficulty;
        }

        public String getQuestionText() {
            return questionText;
        }

        public List<String> getOptions() {
            return options;
        }

        public int getCorrectAnswerIndex() {
            return correctAnswerIndex;
        }

        public String getDifficulty() {
            return difficulty;
        }
    }
}
