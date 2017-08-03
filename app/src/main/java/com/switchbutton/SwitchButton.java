package com.switchbutton;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by wujun on 2017/8/3.
 *
 * @author madreain
 * @desc
 */

public class SwitchButton extends View {

    Paint closePaint;
    Paint ciclePaint;
    Paint openPaint;
    int mPaintStrokeWidth = 3;
    int backgroundRadius;
    int mwidth;
    int mheight;
    //圆的x,y
    float closecenterX, closecenterY;
    float opencenterX, opencenterY;
    int circleRadius;
    //close--->open状态动画的变化值
    //圆的变化值
    float c2ocenterX, c2ocenterY;
    float c2oright, c2obottom;
    //close--->open之间的距离
    float closeOpenX, closeOpenRight, closeOpenBottom;
    //open--->close状态动画的变化值
    float o2ccenterX, o2ccenterY;
    float o2cright, o2cbottom;

    private State mcurrentState = State.CLOSE;

    //close--->open的动效
    ValueAnimator close2OpengvalueAnimator;
    //close--->open动画数值(用于控制动画状态,因为同一时间内只允许有一种状态出现,具体数值处理取决于当前状态)
    private float mclose2OpenAnimatorValue = 0;

    //open--->close的动效
    ValueAnimator openg2ClosevalueAnimator;
    //open--->close动画数值(用于控制动画状态,因为同一时间内只允许有一种状态出现,具体数值处理取决于当前状态)
    private float mopeng2CloseAnimatorValue = 0;


    public enum State {
        CLOSE,
        OPEN,
        CLOSE2OPEN,
        OPEN2CLOSE,
    }

    public SwitchButton(Context context) {
        super(context);
    }

