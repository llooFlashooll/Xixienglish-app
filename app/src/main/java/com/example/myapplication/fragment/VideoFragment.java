package com.example.myapplication.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.activity.LoginActivity;
import com.example.myapplication.adapter.VideoAdapter;
import com.example.myapplication.api.Api;
import com.example.myapplication.api.ApiConfig;
import com.example.myapplication.api.HttpCallBack;
import com.example.myapplication.entity.VideoEntity;
import com.example.myapplication.entity.VideoListResponse;
import com.example.myapplication.util.StringUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class VideoFragment extends BaseFragment {

    private String title;
    private RecyclerView recyclerView;

    public VideoFragment() {
        // Required empty public constructor
    }

    public static VideoFragment newInstance(String title) {
        VideoFragment fragment = new VideoFragment();
        fragment.title = title;
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_video, container, false);
        recyclerView = v.findViewById(R.id.recyclerView);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        // 本地模拟数据，待转换为接口
/*        List<VideoEntity> datas = new ArrayList<>();
        for(int i = 0; i < 8; i++) {
            VideoEntity ve = new VideoEntity();
            ve.setTitle("韭菜盒子新做法，不发面不发烫");
            ve.setName("大胃王");
            ve.setDzCount(i*2);
            ve.setCollectCount(i*4);
            ve.setCommentCount(i*6);
            datas.add(ve);
        }*/
        getVideoList();

    }

    // 通过接口获取视频数据
    private void getVideoList() {
        String token = getStringFromSp("token");
        if(!StringUtils.isEmpty(token)) {
            HashMap<String, Object> params = new HashMap<>();
            params.put("token", token);
            Api.config(ApiConfig.VIDEO_LIST, params).getRequest(new HttpCallBack() {
                @Override
                public void onSuccess(String res) {
                    // 获取视频数据后则传入VideoAdapter
//                    VideoAdapter videoAdapter = new VideoAdapter(getActivity(), datas);
//                    recyclerView.setAdapter(videoAdapter);
                    // 通过Gson库进行转换??
                    VideoListResponse response = new Gson().fromJson(res, VideoListResponse.class);
                    if (response != null && response.getCode() == 0) {
                        List<VideoEntity> datas = response.getPage().getList();
                        VideoAdapter videoAdapter = new VideoAdapter(getActivity(), datas);
                        recyclerView.setAdapter(videoAdapter);
                    }
                    showToastSync(res);
                }

                @Override
                public void onFailure(Exception e) {

                }
            });
        } else {
            navigateTo(LoginActivity.class);
        }
    }
}