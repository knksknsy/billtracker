package de.hdm.project.billtracker.helpers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdm.project.billtracker.models.Scan;

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

    public boolean writeBill(Scan scan) {
        String userUID = auth.getCurrentUser().getUid();
        if (userUID != null) {
            dbCategories.child(userUID).child(scan.getCategory()).setValue(scan.getCategory());

            scan.setId(dbBills.push().getKey());
            scan.setImageId(dbImages.push().getKey());

            dbImages.child(userUID).child(scan.getImageId()).setValue(scan.getImageData());

            scan.setImageData(null);

            dbBills.child(userUID).child(scan.getCategory()).child(scan.getId()).setValue(scan);


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
