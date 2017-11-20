package de.hdm.project.billtracker.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import de.hdm.project.billtracker.R;
import de.hdm.project.billtracker.helpers.FirebaseDatabaseHelper;
import de.hdm.project.billtracker.helpers.ImageHelper;
import de.hdm.project.billtracker.models.Bill;

public class BillListAdapter extends ArrayAdapter<Bill> {

    private List<Bill> bills;
    private Activity activity;

    public BillListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public BillListAdapter(Context context, int resource, List<Bill> bills) {
        super(context, resource, bills);
        this.bills = bills;
        this.activity = (Activity) context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (getCount() > 0) {
            View v = convertView;

            if (v == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                v = vi.inflate(R.layout.bill_list_row, null);
            }

            final Bill bill = getItem(position);

            if (bill != null) {
                Button deleteButton = (Button) v.findViewById(R.id.deleteButton);
                ImageView thumbnail = (ImageView) v.findViewById(R.id.thumbnail);
                TextView titleText = (TextView) v.findViewById(R.id.titleText);
                TextView dateText = (TextView) v.findViewById(R.id.dateText);
                TextView sumText = (TextView) v.findViewById(R.id.sumText);

                if (thumbnail != null) {
                    File thumbnaildFile = new File(bill.getThumbnailPath());

                    if (thumbnaildFile.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(thumbnaildFile.getAbsolutePath());
                        thumbnail.setImageBitmap(bitmap);
                    } else {
                        // create thumbnail
                        ImageHelper imageHelper = new ImageHelper(this.activity, bill);
                        if (!new File(bill.getThumbnailPath()).exists()) {
                            imageHelper.saveThumbnail();
                        }
                    }
                }

                if (titleText != null) {
                    titleText.setText(bill.getTitle());
                }

                if (dateText != null) {
                    dateText.setText(bill.printDate());
                }

                if (sumText != null) {
                    // TODO save user currency
                    sumText.setText("Sum: " + String.valueOf(bill.getSum()) + " €");
                }

                if (deleteButton != null) {
                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openDialog(position, bill);
                        }
                    });
                }
            }
            return v;
        }
        return null;
    }

    public int getCount() {
        return bills.size();
    }

    private void openDialog(final int position, final Bill bill) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this.activity);
        builder.setTitle("Delete bill")
                .setMessage("Are you sure you want to delete this bill?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteBill(position, bill);
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

    private void deleteBill(int position, Bill bill) {
        bills.remove(position);
        FirebaseDatabaseHelper fDatabase = new FirebaseDatabaseHelper(this.activity);
        fDatabase.deleteBill(bill);
    }

}
