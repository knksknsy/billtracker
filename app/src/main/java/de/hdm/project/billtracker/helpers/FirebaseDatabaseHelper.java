package de.hdm.project.billtracker.helpers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import de.hdm.project.billtracker.models.Bill;

public class FirebaseDatabaseHelper {

    private Activity activity;

    private FirebaseAuth auth;
    private String userUID;

    private ImageHelper imageHelper;

    private DatabaseReference dbUsers;
    private DatabaseReference dbCategories;
    private DatabaseReference dbBills;
    private StorageReference imageStorage;


    public FirebaseDatabaseHelper(Activity activity) {
        this.activity = activity;
        auth = FirebaseAuth.getInstance();
        userUID = auth.getCurrentUser().getUid();
        dbUsers = FirebaseDatabase.getInstance().getReference("users");
        dbCategories = FirebaseDatabase.getInstance().getReference("categories");
        dbBills = FirebaseDatabase.getInstance().getReference("bills");
        imageStorage = FirebaseStorage.getInstance().getReference();
    }

    public void writeBill(final Bill bill) {
        if (userUID != null) {
            // Save category
            dbCategories.child(userUID).child(bill.getCategory()).setValue(bill.getCategory());

            bill.setId(dbBills.push().getKey());
            bill.setImageId(UUID.randomUUID().toString());

            // Save bill information
            dbBills.child(userUID).child(bill.getCategory()).child(bill.getId()).setValue(bill);

            // Upload bill image
            uploadImage(bill);
        }
    }

    public void updateBill(final Bill bill) {
        dbBills.child(userUID).child(bill.getCategory()).child(bill.getId()).setValue(bill);
    }

    public void deleteBill(final Bill bill) {
        // delete bill
        dbBills.child(userUID).child(bill.getCategory()).child(bill.getId()).removeValue();
        // delete bill image
        imageStorage.child("user").child(userUID).child(bill.getImageId()).delete();

        imageHelper = new ImageHelper(getActivity(), bill);

        // delete image and thumbnail on device
        imageHelper.deleteImageOnDevice(bill.getImagePath());
        imageHelper.deleteImageOnDevice(bill.getThumbnailPath());
    }

    public void updateCategory(final Bill bill, final String oldCategory) {
        imageHelper = new ImageHelper(getActivity(), bill);

        // move image on device into new category
        imageHelper.moveImageOnDevice(bill.getCategory());

        bill.setImagePath(imageHelper.getImagePath());
        bill.setThumbnailPath(imageHelper.getThumbnailPath());

        // check if new category already exists
        dbCategories.child(userUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> categories = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String category = snapshot.getValue(String.class);
                    categories.add(category);
                }
                boolean categoryExists = categories.contains(bill.getCategory());

                if (!categoryExists) {
                    // create new category in firebase
                    dbCategories.child(userUID).child(bill.getCategory()).setValue(bill.getCategory());
                }
                // remove bill from old category
                dbBills.child(userUID).child(oldCategory).child(bill.getId()).removeValue();
                // create new bill
                dbBills.child(userUID).child(bill.getCategory()).child(bill.getId()).setValue(bill);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void uploadImage(final Bill bill) {
        Uri imageFile = Uri.fromFile(new File(bill.getImagePath()));

        StorageMetadata metadata = new StorageMetadata.Builder().setContentType("image/jpeg").build();

        UploadTask uploadTask = imageStorage.child("user").child(userUID).child(bill.getImageId()).putFile(imageFile, metadata);

        // show upload progress dialog
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Uploading Image", "Uploading image...", true, false);

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                progressDialog.setMessage("Upload is " + round(progress, 2) + "% done.");
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.setMessage("Upload is paused.");
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.setMessage("Upload has failed.");
                progressDialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                bill.setDownloadUrl(downloadUrl.toString());
                updateBill(bill);
                progressDialog.dismiss();
            }
        });
    }

    public void synchronizeImages() {
        dbCategories.child(userUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot categoryDataSnapshot) {
                for (DataSnapshot categorySnapshot : categoryDataSnapshot.getChildren()) {
                    String category = categorySnapshot.getValue(String.class);
                    dbBills.child(userUID).child(category).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot scanDataSnapshot) {
                            for (DataSnapshot scanSnapshot : scanDataSnapshot.getChildren()) {
                                Bill bill = scanSnapshot.getValue(Bill.class);
                                // Check if image exists on device
                                File image = new File(bill.getImagePath());
                                if (!image.exists()) {
                                    downloadImage(bill);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void downloadImage(final Bill bill) {
        StorageReference imageStorage = FirebaseStorage.getInstance().getReferenceFromUrl(bill.getDownloadUrl());
        imageHelper = new ImageHelper(getActivity(), bill);
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Synchronizing Image", "Downloading image...", true, false);

        imageHelper.createCategoryDir();

        imageStorage.getFile(imageHelper.getImageFile()).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                progressDialog.setMessage("Download is " + round(progress, 2) + "% done.");
            }
        }).addOnPausedListener(new OnPausedListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onPaused(FileDownloadTask.TaskSnapshot taskSnapshot) {
                progressDialog.setMessage("Download is paused.");
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.setMessage("Download has failed.");
                progressDialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
            }
        });
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
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

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public StorageReference getImageStorage() {
        return imageStorage;
    }

    public void setImageStorage(StorageReference imageStorage) {
        this.imageStorage = imageStorage;
    }
}
