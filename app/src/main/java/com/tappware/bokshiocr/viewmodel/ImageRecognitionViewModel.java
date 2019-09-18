package com.tappware.bokshiocr.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.tappware.bokshiocr.repository.ImageRecognitionRepo;

public class ImageRecognitionViewModel extends AndroidViewModel {
    ImageRecognitionRepo imageRecognitionRepo;


    public ImageRecognitionViewModel(@NonNull Application application) {
        super(application);
        imageRecognitionRepo = new ImageRecognitionRepo(application);
    }

    public MutableLiveData<String> getDataViaRetrofit(String filePath, Context context) {
        return imageRecognitionRepo.getDataViaRetrofit(filePath,context);
    }
    public MutableLiveData<String> getDataViaOKHTTP(String filePath) {
        return imageRecognitionRepo.getDataViaOKHTTP(filePath);
    }
}