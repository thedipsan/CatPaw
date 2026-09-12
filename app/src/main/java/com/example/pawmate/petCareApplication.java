package com.example.pawmate;

import android.app.Application;
import com.cloudinary.android.MediaManager;

import java.util.HashMap;
import java.util.Map;

public class petCareApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Configure Cloudinary
        Map<String, String>config = new HashMap<>();
        config.put("cloud_name", "c8aq18yg");
        config.put("secure", "true");

        MediaManager.init(this, config);
    }
}
