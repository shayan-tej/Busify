package com.sip.busify;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.sip.busify.databinding.ActivityConductorTicketBinding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;

public class ConductorTicketActivity extends AppCompatActivity {
	TextView ticketNo, dAT, fromAndTo, busNo, validity, costOfTicket, validText;
	ImageButton PrintTicket;
	Boolean validOrNot;

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
			String e = intent.getStringExtra("tN");
			String f = intent.getStringExtra("to");
			String g = intent.getStringExtra("vT");
			boolean h = intent.getBooleanExtra("dT", false);
			boolean i = intent.getBooleanExtra("iV", true);

			validOrNot = !h && i;

			ticketNo.setText("Ticket No.:  " + e);
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

	private void printTicket(View view) {
		try {
			// Create a directory if it doesn't exist
			File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Busify");
			if (!dir.exists())
				dir.mkdirs();

			String fileName = "conductor_busify.jpeg";
			File imageFile = new File(dir, fileName);

			// Create an output stream for the file
			OutputStream fos = null;
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
				fos = Files.newOutputStream(imageFile.toPath());

			// Get the bitmap from the view
			view.setDrawingCacheEnabled(true);
			Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
			view.setDrawingCacheEnabled(false);

			// Compress and write the bitmap to the output stream
			assert fos != null;
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream); // Use PNG format for lossless compression
			String imageBase64 = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

			// Close the output stream
			fos.flush();
			fos.close();
			Toast.makeText(ConductorTicketActivity.this, "Open Thermer to print the ticket", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(ConductorTicketActivity.this, "Error saving ticket to gallery", Toast.LENGTH_SHORT).show();
		}
	}
}