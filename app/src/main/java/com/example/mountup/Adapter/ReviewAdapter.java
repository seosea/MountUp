package com.example.mountup.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mountup.Model.Review;
import com.example.mountup.R;

import java.util.ArrayList;
import java.util.List;

import static android.graphics.BitmapFactory.decodeResource;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder>{
    ArrayList<Review> items = new ArrayList<Review>();

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView m_textView_user_id; // user id
        TextView m_textView_coment; // review coment
        TextView m_textView_like; // review how many people like review

        ImageView m_imageView_user_image;
        ImageView m_imageView_iamge; // review main image
        ImageButton m_imageButton_like;

        RatingBar m_ratingbar_grade;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            m_textView_user_id = itemView.findViewById(R.id.tv_user_id);
            m_textView_coment = itemView.findViewById(R.id.tv_review_content);
            m_textView_like = itemView.findViewById(R.id.tv_review_like);

            //m_imageView_user_image = itemView.findViewById(R.id.iv_review_user_profile);
            m_imageView_iamge = itemView.findViewById(R.id.iv_review_image);
            m_imageButton_like = itemView.findViewById(R.id.btn_review_heart_button);
            m_ratingbar_grade = itemView.findViewById(R.id.rb_review_grade);
        }

        public void setItem(Review item){
           m_textView_user_id.setText(item.getM_user_id());
           m_textView_coment.setText(item.getM_cotent());
           m_textView_like.setText("like : "+item.getM_like());
           //m_imageView_user_image.setImageBitmap(item.getM_main_image());

            if(item.isM_mylike() == true){
                m_imageButton_like.setImageResource(R.drawable.heart);
            }
            else{
                m_imageButton_like.setImageResource(R.drawable.heart_uncheck);
            }
            m_ratingbar_grade.setRating((float)item.getM_grade());
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

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Review> list) {
        items.addAll(list);
        notifyDataSetChanged();
    }
}
