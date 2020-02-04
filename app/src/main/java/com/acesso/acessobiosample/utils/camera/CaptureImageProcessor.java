package com.acesso.acessobiosample.utils.camera;

import android.graphics.Bitmap;

public interface CaptureImageProcessor {

    void capture(String base64);
    void capture(String base64, Bitmap bitmap);

}
