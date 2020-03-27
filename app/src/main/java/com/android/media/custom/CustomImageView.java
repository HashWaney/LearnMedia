package com.android.media.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.android.media.R;

public class CustomImageView extends View {


    private int mWidth; // 自定义View 的宽

    private int mHeight;// 自定义View 的高

    private Bitmap mBitmap;//自定义View 的图片

    private int mImageScale;//图片缩放模式

    public static final int IMG_SCALE_FIT_XY = 0;
    public static final int IMG_SCALE_CENTER = 1;

    private String mTitle;//图片标题

    private int mTextColor;//字体颜色

    private int mTextSize;// 字体大小

    private Paint mPaint;

    private Rect mTextBound;//文本绘制范围

    private Rect allBound;//绘制的整个矩形范围


    public CustomImageView(Context context) {
        this(context, null);
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomImageView);
        mTitle = typedArray.getString(R.styleable.CustomImageView_text);
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.CustomImageView_textSize, 16);

        mTextColor = typedArray.getColor(R.styleable.CustomImageView_textColor, Color.BLACK);




        mBitmap = adapterHighVersionBitmapCreate(context, typedArray.getDrawable(R.styleable.CustomImageView_srcs));

        Log.e(CustomImageView.class.getSimpleName(), " Bitmap: " + mBitmap);

        mImageScale = typedArray.getInt(R.styleable.CustomImageView_imageScaleType, 0);
        //回收
        typedArray.recycle();

        initDrawTool();
    }


    public Bitmap adapterHighVersionBitmapCreate(Context context, Drawable drawable) {

        if (drawable == null) {
            return null;
        }

        //根据获得的drawable的透明度/不透明度，来设置Bitmap配置
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), config);
        /**
         * 在onDraw方法中调用canvas.drawBitmap()时需要构造一个绘制位图的画布，否则canvas绘制的位图将不显示，变成黑色
         */
        Canvas canvas = new Canvas(bitmap);
        //为可绘制的图形指定一个边框，大小为图片大固定宽和高
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        //调用此方法绘制，绘制setBounds（）方法指定大小的边界
        drawable.draw(canvas);
        return bitmap;
    }

    private void initDrawTool() {
        allBound = new Rect();
        mPaint = new Paint();
        mTextBound = new Rect();
        mPaint.setTextSize(mTextSize);
        //计算描绘字体需要的范围
        mPaint.getTextBounds(mTitle, 0, mTitle.length(), mTextBound);


    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //1。 设置自定义View的宽度
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);

        if (specMode == MeasureSpec.EXACTLY) { //match_parent .100dp
            mWidth = specSize;
        } else {
            //自定义View 的宽度 由左右填充和图片宽度决定
            int desireImgWidth = getPaddingLeft() + getPaddingRight() + mBitmap.getHeight();
            //自定义View的宽度 由左右填充和标题的宽度决定
            int desireTitleWidth = getPaddingLeft() + getPaddingRight() + mTextBound.width();

            if (specMode == MeasureSpec.AT_MOST) { //wrap_content

                mWidth = Math.min(Math.max(desireImgWidth, desireTitleWidth), specSize);
            }

        }


        //2。设置自定义View的高度
        specMode = MeasureSpec.getMode(heightMeasureSpec);
        specSize = MeasureSpec.getSize(heightMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) { //100 dp (明确值） match_parent
            mHeight = specSize;

        } else {
            //由上下填充，图片高度和字体绘制高度决定
            int desireHeight = getPaddingTop() + getPaddingBottom() +
                    mBitmap.getHeight() + mTextBound.height();

            if (specMode == MeasureSpec.AT_MOST) { //wrap_content

                mHeight = Math.min(desireHeight, specSize);
            }
        }


        //3.指定控件大小
        setMeasuredDimension(mWidth, mHeight);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /**
         * 1。画一个边框，为边框加一些属性
         */
        mPaint.setStrokeWidth(4);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.YELLOW);
        canvas.drawRect(0, 0, getMeasuredWidth(),
                getMeasuredHeight(), mPaint);

        /**
         * 2。绘制文本
         *
         */

        mPaint.setColor(mTextColor);
        mPaint.setStyle(Paint.Style.FILL);
        //文本宽度大于设置的宽度，字体设置为xxx...
        if (mTextBound.width() > mWidth) {
            TextPaint textPaint = new TextPaint(mPaint);
            String msg = TextUtils.ellipsize(mTitle, textPaint,
                    (float) mWidth - getPaddingLeft() - getPaddingRight(),
                    TextUtils.TruncateAt.END).toString();
            canvas.drawText(msg, getPaddingLeft(), getPaddingTop(), mPaint);

        } else {
            //居中显示
            canvas.drawText(mTitle, mWidth / 2, getPaddingTop(), mPaint);

        }

        /**
         * 3.设置图像的位置 allBound 减去 TextBound所用的范围就是Image图片显示的范围
         *
         */
        allBound.left = getPaddingLeft();
        allBound.right = mWidth - getPaddingRight();
        allBound.top = getPaddingTop() - mTextBound.height();
        allBound.bottom = mHeight - getPaddingBottom();

        if (mImageScale == IMG_SCALE_FIT_XY) {
            Log.i(CustomImageView.class.getSimpleName(), ".....绘制图片1");
            canvas.drawBitmap(mBitmap, null, allBound, mPaint);
        } else {
            Log.i(CustomImageView.class.getSimpleName(), ".....绘制图片2");
            //计算居中的范围
            allBound.left = (mWidth - mBitmap.getWidth()) / 2;
            allBound.right = (mWidth + mBitmap.getWidth()) / 2;
            allBound.top = (mHeight - mTextBound.height() / 2) - mBitmap.getHeight() / 2;

            allBound.bottom = (mHeight - mTextBound.height() / 2) + mBitmap.getHeight() / 2;
            canvas.drawBitmap(mBitmap, null, allBound, mPaint);

        }

    }


}
