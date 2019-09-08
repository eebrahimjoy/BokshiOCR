package com.tappware.bokshiocr.repository;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.MutableLiveData;

import com.tappware.bokshiocr.utility.ApiPoster;
import com.tappware.bokshiocr.utility.StaticKeys;;

import java.io.IOException;

public class ImageRecognitionRepo {
    private Application application;

    public ImageRecognitionRepo(Application application) {
        this.application = application;
    }

    public MutableLiveData<String> getDataFromApi(final String encodedImage) {
        final MutableLiveData<String> response = new MutableLiveData<>();

        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                ApiPoster apiPoster = new ApiPoster();
                try {
                    String returnData = apiPoster.post(StaticKeys.URL, encodedImage);
                    if (!returnData.isEmpty()) {
                        return returnData;
                    }
                    return null;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }

            }

            @Override
            protected void onPostExecute(String dataString) {
                super.onPostExecute(dataString);
                if (dataString != null) {

                    String responseString = "There are ";

                    if (dataString.contains(", 1,") || dataString.contains(", 2,")
                            || dataString.contains(", 3,") || dataString.contains(", 4,")
                            || dataString.contains(", 5,") || dataString.contains(", 6,")
                            || dataString.contains(", 7,") || dataString.contains(", 8,")
                            || dataString.contains(", 9,") || dataString.contains(", 10,")
                            || dataString.contains(", 11,") || dataString.contains(", 12,")
                            || dataString.contains(", 13,") || dataString.contains(", 14,")
                            || dataString.contains(", 15,") || dataString.contains(", 16,")
                            || dataString.contains(", 17,") || dataString.contains(", 18,")
                            || dataString.contains(", 19,") || dataString.contains(", 20,")) {

                        if (dataString.contains(", 1,")) {
                            responseString = responseString + "aeroplane";
                        }
                        if (dataString.contains(", 2,")) {
                            responseString = responseString + " bicycle";
                        }
                        if (dataString.contains(", 3,")) {
                            responseString = responseString + " bird";
                        }
                        if (dataString.contains(", 4,")) {
                            responseString = responseString + " boat";
                        }
                        if (dataString.contains(", 5,")) {
                            responseString = responseString + " bottle";
                        }
                        if (dataString.contains(", 6,")) {
                            responseString = responseString + " bus";
                        }
                        if (dataString.contains(", 7,")) {
                            responseString = responseString + " car";
                        }
                        if (dataString.contains(", 8,")) {
                            responseString = responseString + " cat";
                        }
                        if (dataString.contains(", 9,")) {
                            responseString = responseString + " chair";
                        }
                        if (dataString.contains(", 10,")) {
                            responseString = responseString + " cow";
                        }
                        if (dataString.contains(", 11,")) {
                            responseString = responseString + " diningtable";
                        }
                        if (dataString.contains(", 12,")) {
                            responseString = responseString + " dog";
                        }
                        if (dataString.contains(", 13,")) {
                            responseString = responseString + " horse";
                        }
                        if (dataString.contains(", 14,")) {
                            responseString = responseString + " motorbike";
                        }
                        if (dataString.contains(", 15,")) {
                            responseString = responseString + " person";
                        }
                        if (dataString.contains(", 16,")) {
                            responseString = responseString + " pottedplant";
                        }
                        if (dataString.contains(", 17,")) {
                            responseString = responseString + "sheep";
                        }
                        if (dataString.contains(", 18,")) {
                            responseString = responseString + " sofa";
                        }
                        if (dataString.contains(", 19,")) {
                            responseString = responseString + " train";
                        }
                        if (dataString.contains(", 20,")) {
                            responseString = responseString + " tvmonitor";
                        }
                        response.postValue(responseString);

                    } else {
                        response.postValue("Can not recognized the given image");
                    }
                }
            }

        };
        asyncTask.execute();

        return response;

    }
}