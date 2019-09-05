package com.example.mountup.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mountup.R;
import com.example.mountup.VO.MountVO;

import java.util.ArrayList;

public class MountListRecyclerViewAdapter extends RecyclerView.Adapter<MountListRecyclerViewAdapter.MountListViewHolder> {

    private Context m_context;
    private ArrayList<MountVO> m_mountItems;

    public MountListRecyclerViewAdapter(Context m_context) {
        this.m_context = m_context;
        //this.m_mountItems = m_mountItems;
        m_mountItems = new ArrayList<>();
    }

    public static class MountListViewHolder extends RecyclerView.ViewHolder {

        private CardView layout_mountPanel;
        private ImageView iv_mountThumbnail;
        private TextView tv_mountName;
        private TextView tv_mountHeight;
        private TextView tv_mountDistance;
        private TextView tv_mountGrade;
        private boolean m_bClimb;

        public MountListViewHolder(View convertView) {
            super(convertView);

            layout_mountPanel = (CardView) convertView.findViewById(R.id.layout_mountPanel);
            iv_mountThumbnail = (ImageView) convertView.findViewById(R.id.img_mountThumbnail);
            tv_mountName = (TextView) convertView.findViewById(R.id.tv_mountName);
            tv_mountHeight = (TextView) convertView.findViewById(R.id.tv_mountHeight);
            tv_mountDistance = (TextView) convertView.findViewById(R.id.tv_mountDistance);
            tv_mountGrade = (TextView) convertView.findViewById(R.id.tv_mountGrade);
        }
    }

    @NonNull
    @Override
    public MountListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(m_context).inflate(R.layout.item_mount, parent, false);
        return new MountListViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull MountListViewHolder holder, int position) {
        final MountVO mountVO = m_mountItems.get(position);

        holder.iv_mountThumbnail.setImageDrawable(mountVO.getThumbnail());
        holder.tv_mountName.setText(mountVO.getName());
        holder.tv_mountHeight.setText(Integer.toString(mountVO.getHeight()));
        holder.tv_mountDistance.setText(Float.toString(mountVO.getDistance()));
        holder.tv_mountGrade.setText(Float.toString(mountVO.getGrade()));
        holder.m_bClimb = true;

        holder.layout_mountPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(m_context,mountVO.getName(),Toast.LENGTH_SHORT).show();
                //m_onItemClickListener.onItemClick(view, mountVO);
            }
        });
    }

    @Override
    public int getItemCount() {
        return m_mountItems.size();
    }

    /*
    @Override
   public View getView(int position, View convertView, ViewGroup parent) {

        Context context = parent.getContext();

        // listview_custom Layout을 inflate하여 conertView 참조 획득
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_mount, parent, false);
        }

        // listview_custom에 정의된 위젯에 대한 참조 획득
        ImageView iv_mountThumnail = (ImageView) convertView.findViewById(R.id.img_mountThumbnail);
        TextView tv_mountName = (TextView) convertView.findViewById(R.id.tv_mountName);
        TextView tv_mountHeight = (TextView) convertView.findViewById(R.id.tv_mountHeight);
        TextView tv_mountDistance = (TextView) convertView.findViewById(R.id.tv_mountDistance);
        TextView tv_mountGrade = (TextView) convertView.findViewById(R.id.tv_mountGrade);

        // 각 리스트에 뿌려줄 아이템을 받아오는데 mMyItem 재활용
        MountListItem myItem = getItem(position);

        // 각 위젯에 세팅된 아이템을 뿌려준다.
        iv_mountThumnail.setImageDrawable(myItem.getThumbnail());
        tv_mountName.setText(myItem.getName());
        tv_mountHeight.setText(String.valueOf(myItem.getHeight()));
        tv_mountDistance.setText(String.valueOf(myItem.getDistance())); // 임시 맞는지 모름
        tv_mountGrade.setText(String.valueOf(myItem.getGrade()));

        // 위젯에 대한 이벤트 리스너는 여기에 작성

        return convertView;
    }
    */

    public void addItem(Drawable thumbnail, String name, int height, float distance, float grade, Boolean bClimb) {
        MountVO newItem = new MountVO();

        newItem.setThumbnail(thumbnail);
        newItem.setName(name);
        newItem.setHeight(height);
        newItem.setDistance(distance);
        newItem.setGrade(grade);
        newItem.setClimb(bClimb);

        m_mountItems.add(newItem);
    }


}
