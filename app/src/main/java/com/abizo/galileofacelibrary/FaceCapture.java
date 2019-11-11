package com.abizo.galileofacelibrary;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.abizo.philhealthpoc.MyUtils;
import com.abizo.philhealthpoc.R;
import com.abizo.philhealthpoc.TaskFlag;
import com.element.camera.Capture;
import com.element.camera.ElementFaceCaptureActivity;
import com.google.gson.Gson;

import java.net.HttpURLConnection;

import okhttp3.Response;

public class FaceCapture extends ElementFaceCaptureActivity {
    private MyUtils myUtils;

    //Data
    private String philhealthNumber;
    private int taskFlag;
    private String faceMatchingEndpoint;
    private String faceEnrollEndpoint;

    //Misc
    private String TAG = "Avery";

    private final int LOCATION_REQ_CODE = 200;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        myUtils = new MyUtils(FaceCapture.this);

        faceMatchingEndpoint = getString(R.string.face_matching_endpoint);
        faceEnrollEndpoint = getString(R.string.face_enroll_endpoint);

        if (getIntent().hasExtra(EXTRA_ELEMENT_USER_ID) && getIntent().hasExtra(TaskFlag.FACE_TASK_EXTRA)) {
            philhealthNumber = getIntent().getExtras().getString(EXTRA_ELEMENT_USER_ID);
            taskFlag = getIntent().getExtras().getInt(TaskFlag.FACE_TASK_EXTRA);
        } else {
            Log.e(TAG, "Philhealth ID is null");
        }

    }

    @Override
    public void onImageCaptured(@Nullable Capture[] captures, @NonNull String s) {
        if (CAPTURE_RESULT_OK.equals(s) || CAPTURE_RESULT_GAZE_OK.equals(s) || CAPTURE_STATUS_VALID_CAPTURES.equals(s)) {
            Log.d(TAG, "Capture is valid");

            switch (taskFlag) {
                case TaskFlag.FACE_ENROLL:
                    sendFaceDataToElement(captures, faceEnrollEndpoint);
                    break;
                case TaskFlag.FACE_MATCH:
                    sendFaceDataToElement(captures, faceMatchingEndpoint);
                    break;
                default:
                    Log.e(TAG, "No task flag detected");
                    break;
            }
        } else if (CAPTURE_RESULT_NO_FACE.equals(s) || CAPTURE_RESULT_GAZE_FAILED.equals(s)) {
            Log.e(TAG, "Invalid face alignment");
        }

    }

    public void sendFaceDataToElement(final Capture[] captures, final String url) {
        new FmTasks(faceMatchingTaskCallback, url).execute(philhealthNumber, captures);
    }

    public FmTasks.FaceMatchingTaskCallback faceMatchingTaskCallback = new FmTasks.FaceMatchingTaskCallback() {
        @Override
        public Context getContext() {
            return getBaseContext();
        }

        @Override
        public void onResult(Response response) throws Exception {
            if (response != null) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    FmTasks.FmResponse fmResponse = new Gson().fromJson(response.body().string(), FmTasks.FmResponse.class);

                    if (taskFlag == TaskFlag.FACE_MATCH) {
                        double confiScore = fmResponse.confidenceScore;
                        if (confiScore >= 0.80) {
                            Log.d(TAG, "HIT");
                        } else {
                            Log.e(TAG, "MISS");
                        }
                    } else {
                        Log.d(TAG, "Inserted into element.");
                    }

                } else {
                    Log.e(TAG, "An error has occured: " + response.code());
                }
            } else {
                Log.e(TAG, "Response is null");
            }
        }

        @Override
        public void onException(String message) {
            Log.e(TAG, "Error on callback: " + message);
        }
    };
}
