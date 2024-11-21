package b.brandquiz;

import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class QuizActivity extends AppCompatActivity {
    TextView timerTextView, questionTextView;
    Button optionAButton, optionBButton, optionCButton, optionDButton;

    DatabaseHelper db;
    Cursor questionCursor;
    int currentQuestionIndex = 0;
    int score = 0;
    final int totalQuestions = 10;
    final int qualifyingMarks = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_quiz);
        timerTextView = findViewById(R.id.timerTextView);
        questionTextView = findViewById(R.id.questionTextView);
        optionAButton = findViewById(R.id.optionAButton);
        optionBButton = findViewById(R.id.optionBButton);
        optionCButton = findViewById(R.id.optionCButton);
        optionDButton = findViewById(R.id.optionDButton);

        db = new DatabaseHelper(this);
        questionCursor = db.getQuestions();

        startTimer();
        loadQuestion();

        optionAButton.setOnClickListener(v -> checkAnswer("A"));
        optionBButton.setOnClickListener(v -> checkAnswer("B"));
        optionCButton.setOnClickListener(v -> checkAnswer("C"));
        optionDButton.setOnClickListener(v -> checkAnswer("D"));
    }

    private void startTimer() {
        new CountDownTimer(600000, 1000) {
            public void onTick(long millisUntilFinished) {
                timerTextView.setText("Time Left: " + millisUntilFinished / 1000 + " seconds");
            }

            public void onFinish() {
                timerTextView.setText("Time's Up!");
                questionCursor.close();
                checkQualification();
            }
        }.start();
    }

    private void loadQuestion() {
        if (currentQuestionIndex < totalQuestions && questionCursor.moveToPosition(currentQuestionIndex)) {
            questionTextView.setText(questionCursor.getString(questionCursor.getColumnIndex("question")));
            optionAButton.setText(questionCursor.getString(questionCursor.getColumnIndex("optionA")));
            optionBButton.setText(questionCursor.getString(questionCursor.getColumnIndex("optionB")));
            optionCButton.setText(questionCursor.getString(questionCursor.getColumnIndex("optionC")));
            optionDButton.setText(questionCursor.getString(questionCursor.getColumnIndex("optionD")));
        } else {
            questionCursor.close();
            checkQualification();
        }
    }

    private void checkAnswer(String selectedOption) {
        String correctAnswer = questionCursor.getString(questionCursor.getColumnIndex("correctAnswer"));
        if (selectedOption.equals(correctAnswer)) {
            score++;
        }
        currentQuestionIndex++;
        loadQuestion();
    }

    private void checkQualification() {
        if (score >= qualifyingMarks) {
            Toast.makeText(this, "Qualified for interview with score: " + score, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Rejected with score: " + score, Toast.LENGTH_LONG).show();
        }
        finish();
    }
}