package de.hdm.project.billtracker.fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.hdm.project.billtracker.R;
import de.hdm.project.billtracker.helpers.FirebaseDatabaseHelper;

public class ContentFragment extends Fragment {

    private FirebaseDatabaseHelper fDatabase;

    private TabLayout tabs;
    private CategoriesFragment categories;
    private BillsFragment bills;

    public static ContentFragment newInstance() {
        return new ContentFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);

        tabs = view.findViewById(R.id.tablayout);
        bindWidgetsWithEvent();
        setupTabLayout();

        fDatabase = new FirebaseDatabaseHelper(getActivity());
        // check for missing local images and synchronize if needed
        fDatabase.synchronizeImages();

        return view;
    }

    /**
     * Initialize TabSelectedListener
     */
    private void bindWidgetsWithEvent() {
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setCurrentTabFragment(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    /**
     * Handle setting current TabFragment
     *
     * @param tabPosition
     */
    private void setCurrentTabFragment(int tabPosition) {
        switch (tabPosition) {
            case 0:
                replaceFragment(categories);
                break;
            case 1:
                replaceFragment(bills);
                break;
        }
    }

    /**
     * Open Fragment
     *
     * @param fragment
     */
    private void replaceFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_container, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    private void setupTabLayout() {
        categories = new CategoriesFragment();
        bills = new BillsFragment();

        tabs.addTab(tabs.newTab().setText("Categories"), true);
        tabs.addTab(tabs.newTab().setText("All Bills"));
    }

}
