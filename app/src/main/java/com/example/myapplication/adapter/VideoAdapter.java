package com.example.myapplication.adapter;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.entity.VideoEntity;
import com.example.myapplication.view.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // 接受传进来的每一个entity数据
    private Context mContext;
    private List<VideoEntity> datas;

    public void setDatas(List<VideoEntity> datas) {
        this.datas = datas;
    }

    public VideoAdapter(Context context) {
        this.mContext = context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_video_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // 绑定数据，给对象赋值，例如点赞数+1，例如导入图片url并显示
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder vh = (ViewHolder) holder;
        VideoEntity videoEntity = datas.get(position);
        vh.tvTitle.setText(videoEntity.getVtitle());
        vh.tvAuthor.setText(videoEntity.getAuthor());
        vh.tvDz.setText(String.valueOf(videoEntity.getLikenum()));
        vh.tvComment.setText(String.valueOf(videoEntity.getCommentnum()));
        vh.tvCollect.setText(String.valueOf(videoEntity.getCollectnum()));

        // 利用Picasso导入图片链接绑定
        Picasso.with(mContext)
                .load(videoEntity.getHeadurl())
                .transform(new CircleTransform())
                .into(vh.imgHeader);
        Picasso.with(mContext).load(videoEntity.getCoverurl()).into(vh.imgCover);

    }

    @Override
    public int getItemCount() {
        if (datas != null && datas.size() > 0) {
            return datas.size();
        } else {
            return 0;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvAuthor;
        private TextView tvDz;
        private TextView tvComment;
        private TextView tvCollect;
        // 视频图片预加载
        private ImageView imgHeader;
        private ImageView imgCover;

        public ViewHolder(@NonNull View view) {
            super(view);
            tvTitle = view.findViewById(R.id.title);
            tvAuthor = view.findViewById(R.id.author);
            tvDz = view.findViewById(R.id.dz);
            tvComment = view.findViewById(R.id.comment);
            tvCollect = view.findViewById(R.id.collect);
            imgHeader = view.findViewById(R.id.img_header);
            imgCover = view.findViewById(R.id.img_cover);
        }
    }
}
