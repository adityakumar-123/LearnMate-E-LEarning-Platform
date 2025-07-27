package com.learnify.Model;

public class Course {
    private String courseName;
    private String youtubeLink;
    private String courseImageUrl;
    private String courseDetails;
    private int price;

    public Course() {} // Firestore needs this

    public Course(String courseName, String youtubeLink, String courseImageUrl, String courseDetails, int price) {
        this.courseName = courseName;
        this.youtubeLink = youtubeLink;
        this.courseImageUrl = courseImageUrl;
        this.courseDetails = courseDetails;
        this.price = price;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getYoutubeLink() {
        return youtubeLink;
    }

    public String getCourseImageUrl() {
        return courseImageUrl;
    }

    public String getCourseDetails() {
        return courseDetails;
    }

    public int getPrice() {
        return price;
    }

    public boolean isPaid() {
        return price > 0;
    }
}
