package com.example.healthplex;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText editTextName, editTextWeight, editTextHeight, editDob;
    private Spinner spinnerGoal;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        editTextName = findViewById(R.id.editTextName);
        editTextWeight = findViewById(R.id.editTextWeight);
        editTextHeight = findViewById(R.id.editTextHeight);
        editDob = findViewById(R.id.editDob);
        spinnerGoal = findViewById(R.id.spinnerGoal);
        btnSave = findViewById(R.id.btnSave);

        // Define options for the spinner
        String[] goals = {"Weight Loss", "Muscle Gain", "Lean Bulk"};

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, goals);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGoal.setAdapter(adapter);

        // Load existing user data if logged in
        loadUserProfile();

        btnSave.setOnClickListener(view -> saveProfile());
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(email);

            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Load user data into fields
                        String name = document.getString("name");
                        String weight = document.getString("weight");
                        String height = document.getString("height");

                        // Check if DOB is a Timestamp
                        if (document.get("DOB") instanceof com.google.firebase.Timestamp) {
                            com.google.firebase.Timestamp timestamp = document.getTimestamp("DOB");
                            // Format the date as a string
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                            String dob = dateFormat.format(timestamp.toDate());
                            editDob.setText(dob);
                        } else {
                            String dob = document.getString("DOB"); // Handle as string if already formatted
                            editDob.setText(dob);
                        }

                        String goal = document.getString("goal");

                        editTextName.setText(name);
                        editTextWeight.setText(weight);
                        editTextHeight.setText(height);

                        // Set spinner selection based on user's goal
                        // int spinnerPosition = adapter.getPosition(goal);
                        // spinnerGoal.setSelection(spinnerPosition);
                    } else {
                        Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
        }
    }


    private void saveProfile() {
        String name = editTextName.getText().toString().trim();
        String weight = editTextWeight.getText().toString().trim();
        String height = editTextHeight.getText().toString().trim();
        String dobString = editDob.getText().toString().trim();
        String goal = spinnerGoal.getSelectedItem().toString();

        if (name.isEmpty() || weight.isEmpty() || height.isEmpty() || dobString.isEmpty()) {
            Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert dobString to Date object
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date dob;
        try {
            dob = dateFormat.parse(dobString);
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save profile data to Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String email = auth.getCurrentUser().getEmail();
        if (email == null) {
            Toast.makeText(this, "User email not found", Toast.LENGTH_SHORT).show();
            return;
        }
        DocumentReference userRef = db.collection("users").document(email);

        Map<String, Object> profile = new HashMap<>();
        profile.put("name", name);
        profile.put("weight", weight);
        profile.put("height", height);
        profile.put("DOB", dob);
        profile.put("goal", goal);

        userRef.set(profile)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ProfileActivity.this, "Profile saved successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ProfileActivity.this, "Failed to save profile", Toast.LENGTH_SHORT).show();
                });
    }
}
