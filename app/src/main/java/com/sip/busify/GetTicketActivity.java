package com.sip.busify;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.sip.busify.databinding.ActivityGetTicketBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class GetTicketActivity extends AppCompatActivity {

	private FirebaseFirestore dbf;
	private String uid;
	private String ticketNum;

	@SuppressLint("SetTextI18n")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		com.sip.busify.databinding.ActivityGetTicketBinding binding = ActivityGetTicketBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		TextView ticketNo = binding.ticketNo;
		TextView dAT = binding.dAT;
		TextView fromAndTo = binding.fromAndTo;
		TextView busNo = binding.busNo;
		TextView validity = binding.validity;
		TextView costOfTicket = binding.costOfTicket;
		ImageButton sampleQr = binding.sampleQr;
		ImageButton downloadTicket = binding.download;
		dbf = FirebaseFirestore.getInstance();
		uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

		// Retrieve data from the intent
		Intent intent = getIntent();
		if (intent != null) {
			String startingLocation = intent.getStringExtra("start");
			String destination = intent.getStringExtra("end");
			String fair = intent.getStringExtra("fair");

			ticketNum = String.valueOf(generateUniqueNumber());
			String busNum = busNumber(startingLocation, destination);
			String validDate = validityDate();
			String dateAndTime = currentDateAndTime();

			ticketNo.setText("Ticket No.:  " + ticketNum);
			dAT.setText(dateAndTime);
			fromAndTo.setText(startingLocation + "   to   " + destination);
			busNo.setText(busNum);
			validity.setText("Valid till Date:  " + validDate);
			costOfTicket.setText(fair);

			Map<String, Object> ticket = new HashMap<>();
			ticket.put("FROM", startingLocation);
			ticket.put("TO", destination);
			ticket.put("TICKET NUMBER", ticketNum);
			ticket.put("BUS NUMBER", busNum);
			ticket.put("DATE & TIME", dateAndTime);
			ticket.put("FAIR", fair);
			ticket.put("VALID TILL", validDate);
			ticket.put("isValid", true);
			ticket.put("didTravel", false);

			dbf.collection(uid)
					.document(ticketNum)
					.set(ticket)
					.addOnSuccessListener(documentReference -> Toast.makeText(GetTicketActivity.this,"Ticket Generated Successfully!", Toast.LENGTH_SHORT).show())
					.addOnFailureListener(e -> Toast.makeText(GetTicketActivity.this,"Failed! " + e, Toast.LENGTH_SHORT).show());

			scheduleDailyUpdate();

			String dataToEncode = uid + "," + ticketNum;
			displayQRCode(dataToEncode, sampleQr);
			downloadTicket.setOnClickListener(v -> {
				ConstraintLayout layout = findViewById(R.id.fullTicket);
				// Save the screenshot as an image file
				saveBitmap(layout);
				// Toast.makeText(GetTicketActivity.this, "Ticket saved successfully", Toast.LENGTH_SHORT).show();
			});
			sampleQr.setOnClickListener(v -> Toast.makeText(GetTicketActivity.this, "Ticket no. " + ticketNum, Toast.LENGTH_SHORT).show());
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

	private void saveBitmap(View view) {
		try {
			// Create a directory if it doesn't exist
			File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Busify");
			if (!dir.exists()) {
				dir.mkdirs();
			}

			String fileName = ticketNum + "_busify.jpeg";
			File imageFile = new File(dir, fileName);

			// Create an output stream for the file
			OutputStream fos = null;
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
				fos = Files.newOutputStream(imageFile.toPath());
			}

			// Get the bitmap from the view
			view.setDrawingCacheEnabled(true);
			Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
			view.setDrawingCacheEnabled(false);

			// Compress and write the bitmap to the output stream
			assert fos != null;
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

			// Close the output stream
			fos.flush();
			fos.close();

			// Notify the gallery about the new file for it to be visible
			notifyGallery(GetTicketActivity.this, imageFile);
			Toast.makeText(GetTicketActivity.this, "Ticket saved to gallery", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(GetTicketActivity.this, "Error saving ticket to gallery", Toast.LENGTH_SHORT).show();
		}
	}

	private static void notifyGallery(Context context, File file) {
		// Notify the gallery about the new file so that it appears in the gallery app
		MediaScannerConnection.scanFile(context,
				new String[]{file.getAbsolutePath()},
				new String[]{"image/jpeg"},
				(path, uri) -> {
					// Do nothing for now, you can add additional logic if needed
				});
	}

	private void displayQRCode(String data, ImageView imageView) {
		try {
			BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
			Bitmap bitmap = barcodeEncoder.encodeBitmap(data, BarcodeFormat.QR_CODE, 400, 400);
			imageView.setImageBitmap(bitmap);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// End of the day ticket validity
//	private void scheduleDailyUpdate() {
//		Timer timer = new Timer();
//		Calendar calendar = Calendar.getInstance();
//
//		// Set the time for 12:00 AM
//		calendar.set(Calendar.HOUR_OF_DAY, 0);
//		calendar.set(Calendar.MINUTE, 0);
//		calendar.set(Calendar.SECOND, 0);
//
//		// If the current time is already past 12:00 AM, schedule for the next day
//		if (calendar.getTime().before(new Date())) {
//			calendar.add(Calendar.DAY_OF_MONTH, 1);
//		}
//
//		// Calculate the delay until the next 12:00 AM
//		long delay = calendar.getTime().getTime() - new Date().getTime();
//
//		// Schedule the task to run daily
//		timer.scheduleAtFixedRate(new TimerTask() {
//			@Override
//			public void run() {
//				// Update "isValid" field for the ticket
//				updateTicketValidity();
//			}
//		}, delay, 24 * 60 * 60 * 1000); // 24 hours in milliseconds
//	}

	// 3 minutes validity for test purpose
	private void scheduleDailyUpdate() {
		Timer timer = new Timer();
		Calendar calendar = Calendar.getInstance();

		// Set the time for 1 minute from now
		calendar.add(Calendar.MINUTE, 1);

		// Calculate the delay until the next minute
		long delay = calendar.getTime().getTime() - new Date().getTime();

		// Schedule the task to run every minute
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				// Update "isValid" field for the ticket
				updateTicketValidity();
			}
		}, delay, 60 * 3000); // 3 minute in milliseconds
	}

	private void updateTicketValidity() {
		Map<String, Object> updates = new HashMap<>();
		updates.put("isValid", false);

		dbf.collection(uid)
				.document(ticketNum)
				.update(updates)
				.addOnSuccessListener(aVoid -> Log.d(TAG, "Ticket expired"))
				.addOnFailureListener(e -> Log.e(TAG, "Failed to update ticket validity", e));
	}

	private String busNumber(String startLocation, String destinationLocation) {
		if (Objects.equals(startLocation, "Suchitra") && Objects.equals(destinationLocation, "CMR College") || Objects.equals(startLocation, "CMR College") && Objects.equals(destinationLocation, "Suchitra"))
			return "229";
		else if (Objects.equals(startLocation, "Medchal") && Objects.equals(destinationLocation, "Jubilee Bus Station") || Objects.equals(startLocation, "Jubilee Bus Station") && Objects.equals(destinationLocation, "Medchal"))
			return "25S";
		else if (Objects.equals(startLocation, "Secunderabad") && Objects.equals(destinationLocation, "Koti") || Objects.equals(startLocation, "Koti") && Objects.equals(destinationLocation, "Secunderabad"))
			return "1, 8A";
		else if (Objects.equals(startLocation, "Kompally") && Objects.equals(destinationLocation, "Medchal") || Objects.equals(startLocation, "Medchal") && Objects.equals(destinationLocation, "Kompally"))
			return "229";
		else if (Objects.equals(startLocation, "Secunderabad") && Objects.equals(destinationLocation, "Patancheruvu") || Objects.equals(startLocation, "Patancheruvu") && Objects.equals(destinationLocation, "Secunderabad"))
			return "219";
		else if (Objects.equals(startLocation, "Secunderabad") && Objects.equals(destinationLocation, "LB Nagar") || Objects.equals(startLocation, "LB Nagar") && Objects.equals(destinationLocation, "Secunderabad"))
			return "90L";
		else if (Objects.equals(startLocation, "Secunderabad") && Objects.equals(destinationLocation, "Hitech City") || Objects.equals(startLocation, "Hitech City") && Objects.equals(destinationLocation, "Secunderabad"))
			return "47L";
		else if (Objects.equals(startLocation, "Dulapally") && Objects.equals(destinationLocation, "Maisammaguda") || Objects.equals(startLocation, "Maisammaguda") && Objects.equals(destinationLocation, "Dulapally"))
			return "227";
		else if (Objects.equals(startLocation, "Dulapally") && Objects.equals(destinationLocation, "Gandimaisamma") || Objects.equals(startLocation, "Gandimaisamma") && Objects.equals(destinationLocation, "Dulapally"))
			return "273";
		else if (Objects.equals(startLocation, "Bowenpally") && Objects.equals(destinationLocation, "Miyapur") || Objects.equals(startLocation, "Miyapur") && Objects.equals(destinationLocation, "Bowenpally"))
			return "219";
		else {
			Random random = new Random();
			int randomNumber = random.nextInt(71);
			int result = randomNumber + 215;
			return String.valueOf(result);
		}
	}

	private String currentDateAndTime() {
		// Get the current date and time
		LocalDateTime currentDateTime = null;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
			currentDateTime = LocalDateTime.now();
		}
		// Format the date and time using a specific pattern
		DateTimeFormatter formatter = null;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
			formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy    HH:mm:ss");
		}
		String formattedDateTime = null;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
			formattedDateTime = currentDateTime.format(formatter);
		}
		// Print the current date and time
		return formattedDateTime;
	}

	private String validityDate() {
		// Get today's date
		LocalDate today = null;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
			today = LocalDate.now();
		}

		// Calculate tomorrow's date
		LocalDate tomorrow = null;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
			tomorrow = today.plusDays(1);
		}

		// Format the date using a specific pattern
		DateTimeFormatter formatter = null;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
			formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		}
		String formattedTomorrow = null;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
			formattedTomorrow = tomorrow.format(formatter);
		}

		// Print tomorrow's date
		return formattedTomorrow + "    00:00 AM";
	}

	private int generateUniqueNumber() {
		Set<Integer> generatedNumbers = new HashSet<>();
		Random random = new Random();

		while (true) {
			int randomNumber = generateRandomNumber(random);
			if (!generatedNumbers.contains(randomNumber)) {
				generatedNumbers.add(randomNumber);
				return randomNumber;
			}
		}
	}

	private int generateRandomNumber(Random random) {
		int min = (int) Math.pow(10, 5 - 1);
		int max = (int) Math.pow(10, 5) - 1;
		return random.nextInt(max - min + 1) + min;
	}
}