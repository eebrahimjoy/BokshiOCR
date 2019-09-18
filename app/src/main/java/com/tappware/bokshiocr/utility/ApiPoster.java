package com.tappware.bokshiocr.utility;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiPoster {
    OkHttpClient client = new OkHttpClient();



    public String post(String url, String filePath) throws IOException {
        File file = new File(filePath);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file","filename",RequestBody.create(MediaType.parse("application/octet-stream"),file))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "null";
    }
}
