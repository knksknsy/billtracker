package de.hdm.project.billtracker.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdm.project.billtracker.R;
import de.hdm.project.billtracker.adapters.BillListAdapter;
import de.hdm.project.billtracker.helpers.FirebaseDatabaseHelper;
import de.hdm.project.billtracker.models.Bill;

public class CategoriesFragment extends Fragment {

    private FirebaseDatabaseHelper fDatabase;
    private ListView listView;
    private String userUID;

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

        fDatabase = new FirebaseDatabaseHelper();
        userUID = fDatabase.getAuth().getCurrentUser().getUid();

        listView = view.findViewById(R.id.categoryList);

        initCategoriesListView();

        return view;
    }

    private void initCategoriesListView() {
        fDatabase.getDbCategories().child(userUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> c = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String category = snapshot.getValue(String.class);
                    c.add(category);
                }
                final String[] categories = c.toArray(new String[0]);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getBaseContext(), android.R.layout.simple_list_item_1, android.R.id.text1, categories);
                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        initBillsListView(categories[i]);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initBillsListView(String category) {
        fDatabase.getDbBills().child(userUID).child(category).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Bill> bills = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Bill bill = snapshot.getValue(Bill.class);
                    bills.add(bill);
                }
                BillListAdapter adapter = new BillListAdapter(getActivity().getBaseContext(), R.layout.bill_list_row, bills);
                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        // TODO open ScanDetailsActivity
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
