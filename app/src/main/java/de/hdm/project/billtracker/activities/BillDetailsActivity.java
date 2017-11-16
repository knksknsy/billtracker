package de.hdm.project.billtracker.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;

import de.hdm.project.billtracker.R;
import de.hdm.project.billtracker.helpers.FirebaseDatabaseHelper;
import de.hdm.project.billtracker.helpers.ImageHelper;
import de.hdm.project.billtracker.models.Bill;

public class BillDetailsActivity extends AppCompatActivity {

    private Bill bill;
    private ImageHelper imageHelper;
    private FirebaseDatabaseHelper fDatabase;

    private ImageView imageView;
    private TextView dateText;
    private EditText titleText;
    private EditText categoryText;
    private EditText sumText;
    private Button saveButton;
    private Button deleteButton;

    // TODO: expandable ImageView -> new Activity for image with zooming functionality

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent i = getIntent();
        bill = (Bill) i.getParcelableExtra("bill");

        fDatabase = new FirebaseDatabaseHelper();

        imageHelper = new ImageHelper(this, bill);
        imageHelper.setImagePath(bill.getImagePath());
        imageHelper.setThumbnailPath(bill.getThumbnailPath());

        imageView = (ImageView) findViewById(R.id.billImageView);
        File imgFile = new File(bill.getImagePath());
        if (imgFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(bitmap);
        }

        titleText = (EditText) findViewById(R.id.title);
        titleText.setText(bill.getTitle());

        categoryText = (EditText) findViewById(R.id.category);
        categoryText.setText(bill.getCategory());

        dateText = (TextView) findViewById(R.id.date);
        dateText.setText(bill.printDate());

        sumText = (EditText) findViewById(R.id.sum);
        sumText.setText(String.valueOf(bill.getSum()));

        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBill();
            }
        });

        deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteBill();
            }
        });
    }

    private void updateBill() {
        String newTitle = titleText.getText().toString();
        String oldCategory = bill.getCategory();
        String newCategory = categoryText.getText().toString().toUpperCase();
        String newSum = sumText.getText().toString();

        bill.setTitle(newTitle);

        if (!newCategory.isEmpty() && !newCategory.equals(bill.getCategory())) {
            bill.setCategory(newCategory);
        }

        if (!newSum.isEmpty() && Double.parseDouble(newSum) != bill.getSum()) {
            bill.setSum(Double.parseDouble(newSum));
        }

        // category has changed
        if (!newCategory.equals(oldCategory)) {
            updateCategory(oldCategory);
        } else {
            fDatabase.getDbBills().child(fDatabase.getUserUID()).child(bill.getCategory()).child(bill.getId()).setValue(bill);
        }
    }

    private void updateCategory(final String oldCategory) {
        // move image on device into new category
        imageHelper.moveImageOnDevice(bill.getCategory());
        imageHelper.deleteImageOnDevice(bill.getThumbnailPath());

        bill.setImagePath(imageHelper.getImagePath());
        bill.setThumbnailPath(imageHelper.getThumbnailPath());

        // check if new category already exists
        fDatabase.getDbCategories().child(fDatabase.getUserUID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> categories = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String category = snapshot.getValue(String.class);
                    categories.add(category);
                }
                boolean exists = categories.contains(bill.getCategory());

                if (!exists) {
                    // create new category in firebase
                    fDatabase.getDbCategories().child(fDatabase.getUserUID()).child(bill.getCategory()).setValue(bill.getCategory());
                    // remove bill from old category
                    fDatabase.getDbBills().child(fDatabase.getUserUID()).child(oldCategory).child(bill.getId()).removeValue();
                    // create new bill
                    fDatabase.getDbBills().child(fDatabase.getUserUID()).child(bill.getCategory()).child(bill.getId()).setValue(bill);
                } else {
                    // remove bill from old category
                    fDatabase.getDbBills().child(fDatabase.getUserUID()).child(oldCategory).child(bill.getId()).removeValue();
                    // create new bill
                    fDatabase.getDbBills().child(fDatabase.getUserUID()).child(bill.getCategory()).child(bill.getId()).setValue(bill);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void deleteBill() {
        fDatabase.getDbBills().child(fDatabase.getUserUID()).child(bill.getCategory()).child(bill.getId()).removeValue();
        imageHelper.deleteImageOnDevice(bill.getImagePath());
        imageHelper.deleteImageOnDevice(bill.getThumbnailPath());
        // TODO: delete image from images database
    }

}
