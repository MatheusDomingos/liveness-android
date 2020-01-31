package com.acesso.acessobiosample.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;

import com.acesso.acessobiosample.R;
import com.acesso.acessobiosample.dto.ExecuteProcessResponse;
import com.acesso.acessobiosample.dto.FaceInsertRequest;
import com.acesso.acessobiosample.dto.FaceInsertResponse;
import com.acesso.acessobiosample.dto.GetProcessResponse;
import com.acesso.acessobiosample.fragment.CustomFragment;
import com.acesso.acessobiosample.fragment.HomeFragment;
import com.acesso.acessobiosample.services.BioService;
import com.acesso.acessobiosample.services.ServiceGenerator;
import com.acesso.acessobiosample.support.MaskSilhouette;
import com.acesso.acessobiosample.support.MaskView;
import com.acesso.acessobiosample.utils.camera.CaptureImageProcessor;
import com.acesso.acessobiosample.utils.camera.ImageProcessor;
import com.acesso.acessobiosample.utils.dialog.SweetAlertDialog;
import com.acesso.acessobiosample.utils.enumetators.SharedKey;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;
import com.orhanobut.hawk.Hawk;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import org.tensorflow.lite.Interpreter;

public class SelfieActivity extends Camera2Base implements ImageProcessor, CaptureImageProcessor {

    public static final float COMPENSATION_EYE = 0.05f;
    public static int total = 0;
    private FirebaseVisionFaceDetector firebaseVisionFaceDetector;
    private FirebaseVisionImageMetadata metadata;

    private Toast toast;

    private final String[] mensagens = new String[] {
            "Centraliza o rosto",
            "Centraliza o rosto",//"Incline o celular para trás",
            "Centraliza o rosto",//"Incline o celular para frente",
            "Aproxime o rosto",
            "Afaste o rosto",
            "Gire um pouco a esquerda",
            "Gire um pouco a direita",
            "Rosto não identificado",
            "Rosto inclinado"} ;

    private int erroIndex = -1;
    private boolean faceOK = true;

    private ImageView rectangleImageView;

    private float posVerticalLineLeft = 0.0f;
    private float posVerticalLineRight = 0.0f;
    private float posHorizontalLineBottom = 0.0f;
    private float posHorizontalLineTop = 0.0f;

    private int primaryColor = Color.parseColor("#2980ff");

    // manter TRUE para exibir as linhas (deixar desabilitado)
    private boolean showLines = false;

    // variáveis do firebase
    private FirebaseVisionFace firebaseVisionFace;
    private FirebaseVisionPoint nosePosition;
    private FirebaseVisionPoint leftEyePosition;
    private FirebaseVisionPoint rightEyePosition;
    private FirebaseVisionImage[] visionImage = new FirebaseVisionImage[1];

    // posição dos olhos
    private float leftEyePosX = 0f;
    private float leftEyePosY = 0f;
    private float rightEyePosX = 0f;
    private float rightEyePosY = 0f;

    private float nosePosY = 0f;

    float diffNose = 0f;
    float noseRange = 0f;

    // rotação da cabeça
    private float headPosition = 0f;

    // utilizado para calcular as linhas de base (showLines)
    float aspectRatioRelative = 0f;

    // diferença entre os olhos
    final float minDiffEye = 100f;
    final float maxDiffEye = 190f;
    float densityFactor = 2f;
    float densityMultiply = 2f;

    // área em % da tela permitida para enquadramento na horizontal
    float percentHorizontalRange = 25f;
    // área em % da tela permitida para enquadramento na vertical
    float percentVerticalRange = 30f;
    float percentOffsetVerticalRange = 30f;

    // altura da imagem no celular
    float screenWidth = 0f;
    float screenHeight = 0f;

    float aspectRatioBioEye = 1f;

    private SweetAlertDialog dialog;

    private String origin = "";
    private boolean initialized = false;

    // contador
    private CountDownTimer countDownTimer;
    private Boolean[] countDownCancelled = new Boolean[] { Boolean.FALSE };
    private Boolean isRequestImage;
    private Boolean autoCapture;
    private Boolean countRegressive;

    protected Interpreter tflite;


    private static final int MAX_RESULTS = 3;
    private static final int BATCH_SIZE = 1;
    private static final int PIXEL_SIZE = 3;
    private static final float THRESHOLD = 0.1f;

