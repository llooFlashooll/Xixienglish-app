package com.example.myapplication.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.example.myapplication.R;
import com.example.myapplication.activity.LoginActivity;

import butterknife.BindView;
import butterknife.OnClick;


public class MyFragment extends BaseFragment {

    @BindView(R.id.img_header)
    ImageView imgHeader;

    public MyFragment() {
    }

    public static MyFragment newInstance() {
        MyFragment fragment = new MyFragment();
        return fragment;
    }


    @Override
    protected int initLayout() {
        return R.layout.fragment_my;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.img_header, R.id.rl_collect, R.id.rl_skin, R.id.rl_logout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_header:
                break;
            case R.id.rl_collect:
                break;
            case R.id.rl_skin:
                break;
            case R.id.rl_logout:
//                showToast("退出登录");
                reomveByKey("token");
                navigateToWithFlags(LoginActivity.class,
                        Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                break;
        }
    }
}