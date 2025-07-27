package com.learnify.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.learnify.Model.UserModel;
import com.learnify.R;
import com.learnify.databinding.FragmentProfileBinding;
import com.learnify.login.SignInActivity;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Dialog loadingDialog;
    Uri profileUri;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        // Initialize Firebase instances
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        // Initialize loading dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_dialog);
        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loadingDialog.setCancelable(false);
        }

        // Load user profile data
        loadingProfileimage();

        // Set onClickListener for selecting a profile image
        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        // Open terms and conditions page
        binding.cardTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://termsandcondtionforlearnify.netlify.app/")));
            }
        });

        // Open Play Store for app rating
        binding.cardRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getContext().getPackageName())));
            }
        });

        // Logout functionality
        binding.cardLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent intent = new Intent(getContext(), SignInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        return binding.getRoot();
    }

    // Method to load user profile image and details from Firebase
    private void loadingProfileimage() {
        database.getReference().child("user_details").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserModel model = snapshot.getValue(UserModel.class);
                    binding.userName.setText(model.getName());
                    binding.userEmail.setText(model.getEmail());
                    Picasso.get()
                            .load(model.getProfile())
                            .placeholder(R.drawable.profilepic)
                            .into(binding.profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && data != null && data.getData() != null) {
            updateprofile(data.getData());
        }
    }

    // Method to update the user's profile image in Firebase Storage and Database
    private void updateprofile(Uri data) {
        loadingDialog.show();

        final StorageReference reference = storage.getReference().child("profile_image")
                .child(auth.getCurrentUser().getUid());

        reference.putFile(data) // Fixed 'uri' issue by using 'data'
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        // Successfully got the download URL
                                        Log.d("ProfileUpdate", "Profile image URL: " + uri.toString());
                                        HashMap<String, Object> map = new HashMap<>();
                                        map.put("profile", uri.toString());

                                        // Update user details in the database with the new profile URL
                                        database.getReference().child("user_details").child(auth.getUid())
                                                .updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        loadingDialog.dismiss();
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(getContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(getContext(), "Failed to update profile.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                }).addOnFailureListener(e -> {
                                    // Failed to get download URL
                                    Log.e("ProfileUpdate", "Error getting download URL: " + e.getMessage());
                                    Toast.makeText(getContext(), "Error getting profile image URL.", Toast.LENGTH_SHORT).show();
                                    loadingDialog.dismiss();
                                });
                    }
                }).addOnFailureListener(e -> {
                    // Failed to upload the file
                    Log.e("ProfileUpdate", "Error uploading profile image: " + e.getMessage());
                    Toast.makeText(getContext(), "Error uploading profile image.", Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                });
    }
}