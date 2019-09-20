package com.example.mountup.Fragment;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.example.mountup.R;
import com.example.mountup.Adapter.MountClimbedListRecyclerViewAdapter;

import com.example.mountup.Helper.MountListRecyclerViewDecoration;

import com.example.mountup.Singleton.MountManager;
import com.example.mountup.VO.MountVO;


import java.util.ArrayList;

public class UserFragment extends Fragment implements MountClimbedListRecyclerViewAdapter.OnLoadMoreListener,
        SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView m_mountRecycleView;
    private RecyclerView.LayoutManager m_layoutManager;
    private MountClimbedListRecyclerViewAdapter m_adapter;
    private ArrayList<MountVO> m_bufferItems; // 버퍼로 사용할 리스트

    private TextView txtCount1, txtCount2, txtTotalHeight;
    private int nCount=0;
    private int nTotalHeigth=0;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        m_bufferItems = new ArrayList();

        // RecycleView 생성 및 사이즈 고정
        m_mountRecycleView = (RecyclerView) view.findViewById(R.id.rv_mountList);
        m_mountRecycleView.setHasFixedSize(true);

        // Grid 레이아웃 적용
        m_layoutManager= new LinearLayoutManager(getContext());
        ((LinearLayoutManager) m_layoutManager).setOrientation(LinearLayoutManager.HORIZONTAL);

        m_mountRecycleView.setLayoutManager(m_layoutManager);
        m_mountRecycleView.addItemDecoration(new MountListRecyclerViewDecoration(getActivity()));

        // 어뎁터 연결
        m_adapter = new MountClimbedListRecyclerViewAdapter(getContext(), this);
        m_mountRecycleView.setAdapter(m_adapter);

        loadAll();

        txtCount1 = view.findViewById(R.id.txt_count_1_user);
        txtCount2 = view.findViewById(R.id.txt_count_2_user);
        txtTotalHeight = view.findViewById(R.id.txt_height_user);

        txtCount1.setText(String.valueOf(nCount)+"회");
        txtCount2.setText(String.valueOf(nCount));
        txtTotalHeight.setText(String.valueOf(nTotalHeigth)+"m");

        return view;
    }

    private void loadAll() {
        m_bufferItems.clear();
        for (int i = 0; i < 10; i++) {
            if(MountManager.getInstance().getItems().get(i).isClimbed()) {
                m_bufferItems.add(MountManager.getInstance().getItems().get(i));
                nCount++;
                nTotalHeigth+=MountManager.getInstance().getItems().get(i).getHeight();
            }
        }
        m_adapter.addAll(m_bufferItems);
    }


    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {

    }
}
