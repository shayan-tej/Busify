package com.sip.busify;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.sip.busify.databinding.FragmentConductorBinding;

public class ConductorFragment extends Fragment {
	private FragmentConductorBinding binding;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {

		binding = FragmentConductorBinding.inflate(inflater, container, false);
		View view = binding.getRoot();

		setupAnimations();

		CardView scanQrBtn = binding.cardView3;
		scanQrBtn.setOnClickListener(v -> openCamera());

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
			binding.cardView3.setAnimation(fadeIn);
		}, 1000);
	}

	private void openCamera() {
		IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
		integrator.setPrompt("Scan a QR code");
		integrator.setBeepEnabled(true);
		integrator.setOrientationLocked(true);
		integrator.initiateScan();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
		if (result != null) {
			if (result.getContents() != null) {
				// Handle the scanned result
				String scannedData = result.getContents();
				String[] scannedDetails = scannedData.split(",");

				try {
					String userCollectionName = scannedDetails[0];
					String userDocumentName = scannedDetails[1];

					FirebaseFirestore db = FirebaseFirestore.getInstance();
					DocumentReference documentRef = db.collection(userCollectionName).document(userDocumentName);

					documentRef.get()
							.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
								@Override
								public void onComplete(@NonNull Task<DocumentSnapshot> task) {
									if (task.isSuccessful()) {
										DocumentSnapshot document = task.getResult();
										if (document.exists()) {
											// Document found, retrieve data
											String a = document.getString("BUS NUMBER");
											String b = document.getString("DATE & TIME");
											String c = document.getString("FAIR");
											String d = document.getString("FROM");
											String e = document.getString("TICKET NUMBER");
											String f = document.getString("TO");
											String g = document.getString("VALID TILL");
											Boolean h = document.getBoolean("didTravel");
											Boolean i = document.getBoolean("isValid");
											Intent intent = new Intent(requireContext(), ConductorTicketActivity.class);
											intent.putExtra("bN", a);
											intent.putExtra("dAT", b);
											intent.putExtra("fa", c);
											intent.putExtra("fr", d);
											intent.putExtra("tN", e);
											intent.putExtra("to", f);
											intent.putExtra("vT", g);
											intent.putExtra("dT", h);
											intent.putExtra("iV", i);
											intent.putExtra("UID", userCollectionName);
											startActivity(intent);
										} else {
											// Document does not exist
											Toast.makeText(requireContext(), "Ticket doesn't exist!", Toast.LENGTH_SHORT).show();
										}
									} else {
										// Error getting document
										Toast.makeText(requireContext(), "Error! Invalid QR code", Toast.LENGTH_SHORT).show();
										Log.e(TAG, "Error! Invalid QR code", task.getException());
									}
								}
							});
				} catch (ArrayIndexOutOfBoundsException e) {
					Toast.makeText(requireContext(), "Error! Invalid QR code", Toast.LENGTH_SHORT).show();
					Log.e(TAG, "Error finding ticket!", e);
				}
			} else {
				Toast.makeText(requireContext(), "No QR found!", Toast.LENGTH_SHORT).show();
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
}
