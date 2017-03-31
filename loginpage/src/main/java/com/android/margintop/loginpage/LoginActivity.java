package com.android.margintop.loginpage;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private int mScreenHeight;        // 屏幕高度
    private int mKeyboardHeight;      // 软件盘弹起后所占高度
    private float mScale = 0.6f;      // logo缩放比例
    private int mHeight;              // 软键盘弹起时影响的高度
    private FrameLayout mFlLogo;
    private EditText mEtMobile;
    private EditText mEtPassword;
    private ImageView mIvCleanPhone;
    private ImageView mIvCleanPassword;
    private ImageView mIvShowPwd;
    private Button mBtnLogin;
    private View mRlRoot, mLlService, mLlContent, mLlBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Utils.init(this);
        // 全屏时，监听addOnLayoutChangeListener失效。
        if (isFullScreen()) {
            AndroidBug5497Workaround.assistActivity(this);
        }
        initView();
        initData();
        initListener();
    }

    /**
     * 判断是否是全屏。
     *
     * @return
     */
    public boolean isFullScreen() {
        return (getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) == WindowManager.LayoutParams.FLAG_FULLSCREEN;
    }

    /**
     * 找到我们关心的控件。
     */
    private void initView() {
        mFlLogo = (FrameLayout) findViewById(R.id.fl_logo);
        mEtMobile = (EditText) findViewById(R.id.et_mobile);
        mEtPassword = (EditText) findViewById(R.id.et_password);
        mIvCleanPhone = (ImageView) findViewById(R.id.iv_clean_phone);
        mIvCleanPassword = (ImageView) findViewById(R.id.iv_clean_password);
        mIvShowPwd = (ImageView) findViewById(R.id.iv_show_pwd);
        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mLlService = findViewById(R.id.ll_service);
        mLlContent = findViewById(R.id.ll_content);
        mRlRoot = findViewById(R.id.rl_root);
        mLlBottom = findViewById(R.id.ll_bottom);
    }

    /**
     * 获取屏幕高度，然后定义弹起高度。
     */
    private void initData() {
        mScreenHeight = this.getResources().getDisplayMetrics().heightPixels; //获取屏幕高度
        mKeyboardHeight = mScreenHeight / 3;//弹起高度为屏幕高度的1/3
    }

    /**
     * 设置监听。
     */
    private void initListener() {
        mIvCleanPhone.setOnClickListener(this);
        mIvCleanPassword.setOnClickListener(this);
        mBtnLogin.setOnClickListener(this);
        setTextChangedListener(mEtMobile, mIvCleanPhone, "^[0-9]+$", R.string.please_input_limit_phone);
        setTextChangedListener(mEtPassword, mIvCleanPassword, "^[A-Za-z0-9]+$", R.string.please_input_limit_pwd);
        mIvShowPwd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mEtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        mIvShowPwd.setImageResource(R.mipmap.psw_show);
                        mEtPassword.setSelection(mEtPassword.getText().length());
                        break;
                    case MotionEvent.ACTION_UP:
                        mEtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        mIvShowPwd.setImageResource(R.mipmap.psw_gone);
                        mEtPassword.setSelection(mEtPassword.getText().length());
                        break;
                }
                return true;
            }
        });
        mRlRoot.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                // old是改变前的左上右下坐标点值，没有old的是改变后的左上右下坐标点值
                // 我们默认只要控件将Activity向上推的高度超过了1/3屏幕高，就认为软键盘弹起
                if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > mKeyboardHeight)) { // 软键盘弹起
                    mHeight = 200;
                    animationOpen();
                    mLlService.setVisibility(View.INVISIBLE);

                } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > mKeyboardHeight)) { // 软键盘关闭
                    animationClose();
                    mLlService.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * 设置文本实时变化监听。
     */
    private void setTextChangedListener(EditText et, final ImageView iv, final String regex, @StringRes final int resId) {
        et.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s) && iv.getVisibility() == View.GONE) {
                    iv.setVisibility(View.VISIBLE);
                    if (!s.toString().matches(regex)) {
                        ToastUtil.showLongToastSafe(resId);
                        s.delete(s.length() - 1, s.length());
                        mEtMobile.setSelection(s.length());
                    }
                } else if (TextUtils.isEmpty(s)) {
                    iv.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * 软键盘弹起动画。
     */
    private void animationOpen() {
        startAnimation(true);
    }

    /**
     * 软键盘关闭动画。
     */
    private void animationClose() {
        startAnimation(false);
    }

    private void startAnimation(boolean isOpen) {
        AnimatorSet animators = new AnimatorSet();

//        ObjectAnimator oa1 = ObjectAnimator.ofFloat(mLlContent, "translationY", new float[2]);

        mFlLogo.setPivotX(mFlLogo.getWidth() / 2);
        mFlLogo.setPivotY(mFlLogo.getHeight());
        ObjectAnimator oa2 = ObjectAnimator.ofFloat(mFlLogo, "scaleX", new float[2]);
        ObjectAnimator oa3 = ObjectAnimator.ofFloat(mFlLogo, "scaleY", new float[2]);


        if (isOpen) {
//            oa1.setFloatValues(0.0f, -mHeight);
            mLlContent.scrollTo(0, mHeight);
            oa2.setFloatValues(1.0f, mScale);
            oa3.setFloatValues(1.0f, mScale);
        } else {
//            oa1.setFloatValues(mHeight, 0.0f);
            mLlContent.scrollTo(0, 0);
            oa2.setFloatValues(mScale, 1.0f);
            oa3.setFloatValues(mScale, 1.0f);
        }

        animators.playTogether(oa2, oa3);
        animators.setDuration(200);
        animators.start();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_clean_phone) { // 清除手机号码
            mEtMobile.setText("");
        } else if (id == R.id.iv_clean_password) { // 清除密码
            mEtPassword.setText("");
        } else if (id == R.id.btn_login) { // 点击确定
            ToastUtil.showLongToastSafe("未实现点击确定逻辑");
        }
    }

}
