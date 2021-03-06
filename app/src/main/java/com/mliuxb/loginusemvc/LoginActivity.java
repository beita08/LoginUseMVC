package com.mliuxb.loginusemvc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mliuxb.loginusemvc.bean.LoginData;
import com.mliuxb.loginusemvc.global.Constants;
import com.mliuxb.loginusemvc.global.MD5Utils;
import com.mliuxb.loginusemvc.model.LoginModel;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";

    private EditText    etPhoneNumber;
    private EditText    etPassword;
    private Button      btnLogin;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etPhoneNumber = (EditText) findViewById(R.id.et_phone_number);
        etPassword = (EditText) findViewById(R.id.et_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:    //登录
                String phoneNumber = etPhoneNumber.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                login(phoneNumber, password);
                break;
            default:
                break;
        }
    }

    private void login(String phoneNumber, String password) {
        //本地对输入情况做校验
        boolean validateOk = validateInput(phoneNumber, password);
        if (validateOk) {
            progressBar.setVisibility(View.VISIBLE);
            String md5Password = MD5Utils.getMd5(password);

            LoginModel loginModel = new LoginModel();
            loginModel.gotoLogin(phoneNumber, md5Password, new LoginModel.OnNetResponseListener() {
                @Override
                public void onNetResposeError(String msg) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNetReponseSuccess(LoginData loginData) {
                    progressBar.setVisibility(View.GONE);
                    switch (loginData.status) {
                        case 200: //用户名未注册
                        case 201: //密码有误
                        case 203: //登录失败
                            Toast.makeText(LoginActivity.this, loginData.message, Toast.LENGTH_SHORT).show();
                            Log.i(TAG, "onResponse: = " + loginData.message);
                            break;
                        case 202:   //登录成功
                            Toast.makeText(LoginActivity.this, loginData.message, Toast.LENGTH_SHORT).show();
                            Log.i(TAG, "onResponse: = " + loginData.message);

                            //本地保存必要的用户信息
                            //......

                            Intent intent = new Intent(LoginActivity.this, LoginSuccessActivity.class);
                            startActivity(intent);
                            //登录页面直接消失
                            finish();
                            break;
                        default:
                            Toast.makeText(LoginActivity.this, "登录出现未知异常", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });

        }
    }

    private boolean validateInput(String phoneNumber, String password) {
        if (TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(this, "手机号不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!phoneNumber.matches(Constants.STR_PHONE_REGEX2)) {  //匹配正则表达式
            Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}