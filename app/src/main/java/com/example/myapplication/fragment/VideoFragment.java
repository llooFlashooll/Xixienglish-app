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
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class VideoFragment extends BaseFragment {

    private String title;
    private RecyclerView recyclerView;
    private RefreshLayout refreshLayout;
    private int pageNum = 1;
    private VideoAdapter videoAdapter;
    private List<VideoEntity> datas = new ArrayList<>();

    public VideoFragment() {
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
        refreshLayout = v.findViewById(R.id.refreshLayout);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        videoAdapter = new VideoAdapter(getActivity());
        recyclerView.setAdapter(videoAdapter);

        // refreshLayout 即实现上拉 下拉界面刷新动态效果
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
//                refreshLayout.finishRefresh(2000);
                // 下拉时刷新请求视频
                pageNum = 1;
                getVideoList(true);

            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(2000);
                pageNum ++;
                getVideoList(false);
            }
        });

        getVideoList(true);

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

    }

    // 通过接口获取视频数据
    private void getVideoList(final boolean isRefresh) {
        String token = getStringFromSp("token");
        if(!StringUtils.isEmpty(token)) {
            HashMap<String, Object> params = new HashMap<>();
            // 添加参数
            params.put("token", token);
            params.put("page", pageNum);
            params.put("limit",ApiConfig.PAGE_SIZE);
            Api.config(ApiConfig.VIDEO_LIST, params).getRequest(new HttpCallBack() {
                @Override
                public void onSuccess(final String res) {
                    // 页面UI在线程里执行
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 页面上拉刷新成功
                            if (isRefresh) {
                                refreshLayout.finishRefresh(true);
                            } else {
                                refreshLayout.finishLoadMore(true);
                            }
                            // 获取视频数据后则传入VideoAdapter
//                    VideoAdapter videoAdapter = new VideoAdapter(getActivity(), datas);
//                    recyclerView.setAdapter(videoAdapter);
                            // 通过Gson库进行转换??
                            VideoListResponse response = new Gson().fromJson(res, VideoListResponse.class);
                            if (response != null && response.getCode() == 0) {
                                List<VideoEntity> list = response.getPage().getList();
                                if (list != null && list.size() > 0) {
                                    // 刷新添加数据
                                    if(isRefresh) {
                                        datas = list;
                                    } else {
                                        datas.addAll(list);
                                    }
                                    // 通知页面刷新数据
                                    videoAdapter.setDatas(datas);
                                    videoAdapter.notifyDataSetChanged();
                                } else {
                                    if(isRefresh) {
                                        showToast("暂时无数据");
                                    } else {
                                        showToast("没有更多数据");
                                    }
                                }
                            }
                        }
                    });
                }

                @Override
                public void onFailure(Exception e) {
                    // 上下拉刷新动画关闭
                    if(isRefresh) {
                        refreshLayout.finishRefresh(true);
                    } else {
                        refreshLayout.finishLoadMore(true);
                    }
                }
            });
        } else {
            navigateTo(LoginActivity.class);
        }
    }
}