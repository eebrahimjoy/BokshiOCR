package com.tappware.bokshiocr.utility;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiPoster {
    OkHttpClient client = new OkHttpClient();

    public String post(String url, String image64) throws IOException {
        RequestBody body = new FormBody.Builder()
                .add("image64", image64)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
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
