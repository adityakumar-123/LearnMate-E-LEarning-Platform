package com.learnify;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;

import com.learnify.Model.NoteItem;
import com.learnify.adapter.NoteAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.*;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.*;

public class VideoPlayerActivity extends AppCompatActivity {

    private YouTubePlayerView playerView;
    private EditText etNote;
    private Button btnSaveNote;
    private RecyclerView rvNotes;
    private ImageButton btnFullscreen;

    private float currentTimestamp = 0;
    private String videoId;
    private List<NoteItem> noteList = new ArrayList<>();
    private NoteAdapter noteAdapter;

    private FirebaseFirestore firestore;
    private String userId;

    private boolean isFullscreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        playerView = findViewById(R.id.youtube_player_view);
        etNote = findViewById(R.id.etNote);
        btnSaveNote = findViewById(R.id.btnSaveNote);
        rvNotes = findViewById(R.id.rvNotes);
        btnFullscreen = findViewById(R.id.btnFullscreen);

        firestore = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        noteAdapter = new NoteAdapter(noteList);
        rvNotes.setLayoutManager(new LinearLayoutManager(this));
        rvNotes.setAdapter(noteAdapter);

        getLifecycle().addObserver(playerView);

        String youtubeLink = getIntent().getStringExtra("youtubeLink");
        videoId = extractYoutubeId(youtubeLink);

        loadNotes();

        playerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(videoId, 0);
                youTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onCurrentSecond(@NonNull YouTubePlayer youTubePlayer, float second) {
                        currentTimestamp = second;
                    }
                });
            }
        });

        btnSaveNote.setOnClickListener(v -> {
            String text = etNote.getText().toString().trim();
            if (!text.isEmpty()) {
                NoteItem note = new NoteItem(currentTimestamp, text);
                saveNoteToFirestore(note);
                etNote.setText("");
            }
        });

        btnFullscreen.setOnClickListener(v -> toggleFullscreen());
    }

    private void toggleFullscreen() {
        if (isFullscreen) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            btnFullscreen.setImageResource(R.drawable.fullscreen); // Replace with your fullscreen icon
            isFullscreen = false;
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            btnFullscreen.setImageResource(R.drawable.fullscreen); // Replace with exit icon
            isFullscreen = true;
        }
    }

    private String extractYoutubeId(String url) {
        try {
            Uri uri = Uri.parse(url);
            String videoId = null;

            if (url.contains("youtu.be/")) {
                videoId = uri.getLastPathSegment();
            } else if (url.contains("youtube.com/watch")) {
                videoId = uri.getQueryParameter("v");
            } else if (url.contains("youtube.com/embed/")) {
                videoId = uri.getLastPathSegment();
            }

            if (videoId != null && videoId.contains("?")) {
                videoId = videoId.split("\\?")[0];
            }

            return videoId;
        } catch (Exception e) {
            Toast.makeText(this, "Invalid YouTube link format", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void saveNoteToFirestore(NoteItem note) {
        firestore.collection("users")
                .document(userId)
                .collection("videos")
                .document(videoId)
                .collection("notes")
                .add(note)
                .addOnSuccessListener(docRef -> {
                    noteList.add(note);
                    noteAdapter.notifyItemInserted(noteList.size() - 1);
                    Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save note", Toast.LENGTH_SHORT).show());
    }

    private void loadNotes() {
        firestore.collection("users")
                .document(userId)
                .collection("videos")
                .document(videoId)
                .collection("notes")
                .orderBy("timestamp")
                .get()
                .addOnSuccessListener(query -> {
                    noteList.clear();
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        NoteItem note = doc.toObject(NoteItem.class);
                        noteList.add(note);
                    }
                    noteAdapter.notifyDataSetChanged();
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (playerView != null) playerView.release();
    }
}
