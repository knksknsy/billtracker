package de.hdm.project.billtracker.helpers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdm.project.billtracker.models.Bill;

public class FirebaseDatabaseHelper {

    private FirebaseAuth auth;
    private DatabaseReference dbCategories;
    private DatabaseReference dbBills;
    private DatabaseReference dbImages;

    public FirebaseDatabaseHelper() {
        auth = FirebaseAuth.getInstance();
        dbCategories = FirebaseDatabase.getInstance().getReference("categories");
        dbBills = FirebaseDatabase.getInstance().getReference("bills");
        dbImages = FirebaseDatabase.getInstance().getReference("images");
    }

    public boolean writeBill(Bill bill) {
        String userUID = auth.getCurrentUser().getUid();
        if (userUID != null) {
            dbCategories.child(userUID).child(bill.getCategory()).setValue(bill.getCategory());

            bill.setId(dbBills.push().getKey());
            bill.setImageId(dbImages.push().getKey());

            dbImages.child(userUID).child(bill.getImageId()).setValue(bill.getImageData());

            bill.setImageData(null);

            dbBills.child(userUID).child(bill.getCategory()).child(bill.getId()).setValue(bill);


            return true;
        }
        return false;
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public void setAuth(FirebaseAuth auth) {
        this.auth = auth;
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
