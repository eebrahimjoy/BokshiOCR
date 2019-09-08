package com.tappware.bokshiocr.viewmodel;

import android.app.Application;

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

    public MutableLiveData<String> getDataFromApi(String encodedImage) {
        return imageRecognitionRepo.getDataFromApi(encodedImage);
    }
}