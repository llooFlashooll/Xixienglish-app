package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dueeeke.videocontroller.component.PrepareView;
import com.example.myapplication.R;
import com.example.myapplication.entity.NewsEntity;
import com.example.myapplication.entity.VideoEntity;
import com.example.myapplication.listener.OnItemChildClickListener;
import com.example.myapplication.listener.OnItemClickListener;
import com.example.myapplication.view.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // 接受传进来的每一个entity数据
    private Context mContext;
    private List<NewsEntity> datas;

    public void setDatas(List<NewsEntity> datas) {
        this.datas = datas;
    }

    public NewsAdapter(Context context) {
        this.mContext = context;
    }

    public NewsAdapter(Context context, List<NewsEntity> datas) {
        this.mContext = context;
        this.datas = datas;
    }

    @Override
    public int getItemViewType(int position) {
        int type = datas.get(position).getType();
        return type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.news_item_one, parent, false);
            return new ViewHolderOne(view);
        } else if (viewType == 2) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.news_item_two, parent, false);
            return new ViewHolderTwo(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.news_item_three, parent, false);
            return new ViewHolderThree(view);
        }
    }

    // 绑定数据，给对象赋值，例如点赞数+1，例如导入图片url并显示
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        if (type == 1) {
            ViewHolderOne vh = (ViewHolderOne) holder;
        } else if (type == 2) {
            ViewHolderTwo vh = (ViewHolderTwo) holder;
        } else {
            ViewHolderThree vh = (ViewHolderThree) holder;
        }
        // 得到ViewHolder后类似Video应该给界面绑定数据
        NewsEntity newsEntity = datas.get(position);
    }

    @Override
    public int getItemCount() {
        if (datas != null && datas.size() > 0) {
            return datas.size();
        } else {
            return 0;
        }
    }

    public class ViewHolderOne extends RecyclerView.ViewHolder{

        public ViewHolderOne(@NonNull View view) {
            super(view);
        }
    }

    public class ViewHolderTwo extends RecyclerView.ViewHolder{

        public ViewHolderTwo(@NonNull View view) {
            super(view);
        }
    }

    public class ViewHolderThree extends RecyclerView.ViewHolder{

        public ViewHolderThree(@NonNull View view) {
            super(view);
        }
    }

}
