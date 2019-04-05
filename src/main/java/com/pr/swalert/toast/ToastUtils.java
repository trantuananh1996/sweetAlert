package com.pr.swalert.toast;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.pr.swalert.BuildConfig;
import com.pr.swalert.R;
import com.pr.swalert.SweetAlertDialog;

import static com.pr.swalert.SweetAlertDialog.AlertType.CUSTOM_IMAGE;
import static com.pr.swalert.SweetAlertDialog.AlertType.ERROR;
import static com.pr.swalert.SweetAlertDialog.AlertType.NORMAL;
import static com.pr.swalert.SweetAlertDialog.AlertType.SUCCESS;
import static com.pr.swalert.SweetAlertDialog.AlertType.WARNING;


/**
 * Created by Tran Anh
 * on 12/26/2017.
 */

public class ToastUtils {

    public interface AlertListener {
        void onAlertConfirmed(boolean yesButtonConfirmed);
    }

    public static void logException(Throwable e) {
        if (e == null) return;
        if (BuildConfig.DEBUG) Log.e("Error", "Message" + e.getMessage());
        //TODO: CrashLytics.logException
    }

    public static void alertYesNo(@Nullable Activity activity, @StringRes int message, AlertListener listener) {
        if (activity == null|| activity.isFinishing()) return;
        alertYesNo(activity, activity.getResources().getString(message), listener);
    }

    public static void alertYesNo(@Nullable Activity activity, String message, AlertListener listener) {
        if (activity == null || activity.isFinishing()) return;
        try {
            if (!message.trim().endsWith("?")) message = message + "?";
            new SweetAlertDialog(activity, CUSTOM_IMAGE)
                    .setTitleText(message)
                    .setCustomImage(null)
                    .showCancelButton(true)
                    .showConfirmButton(true)
                    .setCancelText(R.string.button_no)
                    .setConfirmText(R.string.button_yes)
                    .setConfirmClickListener(sweetAlertDialog -> {
                        listener.onAlertConfirmed(true);
                        sweetAlertDialog.dismiss();
                    })
                    .setCancelClickListener(sweetAlertDialog -> {
                        listener.onAlertConfirmed(false);
                        sweetAlertDialog.dismiss();
                    })
                    .show();
        } catch (Exception e) {
            ToastUtils.logException(e);
        }
    }

    public static void showWarningConfirm(@Nullable Activity activity, String message, AlertListener listener) {
        if (activity == null) return;
        try {
            if (!message.trim().endsWith("?")) message = message + "?";
            new SweetAlertDialog(activity, WARNING)
                    .setTitleText(message)
                    .setCustomImage(null)
                    .showCancelButton(true)
                    .showConfirmButton(true)
                    .setCancelText(R.string.button_no)
                    .setConfirmText(R.string.button_yes)
                    .setConfirmClickListener(sweetAlertDialog -> {
                        listener.onAlertConfirmed(true);
                        sweetAlertDialog.dismiss();
                    })
                    .setCancelClickListener(sweetAlertDialog -> {
                        listener.onAlertConfirmed(false);
                        sweetAlertDialog.dismiss();
                    })
                    .show();
        } catch (Exception e) {
            ToastUtils.logException(e);
        }
    }

    public static void showToastSuccessConfirm(Context context, String message) {
        if (context == null || TextUtils.isEmpty(message)) return;
        try {
            new SweetAlertDialog(context, SUCCESS)
                    .setTitleText(message)
                    .showCancelButton(false)
                    .showConfirmButton(true)
                    .show();
        } catch (Exception e) {
            ToastUtils.logException(e);
            showToastText(context, message);
        }
    }

    public static void showToastConfirm(Context context, String message) {
        if (context == null || TextUtils.isEmpty(message)) return;
        try {
            new SweetAlertDialog(context, NORMAL)
                    .setTitleText(message)
                    .showCancelButton(false)
                    .showConfirmButton(true)
                    .show();
        } catch (Exception e) {
            ToastUtils.logException(e);
            showToastText(context, message);
        }
    }

    public static void showToastSuccessConfirm(Context context, String message, AlertListener alertListener) {
        if (context == null || TextUtils.isEmpty(message)) return;
        try {
            new SweetAlertDialog(context, SUCCESS)
                    .setTitleText(message)
                    .showCancelButton(false)
                    .showConfirmButton(true)
                    .setConfirmClickListener(sweetAlertDialog -> {
                        alertListener.onAlertConfirmed(true);
                        sweetAlertDialog.dismiss();
                    })
                    .show();
        } catch (Exception e) {
            ToastUtils.logException(e);
            showToastText(context, message);
        }
    }

    public static void showDialogSuccess(Context context, String message, AlertListener alertListener) {
        if (context == null || TextUtils.isEmpty(message)) return;
        try {
            new SweetAlertDialog(context, SUCCESS)
                    .setTitleText(message)
                    .showCancelButton(false)
                    .showConfirmButton(true)
                    .setConfirmClickListener(sweetAlertDialog -> {
                        alertListener.onAlertConfirmed(true);
                        sweetAlertDialog.dismiss();
                    })
                    .show();
        } catch (Exception e) {
            ToastUtils.logException(e);
            showToastText(context, message);
        }
    }

