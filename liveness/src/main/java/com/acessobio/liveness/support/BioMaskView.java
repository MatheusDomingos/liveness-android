package com.acessobio.liveness.support;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.acessobio.liveness.activity.SelfieActivity;


public class BioMaskView extends View {
  private Paint mTransparentPaint;
  private Paint mSemiBlackPaint;
  private Path mPath = new Path();

  Canvas canvasGlobal = new Canvas();
  SelfieActivity selfieActivity;

  public enum MaskType {
      CLOSE , AFAR
  }

  public MaskType mType;

  public BioMaskView(Context context) {
    super(context);
    initPaints();
  }

  public BioMaskView(Context context, MaskType mType, SelfieActivity selfieActivity) {
    super(context);
    this.mType = mType;
    this.selfieActivity = selfieActivity;
    initPaints();
  }

  public BioMaskView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initPaints();
  }

  public BioMaskView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initPaints();
  }

  private void initPaints() {

    mSemiBlackPaint = new Paint();
   // mSemiBlackPaint.setColor(ResourcesCompat.getColor(getResources(), R.color.colorBlueMask, null)); // muda a cor do fundo. TRANSPARENT deixa em alfa.
    mSemiBlackPaint.setAlpha(0);
    mSemiBlackPaint.setStrokeWidth(10);

  }

  public RectF maskClose(Canvas canvas) {
    float x = (float) (getWidth() / 5);
    float width = canvas.getWidth() - x;
    float height =  ((float )getHeight() / 2) + 60; // (2/4 + 60) Aumentei 60 compensando a label de status e aumentando um pouco o tamanho de 2/4 ta tela.
    float y = (height / 2) ;
    RectF rectF = new RectF(x, y,width, (height + y));
    this.selfieActivity.setParamsBio(rectF);
    return  rectF;// x, y, width, height; (height + y) pois um lado achata o outro.
  }

  public RectF maskAfar(Canvas canvas) {
    float screenHeight = getHeight();
    float screenWidht = getWidth();
    float x = (float) (getWidth() / 3.5);
    float width = canvas.getWidth() - x;
    float height =  (((float )getHeight() / 3) ) + 60;
    float y = (screenHeight / 2) - (height / 2) ;
 //   float y =  (height / 2) ;
      RectF rectF = new RectF(x, y, width, (height + y));
      this.selfieActivity.setParamsBio(rectF);
    return rectF; // x, y, width, height; (height + y) pois um lado achata o outro.
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    canvasGlobal = canvas;

    mPath.reset();

    if(mType == MaskType.CLOSE) {
      mPath.addRoundRect(maskClose(canvas), (maskClose(canvas).right / 2), (maskClose(canvas).right / 2), Path.Direction.CCW);
      //mPath.addOval(maskClose(canvas), Path.Direction.CCW);
    }else{
      mPath.addRoundRect(maskAfar(canvas), (maskAfar(canvas).right / 2), (maskAfar(canvas).right / 2), Path.Direction.CCW);
      //  mPath.addOval(maskAfar(canvas), Path.Direction.CCW);
    }
    mPath.setFillType(Path.FillType.INVERSE_EVEN_ODD);

    canvas.drawPath(mPath, mSemiBlackPaint);
    canvas.clipPath(mPath);
    canvas.drawColor(Color.parseColor("#CC394A62"));

  }

 }