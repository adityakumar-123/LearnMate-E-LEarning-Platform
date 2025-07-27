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

public class AppQuestion extends AppCompatActivity {

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
        setContentView(R.layout.activity_app_question);

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
        questions.add(new Question("What does XML define in Android development?",
                Arrays.asList("UI layout", "App logic", "Database schema", "Network protocol"), 0, "Easy"));

        questions.add(new Question("Which file is the entry point of an Android app?",
                Arrays.asList("MainActivity.java", "AndroidManifest.xml", "activity_main.xml", "build.gradle"), 1, "Easy"));

        questions.add(new Question("What language is primarily used for Android app development?",
                Arrays.asList("Kotlin", "Swift", "Dart", "Python"), 0, "Easy"));

        questions.add(new Question("Which method is called when an Activity is first created?",
                Arrays.asList("onStart()", "onResume()", "onCreate()", "onDestroy()"), 2, "Easy"));

        questions.add(new Question("What is the extension of an Android package file?",
                Arrays.asList(".apk", ".dex", ".jar", ".app"), 0, "Easy"));

        // Medium
        questions.add(new Question("Which Android component is used for background tasks?",
                Arrays.asList("Activity", "Service", "ContentProvider", "BroadcastReceiver"), 1, "Medium"));

        questions.add(new Question("What does the 'dp' unit stand for in Android UI design?",
                Arrays.asList("Device Pixels", "Density-independent Pixels", "Dots per Inch", "Design Pixels"), 1, "Medium"));

        questions.add(new Question("What is the function of RecyclerView?",
                Arrays.asList("Display a scrollable list", "Handle animations", "Manage app lifecycle", "Connect to APIs"), 0, "Medium"));

        questions.add(new Question("Which component is responsible for data persistence in Android?",
                Arrays.asList("Intent", "SharedPreferences", "Manifest", "Service"), 1, "Medium"));

        questions.add(new Question("Which folder contains the layout XML files?",
                Arrays.asList("/res/layout", "/src/layout", "/assets/layout", "/layout/res"), 0, "Medium"));

        // Hard
        questions.add(new Question("Which lifecycle method is always called before onResume()?",
                Arrays.asList("onStart()", "onPause()", "onDestroy()", "onStop()"), 0, "Hard"));

        questions.add(new Question("Which class is used for async background tasks in Android (deprecated)?",
                Arrays.asList("AsyncTask", "Thread", "Handler", "ExecutorService"), 0, "Hard"));

        questions.add(new Question("What does ANR stand for in Android?",
                Arrays.asList("Application Not Responding", "Android Not Running", "App Network Retry", "Active Node Resource"), 0, "Hard"));

        questions.add(new Question("How does the ViewModel survive configuration changes?",
                Arrays.asList("By being lifecycle-aware", "Using Singleton pattern", "Stored in SharedPreferences", "Registered in manifest"), 0, "Hard"));

        questions.add(new Question("What is the purpose of ProGuard in Android?",
                Arrays.asList("Obfuscate code", "Speed up app", "Manage permissions", "Debug logs"), 0, "Hard"));

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
                Toast.makeText(AppQuestion.this, "Time's up!", Toast.LENGTH_SHORT).show();
                answered = true;
                highlightAnswers("");
                btnSubmit.setText(currentQuestionIndex == questionList.size() - 1 ? "Finish" : "Next");
            }
        }.start();

        isTimerRunning = true;
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
