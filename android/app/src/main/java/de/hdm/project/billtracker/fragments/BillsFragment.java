package de.hdm.project.billtracker.fragments;

import android.app.Activity;
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

    public static final int REQUEST_CODE = 1;

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

        fDatabase = new FirebaseDatabaseHelper(getActivity());

        bills = new ArrayList<>();

        fetchBills();

        listView = view.findViewById(R.id.scansList);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                openBillDetailsActivity(bills.get(i));
            }
        });

        return view;
    }

    /**
     * Retrieve all bills regardless of it's category
     */
    private void fetchBills() {
        fDatabase.getDbCategories().child(fDatabase.getUserUid()).addValueEventListener(new ValueEventListener() {
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

    /**
     * Retrieve bills by category
     *
     * @param category
     */
    private void initBillsByCategory(String category) {
        fDatabase.getDbBills().child(fDatabase.getUserUid()).child(category).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot scanDataSnapshot) {
                for (DataSnapshot billSnapshot : scanDataSnapshot.getChildren()) {
                    Bill bill = billSnapshot.getValue(Bill.class);
                    bills.add(bill);
                }
                if (getActivity() != null) {
                    BillListAdapter adapter = new BillListAdapter(getActivity(), R.layout.bill_list_row, bills);
                    listView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void openBillDetailsActivity(Bill bill) {
        Intent intent = new Intent(getActivity(), BillDetailsActivity.class);
        intent.putExtra("bill", (Parcelable) bill);
        startActivityForResult(intent, REQUEST_CODE);
    }

    /**
     * Retrieve updated bills
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // update listView
                bills.clear();
                fetchBills();
            }
        }
    }


}
