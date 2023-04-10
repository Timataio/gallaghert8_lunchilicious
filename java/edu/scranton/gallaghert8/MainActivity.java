package edu.scranton.gallaghert8;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.content.res.Configuration;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    MenuViewModel menuViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        menuViewModel = new ViewModelProvider(this).get(MenuViewModel.class);
        launchFragment(savedInstanceState);

    }
    private void launchFragment(Bundle savedInstanceState) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            transaction.replace(R.id.fragment, MenuFragment.newInstance());
        }
        else {
            getSupportFragmentManager().popBackStack("All Orders", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            getSupportFragmentManager().popBackStack("Review", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            getSupportFragmentManager().popBackStack("Single Order", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            transaction.replace(R.id.land_left, MenuFragment.newInstance());
            transaction.replace(R.id.land_right, SingleOrderFragment.newInstance());
        }
        transaction.commit();
    }

    public void startSingleOrderFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack("Single Order");
        transaction.replace(R.id.fragment, SingleOrderFragment.newInstance());
        transaction.commit();
    }

    public void startReviewFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            transaction.addToBackStack("Review");
            transaction.replace(R.id.fragment, ReviewFragment.newInstance());
        }
        else {
            transaction.replace(R.id.land_left, ReviewFragment.newInstance());
        }
        transaction.commit();
    }

    public void startAllOrdersFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            transaction.addToBackStack("All Orders");
            transaction.replace(R.id.fragment, AllOrdersFragment.newInstance());
        }
        else {
            transaction.replace(R.id.land_right, AllOrdersFragment.newInstance());
        }
        transaction.commit();
    }
}