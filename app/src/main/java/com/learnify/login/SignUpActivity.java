package com.learnify.login;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
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
import com.learnify.Model.UserModel;
import com.learnify.R;
import com.learnify.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    Dialog loadingDialog;

    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        loadingDialog = new Dialog(SignUpActivity.this);
        loadingDialog.setContentView(R.layout.loading_dialog);

        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loadingDialog.setCancelable(false);
        }

        // Show/hide password logic
        binding.eyeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPasswordVisible = !isPasswordVisible;
                if (isPasswordVisible) {
                    binding.editPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    binding.eyeIcon.setImageResource(R.drawable.eye_open); // change icon
                } else {
                    binding.editPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    binding.eyeIcon.setImageResource(R.drawable.eye); // change icon
                }

                // Move cursor to the end of text
                binding.editPassword.setSelection(binding.editPassword.getText().length());
            }
        });

        binding.buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.editName.getText().toString();
                String email = binding.editEmail.getText().toString();
                String password = binding.editPassword.getText().toString();

                if (name.isEmpty()) {
                    binding.editName.setError("Please Enter your Name");
                } else if (email.isEmpty()) {
                    binding.editEmail.setError("Please enter your email");
                } else if (password.isEmpty()) {
                    binding.editPassword.setError("Please enter your Password");
                } else {
                    SignUp(name, email, password);
                }
            }
        });

        binding.AlreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent,
                        android.app.ActivityOptions.makeCustomAnimation(
                                SignUpActivity.this,
                                R.anim.zoomin,
                                R.anim.zoomout
                        ).toBundle()
                );
                finish();
            }
        });
    }

    private void SignUp(String name, String email, String password) {
        loadingDialog.show();

        long startTime = System.currentTimeMillis();

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        long endTime = System.currentTimeMillis();
                        Log.d("SignUp", "Authentication time: " + (endTime - startTime) + "ms");

                        if (task.isSuccessful()) {
                            String userId = task.getResult().getUser().getUid();

                            UserModel model = new UserModel(name, email, password, "https://firebasestorage.googleapis.com/v0/b/learnify-3ca2a.firebasestorage.app/o/profile-icon-design-free-vector.jpg?alt=media&token=52be116e-bda2-4751-b103-fbd72a098f9b");

                            database.getReference().child("user_details").child(userId)
                                    .setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        loadingDialog.dismiss();

                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(SignUpActivity.this, "Welcome! Kindly confirm your email to complete your registration", Toast.LENGTH_SHORT).show();

                                                            auth.signInWithEmailAndPassword(email, password)
                                                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                                            if (task.isSuccessful()) {
                                                                                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                                                                                startActivity(intent);
                                                                                finish();
                                                                            } else {
                                                                                Toast.makeText(SignUpActivity.this, "Error logging in after registration: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    });
                                                        } else {
                                                            Toast.makeText(SignUpActivity.this, "Error sending verification email. Please try again.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(SignUpActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(SignUpActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
