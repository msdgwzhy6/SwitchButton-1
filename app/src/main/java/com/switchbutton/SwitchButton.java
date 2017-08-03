package com.switchbutton;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by wujun on 2017/8/3.
 * 仿iOS开关button
 * @author madreain
 * @desc
 */

public class SwitchButton extends View {

    int backgroudColor=Color.WHITE;
    //设置间距（这里把开始的x,y，距离的间距都写在一起了，可以考虑给分开写，懒的改了）
    int padding = 1;
    Paint closePaint;
    int closeColor=Color.GRAY;
    Paint circlePaint;
    int circleColor=Color.WHITE;
    Paint openPaint;
    int openColor=Color.GREEN;
    //半径
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

    private int animatorTime=500;

    private enum State {
        CLOSE,
        OPEN,
        CLOSE2OPEN,
        OPEN2CLOSE,
    }

    public SwitchButton(Context context) {
        super(context);
        init();
        initListener();
    }

    public SwitchButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initTypeArray(context,attrs);
        init();
        initListener();
    }




    public SwitchButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTypeArray(context,attrs);
        init();
        initListener();
    }

    private void initTypeArray(Context context, AttributeSet attrs) {
        TypedArray typedArray=context.obtainStyledAttributes(attrs,R.styleable.SwitchButton);
        backgroudColor=typedArray.getColor(R.styleable.SwitchButton_backgroudColor,Color.WHITE);
        closeColor=typedArray.getColor(R.styleable.SwitchButton_closeColor,Color.GRAY);
        openColor=typedArray.getColor(R.styleable.SwitchButton_openColor,Color.GREEN);
        circleColor=typedArray.getColor(R.styleable.SwitchButton_circleColor,Color.WHITE);
        padding=typedArray.getInteger(R.styleable.SwitchButton_padding,1);
        animatorTime=typedArray.getInteger(R.styleable.SwitchButton_animatorTime,500);
        typedArray.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mwidth = w;
        mheight = h;
        //背景的半径
        backgroundRadius = mheight / 2 - padding;
        //圆的半径
        circleRadius = backgroundRadius - padding;
        //关闭状态的x,y
        closecenterX = backgroundRadius + padding;
        closecenterY = backgroundRadius + padding;
        //打开状态的x,y
        opencenterX = mwidth - backgroundRadius - padding;
        opencenterY = backgroundRadius + padding;
        //关闭--->打开状态的差值
        closeOpenX = opencenterX - closecenterX;
        closeOpenRight = mwidth - padding - (backgroundRadius * 2 + padding);
        closeOpenBottom = mheight - padding - (backgroundRadius * 2 + padding);
    }

    private void init() {
        closePaint = new Paint();
        closePaint.setAntiAlias(true);
        closePaint.setStyle(Paint.Style.FILL);
//        closePaint.setStrokeWidth(mPaintStrokeWidth);
        closePaint.setColor(closeColor);

        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Paint.Style.FILL);
//        circlePaint.setStrokeWidth(mPaintStrokeWidth);
        circlePaint.setColor(circleColor);

        openPaint = new Paint();
        openPaint.setAntiAlias(true);
        openPaint.setStyle(Paint.Style.FILL);
