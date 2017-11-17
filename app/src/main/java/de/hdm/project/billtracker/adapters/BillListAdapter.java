package de.hdm.project.billtracker.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import de.hdm.project.billtracker.R;
import de.hdm.project.billtracker.models.Bill;

public class BillListAdapter extends ArrayAdapter<Bill> {

    private List<Bill> bills;

    public BillListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public BillListAdapter(Context context, int resource, List<Bill> bills) {
        super(context, resource, bills);
        this.bills = bills;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (getCount() > 0) {
            View v = convertView;

            if (v == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                v = vi.inflate(R.layout.bill_list_row, null);
            }

            Bill s = getItem(position);

            if (s != null) {
                ImageView thumbnail = (ImageView) v.findViewById(R.id.thumbnail);
                TextView titleText = (TextView) v.findViewById(R.id.titleText);
                TextView dateText = (TextView) v.findViewById(R.id.dateText);
                TextView sumText = (TextView) v.findViewById(R.id.sumText);

                if (thumbnail != null) {
                    /*if (thumbnail.getDrawable() != null) {
                        ((BitmapDrawable) thumbnail.getDrawable()).getBitmap().recycle();
                    }*/
                    File imgFile = new File(s.getThumbnailPath());

                    if (imgFile.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        thumbnail.setImageBitmap(bitmap);
                    }
                }

                if (titleText != null) {
                    titleText.setText(s.getTitle());
                }

                if (dateText != null) {
                    dateText.setText(s.printDate());
                }

                if (sumText != null) {
                    // TODO save user currency
                    sumText.setText("Sum: " + String.valueOf(s.getSum()) + " €");
                }
            }
            return v;
        }
        return null;
    }

    public int getCount() {
        return bills.size();
    }

}
