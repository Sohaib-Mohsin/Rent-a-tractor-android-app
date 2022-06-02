package com.tractor.rentatractorapp.User.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.tractor.rentatractorapp.User.Fragments.Rent_Bucket_Fragment;
import com.tractor.rentatractorapp.User.Fragments.HomeFragment;
import com.tractor.rentatractorapp.User.Fragments.ProfileFragment;
import com.tractor.rentatractorapp.User.Fragments.StoreFragment;
import com.tractor.rentatractorapp.R;
import com.tractor.rentatractorapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    int FragmentNo = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportFragmentManager().beginTransaction().replace(R.id.linear, new HomeFragment()).commit();
        binding.bottomNav.setSelectedItemId(R.id.nav_home);

        binding.bottomNav.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                FragmentNo = 1;
                getSupportFragmentManager().beginTransaction().replace(R.id.linear, new HomeFragment()).commit();
            }
            if (item.getItemId() == R.id.nav_store) {
                FragmentNo = 2;
                getSupportFragmentManager().beginTransaction().replace(R.id.linear, new StoreFragment()).commit();
            }
            if (item.getItemId() == R.id.nav_cart) {
                FragmentNo = 3;
                getSupportFragmentManager().beginTransaction().replace(R.id.linear, new Rent_Bucket_Fragment()).commit();
            }
            if (item.getItemId() == R.id.nav_profile) {
                FragmentNo = 4;
                getSupportFragmentManager().beginTransaction().replace(R.id.linear, new ProfileFragment()).commit();
            }
            return true;
        });

    }

    @Override
    public void onBackPressed() {
        if (FragmentNo != 1) {
            getSupportFragmentManager().beginTransaction().replace(R.id.linear, new HomeFragment()).commit();
            binding.bottomNav.setSelectedItemId(R.id.nav_home);
        } else {
            finish();
            super.onBackPressed();
        }
    }
}