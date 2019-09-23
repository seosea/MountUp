package com.example.mountup.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mountup.Activity.MountDetailActivity;
import com.example.mountup.R;
import com.example.mountup.VO.MountVO;

import java.util.ArrayList;

public class MountListRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context m_context;
    private ArrayList<MountVO> m_mountItems;
    private ArrayList<MountVO> m_filteredItems;

    private OnLoadMoreListener onLoadMoreListener;

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private boolean isMoreLoading = true;

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public MountListRecyclerViewAdapter(Context context, OnLoadMoreListener onLoadMoreListener) {
        this.m_context = context;
        this.onLoadMoreListener = onLoadMoreListener;
        m_filteredItems = new ArrayList();
        m_mountItems = new ArrayList();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            return new MountListViewHolder(LayoutInflater.from(m_context).inflate(R.layout.item_mount, parent, false));
        } else {
            return new ProgressViewHolder(LayoutInflater.from(m_context).inflate(R.layout.item_progress, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MountListViewHolder) {
            final MountVO mountVO = m_mountItems.get(position);

            ((MountListViewHolder) holder).iv_mountThumbnail.setImageBitmap(mountVO.getThumbnail());
            ((MountListViewHolder) holder).tv_mountName.setText(mountVO.getName());
            ((MountListViewHolder) holder).tv_mountHeight.setText(Integer.toString(mountVO.getHeight()) + "m");
            ((MountListViewHolder) holder).tv_mountDistance.setText(Float.toString(mountVO.getDistance()) + "km");
            ((MountListViewHolder) holder).tv_mountGrade.setText(Float.toString(mountVO.getGrade()));
            ((MountListViewHolder) holder).iv_isClimbed.setImageResource(R.drawable.ic_flag_isclimbed);

            if (mountVO.isClimbed()) {
                ((MountListViewHolder) holder).iv_isClimbed.setVisibility(View.VISIBLE);
            } else {
                ((MountListViewHolder) holder).iv_isClimbed.setVisibility(View.INVISIBLE);
            }

            ((MountListViewHolder) holder).rb_mountGrade.setRating(mountVO.getGrade());

            ((MountListViewHolder) holder).layout_mountPanel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(m_context, mountVO.getName(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(m_context, MountDetailActivity.class);
                    intent.putExtra("MountID", Integer.toString(mountVO.getID()));
                    //intent.putExtra("name", mountVO.getName());
                    //intent.putExtra("height", Integer.toString(mountVO.getHeight()));
                    //intent.putExtra("distance", Float.toString(mountVO.getDistance()));
                    //intent.putExtra("grade", Float.toString(mountVO.getGrade()));
                    //intent.putExtra("isClimbed", Boolean.toString(mountVO.isClimbed()));
                    //Log.d("mee:MountListAdapter", "isClimbed : " + Boolean.toString(mountVO.isClimbed()));

                    m_context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return m_mountItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return m_mountItems.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    public ArrayList<MountVO> getItems() {
        return m_mountItems;
    }

    public void showLoading() {
        if (isMoreLoading && m_mountItems != null && onLoadMoreListener != null) {
            Log.d("mee:MountListAdapter", "showLoading");
            isMoreLoading = false;
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    m_mountItems.add(null); // 무슨 의미? 결국 하나 추가했다 지워주는데 나중에 이해해보자
                    notifyItemInserted(m_mountItems.size() - 1); // -1?
                    onLoadMoreListener.onLoadMore();
                }
            });
        }
    }

    public void addAll(ArrayList<MountVO> items) {
        m_mountItems.clear();
        m_mountItems.addAll(items);
        notifyDataSetChanged();
    }

    public void dismissLoading() {
        if (m_mountItems != null && m_mountItems.size() > 0) {
            m_mountItems.remove(m_mountItems.size() - 1);
            notifyItemRemoved(m_mountItems.size());
        }
    }

    public void addItemMore(ArrayList<MountVO> items) {
        int sizeInit = m_mountItems.size();
        m_mountItems.addAll(items);
        notifyItemRangeChanged(sizeInit, m_mountItems.size());
    }

    public void setMore(boolean isMore) { this.isMoreLoading = isMore; }

    public void filterList(ArrayList<MountVO> filteredItems) {
        m_mountItems.clear();
        m_mountItems = filteredItems;
        notifyDataSetChanged();
    }

    public static class MountListViewHolder extends RecyclerView.ViewHolder {

        private CardView layout_mountPanel;
        private ImageView iv_mountThumbnail;
        private TextView tv_mountName;
        private TextView tv_mountHeight;
        private TextView tv_mountDistance;
        private TextView tv_mountGrade;
        private ImageView iv_isClimbed;
        private RatingBar rb_mountGrade;
        //private boolean isClimbed;

        public MountListViewHolder(View convertView) {
            super(convertView);

            layout_mountPanel = (CardView) convertView.findViewById(R.id.layout_mountPanel);
            iv_mountThumbnail = (ImageView) convertView.findViewById(R.id.iv_mountThumbnail);
            tv_mountName = (TextView) convertView.findViewById(R.id.tv_mountName);
            tv_mountHeight = (TextView) convertView.findViewById(R.id.tv_mountHeight);
            tv_mountDistance = (TextView) convertView.findViewById(R.id.tv_mountDistance);
            tv_mountGrade = (TextView) convertView.findViewById(R.id.tv_mountGrade);
            iv_isClimbed = (ImageView) convertView.findViewById(R.id.iv_climbed);
            rb_mountGrade = (RatingBar) convertView.findViewById(R.id.rb_mountGrade);
            /*
            rb_mountGrade.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating,
                                            boolean fromUser) {
                    tv_mountGrade.setText(rating + "");
                }
            });*/
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar pBar;

        public ProgressViewHolder(View convertView) {
            super(convertView);
            pBar = (ProgressBar) convertView.findViewById(R.id.pBar);
        }
    }
}
