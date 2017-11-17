package de.hdm.project.billtracker.helpers;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdm.project.billtracker.models.Bill;

public class FirebaseDatabaseHelper {

    private FirebaseAuth auth;
    private String userUID;

    private DatabaseReference dbUsers;
    private DatabaseReference dbCategories;
    private DatabaseReference dbBills;
    private DatabaseReference dbImages;

    public FirebaseDatabaseHelper() {
        auth = FirebaseAuth.getInstance();
        userUID = auth.getCurrentUser().getUid();
        dbUsers = FirebaseDatabase.getInstance().getReference("users");
        dbCategories = FirebaseDatabase.getInstance().getReference("categories");
        dbBills = FirebaseDatabase.getInstance().getReference("bills");
        dbImages = FirebaseDatabase.getInstance().getReference("images");
    }

    public void writeBill(final Bill bill) {
        if (userUID != null) {
            dbCategories.child(userUID).child(bill.getCategory()).setValue(bill.getCategory());

            bill.setId(dbBills.push().getKey());
            bill.setImageId(dbImages.push().getKey());

            dbImages.child(userUID).child(bill.getImageId()).setValue(bill.getImageData()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    bill.setImageData(null);

                    dbBills.child(userUID).child(bill.getCategory()).child(bill.getId()).setValue(bill);
                }
            });
        }
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public void setAuth(FirebaseAuth auth) {
        this.auth = auth;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public DatabaseReference getDbUsers() {
        return dbUsers;
    }

    public void setDbUsers(DatabaseReference dbUsers) {
        this.dbUsers = dbUsers;
    }

    public DatabaseReference getDbCategories() {
        return dbCategories;
    }

    public void setDbCategories(DatabaseReference dbCategories) {
        this.dbCategories = dbCategories;
    }

    public DatabaseReference getDbBills() {
        return dbBills;
    }

    public void setDbBills(DatabaseReference dbBills) {
        this.dbBills = dbBills;
    }

    public DatabaseReference getDbImages() {
        return dbImages;
    }

    public void setDbImages(DatabaseReference dbImages) {
        this.dbImages = dbImages;
    }
}
