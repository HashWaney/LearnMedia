package com.android.view.parallaxview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * https://blog.csdn.net/smile_Running/article/details/81113561
 */
public class AliInfoListView extends ListView {


    private ImageView mImageView;

    private View headView;

    private int mImageHeight;

    private boolean isFirst = false;

    public AliInfoListView(Context context) {
        super(context);
    }

    public AliInfoListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_IDLE:
                        //listview 滚动完成
                        if (mImageView != null) {
                            if (mImageView.getHeight() > mImageHeight) {
                                startRestoreAnimation();
                                ;
                            }
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (headView != null) {
            if (!isFirst) {
                mImageView = headView.findViewById(R.id.iv);
                mImageHeight = headView.getHeight();
                isFirst = true;
            }

        }
    }

    /**
     * 当滑动的超出上下左右最大范围的回调
     * <p>
     * 当deltaY<0 表示手指向下滑，此时imageview 高度增加，并且放大效果
     * 当deltaY>0 并且此时ImageView的高度大于原始高度，手指向上滑，需要将ImageView还原
     *
     * @param deltaX         ： x方向的瞬时偏移量，左边到头，向右拉为负，右边到头，向左拉为正
     * @param deltaY         ： y方向的瞬时偏移量，顶部到头，向下拉为负，底部到头，向上拉为正
     * @param scrollX
     * @param scrollY
     * @param scrollRangeX
     * @param scrollRangeY
     * @param maxOverScrollX
     * @param maxOverScrollY
     * @param isTouchEvent
     * @return
     */
    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        if (mImageView != null) {
            if (deltaY < 0) {
                mImageView.getLayoutParams().height = mImageView.getHeight() + (-deltaY);
            } else if (deltaY > 0 && mImageView.getHeight() > mImageHeight) {
                mImageView.getLayoutParams().height = mImageView.getHeight() - deltaY;
            }
            mImageView.requestLayout();
        }


        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }

    /**
     * listview 数量太少了， 导致不触发overscrollBy
     *
     * @param l
     * @param t
     * @param oldl
     * @param oldt
     */

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mImageView != null) {
            View parent = (View) mImageView.getParent();
            if (mImageView.getHeight() > mImageHeight && parent.getTop() < 0) {
                mImageView.getLayoutParams().height = mImageView.getHeight() + parent.getTop();
                mImageView.requestLayout();
                ;
                parent.layout(0, 0, mImageView.getWidth(), mImageHeight);
            }
        }
    }

    //自动恢复效果。手指松开，自动收缩回去
    ValueAnimator mRestoreAnimator = null;

    @SuppressWarnings("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                startRestoreAnimation();
                break;
        }


        return super.onTouchEvent(ev);
    }


    private void startRestoreAnimation() {
        if (mImageView.getHeight() > mImageHeight) {
            // TODO: 2020-03-27
            if (mRestoreAnimator == null) {
                mRestoreAnimator = ValueAnimator.ofInt(mImageView.getHeight(), mImageHeight);
                mRestoreAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int height = (int) animation.getAnimatedValue();
                        if (mImageView != null) {
                            int top = ((View) mImageView.getParent()).getTop();
                            Log.e(AliInfoListView.class.getSimpleName(), "top :" + top + " height:" + height);

                            mImageView.getLayoutParams().height = height + top;
                            mImageView.requestLayout();
                        }

                    }
                });
                mRestoreAnimator.setDuration(400);
                mRestoreAnimator.start();
                mRestoreAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mRestoreAnimator.cancel();
                        mRestoreAnimator = null;
                    }
                });
            }
        }
    }

    public void setHeaderView(View headView) {
        this.headView = headView;

    }


}