    public static void showToastSuccess(Context context, String message) {
        if (context == null || TextUtils.isEmpty(message)) return;
        try {
            new SweetAlertDialog(context, SUCCESS)
                    .setTitleText(message)
                    .showCancelButton(false)
                    .showConfirmButton(false)
                    .show();
        } catch (Exception e) {
            ToastUtils.logException(e);
            showToastText(context, message);
        }
    }


    public static void showToastSuccess(Context context, @StringRes int messageId) {
        if (context == null) return;
        try {
            new SweetAlertDialog(context, SUCCESS)
                    .setTitleText(context.getResources().getString(messageId))
                    .showCancelButton(false)
                    .showConfirmButton(false)
                    .show();
        } catch (Exception e) {
            ToastUtils.logException(e);
            showToastText(context, messageId);
        }
    }

    public static void showToastWarning(Context context, String message) {
        try {
            new SweetAlertDialog(context, WARNING)
                    .setTitleText(message)
                    .showCancelButton(false)
                    .showConfirmButton(false)
                    .show();
        } catch (Exception e) {
            ToastUtils.logException(e);
            showToastText(context, message);
        }
    }

    public static void showToastWarning(Context context, @StringRes int messageId) {
        try {
            new SweetAlertDialog(context, WARNING)
                    .setTitleText(context.getResources().getString(messageId))
                    .showCancelButton(false)
                    .showConfirmButton(false)
                    .show();
        } catch (Exception e) {
            ToastUtils.logException(e);
            showToastText(context, messageId);
        }
    }

    public static void showToastError(Context context, String message) {
        try {
            new SweetAlertDialog(context, ERROR)
                    .setTitleText(message)
                    .showCancelButton(false)
                    .showConfirmButton(false)
                    .show();
        } catch (Exception e) {
            ToastUtils.logException(e);
            showToastText(context, message);
        }
    }

    public static void showToastError(@Nullable Context context, @StringRes int messageId) {
        if (context == null) return;
        try {
            new SweetAlertDialog(context, ERROR)
                    .setTitleText(context.getResources().getString(messageId))
                    .showCancelButton(false)
                    .showConfirmButton(false)
                    .show();
        } catch (Exception e) {
            ToastUtils.logException(e);
            showToastText(context, messageId);
        }
    }

    public static void showToastText(Context context, @StringRes int messageId) {
        if (context == null) return;
        showToastText(context, context.getResources().getString(messageId));
    }

    public static void showToastText(Context context, String content) {
        if (context == null) return;
        try {
            CustomToastNotification customToastNotification = new CustomToastNotification(context);
            customToastNotification.setIcon(R.mipmap.ic_launcher).setTitle(context.getResources().getString(R.string.app_name));
            customToastNotification.setMessage(content);
            Toast toast = Toast.makeText(context, context.getResources().getString(R.string.app_name), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER | Gravity.FILL_HORIZONTAL, 0, 0);
            toast.setView(customToastNotification);
            toast.show();
        } catch (Exception e) {
            Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
        }
    }

    public static void showToastSuccessConfirm(Context context, @StringRes int messageId) {
        if (context == null) return;
        try {
            new SweetAlertDialog(context, SUCCESS)
                    .setTitleText(context.getResources().getString(messageId))
                    .showCancelButton(false)
                    .showConfirmButton(true)
                    .show();
        } catch (Exception e) {
            ToastUtils.logException(e);
            showToastText(context, messageId);
        }
    }

    public static void showToastWarningConfirm(Context context, String message) {
        try {
            new SweetAlertDialog(context, WARNING)
                    .setTitleText(message)
                    .showCancelButton(false)
                    .showConfirmButton(true)
                    .show();
        } catch (Exception e) {
            ToastUtils.logException(e);
            showToastText(context, message);
        }
    }

    public static void showToastWarningConfirm(Context context, @StringRes int messageId) {
        try {
            new SweetAlertDialog(context, WARNING)
                    .setTitleText(context.getResources().getString(messageId))
                    .showCancelButton(false)
                    .showConfirmButton(true)
                    .show();
        } catch (Exception e) {
            ToastUtils.logException(e);
            showToastText(context, messageId);
        }
    }

    public static void showToastConfirm(Context context, @StringRes int messageId) {
        try {
            new SweetAlertDialog(context, NORMAL)
                    .setTitleText(context.getResources().getString(messageId))
                    .showCancelButton(false)
                    .showConfirmButton(true)
                    .show();
        } catch (Exception e) {
            ToastUtils.logException(e);
            showToastText(context, messageId);
        }
    }

    public static void showToastErrorConfirm(Context context, String message) {
        try {
            new SweetAlertDialog(context, ERROR)
                    .setTitleText(message)
                    .showCancelButton(false)
                    .showConfirmButton(true)
                    .show();
        } catch (Exception e) {
            ToastUtils.logException(e);
            showToastText(context, message);
        }
    }

    public static SweetAlertDialog showToastErrorConfirm(Context context, @StringRes int messageId) {
        try {
            SweetAlertDialog alertDialog = new SweetAlertDialog(context, ERROR)
                    .setTitleText(context.getResources().getString(messageId))
                    .showCancelButton(false)
                    .showConfirmButton(true);
            alertDialog.show();
            return alertDialog;
        } catch (Exception e) {
            ToastUtils.logException(e);
            showToastText(context, messageId);
            return null;
        }
    }

}
