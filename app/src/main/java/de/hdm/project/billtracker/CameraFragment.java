package de.hdm.project.billtracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import static android.R.attr.data;

public class CameraFragment extends Fragment {

    private RelativeLayout btnCapture;
    private static final int CAMERA_REQUEST_CODE = 102;
//    public static final int MEDIA_TYPE_IMAGE = 1;


    public static ChartFragment newInstance() {
        return new ChartFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        btnCapture = getActivity().findViewById(R.id.buttoncam);
//        btnCapture.setOnClickListener(new View.OnClickListener() {

//            @Override
//            public void onClick(View v) {
//                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
//            }
//        });

        return inflater.inflate(R.layout.fragment_camera, container, false);
    }
}
