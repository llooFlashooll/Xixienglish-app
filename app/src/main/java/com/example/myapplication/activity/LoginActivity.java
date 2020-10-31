package com.example.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.api.Api;
import com.example.myapplication.api.ApiConfig;
import com.example.myapplication.api.HttpCallBack;
import com.example.myapplication.entity.LoginResponse;
import com.example.myapplication.util.AppConfig;
import com.example.myapplication.util.StringUtils;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends BaseActivity {

    private EditText etAccount;
    private EditText etPwd;
    private Button btnLogin;


    @Override
    protected int initLayout() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        etAccount = findViewById(R.id.et_account);
        etPwd = findViewById(R.id.et_pwd);
        btnLogin = findViewById(R.id.btn_login);
    }

    @Override
    protected void initData() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = etAccount.getText().toString().trim();
                String pwd = etPwd.getText().toString().trim();
                login(account, pwd);
            }
        });
    }

    private void login(String account, String pwd) {
        if (StringUtils.isEmpty(account)) {
            showToast("请输入账号");
            return;
        }
        if (StringUtils.isEmpty(pwd)) {
            showToast("请输入密码");
            return;
        }

        navigateTo(HomeActivity.class);

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("mobile", account);
        params.put("password", pwd);
        Api.config(ApiConfig.LOGIN, params).postRequest(new HttpCallBack() {
            @Override
            public void onSuccess(final String res) {
                Log.e("onSuccess", res);
                showToastSync(res);
                // Gson库封装拿token
                Gson gson = new Gson();
                LoginResponse loginResponse = gson.fromJson(res, LoginResponse.class);
                if (loginResponse.getCode() == 0) {
                    String token = loginResponse.getToken();
                    // 应用sharedPreference存键值对
                    insertVal("token", token);
                    // 登陆成功跳转至首页
//                    navigateTo(HomeActivity.class);
                    navigateToWithFlags(HomeActivity.class,
                            Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    showToastSync("登录成功");
                } else {
                    showToastSync("登录失败");
                }
            }

            @Override
            public void onFailure(Exception e) {

            }
        });

        /*//网络部分
        //第一步创建OKHttpClient
        OkHttpClient client = new OkHttpClient.Builder()
                .build();
        Map m = new HashMap();
        m.put("mobile", account);
        m.put("password", pwd);
        JSONObject jsonObject = new JSONObject(m);
        String jsonStr = jsonObject.toString();
        RequestBody requestBodyJson =
                RequestBody.create(MediaType.parse("application/json;charset=utf-8")
                        , jsonStr);
        //第三步创建Request
        Request request = new Request.Builder()
                .url(AppConfig.BASE_URl + "/app/login")
                .addHeader("contentType", "application/json;charset=utf-8")
                .post(requestBodyJson)
                .build();
        //第四步创建call回调对象
        final Call call = client.newCall(request);
        //第五步发起请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("onFailure", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result = response.body().string();
                //不在主线程执行
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast(result);
                    }
                });
            }
        });*/



    }
}