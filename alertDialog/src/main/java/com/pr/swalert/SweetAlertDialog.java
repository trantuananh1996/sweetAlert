package com.pr.swalert;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatDialog;

import com.pr.swalert.toast.ToastUtils;

import static com.pr.swalert.SweetAlertDialog.AlertType.ERROR;
import static com.pr.swalert.SweetAlertDialog.AlertType.SUCCESS;

public class SweetAlertDialog extends AppCompatDialog implements View.OnClickListener {
    private View mDialogView;
    private AnimationSet mModalInAnim;
    private AnimationSet mModalOutAnim;
    private Animation mOverlayOutAnim;
    private Animation mErrorInAnim;
    private AnimationSet mErrorXInAnim;
    private AnimationSet mSuccessLayoutAnimSet;
    private Animation mSuccessBowAnim;
    private TextView mTitleTextView;
    private TextView mContentTextView;
    private String mTitleText;
    private String mContentText;
    private boolean mShowCancel;
    private boolean mShowContent;
    private String mCancelText;
    private String mConfirmText;
    private AlertType mAlertType;
    private FrameLayout mErrorFrame;
    private FrameLayout mSuccessFrame;
    private FrameLayout mProgressFrame;
    private SuccessTickView mSuccessTick;
    private ImageView mErrorX;
    private View mSuccessLeftMask;
    private View mSuccessRightMask;
    private Drawable mCustomImgDrawable;
    private ImageView mCustomImage;
    private Button mConfirmButton;
    private Button mCancelButton;
    private ProgressHelper mProgressHelper;
    private FrameLayout mWarningFrame;
    private OnSweetClickListener mCancelClickListener;
    private OnSweetClickListener mConfirmClickListener;
    private boolean mCloseFromCancel;

    public enum AlertType {
        NORMAL, ERROR, SUCCESS, WARNING, CUSTOM_IMAGE, PROGRESS
    }

    private boolean isShowConfirmButton = true;
    //Thowif gian exit ms
    private static int EXIT_TIMEOUT = 3000;

    public static void setExitTimeout(int exitTimeout) {
        EXIT_TIMEOUT = exitTimeout;
    }

    public static int getExitTimeout() {
        return EXIT_TIMEOUT + 100; //+100 for smoother anim
    }

    public interface OnSweetClickListener {
        void onClick(SweetAlertDialog sweetAlertDialog);
    }

    public SweetAlertDialog(Context context) {
        this(context, AlertType.NORMAL);
    }

