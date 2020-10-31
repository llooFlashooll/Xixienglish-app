package com.example.myapplication.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;
import com.example.myapplication.activity.LoginActivity;
import com.example.myapplication.adapter.NewsAdapter;
import com.example.myapplication.adapter.VideoAdapter;
import com.example.myapplication.api.Api;
import com.example.myapplication.api.ApiConfig;
import com.example.myapplication.api.HttpCallBack;
import com.example.myapplication.entity.NewsEntity;
import com.example.myapplication.entity.NewsListResponse;
import com.example.myapplication.entity.VideoEntity;
import com.example.myapplication.entity.VideoListResponse;
import com.example.myapplication.util.StringUtils;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class NewsFragment extends BaseFragment {

    private RecyclerView recyclerView;
    private RefreshLayout refreshLayout;
    private NewsAdapter newsAdapter;
    private List<NewsEntity> datas = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private int pageNum = 1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            // 通知页面刷新数据
            switch (msg.what) {
                case 0:
                    newsAdapter.setDatas(datas);
                    newsAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    public NewsFragment() {
    }

    public static NewsFragment newInstance() {
        NewsFragment fragment = new NewsFragment();
        return fragment;
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_news;
    }

    @Override
    protected void initView() {
        recyclerView = mRootView.findViewById(R.id.recyclerView);
        refreshLayout = mRootView.findViewById(R.id.refreshLayout);

    }

    @Override
    protected void initData() {
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        newsAdapter = new NewsAdapter(getActivity());
        recyclerView.setAdapter(newsAdapter);
        newsAdapter.setOnItemClickListener(new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Serializable obj) {
                // 此处可以编写与H5页面的跳转，待改***
                showToast("点击跳转资讯");
                // 可以通过直接访问https页面
            }
        });
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                pageNum = 1;
                getNewsList(true);

            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(2000);
                pageNum ++;
                getNewsList(false);
            }
        });
    }

    // 通过接口获取资讯数据，类似VideoFragment
    private void getNewsList(final boolean isRefresh) {
        HashMap<String, Object> params = new HashMap<>();
        // 添加参数
        params.put("page", pageNum);
        params.put("limit", ApiConfig.PAGE_SIZE);
        Api.config(ApiConfig.NEWS_LIST, params).getRequest(getActivity(), new HttpCallBack() {
            @Override
            public void onSuccess(final String res) {
                // 页面UI在线程里执行
                // 页面上拉刷新成功
                if (isRefresh) {
                    refreshLayout.finishRefresh(true);
                } else {
                    refreshLayout.finishLoadMore(true);
                }

                NewsListResponse response = new Gson().fromJson(res, NewsListResponse.class);
                if (response != null && response.getCode() == 0) {
                    List<NewsEntity> list = response.getPage().getList();
                    if (list != null && list.size() > 0) {
                        // 刷新添加数据
                        if(isRefresh) {
                            datas = list;
                        } else {
                            datas.addAll(list);
                        }
                        //通过mHandler处理线程问题
                        mHandler.sendEmptyMessage(0);

                    } else {
                        if(isRefresh) {
                            showToastSync("暂时无数据");
                        } else {
                            showToastSync("没有更多数据");
                        }
                    }
                }
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
    }
}