package com.example.myapplication.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dueeeke.videoplayer.player.VideoViewManager;

import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.content.Context.MODE_PRIVATE;

public abstract class BaseFragment extends Fragment {
    // 把Fragment共有的方法集成此处
    protected View mRootView;
    // 使用butterknife
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(mRootView == null) {
            mRootView = inflater.inflate(initLayout(), container, false);
            initView();
        }
        unbinder = ButterKnife.bind(this, mRootView);
        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    protected abstract int initLayout();

    protected abstract void initView();

    protected abstract void initData();

    public void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    //子线程打印Toast,不报错
    public void showToastSync(String msg) {
        Looper.prepare();
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        Looper.loop();
    }

    public void navigateTo(Class cls) {
        Intent in = new Intent(getActivity(), cls);
        startActivity(in);
    }

    public void navigateToWithFlags(Class cls, int flags) {
        Intent in = new Intent(getActivity(), cls);
        in.setFlags(flags);
        startActivity(in);
    }

    protected void insertVal(String key, String val) {
        SharedPreferences sp = getActivity().getSharedPreferences("sp_flash", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, val);
        editor.commit();
    }

    protected String findByKey(String key) {
        SharedPreferences sp = getActivity().getSharedPreferences("sp_flash", MODE_PRIVATE);
        return sp.getString(key, "");
    }

    protected void reomveByKey(String key) {
        SharedPreferences sp = getActivity().getSharedPreferences("sp_flash", MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.remove(key);
        edit.commit();
    }

    protected VideoViewManager getVideoViewManager() {
        return VideoViewManager.instance();
    }
}
