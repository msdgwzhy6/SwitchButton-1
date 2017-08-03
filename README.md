# SwitchButton
  自定义view————开关button

 陷入自定义view的坑越陷越深，励志要把自定义view撸给遍，今天给大家介绍一下自定义view————开关button，这个在很多app中都能用的，废话不多说，先来给动图。
 
 ![效果图](/images/switchbutton.gif)
 
 分析一下上面的效果：四个状态：关闭状态、关闭->打开过程、打开状态、打开->关闭过程，结合ValueAnimator动效实现这个过程
 
 ### 关闭状态
 
 绘制一个圆角矩形(drawRoundRect()方法)，再画一个圆(drawCircle())，默认关闭就画好了

```
    RectF rectF = new RectF(padding, padding, mwidth - padding, mheight - padding);
    //先画背景
    canvas.drawRoundRect(rectF, backgroundRadius, backgroundRadius, closePaint);
 
    //再画圆
    canvas.drawCircle(closecenterX, closecenterY, circleRadius, circlePaint);
```

 ### 打开状态
 
 绘制了关闭状态，再绘制打开状态，，最后绘制关闭->打开状态，打开->关闭状态，其实这里和关闭状态类似的，只是画圆的x、y的位置不一样而已
 
```

    RectF rectF = new RectF(padding, padding, mwidth - padding, mheight - padding);
    //先画背景
    canvas.drawRoundRect(rectF, backgroundRadius, backgroundRadius, closePaint);

    RectF rectFAnimation = new RectF(padding, padding, o2cright, o2cbottom);
    //先画背景
    canvas.drawRoundRect(rectFAnimation, backgroundRadius, backgroundRadius, openPaint);

    //再画圆
    canvas.drawCircle(o2ccenterX, o2ccenterY, circleRadius, circlePaint);
 
```
 
 ### 关闭->打开状态
 
 ValueAnimator动效中的addUpdateListener()的回调通过valueAnimator.getAnimatedValue();来重新绘制过程，计算圆在状态变化时的x,y及其圆角矩形的right,bottom值来动态改变然后重新绘制。
 
记录动态的变化值 
```
 
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
 
```
 
根据变化值重新绘制
 
```
     RectF rectF = new RectF(padding, padding, mwidth - padding, mheight - padding);
     //先画背景
     canvas.drawRoundRect(rectF, backgroundRadius, backgroundRadius, closePaint);

     RectF rectFAnimation = new RectF(padding, padding, c2oright, c2obottom);
     //先画背景
     canvas.drawRoundRect(rectFAnimation, backgroundRadius, backgroundRadius, openPaint);

     //再画圆
     canvas.drawCircle(c2ocenterX, c2ocenterY, circleRadius, circlePaint); 
 
```
  
 ### 打开->关闭状态
 
 这个动画状态和上面的动画状态同理
 
 记录变化值
```

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
 
```
根据变化值重新绘制
```
            RectF rectF = new RectF(padding, padding, mwidth - padding, mheight - padding);
            //先画背景
            canvas.drawRoundRect(rectF, backgroundRadius, backgroundRadius, closePaint);

            RectF rectFAnimation = new RectF(padding, padding, o2cright, o2cbottom);
            //先画背景
            canvas.drawRoundRect(rectFAnimation, backgroundRadius, backgroundRadius, openPaint);

            //再画圆
            canvas.drawCircle(o2ccenterX, o2ccenterY, circleRadius, circlePaint);

```

上面的四种状态介绍完毕了,大致功能就实现了，接下来介绍一些属性的设置、四种状态设置的方法、点击事件等
 
### 四种状态的设置
 
```
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

```

### 点击事件执行打开关闭

```
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

```

### 属性的设置

#### xml设置

attrs.xml

```
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <declare-styleable name="SwitchButton">
        <attr name="backgroudColor" format="color"/>
        <attr name="closeColor" format="color"/>
        <attr name="openColor" format="color"/>
        <attr name="circleColor" format="color"/>
        <attr name="animatorTime" format="integer"/>
        <attr name="padding" format="integer"/>
    </declare-styleable>
</resources>

```

```
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

```

xml中设置

```
    <com.switchbutton.SwitchButton
        android:id="@+id/btn1"
        android:layout_width="100dp"
        android:layout_height="50dp"
        android:layout_marginTop="20dp"
        app:closeColor="@color/colorAccent"
        app:openColor="@android:color/holo_green_light"
        app:padding="6"
        app:animatorTime="1000"
        />

```


#### 代码设置
 
```


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
 
```

### 考虑实践应用中，需要获取相关状态，上传至服务器／根据状态设置相关设置等

```
    /**
     * 获取当前的状态 用于使用时的需求
     * @return
     */
    public State getMcurrentState() {
        return mcurrentState;
    }
    
```
其他方法也可以实现，根据自己的需求去做相应的更改


[个人博客](https://madreain.github.io/)
