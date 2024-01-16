package com.sip.busify;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.firestore.FirebaseFirestore;
import com.sip.busify.databinding.ActivityConductorTicketBinding;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class ConductorTicketActivity extends AppCompatActivity {
	TextView ticketNo, dAT, fromAndTo, busNo, validity, costOfTicket, validText;
	ImageButton PrintTicket;
	Boolean validOrNot;
	String UID, tNum;

	@SuppressLint("SetTextI18n")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityConductorTicketBinding binding = ActivityConductorTicketBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		ticketNo = binding.ticketNo;
		dAT = binding.dAT;
		fromAndTo = binding.fromAndTo;
		busNo = binding.busNo;
		validity = binding.validity;
		costOfTicket = binding.costOfTicket;
		validText = binding.validityTextView;
		PrintTicket = binding.printVec;

		Intent intent = getIntent();
		if (intent != null) {
			String a = intent.getStringExtra("bN");
			String b = intent.getStringExtra("dAT");
			String c = intent.getStringExtra("fa");
			String d = intent.getStringExtra("fr");
			tNum = intent.getStringExtra("tN");
			String f = intent.getStringExtra("to");
			String g = intent.getStringExtra("vT");
			UID = intent.getStringExtra("UID");
			boolean h = intent.getBooleanExtra("dT", false);
			boolean i = intent.getBooleanExtra("iV", true);

			validOrNot = !h && i;

			ticketNo.setText("Ticket No.:  " + tNum);
			dAT.setText(b);
			fromAndTo.setText(d + "   to   " + f);
			busNo.setText(a);
			validity.setText("Valid till Date:  " + g);
			costOfTicket.setText(c);

			if (!validOrNot) {
				validText.setText("INVALID");
				validText.setTextColor(Color.RED);
			}

			PrintTicket.setOnClickListener(v -> {
				if (validOrNot) {
					ConstraintLayout layout = findViewById(R.id.ticketPrint);
					// Save the screenshot as an image file
					printTicket(layout);
				} else
					Toast.makeText(ConductorTicketActivity.this, "Invalid ticket!", Toast.LENGTH_SHORT).show();
			});
		}
	}

	@Override
	public void onBackPressed() {
		// Redirect the user to the homepage
		super.onBackPressed();
		Intent intent = new Intent(this, HomeActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
		finish();
	}

	private void updateTicketValidity() {
		FirebaseFirestore dbf = FirebaseFirestore.getInstance();

		Map<String, Object> updates = new HashMap<>();
		updates.put("didTravel", true);

		dbf.collection(UID)
				.document(tNum)
				.update(updates)
				.addOnSuccessListener(aVoid -> Log.d(TAG, "Ticket invalid"))
				.addOnFailureListener(e -> Log.e(TAG, "Failed to update ticket validity", e));
	}

	private void printTicket(View view) {
		try {
			// Get the bitmap from the view
			view.setDrawingCacheEnabled(true);
			Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
			view.setDrawingCacheEnabled(false);

			// Convert the bitmap to Base64
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
			byte[] byteArray = byteArrayOutputStream.toByteArray();
			String base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT);

			// Create the data string for the intent
			String str = "<IMAGE>1#" + base64Image + "<110>Ticket by BUSIFY!";

			// Send the intent
			Intent printIntent = new Intent();
			printIntent.setAction(Intent.ACTION_SEND);
			printIntent.setPackage("mate.bluetoothprint");
			printIntent.putExtra(Intent.EXTRA_TEXT, str);
			printIntent.setType("text/plain");
			startActivity(printIntent);

			// Open the Bluetooth Print app explicitly
			Intent openAppIntent = getPackageManager().getLaunchIntentForPackage("mate.bluetoothprint");
			if (openAppIntent != null) {
				startActivity(openAppIntent);
				Toast.makeText(ConductorTicketActivity.this, "Ticket printed!", Toast.LENGTH_SHORT).show();
			} else {
				// Handle the case where the launch intent is null (app not found)
				Toast.makeText(ConductorTicketActivity.this, "Error opening Thermer app", Toast.LENGTH_SHORT).show();
			}
			updateTicketValidity();  // invalid after the scan
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(ConductorTicketActivity.this, "Error printing the ticket", Toast.LENGTH_SHORT).show();
		}
	}
}