package de.hdm.project.billtracker.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;

import de.hdm.project.billtracker.R;
import de.hdm.project.billtracker.helpers.ImageHelper;
import de.hdm.project.billtracker.models.Bill;

public class BillDetailsActivity extends AppCompatActivity {

    private Bill bill;
    private ImageHelper imageHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent i = getIntent();
        bill = (Bill) i.getParcelableExtra("bill");

        imageHelper = new ImageHelper(this, bill);

        ImageView imageView = (ImageView) findViewById(R.id.billImageView);
        EditText titleText = (EditText) findViewById(R.id.title);
        EditText dateText = (EditText) findViewById(R.id.date);
        EditText sum = (EditText) findViewById(R.id.sum);
        Button saveButton = (Button) findViewById(R.id.saveButton);
        Button deleteButton = (Button) findViewById(R.id.deleteButton);

        File imgFile = new File(bill.getImagePath());

        if (imgFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(bitmap);
        }
        titleText.setText(bill.getTitle());
        dateText.setText(bill.printDate());
        sum.setText(String.valueOf(bill.getSum()));
    }

}
