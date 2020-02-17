package com.acesso.acessobiosample.activity.homolog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
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
import com.acesso.acessobiosample.dto.CreateProcessRequest;
import com.acesso.acessobiosample.dto.CreateProcessResponse;
import com.acesso.acessobiosample.dto.ExecuteProcessResponse;
import com.acesso.acessobiosample.dto.FaceInsertRequest;
import com.acesso.acessobiosample.dto.FaceInsertResponse;
import com.acesso.acessobiosample.dto.GetProcessResponse;
import com.acesso.acessobiosample.dto.LivenessBillingRequest;
import com.acesso.acessobiosample.dto.LivenessBillingResponse;
import com.acesso.acessobiosample.dto.LivenessRequest;
import com.acesso.acessobiosample.dto.LivenessRequestSample;
import com.acesso.acessobiosample.dto.LivenessResponse;
import com.acesso.acessobiosample.dto.Subject;
import com.acesso.acessobiosample.services.BioService;
import com.acesso.acessobiosample.services.ServiceGenerator;
import com.acesso.acessobiosample.support.BioLivenessServiceHomolog;
import com.acesso.acessobiosample.support.BioLivenessValidateHomolog;
import com.acesso.acessobiosample.support.BioMaskSilhouetteHomolog;
import com.acesso.acessobiosample.support.BioMaskViewHomolog;
import com.acesso.acessobiosample.support.LivenessXHomolog;
import com.acesso.acessobiosample.support.TFBioReader;
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

