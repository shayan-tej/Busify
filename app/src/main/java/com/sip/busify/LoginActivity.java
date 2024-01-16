package com.sip.busify;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sip.busify.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
	private EditText editTextDOB;
	private EditText genderEditText;
	private Spinner genderSpinner;
	private String fullName, dateOfBirth, gender, time, contact, mail, photoUrl;
	private FirebaseDatabase db;
	private DatabaseReference reference;

	public LoginActivity() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		setupAnimations();

		editTextDOB = findViewById(R.id.userDateOfBirth);

		// Initialize views
		genderEditText = findViewById(R.id.userGender);
		genderSpinner = findViewById(R.id.userGenderSpinner);

		// Set up the Spinner with gender options
		List<String> genderOptions = new ArrayList<>();
		genderOptions.add("Male");
		genderOptions.add("Female");
		genderOptions.add("Other");

		ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genderOptions);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		genderSpinner.setAdapter(adapter);

		// Handle item selection in the Spinner
		genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				// Handle the selected gender
				String selectedGender = parentView.getItemAtPosition(position).toString();
				genderEditText.setText(selectedGender);

				// Toggle visibility back to EditText
				genderEditText.setVisibility(View.VISIBLE);
				genderSpinner.setVisibility(View.GONE);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// Do nothing
			}
		});

		// Handle click on the EditText to switch to the Spinner
		genderEditText.setOnClickListener(v -> {
			// Toggle visibility
			genderEditText.setVisibility(View.GONE);
			genderSpinner.setVisibility(View.VISIBLE);
		});

		binding.doneSavingUserDetails.setOnClickListener(v -> {
			fullName = binding.userFullNameEditText.getText().toString();
			dateOfBirth = binding.userDateOfBirth.getText().toString();
			gender = binding.userGender.getText().toString();
			time = recordAndStoreDateTime();
			contact = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getPhoneNumber();
			mail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
			photoUrl = String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl());
			String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

			if (!fullName.isEmpty() && !dateOfBirth.isEmpty() && !gender.isEmpty() && !time.isEmpty()){
				Users users = new Users(fullName, dateOfBirth, gender, time, contact, mail, photoUrl);
				db = FirebaseDatabase.getInstance();
				reference = db.getReference("Users");
				reference.child(uid).setValue(users).addOnCompleteListener(task -> {
					binding.userFullNameEditText.setText("");
					binding.userDateOfBirth.setText("");
					binding.userGender.setText("");
					Toast.makeText(LoginActivity.this,"Successfully Updated", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
					finish();
				});
			}
		});
	}

	private void setupAnimations() {
		Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
		Animation bottomDown = AnimationUtils.loadAnimation(this, R.anim.bottom_down);

		findViewById(R.id.topLinearLayout2).setAnimation(bottomDown);
		findViewById(R.id.appName).setAnimation(bottomDown);

		Handler handler = new Handler();
		handler.postDelayed(() -> {
			findViewById(R.id.cardView1).setAnimation(fadeIn);
			findViewById(R.id.cardView2).setAnimation(fadeIn);
			findViewById(R.id.doneSavingUserDetails).setAnimation(fadeIn);
		}, 1000);
	}

	public void showDatePicker(View view) {
		// Get the current date
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);

		// Create a DatePickerDialog and show it
		DatePickerDialog datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, day);
		datePickerDialog.show();
	}

	private final DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
			// Handle the selected date
			String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
			editTextDOB.setText(selectedDate);
		}
	};

	private String recordAndStoreDateTime() {
		// Get the current time in milliseconds
		long currentTimeMillis = System.currentTimeMillis();

		// Convert the time to a human-readable format
		@SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
		return sdf.format(new Date(currentTimeMillis));
	}
}