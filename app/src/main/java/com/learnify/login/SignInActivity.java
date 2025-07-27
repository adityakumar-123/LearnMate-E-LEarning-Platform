package com.learnify.login;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.learnify.MainActivity;
import com.learnify.R;
import com.learnify.databinding.ActivitySignInBinding;
import android.util.Log;
import com.airbnb.lottie.LottieAnimationView;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private Dialog loadingDialog;
    private boolean isValidationInProgress = false;
    private LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Lottie Animation
        lottieAnimationView = findViewById(R.id.lottieAnimation);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        loadingDialog = new Dialog(SignInActivity.this);
        loadingDialog.setContentView(R.layout.loading_dialog);

        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loadingDialog.setCancelable(false);
        }

        setupListeners();

        if (auth.getCurrentUser() != null) {
            navigateToMainActivity();
        }

        // Change Lottie animation after 3 seconds
        changeLottieAnimationAfterDelay();
    }

    private void setupListeners() {
        binding.buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.editEmail.getText().toString();
                String password = binding.editPassword.getText().toString();

                if (email.isEmpty()) {
                    binding.editEmail.setError("Please enter your email");
                } else if (password.isEmpty()) {
                    binding.editPassword.setError("Please enter your Password");
                } else {
                    signIn(email, password);
                }
            }
        });

        binding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create the intent to navigate to SignUpActivity
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);

                // Add transition animation here
                startActivity(intent,
                        android.app.ActivityOptions.makeCustomAnimation(
                                SignInActivity.this,
                                R.anim.zoomin,
                                R.anim.zoomout
                        ).toBundle()
                );
                finish(); // Finish SignInActivity to remove it from the back stack
            }
        });

        binding.ForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, ForgotPasswordActivity.class));
            }
        });

        binding.eyeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });

        // Real-time password validation
        binding.editPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // No action needed here
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // No action needed here for real-time validation
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String enteredPassword = editable.toString();
                if (enteredPassword.length() >= 10 && !isValidationInProgress) {
                    // Show loading dialog only after password length is 8 or more
                    loadingDialog.show();
                    isValidationInProgress = true; // Prevent multiple simultaneous validations
                    validatePasswordWithFirebase(enteredPassword);
                } else if (enteredPassword.length() < 10) {
                    loadingDialog.dismiss();
                    binding.passwordCardView.setBackgroundResource(R.drawable.edittext_background_red);
                }
            }
        });
    }

    private void validatePasswordWithFirebase(String password) {
        String email = binding.editEmail.getText().toString();  // Use the email entered in the form

        if (!email.isEmpty()) {
            // Set a timeout to dismiss the loading dialog after 10 seconds, regardless of the result
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isValidationInProgress) {
                        loadingDialog.dismiss(); // Force the loading dialog to dismiss after 10 seconds
                        isValidationInProgress = false;
                        binding.passwordCardView.setBackgroundResource(R.drawable.edittext_background);
                    }
                }
            }, 10000); // Timeout after 10 seconds

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            loadingDialog.dismiss(); // Dismiss loading dialog after authentication task is complete
                            isValidationInProgress = false;

                            if (task.isSuccessful()) {
                                // Password is correct, turn the field green
                                binding.passwordCardView.setBackgroundResource(R.drawable.edittext_background_green);  // Green background
                            } else {
                                // Password is incorrect, turn the field red
                                binding.passwordCardView.setBackgroundResource(R.drawable.edittext_background_red);  // Red background
                            }
                        }
                    });
        }
    }

    private void signIn(String email, String password) {
        loadingDialog.show();

        long startTime = System.currentTimeMillis();

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        long endTime = System.currentTimeMillis();
                        Log.d("SignIn", "Authentication time: " + (endTime - startTime) + "ms");

                        if (task.isSuccessful()) {
                            if (auth.getCurrentUser().isEmailVerified()) {
                                loadingDialog.dismiss();
                                navigateToMainActivity();
                            } else {
                                Toast.makeText(SignInActivity.this, "Verify your email address to unlock seamless access and enhanced features.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            loadingDialog.dismiss();
                            Toast.makeText(SignInActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            binding.passwordCardView.setCardBackgroundColor(getResources().getColor(R.color.red));
                        }
                    }
                });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void togglePasswordVisibility() {
        if (binding.editPassword.getInputType() == (android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            binding.editPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            binding.eyeIcon.setImageResource(R.drawable.eye_open);
        } else {
            binding.editPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            binding.eyeIcon.setImageResource(R.drawable.eye);
        }

        binding.editPassword.setSelection(binding.editPassword.getText().length());
    }

    private void changeLottieAnimationAfterDelay() {
        // Start the first animation with a slow fade-in
        lottieAnimationView.setAnimation(R.raw.coding);  // First animation (coding)
        lottieAnimationView.setAlpha(0f);  // Start with invisible
        lottieAnimationView.animate()
                .alpha(1f)  // Fade in to fully visible
                .setDuration(4000)  // Duration for fade-in effect
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        // After the first animation fades in, start the animation sequence
                        startAnimationsSequence();
                    }
                })
                .start();
    }

    private void startAnimationsSequence() {
        // Animation 1: Start with the first animation and play
        playAnimationSequence(R.raw.signup);  // Animation 2

        // Animation 2: Wait for the current animation to finish before changing
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                playAnimationSequence(R.raw.coding);  // Animation 3
            }
        }, 7000);  // Delay for 7 seconds (duration of previous animation)

        // Animation 3: After a short delay, transition to the next animation
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                playAnimationSequence(R.raw.android);  // Animation 4
            }
        }, 16000);  // Delay increased to 16 seconds (from 14 seconds)

        // Animation 4: After the last animation, transition back to the first animation
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                transitionBackToFirstAnimation();
            }
        }, 22000);  // Delay increased to 12 seconds (from 9.8 seconds)
    }

    private void playAnimationSequence(int animationResource) {
        // Set the animation resource to the Lottie animation view
        lottieAnimationView.setAnimation(animationResource);
        // Ensure the animation view is visible
        lottieAnimationView.setAlpha(1f);
        // Play the animation
        lottieAnimationView.playAnimation();
    }

    private void transitionBackToFirstAnimation() {
        // Slide the current animation out with subtle slide left and fade out
        float slideDistance = 200f;  // Slight slide distance

        lottieAnimationView.animate()
                .translationX(-slideDistance)  // Slide left slightly
                .alpha(0f)  // Fade out
                .setDuration(800)  // Duration for slide and fade out
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        // Once the current animation is faded and slid out, start the first animation again
                        lottieAnimationView.setAnimation(R.raw.coding);  // Transition back to the first animation
                        lottieAnimationView.setAlpha(0f);  // Start with invisible
                        lottieAnimationView.animate()
                                .alpha(1f)  // Fade in to fully visible
                                .translationX(0)  // Reset to center position
                                .setDuration(2000)  // Fade-in duration
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Once the first animation has finished, transition to the second animation in the sequence again
                                        startAnimationsSequence();
                                    }
                                })
                                .start();
                    }
                })
                .start();
    }
}