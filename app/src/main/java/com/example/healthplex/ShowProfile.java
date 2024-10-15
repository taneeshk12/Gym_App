package com.example.healthplex;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ShowProfile extends AppCompatActivity {

    private static final String TAG = "ShowProfile";
    private TextView textName, textHeight, textWeight, textGoal, textDOB;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private DocumentReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showprofile);

        // Initialize the TextViews
        textName = findViewById(R.id.textName);
        textHeight = findViewById(R.id.textHeight);
        textWeight = findViewById(R.id.textWeight);
        textGoal = findViewById(R.id.textGoal);
        textDOB = findViewById(R.id.textDOB);

        // Firestore and FirebaseAuth instance
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Get current logged-in user
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String userEmail = currentUser.getEmail();

            // Reference the user's document based on their email
            userRef = db.collection("users").document(userEmail);

            // Fetch user data from Firestore
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Retrieve and display data
                        String name = document.getString("name");
                        String height = document.getString("height");
                        String weight = document.getString("weight");
                        String goal = document.getString("goal");
                        String dob = document.getString("dob");

                        textName.setText("Name: " + name);
                        textHeight.setText("Height: " + height);
                        textWeight.setText("Weight: " + weight);
                        textGoal.setText("Goal: " + goal);
                        textDOB.setText("DOB: " + dob);
                    } else {
                        // Document does not exist, show a toast
                        Toast.makeText(ShowProfile.this, "Data not available", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "No such document");
                    }
                } else {
                    // Task failed, show a toast with error message
                    Toast.makeText(ShowProfile.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "get failed with ", task.getException());
                }
            });
        } else {
            // No user is logged in, show a toast
            Toast.makeText(ShowProfile.this, "No user logged in", Toast.LENGTH_SHORT).show();
        }
    }
}
