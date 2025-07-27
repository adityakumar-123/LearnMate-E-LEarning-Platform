package com.learnify.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.learnify.R;

import java.util.List;

public class PopularCourseAdapter extends RecyclerView.Adapter<PopularCourseAdapter.CourseViewHolder> {

    private List<Course> courseList;

    // Constructor
    public PopularCourseAdapter(List<Course> courseList) {
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.popular_course_card, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        // Bind the course data to the views
        Course course = courseList.get(position);

        holder.courseTitle.setText(course.getTitle());
        holder.courseDescription.setText(course.getDescription());
        // Set image dynamically (example: using Glide or Picasso)
        holder.courseImage.setImageResource(course.getImageResource()); // Replace with actual image loading logic
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView courseTitle, courseDescription;
        ImageView courseImage;

        public CourseViewHolder(View itemView) {
            super(itemView);
            courseTitle = itemView.findViewById(R.id.courseTitle);
            courseDescription = itemView.findViewById(R.id.courseDescription);
            courseImage = itemView.findViewById(R.id.courseImage);
        }
    }

    // Course model class to hold course data
    public static class Course {
        private String title;
        private String description;
        private int imageResource;

        public Course(String title, String description, int imageResource) {
            this.title = title;
            this.description = description;
            this.imageResource = imageResource;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public int getImageResource() {
            return imageResource;
        }
    }
}