    public SwitchButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
        initListener();
    }

    public SwitchButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mwidth = w;
        mheight = h;
        //背景的半径
        backgroundRadius = mheight / 2 - mPaintStrokeWidth;
        //圆的半径
        circleRadius = backgroundRadius - mPaintStrokeWidth;
        //关闭状态的x,y
        closecenterX = backgroundRadius + mPaintStrokeWidth;
        closecenterY = backgroundRadius + mPaintStrokeWidth;
        //打开状态的x,y
        opencenterX = mwidth - backgroundRadius - mPaintStrokeWidth;
        opencenterY = backgroundRadius + mPaintStrokeWidth;
        //关闭--->打开状态的差值
        closeOpenX = opencenterX - closecenterX;
        closeOpenRight = mwidth - mPaintStrokeWidth - (backgroundRadius * 2 + mPaintStrokeWidth);
        closeOpenBottom = mheight - mPaintStrokeWidth - (backgroundRadius * 2 + mPaintStrokeWidth);
    }

    private void init() {
        closePaint = new Paint();
        closePaint.setAntiAlias(true);
        closePaint.setStyle(Paint.Style.STROKE);
        closePaint.setStrokeWidth(mPaintStrokeWidth);
        closePaint.setColor(Color.GRAY);

        ciclePaint = new Paint();
        ciclePaint.setAntiAlias(true);
        ciclePaint.setStyle(Paint.Style.FILL);
        ciclePaint.setStrokeWidth(mPaintStrokeWidth);
        ciclePaint.setColor(Color.WHITE);

        openPaint = new Paint();
        openPaint.setAntiAlias(true);
        openPaint.setStyle(Paint.Style.FILL);
        openPaint.setStrokeWidth(mPaintStrokeWidth);
        openPaint.setColor(Color.GREEN);

    }

    private void initListener() {
        //close--->open状态
        // 创建0－1的一个过程,任何复杂的过程都可以采用归一化，然后在addUpdateListener回调里去做自己想要的变化
        close2OpengvalueAnimator = ValueAnimator.ofFloat(0, 1);
        // 设置过程的时间为2S
        close2OpengvalueAnimator.setDuration(500);

        close2OpengvalueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mclose2OpenAnimatorValue = (float) valueAnimator.getAnimatedValue();
                //圆在状态变化时的x,y
                c2ocenterX = closecenterX + closeOpenX * mclose2OpenAnimatorValue;
                c2ocenterY = backgroundRadius + mPaintStrokeWidth;
                //背景的右下标=关闭状态的位置（2*backgroundRadius+mPaintStrokeWidth）+差值*变化
                c2oright = 2 * backgroundRadius + mPaintStrokeWidth + closeOpenRight * mclose2OpenAnimatorValue;
                //背景的底下标=关闭状态的位置（2*backgroundRadius+mPaintStrokeWidth）+差值*变化
                c2obottom = 2 * backgroundRadius + mPaintStrokeWidth + closeOpenBottom * mclose2OpenAnimatorValue;
                invalidate();
            }
        });
        close2OpengvalueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                setOpen();
                invalidate();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        //open--->close状态
        // 创建0－1的一个过程,任何复杂的过程都可以采用归一化，然后在addUpdateListener回调里去做自己想要的变化
        openg2ClosevalueAnimator = ValueAnimator.ofFloat(1, 0);
        // 设置过程的时间为2S
        openg2ClosevalueAnimator.setDuration(500);

        openg2ClosevalueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mopeng2CloseAnimatorValue = (float) valueAnimator.getAnimatedValue();

                //圆在状态变化时的x,y
                o2ccenterX = closecenterX+closeOpenX * mopeng2CloseAnimatorValue;
                o2ccenterY = backgroundRadius + mPaintStrokeWidth;
                //背景的右下标=关闭状态的位置（2*backgroundRadius+mPaintStrokeWidth）+差值*变化
                o2cright = 2 * backgroundRadius + mPaintStrokeWidth + closeOpenRight * mopeng2CloseAnimatorValue;
                //背景的底下标=关闭状态的位置（2*backgroundRadius+mPaintStrokeWidth）+差值*变化
                o2cbottom = 2 * backgroundRadius + mPaintStrokeWidth + closeOpenBottom * mopeng2CloseAnimatorValue;
                invalidate();

            }
        });
        openg2ClosevalueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                setClose();
                invalidate();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        canvas.drawColor(Color.BLUE);

        RectF rectF = new RectF(mPaintStrokeWidth, mPaintStrokeWidth, mwidth - mPaintStrokeWidth, mheight - mPaintStrokeWidth);
        //先画背景
        canvas.drawRoundRect(rectF, backgroundRadius, backgroundRadius, closePaint);
        //关闭状态
        if (mcurrentState == State.CLOSE) {
            //再画圆
            canvas.drawCircle(closecenterX, closecenterY, circleRadius, ciclePaint);
            //打开状态
        } else if (mcurrentState == State.OPEN) {
            RectF rectFOpen = new RectF(mPaintStrokeWidth, mPaintStrokeWidth, mwidth - mPaintStrokeWidth, mheight - mPaintStrokeWidth);
            //先画背景
            canvas.drawRoundRect(rectFOpen, backgroundRadius, backgroundRadius, openPaint);
            //再画圆
            canvas.drawCircle(opencenterX, opencenterY, circleRadius, ciclePaint);
            //关闭到打开状态
        } else if (mcurrentState == State.CLOSE2OPEN) {
            RectF rectFAnimation = new RectF(mPaintStrokeWidth, mPaintStrokeWidth, c2oright, c2obottom);
            //先画背景
            canvas.drawRoundRect(rectFAnimation, backgroundRadius, backgroundRadius, openPaint);
            //再画圆
            canvas.drawCircle(c2ocenterX, c2ocenterY, circleRadius, ciclePaint);
        } else if (mcurrentState == State.OPEN2CLOSE) {
            RectF rectFAnimation = new RectF(mPaintStrokeWidth, mPaintStrokeWidth, o2cright, o2cbottom);
            //先画背景
            canvas.drawRoundRect(rectFAnimation, backgroundRadius, backgroundRadius, openPaint);
            //再画圆
            canvas.drawCircle(o2ccenterX, o2ccenterY, circleRadius, ciclePaint);
        }
    }

    public void setClose() {
        mcurrentState = State.CLOSE;
        invalidate();
    }

    public void setClose2Open() {
        mcurrentState = State.CLOSE2OPEN;
        close2OpengvalueAnimator.start();
    }

    public void setOpen2Close() {
        mcurrentState = State.OPEN2CLOSE;
        openg2ClosevalueAnimator.start();
    }

    public void setOpen() {
        mcurrentState = State.OPEN;
        invalidate();
    }

    public void onclick(){
        if(mcurrentState==State.CLOSE){
            setClose2Open();
        }else if(mcurrentState==State.OPEN){
            setOpen2Close();
        }
    }
}
