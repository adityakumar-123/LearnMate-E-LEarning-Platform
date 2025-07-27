package com.learnify.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.learnify.EnrollActivity;
import com.learnify.Model.Course;
import com.learnify.R;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private List<Course> courseList;
    private Context context;

    public CourseAdapter(List<Course> courseList) {
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courseList.get(position);

        holder.courseTitle.setText(course.getCourseName());
        holder.courseDetails.setText(course.getCourseDetails());

        // Price tag handling
        if (course.isPaid()) {
            holder.priceTag.setText("₹" + course.getPrice());
            holder.priceTag.setBackgroundResource(R.drawable.bg_paid_tag);
        } else {
            holder.priceTag.setText("Free");
            holder.priceTag.setBackgroundResource(R.drawable.bg_free_tag);
        }

        Glide.with(context)
                .load(course.getCourseImageUrl())
                .placeholder(R.drawable.android_logo)
                .into(holder.courseImage);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EnrollActivity.class);
            intent.putExtra("courseName", course.getCourseName());
            intent.putExtra("courseDetails", course.getCourseDetails());
            intent.putExtra("courseImageUrl", course.getCourseImageUrl());
            intent.putExtra("youtubeLink", course.getYoutubeLink());
            intent.putExtra("isPaid", course.isPaid());
            intent.putExtra("price", course.getPrice());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView courseTitle, courseDetails, priceTag;
        ImageView courseImage;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseTitle = itemView.findViewById(R.id.courseTitle);
            courseDetails = itemView.findViewById(R.id.courseDetails);
            priceTag = itemView.findViewById(R.id.priceTag);
            courseImage = itemView.findViewById(R.id.courseImage);
        }
    }
}
