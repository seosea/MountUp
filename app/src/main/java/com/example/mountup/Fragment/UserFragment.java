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


import com.example.mountup.Helper.Constant;
import com.example.mountup.Listener.AsyncCallback;
import com.example.mountup.R;
import com.example.mountup.Adapter.MountClimbedListRecyclerViewAdapter;

import com.example.mountup.Helper.MountListRecyclerViewDecoration;

import com.example.mountup.ServerConnect.MountImageTask;
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
    private int nTotalHeight=0;

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

        loadAll();

        // 어뎁터 연결
        m_adapter = new MountClimbedListRecyclerViewAdapter(getContext(), this);
        m_mountRecycleView.setAdapter(m_adapter);

        txtCount1 = view.findViewById(R.id.txt_count_1_user);
        txtCount2 = view.findViewById(R.id.txt_count_2_user);
        txtTotalHeight = view.findViewById(R.id.txt_height_user);

        return view;
    }

    private void loadAll() {
        MountImageTask mountImageTask = new MountImageTask(Constant.CLIMBED, new AsyncCallback() {
            @Override
            public void onSuccess(Object object) {
                ArrayList < MountVO > mountList = MountManager.getInstance().getItems();
                m_bufferItems.clear();
                for (int i = 0; i < mountList.size(); i++) {
                    if(mountList.get(i).isClimbed()) {
                        m_bufferItems.add(MountManager.getInstance().getItems().get(i));
                        nCount++;
                        nTotalHeight+=MountManager.getInstance().getItems().get(i).getHeight();
                    }
                }
                m_adapter.addAll(m_bufferItems);

                txtCount1.setText(String.valueOf(nCount)+"회");
                txtCount2.setText(String.valueOf(nCount));
                txtTotalHeight.setText(String.valueOf(nTotalHeight)+"m");
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
        mountImageTask.execute();
    }


    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {

    }
}