//        openPaint.setStrokeWidth(mPaintStrokeWidth);
        openPaint.setColor(openColor);
    }

    private void initListener() {
        //close--->open状态
        // 创建0－1的一个过程,任何复杂的过程都可以采用归一化，然后在addUpdateListener回调里去做自己想要的变化
        close2OpengvalueAnimator = ValueAnimator.ofFloat(0, 1);
        // 设置过程的时间为2S
        close2OpengvalueAnimator.setDuration(animatorTime);


        //open--->close状态
        // 创建0－1的一个过程,任何复杂的过程都可以采用归一化，然后在addUpdateListener回调里去做自己想要的变化
        openg2ClosevalueAnimator = ValueAnimator.ofFloat(1, 0);
        // 设置过程的时间为2S
        openg2ClosevalueAnimator.setDuration(animatorTime);
    }

    private void addCloseToOpenAnimator(){
        close2OpengvalueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mclose2OpenAnimatorValue = (float) valueAnimator.getAnimatedValue();
                //圆在状态变化时的x,y
                c2ocenterX = closecenterX + closeOpenX * mclose2OpenAnimatorValue;
                c2ocenterY = backgroundRadius + padding;
                //背景的右下标=关闭状态的位置（2*backgroundRadius+padding）+差值*变化
                c2oright = 2 * backgroundRadius + padding + closeOpenRight * mclose2OpenAnimatorValue;
                //背景的底下标=关闭状态的位置（2*backgroundRadius+padding）+差值*变化
                c2obottom = 2 * backgroundRadius + padding + closeOpenBottom * mclose2OpenAnimatorValue;
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
    }

    private void addOpenToCloseAnimator(){
        openg2ClosevalueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mopeng2CloseAnimatorValue = (float) valueAnimator.getAnimatedValue();

                //圆在状态变化时的x,y
                o2ccenterX = closecenterX+closeOpenX * mopeng2CloseAnimatorValue;
                o2ccenterY = backgroundRadius + padding;
                //背景的右下标=关闭状态的位置（2*backgroundRadius+padding）+差值*变化
                o2cright = 2 * backgroundRadius + padding + closeOpenRight * mopeng2CloseAnimatorValue;
                //背景的底下标=关闭状态的位置（2*backgroundRadius+padding）+差值*变化
                o2cbottom = 2 * backgroundRadius + padding + closeOpenBottom * mopeng2CloseAnimatorValue;
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

        canvas.drawColor(backgroudColor);

//        RectF rectF = new RectF(mPaintStrokeWidth, mPaintStrokeWidth, mwidth - mPaintStrokeWidth, mheight - mPaintStrokeWidth);
//        //先画背景
//        canvas.drawRoundRect(rectF, backgroundRadius, backgroundRadius, closePaint);

        //关闭状态
        if (mcurrentState == State.CLOSE) {
            RectF rectF = new RectF(padding, padding, mwidth - padding, mheight - padding);
            //先画背景
            canvas.drawRoundRect(rectF, backgroundRadius, backgroundRadius, closePaint);

            //再画圆
            canvas.drawCircle(closecenterX, closecenterY, circleRadius, circlePaint);
            //打开状态
        } else if (mcurrentState == State.OPEN) {
            RectF rectFOpen = new RectF(padding, padding, mwidth - padding, mheight - padding);
            //先画背景
            canvas.drawRoundRect(rectFOpen, backgroundRadius, backgroundRadius, openPaint);

            //再画圆
            canvas.drawCircle(opencenterX, opencenterY, circleRadius, circlePaint);
            //关闭到打开状态
        } else if (mcurrentState == State.CLOSE2OPEN) {
            RectF rectF = new RectF(padding, padding, mwidth - padding, mheight - padding);
            //先画背景
            canvas.drawRoundRect(rectF, backgroundRadius, backgroundRadius, closePaint);

            RectF rectFAnimation = new RectF(padding, padding, c2oright, c2obottom);
            //先画背景
            canvas.drawRoundRect(rectFAnimation, backgroundRadius, backgroundRadius, openPaint);

            //再画圆
            canvas.drawCircle(c2ocenterX, c2ocenterY, circleRadius, circlePaint);
        } else if (mcurrentState == State.OPEN2CLOSE) {
            RectF rectF = new RectF(padding, padding, mwidth - padding, mheight - padding);
            //先画背景
            canvas.drawRoundRect(rectF, backgroundRadius, backgroundRadius, closePaint);

            RectF rectFAnimation = new RectF(padding, padding, o2cright, o2cbottom);
            //先画背景
            canvas.drawRoundRect(rectFAnimation, backgroundRadius, backgroundRadius, openPaint);

            //再画圆
            canvas.drawCircle(o2ccenterX, o2ccenterY, circleRadius, circlePaint);
        }
    }

    /***
     * 关闭
     */
    private void setClose() {
        mcurrentState = State.CLOSE;
        invalidate();
    }

    /***
     * 打开
     */
    private void setOpen() {
        mcurrentState = State.OPEN;
        invalidate();
    }

    /***
     * 关闭到打开
     */
    private void setClose2Open() {
        mcurrentState = State.CLOSE2OPEN;
        openg2ClosevalueAnimator.removeAllUpdateListeners();
        addCloseToOpenAnimator();
        close2OpengvalueAnimator.start();
    }

    /***
     * 打开到关闭
     */
    private void setOpen2Close() {
        mcurrentState = State.OPEN2CLOSE;
        close2OpengvalueAnimator.removeAllUpdateListeners();
        addOpenToCloseAnimator();
        openg2ClosevalueAnimator.start();
    }

    /***
     * 控件点击执行该方法 改变状态
     */
    public void onclick(){
        if(mcurrentState==State.CLOSE){
            setClose2Open();
        }else if(mcurrentState==State.OPEN){
            setOpen2Close();
        }
    }

    /**
     * 获取当前的状态 用于使用时的需求
     * @return
     */
    public State getMcurrentState() {
        return mcurrentState;
    }

    /**
     * 设置背景颜色
     * @param backgroudColor
     */
    public void setBackgroudColor(int backgroudColor) {
        this.backgroudColor = backgroudColor;
    }

    /**
     * 关闭状态颜色
     * @param closeColor
     */
    public void setCloseColor(int closeColor) {
        this.closeColor = closeColor;
        closePaint.setColor(closeColor);
    }

    /**
     * 打开状态颜色
     * @param openColor
     */
    public void setOpenColor(int openColor) {
        this.openColor = openColor;
        openPaint.setColor(openColor);
    }

    /**
     * 圆的颜色
     * @param circleColor
     */
    public void setCircleColor(int circleColor) {
        this.circleColor = circleColor;
        circlePaint.setColor(circleColor);
    }

    /**
     * 间距
     * @param padding
     */
    public void setPadding(int padding) {
        this.padding = padding;
    }

    /**
     * 动画时间
     * @param animatorTime
     */
    public void setAnimatorTime(int animatorTime) {
        this.animatorTime = animatorTime;
    }

}
