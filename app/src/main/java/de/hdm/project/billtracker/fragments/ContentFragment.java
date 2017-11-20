package de.hdm.project.billtracker.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.hdm.project.billtracker.R;
import de.hdm.project.billtracker.helpers.FirebaseDatabaseHelper;

public class ContentFragment extends Fragment {

    private CategoriesFragment categories;
    private BillsFragment bills;
    private TabLayout tabs;
    private FirebaseDatabaseHelper fDatabase;

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

        tabs = (TabLayout) view.findViewById(R.id.tablayout);
        bindWidgetsWithEvent();
        setupTabLayout();

        fDatabase = new FirebaseDatabaseHelper(getActivity());
        fDatabase.synchronizeImages();

        return view;
    }

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

    private void setupTabLayout() {
        categories = new CategoriesFragment();
        bills = new BillsFragment();
        tabs.addTab(tabs.newTab().setText("Categories"), true);
        tabs.addTab(tabs.newTab().setText("All Bills"));
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_container, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

}
