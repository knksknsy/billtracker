package de.hdm.project.billtracker.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import de.hdm.project.billtracker.R;
import de.hdm.project.billtracker.helpers.FirebaseDatabaseHelper;

public class CategoryListAdapter extends ArrayAdapter<String> {

    private List<String> categories;
    private Activity activity;

    public CategoryListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public CategoryListAdapter(Context context, int resource, List<String> categories) {
        super(context, resource, categories);
        this.categories = categories;
        this.activity = (Activity) context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (getCount() > 0) {
            View v = convertView;

            if (v == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                v = vi.inflate(R.layout.category_list_row, null);
            }

            final String category = getItem(position);

            if (category != null) {
                TextView categoryText = (TextView) v.findViewById(R.id.category);
                Button deleteButton = (Button) v.findViewById(R.id.deleteButton);

                if (categoryText != null) {
                    categoryText.setText(category);
                }

                if (deleteButton != null) {
                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openDialog(category);
                        }
                    });
                }

            }
            return v;
        }
        return null;
    }

    private void openDialog(final String category) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this.activity);
        builder.setTitle("Delete category")
                .setMessage("Are you sure you want to delete this category with all it's images?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteCategory(category);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteCategory(String category) {
        FirebaseDatabaseHelper fDatabase = new FirebaseDatabaseHelper(this.activity);
        fDatabase.deleteCategory(category);
    }

    public int getCount() {
        return categories.size();
    }

}
