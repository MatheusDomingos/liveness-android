package com.acesso.acessobiosample.support;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import androidx.annotation.ColorInt;

public class BioMaskSilhouetteHomolog extends View {

    public Paint pStatus = new Paint();
    private BioMaskViewHomolog bioMaskView;
    private BioMaskViewHomolog.MaskType maskType;

    @ColorInt
    public Integer  colorBorder;

    public BioMaskSilhouetteHomolog(Context context) {
        super(context);
    }

    public BioMaskSilhouetteHomolog(Context context, BioMaskViewHomolog bioMaskView, BioMaskViewHomolog.MaskType maskType , @ColorInt Integer  color) {
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

        if(this.maskType == BioMaskViewHomolog.MaskType.CLOSE) {
            canvas.drawRoundRect(bioMaskView.maskClose(canvas),(bioMaskView.maskClose(canvas).right / 2), (bioMaskView.maskClose(canvas).right / 2), pStatus);
        }else{
            canvas.drawRoundRect(bioMaskView.maskAfar(canvas),(bioMaskView.maskAfar(canvas).right / 2), (bioMaskView.maskAfar(canvas).right / 2), pStatus);
        }

    }

}
