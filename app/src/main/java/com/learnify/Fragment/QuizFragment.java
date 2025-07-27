package com.learnify.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.learnify.R;
import com.learnify.question.AiMl;
import com.learnify.question.AppQuestion;
import com.learnify.question.JavaQuestion;
import com.learnify.question.Web_questions;
import com.learnify.question.python_activity;

public class QuizFragment extends Fragment {

    // Declare CardView variables
    private CardView cardJava, cardWebDev, cardAppDev, cardAiMl,cardPython;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);

        // Initialize the CardViews
        cardJava = view.findViewById(R.id.cardJava);
        cardWebDev = view.findViewById(R.id.cardWebDev);
        cardAppDev = view.findViewById(R.id.cardAppDev);
        cardAiMl = view.findViewById(R.id.cardAiMl);
        cardPython=view.findViewById(R.id.cardPython);

        // Set onClick listeners for each CardView to navigate to respective quiz activity
        cardJava.setOnClickListener(v -> openQuizActivity("Java"));
        cardWebDev.setOnClickListener(v -> openWebActivity("Web Development"));
        cardAppDev.setOnClickListener(v -> openAppActivity("App Development"));
        cardAiMl.setOnClickListener(v -> openAiActivity("AI / ML"));
        cardPython.setOnClickListener(v -> openPyhtonActivity("Python"));

        return view;
    }

    // Method to open Java quiz activity
    private void openQuizActivity(String topic) {
        Intent intent = new Intent(getActivity(), JavaQuestion.class);
        intent.putExtra("TOPIC", topic);  // Pass the topic to the next activity
        startActivity(intent);
    }

    // Method to open Web Development quiz activity
    private void openWebActivity(String topic) {
        Intent intent = new Intent(getActivity(), Web_questions.class);
        intent.putExtra("TOPIC", topic);  // Pass the topic to the next activity
        startActivity(intent);
    }

    // Method to open App Development quiz activity
    private void openAppActivity(String topic) {
        Intent intent = new Intent(getActivity(), AppQuestion.class);
        intent.putExtra("TOPIC", topic);  // Pass the topic to the next activity
        startActivity(intent);
    }

    // Method to open AI/ML quiz activity
    private void openAiActivity(String topic) {
        Intent intent = new Intent(getActivity(), AiMl.class);
        intent.putExtra("TOPIC", topic);  // Pass the topic to the next activity
        startActivity(intent);
    }

    private void openPyhtonActivity(String topic) {
        Intent intent = new Intent(getActivity(), python_activity.class);
        intent.putExtra("TOPIC", topic);  // Pass the topic to the next activity
        startActivity(intent);
    }
}
