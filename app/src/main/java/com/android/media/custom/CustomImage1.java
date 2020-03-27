package com.android.media.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.android.media.R;

/**
 * https://blog.csdn.net/smile_running/article/details/81507191
 */

public class CustomImage1 extends View {


    private Paint mPaint;

    private Drawable mDrawable;

    private int mHeight;

    private int mWidth;


    public CustomImage1(Context context) {
        super(context);
    }

    public CustomImage1(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();

        mPaint.setAntiAlias(true);
        initAttr(attrs);
    }

    // 该构造不能正常显示图片

//    public CustomImage1(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//
//
//
//    }

    @SuppressWarnings("Recycle")
    private void initAttr(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = null;

            try {
                array = getContext().obtainStyledAttributes(attrs, R.styleable.CustomImage1);
                //根据图片id获取到drawable对象
                mDrawable = array.getDrawable(R.styleable.CustomImage1_src);

                if (mDrawable == null) {
                    throw new NullPointerException("drawable is null");
                }
                //获取 图片固定的宽高
                mHeight = mDrawable.getIntrinsicHeight();
                mWidth = mDrawable.getIntrinsicWidth();


            } finally {
                if (array != null) {
                    array.recycle();
                }
            }
        }


    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDrawable == null) {
            System.err.println("Drawable is null ");
            return;
        }

        canvas.drawBitmap(drawableToBitmap(mDrawable), getLeft(),
                getTop(),
                mPaint);


    }

    /**
     * 将Drawable 转换为 bitmap
     */

    private Bitmap drawableToBitmap(Drawable drawable) {
        //根据获得的drawable的透明度/不透明度，来设置Bitmap配置
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(mWidth, mHeight, config);
        /**
         * 在onDraw方法中调用canvas.drawBitmap()时需要构造一个绘制位图的画布，否则canvas绘制的位图将不显示，变成黑色
         */
        Canvas canvas = new Canvas(bitmap);
        //为可绘制的图形指定一个边框，大小为图片大固定宽和高
        drawable.setBounds(0, 0, mWidth, mHeight);
        //调用此方法绘制，绘制setBounds（）方法指定大小的边界
        drawable.draw(canvas);
        return bitmap;

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //测量View 的尺寸， 将view的宽和高设置为图片的宽和高
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));

    }

    private int measureWidth(int widthMeasureSpec) {
        //获取宽度的模式和大小
        int measureMode = MeasureSpec.getMode(widthMeasureSpec);
        int measureSize = MeasureSpec.getSize(widthMeasureSpec);
        switch (measureMode) {
            case MeasureSpec.UNSPECIFIED://表示将视图可以设置任意大小，无限制一般不常用
            case MeasureSpec.AT_MOST: //wrap_content 模式
                break;

            case MeasureSpec.EXACTLY: // mathparent，具体数值
                mWidth = measureSize;
                break;

        }


        return mWidth;
    }

    private int measureHeight(int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        switch (heightMode) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                break;

            case MeasureSpec.EXACTLY:
                mHeight = heightSize;
                break;
        }


        return mHeight;
    }
}
