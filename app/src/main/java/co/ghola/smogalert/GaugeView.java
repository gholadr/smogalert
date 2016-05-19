package co.ghola.smogalert;


import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

public class GaugeView extends View {


    private float radius;

    Paint paint;
    Paint shadowPaint;

    Path myPath;
    Path shadowPath;

    RectF outterCircle;
    RectF innerCircle;
    RectF shadowRectF;

    private String text;

    public GaugeView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.GaugeView,
                0, 0
        );

        try {
            radius = a.getDimension(R.styleable.GaugeView_radius, 20.0f);
        } finally {
            a.recycle();
        }

        paint = new Paint();
        paint.setDither(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(radius / 14.0f);

        shadowPaint = new Paint();
        shadowPaint.setColor(0xf0000000);
        shadowPaint.setStyle(Paint.Style.STROKE);
        shadowPaint.setAntiAlias(true);
        shadowPaint.setStrokeWidth(6.0f);
        shadowPaint.setMaskFilter(new BlurMaskFilter(4, BlurMaskFilter.Blur.SOLID));


        myPath = new Path();
        shadowPath = new Path();


        outterCircle = new RectF();
        innerCircle = new RectF();
        shadowRectF = new RectF();

        float adjust = (.019f*radius);
        shadowRectF.set(adjust, adjust, radius*2-adjust, radius*2-adjust);

        adjust = .038f * radius;
        outterCircle.set(adjust, adjust, radius*2-adjust, radius*2-adjust);

        adjust = .276f * radius;
        innerCircle.set(adjust, adjust, radius*2-adjust, radius*2-adjust);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        // draw shadow
        paint.setShader(null);
        float adjust = (.0095f*radius);
        paint.setShadowLayer(8, adjust, -adjust, 0xaa000000);
        drawDonut(canvas,paint, 135, 270);


        // colorAccent
        int color = getResources().getColor(R.color.colorAccent);;
        setGradient(color,color);
        drawDonut(canvas,paint, 135,270);

//        adjust = .119f * radius;
//        innerCircle.set(adjust, adjust, radius*2-adjust, radius*2-adjust);

        // red
        setGradient(0xffff5c33,0xffff3300);
        drawDonut(canvas,paint, 135,(float)(Integer.valueOf(text)*1.35)); //corresponds to 150 AQI

        //draw text
        drawText2(canvas, text);

    }

    public void drawDonut(Canvas canvas, Paint paint, float start,float sweep){

        myPath.reset();
        myPath.arcTo(outterCircle, start, sweep, false);
        myPath.arcTo(innerCircle, start+sweep, -sweep, false);
        myPath.close();
        canvas.drawPath(myPath, paint);
    }

    public void setGradient(int sColor, int eColor){
        paint.setShader(new RadialGradient(radius, radius, radius-5,
                new int[]{sColor,eColor},
                new float[]{.6f,.95f},TileMode.CLAMP) );
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int desiredWidth = (int) radius*2;
        int desiredHeight = (int) radius*2;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //70dp exact
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        }else if (widthMode == MeasureSpec.AT_MOST) {
            //wrap content
            width = Math.min(desiredWidth, widthSize);
        } else {
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = desiredHeight;
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height);
    }

    private void drawText2(final Canvas canvas, String text){
        Resources resources = getContext().getResources();
        float scale = resources.getDisplayMetrics().density;
        final Paint paint = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(0.005f);
        paint.setTypeface(Typeface.SANS_SERIF);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        // text color - #3D3D3D
        paint.setColor(Color.rgb(61, 61, 61));
        // text size in pixels
        paint.setTextSize((int) (40 * scale));
        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

        // draw text to the Canvas center
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        int x = (canvas.getWidth() - bounds.width())/2;
        int y = (canvas.getHeight() + bounds.height())/2;

        canvas.drawText(text, x, y, paint);
    }

    public void setGaugeValue(String txt){
        this.text = txt;
        this.setVisibility(View.VISIBLE);

    }

}
