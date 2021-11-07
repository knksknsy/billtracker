package de.hdm.project.billtracker.helpers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import java.util.ArrayList;
import java.util.UUID;

import de.hdm.project.billtracker.models.Bill;

/**
 * Helper class for accessing firebase's CRUD methods
 */
public class FirebaseDatabaseHelper {

    private static final String TAG = "FirebaseDatabaseHelper";

    private Activity activity;

    private FirebaseAuth auth;

    private ImageHelper imageHelper;

    private DatabaseReference dbUsers;
    private DatabaseReference dbCategories;
    private DatabaseReference dbBills;
    private StorageReference imageStorage;

    /**
     * Create an instance of FirebaseDatabaseHelper
     *
     * @param activity
     */
    public FirebaseDatabaseHelper(Activity activity) {
        this.activity = activity;
        auth = FirebaseAuth.getInstance();
        dbUsers = FirebaseDatabase.getInstance().getReference("users");
        dbCategories = FirebaseDatabase.getInstance().getReference("categories");
        dbBills = FirebaseDatabase.getInstance().getReference("bills");
        imageStorage = FirebaseStorage.getInstance().getReference();
    }

    /**
     * Save a bill instance
     *
     * @param bill
     */
    public void createBill(final Bill bill) {
        final String userUid = getUserUid();
        if (userUid != null) {
            // Save category
            dbCategories.child(userUid).child(bill.getCategory()).setValue(bill.getCategory());

            bill.setId(dbBills.push().getKey());
            bill.setImageId(UUID.randomUUID().toString());

            // Save bill information
            dbBills.child(userUid).child(bill.getCategory()).child(bill.getId()).setValue(bill);

            // Upload bill image
            uploadImage(bill);
        }
    }

    /**
     * Upload a bill's image to the ImageStorage
     *
     * @param bill
     */
    private void uploadImage(final Bill bill) {
        final String userUid = getUserUid();
        if (userUid != null) {
            Uri imageFile = ImageHelper.getImagePathAsUri(bill.getImagePath());

            StorageMetadata metadata = new StorageMetadata.Builder().setContentType("image/jpeg").build();

            UploadTask uploadTask = imageStorage.child("user").child(userUid).child(bill.getImageId()).putFile(imageFile, metadata);

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
                    taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUrl) {
                            bill.setDownloadUrl(downloadUrl.toString());
                            updateBill(bill);
                            progressDialog.dismiss();
                        }
                    });
                }
            });
        }
    }

    /**
     * Update an instance of bill
     *
     * @param bill
     */
    public void updateBill(final Bill bill) {
        final String userUid = getUserUid();
        if (userUid != null) {
            dbBills.child(userUid).child(bill.getCategory()).child(bill.getId()).setValue(bill);
            sendFirebaseDoneBroadcast();
        }
        sendFirebaseDoneBroadcast();
    }

    /**
     * Delete an instance of bill and it's referenced image in the ImageStorage
     *
     * @param bill
     */
    public void deleteBill(final Bill bill) {
        final String userUid = getUserUid();
        if (userUid != null) {
            // delete bill
            dbBills.child(userUid).child(bill.getCategory()).child(bill.getId()).removeValue();
            // delete bill image
            imageStorage.child("user").child(userUid).child(bill.getImageId()).delete();

            // delete image and thumbnail on device
            ImageHelper.deleteFromPicturesDir(bill.getImagePath());
            ImageHelper.deleteFromPicturesDir(bill.getThumbnailPath());
            sendFirebaseDoneBroadcast();
        }
        sendFirebaseDoneBroadcast();
    }

    /**
     * Update a bill's category and the locally saved image's location
     *
     * @param bill
     * @param oldCategory
     */
    public void updateCategory(final Bill bill, final String oldCategory) {
        final String userUid = getUserUid();
        if (userUid != null) {
            imageHelper = new ImageHelper(getActivity(), bill);

            // move image on device into new category
            imageHelper.moveToPicturesDir(bill.getCategory());

            bill.setImagePath(imageHelper.getImagePath());
            bill.setThumbnailPath(imageHelper.getThumbnailPath());

            // check if new category already exists
            dbCategories.child(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<String> categories = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String category = snapshot.getValue(String.class);
                        categories.add(category);
                    }
                    boolean categoryExists = categories.contains(bill.getCategory());

                    // remove bill from old category
                    dbBills.child(userUid).child(oldCategory).child(bill.getId()).removeValue();
                    // create new bill
                    dbBills.child(userUid).child(bill.getCategory()).child(bill.getId()).setValue(bill);

                    if (!categoryExists) {
                        // create new category in firebase
                        dbCategories.child(userUid).child(bill.getCategory()).setValue(bill.getCategory());
                    }
                    sendFirebaseDoneBroadcast();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        sendFirebaseDoneBroadcast();
    }

    /**
     * Delete a category, it's containing bills, and the bills' images
     *
     * @param category
     */
    public void deleteCategory(final String category) {
        final String userUid = getUserUid();
        if (userUid != null) {
            // delete bill image references
            dbBills.child(userUid).child(category).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Bill bill = snapshot.getValue(Bill.class);
                        imageStorage.child("user").child(userUid).child(bill.getImageId()).delete();
                    }
                    dbBills.child(userUid).child(category).removeValue();

                    dbCategories.child(userUid).child(category).removeValue();

                    ImageHelper.deleteCategoryDir(category);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    /**
     * Synchronize missing locally saved images
     */
    public void synchronizeImages() {
        final String userUid = getUserUid();
        if (userUid != null) {
            dbCategories.child(getUserUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot categoryDataSnapshot) {
                    for (DataSnapshot categorySnapshot : categoryDataSnapshot.getChildren()) {
                        String category = categorySnapshot.getValue(String.class);
                        dbBills.child(userUid).child(category).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot scanDataSnapshot) {
                                for (DataSnapshot scanSnapshot : scanDataSnapshot.getChildren()) {
                                    Bill bill = scanSnapshot.getValue(Bill.class);
                                    // Check if image exists on device
                                    if (!ImageHelper.fileExists(bill.getImagePath())) {
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
    }

    /**
     * Download a bill's locally missing image
     *
     * @param bill
     */
    private void downloadImage(final Bill bill) {
        final String userUid = getUserUid();
        if (userUid != null) {
            StorageReference imageStorage = FirebaseStorage.getInstance().getReferenceFromUrl(bill.getDownloadUrl());
            final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Synchronizing Image", "Downloading image...", true, false);

            ImageHelper.createCategoryDir(bill.getImagePath());

            imageStorage.getFile(ImageHelper.getImageFile(bill.getImagePath())).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
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
    }

    /**
     * Send broadcast message when firebase is done with a task
     */
    private void sendFirebaseDoneBroadcast() {
        // send message that the bill has been updated
        Intent intent = new Intent("firebase-done-event");
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }

    /**
     * Rounding a decimal value
     *
     * @param value
     * @param places
     * @return
     */
    private double round(double value, int places) {
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

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public String getUserUid() {
        return auth.getCurrentUser().getUid();
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
