package com.sip.busify;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.sip.busify.databinding.FragmentAccountBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class AccountFragment extends Fragment {

	private FragmentAccountBinding binding;
	private TextView profileName, profileCredential, profileCredentialValue, profileDOB, profileGender;
	private ImageView profilePicture;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {

		binding = FragmentAccountBinding.inflate(inflater, container, false);
		View view = binding.getRoot();

		setupAnimations();

		profileName = binding.profileName;
		profileCredential = binding.credential;
		profileCredentialValue = binding.profileCredential;
		profileDOB = binding.profileDOB;
		profileGender = binding.profileGender;
		profilePicture = binding.profilePicture;

		// Initialize Firebase Database
		FirebaseDatabase db = FirebaseDatabase.getInstance();
		// Use the UID of the current user as the key
		String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
		DatabaseReference reference = db.getReference("Users").child(uid);

		// Read data from the database
		reference.addListenerForSingleValueEvent(new ValueEventListener() {
			@SuppressLint("SetTextI18n")
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				if (dataSnapshot.exists()) {
					Users user = dataSnapshot.getValue(Users.class);
					if (user != null) {
						profileName.setText(user.getFullName());
						profileDOB.setText(user.getDateOfBirth());
						profileGender.setText(user.getGender());

						// Check for null or empty values before setting
						if (user.getContact() != null && !user.getContact().isEmpty()) {
							profileCredentialValue.setText(user.getContact().substring(3));
							profileCredential.setText("Contact");
						} else if (user.getMail() != null && !user.getMail().isEmpty()) {
							profileCredentialValue.setText(user.getMail());
							profileCredential.setText("GMail");
						}

						// Load the profile picture using Picasso or any other image loading library
						if (!Objects.equals(user.getPhotoUrl(), "null")) {
							Picasso.get().load(user.getPhotoUrl()).centerInside().fit().into(profilePicture);
						}
					}
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
				Toast.makeText(requireContext(), "Database error!", Toast.LENGTH_SHORT).show();
			}
		});

		binding.signOutButton.setOnClickListener(v -> {
			FirebaseAuth.getInstance().signOut();
			Intent intent = new Intent(requireContext(), MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
			requireActivity().finish();
		});

		return view;
	}

	private void setupAnimations() {
		Animation fadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in);
		Animation bottomDown = AnimationUtils.loadAnimation(requireContext(), R.anim.bottom_down);

		binding.topLinearLayout2.setAnimation(bottomDown);
		binding.layoutHeader.setAnimation(bottomDown);

		Handler handler = new Handler();
		handler.postDelayed(() -> {
			binding.cardView1.setAnimation(fadeIn);
			binding.cardView2.setAnimation(fadeIn);
			binding.signOutButton.setAnimation(fadeIn);
		}, 1000);
	}
}