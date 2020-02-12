package com.acessobio.liveness.utils.camera;

public interface ImageProcessor {

    void process(byte[] image, int w, int h, int f);
}
