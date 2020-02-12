package com.acessobio.liveness.support;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import androidx.annotation.ColorInt;

public class BioMaskSilhouette extends View {

    public Paint pStatus = new Paint();
    private BioMaskView bioMaskView;
    private BioMaskView.MaskType maskType;

    @ColorInt
    public Integer  colorBorder;

    public BioMaskSilhouette(Context context) {
        super(context);
    }

    public BioMaskSilhouette(Context context, BioMaskView bioMaskView, BioMaskView.MaskType maskType , @ColorInt Integer  color) {
        super(context);
        this.bioMaskView = bioMaskView;
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

        if(this.maskType == BioMaskView.MaskType.CLOSE) {
            canvas.drawRoundRect(bioMaskView.maskClose(canvas),(bioMaskView.maskClose(canvas).right / 2), (bioMaskView.maskClose(canvas).right / 2), pStatus);
        }else{
            canvas.drawRoundRect(bioMaskView.maskAfar(canvas),(bioMaskView.maskAfar(canvas).right / 2), (bioMaskView.maskAfar(canvas).right / 2), pStatus);
        }

    }

}