import org.json.JSONObject;
import org.tensorflow.lite.Interpreter;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.tensorflow.lite.Interpreter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelfieActivityHomolog extends Camera2BaseHomolog implements ImageProcessor, CaptureImageProcessor {

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


    private View lineTopView;
    private View lineBottomView;
    private View lineLeftView;
    private View lineRightView;

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
    float minDiffEye = 160f;// old 100f
    float maxDiffEye = 240f; // old: 190f
    float densityFactor = 2f;
    float densityMultiply = 2f;

    // área em % da tela permitida para enquadramento na horizontal
    float percentHorizontalRange = 20f; // antigo:  percentHorizontalRange = 25f
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


    private static final int MAX_RESULTS = 3;
    private static final int BATCH_SIZE = 1;
    private static final int PIXEL_SIZE = 3;
    private static final float THRESHOLD = 0.1f;

    private static final int IMAGE_MEAN = 128;
    private static final float IMAGE_STD = 128.0f;

    private boolean quant = false;


    // TensorFlow
    protected Interpreter tflite;
    private  final String CLOSE_MODEL = "perto.tflite";
    private  final String AWAY_MODEL = "longe.tflite";


    // MaskBio
    BioMaskViewHomolog bioMaskView;
    BioMaskSilhouetteHomolog bioMaskSilhouette;
    @ColorInt Integer  currentColorBorder;
    private TextView tvStatus;
    private  View viewFlash;
    private BioMaskViewHomolog.MaskType maskType;
    public RectF rectMask;


    // Bitmaps and images of process
    private Bitmap bitmapClose;
    private  Bitmap bitmapAfarSmiling;
    private String base64Close;
    private String base64Afar;


    // Flows of process
    private enum Flow {
        CLOSE , AFAR, SMILE, SMILE_VALIDATE_ERROR, RESET
    }
    private Flow currentFlow = Flow.CLOSE; // Started with Flow.CLOSE flow
    private boolean isChangeFlow = true;


    //User behaviors
    float smilingProbability = 0f;
    boolean isSmilingApproved = false;
    boolean isUserBlinkingAprroved = false;
    float leftEyeOpenProbability = 0f;
    float rightEyeOpenProbability = 0f;
    boolean isEnterSmiling = false; // Did the user come in with a smile on his face?
    List<Float> arrLeftEyeOpenProbability = new ArrayList<Float>();
    Date startDateOfProcess; // This variable indicates the hour/minute/second of the begin process.

    // Process Session
    // Through  this variable, we have been control the session of user.
    private int SESSION = 0;
    private boolean countDownSmileStarted = false;
    CountDownTimer countDownUserBehavior;

    // Billing
    private String urlInstance, apikey, authToken;

    // Requests
    String biometryMessageClose = "";
    Integer biometryCodeClose = 200;

    String biometryMessageAfar = "";
    Integer biometryCodeAfar = 200;

    HashMap<String, String> resultLiveness;

    int spoofingReset = 0;

    boolean isCommingVerificationFromSpoofing = false;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();

        if (b != null) {
            origin = b.getString("origin");
            this.urlInstance = b.getString("urlInstance");
            this.apikey = b.getString("apikey");
            this.authToken = b.getString("authToken");
        }

        currentColorBorder = Color.WHITE;

        if (DEBUG) Log.d(TAG, "from activity: " + origin);

        isRequestImage = false;

        super.activity = SelfieActivityHomolog.this;
        super.imageProcessor = this;
        super.captureImageProcessor = this;


        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_selfie_homolog);

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

        lineTopView = findViewById(R.id.lineTop);
        lineBottomView = findViewById(R.id.lineBottom);
        lineLeftView = findViewById(R.id.lineLeft);
        lineRightView = findViewById(R.id.lineRight);

        // Mask
        flowClose();
    }

    @Override
    public void onBackPressed() {
        countDownCancelled[0] = Boolean.TRUE;
        destroyTimer();
        activity.finish();
    }


    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }


    // Reset Session
    private void resetSession() {

        SESSION++;

        currentFlow = Flow.CLOSE;

        if(isCommingVerificationFromSpoofing) {
           isCommingVerificationFromSpoofing = false;
        }else{
            if(!verifySession()) return;
        }

        isSmilingApproved = false;
        bitmapClose = null;
        bitmapAfarSmiling = null;
        base64Close = null;
        base64Afar = null;
        flowClose();

    }

    private boolean verifySession () {
        if(SESSION == 3) {
            currentFlow = Flow.SMILE_VALIDATE_ERROR;
            if(isCommingVerificationFromSpoofing) {
                return  false;
            }
            createTimer(); // Do request
            return false;
        }
        return true;
    }

    // Layout mask
    private void insertMask(BioMaskViewHomolog.MaskType maskType) {

        this.maskType = maskType;

        if(bioMaskView != null) {
            ((ViewGroup) bioMaskView.getParent()).removeView(bioMaskView);
            bioMaskView = null;
        }

        if(maskType == BioMaskViewHomolog.MaskType.CLOSE) {
            bioMaskView = new BioMaskViewHomolog(this, BioMaskViewHomolog.MaskType.CLOSE, this);
        }else{
            bioMaskView = new BioMaskViewHomolog(this, BioMaskViewHomolog.MaskType.AFAR, this, previewSize, aspectRatioRelative);
        }

        addContentView(bioMaskView);

    }

    private void insertMask(BioMaskViewHomolog.MaskType maskType, @ColorInt Integer  color) {

        currentColorBorder = color;

        if(bioMaskView != null) {
            ((ViewGroup) bioMaskView.getParent()).removeView(bioMaskView);
            bioMaskView = null;
        }

        bioMaskView = new BioMaskViewHomolog(this, maskType, this);
        bioMaskSilhouette = new BioMaskSilhouetteHomolog(this, bioMaskView, maskType, color);

        addContentView(bioMaskView);
    }

    private void insertSillhoutte(@ColorInt Integer color) {

        if(bioMaskSilhouette != null) {
            ((ViewGroup) bioMaskSilhouette.getParent()).removeView(bioMaskSilhouette);
            bioMaskSilhouette = null;
        }
        bioMaskSilhouette = new BioMaskSilhouetteHomolog(this, bioMaskView, this.maskType, color);
        addContentView(bioMaskSilhouette);

    }

    public void insertTvStatus() {

        if(tvStatus != null) {
            ((ViewGroup) tvStatus.getParent()).removeView(tvStatus);
            tvStatus = null;
        }

        tvStatus = new TextView(this);

        int widthTvStatus = (getWidthPixels() - dpToPx(120));

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                widthTvStatus, // Width of TextView
                RelativeLayout.LayoutParams.WRAP_CONTENT); // Height of TextView
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        tvStatus.setLayoutParams(lp);
        tvStatus.setPadding(0, dpToPx(10), 0, dpToPx(10));
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
        addContentView(tvStatus, lp);

    }

    private void fireFlash(){
        viewFlash = new View(this);
        RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        viewFlash.setLayoutParams(rp);
        viewFlash.setBackgroundColor(Color.WHITE);
        viewFlash.setAlpha((float) 0.8);
        addContentView(viewFlash, rp);
    }

    private void addContentView(View view){
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        addContentView(view, lp);
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

                                // --------  SMILE AND EYES VALIDATION  --------
                                smilingProbability = firebaseVisionFace.getSmilingProbability();
                                leftEyeOpenProbability = firebaseVisionFace.getLeftEyeOpenProbability();
                                rightEyeOpenProbability = firebaseVisionFace.getRightEyeOpenProbability();

                                if(DEBUG) {
                                    Log.d(TAG, "L_EYE_P: " + leftEyeOpenProbability);
                                    Log.d(TAG, "R_EYE_P: " + rightEyeOpenProbability);
                                    Log.d(TAG, "SMLIG_P: " + smilingProbability);
                                }

                                arrLeftEyeOpenProbability.add(leftEyeOpenProbability);

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
                                      Log.d(TAG, "Olho direito (X): " + rightEyePosX);
                                      Log.d(TAG, "Linha vertical esquerda: " + posVerticalLineLeft);
                                      Log.d(TAG, "Linha vertical direita: " + posVerticalLineRight);
                                      Log.d(TAG, "Atura dos olhos (Y): " + leftEyePosY);
                                      // Log.d(TAG, "Olho direito (Y): " + rightEyePosY);
                                      Log.d(TAG, "Linha horizontal acima: " + posHorizontalLineTop);
                                      Log.d(TAG, "Linha horizontal abaixo: " + posHorizontalLineBottom);
                                  }


                                if(showLines) {
                                    addHorizontalLineBottom(posHorizontalLineBottom * aspectRatioRelative);
                                    addHorizontalLineTop(posHorizontalLineTop * aspectRatioRelative);
                                    addVerticalLineLeft(posVerticalLineLeft * aspectRatioRelative);
                                    addVerticalLineRight(posVerticalLineRight * aspectRatioRelative);
                                }

                                // Olhos fora do enquadramento na horizontal
                                if (leftEyePosX < posVerticalLineLeft || rightEyePosX   > posVerticalLineRight) {
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
                                    System.out.println("ROSTO PROXIMO");
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

//                            if (showLines) {
//                                addHorizontalLineBottom(posHorizontalLineBottom * aspectRatioRelative);
//                                addHorizontalLineTop((posHorizontalLineTop * aspectRatioRelative));
//                                addVerticalLineLeft(posVerticalLineLeft * aspectRatioRelative);
//                                addVerticalLineRight(posVerticalLineRight * aspectRatioRelative);

//                            }

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

    private void destroyTimerUserBehavior () {
        if (countDownUserBehavior != null) {
            countDownSmileStarted = false;
            countDownUserBehavior.cancel();
            countDownUserBehavior = null;
        }
    }

    private void createTimerWaitingUserBehavior () {

            long secondsWaiting = 4;
            countDownUserBehavior = new CountDownTimer((secondsWaiting * 1000), 1000) {

                public void onTick(long millisUntilFinished) {
                   // countDownCancelled[0] = Boolean.FALSE;
                }

                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                public void onFinish() {
                    destroyTimer();
                    destroyTimerUserBehavior();
                    currentFlow = Flow.RESET;
                    changeTheFlow();
                }

            };
        countDownUserBehavior.start();

    }

    private void createTimer () {

        if (countDownTimer == null && isRequestImage == false) {

            // Estipulo o tempo de espera do timer. Se for o processo de sorriso, faço a captura de imediato.
            long waitingTime;
            if(currentFlow == Flow.SMILE) {
                waitingTime = 600;
            }else{
                waitingTime = 1000;
            }

            countDownTimer = new CountDownTimer(waitingTime, 1000) {

                public void onTick(long millisUntilFinished) {
                    countDownCancelled[0] = Boolean.FALSE;
                }

                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                public void onFinish() {
                    if (!countDownCancelled[0]) {
                        //isRunning = false;
                        isRequestImage = true;
                        destroyTimer();

                        if(currentFlow == Flow.CLOSE || currentFlow == Flow.SMILE || currentFlow == Flow.SMILE_VALIDATE_ERROR) {
                            fireFlash();
                            takePicture();
                        }else if (currentFlow == Flow.AFAR) {
                            changeTheFlow();
                        }

                    }
                }

            };
            countDownTimer.start();
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

            //
            if(currentFlow == Flow.SMILE) {
                tvStatus.setTextColor(colorGreen);
                if(isEnterSmiling) {
                    tvStatus.setText(getString(R.string.status_stop_smile));
                }else{
                    tvStatus.setText(getString(R.string.status_smile));
                }
            }else{
                tvStatus.setText(getString(R.string.status_success));
            }

            // insertMask(MaskView.MaskType.CLOSE, ResourcesCompat.getColor(getResources(), R.color.colorGreenMaskBorder, null));
        }

        if (autoCapture && countRegressive && !countDownCancelled[0]) {

            if(currentFlow ==  Flow.SMILE) {

                if(!countDownSmileStarted) {
                    countDownSmileStarted = true;
                    createTimerWaitingUserBehavior();
                }
                if(isEnterSmiling) {
                    if(!userIsSmilling()) {
                        isSmilingApproved = true;
                        createTimer();
                    }
                }else{
                    if(userIsSmilling()) {
                        isSmilingApproved = true;
                        createTimer();
                    }
                }


            }else{
                createTimer();
            }
        }
        else if (autoCapture) {
            autoCapture();
        }

        int size = 18;
        if (screenWidth > 1600) {
            size = 34;
        }

    }

    /**
     * @return Este metodo retorna true se a face em análise estiver sorrindo.
     */
    private boolean userIsSmilling () {
        return smilingProbability > 0.9;
    }

    /**
     * @return Este metodo retorna true se a face em análise estiver piscando.
     */
    private boolean userIsBlinking () {

        float[] floatBlink = new float[arrLeftEyeOpenProbability.size()];
        int i = 0;

        for (Float f : arrLeftEyeOpenProbability) {
            floatBlink[i++] = (f != null ? f : Float.NaN); // Or whatever default you want.
        }
        Arrays.sort(floatBlink, 0, floatBlink.length);

        if(floatBlink[floatBlink.length - 1] > 0.8f &&
                floatBlink[0] < 0.5f && floatBlink[1] < 0.5f){
            return true;
        }else{
            return false;
        }

    }



    private void markRed() {

        destroyTimer();
        if(currentFlow == Flow.SMILE) {
            destroyTimerUserBehavior();
        }

        if(currentColorBorder !=  Color.WHITE) {
            currentColorBorder = Color.WHITE;
            insertSillhoutte(Color.WHITE);
            tvStatus.setText(getString(R.string.status_error));
            if(currentFlow == Flow.SMILE) {
                tvStatus.setTextColor(primaryColor);
            }
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

            float topOffet = (refHeight * ((percentOffsetVerticalRange) / 100));
            float heightRange = (refHeight * ((percentVerticalRange) / 100));

            // densidade
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            densityFactor = metrics.density;

            posVerticalLineLeft = (refWidth * (percentHorizontalRange / 100));
            // posVerticalLineRight = (refWidth * ((percentHorizontalRange / 100) * 3)); - old version
            posVerticalLineRight = (refWidth * ((percentHorizontalRange / 100) * 4));
            posHorizontalLineTop = topOffet;
            posHorizontalLineBottom = topOffet + heightRange;
            initialized = true;
        }
    }

    private void addHorizontalLineBottom(float bottom) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int)(screenWidth), 10);
        layoutParams.setMargins(0,(int)bottom,0,0);
        lineBottomView.setLayoutParams(layoutParams);
    }

    private void addHorizontalLineTop(float top) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int)(screenWidth), 10);
        layoutParams.setMargins(0,(int)top,0,0);
        lineTopView.setLayoutParams(layoutParams);
    }

    private void addVerticalLineLeft(float left) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(10, (int)(screenHeight));
        layoutParams.setMargins((int)left,0,0,0);
        lineLeftView.setLayoutParams(layoutParams);
    }

    private void addVerticalLineRight(float right) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(10, (int)(screenHeight));
        layoutParams.setMargins((int)right,0,0,0);
        lineRightView.setLayoutParams(layoutParams);
    }


    @Override
    public void onClick(View view) {

    }

    // this method changed the flow of proccess liveness.
    private void changeTheFlow() {

        isChangeFlow = true;

        if(viewFlash != null) {
            ((ViewGroup) viewFlash.getParent()).removeView(viewFlash);
            viewFlash = null;
        }

        switch (currentFlow){

            case CLOSE:
                currentFlow  = Flow.AFAR;
                currentColorBorder = Color.WHITE;
                flowAfar();
                break;
            case AFAR:
                isRequestImage = false;
                currentFlow = Flow.SMILE;
                currentColorBorder = Color.WHITE;
                flowSmile();
                break;
            case RESET:
                resetSession();
                break;
                case SMILE:
                break;
            default:
        }

    }

    private void flowClose() {
     //   showLines = true;
        insertMask(BioMaskViewHomolog.MaskType.CLOSE);
        insertSillhoutte(Color.WHITE);
        insertTvStatus();
    }

    private void flowAfar(){
     //   showLines = true;
        insertMask(BioMaskViewHomolog.MaskType.AFAR);
        insertSillhoutte(Color.WHITE);
        insertTvStatus();
        reopenCamera();
    }

    public void setParamsBio (RectF rectF) {
        if(isChangeFlow){
            isChangeFlow = false;
            this.rectMask = rectF;
            if(currentFlow == Flow.CLOSE) {
                paramsBioClose();
            }else{
                paramsBioAfar();
            }
        }
    }

    private void paramsBioClose(){

        // This condition verify wether the flow is close and different of smile error. Then the startTime is initialized.
        if(currentFlow == Flow.CLOSE) {
            if(startDateOfProcess == null) {
                startDateOfProcess = Calendar.getInstance().getTime();
            }
        }

        minDiffEye = 160f;
        maxDiffEye = 240f;
//        posVerticalLineLeft = this.rectMask.left - 200;
//        posVerticalLineRight = this.rectMask.right - 200;
//        posHorizontalLineTop = this.rectMask.top + 200f;
//        posHorizontalLineBottom = this.rectMask.bottom + 200f;

        posVerticalLineLeft = this.rectMask.left / this.aspectRatioRelative;
        posVerticalLineRight = this.rectMask.right  / this.aspectRatioRelative;
        posHorizontalLineTop = (this.rectMask.top  / this.aspectRatioRelative) + 200; //This 200px compensation occurs because we restrict the eye area on the top.
        posHorizontalLineBottom = (this.rectMask.bottom  / this.aspectRatioRelative) - 200; //This -200px compensation occurs because we restrict the eye area on the bottom.


    }

    private void paramsBioAfar(){

        markRed();
        minDiffEye = 80f;
        maxDiffEye = 160f;

        int sWidht = previewSize.getHeight(); // invertido widht com height

        // Dividimos todos pelo aspect ratio da tela por conta que o olho já esta levando em consideração isto.
        posVerticalLineLeft = this.rectMask.left / this.aspectRatioRelative;
        posVerticalLineRight = this.rectMask.right  / this.aspectRatioRelative;
        posHorizontalLineTop = this.rectMask.top  / this.aspectRatioRelative;
        posHorizontalLineBottom = this.rectMask.bottom  / this.aspectRatioRelative;
        // No de longe eu vejo a linha horizontal de baixo e diminuo com o 1/3 da tela (o tamanho do frame do rosto).



    }


    private void flowSmile() {

        isEnterSmiling = userIsSmilling();

        if(isEnterSmiling) {
            tvStatus.setText(getString(R.string.status_stop_smile));
        }else{
            tvStatus.setText(getString(R.string.status_smile));
        }

        tvStatus.setTextColor( ResourcesCompat.getColor(getResources(), R.color.colorGreenMaskBorder, null));
    }

    @Override
    public void capture(String base64) {

    }

    @Override
    public void capture(String base64, Bitmap bitmap) {

        if (base64 != null) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if(currentFlow == Flow.CLOSE) {

                        bitmapClose = bitmap;
                        base64Close = base64;
                        changeTheFlow();

                    }else if(currentFlow == Flow.SMILE || currentFlow == Flow.SMILE_VALIDATE_ERROR) {

                        destroyTimerUserBehavior();

                        bitmapAfarSmiling = bitmap;
                        base64Afar = base64;

                        TFBioReader tfBioReader = new  TFBioReader(SelfieActivityHomolog.this, CLOSE_MODEL, AWAY_MODEL);
                        Map<String, Float> mConfidenceClose = tfBioReader.processImage(bitmapClose);
                        Map<String, Float> mConfidenceAfar = tfBioReader.processImage(bitmapAfarSmiling);

                        isUserBlinkingAprroved =   userIsBlinking();

                        BioLivenessValidateHomolog bioLivenessValidate = new BioLivenessValidateHomolog(mConfidenceClose, mConfidenceAfar, isSmilingApproved, isUserBlinkingAprroved, startDateOfProcess);
                        resultLiveness = bioLivenessValidate.getLivenessResultDescription();

                        if(resultLiveness.get("isLiveness").equals("0")) {
                            if(verifySession()) {

                                spoofingReset ++;
                                isCommingVerificationFromSpoofing = true;
                                isRequestImage = false;
                                destroyTimer();
                                destroyTimerUserBehavior();
                                currentFlow = Flow.RESET;
                                changeTheFlow();
                                reopenCamera();

                                return;
                            }
                        }

                        sendBilling(resultLiveness.get("isLiveness"));
                        createProcess();
                        // sendRequestLiveness(resultLiveness);

                        Boolean IsLiveness = "1".equals(resultLiveness.get("isLiveness"));

                        Bitmap bitClose = Bitmap.createScaledBitmap(bitmapClose, 200, 280, false);
                        Bitmap bitAfar = Bitmap.createScaledBitmap(bitmapAfarSmiling, 200, 280, false);

                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitClose.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                        byte[] byteArrayClose = byteArrayOutputStream .toByteArray();

                        ByteArrayOutputStream byteArrayOutputStreamAfar = new ByteArrayOutputStream();
                        bitAfar.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStreamAfar);
                        byte[] byteArrayAfar = byteArrayOutputStreamAfar.toByteArray();


                        HashMap<String, String> callBackResult = new HashMap<>();
                        callBackResult.put("isLiveness", resultLiveness.get("isLiveness"));
                        callBackResult.put("base64", Base64.encodeToString(byteArrayClose, Base64.DEFAULT));
                        callBackResult.put("base64Close", Base64.encodeToString(byteArrayAfar, Base64.DEFAULT));

                        Intent resultIntent = new Intent();
                        resultIntent.putExtra(LivenessXHomolog.RESULT_OK, callBackResult);
                        SelfieActivityHomolog.this.setResult(Activity.RESULT_OK, resultIntent);
                        finish();

                    }

                    isRequestImage = false; // Added this line in the liveness branch. Before, this line was just after the faceInset request or in ErrorMethod.

                }
            });


        } else {
            showErrorMessage("Erro ao recuperar imagem capturada");
        }
    }


    private void sendBilling (String isLiveness) {

        String suuid = UUID.randomUUID().toString();

        LivenessBillingRequest request = new LivenessBillingRequest();
        request.setId(suuid);
        request.setStatus(isLiveness);

        // Face Insert  ----------------------
        ServiceGenerator
                .createService(BioService.class)
                .livenessBilling(request)
                .enqueue(new Callback<LivenessBillingResponse>() {

                    @Override
                    public void onResponse(Call<LivenessBillingResponse> call, Response<LivenessBillingResponse> response) {
                        LivenessBillingResponse body = response.body();

                        isRequestImage = false;

                        if (body != null && body.isValid()) {

                        }
                        else {

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
                    public void onFailure(Call<LivenessBillingResponse> call, Throwable t) {
                        if (DEBUG) Log.d(TAG, "ERRO: " + t.toString());
                        dialog.dismiss();
                        dialog.dismiss();

                    }
                });


    }


    private void createProcess () {

        CreateProcessRequest request = new CreateProcessRequest();
        Subject subject = new Subject();
        subject.setCode(verifyNullStringRequest(Hawk.get(SharedKey.USER_DOCUMENT)));
        subject.setName(verifyNullStringRequest(Hawk.get(SharedKey.USER_NAME)));
        request.setSubject(subject);

        ServiceGenerator
                .createService(BioService.class, false, true, ServiceGenerator.API_BASE_URL_HML)
                .createProcess("1", request)
                .enqueue(new Callback<CreateProcessResponse>() {

                    @Override
                    public void onResponse(Call<CreateProcessResponse> call, Response<CreateProcessResponse> response) {
                        CreateProcessResponse body = response.body();

                        if (body != null && body.isValid()) {

                            String processId = body.getCreateProcessResult().getProcess().getId();
                            faceInsertClose(base64Close, processId);

                        } else {

                            try {
                                JSONObject j = new JSONObject(response.errorBody().string());
                                JSONObject error = j.getJSONObject("Error");
                                String message = error.getString("Description");

                                new SweetAlertDialog(SelfieActivityHomolog.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("Ops!")
                                        .setContentText(message)
                                        .setConfirmText("Entendi")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {

                                                sDialog.dismissWithAnimation();

                                            }
                                        }).show();

                            } catch (Exception ex) {

                             //   if (!showSnackbarError(response.message().toString())) {
                                    Log.d("SELFIE ACTIVITY HOMOLOG", body != null ? body.getMessageError() : "Erro ao criar registro");
                              //  }

                                Log.d(TAG, ex.getMessage());
                            }


                        }
                    }

                    @Override
                    public void onFailure(Call<CreateProcessResponse> call, Throwable t) {
                        Log.d(TAG, t.getMessage());
                        showSnackbarError(t.getMessage());
                    }
                });

    }

    private void sendRequestLiveness () {

        Float Score = Float.valueOf(resultLiveness.get("Score"));
        Boolean IsLiveness = "1".equals(resultLiveness.get("isLiveness"));
        Boolean LivenessClose = "1".equals(resultLiveness.get("isLiveClose"));
        Boolean LivenessAway = "1".equals(resultLiveness.get("isLiveAway"));
        Float ScoreClose = Float.valueOf(resultLiveness.get("ScoreClose"));
        Float ScoreAway = Float.valueOf(resultLiveness.get("ScoreAway"));
        Integer Time = Integer.valueOf(resultLiveness.get("Time"));
        Boolean IsBlinking = isUserBlinkingAprroved;

        String username = Hawk.get(SharedKey.USER_NAME);
        String document = Hawk.get(SharedKey.USER_DOCUMENT);
        String devicemodelname = getDeviceName();

        boolean isResetSession = false;
        if(SESSION > 0) {
            isResetSession = true;
        }

        boolean isSpoofingReset = false;
        if(spoofingReset > 0) {
            isSpoofingReset = true;
        }

        LivenessRequest requestLiveness = new LivenessRequest();
        requestLiveness.setUserName(verifyNullStringRequest(username));
        requestLiveness.setUserCPF(verifyNullStringRequest(document));
        requestLiveness.setScore(Score);
        requestLiveness.setIsLIve(IsLiveness);
        requestLiveness.setScoreClose(ScoreClose);
        requestLiveness.setIsLiveClose(LivenessClose);
        requestLiveness.setScoreAway(ScoreAway);
        requestLiveness.setIsLiveAway(LivenessAway);
        requestLiveness.setIsBlinking(IsBlinking);
        requestLiveness.setIsSmilling(isSmilingApproved);
        requestLiveness.setDeviceModel(verifyNullStringRequest(devicemodelname));
        requestLiveness.setBase64Center(base64Close);
        requestLiveness.setBase64Away(base64Afar);
        requestLiveness.setIsResetSession(isResetSession);
        requestLiveness.setAttemptsValidate(1);
        requestLiveness.setIsResetSessionSpoofing(isSpoofingReset);
        requestLiveness.setAttemptsSpoofing(spoofingReset);
        requestLiveness.setTimeTotal(Time);
        requestLiveness.setBiometryMessage(biometryMessageClose);
        requestLiveness.setBiometryStatus(biometryCodeClose);
        requestLiveness.setBiometryMessageAway(biometryMessageAfar);
        requestLiveness.setBiometryStatusAway(biometryCodeAfar);

        LivenessRequestSample livenessRequestSample = new LivenessRequestSample();
        livenessRequestSample.setLiveness(requestLiveness);

        ServiceGenerator
                .createService(BioService.class, false, true, "https://crediariohomolog.acesso.io/blackpanther/services/v2/credService.svc/")
                .liveness("24036E73-64A1-498E-8824-67BC99F81AB3", livenessRequestSample)
                .enqueue(new Callback<LivenessResponse>() {

                    @Override
                    public void onResponse(Call<LivenessResponse> call, Response<LivenessResponse> response) {
                        LivenessResponse body = response.body();

                        isRequestImage = false;

                        if (body != null && body.isValid()) {

                        }
                        else {

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
                    public void onFailure(Call<LivenessResponse> call, Throwable t) {
                        if (DEBUG) Log.d(TAG, "ERRO: " + t.toString());
                        dialog.dismiss();
                        dialog.dismiss();

                    }
                });

    }

    private String verifyNullStringRequest (String string) {

        if(string == null || string.length() == 0) {
            string = "NÃO INFORMADO";
        }

        return string;
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



    private void faceInsertClose(String base64, String processId) {

        FaceInsertRequest request = new FaceInsertRequest();
        request.setImagebase64(base64);
        request.setValidateLiveness(true);

        // Face Insert  ----------------------
        ServiceGenerator
                .createService(BioService.class, false, true, ServiceGenerator.API_BASE_URL_HML)
                .faceInsert(processId, request)
                .enqueue(new Callback<FaceInsertResponse>() {

                    @Override
                    public void onResponse(Call<FaceInsertResponse> call, Response<FaceInsertResponse> response) {

                        FaceInsertResponse body = response.body();

                        if (body != null && body.isValid()) {
                        }
                        else {

                            String message = getErrorMessage(response);
                            Integer code = getResponseCode(response);
                            biometryCodeClose = code;

                            if (message != null) {
                                //showToastMessage(message);
                                     biometryMessageClose = message;
                            }else{
                                showToastMessage("Face não autenticada");
                            }

                        }

                        faceInsertAfar(base64Afar, processId);


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

    private void faceInsertAfar(String base64, String processId) {

        FaceInsertRequest request = new FaceInsertRequest();
        request.setImagebase64(base64);
        request.setValidateLiveness(true);

        // Face Insert  ----------------------
        ServiceGenerator
                .createService(BioService.class, false, true, ServiceGenerator.API_BASE_URL_HML)
                .faceInsert(processId, request)
                .enqueue(new Callback<FaceInsertResponse>() {

                    @Override
                    public void onResponse(Call<FaceInsertResponse> call, Response<FaceInsertResponse> response) {

                        FaceInsertResponse body = response.body();

                        if (body != null && body.isValid()) {


                        }
                        else {

                            String message = getErrorMessage(response);
                            Integer code = getResponseCode(response);
                            biometryCodeAfar = code;

                            if (message != null) {
                                //showToastMessage(message);
                                biometryMessageAfar = message;

                            }else{
                                showToastMessage("Face não autenticada");

                            }

                        }

                        sendRequestLiveness();

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


    /** Returns the consumer friendly device name */
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }

}