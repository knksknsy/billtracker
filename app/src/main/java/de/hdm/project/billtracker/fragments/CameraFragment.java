package de.hdm.project.billtracker.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.annotation.NonNull;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Date;

import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.EditText;
import android.widget.Toast;

import de.hdm.project.billtracker.helpers.CameraPreview;
import de.hdm.project.billtracker.helpers.FirebaseDatabaseHelper;
import de.hdm.project.billtracker.models.Bill;
import de.hdm.project.billtracker.helpers.ImageHelper;
import de.hdm.project.billtracker.R;

public class CameraFragment extends Fragment {

    private static final int REQUEST_CAMERA_PERMISSION = 200;
    public static final int SAVE_DIALOG_FRAGMENT = 1;

    int dialogStack = 0;

    private TextureView textureView;
    private Button photoButton;
    private Button saveButton;
    private EditText totalSum;

    private boolean isSumEmpty;
    private boolean pictureTaken = false;

    private ImageHelper imageHelper;
    private FirebaseDatabaseHelper fDatabaseHelper;
    private CameraPreview cameraPreview;

    public static ChartFragment newInstance() {
        return new ChartFragment();
    }

    // Update UI when image has saved picture successfully on device
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        enableSaveButton(true);
                    }
                });
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View inflatedView = inflater.inflate(R.layout.fragment_camera, container, false);

        if (savedInstanceState != null) {
            dialogStack = savedInstanceState.getInt("dialog-level");
        }

        fDatabaseHelper = new FirebaseDatabaseHelper(getActivity());
        imageHelper = new ImageHelper(getActivity());

        textureView = inflatedView.findViewById(R.id.textureView);
        // Initialize camera preview
        cameraPreview = new CameraPreview(getActivity(), textureView, imageHelper);
        textureView.setSurfaceTextureListener(cameraPreview.getTextureListener());

        totalSum = inflatedView.findViewById(R.id.totalSum);
        totalSum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                isSumEmpty = totalSum.getText().toString().isEmpty();
                enableSaveButton();
            }
        });

        isSumEmpty = totalSum.getText().toString().isEmpty();

        photoButton = inflatedView.findViewById(R.id.photoButton);
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pictureTaken) {
                    cameraPreview.capturePicture();
                } else {
                    recaptureImage();
                }
            }
        });

        saveButton = inflatedView.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSaveDialog(SAVE_DIALOG_FRAGMENT);
            }
        });
        enableSaveButton();

        // Broadcast signaling if image capture in CameraPreview is complete
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter("capture-complete-event"));

        return inflatedView;
    }

    private void enableSaveButton(boolean pictureTaken) {
        this.pictureTaken = pictureTaken;
        photoButton.setText(this.pictureTaken ? "Retake Photo" : "Take Picture");
        saveButton.setEnabled(!isSumEmpty && this.pictureTaken);
    }

    private void enableSaveButton() {
        saveButton.setEnabled(!isSumEmpty && pictureTaken);
    }

    private void recaptureImage() {
        enableSaveButton(false);
        cameraPreview.createCameraPreview(textureView);
    }

    /**
     * Display the CategoryDialogFragment
     *
     * @param type
     */
    private void showSaveDialog(int type) {
        dialogStack++;

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("save-dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        switch (type) {
            case SAVE_DIALOG_FRAGMENT:
                DialogFragment dialogFrag = CategoryDialogFragment.newInstance(123);
                dialogFrag.setTargetFragment(this, SAVE_DIALOG_FRAGMENT);
                dialogFrag.show(getFragmentManager().beginTransaction(), "save-dialog");
                break;
        }
    }

    /**
     * Handle the result of the CategoryDialogFragment
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SAVE_DIALOG_FRAGMENT:
                if (resultCode == Activity.RESULT_OK) {
                    enableSaveButton(false);

                    String category = data.getStringExtra("category");
                    String title = data.getStringExtra("title");

                    prepareAndUploadBill(category, title);

                    cameraPreview.createCameraPreview(textureView);
                }
                break;
        }
    }

    /**
     * Prepares a Bill object and uploads it to firebase
     *
     * @param category
     * @param title
     */
    private void prepareAndUploadBill(String category, String title) {
        imageHelper.moveImageOnDevice(category);

        Double sum = Double.parseDouble(totalSum.getText().toString());
        totalSum.getText().clear();

        Bill bill = new Bill(title, category, new Date().getTime(), sum, imageHelper.getImagePath(), imageHelper.getThumbnailPath());

        // Upload bill to firebase
        fDatabaseHelper.writeBill(bill);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(getActivity(), "You can't use this app without granting permission", Toast.LENGTH_SHORT).show();
                System.exit(0);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("dialog-level", dialogStack);
    }

    @Override
    public void onResume() {
        super.onResume();
        cameraPreview.startBackgroundThread();
        enableSaveButton(false);
        if (textureView.isAvailable()) {
            cameraPreview.openCamera();
        } else {
            textureView.setSurfaceTextureListener(cameraPreview.getTextureListener());
        }
    }

    @Override
    public void onPause() {
        cameraPreview.closeCamera();
        cameraPreview.stopBackgroundThread();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
