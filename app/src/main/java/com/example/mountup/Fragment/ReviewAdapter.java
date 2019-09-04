package com.example.mountup.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mountup.R;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder>{
    ArrayList<Review> items = new ArrayList<Review>();

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView m_textView_review_user_id;
        TextView m_textView_review_coment;
        ImageView m_imageView_review_iamge;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            m_textView_review_user_id = itemView.findViewById(R.id.tv_user_id);
            m_textView_review_coment = itemView.findViewById(R.id.tv_review_content);
            m_imageView_review_iamge = itemView.findViewById(R.id.iv_review_image);
        }

        public void setItem(Review item){
           m_textView_review_user_id.setText(item.getM_review_user_id());
           m_textView_review_coment.setText(item.getM_review_cotent());
           m_imageView_review_iamge.setImageBitmap(item.getM_review_image());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.review_item,parent,false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review item = items.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(Review item){
        items.add(item);
    }

    public void setItems(ArrayList<Review> items){
        this.items = items;
    }

    public Review getItem(int position){
        return items.get(position);
    }

    public void setItem(int position,Review item){
        items.set(position,item);
    }
}
