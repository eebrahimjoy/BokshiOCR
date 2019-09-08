package com.tappware.bokshiocr.view.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.tappware.bokshiocr.Interface.OnNetworkStateChangeListener;
import com.tappware.bokshiocr.R;
import com.tappware.bokshiocr.databinding.ActivityPickImageFromCameraBinding;
import com.tappware.bokshiocr.utility.ConnectivityHelper;
import com.tappware.bokshiocr.utility.CustomVisibility;
import com.tappware.bokshiocr.view.receiver.NetworkChangeReceiver;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PickImageFromCameraActivity extends AppCompatActivity implements OnNetworkStateChangeListener {
    ActivityPickImageFromCameraBinding binding;
    private String currentPhotoPath;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private Uri photoURI;
    private NetworkChangeReceiver mNetworkReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pick_image_from_camera);
        init();
        setGreetingMessage();

        binding.openCamFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.openCamFAB.setRippleColor(Color.GREEN);

                dispatchTakePictureIntent();
            }
        });

        binding.menuIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(PickImageFromCameraActivity.this, binding.menuIV);
                popup.getMenuInflater().inflate(R.menu.menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.settings_menu:

                                break;
                            case R.id.language_menu:

                                break;
                            case R.id.privacy_menu:

                                break;
                            case R.id.dataBinding:

                                return true;
                            default:
                                break;
                        }
                        return true;
                    }
                });
                popup.show();


            }
        });

    }

    private void init() {
        mNetworkReceiver = new NetworkChangeReceiver(this);
        registerNetworkBroadcast();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case R.id.settings_menu:

                return true;
            case R.id.language_menu:

                return true;
            case R.id.privacy_menu:

                return true;
            case R.id.dataBinding:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setGreetingMessage() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        String greeting = null;
        if (hour >= 5 && hour < 12) {
            greeting = "Good Morning";
            binding.greetingTV.setText(greeting);
        } else if (hour >= 12 && hour < 16) {
            greeting = "Good Afternoon";
            binding.greetingTV.setText(greeting);
        } else if (hour >= 16 && hour <= 23) {
            greeting = "Good Evening";
            binding.greetingTV.setText(greeting);


        } else {
            greeting = "Good Night";
            binding.greetingTV.setText(greeting);


        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.tappware.snapandlearnprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

            CropImage.activity(photoURI)
                    .setInitialCropWindowPaddingRatio(0)
                    .start(this);
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            binding.progressBarId.setVisibility(View.VISIBLE);
            Uri resultUri = result.getUri();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);

            } catch (IOException e) {
                e.printStackTrace();
            }

            if (bitmap != null) {

                gotoAnotherActivity(bitmap);
            }

        }
    }

    private void gotoAnotherActivity(Bitmap bitmap) {

        String image64 = convertTo64Base(bitmap);

        binding.progressBarId.setVisibility(View.GONE);

        startActivity(new Intent(PickImageFromCameraActivity.this, ImageRecognitionActivity.class).
                putExtra("image64", image64));
    }

    private String convertTo64Base(Bitmap bitmap) {
        byte[] byteImage;

        float originalWidth = bitmap.getWidth();
        float originalHeight = bitmap.getHeight();
        String encodedImage;
        if (originalWidth > 600 && originalHeight >= originalWidth) {

            float destWidth = 513;
            float destHeight = originalHeight / (originalWidth / destWidth);
            Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap, Math.round(destWidth), Math.round(destHeight), false);
            ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
            bitmap1.compress(Bitmap.CompressFormat.JPEG, 50, baos1);
            byteImage = baos1.toByteArray();
            encodedImage = Base64.encodeToString(byteImage, Base64.DEFAULT);

        } else if (originalWidth > 600 && originalHeight < originalWidth) {
            float destWidth = 513;
            float destHeight = originalHeight / (originalWidth / destWidth);
            Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap, Math.round(destWidth), Math.round(destHeight), false);
            ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
            bitmap1.compress(Bitmap.CompressFormat.JPEG, 50, baos1);
            byteImage = baos1.toByteArray();
            encodedImage = Base64.encodeToString(byteImage, Base64.DEFAULT);

        } else {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos); //decodedFoodBitmap is the bitmap object
            byteImage = baos.toByteArray();
            encodedImage = Base64.encodeToString(byteImage, Base64.DEFAULT);

        }

        return encodedImage;
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

}
