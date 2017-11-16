package de.hdm.project.billtracker.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdm.project.billtracker.R;
import de.hdm.project.billtracker.activities.BillDetailsActivity;
import de.hdm.project.billtracker.adapters.BillListAdapter;
import de.hdm.project.billtracker.helpers.FirebaseDatabaseHelper;
import de.hdm.project.billtracker.models.Bill;

public class BillsFragment extends Fragment {

    private FirebaseDatabaseHelper fDatabase;
    private ListView listView;
    private ArrayList<Bill> bills;

    public static BillsFragment newInstance() {
        return new BillsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bills, container, false);

        fDatabase = new FirebaseDatabaseHelper();

        listView = view.findViewById(R.id.scansList);

        bills = new ArrayList<>();

        getFirebaseData();

        return view;
    }

    private void getFirebaseData() {
        fDatabase.getDbCategories().child(fDatabase.getUserUID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot categoryDataSnapshot) {
                for (DataSnapshot categorySnapshot : categoryDataSnapshot.getChildren()) {
                    String category = categorySnapshot.getValue(String.class);
                    initBillsByCategory(category);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initBillsByCategory(String category) {
        fDatabase.getDbBills().child(fDatabase.getUserUID()).child(category).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot scanDataSnapshot) {
                for (DataSnapshot scanSnapshot : scanDataSnapshot.getChildren()) {
                    Bill bill = scanSnapshot.getValue(Bill.class);
                    bills.add(bill);
                }
                BillListAdapter adapter = new BillListAdapter(getActivity(), R.layout.bill_list_row, bills);
                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        openBillDetailsActivity(bills.get(i));
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
