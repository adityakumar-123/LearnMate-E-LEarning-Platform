package com.learnify.question;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.learnify.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.*;

public class Web_questions extends AppCompatActivity {

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
        setContentView(R.layout.activity_web_questions);

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

        // Easy
        questions.add(new Question("What does HTML stand for?",
                Arrays.asList("Hyper Text Markup Language", "Home Tool Markup Language", "Hyperlinks and Text Markup Language", "Hyperlinking Text Mark Language"), 0, "Easy"));

        questions.add(new Question("Which tag is used to create a hyperlink in HTML?",
                Arrays.asList("<a>", "<link>", "<href>", "<url>"), 0, "Easy"));

        questions.add(new Question("Which language is used for styling web pages?",
                Arrays.asList("HTML", "JQuery", "CSS", "XML"), 2, "Easy"));

        questions.add(new Question("Which HTML tag is used to display an image?",
                Arrays.asList("<image>", "<img>", "<src>", "<pic>"), 1, "Easy"));

        questions.add(new Question("What does CSS stand for?",
                Arrays.asList("Cascading Style Sheets", "Creative Style System", "Computer Style Syntax", "Color Style Sheet"), 0, "Easy"));

        // Medium
        questions.add(new Question("What is the purpose of the 'alt' attribute in an <img> tag?",
                Arrays.asList("To set image alignment", "To provide alternate text", "To link the image", "To set background color"), 1, "Medium"));

        questions.add(new Question("Which HTTP method is used to retrieve data from a server?",
                Arrays.asList("POST", "PUT", "GET", "DELETE"), 2, "Medium"));

        questions.add(new Question("Which of the following is a JavaScript framework?",
                Arrays.asList("Laravel", "React", "Django", "Spring"), 1, "Medium"));

        questions.add(new Question("Which HTML attribute is used to define inline styles?",
                Arrays.asList("style", "css", "font", "class"), 0, "Medium"));

        questions.add(new Question("In the MERN stack, which library is used for building user interfaces?",
                Arrays.asList("React", "Express", "Node", "MongoDB"), 0, "Medium"));

        // Hard
        questions.add(new Question("What is the main difference between localStorage and sessionStorage?",
                Arrays.asList("localStorage persists after browser closes, sessionStorage doesn't", "They are the same", "localStorage clears on tab close", "sessionStorage persists forever"), 0, "Hard"));

        questions.add(new Question("What is CORS in web development?",
                Arrays.asList("Cross-Origin Resource Sharing", "Content-Origin Request Setup", "Control Origin Routing Service", "Cross Object Resource Setup"), 0, "Hard"));

        questions.add(new Question("Which security header prevents a website from being embedded in an iframe?",
                Arrays.asList("X-Frame-Options", "Content-Security-Policy", "Strict-Transport-Security", "X-XSS-Protection"), 0, "Hard"));

        questions.add(new Question("What is the purpose of a middleware function in Express.js?",
                Arrays.asList("Intercept HTTP requests and responses", "Define HTML structure", "Style front-end pages", "Connect to the database"), 0, "Hard"));

        questions.add(new Question("What is JSX in React?",
                Arrays.asList("A syntax extension that allows HTML in JavaScript", "A new JavaScript library", "CSS-in-JS format", "A replacement for JavaScript"), 0, "Hard"));

        // Coding
        questions.add(new Question("What does this line of JavaScript output: console.log(typeof null);",
                Arrays.asList("'object'", "'null'", "'undefined'", "'boolean'"), 0, "Coding"));

        questions.add(new Question("Which method is used to create a new array without modifying the original?",
                Arrays.asList("map()", "forEach()", "push()", "splice()"), 0, "Coding"));

        questions.add(new Question("How do you declare a constant in JavaScript?",
                Arrays.asList("const x = 5;", "constant x = 5;", "let x = constant;", "define x = 5;"), 0, "Coding"));

        questions.add(new Question("Which tag creates a dropdown in HTML?",
                Arrays.asList("<select>", "<input type='dropdown'>", "<dropdown>", "<option>"), 0, "Coding"));

        questions.add(new Question("What does this CSS do: .box { margin: 0 auto; }",
                Arrays.asList("Centers the element horizontally", "Adds margin around all sides", "Removes margin", "Floats the element"), 0, "Coding"));

        questions.add(new Question("What will this code return: Array.isArray([]);",
                Arrays.asList("true", "false", "undefined", "error"), 0, "Coding"));

        questions.add(new Question("Which command initializes a new React app?",
                Arrays.asList("npx create-react-app my-app", "npm init react", "react new app", "npx react start"), 0, "Coding"));

        questions.add(new Question("In MongoDB, which method retrieves all documents in a collection?",
                Arrays.asList("find()", "getAll()", "fetch()", "select()"), 0, "Coding"));

        questions.add(new Question("Which Express method handles GET requests?",
                Arrays.asList("app.get()", "app.send()", "app.route()", "app.fetch()"), 0, "Coding"));

        questions.add(new Question("How do you export a module in Node.js?",
                Arrays.asList("module.exports = myFunc;", "export myFunc;", "exports: myFunc;", "require('myFunc');"), 0, "Coding"));

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
                Toast.makeText(Web_questions.this, "Time's up!", Toast.LENGTH_SHORT).show();
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            String uid = user.getUid();
            TextView score = tvScore; // replace with your actual score variable

            HashMap<String, Object> data = new HashMap<>();
            data.put("email", email);
            data.put("uid", uid);
            data.put("score", score);

            FirebaseFunctions.getInstance()
                    .getHttpsCallable("sendCertificate")
                    .call(data)
                    .addOnSuccessListener(httpsCallableResult -> {
                        Log.d("Cert", "✅ Certificate triggered successfully");
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Cert", "❌ Failed to trigger certificate", e);
                    });
        }
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
