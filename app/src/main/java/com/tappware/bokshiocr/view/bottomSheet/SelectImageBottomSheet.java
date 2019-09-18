package com.tappware.bokshiocr.view.bottomSheet;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.tappware.bokshiocr.R;
public class SelectImageBottomSheet extends BottomSheetDialogFragment {

    private BottomSheetListener mListener;
    private LinearLayout camera,gallery;

    public SelectImageBottomSheet(){

    }

    @SuppressLint("ValidFragment")
    public SelectImageBottomSheet(BottomSheetListener mListener) {
        this.mListener = mListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.capture_image_bottom_sheet,container,false);
        camera = view.findViewById(R.id.cameraImageLayoutID);
        gallery = view.findViewById(R.id.galleryImageLayoutID);
        onClick();

        return view;
    }


    private void onClick() {
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onCameraButtonClicked();

            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mListener.onGalleryButtonClicked();
            }
        });
    }
    public interface BottomSheetListener {
        void onCameraButtonClicked();
        void onGalleryButtonClicked();
    }
}
