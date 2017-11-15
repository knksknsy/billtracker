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

import java.io.File;

import de.hdm.project.billtracker.R;
import de.hdm.project.billtracker.helpers.ImageHelper;
import de.hdm.project.billtracker.models.Bill;

public class BillDetailsActivity extends AppCompatActivity {

    private Bill bill;
    private ImageHelper imageHelper;

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

        imageHelper = new ImageHelper(this, bill);

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
        bill.setTitle(titleText.getText().toString());

        if (!categoryText.getText().toString().isEmpty()) {
            bill.setCategory(categoryText.getText().toString());
        }

        if (!sumText.getText().toString().isEmpty()) {
            bill.setSum(Double.parseDouble(sumText.getText().toString()));
        }

        // TODO: save -> update bill on firebase
        // TODO: delete -> delete bill on firebase and local image
        // TODO: change category -> delete old bill on firebase + move image file locally + upload new bill on firebase
    }

    private void deleteBill() {

    }

}
