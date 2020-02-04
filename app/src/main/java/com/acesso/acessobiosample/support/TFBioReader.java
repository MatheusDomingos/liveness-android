package com.acesso.acessobiosample.support;


import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.widget.Button;
import android.widget.TextView;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is responsible for handling the Bio Liveness tensorflow results.
 */
public class TFBioReader {

    private Interpreter tflite;
    private Activity activity;
    private String fileNameClose;
    private String fileNameAfar;


    private static final int MAX_RESULTS = 3;
    private static final int BATCH_SIZE = 1;
    private static final int PIXEL_SIZE = 3;
    private static final float THRESHOLD = 0.1f;

    private static final int IMAGE_MEAN = 128;
    private static final float IMAGE_STD = 128.0f;

    private boolean quant = false;

    public TFBioReader (Activity pActivity) {
        this.activity = pActivity;
    }

    public TFBioReader (Activity pActivity, String pFileNameClose) {
        this.activity = pActivity;
        this.fileNameClose = pFileNameClose;
    }

    public TFBioReader (Activity pActivity, String pFileNameClose, String pFileNameAfar) {
        this.activity = pActivity;
        this.fileNameClose = pFileNameClose;
        this.fileNameAfar = pFileNameAfar;
        initTFLite();
    }


    private void initTFLite(){
        // Carregar modelo
        try {
            tflite = new Interpreter(loadModelFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public Map<String, Float> processImage(Bitmap bitmap){

        ByteBuffer byteBuffer = convertBitmapToByteBuffer(bitmap);

        if(quant){

            byte[][] result = new byte[1][2];
            tflite.run(byteBuffer, result);
            System.out.println(Arrays.deepToString(result));

        } else {

            float [][] result = new float[1][2];
            tflite.run(byteBuffer, result);
            float[] resultLiveness = result[0];
            float confidenceBoa = resultLiveness[0];
            float confidenceFotodefoto = resultLiveness[1];

            DecimalFormat df = new DecimalFormat("#.##");

            String strFotoboa =  df.format(confidenceBoa);
            String strFotodefoto =  df.format(confidenceFotodefoto);
            System.out.println("Confidence PhotoLive: " + strFotoboa + " Confidence PhotoOfPhoto " + strFotodefoto);

            Map<String, Float> map = new HashMap<>();
            map.put("confidencePhotoLive", confidenceBoa);
            map.put("confidencePhotoOfPhoto", confidenceFotodefoto);

            return map;

        }

        return null;
    }

    /** Memory-map the model file in Assets. */
    private MappedByteBuffer loadModelFile() throws IOException, IOException {

        AssetFileDescriptor fileDescriptor = this.activity.getAssets().openFd(this.fileNameClose);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);

    }


    private ByteBuffer convertBitmapToByteBuffer(Bitmap pBitmap) {

        Bitmap bitmap = Bitmap.createScaledBitmap(pBitmap, 224, 224, false);

        ByteBuffer byteBuffer;

        int inputSize =  224;

        if(quant) {
            byteBuffer = ByteBuffer.allocateDirect(BATCH_SIZE * inputSize * inputSize * PIXEL_SIZE);
        } else {
            byteBuffer = ByteBuffer.allocateDirect(4 * BATCH_SIZE * inputSize * inputSize * PIXEL_SIZE);
        }

        byteBuffer.order(ByteOrder.nativeOrder());
        int[] intValues = new int[inputSize * inputSize];
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        int pixel = 0;
        for (int i = 0; i < inputSize; ++i) {
            for (int j = 0; j < inputSize; ++j) {
                final int val = intValues[pixel++];
                if(quant){
                    byteBuffer.put((byte) ((val >> 16) & 0xFF));
                    byteBuffer.put((byte) ((val >> 8) & 0xFF));
                    byteBuffer.put((byte) (val & 0xFF));
                } else {
                    byteBuffer.putFloat((((val >> 16) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
                    byteBuffer.putFloat((((val >> 8) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
                    byteBuffer.putFloat((((val) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
                }

            }
        }
        return byteBuffer;
    }



}
