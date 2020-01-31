package com.acesso.acessobiosample.support;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;

public class MaskSilhouette extends View {

    public Paint pStatus = new Paint();
    private MaskView maskView;
    private MaskView.MaskType maskType;

    @ColorInt
    public Integer  colorBorder;

    public MaskSilhouette(Context context) {
        super(context);
    }

    public MaskSilhouette(Context context, MaskView maskView, MaskView.MaskType maskType ,@ColorInt Integer  color) {
        super(context);
        this.maskView = maskView;
        this.maskType = maskType;
        this.colorBorder = color;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(colorBorder == null) {
            pStatus.setColor(Color.WHITE);
        }else{
            pStatus.setColor(colorBorder);
        }

        pStatus.setStyle(Paint.Style.STROKE);
        pStatus.setStrokeWidth(10f);

        if(this.maskType == MaskView.MaskType.CLOSE) {
            canvas.drawRoundRect(maskView.maskClose(canvas),(maskView.maskClose(canvas).right / 2), (maskView.maskClose(canvas).right / 2), pStatus);
        }else{
            canvas.drawRoundRect(maskView.maskAfar(canvas),(maskView.maskAfar(canvas).right / 2), (maskView.maskAfar(canvas).right / 2), pStatus);
        }

    }

}
