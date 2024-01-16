package com.sip.busify;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.sip.busify.databinding.FragmentPassengerBinding;

public class PassengerFragment extends Fragment {

	private FragmentPassengerBinding binding;
	private AutoCompleteTextView fromTextView, toTextView;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = FragmentPassengerBinding.inflate(inflater, container, false);
		View view = binding.getRoot();

		fromTextView = binding.fromLocationSelector;
		toTextView = binding.toLocationSelector;
		AppCompatButton bookTicket = binding.bookTicketButton;

		setupAnimations();

		String[] locationSuggestions = {
				"Suchitra", "CMR College", "Medchal", "Jubilee Bus Station", "Kompally",
				"Koti", "Secunderabad", "Patancheruvu", "LB Nagar", "Hitech City", "Dulapally",
				"Gandimaisamma", "Maisammaguda", "Bowenpally", "Miyapur", "Pragathi Nagar",
				"Kukatpally", "Nizampet", "Bala Nagar", "SR Nagar", "Uppal", "BHEL", "Lingampally",
				"KPHB", "Dilsukhnagar", "Hayathnagar", "Ibrahimpatnam", "Himayat Nagar",
				"Langar House", "Kachiguda", "Gachibowli", "Jeedimetla", "Erragadda", "Ameerpet",
				"Sanath Nagar", "Punjagutta", "Khairatabad", "Lakdikapul", "Nampally", "Gandhi Bhavan",
				"Gajularamaram", "Chintal", "IDPL Colony", "Moosapet", "Yousufguda"
		};
		ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, locationSuggestions);
		fromTextView.setAdapter(adapter);
		toTextView.setAdapter(adapter);

		bookTicket.setOnClickListener(v -> {
			if (fromTextView.getText().toString().trim().isEmpty()) {
				Toast.makeText(requireContext(), "Please enter beginning location first!", Toast.LENGTH_SHORT).show();
			} else if (toTextView.getText().toString().trim().isEmpty()) {
				Toast.makeText(requireContext(), "Please enter destination also!", Toast.LENGTH_SHORT).show();
			} else {
				String startingLocation = fromTextView.getText().toString().trim();
				String destination = toTextView.getText().toString().trim();

				if (startingLocation.equals(destination))
					Toast.makeText(requireContext(), "Please enter valid locations!", Toast.LENGTH_SHORT).show();
				else {
					// Create an Intent to launch the GetTicketActivity
					Intent intent = new Intent(requireContext(), PaymentActivity.class);
					// Pass data to the intent
					intent.putExtra("startingLocation", startingLocation);
					intent.putExtra("destination", destination);
					// Start the GetTicketActivity
					startActivity(intent);
				}
			}
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
			binding.bookTicketButton.setAnimation(fadeIn);
		}, 1000);
	}
}
