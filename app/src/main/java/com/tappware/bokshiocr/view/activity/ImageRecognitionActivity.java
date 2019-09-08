package com.tappware.bokshiocr.view.activity;

import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.tappware.bokshiocr.Interface.OnNetworkStateChangeListener;
import com.tappware.bokshiocr.R;
import com.tappware.bokshiocr.databinding.ActivityImageRecognitionBinding;
import com.tappware.bokshiocr.utility.ConnectivityHelper;
import com.tappware.bokshiocr.utility.CustomVisibility;
import com.tappware.bokshiocr.view.fragment.ImageZoomingDialog;
import com.tappware.bokshiocr.view.receiver.NetworkChangeReceiver;
import com.tappware.bokshiocr.viewmodel.ImageRecognitionViewModel;

public class ImageRecognitionActivity extends AppCompatActivity implements OnNetworkStateChangeListener {
    private ActivityImageRecognitionBinding binding;
    private String image64 = null;
    private ImageRecognitionViewModel imageRecognitionViewModel;
    private NetworkChangeReceiver mNetworkReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_image_recognition);
        binding.progressBar.setVisibility(View.VISIBLE);
        init();

        binding.originalImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageZoomingDialog(image64);
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (getIntent().getExtras() != null) {
            image64 = getIntent().getStringExtra("image64");

            byte[] decodedString = Base64.decode(image64, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            binding.originalImageView.setImageBitmap(decodedByte);
            getData();
        }


    }

    private void getData() {
        imageRecognitionViewModel.getDataFromApi(image64).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String string) {
                if (string != null) {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.resultTV.setText(string);

                }


            }
        });
    }

    private void init() {
        imageRecognitionViewModel = ViewModelProviders.of(this).get(ImageRecognitionViewModel.class);
        mNetworkReceiver = new NetworkChangeReceiver(this);
        registerNetworkBroadcast();
    }


    private void registerNetworkBroadcast() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(mNetworkReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    private void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterNetworkChanges();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ConnectivityHelper.isConnected(this)) {
            binding.noInternetTV.setVisibility(View.GONE);
        } else {
            binding.noInternetTV.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onChange(boolean isConnected) {
        if (isConnected) {
            binding.noInternetTV.setBackgroundColor(getResources().getColor(R.color.green));
            binding.noInternetTV.setText("Back to online");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    CustomVisibility.collapse(binding.noInternetTV, 500);
                }
            }, 2000);
        } else {
            binding.noInternetTV.setBackgroundColor(getResources().getColor(R.color.red));
            binding.noInternetTV.setText("No internet connection");
            CustomVisibility.expand(binding.noInternetTV, 500);
        }
    }
    private void openImageZoomingDialog(String imageUrl) {
        Bundle bundle = new Bundle();
        bundle.putString("imageUrl", imageUrl);
        ImageZoomingDialog dialog = new ImageZoomingDialog();
        dialog.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        dialog.show(ft, "TAG");
    }

}
