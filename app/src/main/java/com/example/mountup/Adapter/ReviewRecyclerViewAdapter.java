package com.example.mountup.Adapter;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mountup.Singleton.MountManager;
import com.example.mountup.VO.MountVO;
import com.example.mountup.VO.ReviewVO;
import com.example.mountup.R;

import java.util.ArrayList;
import java.util.List;

import static androidx.recyclerview.widget.RecyclerView.*;

public class ReviewRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<ReviewVO> m_reivewItems;
    private OnLoadMoreListener onLoadMoreListener;
    private boolean isLoading;
    private int lastVisibleItem, totalItemCount;
    private int visibleThreshold = 2;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public int getItemViewType(int position) { //null값인 경우 로딩타입
        return m_reivewItems.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public ReviewRecyclerViewAdapter(Context context,RecyclerView recyclerView, ArrayList<ReviewVO> reviewItems, OnLoadMoreListener onLoadMoreListener1) {
        this.m_reivewItems = reviewItems;
        this.onLoadMoreListener = onLoadMoreListener1;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                //로딩중이아니고 , 전체아이템수 <= 마지막에 보이는 아이템인덱스 + 화면에보이는개수(리사이클러뷰에 아이템이 5개씩 보이므로 5로 설정함
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (onLoadMoreListener != null) {
                        Log.d("smh:scrolled","LoadMore");

                        onLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }//생산자

    @Override
    public int getItemCount() {
        return m_reivewItems.size();
    }

    public ArrayList<ReviewVO> getItems() {
        return m_reivewItems;
    }

    public void clear() {
        m_reivewItems.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<ReviewVO> list) {
        m_reivewItems.addAll(list);
        notifyDataSetChanged();
    }
    public void setLoaded() {
        isLoading = false;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
            return new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }//아이템 뷰를 위한 뷰홀더 객체 생성

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            final ReviewVO item = m_reivewItems.get(position);

            ((ItemViewHolder) holder).m_imageButton_like.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    int like;
                    if(item.isPic() == true){
                        item.setPic(false);

                        item.setLike(item.getLike()-1);

                        Log.d("like",""+item.getLike());
                        ((ItemViewHolder) holder).m_textView_like.setText(String.valueOf(item.getLike()));
                        ((ItemViewHolder) holder).m_imageButton_like.setImageResource(R.drawable.heart_uncheck);
                    }
                    else{
                        item.setPic(true);
                        item.setLike(item.getLike()+1);
                        ((ItemViewHolder) holder).m_textView_like.setText(String.valueOf(item.getLike()));
                        ((ItemViewHolder) holder).m_imageButton_like.setImageResource(R.drawable.heart);
                        Log.d("like",""+item.getLike());
                    }
                }
            });

            ((ItemViewHolder) holder).setItem(item);
        } else if (holder instanceof LoadingViewHolder) {
            ((LoadingViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }//position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시


    static class ItemViewHolder extends ViewHolder {
        TextView m_textView_user_id; // user id
        TextView m_textView_coment; // review coment
        TextView m_textView_like; // review how many people like review
        TextView m_textView_mount_name;
        ImageView m_imageView_user_image;
        ImageView m_imageView_iamge; // review main image
        ImageButton m_imageButton_like;

        RatingBar m_ratingbar_grade;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            m_textView_user_id = itemView.findViewById(R.id.tv_user_id);
            m_textView_coment = itemView.findViewById(R.id.tv_review_content);
            m_textView_like = itemView.findViewById(R.id.tv_review_like);
            m_textView_mount_name = itemView.findViewById(R.id.txt_mount_name_review);

            //m_imageView_user_image = itemView.findViewById(R.id.iv_review_user_profile);
            m_imageView_iamge = itemView.findViewById(R.id.iv_review_image);
            m_imageButton_like = itemView.findViewById(R.id.btn_review_heart_button);
            m_ratingbar_grade = itemView.findViewById(R.id.rb_review_grade);
        }

        public void setItem(final ReviewVO item) {
            m_textView_user_id.setText(item.getUserId());
            m_textView_coment.setText(item.getCotent());
            m_textView_like.setText(String.valueOf(item.getLike()));
            for(MountVO mount : MountManager.getInstance().getItems()){
                if(mount.getID() == item.getM_mntID()){
                    m_textView_mount_name.setText(String.valueOf(item.getM_mntID()));
                }
            }

            m_ratingbar_grade.setRating((float) item.getGrade());
            //m_imageView_user_image.setImageBitmap(item.getM_main_image());
            if (item.isPic() == true) {
                m_imageButton_like.setImageResource(R.drawable.heart);
            } else {
                m_imageButton_like.setImageResource(R.drawable.heart_uncheck);
            }
            m_imageView_iamge.setImageBitmap(item.getImage());

            m_imageView_iamge.setBackground(new ShapeDrawable(new OvalShape()));
            if(Build.VERSION.SDK_INT >= 21) {
                m_imageView_iamge.setClipToOutline(true);
            }
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar_review);
        }
    }
}