    private static final int IMAGE_MEAN = 128;
    private static final float IMAGE_STD = 128.0f;

    private boolean quant = false;

    // Mask dinamiccally
    MaskView maskView;
    MaskSilhouette maskSilhouette;
    @ColorInt Integer  currentColorBorder;

    private TextView tvStatus;
    private  View viewFlash;
    private MaskView.MaskType maskType;

    private enum Flow {
        CLOSE , AFAR, SMILE
    }

    // Started with Flow.CLOSE flow
    private  Flow flow = Flow.CLOSE;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();

        if (b != null) {
            origin = b.getString("origin");
        }

        currentColorBorder = Color.WHITE;

        if (DEBUG) Log.d(TAG, "from activity: " + origin);

        isRequestImage = false;

        super.activity = SelfieActivity.this;
        super.imageProcessor = this;
        super.captureImageProcessor = this;


        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_selfie);

        textureView = findViewById(R.id.texture);

        autoCapture = Hawk.get(SharedKey.AUTOCAPTURE, true);
        countRegressive = Hawk.get(SharedKey.COUNT_REGRESSIVE, true);

        setMaxSizes();

        FirebaseVisionFaceDetectorOptions options =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setClassificationMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                        .setMinFaceSize(1.0f)
                        .enableTracking()
                        .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                        .build();

        firebaseVisionFaceDetector = FirebaseVision.getInstance().getVisionFaceDetector(options);

        rectangleImageView = findViewById(R.id.rectangle);

        try {
            tflite = new Interpreter(loadModelFile(this));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Mask
        flowClose();
    }

    @Override
    public void onBackPressed() {
        countDownCancelled[0] = Boolean.TRUE;
        destroyTimer();
        activity.finish();
    }

    private void addContentView(View view){
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        addContentView(view, lp);
    }


    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public void insertTvStatus() {

        if(tvStatus != null) {
            ((ViewGroup) tvStatus.getParent()).removeView(tvStatus);
        }

        tvStatus = new TextView(this);

        int widthTvStatus = (getWidthPixels() - dpToPx(150));

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                widthTvStatus, // Width of TextView
                RelativeLayout.LayoutParams.WRAP_CONTENT); // Height of TextView

        tvStatus.setLayoutParams(lp);
        tvStatus.setPadding(0, dpToPx(10), 10, dpToPx(10));
        tvStatus.setText(getString(R.string.status_error));
        tvStatus.setTextColor(primaryColor);
        tvStatus.setY(200);
        tvStatus.setX((getWidthPixels() /2) - (widthTvStatus / 2));

        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.RECTANGLE);
        gd.setColor(Color.WHITE); // make the background transparent
        gd.setCornerRadius(15.0f); // border corner radius
        tvStatus.setBackground(gd);
        tvStatus.setBackground(gd);
        tvStatus.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
        tvStatus.setTypeface(null, Typeface.BOLD);
        tvStatus.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tvStatus.setGravity(Gravity.CENTER);
        addContentView(tvStatus, lp);

    }

    private void fireFlash(){
        viewFlash = new View(this);
        RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        viewFlash.setLayoutParams(rp);
        viewFlash.setBackgroundColor(Color.WHITE);
        viewFlash.setAlpha((float) 0.7);
        addContentView(viewFlash, rp);
    }

    private void insertMask(MaskView.MaskType maskType) {

        this.maskType = maskType;

        if(maskView != null) {
            ((ViewGroup) maskView.getParent()).removeView(maskView);
        }

        if(maskType == MaskView.MaskType.CLOSE) {
            maskView = new MaskView(this, MaskView.MaskType.CLOSE);
        }else{
            maskView = new MaskView(this, MaskView.MaskType.AFAR);
        }

        addContentView(maskView);

    }

    private void insertMask(MaskView.MaskType maskType, @ColorInt Integer  color) {

        currentColorBorder = color;

        if(maskView != null) {
            ((ViewGroup) maskView.getParent()).removeView(maskView);
        }

        maskView = new MaskView(this, maskType);
        maskSilhouette = new MaskSilhouette(this, maskView, maskType, color);

        addContentView(maskView);
    }

    private void insertSillhoutte(@ColorInt Integer color) {

        if(maskSilhouette != null) {
            ((ViewGroup) maskSilhouette.getParent()).removeView(maskSilhouette);
        }
        maskSilhouette = new MaskSilhouette(this, maskView, this.maskType, color);
        addContentView(maskSilhouette);

    }

    /** Memory-map the model file in Assets. */
    private MappedByteBuffer loadModelFile(Activity activity) throws IOException {

        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd("perto.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void process(byte[] image, int w, int h, int f) {

        if (!initialized && DEBUG) {
            Log.d(TAG, "width  buffer: " + w);
            Log.d(TAG, "height buffer: " + h);
        }

        if (countDownCancelled[0]) {
            return;
        }

        init(w, h);

        if (metadata == null) {
            metadata = new FirebaseVisionImageMetadata.Builder()
                    .setWidth(w)
                    .setHeight(h)
                    .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
                    .setRotation(getImageFirebaseVisionRotation())
                    .build();
        }

        visionImage[0] = FirebaseVisionImage.fromByteArray(image, metadata);

        firebaseVisionFaceDetector
                .detectInImage(visionImage[0])
                .addOnSuccessListener(this, new OnSuccessListener<List<FirebaseVisionFace>>() {

                    @Override
                    public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {

                        visionImage[0] = null;

                        if (firebaseVisionFaces.size() > 0) {
                            firebaseVisionFace = firebaseVisionFaces.get(0);

                            leftEyePosition = firebaseVisionFace.getLandmark(FirebaseVisionFaceLandmark.LEFT_EYE).getPosition();
                            rightEyePosition = firebaseVisionFace.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EYE).getPosition();
                            nosePosition = firebaseVisionFace.getLandmark(FirebaseVisionFaceLandmark.NOSE_BASE).getPosition();

                            if (leftEyePosition != null && rightEyePosition != null) {

                                headPosition = firebaseVisionFace.getHeadEulerAngleY();

                                // Left Eye  -------------
                                leftEyePosX = (h - rightEyePosition.getX());
                                leftEyePosY = (rightEyePosition.getY());

                                // Right Eye -------------
                                rightEyePosX = (h - leftEyePosition.getX());
                                rightEyePosY = (leftEyePosition.getY());

                                nosePosY = nosePosition.getY();


                                // set to GC -------------
                                leftEyePosition = null;
                                rightEyePosition = null;
                                firebaseVisionFace = null;

                                faceOK = true;
                                erroIndex = -1;

                                // Distancia entre olhos
                                float diffEye = Math.abs(rightEyePosX - leftEyePosX);
                                diffEye = (diffEye * aspectRatioBioEye);

                                diffNose = (nosePosY - leftEyePosY);
                                noseRange = (diffEye / 3);
                                float maxDiffNose = (noseRange * (float)2.2);

                                if (DEBUG) {
                                    Log.d(TAG, "Entre olhos: " + diffEye);
                                    Log.d(TAG, "Olho esquerdo (X): " + leftEyePosX);
                                    Log.d(TAG, "Olho esquerdo (Y): " + leftEyePosY);
                                    Log.d(TAG, "Olho direito (X): " + rightEyePosX);
                                    Log.d(TAG, "Olho direito (Y): " + rightEyePosY);
                                }

                                // Olhos fora do enquadramento na horizontal
                                if (leftEyePosX < posVerticalLineLeft || rightEyePosX > posVerticalLineRight) {
                                    erroIndex = 0;
                                    faceOK = false;
                                }
                                // Olhos fora do enquadramento na vertical
                                else if (leftEyePosY < posHorizontalLineTop || leftEyePosY > posHorizontalLineBottom) {
                                    // olhos muito acima
                                    if (leftEyePosY < posHorizontalLineTop) {
                                        erroIndex = 1;
                                    }
                                    // olhos muito abaixo
                                    else {
                                        erroIndex = 2;
                                    }
                                    faceOK = false;
                                }
                                // Rosto muito próximo
                                else if (diffEye < minDiffEye) {
                                    erroIndex = 3;
                                    faceOK = false;
                                }
                                // Rosto muito afastado
                                else if (diffEye > maxDiffEye) {
                                    erroIndex = 4;
                                    faceOK = false;
                                }
                                // Vire a esquerda
                                else if (headPosition < -16) {
                                    erroIndex = 5;
                                    faceOK = false;
                                }
//                                // Vire a direita
                                else if (headPosition > 16) {
                                    erroIndex = 6;
                                    faceOK = false;
                                }
                                // rotação da cabeça (direita & esquerda)
                                else if (((Math.abs(rightEyePosY - leftEyePosY)) > 20) || ((Math.abs(leftEyePosY - rightEyePosY)) > 20)) {
                                    erroIndex = 8;
                                    faceOK = false;
                                }  // celular muito inclinado referente ao olhos
                                else if (diffNose < noseRange || diffNose > maxDiffNose) {
                                    //Log.d(TAG, "celular muito inclinado referente ao olhos");
                                    erroIndex = 0;
                                    faceOK = false;
                                }

                                if (faceOK) {
                                    markBlue();
                                }
                                else {
                                    markRed();
                                }

                            }
                            else {
                                erroIndex = 7;
                                markRed();
                            }
                        }
                        else {
                            erroIndex = 7;
                            markRed();
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (erroIndex != -1) {
                                    showFastToast(mensagens[erroIndex]);
                                }
                            }
                        });
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception ex) {
                        Log.d(TAG, "ERRO: " + ex.toString());
                    }
                });
    }

    private void destroyTimer () {

        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    private void createTimer () {

        if (countDownTimer == null && isRequestImage == false) {


            countDownTimer = new CountDownTimer(2000, 1000) {

                public void onTick(long millisUntilFinished) {
                    countDownCancelled[0] = Boolean.FALSE;
                }

                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                public void onFinish() {
                    if (!countDownCancelled[0]) {
                        //isRunning = false;
                        isRequestImage = true;
                        destroyTimer();

                        fireFlash();
                        takePicture();

                    }
                }

            };
            countDownTimer.start();
        }
    }

    protected  void removeView(View view) {
        if(view != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    private void autoCapture () {

        if(countDownTimer == null && isRequestImage == false) {

            countDownTimer = new CountDownTimer(2000, 1000) {

                public void onTick(long millisUntilFinished) {

                }

                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                public void onFinish() {
                    //isRunning = false;
                    isRequestImage = true;
                    destroyTimer();
                    takePicture();
                }

            }.start();
        }
    }

    public static float convertPixelsToDp(float px, Context context){
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static float convertDpToPixel(float dp, Context context){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    private void markBlue() {

        if (toast != null) {
            toast.cancel();
        }

        @ColorInt Integer  colorGreen = ResourcesCompat.getColor(getResources(), R.color.colorGreenMaskBorder, null) ;

        if(!currentColorBorder.equals(colorGreen)) {
            currentColorBorder = colorGreen;
            insertSillhoutte(colorGreen);
            tvStatus.setText(getString(R.string.status_success));
           // insertMask(MaskView.MaskType.CLOSE, ResourcesCompat.getColor(getResources(), R.color.colorGreenMaskBorder, null));
        }

        if (autoCapture && countRegressive && !countDownCancelled[0]) {
            createTimer();
        }
        else if (autoCapture) {
            autoCapture();
        }

        int size = 18;
        if (screenWidth > 1600) {
            size = 34;
        }

    }


    private void markRed() {

        destroyTimer();

        if(currentColorBorder !=  Color.WHITE) {
            currentColorBorder = Color.WHITE;
            insertSillhoutte(Color.WHITE);
            tvStatus.setText(getString(R.string.status_error));
        }

        if (!countDownCancelled[0]) {
            int size = 18;
            if (screenWidth > 1600) {
                size = 34;
            }
        }

    }

    private void init(float widthBuffer, float heightBuffer) {

        if (!initialized) {

            // tela
            screenWidth = getWidthPixels();
            screenHeight = getHeightPixels();

            // construcao do aspect ratio (show lines)
            boolean isBufferPortrait = widthBuffer < heightBuffer;

            float aspectRatioBuffer = isBufferPortrait ? widthBuffer / heightBuffer : heightBuffer / widthBuffer;
            float imageHeightScreen = screenWidth / aspectRatioBuffer;
            float imageHeightBuffer = BIOMETRY_IMAGE_HEIGHT / aspectRatioBuffer;

            aspectRatioRelative = imageHeightScreen / (isBufferPortrait ? heightBuffer : widthBuffer);
            aspectRatioBioEye = imageHeightBuffer / (isBufferPortrait ? heightBuffer : widthBuffer);

            // validação para os casos em que o tamanho do buffer é maior que o esperado
            aspectRatioBioEye = (aspectRatioBioEye < 1f ? 1f : aspectRatioBioEye);

            if (DEBUG) Log.d(TAG, "Aspect Ratio BIO: " + aspectRatioBioEye);

            // linhas delimitadoras
            float refWidth = isBufferPortrait ? widthBuffer : heightBuffer;
            float refHeight = isBufferPortrait ? heightBuffer : widthBuffer;

            float topOffet = (refHeight * (percentOffsetVerticalRange) / 100);
            float heightRange = (refHeight * (percentVerticalRange) / 100);

            // densidade
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            densityFactor = metrics.density;

            posVerticalLineLeft = (refWidth * (percentHorizontalRange / 100));
            posVerticalLineRight = (refWidth * ((percentHorizontalRange / 100) * 3));
            posHorizontalLineTop = topOffet;
            posHorizontalLineBottom = topOffet + heightRange;
            initialized = true;
        }
    }

    @Override
    public void onClick(View view) {

    }

    // this method changed the flow of proccess liveness.
    private void changeTheFlow() {

        ((ViewGroup) viewFlash.getParent()).removeView(viewFlash);
        currentColorBorder = Color.WHITE;

        switch (flow){
            case CLOSE:
                flow  = Flow.AFAR;
                flowAfar();
                break;
            case AFAR:
                flow = Flow.SMILE;
                flowSmile();
                break;
            case SMILE:
                break;
            default:
        }

    }

    private void flowClose() {
        insertMask(MaskView.MaskType.CLOSE);
        insertTvStatus();
    }

    private void flowAfar(){
        insertMask(MaskView.MaskType.AFAR);
        insertSillhoutte(Color.WHITE);
        insertTvStatus();
        reopenCamera();
    }

    private void flowSmile() {
        tvStatus.setText(getString(R.string.status_smile));
        tvStatus.setTextColor( ResourcesCompat.getColor(getResources(), R.color.colorGreenMaskBorder, null));
    }

    @Override
    public void capture(String base64) {

        if (base64 != null) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    changeTheFlow();

                }
            });

            //* String processId = Hawk.get(SharedKey.PROCESS, "");
           //* String cpf = Hawk.get(SharedKey.CPF, "");

//            dialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
//            dialog.getProgressHelper().setBarColor(Color.parseColor("#2980ff"));
//            dialog.setTitleText("Aguarde...");
//            dialog.setCancelable(false);
//            dialog.show();

            // cadastro
           //* if (processId != null && processId.trim().length() > 0) {
               //* faceInsert(base64, processId);
            //*}


        } else {
            showErrorMessage("Erro ao recuperar imagem capturada");
        }
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


    public float[] convertByteToInt (byte[] byteArray) {

        ByteBuffer buffer = ByteBuffer.wrap(byteArray);
        float[] floatArray = new float[byteArray.length];

        for(int i = 0; i < byteArray.length; i ++) {
            floatArray[i] = buffer.getFloat(i);
        }

        return floatArray;
    }



    private void faceInsert(String base64, String processId) {

        try {

            byte[] imageAsBytes = Base64.decode(base64.getBytes(), Base64.DEFAULT);

            Bitmap bitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
            ByteBuffer byteBuffer = convertBitmapToByteBuffer(bitmap);

            if(quant){
                byte[][] result = new byte[1][2];
                tflite.run(byteBuffer, result);
                System.out.println(result);;
            } else {
                float [][] result = new float[1][2];
                tflite.run(byteBuffer, result);
                System.out.println(result);

            }


//            String[] inputs = {"fotoboa", "fotodefoto"};
//
//            float[][] labelProbArray = new float[1][inputs.length];
//
//            float[] x = convertByteToInt(imageAsBytes);
//            tflite.run(x, labelProbArray);

        }catch (Exception e) {
            System.out.println(e);
        }



        FaceInsertRequest request = new FaceInsertRequest();
        request.setImagebase64(base64);
        request.setValidateLiveness(Hawk.get(SharedKey.LIVENESS, true));

        // Face Insert  ----------------------
        ServiceGenerator
                .createService(BioService.class)
                .faceInsert(processId, request)
                .enqueue(new Callback<FaceInsertResponse>() {

                    @Override
                    public void onResponse(Call<FaceInsertResponse> call, Response<FaceInsertResponse> response) {
                        FaceInsertResponse body = response.body();

                        isRequestImage = false;

                        if (body != null && body.isValid()) {

                            Hawk.delete(SharedKey.PROCESS);
                            executeProcess(processId);

                        }
                        else {


                            countDownCancelled[0] = Boolean.FALSE;
                            isRequestImage = false;

                            dialog.dismiss();



                            String message = getErrorMessage(response);
                            if (message != null) {
                                //showToastMessage(message);

                                showErrorMessage(message);

                            }else{
                                showToastMessage("Face não autenticada");

                            }


                        }
                    }

                    @Override
                    public void onFailure(Call<FaceInsertResponse> call, Throwable t) {
                        if (DEBUG) Log.d(TAG, "ERRO: " + t.toString());
                        dialog.dismiss();
                        isRequestImage = false;
                        showSnackbarError("Falha ao inserir face. " + t.getMessage());
                        reopenCamera();
                        dialog.dismiss();

                    }
                });
    }


    public void executeProcess (String processId) {

        // Execute Process  ----------------------
        ServiceGenerator
                .createService(BioService.class)
                .executeProcess(processId).enqueue(new Callback<ExecuteProcessResponse>() {

            @Override
            public void onResponse(Call<ExecuteProcessResponse> call, Response<ExecuteProcessResponse> response) {
                ExecuteProcessResponse body = response.body();

                if (body == null || !body.isValid()) {
                    showToast("Não foi possível executar o processo");
                    reopenCamera();
                    dialog.dismiss();

                } else {
                    getProcess(processId);
                }
            }

            @Override
            public void onFailure(Call<ExecuteProcessResponse> call, Throwable t) {
                if (DEBUG) Log.d(TAG, "ERRO: " + t.toString());
                dialog.dismiss();
                isRequestImage = false;

                if (DEBUG) Log.d(TAG, t.toString());
                showSnackbarError("Erro ao executar processo. " + t.getMessage());
                reopenCamera();
                dialog.dismiss();

            }
        });

    }


    private void getProcess(String processId) {

        ServiceGenerator
                .createService(BioService.class)
                .getProcess(processId).enqueue(new Callback<GetProcessResponse>() {

            @Override
            public void onResponse(Call<GetProcessResponse> call, Response<GetProcessResponse> response) {
                GetProcessResponse body = response.body();

                if (body == null || !body.isValid()) {

                    dialog.dismiss();

                    showToast("Não foi possível executar o processo");
                    reopenCamera();
                } else {

                    int status = body.getGetProcessResult().getProcess().getStatus();

                    if(status == 2) {

                        dialog.dismiss();
                        alertDivergence();

                    }else  {

                        if(status == 1) {
                            getProcess(processId);
                        }else{
                            dialog.dismiss();

                            nextFlow();
                        }

                    }

                }
            }

            @Override
            public void onFailure(Call<GetProcessResponse> call, Throwable t) {
                if (DEBUG) Log.d(TAG, "ERRO: " + t.toString());
                dialog.dismiss();
                isRequestImage = false;

                if (DEBUG) Log.d(TAG, t.toString());
                showSnackbarError("Erro ao executar processo. " + t.getMessage());
                reopenCamera();
            }
        });

    }


    public  void nextFlow() {

        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Feito!")
                .setContentText("Face autenticada!")
                .setConfirmText("Entendi")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {

                        Intent intent = new Intent(SelfieActivity.this, SimpleViewActivity.class);
                        intent.putExtra(CustomFragment.FRAGMENT, HomeFragment.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);


                    }
                }).show();
    }

    private void alertDivergence(){
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Divergência")
                .setContentText("Este cadastro possui ou está envolvido com um registro de divergência e foi enviado para a mesa de análise! Consulte o portal crediário.")
                .setConfirmText("Entendi")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {

                        Intent intent = new Intent(SelfieActivity.this, SimpleViewActivity.class);
                        intent.putExtra(CustomFragment.FRAGMENT, HomeFragment.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);


                    }
                }).show();
    }



    private void  alertError(String message){
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Ops")
                .setContentText(message)
                .setConfirmText("Entendi")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {


                    }
                }).show();
    }

    private void showErrorMessage(String message) {
        showAlert(false, message, new SweetAlertDialog.OnSweetClickListener() {

            @Override
            public void onClick(SweetAlertDialog dialog) {
                dialog.dismissWithAnimation();
                reopenCamera();
            }
        });
    }

    protected void showFastToast(final String message) {

        try {
            if (toast == null) {


            }

//            toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
//            toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 0);
//            toast.setText(message);
//            toast.show();

        } catch (Exception ex) {
            Log.d(TAG, ex.toString());
        }
    }

}