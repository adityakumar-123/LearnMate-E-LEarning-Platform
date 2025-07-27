package com.learnify;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.learnify.Fragment.HomeFragment;
import com.learnify.Fragment.ProfileFragment;
import com.learnify.Fragment.QuizFragment;
import com.learnify.Fragment.RoadmapFragment;
import com.learnify.Fragment.CodeFragment; // Make sure this import exists
import com.learnify.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private Fragment homeFragment;
    private Fragment profileFragment;
    private Fragment quizFragment;
    private Fragment roadmapFragment;
    private Fragment codeFragment;
    private Fragment activeFragment;

    private int currentBackgroundResource = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Transparent status bar with fullscreen layout
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNavigation.setItemIconTintList(null);
        binding.bottomNavigation.setSelectedItemId(R.id.nav_home);

        // Initialize fragments
        homeFragment = new HomeFragment();
        profileFragment = new ProfileFragment();
        quizFragment = new QuizFragment();
        roadmapFragment = new RoadmapFragment();
        codeFragment = new CodeFragment(); // ✅ ADD THIS

        // Add all fragments
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, roadmapFragment, "roadmap").hide(roadmapFragment)
                .add(R.id.container, quizFragment, "quiz").hide(quizFragment)
                .add(R.id.container, profileFragment, "profile").hide(profileFragment)
                .add(R.id.container, codeFragment, "code").hide(codeFragment) // ✅ ADD THIS
                .add(R.id.container, homeFragment, "home")
                .commit();

        activeFragment = homeFragment;
        setBackground(R.drawable.quiz_bg);

        binding.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int backgroundRes = R.color.background;

            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                selectedFragment = homeFragment;
                backgroundRes = R.drawable.quiz_bg;
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = profileFragment;
                backgroundRes = R.color.background;
            } else if (itemId == R.id.nav_quiz) {
                selectedFragment = quizFragment;
                backgroundRes = R.drawable.quiz_bg;
            } else if (itemId == R.id.nav_roadmap) {
                selectedFragment = roadmapFragment;
                backgroundRes = R.drawable.bg_gradient_dark;
            } else if (itemId == R.id.nav_code) { // ✅ HANDLE CODE TAB
                selectedFragment = codeFragment;
                backgroundRes = R.drawable.bg_gradient_dark;
            }

            if (selectedFragment != null && selectedFragment != activeFragment) {
                getSupportFragmentManager().beginTransaction()
                        .hide(activeFragment)
                        .show(selectedFragment)
                        .commit();
                activeFragment = selectedFragment;
                setBackground(backgroundRes);
            }

            return true;
        });
    }

    private void setBackground(int resourceId) {
        if (currentBackgroundResource != resourceId) {
            getWindow().getDecorView().setBackgroundResource(resourceId);
            currentBackgroundResource = resourceId;
        }
    }
}
