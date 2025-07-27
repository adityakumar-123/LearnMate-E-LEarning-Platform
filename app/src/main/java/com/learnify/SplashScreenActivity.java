package com.learnify;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.learnify.login.SignInActivity;

public class SplashScreenActivity extends AppCompatActivity {

    private TextView loadingText;
    private LinearLayout skipButton;

    private String[] loadingMessages = {
            "Fetching knowledge modules...",
            "Compiling your future...",
            "Sharpening pencils...",
            "Opening minds and books...",
            "Loading learning magic..."
    };
    private int currentIndex = 0;

    private Handler textHandler = new Handler();
    private Runnable textRunnable;
    private Handler splashHandler = new Handler();
    private Runnable splashRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadingText = findViewById(R.id.loadingText);
        skipButton = findViewById(R.id.skipButton);

        // Start changing loading text
        textRunnable = new Runnable() {
            @Override
            public void run() {
                loadingText.setText(loadingMessages[currentIndex]);
                currentIndex = (currentIndex + 1) % loadingMessages.length;
                textHandler.postDelayed(this, 1200);
            }
        };
        textHandler.post(textRunnable);

        // Splash auto-redirect
        splashRunnable = () -> {
            navigateToSignIn();
        };
        splashHandler.postDelayed(splashRunnable, 4000); // 4 seconds splash

        // Handle Skip button click
        skipButton.setOnClickListener(v -> {
            textHandler.removeCallbacks(textRunnable);
            splashHandler.removeCallbacks(splashRunnable);
            navigateToSignIn();
        });
    }

    private void navigateToSignIn() {
        Intent intent = new Intent(SplashScreenActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }
}
