package de.hdm.project.billtracker.activities;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

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
    private TextView downloadURL;
    private EditText titleText;
    private EditText categoryText;
    private EditText sumText;
    private Button saveButton;
    private Button deleteButton;
    private Button downloadButton;


    // TODO: expandable ImageView -> new Activity for image with zooming functionality

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent i = getIntent();
        bill = (Bill) i.getParcelableExtra("bill");

        fDatabase = new FirebaseDatabaseHelper(BillDetailsActivity.this);

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
                finish();
                // TODO: close activity on result -> reinitialize billsfragment view for updated values
            }
        });

        deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        downloadButton = (Button) findViewById(R.id.downloadButton);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageInBrowser();
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
            fDatabase.updateCategory(bill, oldCategory);
        } else {
            fDatabase.updateBill(bill);
        }
    }

    private void openDialog() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete bill")
                .setMessage("Are you sure you want to delete this bill?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteBill();
                        finish();
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

    private void deleteBill() {
        fDatabase.deleteBill(bill);
    }

    private void openImageInBrowser() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(bill.getDownloadUrl()));
        startActivity(browserIntent);
    }

}
