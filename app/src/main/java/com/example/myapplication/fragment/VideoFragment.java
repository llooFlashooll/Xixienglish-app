package com.example.myapplication.fragment;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videocontroller.component.CompleteView;
import com.dueeeke.videocontroller.component.ErrorView;
import com.dueeeke.videocontroller.component.GestureView;
import com.dueeeke.videocontroller.component.TitleView;
import com.dueeeke.videocontroller.component.VodControlView;
import com.dueeeke.videoplayer.player.VideoView;
import com.example.myapplication.R;
import com.example.myapplication.activity.LoginActivity;
import com.example.myapplication.adapter.VideoAdapter;
import com.example.myapplication.api.Api;
import com.example.myapplication.api.ApiConfig;
import com.example.myapplication.api.HttpCallBack;
import com.example.myapplication.entity.VideoEntity;
import com.example.myapplication.entity.VideoListResponse;
import com.example.myapplication.listener.OnItemChildClickListener;
import com.example.myapplication.util.StringUtils;
import com.example.myapplication.util.Tag;
import com.example.myapplication.util.Utils;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class VideoFragment extends BaseFragment implements OnItemChildClickListener {

    private String title;
    private RecyclerView recyclerView;
    private RefreshLayout refreshLayout;
    private int pageNum = 1;
    private VideoAdapter videoAdapter;
    private List<VideoEntity> datas = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;

    protected VideoView mVideoView;
    protected StandardVideoController mController;
    protected ErrorView mErrorView;
    protected CompleteView mCompleteView;
    protected TitleView mTitleView;

    // 当前播放位置
    protected int mCurPos = -1;
    // 上次播放位置，用于页面切回来之后恢复播放
    protected int mLastPos = mCurPos;

    public VideoFragment() {
    }

    public static VideoFragment newInstance(String title) {
        VideoFragment fragment = new VideoFragment();
        fragment.title = title;
        return fragment;
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_video;
    }

    @Override
    protected void initView() {
        initVideoView();
        recyclerView = mRootView.findViewById(R.id.recyclerView);
        refreshLayout = mRootView.findViewById(R.id.refreshLayout);
    }

    @Override
    protected void initData() {
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        videoAdapter = new VideoAdapter(getActivity());
        videoAdapter.setOnItemChildClickListener(this);
        recyclerView.setAdapter(videoAdapter);
        // recyclerView添加视频播放器事件
        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                // 获取视频播放器
                FrameLayout playerContainer = view.findViewById(R.id.player_container);
                View v = playerContainer.getChildAt(0);
                if (v != null && v == mVideoView && !mVideoView.isFullScreen()) {
                    releaseVideoView();
                }
            }
        });

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

    protected void initVideoView() {
        mVideoView = new VideoView(getActivity());
        mVideoView.setOnStateChangeListener(new com.dueeeke.videoplayer.player.VideoView.SimpleOnStateChangeListener() {
            @Override
            public void onPlayStateChanged(int playState) {
                //监听VideoViewManager释放，重置状态
                if (playState == com.dueeeke.videoplayer.player.VideoView.STATE_IDLE) {
                    Utils.removeViewFormParent(mVideoView);
                    mLastPos = mCurPos;
                    mCurPos = -1;
                }
            }
        });
        mController = new StandardVideoController(getActivity());
        mErrorView = new ErrorView(getActivity());
        mController.addControlComponent(mErrorView);
        mCompleteView = new CompleteView(getActivity());
        mController.addControlComponent(mCompleteView);
        mTitleView = new TitleView(getActivity());
        mController.addControlComponent(mTitleView);
        mController.addControlComponent(new VodControlView(getActivity()));
        mController.addControlComponent(new GestureView(getActivity()));
        mController.setEnableOrientation(true);
        mVideoView.setVideoController(mController);
    }

    @Override
    public void onPause() {
        super.onPause();
        pause();
    }

    /**
     * 由于onPause必须调用super。故增加此方法，
     * 子类将会重写此方法，改变onPause的逻辑
     */
    protected void pause() {
        releaseVideoView();
    }

    @Override
    public void onResume() {
        super.onResume();
        resume();
    }

    /**
     * 由于onResume必须调用super。故增加此方法，
     * 子类将会重写此方法，改变onResume的逻辑
     */
    protected void resume() {
        if (mLastPos == -1)
            return;
        //恢复上次播放的位置
        startPlay(mLastPos);
    }

    /**
     * PrepareView被点击
     */
    @Override
    public void onItemChildClick(int position) {
        startPlay(position);
    }

    /**
     * 开始播放
     *
     * @param position 列表位置
     */
    protected void startPlay(int position) {
        if (mCurPos == position) return;
        if (mCurPos != -1) {
            releaseVideoView();
        }
        VideoEntity videoEntity = datas.get(position);
        //边播边存
//        String proxyUrl = ProxyVideoCacheManager.getProxy(getActivity()).getProxyUrl(videoBean.getUrl());
//        mVideoView.setUrl(proxyUrl);

        mVideoView.setUrl(videoEntity.getPlayurl());
        mTitleView.setTitle(videoEntity.getVtitle());
        View itemView = linearLayoutManager.findViewByPosition(position);
        if (itemView == null) return;
        VideoAdapter.ViewHolder viewHolder = (VideoAdapter.ViewHolder) itemView.getTag();
        //把列表中预置的PrepareView添加到控制器中，注意isPrivate此处只能为true。
        mController.addControlComponent(viewHolder.mPrepareView, true);
        Utils.removeViewFormParent(mVideoView);
        viewHolder.mPlayerContainer.addView(mVideoView, 0);
        //播放之前将VideoView添加到VideoViewManager以便在别的页面也能操作它
        getVideoViewManager().add(mVideoView, Tag.LIST);
        mVideoView.start();
        mCurPos = position;

    }

    private void releaseVideoView() {
        mVideoView.release();
        if (mVideoView.isFullScreen()) {
            mVideoView.stopFullScreen();
        }
        if (getActivity().getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        mCurPos = -1;
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