package com.learnify.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.google.firebase.firestore.*;
import com.learnify.Model.Course;
import com.learnify.Model.UserModel;
import com.learnify.R;
import com.learnify.adapter.CourseAdapter;

import java.util.ArrayList;
import java.util.List;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;


public class HomeFragment extends Fragment {

    private TextView textWelcome;
    private RecyclerView recyclerViewCourses;
    private TextView searchTextView; // 👈 For search input

    private CourseAdapter courseAdapter;
    private List<Course> courseList;
    private List<Course> fullCourseList; // 👈 Keep a full copy for searching

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        textWelcome = view.findViewById(R.id.textWelcome);
        recyclerViewCourses = view.findViewById(R.id.recyclerViewCourses);
        searchTextView = view.findViewById(R.id.searchTextView); // 👈 initialize search view

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        courseList = new ArrayList<>();
        fullCourseList = new ArrayList<>();
        courseAdapter = new CourseAdapter(courseList);
        recyclerViewCourses.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewCourses.setAdapter(courseAdapter);

        fetchUserName();
        fetchCoursesFromFirestore();
        setupSearch(); // 👈 Search logic

        return view;
    }

    private void setupSearch() {
        searchTextView.setFocusableInTouchMode(true);
        searchTextView.setOnClickListener(v -> {
            searchTextView.setText(""); // Clear the hint text on click
        });

        searchTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCourses(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterCourses(String query) {
        courseList.clear();
        if (query.isEmpty()) {
            courseList.addAll(fullCourseList);
        } else {
            for (Course course : fullCourseList) {
                if (course.getCourseName().toLowerCase().contains(query.toLowerCase())) {
                    courseList.add(course);
                }
            }
        }
        courseAdapter.notifyDataSetChanged();
    }

    private void fetchUserName() {
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("user_details")
                .child(auth.getUid());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                UserModel user = snapshot.getValue(UserModel.class);
                if (user != null) {
                    textWelcome.setText("Welcome, " + user.getName() + "!");
                } else {
                    textWelcome.setText("Welcome!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                textWelcome.setText("Welcome!");
            }
        });
    }

    private void fetchCoursesFromFirestore() {
        firestore.collection("courses")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    courseList.clear();
                    fullCourseList.clear();

                    for (DocumentSnapshot doc : querySnapshot) {
                        String name = doc.getString("courseName");
                        String link = doc.getString("youtubeLink");
                        String imageUrl = doc.getString("courseImageUrl");
                        String details = doc.getString("courseDetails");
                        long priceLong = doc.getLong("price") != null ? doc.getLong("price") : 0;
                        int price = (int) priceLong;

                        if (name != null && link != null && imageUrl != null && details != null) {
                            Course course = new Course(name, link, imageUrl, details, price);
                            courseList.add(course);
                            fullCourseList.add(course);
                        } else {
                            Log.w("FirestoreCourse", "Missing fields in document: " + doc.getId());
                        }
                    }

                    courseAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load courses.", Toast.LENGTH_SHORT).show();
                    Log.e("FirestoreCourse", "Error fetching courses: ", e);
                });
    }
}
