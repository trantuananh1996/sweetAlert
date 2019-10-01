package com.pr.sweetalert;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.pr.swalert.SweetAlertDialog;
import com.pr.swalert.toast.ToastUtils;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ToastUtils.showToastConfirm(this,"TestMessage");
    }
}