    public SweetAlertDialog(Context context, AlertType alertType) {
        super(context, R.style.alert_dialog);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        mProgressHelper = new ProgressHelper(context);
        mAlertType = alertType;
        mErrorInAnim = OptAnimationLoader.loadAnimation(getContext(), R.anim.error_frame_in);
        mErrorXInAnim = (AnimationSet) OptAnimationLoader.loadAnimation(getContext(), R.anim.error_x_in);
        mSuccessBowAnim = OptAnimationLoader.loadAnimation(getContext(), R.anim.success_bow_roate);
        mSuccessLayoutAnimSet = (AnimationSet) OptAnimationLoader.loadAnimation(getContext(), R.anim.success_mask_layout);
        mModalInAnim = (AnimationSet) OptAnimationLoader.loadAnimation(getContext(), R.anim.modal_in);
        mModalOutAnim = (AnimationSet) OptAnimationLoader.loadAnimation(getContext(), R.anim.modal_out);
        if (mModalOutAnim != null)
            mModalOutAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mDialogView.setVisibility(View.GONE);
                    mDialogView.post(() -> {
                        try {
                            if (mCloseFromCancel) {
                                SweetAlertDialog.super.cancel();
                            } else {
                                SweetAlertDialog.super.dismiss();
                            }
                        } catch (IllegalArgumentException e) {
                            ToastUtils.logException(e);
                        }
                    });
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        // dialog overlay fade out
        mOverlayOutAnim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (getWindow() == null) return;
                WindowManager.LayoutParams wlp = getWindow().getAttributes();
                wlp.alpha = 1 - interpolatedTime;
                getWindow().setAttributes(wlp);
            }
        };
        mOverlayOutAnim.setDuration(120);
    }

    protected void forceWrapContent(View v) {
        // Start with the provided view
        View current = v;

        // Travel up the tree until fail, modifying the LayoutParams
        do {
            // Get the parent
            ViewParent parent = current.getParent();

            // Check if the parent exists
            if (parent != null) {
                // Get the view
                try {
                    current = (View) parent;
                    ViewGroup.LayoutParams layoutParams = current.getLayoutParams();
                    if (layoutParams instanceof FrameLayout.LayoutParams) {
                        ((FrameLayout.LayoutParams) layoutParams).
                                gravity = Gravity.CENTER_HORIZONTAL;
                    } else if (layoutParams instanceof WindowManager.LayoutParams) {
                        ((WindowManager.LayoutParams) layoutParams).
                                gravity = Gravity.CENTER_HORIZONTAL;
                    }
                } catch (ClassCastException e) {
                    // This will happen when at the top view, it cannot be cast to a View
                    break;
                }

                // Modify the layout
                if (current.getLayoutParams() != null)
                    current.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
        } while (current.getParent() != null);

        // Request a layout to be re-done
        current.requestLayout();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);


        Window window = getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
        setContentView(R.layout.alert_dialog);

        if (getWindow() != null) {
            mDialogView = window.getDecorView().findViewById(android.R.id.content);
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

//        forceWrapContent(mDialogView);
        mTitleTextView = findViewById(R.id.title_text);
//        mTitleTextView.setMovementMethod(new ScrollingMovementMethod());
        mContentTextView = findViewById(R.id.content_text);
        mErrorFrame = findViewById(R.id.error_frame);
        mErrorX = mErrorFrame.findViewById(R.id.error_x);
        mSuccessFrame = findViewById(R.id.success_frame);
        mProgressFrame = findViewById(R.id.progress_dialog);
        mSuccessTick = mSuccessFrame.findViewById(R.id.success_tick);
        mSuccessLeftMask = mSuccessFrame.findViewById(R.id.mask_left);
        mSuccessRightMask = mSuccessFrame.findViewById(R.id.mask_right);
        mCustomImage = findViewById(R.id.custom_image);
        mWarningFrame = findViewById(R.id.warning_frame);
        mConfirmButton = findViewById(R.id.confirm_button);
        mCancelButton = findViewById(R.id.cancel_button);
        mProgressHelper.setProgressWheel(findViewById(R.id.progressWheel));
        mConfirmButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);
        if (isShowConfirmButton) mConfirmButton.setVisibility(View.VISIBLE);
        else mConfirmButton.setVisibility(View.GONE);
        if (mShowCancel) mCancelButton.setVisibility(View.VISIBLE);
        else mCancelButton.setVisibility(View.GONE);
        setTitleText(mTitleText);
        setContentText(mContentText);
        setCancelText(mCancelText);
        setConfirmText(mConfirmText);
        changeAlertType(mAlertType, true);

    }

    private void restore() {
        mCustomImage.setVisibility(View.GONE);
        mErrorFrame.setVisibility(View.GONE);
        mSuccessFrame.setVisibility(View.GONE);
        mWarningFrame.setVisibility(View.GONE);
        mProgressFrame.setVisibility(View.GONE);
        mConfirmButton.setVisibility(View.VISIBLE);

        mConfirmButton.setBackgroundResource(R.drawable.blue_button_background);
        mErrorFrame.clearAnimation();
        mErrorX.clearAnimation();
        mSuccessTick.clearAnimation();
        mSuccessLeftMask.clearAnimation();
        mSuccessRightMask.clearAnimation();
    }

    private void playAnimation() {
        if (mAlertType == ERROR) {
            if (mErrorInAnim != null) mErrorFrame.startAnimation(mErrorInAnim);
            if (mErrorXInAnim != null) mErrorX.startAnimation(mErrorXInAnim);
        } else if (mAlertType == SUCCESS) {
            mSuccessTick.startTickAnim();
            if (mSuccessBowAnim != null) mSuccessRightMask.startAnimation(mSuccessBowAnim);
        }
    }

    private void changeAlertType(AlertType alertType, boolean fromCreate) {
        mAlertType = alertType;
        // call after created views
        if (mDialogView != null) {
            if (!fromCreate) {
                // restore all of views state before switching alert type
                restore();
            }
            switch (mAlertType) {
                case ERROR:
                    mErrorFrame.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    mSuccessFrame.setVisibility(View.VISIBLE);
                    // initial rotate layout of success mask
                    if (mSuccessLayoutAnimSet != null) {
                        mSuccessLeftMask.startAnimation(mSuccessLayoutAnimSet.getAnimations().get(0));
                        mSuccessRightMask.startAnimation(mSuccessLayoutAnimSet.getAnimations().get(1));
                    }
                    break;
                case WARNING:
                    mConfirmButton.setBackgroundResource(R.drawable.red_button_background);
                    mWarningFrame.setVisibility(View.VISIBLE);
                    break;
                case CUSTOM_IMAGE:
                    setCustomImage(mCustomImgDrawable);
                    break;
                case PROGRESS:
                    mProgressFrame.setVisibility(View.VISIBLE);
                    mConfirmButton.setVisibility(View.GONE);
                    break;
            }
            if (!fromCreate) {
                playAnimation();
            }
        }
    }

    public AlertType getAlertType() {
        return mAlertType;
    }

    public void changeAlertType(AlertType alertType) {
        changeAlertType(alertType, false);
    }


    public String getTitleText() {
        return mTitleText;
    }

    public SweetAlertDialog setTitleText(String text) {
        mTitleText = text;
        if (mTitleTextView != null && mTitleText != null) {
            mTitleTextView.setText(mTitleText);
        }
        return this;
    }

    public SweetAlertDialog setCustomImage(Drawable drawable) {
        mCustomImgDrawable = drawable;
        if (mCustomImage != null && mCustomImgDrawable != null) {
            mCustomImage.setVisibility(View.VISIBLE);
            mCustomImage.setImageDrawable(mCustomImgDrawable);
        }
        return this;
    }

    public SweetAlertDialog setCustomImage(int resourceId) {
        return setCustomImage(getContext().getResources().getDrawable(resourceId));
    }

    public String getContentText() {
        return mContentText;
    }

    public SweetAlertDialog setContentText(String text) {
        mContentText = text;
        if (mContentTextView != null && mContentText != null) {
            showContentText(true);
            mContentTextView.setText(mContentText);
        }
        return this;
    }

    public boolean isShowCancelButton() {
        return mShowCancel;
    }

    public SweetAlertDialog showCancelButton(boolean isShow) {
        mShowCancel = isShow;
        if (mCancelButton != null) {
            mCancelButton.setVisibility(mShowCancel ? View.VISIBLE : View.GONE);
        }
        return this;
    }

    public boolean isShowContentText() {
        return mShowContent;
    }

    private SweetAlertDialog showContentText(boolean isShow) {
        mShowContent = isShow;
        if (mContentTextView != null) {
            mContentTextView.setVisibility(mShowContent ? View.VISIBLE : View.GONE);
        }
        return this;
    }

    public String getCancelText() {
        return mCancelText;
    }

    public SweetAlertDialog setCancelText(String text) {
        mCancelText = text;
        if (mCancelButton != null && mCancelText != null) {
            showCancelButton(true);
            mCancelButton.setText(mCancelText);
        }
        return this;
    }

    public SweetAlertDialog setCancelText(@StringRes int text) {
        if (mCancelButton != null) {
            showCancelButton(true);
            mCancelButton.setText(text);
            mCancelText = mCancelButton.getText().toString();
        }
        return this;
    }

    public SweetAlertDialog setConfirmText(@StringRes int text) {
        if (mConfirmButton != null) {
            showCancelButton(true);
            mConfirmButton.setText(text);
            mConfirmText = mConfirmButton.getText().toString();
        }
        return this;
    }

    public String getConfirmText() {
        return mConfirmText;
    }

    public SweetAlertDialog setConfirmText(String text) {
        mConfirmText = text;
        if (mConfirmButton != null && mConfirmText != null) {
            mConfirmButton.setText(mConfirmText);
        }
        return this;
    }

    public SweetAlertDialog showConfirmButton(boolean isShow) {
        isShowConfirmButton = isShow;
        return this;
    }

    public SweetAlertDialog setCancelClickListener(OnSweetClickListener listener) {
        mCancelClickListener = listener;
        return this;
    }

    public SweetAlertDialog setConfirmClickListener(OnSweetClickListener listener) {
        mConfirmClickListener = listener;
        return this;
    }

    @Override
    public void show() {
        setCanceledOnTouchOutside(true);
        if (isShowing()) return;
        super.show();
        if (!isShowConfirmButton && !isShowCancelButton()) {
            new Handler().postDelayed(() -> mConfirmButton.performClick(), EXIT_TIMEOUT);
        }
    }

    protected void onStart() {
        if (mModalInAnim != null)
            mDialogView.startAnimation(mModalInAnim);
        playAnimation();
    }

    /**
     * The real Dialog.cancel() will be invoked async-ly after the animation finishes.
     */
    @Override
    public void cancel() {
        dismissWithAnimation(true);
    }

    /**
     * The real Dialog.dismiss() will be invoked async-ly after the animation finishes.
     */
    public void dismissWithAnimation() {
        dismissWithAnimation(false);
    }

    private void dismissWithAnimation(boolean fromCancel) {
        mCloseFromCancel = fromCancel;
        mTitleTextView.startAnimation(mOverlayOutAnim);
        if (mModalOutAnim != null) mDialogView.startAnimation(mModalOutAnim);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cancel_button) {
            if (mCancelClickListener != null) {
                mCancelClickListener.onClick(SweetAlertDialog.this);
            } else {
                dismissWithAnimation();
            }
        } else if (v.getId() == R.id.confirm_button) {
            if (mConfirmClickListener != null) {
                mConfirmClickListener.onClick(SweetAlertDialog.this);
            } else {
                dismissWithAnimation();
            }
        }
    }

    public ProgressHelper getProgressHelper() {
        return mProgressHelper;
    }
}