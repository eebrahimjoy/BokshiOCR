package com.tappware.bokshiocr.repository;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.tappware.bokshiocr.retrofit.ApiService;
import com.tappware.bokshiocr.retrofit.RetrofitInstance;
import com.tappware.bokshiocr.utility.ApiPoster;
import com.tappware.bokshiocr.utility.StaticKeys;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ImageRecognitionRepo {
    private Application application;

    public ImageRecognitionRepo(Application application) {
        this.application = application;
    }


    public MutableLiveData<String> getDataViaRetrofit(final String filePath, final Context context) {
        final MutableLiveData<String> responseString = new MutableLiveData<>();

        Retrofit retrofit = RetrofitInstance.getRetrofitClient(context);
        ApiService uploadAPIs = retrofit.create(ApiService.class);
        File file = new File(filePath);
        RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), fileReqBody);
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), "image-type");

        Call<ResponseBody> call = uploadAPIs.uploadImage(description,part);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        if (response.body() != null) {
                            String dataString = response.body().string();
                            dataString = dataString.substring(dataString.indexOf("{"), dataString.lastIndexOf("}") + 1);
                            dataString = dataString.replace("\\", "");
                            Log.d("", "onResponse: "+dataString);

                            JsonObject jsonObject = Json.parse(dataString).asObject();
                            String jsonArray = jsonObject.get("data").asString();
                            Log.d("", "onResponse: "+jsonArray);

                            responseString.postValue(jsonArray);


                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    responseString.postValue("Retrofit Success: but something error in body");
                }

            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {

                responseString.postValue("Failure: " + t.getMessage());

            }
        });
        return responseString;
    }

    public MutableLiveData<String> getDataViaOKHTTP(final String filePath) {
        final MutableLiveData<String> response = new MutableLiveData<>();

        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                ApiPoster apiPoster = new ApiPoster();
                try {
                    String returnData = apiPoster.post(StaticKeys.URL, filePath);
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
                    String a = dataString;
                    String aa = dataString;
                }
            }

        };
        asyncTask.execute();

        return response;

    }



}