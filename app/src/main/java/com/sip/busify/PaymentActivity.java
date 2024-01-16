package com.sip.busify;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.sip.busify.databinding.ActivityPaymentBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.Random;

public class PaymentActivity extends AppCompatActivity {

	private LinearLayout topLinearLayout;
	private TextView layoutHeader;
	private CardView cardView1, cardView2, cardView3;
	String userName, userPhone, userMail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Add this line
		com.sip.busify.databinding.ActivityPaymentBinding binding = ActivityPaymentBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		topLinearLayout = binding.topLinearLayout1;
		layoutHeader = binding.layoutHeader;
		cardView1 = binding.cardView1;
		cardView2 = binding.cardView2;
		cardView3 = binding.cardView3;

		setupAnimations();

		Intent intent = getIntent();
		if (intent != null) {
			// Initialize Firebase Database
			FirebaseDatabase db = FirebaseDatabase.getInstance();
			// Use the UID of the current user as the key
			String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
			DatabaseReference reference = db.getReference("Users").child(uid);

			String startingLocation = intent.getStringExtra("startingLocation");
			String destination = intent.getStringExtra("destination");
			TextView fairTextView = binding.fairTextView;

			double amount = calculateTicketData(startingLocation, destination);
			String ticketCost = "Rs." + amount;

			fairTextView.setText(ticketCost);
			View payBtn = binding.payBtn;
			TextView fromTo = binding.fromTo;
			fromTo.setText(startingLocation + "  to  " + destination);

			reference.addListenerForSingleValueEvent(new ValueEventListener() {
				@SuppressLint("SetTextI18n")
				@Override
				public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
					if (dataSnapshot.exists()) {
						Users user = dataSnapshot.getValue(Users.class);
						if (user != null) {
							userName = user.getFullName();
							userPhone = user.getContact();
							userMail = user.getMail();
						}
					}
				}

				@Override
				public void onCancelled(@NonNull DatabaseError databaseError) {
					Toast.makeText(PaymentActivity.this, "Database error!", Toast.LENGTH_SHORT).show();
				}
			});

			payBtn.setOnClickListener(v -> {
				// initialize Razorpay account.
				Checkout checkout = new Checkout();
				// set your id as below
				checkout.setKeyID("rzp_test_kAv1QhiIufZDk0");
				// set image
				//checkout.setImage(R.drawable.gfgimage);
				// initialize json object
				JSONObject object = new JSONObject();
				try {
					// to put name
					object.put("name", userName);
					// put description
					object.put("description", "Ticket fair");
					// to set theme color
					object.put("theme.color", "");
					// put the currency
					object.put("currency", "INR");
					// put amount
					object.put("amount", (int) amount * 100);
					// put mobile number
					object.put("prefill.contact", userPhone);
					// put email
					object.put("prefill.email", userMail);
					// open razorpay to checkout activity
					checkout.open(PaymentActivity.this, object);
					Intent intent1 = new Intent(PaymentActivity.this, GetTicketActivity.class);
					intent1.putExtra("start", startingLocation);
					intent1.putExtra("end", destination);
					intent1.putExtra("fair", ticketCost);
					intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent1);
					finish();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			});
		} else {
			Toast.makeText(PaymentActivity.this, "Error getting your selected locations!", Toast.LENGTH_SHORT).show();
			Intent intent2 = new Intent(PaymentActivity.this, HomeActivity.class);
			intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent2);
			finish();
		}
	}

	public void onPaymentSuccess(String s) {
		// this method is called on payment success.
		Toast.makeText(this, "Payment is successful : " + s, Toast.LENGTH_SHORT).show();
	}

	public void onPaymentError(String s) {
		// on payment failed.
		Toast.makeText(this, "Payment Failed due to error : " + s, Toast.LENGTH_SHORT).show();
	}

	private double calculateTicketData(String startLocation, String destinationLocation) {
		if (Objects.equals(startLocation, "Suchitra") && Objects.equals(destinationLocation, "CMR College") || Objects.equals(startLocation, "CMR College") && Objects.equals(destinationLocation, "Suchitra"))
			return 30.00;
		else if (Objects.equals(startLocation, "Medchal") && Objects.equals(destinationLocation, "Jubilee Bus Station") || Objects.equals(startLocation, "Jubilee Bus Station") && Objects.equals(destinationLocation, "Medchal"))
			return 40.00;
		else if (Objects.equals(startLocation, "Secunderabad") && Objects.equals(destinationLocation, "Koti") || Objects.equals(startLocation, "Koti") && Objects.equals(destinationLocation, "Secunderabad"))
			return 35.00;
		else if (Objects.equals(startLocation, "Kompally") && Objects.equals(destinationLocation, "Medchal") || Objects.equals(startLocation, "Medchal") && Objects.equals(destinationLocation, "Kompally"))
			return 30.00;
		else if (Objects.equals(startLocation, "Secunderabad") && Objects.equals(destinationLocation, "Patancheruvu") || Objects.equals(startLocation, "Patancheruvu") && Objects.equals(destinationLocation, "Secunderabad"))
			return 45.00;
		else if (Objects.equals(startLocation, "Secunderabad") && Objects.equals(destinationLocation, "LB Nagar") || Objects.equals(startLocation, "LB Nagar") && Objects.equals(destinationLocation, "Secunderabad"))
			return 50.00;
		else if (Objects.equals(startLocation, "Secunderabad") && Objects.equals(destinationLocation, "Hitech City") || Objects.equals(startLocation, "Hitech City") && Objects.equals(destinationLocation, "Secunderabad"))
			return 40.00;
		else if (Objects.equals(startLocation, "Dulapally") && Objects.equals(destinationLocation, "Maisammaguda") || Objects.equals(startLocation, "Maisammaguda") && Objects.equals(destinationLocation, "Dulapally"))
			return 30.00;
		else if (Objects.equals(startLocation, "Dulapally") && Objects.equals(destinationLocation, "Gandimaisamma") || Objects.equals(startLocation, "Gandimaisamma") && Objects.equals(destinationLocation, "Dulapally"))
			return 35.00;
		else if (Objects.equals(startLocation, "Bowenpally") && Objects.equals(destinationLocation, "Miyapur") || Objects.equals(startLocation, "Miyapur") && Objects.equals(destinationLocation, "Bowenpally"))
			return 35.00;
		else {
			Random random = new Random();
			int randomNumber = random.nextInt(6) + 4;
			return randomNumber * 5;
		}
	}

	private void setupAnimations() {
		Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
		Animation bottomDown = AnimationUtils.loadAnimation(this, R.anim.bottom_down);

		topLinearLayout.setAnimation(bottomDown);
		layoutHeader.setAnimation(bottomDown);

		Handler handler = new Handler();
		handler.postDelayed(() -> {
			cardView1.setAnimation(fadeIn);
			cardView2.setAnimation(fadeIn);
			cardView3.setAnimation(fadeIn);
		}, 1000);
	}
}