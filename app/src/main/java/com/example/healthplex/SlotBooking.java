package com.example.healthplex;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SlotBooking extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slot_booking);

        dbHelper = new DatabaseHelper(this);
        displaySlots();
    }

    private void displaySlots() {
        LinearLayout timeSlotsLayout = findViewById(R.id.timeSlotsLayout);
        timeSlotsLayout.removeAllViews();  // Clear existing views

        String[] slots = {"6am - 7am", "7am - 8am", "8am - 9am", "9am - 10am"};

        for (String slot : slots) {
            View slotView = getLayoutInflater().inflate(R.layout.slot_item, timeSlotsLayout, false);
            TextView slotTextView = slotView.findViewById(R.id.slotTextView);
            TextView bookedSeatsTextView = slotView.findViewById(R.id.bookedSeatsTextView);

            slotTextView.setText(slot);

            // Fetch and display booked seats for each slot
            int bookedSeats = dbHelper.getBookedSeats(slot);
            bookedSeatsTextView.setText(bookedSeats + "/10");  // Assuming max 10 bookings per slot

            // Set click listener to book the slot
            slotView.setOnClickListener(view -> {
                int currentSeats = dbHelper.getBookedSeats(slot);
                if (currentSeats < 10) {  // Check if the slot is not fully booked
                    bookSlot(slot, bookedSeatsTextView);
                } else {
                    Toast.makeText(SlotBooking.this, slot + " is fully booked!", Toast.LENGTH_SHORT).show();
                }
            });

            // Add slot view to the layout
            timeSlotsLayout.addView(slotView);
        }
    }

    // Book a slot and update the count of booked seats
    private void bookSlot(String slot, TextView bookedSeatsTextView) {
        dbHelper.bookSlot(slot);  // Increment seat count in the database
        int newBookingCount = dbHelper.getBookedSeats(slot);
        bookedSeatsTextView.setText(newBookingCount + "/10");  // Update UI after booking
        Toast.makeText(SlotBooking.this, "Successfully booked " + slot + ". Total bookings: " + newBookingCount, Toast.LENGTH_LONG).show();
    }
}
