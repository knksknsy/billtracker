package de.hdm.project.billtracker.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.DialogFragment;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.hdm.project.billtracker.R;
import de.hdm.project.billtracker.helpers.FirebaseDatabaseHelper;

public class CategoryDialogFragment extends DialogFragment {

    private AutoCompleteTextView autocompleteCategory;
    private EditText titleText;
    private List<String> categories;
    private TextView errorTextView;

    private FirebaseDatabaseHelper fDatabase;

    public static CategoryDialogFragment newInstance(int num) {
        CategoryDialogFragment dialogFragment = new CategoryDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("num", num);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        fDatabase = new FirebaseDatabaseHelper();

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.fragment_category_dialog, null);

        // Fetch categories from firebase for auto completion
        fDatabase.getDbCategories().child(fDatabase.getUserUID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                categories = new ArrayList<>();
                Iterator<DataSnapshot> iter = dataSnapshot.getChildren().iterator();
                while (iter.hasNext()) {
                    categories.add(iter.next().getValue().toString());
                }
                autocompleteCategory = view.findViewById(R.id.autocompleteCategory);

                if (getActivity() != null && categories.size() > 0) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.category_autocomplete, R.id.autoTextView, categories);
                    autocompleteCategory.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        errorTextView = view.findViewById(R.id.errorTextView);

        titleText = view.findViewById(R.id.titleText);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        // Overridden in onStart()
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Send the negative button event back to the host activity
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, getActivity().getIntent());
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog d = (AlertDialog) getDialog();
        if (d != null) {
            Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (autocompleteCategory.getText().toString().isEmpty()) {
                        errorTextView.setText("Please enter a valid category.");
                        return;
                    } else {
                        // Send the positive button event back to the host activity
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getIntent());
                        dismiss();
                    }
                }
            });
        }
    }

    private Intent getIntent() {
        Intent intent = getActivity().getIntent();
        intent.putExtra("category", autocompleteCategory.getText().toString().toUpperCase());
        intent.putExtra("title", titleText.getText().toString());
        return intent;
    }

}
