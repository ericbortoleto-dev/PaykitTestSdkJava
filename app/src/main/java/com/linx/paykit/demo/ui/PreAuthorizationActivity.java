package com.linx.paykit.demo.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.linx.paykit.demo.R;

public class PreAuthorizationActivity extends AppCompatActivity {

    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_authorization);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        PreAuthorizationPagerAdapter adapter = new PreAuthorizationPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Nova");
            } else {
                tab.setText("Gerenciar");
            }
        }).attach();
    }

    public void switchToManageTab() {
        if (viewPager != null) {
            viewPager.setCurrentItem(1, true);
        }
    }

    private static class PreAuthorizationPagerAdapter extends FragmentStateAdapter {
        public PreAuthorizationPagerAdapter(@NonNull AppCompatActivity activity) {
            super(activity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                return new NewPreAuthorizationFragment();
            } else {
                return new ManagePreAuthorizationFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}
