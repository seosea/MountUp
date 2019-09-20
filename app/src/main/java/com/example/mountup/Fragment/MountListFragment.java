package com.example.mountup.Fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mountup.Adapter.MountListRecyclerViewAdapter;
import com.example.mountup.Helper.Constant;
import com.example.mountup.Helper.MountListRecyclerViewDecoration;
import com.example.mountup.R;
import com.example.mountup.Singleton.MountManager;
import com.example.mountup.VO.MountVO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MountListFragment extends Fragment implements MountListRecyclerViewAdapter.OnLoadMoreListener,
SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView m_mountRecycleView;
    private RecyclerView.LayoutManager m_layoutManager;
    private MountListRecyclerViewAdapter m_adapter;
    private SwipeRefreshLayout m_swipeRefresh;
    private Spinner m_sortSpinner;
    private EditText m_et_mountSearch;

    private ArrayList<MountVO> m_bufferItems; // 버퍼로 사용할 리스트
    private TextView txtCurrentAddress;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mount_list, container, false);

        txtCurrentAddress = view.findViewById(R.id.tv_myAddress);
        txtCurrentAddress.setText(Constant.CURRENT_ADDRESS);

        m_bufferItems = new ArrayList();

        // RecycleView 생성 및 사이즈 고정
        m_mountRecycleView = (RecyclerView) view.findViewById(R.id.rv_mountList);
        m_mountRecycleView.setHasFixedSize(true);

        // Grid 레이아웃 적용
        m_layoutManager = new GridLayoutManager(getContext(), 2);
        m_mountRecycleView.setLayoutManager(m_layoutManager);
        m_mountRecycleView.addItemDecoration(new MountListRecyclerViewDecoration(getActivity()));

        // 어뎁터 연결
        m_adapter = new MountListRecyclerViewAdapter(getContext(), this);
        m_mountRecycleView.setAdapter(m_adapter);

        // 새로고침
        m_swipeRefresh = view.findViewById(R.id.swipeRefresh_mountList);
        m_swipeRefresh.setOnRefreshListener(this);

        // 화면 끝까지 스크롤 했을 때 추가 로딩 리스너
        m_mountRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // 마지막 체크, 왜 -2 인지?
                GridLayoutManager gridLayoutManager = (GridLayoutManager) m_mountRecycleView.getLayoutManager();
                boolean isEditTextEmpty = m_et_mountSearch.getText().toString().equals("");
                if(isEditTextEmpty && dy > 0 &&
                        gridLayoutManager.findLastCompletelyVisibleItemPosition() > (m_adapter.getItemCount() - 2)) {
                    m_adapter.showLoading();
                }
            }
        });

        // EditText 필터
        m_et_mountSearch = (EditText) view.findViewById(R.id.et_mountSearch);
        m_et_mountSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mountFilter(editable.toString());
            }
        });

        // 정렬 스피너
        m_sortSpinner = (Spinner) view.findViewById(R.id.spinner_mountSort);
        m_sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("mmee:MountListFrament","Mount 정렬");
                sortMountList(adapterView.getItemAtPosition(i).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //sortMountList(m_sortSpinner.getSelectedItem().toString());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("mmee:MountListFragment", "onStart");
    }

    @Override
    public void onRefresh() {
        Log.d("mmee:MountListFragment", "onRefresh");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                m_swipeRefresh.setRefreshing(false);
                //m_mountItems.clear();
                loadFirstData();

                m_et_mountSearch.setText("");
                sortMountList(m_sortSpinner.getSelectedItem().toString());
            }
        }, 2000);
    }

    private static float calculateDistance(double lat2, double lon2){

        double theta = Constant.Y - lon2;
        double dist = Math.sin(deg2rad(Constant.X)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(Constant.X)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        dist = dist * 1.609344;

        Log.v("Distance", String.valueOf(dist));
        Log.v("x", String.valueOf(lat2));
        Log.v("y", String.valueOf(lon2));
        return (float)(dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }


    @Override
    public void onLoadMore() {
        Log.d("mmee:MountListFragment", "onLoadMore");
        new AsyncTask<Void, Void, ArrayList<MountVO>>() {
            @Override
            protected ArrayList<MountVO> doInBackground(Void... voids) {

                // 목록 10개 추가
                int start = m_adapter.getItemCount() - 1;
                int end = start + 10;
                if (end > MountManager.getInstance().getItems().size() - 1) {
                    end = MountManager.getInstance().getItems().size() - 1;
                }
                ArrayList<MountVO> items = new ArrayList<>();

                m_bufferItems.clear();
                for (int i = start; i < end; i++) {
                    m_bufferItems.add(MountManager.getInstance().getItems().get(i));
                    /*
                    MountVO newItem = new MountVO();
                    newItem.setMount(ContextCompat.getDrawable(getContext(), R.drawable.mountain_sample),
                            i + 1 + "번산", random.nextInt(900) + 100,
                            Math.round(random.nextFloat() * 1000) / (float)10.0,
                            Math.round(random.nextFloat() * 50) / (float)10.0,
                            random.nextInt(2) > 0 ? true : false);
                            */

                }

                // 1초 sleep

                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                return m_bufferItems;
            }

            @Override
            protected void onPostExecute(ArrayList<MountVO> items) {
                super.onPostExecute(items);

                m_adapter.dismissLoading();     // 하나 삭제해주는데 왜?
                m_adapter.addItemMore(items);   // 로딩될 아이템들 add
                m_adapter.setMore(true);
            }
        }.execute();
    }

    private void loadFirstData() {
        Log.d("mmee:MountListFragment", "loadData");
        m_bufferItems.clear();
        for (int i = 0; i < 10; i++) {
            MountManager.getInstance().getItems().get(i).setDistance(
                    calculateDistance(
                            MountManager.getInstance().getItems().get(i).getLocX(),
                            MountManager.getInstance().getItems().get(i).getLocY()
                    )
            );
            m_bufferItems.add(MountManager.getInstance().getItems().get(i));
            /*
            Random random = new Random();

            m_bufferItems.add(new MountVO(ContextCompat.getDrawable(getContext(), R.drawable.mountain_sample),
                    i + 1 + "번산", random.nextInt(900) + 100,
                    Math.round(random.nextFloat() * 1000) / (float)10.0,
                    Math.round(random.nextFloat() * 50) / (float)10.0,
                    random.nextInt(2) > 0 ? true : false));
                    */
        }
        m_adapter.addAll(m_bufferItems);
    }

    public void sortMountList(String str) {
        Log.d("mmee:MountListFragment", "spinner changed : " + str);

        if (str.equals("별점 순")) {
            Collections.sort(MountManager.getInstance().getItems(), new Comparator<MountVO>() {
                @Override
                public int compare(MountVO o1, MountVO o2) {
                    if (o1.getGrade() < o2.getGrade()) {
                        return 1;
                    } else if (o1.getGrade() > o2.getGrade()) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            });
        } else if (str.equals("가까운 순")) {
            Collections.sort(MountManager.getInstance().getItems(), new Comparator<MountVO>() {
                @Override
                public int compare(MountVO o1, MountVO o2) {
                    if (o1.getDistance() > o2.getDistance()) {
                        return 1;
                    } else if (o1.getDistance() < o2.getDistance()) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            });
        } else if (str.equals("높은 순")) {
            Collections.sort(MountManager.getInstance().getItems(), new Comparator<MountVO>() {
                @Override
                public int compare(MountVO o1, MountVO o2) {
                    if (o1.getHeight() < o2.getHeight()) {
                        return 1;
                    } else if (o1.getHeight() > o2.getHeight()) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            });
        } else if (str.equals("낮은 순")) {
            Collections.sort(MountManager.getInstance().getItems(), new Comparator<MountVO>() {
                @Override
                public int compare(MountVO o1, MountVO o2) {
                    if (o1.getHeight() > o2.getHeight()) {
                        return 1;
                    } else if (o1.getHeight() < o2.getHeight()) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            });
        }

        if (!MountManager.getInstance().getItems().isEmpty()) {
            loadFirstData();
            m_mountRecycleView.smoothScrollToPosition(0);
        }

        //m_adapter.notifyDataSetChanged();
    }

    private void mountFilter(String text) {
        ArrayList<MountVO> filterItems = new ArrayList();

        for (MountVO item : MountManager.getInstance().getItems()) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filterItems.add(item);
            }
        }
        m_adapter.filterList(filterItems);
    }
}
