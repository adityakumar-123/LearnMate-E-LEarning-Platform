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

public class python_activity extends AppCompatActivity {

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
        setContentView(R.layout.activity_python);

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

        // Easy (Python)
        questions.add(new Question("What is the correct file extension for Python files?",
                Arrays.asList(".py", ".python", ".pt", ".pyt"), 0, "Easy"));

        questions.add(new Question("Which keyword is used to define a function in Python?",
                Arrays.asList("func", "define", "def", "function"), 2, "Easy"));

        questions.add(new Question("Which of these is used to output data in Python?",
                Arrays.asList("echo", "console.log", "printf", "print"), 3, "Easy"));

        questions.add(new Question("What symbol is used to start a comment in Python?",
                Arrays.asList("//", "#", "/*", "--"), 1, "Easy"));

        questions.add(new Question("Which of the following is a valid variable name in Python?",
                Arrays.asList("2value", "value_2", "value-2", "@value"), 1, "Easy"));

        // Medium (Python)
        questions.add(new Question("Which of these data types is immutable in Python?",
                Arrays.asList("List", "Set", "Dictionary", "Tuple"), 3, "Medium"));

        questions.add(new Question("How do you start a for loop in Python?",
                Arrays.asList("for i to range:", "foreach i in range:", "for i in range():", "loop i in range():"), 2, "Medium"));

        questions.add(new Question("What is the output of: print(type([]))?",
                Arrays.asList("<class 'list'>", "<type 'list'>", "<list>", "list"), 0, "Medium"));

        questions.add(new Question("How do you handle exceptions in Python?",
                Arrays.asList("try/except", "catch/try", "error/handle", "try/catch"), 0, "Medium"));

        questions.add(new Question("Which method adds an element to the end of a list?",
                Arrays.asList("insert()", "append()", "add()", "push()"), 1, "Medium"));

        // Hard (Python)
        questions.add(new Question("What does the 'yield' keyword do in Python?",
                Arrays.asList("Returns a value", "Pauses a function and returns a generator", "Creates a new thread", "Stops the function"), 1, "Hard"));

        questions.add(new Question("What is a lambda function in Python?",
                Arrays.asList("A function with no name", "A recursive function", "A type of loop", "A method with multiple returns"), 0, "Hard"));

        questions.add(new Question("Which of the following is not a valid Python data structure?",
                Arrays.asList("List", "Stack", "Tuple", "Set"), 1, "Hard"));

        questions.add(new Question("What is the difference between is and == in Python?",
                Arrays.asList("They are the same", "'is' checks identity, '==' checks equality", "'is' checks equality, '==' checks identity", "Both are used to compare types"), 1, "Hard"));

        questions.add(new Question("Which module in Python is used for regular expressions?",
                Arrays.asList("regex", "re", "pattern", "pyregex"), 1, "Hard"));

        // Coding (Python)
        questions.add(new Question("Write a Python function to check if a number is even.",
                Arrays.asList("def is_even(n): return n % 2 == 0", "def is_even(n): return n % 2 == 1", "def is_even(n): return n // 2", "def is_even(n): print(n%2)"), 0, "Coding"));

        questions.add(new Question("Write a function that returns the factorial of a number.",
                Arrays.asList("def factorial(n): return 1 if n==0 else n*factorial(n-1)", "def factorial(n): return n+n", "def factorial(n): return n/2", "def factorial(n): return n*2"), 0, "Coding"));

        questions.add(new Question("Create a function to reverse a string.",
                Arrays.asList("def reverse(s): return s[::-1]", "def reverse(s): return s[::1]", "def reverse(s): return s", "def reverse(s): return ''.join(s)"), 0, "Coding"));

        questions.add(new Question("Write a function that counts vowels in a string.",
                Arrays.asList("def count_vowels(s): return sum(1 for c in s if c in 'aeiouAEIOU')", "def count_vowels(s): return s.count('aeiou')", "def count_vowels(s): return len(s)", "def count_vowels(s): return vowels(s)"), 0, "Coding"));

        questions.add(new Question("Write a function to check for a palindrome.",
                Arrays.asList("def is_palindrome(s): return s == s[::-1]", "def is_palindrome(s): return s == s[1:]", "def is_palindrome(s): return s.endswith(s)", "def is_palindrome(s): return True"), 0, "Coding"));

        questions.add(new Question("Write a Python function to sum a list of numbers.",
                Arrays.asList("def sum_list(lst): return sum(lst)", "def sum_list(lst): return lst.sum()", "def sum_list(lst): return len(lst)", "def sum_list(lst): return all(lst)"), 0, "Coding"));

        questions.add(new Question("Create a function that returns a list of even numbers from a given list.",
                Arrays.asList("def evens(lst): return [x for x in lst if x%2==0]", "def evens(lst): return x%2==0", "def evens(lst): return lst*2", "def evens(lst): return x in lst"), 0, "Coding"));

        questions.add(new Question("Create a function to calculate the nth Fibonacci number.",
                Arrays.asList("def fib(n): return n if n<=1 else fib(n-1)+fib(n-2)", "def fib(n): return n*2", "def fib(n): return n+n", "def fib(n): return n/2"), 0, "Coding"));

        questions.add(new Question("Write a function to sort a list in ascending order.",
                Arrays.asList("def sort_list(lst): return sorted(lst)", "def sort_list(lst): return lst.sort()", "def sort_list(lst): return lst[::-1]", "def sort_list(lst): return lst.sort(desc=True)"), 0, "Coding"));

        questions.add(new Question("Create a function to check if a list is empty.",
                Arrays.asList("def is_empty(lst): return len(lst) == 0", "def is_empty(lst): return lst == None", "def is_empty(lst): return lst == '[]'", "def is_empty(lst): return not list"), 0, "Coding"));

        Collections.shuffle(questions); // Optional random shuffle
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
                Toast.makeText(python_activity.this, "Time's up!", Toast.LENGTH_SHORT).show();
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
