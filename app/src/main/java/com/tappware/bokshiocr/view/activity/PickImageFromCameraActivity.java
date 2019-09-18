package com.tappware.bokshiocr.view.activity;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.tappware.bokshiocr.Interface.OnNetworkStateChangeListener;
import com.tappware.bokshiocr.R;
import com.tappware.bokshiocr.databinding.ActivityPickImageFromCameraBinding;
import com.tappware.bokshiocr.retrofit.ApiService;
import com.tappware.bokshiocr.retrofit.RetrofitInstance;
import com.tappware.bokshiocr.utility.ConnectivityHelper;
import com.tappware.bokshiocr.utility.CustomVisibility;
import com.tappware.bokshiocr.view.bottomSheet.SelectImageBottomSheet;
import com.tappware.bokshiocr.view.receiver.NetworkChangeReceiver;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PickImageFromCameraActivity extends AppCompatActivity implements SelectImageBottomSheet.BottomSheetListener, OnNetworkStateChangeListener {
    ActivityPickImageFromCameraBinding binding;
    private String currentPhotoPath;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_SELECT_PHOTO = 2;
    private Uri photoURI;
    private NetworkChangeReceiver mNetworkReceiver;
    private SelectImageBottomSheet selectImageBottomSheet;

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

                selectImageBottomSheet = new SelectImageBottomSheet(PickImageFromCameraActivity.this);
                selectImageBottomSheet.show(getSupportFragmentManager(), "selectImage");
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
                        "com.tappware.bokshiocrprovider",
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
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            gotoApiHit(currentPhotoPath);
        } else if (requestCode == REQUEST_SELECT_PHOTO && resultCode == RESULT_OK) {
            String imgDecodableString = null;
            Uri selectedImage = null;
            if (data != null) {
                selectedImage = data.getData();
            }
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = null;
            if (selectedImage != null) {
                cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            }
            if (cursor != null) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                 imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
            }
            //uploadToServer(imgDecodableString);

            gotoApiHit(imgDecodableString);



        }










        }

    private void gotoApiHit(String filePath) {
        startActivity(new Intent(PickImageFromCameraActivity.this,ImageRecognitionActivity.class)
        .putExtra("filePath",filePath));
    }


    private void uploadToServer(String filePath) {
        Retrofit retrofit = RetrofitInstance.getRetrofitClient(this);
        ApiService uploadAPIs = retrofit.create(ApiService.class);
        //Create a file object using file path
        File file = new File(filePath);
        // Create a request body with file and image media type
        RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
        // Create MultipartBody.Part using file request-body,file name and part name
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), fileReqBody);
        //Create request body with text description and text media type
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), "image-type");
        //
        Call<ResponseBody> call = uploadAPIs.uploadImage(description, part);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    Toast.makeText(PickImageFromCameraActivity.this, "Ok, Succsess", Toast.LENGTH_SHORT).show();

                    try {
                        String k = response.body().string();
                        String kk = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                } else {
                    Toast.makeText(PickImageFromCameraActivity.this, "Some bug but hit", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(PickImageFromCameraActivity.this, "Error"+t.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("hhhhhh", "onFailure: "+t.getMessage());
            }
        });
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

    @Override
    public void onCameraButtonClicked() {
        dispatchTakePictureIntent();
        selectImageBottomSheet.dismiss();
    }

    @Override
    public void onGalleryButtonClicked() {
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        startActivityForResult(intent,REQUEST_SELECT_PHOTO);
        selectImageBottomSheet.dismiss();

    }
}
