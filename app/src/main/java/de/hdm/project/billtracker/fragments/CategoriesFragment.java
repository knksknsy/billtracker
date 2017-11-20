package de.hdm.project.billtracker.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdm.project.billtracker.R;
import de.hdm.project.billtracker.activities.BillDetailsActivity;
import de.hdm.project.billtracker.adapters.BillListAdapter;
import de.hdm.project.billtracker.adapters.CategoryListAdapter;
import de.hdm.project.billtracker.helpers.FirebaseDatabaseHelper;
import de.hdm.project.billtracker.models.Bill;

public class CategoriesFragment extends Fragment {

    private FirebaseDatabaseHelper fDatabase;
    private ListView listView;
    private ArrayList<Bill> bills;

    public static CategoriesFragment newInstance() {
        return new CategoriesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);

        fDatabase = new FirebaseDatabaseHelper(getActivity());

        listView = view.findViewById(R.id.categoryList);

        initCategoriesListView();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    // Handle back button press when listView is initialized with bills
                    if (bills != null) {
                        initCategoriesListView();
                        bills = null;
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void initCategoriesListView() {
        fDatabase.getDbCategories().child(fDatabase.getUserUID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayList<String> categories = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String category = snapshot.getValue(String.class);
                    categories.add(category);
                }

                if (getActivity() != null) {
                    CategoryListAdapter adapter = new CategoryListAdapter(getActivity(), R.layout.category_list_row, categories);
                    listView.setAdapter(adapter);
                }

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        initBillsListView(categories.get(i));
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initBillsListView(final String category) {
        fDatabase.getDbBills().child(fDatabase.getUserUID()).child(category).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bills = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Bill bill = snapshot.getValue(Bill.class);
                    bills.add(bill);
                }

                if (bills.size() <= 0) {
                    initCategoriesListView();
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), "Category " + category + " is empty.", Toast.LENGTH_SHORT).show();
                    }
                }

                if (getActivity() != null) {
                    BillListAdapter adapter = new BillListAdapter(getActivity(), R.layout.bill_list_row, bills);
                    listView.setAdapter(adapter);
                }

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if (view != null) {
                            openBillDetailsActivity(bills.get(i));
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void openBillDetailsActivity(Bill bill) {
        Intent intent = new Intent(getActivity(), BillDetailsActivity.class);
        intent.putExtra("bill", (Parcelable) bill);
        startActivity(intent);
    }

}
