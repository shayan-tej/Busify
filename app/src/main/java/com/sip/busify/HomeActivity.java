package com.sip.busify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.sip.busify.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {
	ActivityHomeBinding binding;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = ActivityHomeBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		replaceFragment(new PassengerFragment());
		binding.bottomNavigationView.setSelectedItemId(R.id.passenger);
		binding.bottomNavigationView.setBackground(null);

		binding.bottomNavigationView.setOnItemSelectedListener(item -> {
			if (item.getItemId() == R.id.conductor) {
				replaceFragment(new ConductorFragment());
			} else if (item.getItemId() == R.id.passenger) {
				replaceFragment(new PassengerFragment());
			} else if (item.getItemId() == R.id.account) {
				replaceFragment(new AccountFragment());
			}
			return true;
		});
	}

	private void replaceFragment(Fragment fragment) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.frame_layout, fragment);
		fragmentTransaction.commit();
	}
}