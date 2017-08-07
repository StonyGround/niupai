package com.xiuxiu.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;

import com.xiuxiu.R;

/*
 * 自定义EditText控件
 * 
 * view.OnFocusChangeListener用于监听焦点的改变（得到焦点或者失去焦点）
 * 
 * TextWatcher：有时候需要对EditText进行限制，比如字数限制，此时就通过TextWatcher来进行监听
 */
public class SearchClearEditText extends EditText implements
        OnFocusChangeListener, TextWatcher {

    // 删除按钮的引用
    private Drawable mClearDrawable;
    // 控件是否有焦点
    private boolean hasFocus;

    public SearchClearEditText(Context context) {
        this(context, null);
    }

    public SearchClearEditText(Context context, AttributeSet attrs) {
        // 这里构造方法很重要，不加这个很多属性不能在XML文件中定义
        this(context, attrs, android.R.attr.editTextStyle);
    }

    // 不写这个方法，上面的构造方法会报错
    public SearchClearEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        // 获得EditText的DrawableRight，假如没有设置就使用默认的图片
        mClearDrawable = getCompoundDrawables()[2]; // [2]=right
        if (mClearDrawable == null) {
            mClearDrawable = getResources()
                    .getDrawable(R.drawable.close);
        }
        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(),
                mClearDrawable.getIntrinsicHeight()); // 设置图的边界，有放大缩小的作用
        // 默认设置隐藏图标
        setClearIconVisible(false);
        // 设置焦点改变的监听
        setOnFocusChangeListener(this);
        // 设置输入框里面内容发生改变的监听
        addTextChangedListener(this);

    }

    /*
     * 因为我们不能直接给EditText设置点击事件，所以我们用记住我们按下的位置来模拟点击事件 当我们按下的位置 在 EditText的宽度 -
     * 图标到控件右边的间距 - 图标的宽度 和 EditText的宽度 - 图标到控件右边的间距之间我们就算点击了图标，竖直方向就没有考虑
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) { // ACTION_UP是按下抬起
            if (getCompoundDrawables()[2] != null) {

                // 当手指抬起的位置在clean的图标的区域 我们将此视为进行清除操作 ；
                // getWidth():得到控件的宽度
                // event.getX():抬起时的坐标(该坐标是相对于控件本身而言的)
                // getTotalPaddingRight():clean的图标左边缘至控件右边缘的距离
                // getPaddingRight():clean的图标右边缘至控件右边缘的距离
                // getWidth() - getTotalPaddingRight()表示: 控件左边到clean的图标左边缘的区域
                // getWidth() - getPaddingRight()表示: 控件左边到clean的图标右边缘的区域
                // 所以这两者之间的区域刚好是clean的图标的区域
                boolean touchable = (event.getX() > (getWidth() - getTotalPaddingRight()))
                        && (event.getX() < (getWidth() - getPaddingRight()));
                if (touchable) {
                    setText("");
                }
            }
        }
        return super.onTouchEvent(event);
    }

    /*
     * 设置清除图标的显示和隐藏,调用setCompoundDrawables为EditText绘制上去
     */
    private void setClearIconVisible(boolean visible) {
        Drawable right = visible ? mClearDrawable : null;
        // setCompoundDrawables(Drawable left, Drawable top, Drawable right,
        // Drawable bottom)来设置上下左右的图标
        setCompoundDrawables(getCompoundDrawables()[0],
                getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
    }

    /*
     * 当SearchClearEditText焦点发生变化的时候，判断里面字符串长度设置清楚图标的显示或隐藏
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        this.hasFocus = hasFocus;
        /*
         * 输入的时候，焦点就会改变，hasFocus = true；然后就显示清除图标
		 */
        if (hasFocus) {
            setClearIconVisible(getText().length() > 0);
        } else {
            setClearIconVisible(false);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    /*
     * 当输入框的内容发生改变的时候回调的方法
     */
    @Override
    public void onTextChanged(CharSequence text, int start, int lengthBefore,
                              int lengthAfter) {
        // super.onTextChanged(text, start, lengthBefore, lengthAfter);
        if (hasFocus) {
            setClearIconVisible(text.length() > 0);
        }
    }

    /*
     * 设置晃动动画
     */
    public void setShakeAnimation() {
        this.setAnimation(shakeAnimation(5));
    }

    /*
     * 晃动动画 @param counts：一秒晃动多少下
     */
    private Animation shakeAnimation(int counts) {
        Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
        translateAnimation.setInterpolator(new CycleInterpolator(counts));
        translateAnimation.setDuration(1000);
        return translateAnimation;
    }

}
