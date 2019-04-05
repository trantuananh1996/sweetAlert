package com.pr.swalert.toast;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.pr.swalert.R;


public class CustomToastNotification extends LinearLayout {
    private Context mContext;
    private String mMessage;
    private String mTitle;
    private LinearLayout background;
    private ImageView iconToast;
    TextView tvTitle;
    TextView content;

    public CustomToastNotification(Context context) {
        super(context);
        init(context);
    }

    public CustomToastNotification(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomToastNotification(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CustomToastNotification(Context context, AttributeSet attrs, int defStyleAttr,
                                   int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.layout_custom_toast, this, true);
        tvTitle = findViewById(R.id.tv_title);
        background = findViewById(R.id.notification_background);
        iconToast = findViewById(R.id.icon_toast);
        content = findViewById(R.id.tv_message);

    }

    public String getTitle() {
        if (TextUtils.isEmpty(mTitle)) {
            return getResources().getString(R.string.app_name);
        }
        return mTitle;
    }

    public CustomToastNotification setTitle(String title) {
        mTitle = title;
        tvTitle.setText(mTitle);
        return this;
    }

    public String getMessage() {
        return mMessage;
    }

    public CustomToastNotification setMessage(String message) {
        mMessage = message;
        content.setText(mMessage);
        return this;
    }

    public CustomToastNotification setBackground(int id) {
        background.setBackground(ContextCompat.getDrawable(mContext, id));
        return this;
    }

    public void setBackgroundResource(int resId) {
        background.setBackgroundResource(resId);
    }

    public CustomToastNotification setIcon(int resId) {
        iconToast.setImageResource(resId);
        return this;
    }
}