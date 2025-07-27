package com.learnify;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.HashMap;

public class EnrollActivity extends AppCompatActivity implements PaymentResultListener {

    private TextView courseTitle, courseDetails, priceText;
    private ImageView courseImage;
    private Button enrollBtn;

    private String courseName, courseDetailsStr, courseImageUrl, youtubeLink;
    private boolean isPaid;
    private int price;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll);

        courseTitle = findViewById(R.id.courseTitleEnroll);
        courseDetails = findViewById(R.id.courseDetailsEnroll);
        courseImage = findViewById(R.id.courseImageEnroll);
        enrollBtn = findViewById(R.id.enrollButton);
        priceText = findViewById(R.id.priceText);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        courseName = getIntent().getStringExtra("courseName");
        courseDetailsStr = getIntent().getStringExtra("courseDetails");
        courseImageUrl = getIntent().getStringExtra("courseImageUrl");
        youtubeLink = getIntent().getStringExtra("youtubeLink");
        isPaid = getIntent().getBooleanExtra("isPaid", false);
        price = getIntent().getIntExtra("price", 0);

        courseTitle.setText(courseName);
        courseDetails.setText(courseDetailsStr);
        priceText.setText(isPaid ? ("₹" + price) : "Free");
        Glide.with(this).load(courseImageUrl).into(courseImage);

        checkEnrollment();

        enrollBtn.setOnClickListener(v -> {
            if (isPaid) {
                startPayment(price);
            } else {
                saveEnrollment();
            }
        });
    }

    private void checkEnrollment() {
        String userId = auth.getCurrentUser().getUid();

        firestore.collection("users")
                .document(userId)
                .collection("enrolled_courses")
                .document(courseName)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        openVideo();
                    } else {
                        enrollBtn.setVisibility(Button.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error checking enrollment.", Toast.LENGTH_SHORT).show();
                });
    }

    private void startPayment(int priceInRupees) {
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_live_kEU119zQa2QPbX"); // Replace with your Razorpay live/test key

        try {
            JSONObject options = new JSONObject();
            options.put("name", "Learnify");
            options.put("description", "Course Enrollment");
            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            options.put("amount", priceInRupees * 100); // Razorpay expects amount in paisa
            options.put("prefill.email", "user@example.com");
            options.put("prefill.contact", "9876543210");

            checkout.open(this, options);
        } catch (Exception e) {
            Toast.makeText(this, "Payment Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        Toast.makeText(this, "Payment successful!", Toast.LENGTH_SHORT).show();
        saveEnrollment();
    }

    @Override
    public void onPaymentError(int code, String response) {
        Toast.makeText(this, "Payment failed: " + response, Toast.LENGTH_SHORT).show();
    }

    private void saveEnrollment() {
        String userId = auth.getCurrentUser().getUid();

        HashMap<String, Object> data = new HashMap<>();
        data.put("courseName", courseName);
        data.put("youtubeLink", youtubeLink);
        data.put("courseImageUrl", courseImageUrl);
        data.put("courseDetails", courseDetailsStr);
        data.put("price", price);
        data.put("isPaid", isPaid);
        data.put("timestamp", System.currentTimeMillis());

        // 1️⃣ Save to Firestore
        firestore.collection("users")
                .document(userId)
                .collection("enrolled_courses")
                .document(courseName)
                .set(data)
                .addOnSuccessListener(aVoid -> {
                    // 2️⃣ Save to Realtime Database
                    FirebaseDatabase.getInstance().getReference()
                            .child("enrollments")
                            .child(userId)
                            .child(courseName)
                            .setValue(data)
                            .addOnSuccessListener(dbSuccess -> {
                                Toast.makeText(this, "Enrolled successfully!", Toast.LENGTH_SHORT).show();
                                openVideo();
                            })
                            .addOnFailureListener(dbFail -> {
                                Toast.makeText(this, "Firestore saved, DB failed!", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Enrollment failed", Toast.LENGTH_SHORT).show();
                });
    }

    private void openVideo() {
        Intent intent = new Intent(this, VideoPlayerActivity.class);
        intent.putExtra("youtubeLink", youtubeLink);
        startActivity(intent);
        finish();
    }
}
