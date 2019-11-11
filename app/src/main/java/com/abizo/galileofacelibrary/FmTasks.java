package com.abizo.galileofacelibrary;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

import com.abizo.philhealthpoc.BuildConfig;
import com.abizo.philhealthpoc.R;
import com.element.camera.Capture;
import com.element.camera.ElementSDKHelper;
import com.google.gson.Gson;

import java.util.TimeZone;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class FmTasks extends AsyncTask<Object, Void, Void> {

    private static final MediaType TYPE = MediaType.parse("application/json; charset=utf-8");

    private final FaceMatchingTaskCallback faceMatchingTaskCallback;

    private final Request.Builder builder;

    private Location mLocation;

    FmTasks(FaceMatchingTaskCallback faceMatchingTaskCallback, String mURL) {
        Context context = faceMatchingTaskCallback.getContext();
        String url = context.getString(R.string.face_base_url) + mURL;
        this.faceMatchingTaskCallback = faceMatchingTaskCallback;
        this.builder = new Request.Builder().url(url);

        builder.addHeader("apiKey", context.getString(R.string.api_key));
        builder.addHeader("appVersion", BuildConfig.VERSION_NAME);
        builder.addHeader("os", "ANDROID");
        builder.addHeader("appId", context.getPackageName());
        builder.addHeader("deviceModel", Build.MODEL);
        builder.addHeader("sdkVersion", "1.0");

        Log.d("Avery", mURL);
    }

    @Override
    protected Void doInBackground(Object... params) {
        OkHttpClient client = new OkHttpClient();

        try {
            FmRequest fmRequest = new FmRequest();
            fmRequest.userId = (String) params[0];
            fmRequest.setImages((Capture[]) params[1]);

            String json = new Gson().toJson(fmRequest);
            RequestBody requestBody = RequestBody.create(TYPE, json);
            builder.post(requestBody);

            Request request = builder.build();
            Response response = client.newCall(request).execute();

            faceMatchingTaskCallback.onResult(response);

        } catch (Exception e) {
            faceMatchingTaskCallback.onException(e.getMessage());
        }

        return null;
    }

    public static class FmRequest {
        String userId;
        double latitude;
        double longitude;
        String timeZone;
        Image[] images;

        FmRequest() {
            Location location = ElementSDKHelper.getLocation();
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            timeZone = TimeZone.getDefault().getID();
        }

        void setImages(Capture[] imageData) {
            images = new Image[imageData.length];
            int i = 0;
            for (Capture capture : imageData) {
                images[i] = new Image();
                images[i].index = i;
                images[i].modality = "";
                images[i].mode = "";
                images[i].data = Base64.encodeToString(capture.data, Base64.DEFAULT);
                images[i].tag = capture.tag;
                i++;
            }
        }
    }

    static class Image {
        int index;
        String modality;
        String mode;
        String data;
        String tag;
    }

    static class FmResponse {
        String displayMessage;
        double confidenceScore;
    }

    static class ServerMessage {
        String message;
    }

    interface FaceMatchingTaskCallback {
        Context getContext();

        void onResult(Response response) throws Exception;

        void onException(String message);
    }
}