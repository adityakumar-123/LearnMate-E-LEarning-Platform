package com.learnify.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.learnify.R;
import com.learnify.WebViewActivity;

public class RoadmapFragment extends Fragment {

    private CardView cardJava, cardWebDev, cardAppDev, cardAiMl, cardPython, cardJavaScript;
    private TextView textTitle, textDescription;
    private SearchView searchView;
    private Animation zoomInAnimation;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_roadmap, container, false);

        // Initialize views
        cardJava = view.findViewById(R.id.cardJava);
        cardWebDev = view.findViewById(R.id.cardWebDev);
        cardAppDev = view.findViewById(R.id.cardAppDev);
        cardAiMl = view.findViewById(R.id.cardAiMl);
        cardPython = view.findViewById(R.id.cardPython);
        cardJavaScript = view.findViewById(R.id.cardJavaScript);
        textTitle = view.findViewById(R.id.tvWelcome);
        textDescription = view.findViewById(R.id.tvbelow);
        searchView = view.findViewById(R.id.Search_bar);

        // Load only the zoom-in animation for click
        zoomInAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.zoomin);

        // Set all cards visible immediately
        showAllCards();

        // Set onClick listeners for each CardView
        cardJava.setOnClickListener(v -> applyAnimationAndNavigate(cardJava, zoomInAnimation, this::openJavaRoadmap));
        cardWebDev.setOnClickListener(v -> applyAnimationAndNavigate(cardWebDev, zoomInAnimation, () -> openWebActivity("Web Development")));
        cardAppDev.setOnClickListener(v -> applyAnimationAndNavigate(cardAppDev, zoomInAnimation, () -> openAppActivity("App Development")));
        cardAiMl.setOnClickListener(v -> applyAnimationAndNavigate(cardAiMl, zoomInAnimation, () -> openAiActivity("AI / ML")));
        cardPython.setOnClickListener(v -> applyAnimationAndNavigate(cardPython, zoomInAnimation, () -> openPythonActivity("Python")));
        cardJavaScript.setOnClickListener(v -> applyAnimationAndNavigate(cardJavaScript, zoomInAnimation, () -> openJavaScriptActivity("JavaScript")));

        // SearchView listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    showAllCards();
                } else {
                    filterCards(newText.toLowerCase());
                }
                return true;
            }
        });

        return view;
    }

    private void applyAnimationAndNavigate(View cardView, Animation animation, Runnable navigateAction) {
        cardView.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                navigateAction.run();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    private void openJavaRoadmap() {
        Intent intent = new Intent(getActivity(), WebViewActivity.class);
        intent.putExtra("URL", "https://roadmap.sh/java");
        startActivity(intent);
    }

    private void openWebActivity(String topic) {
        Intent intent = new Intent(getActivity(), WebViewActivity.class);
        intent.putExtra("URL", "https://roadmap.sh/full-stack");
        startActivity(intent);
    }

    private void openAppActivity(String topic) {
        Intent intent = new Intent(getActivity(), WebViewActivity.class);
        intent.putExtra("URL", "https://roadmap.sh/android");
        startActivity(intent);
    }

    private void openAiActivity(String topic) {
        Intent intent = new Intent(getActivity(), WebViewActivity.class);
        intent.putExtra("URL", "https://roadmap.sh/ai-engineer");
        startActivity(intent);
    }

    private void openPythonActivity(String topic) {
        Intent intent = new Intent(getActivity(), WebViewActivity.class);
        intent.putExtra("URL", "https://roadmap.sh/python");
        startActivity(intent);
    }

    private void openJavaScriptActivity(String topic) {
        Intent intent = new Intent(getActivity(), WebViewActivity.class);
        intent.putExtra("URL", "https://roadmap.sh/javascript");
        startActivity(intent);
    }

    private void filterCards(String query) {
        cardJava.setVisibility(query.contains("java") ? View.VISIBLE : View.GONE);
        cardWebDev.setVisibility(query.contains("web") ? View.VISIBLE : View.GONE);
        cardAppDev.setVisibility(query.contains("app") ? View.VISIBLE : View.GONE);
        cardAiMl.setVisibility(query.contains("ai") || query.contains("ml") ? View.VISIBLE : View.GONE);
        cardPython.setVisibility(query.contains("python") ? View.VISIBLE : View.GONE);
        cardJavaScript.setVisibility(query.contains("javascript") ? View.VISIBLE : View.GONE);
    }

    private void showAllCards() {
        cardJava.setVisibility(View.VISIBLE);
        cardWebDev.setVisibility(View.VISIBLE);
        cardAppDev.setVisibility(View.VISIBLE);
        cardAiMl.setVisibility(View.VISIBLE);
        cardPython.setVisibility(View.VISIBLE);
        cardJavaScript.setVisibility(View.VISIBLE);
    }
}
