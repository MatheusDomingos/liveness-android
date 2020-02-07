package com.acesso.acessobiosample.support;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BioLivenessValidate {

    private Map<String, Float> dictClose;
    private Map<String, Float> dictAfar;
    private float confidencePhotoCloseLive;
    private float confidencePhotoAwayLive;
    private boolean userIsSmilling;
    private boolean userIsBlinking;
    private boolean isFastProcess;

    private Date dateStartProcess;

    private float pri;

    private float score;
    private float SCORE_MINIMUM = (float) 74.0;


    public BioLivenessValidate(Map<String, Float> mConfidenceClose, Map<String, Float> mConfidenceAfar, boolean pUserIsSmilling, boolean pUserIsBlinking, Date pDateStartProcess) {
        this.dictClose = mConfidenceClose;
        this.dictAfar = mConfidenceAfar;
        this.userIsSmilling = pUserIsSmilling;
        this.userIsBlinking = pUserIsBlinking;
        this.dateStartProcess = pDateStartProcess;
        confidencePhotoCloseLive  = mConfidenceClose.get("confidencePhotoLive");
        confidencePhotoAwayLive = mConfidenceAfar.get("confidencePhotoLive");
    }

    public HashMap<String, String> getLivenessResultDescription() {

        score = 0;

        validateSpeedOfProcess();

        boolean photoCloseLive = isPhotoCLoseLive();
        boolean photoAwayLive = isPhotoAwayLive();
        String description = "";

        if (userIsSmilling) {
            score += 25;
        }else{
            description = "Usuário não completou o passo de sorriso";
        }

        if (userIsBlinking) {
            score += 25;
        }else{
            description = "Usuário não piscou";
        }


        // We check if the process is really fast, the blink check is not allowed.
        if(isFastProcess && !userIsBlinking) {
                if(photoCloseLive && confidencePhotoCloseLive > 0.8) {
                    score += 37.5;
                }

                if(photoAwayLive && confidencePhotoAwayLive >= 0.8) {
                    score += 37.5;
                }

                //Secondary calculations
                if(photoCloseLive && (confidencePhotoCloseLive < 0.8)) {
                    score += 25.0;
                }

                if(!photoCloseLive && (confidencePhotoCloseLive < 0.7)){
                    score += 25.0;
                }

            if(photoAwayLive && (confidencePhotoAwayLive < 0.8)) {
                score += 25.0;
            }

            if(!photoAwayLive && (confidencePhotoAwayLive < 0.7)) {
                score += 25.0;
            }

        }else{

            if(photoCloseLive && (confidencePhotoCloseLive >= 0.8)) {
                score += 25.0;
            }

            if(photoAwayLive && (confidencePhotoAwayLive >= 0.8)) {
                score += 25.0;
            }


            if(photoCloseLive && (confidencePhotoCloseLive < 0.8)) {
                score += 8.75;
            }

            if(!photoCloseLive && (confidencePhotoCloseLive < 0.7)) {
                score += 8.75;
            }

            if(photoAwayLive && (confidencePhotoAwayLive < 0.8)) {
                score += 8.75;
            }

            if(!photoAwayLive && (confidencePhotoAwayLive < 0.7)) {
                score += 8.75;
            }

        }

        HashMap<String, String> result = new HashMap<>();
        if(score >= SCORE_MINIMUM) {
            result.put("isLive" , "1");
        }else{
            result.put("isLive" , "0");
        }
        result.put("Score" , String.valueOf(score));
        result.put("Description" , description);

        return result;
    }

    private void validateSpeedOfProcess(){

        Date dateEndProcess = Calendar.getInstance().getTime();
        long different = dateEndProcess.getTime() - this.dateStartProcess.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedSeconds = different / secondsInMilli;

        this.isFastProcess = elapsedSeconds < 10;

    }

    private boolean isPhotoCLoseLive () {

        float confidencePhotoLive = dictClose.get("confidencePhotoLive");
        float confidencePhotoOfPhoto = dictClose.get("confidencePhotoOfPhoto");

        if(confidencePhotoLive >= 0.8 && confidencePhotoOfPhoto <= 0.5) {
            return true;
        }else return confidencePhotoLive >= confidencePhotoOfPhoto;

    }

    private boolean isPhotoAwayLive () {

        float confidencePhotoLive = dictClose.get("confidencePhotoLive");
        float confidencePhotoOfPhoto = dictClose.get("confidencePhotoOfPhoto");

        if(confidencePhotoLive >= 0.8 && confidencePhotoOfPhoto <= 0.5) {
            return true;
        }else return confidencePhotoLive >= confidencePhotoOfPhoto;

    }

}
