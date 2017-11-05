package de.hdm.project.billtracker;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.app.DialogFragment;
import android.app.AlertDialog;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CategoryDialogFragment extends DialogFragment {

    AutoCompleteTextView autocompleteCategory;
    LinearLayout radioGroupLayout;
    List<String> categoriesList;
    List<RadioButton> categoryRadioButtons;
    TextView errorTextView;
    SharedPreferences CATEGORY_PREF;

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

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.fragment_category_dialog, null);

        // radioGroupLayout = view.findViewById(R.id.radioGroupLayout);

        // Retrieve categories from shared preference
        Context context = getActivity().getBaseContext();
        CATEGORY_PREF = context.getSharedPreferences(getString(R.string.category_preference), Context.MODE_PRIVATE);

        categoriesList = new ArrayList(CATEGORY_PREF.getAll().values());
        // createRadioButton();

        autocompleteCategory = view.findViewById(R.id.autocompleteCategory);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getBaseContext(), R.layout.category_autocomplete, R.id.autoTextView, categoriesList);

        autocompleteCategory.setAdapter(adapter);

        errorTextView = view.findViewById(R.id.errorTextView);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {

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
                    String categoryString = autocompleteCategory.getText().toString().toUpperCase();

                    if (autocompleteCategory.getText().toString().isEmpty()) {
                        errorTextView.setText("Please enter a valid category.");
                        return;
                    } else {
                        // Save category in shared preference
                        boolean categoryExists = CATEGORY_PREF.contains(categoryString);
                        if (!categoryExists) {
                            SharedPreferences.Editor editor = CATEGORY_PREF.edit();
                            editor.putString(categoryString, categoryString);
                            editor.commit();
                        }

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
        /*for (RadioButton radio: categoryRadioButtons) {
            if (radio.isChecked()) {
                intent.putExtra("category", radio.getText().toString());
                return intent;
            }
        }*/
        intent.putExtra("category", autocompleteCategory.getText().toString().toUpperCase());
        return intent;
    }

    /*private void createRadioButton() {
        categoryRadioButtons = new ArrayList<>();
        RadioGroup rg = new RadioGroup(getActivity().getBaseContext());
        rg.setOrientation(RadioGroup.VERTICAL);
        int id = 0;
        for (String category : categoriesList) {
            id++;
            final RadioButton RADIO = new RadioButton(getActivity().getBaseContext());
            RADIO.setText(category.toString());
            RADIO.setId(id);
            RADIO.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    radioCategory = RADIO.getText().toString();
                    System.out.println(radioCategory);
                }
            });
            categoryRadioButtons.add(RADIO);
            rg.addView(RADIO);
        }
        radioGroupLayout.addView(rg);
    }*/

}
