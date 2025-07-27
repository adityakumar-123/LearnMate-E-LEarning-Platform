package com.learnify.login;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.learnify.R;
import com.learnify.databinding.ActivityForgotPasswordBinding;
import com.learnify.databinding.ActivitySignInBinding;

public class ForgotPasswordActivity extends AppCompatActivity {

    ActivityForgotPasswordBinding binding;
    FirebaseAuth auth;
    Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        loadingDialog = new Dialog(ForgotPasswordActivity.this);
        loadingDialog.setContentView(R.layout.loading_dialog);

        if (loadingDialog.getWindow() != null) {

            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loadingDialog.setCancelable(false);
        }

        binding.buttonForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = binding.email.getText().toString();

                if (email.isEmpty()) {

                    binding.email.setError("Please Enter Your Email");
                }

                else{
                    forgotPassword(email);
                }
            }
        });


        binding.Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ForgotPasswordActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }

    private void forgotPassword(String email) {

        loadingDialog.show();

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                       if (task.isSuccessful()){

                           loadingDialog.dismiss();

                           Toast.makeText(ForgotPasswordActivity.this, "Email Sent Successfully", Toast.LENGTH_SHORT).show();
                           onBackPressed();
                       }
                       else{
                           loadingDialog.dismiss();
                           Toast.makeText(ForgotPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                       }
                    }
                });
    }
}