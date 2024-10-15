package com.example.healthplex;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Button;
import java.util.ArrayList;

public class DailyRemind extends AppCompatActivity {

    private ArrayList<String> reminders;
    private ArrayAdapter<String> remindersAdapter;
    private ListView listViewReminders;
    private ImageView imageViewExerciseAnimation; // ImageView for GIFs
    private VideoView videoViewExercise; // VideoView for videos
    private boolean isVideoPlaying = false; // Flag to control video playback

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dailyremind);

        listViewReminders = findViewById(R.id.listViewReminders);
        reminders = new ArrayList<>();
        reminders.add("Do 30 minutes of cardio");
        reminders.add("Complete strength training");
        reminders.add("Drink 2 liters of water");

        remindersAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, reminders);
        listViewReminders.setAdapter(remindersAdapter);
        listViewReminders.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        findViewById(R.id.buttonAddReminder).setOnClickListener(view -> {
            showAddReminderDialog();
        });

        imageViewExerciseAnimation = findViewById(R.id.imageViewExerciseAnimation);
        videoViewExercise = findViewById(R.id.videoViewExercise);

        setupExerciseCards();

        // Setup Next button
        Button buttonNext = findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener(view -> stopVideo());
    }

    private void showAddReminderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Reminder");

        final EditText input = new EditText(this);
        input.setHint("Enter your reminder");
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String newReminder = input.getText().toString();
            if (!newReminder.isEmpty()) {
                reminders.add(newReminder);
                remindersAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Reminder cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void setupExerciseCards() {
        findViewById(R.id.cardExercise1).setOnClickListener(view -> playExercise(R.raw.exercise1_video));
        findViewById(R.id.cardExercise2).setOnClickListener(view -> playExercise(R.raw.exercise2_video));
        findViewById(R.id.cardExercise3).setOnClickListener(view -> playExercise(R.raw.exercise3_video));
    }

    private void playExercise(int videoResId) {
        // Hide ImageView for GIFs and show VideoView
        imageViewExerciseAnimation.setVisibility(View.GONE);
        videoViewExercise.setVisibility(View.VISIBLE);

        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + videoResId);
        videoViewExercise.setVideoURI(uri);

        // Set an error listener to handle playback errors
        videoViewExercise.setOnErrorListener((mp, what, extra) -> {
            Toast.makeText(this, "Error playing video", Toast.LENGTH_SHORT).show();
            return true; // Handle the error
        });

        // Prepare the video to loop
        videoViewExercise.setOnPreparedListener(mp -> {
            mp.setLooping(true); // Set the video to loop
            videoViewExercise.start(); // Start the video
            isVideoPlaying = true; // Update the playback flag
        });

        // Hide video when completed
        videoViewExercise.setOnCompletionListener(mp -> {
            videoViewExercise.setVisibility(View.GONE); // Hide after completion
            isVideoPlaying = false; // Update playback flag
        });
    }

    private void stopVideo() {
        if (isVideoPlaying) {
            videoViewExercise.stopPlayback(); // Stop the video playback
            videoViewExercise.setVisibility(View.GONE); // Hide the VideoView
            isVideoPlaying = false; // Update playback flag
        }
    }
}
