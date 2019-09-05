package com.example.mountup.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mountup.Adapter.MountListRecyclerViewAdapter;
import com.example.mountup.Helper.MountListRecyclerViewDecoration;
import com.example.mountup.R;

public class MountListFragment extends Fragment {

    private RecyclerView m_mountRecycleView;
    private RecyclerView.LayoutManager m_layoutManager;
    private MountListRecyclerViewAdapter m_adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mount_list, container, false);

        // RecycleView 생성
        m_mountRecycleView = (RecyclerView) view.findViewById(R.id.rv_mountList);
        // 사이즈 고정
        //m_mountRecycleView.setHasFixedSize(true);

        // Grid 레이아웃 적용
        m_layoutManager = new GridLayoutManager(getContext(), 2);
        m_mountRecycleView.setLayoutManager(m_layoutManager);
        m_mountRecycleView.addItemDecoration(new MountListRecyclerViewDecoration(getActivity()));

        // 어뎁터 연결
        m_adapter = new MountListRecyclerViewAdapter(getContext());
        dataSetting();
        m_mountRecycleView.setAdapter(m_adapter);

        // 정렬 스피너
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner_mountSort);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String str = adapterView.getItemAtPosition(i).toString();
                if ( str != "") {
                    // event 처리(정렬)
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return view;
    }

    private void dataSetting() {
        for (int i = 0; i < 10; i++) {
            m_adapter.addItem(ContextCompat.getDrawable(getContext(), R.drawable.ic_user_main), "name_" + i, 100, (float) 100.0, (float) 4.5, false);
        }
    }
}
