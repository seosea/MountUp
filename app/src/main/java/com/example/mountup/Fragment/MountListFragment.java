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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mountup.Adapter.MountListRecyclerViewAdapter;
import com.example.mountup.Helper.Calculator;
import com.example.mountup.Helper.Constant;
import com.example.mountup.Helper.MountListRecyclerViewDecoration;
import com.example.mountup.Listener.AsyncCallback;
import com.example.mountup.R;
import com.example.mountup.ServerConnect.MountImageTask;
import com.example.mountup.ServerConnect.UserClimbedListTask;
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
                if (isEditTextEmpty && dy > 0 &&
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

        // User 등반 리스트 갱신
        String url_userClimbedList = Constant.URL + "/api/mntuplist";
        UserClimbedListTask userClimbedListTask = new UserClimbedListTask(url_userClimbedList, new AsyncCallback() {
            @Override
            public void onSuccess(Object object) {
                // 정렬 스피너
                m_sortSpinner = (Spinner) MountListFragment.super.getView().findViewById(R.id.spinner_mountSort);

                String[] spinnerArray = getResources().getStringArray(R.array.mount_sort);
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                        MountListFragment.super.getContext(), R.layout.spinner_item, spinnerArray);
                spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                m_sortSpinner.setAdapter(spinnerArrayAdapter);

                m_sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        // 왜 자꾸 자동으로 실행되는지
                        Log.d("mmee:MountListFragment", "MountList 정렬");
                        sortMountList(adapterView.getItemAtPosition(i).toString());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                //sortMountList(m_sortSpinner.getSelectedItem().toString());
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
        userClimbedListTask.execute();

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

                //loadFirstData();

                m_et_mountSearch.setText("");
                sortMountList(m_sortSpinner.getSelectedItem().toString());
            }
        }, 1000);
    }

    @Override
    public void onLoadMore() {
        Log.d("mmee:MountListFragment", "onLoadMore");
        new AsyncTask<Void, Void, ArrayList<MountVO>>() {
            @Override
            protected ArrayList<MountVO> doInBackground(Void... voids) {
                ArrayList<MountVO> mountList = MountManager.getInstance().getItems();

                // 목록 10개 추가
                int start = m_adapter.getItemCount() - 1;
                int end = start + 10;
                if (end > mountList.size() - 1) {
                    end = mountList.size() - 1;
                }

                m_bufferItems.clear();
                for (int i = start; i < end; i++) {
                    if (mountList.get(i).getThumbnail() == null) {
                        int id = mountList.get(i).getID();
                        String url_img = Constant.URL + "/basicImages/" + id + ".jpg";
                        mountList.get(i).setThumbnail(MountManager.getInstance().getMountBitmapFromURL(url_img, "mount" + id));
                        Log.d("mmee:loadMore", "get mount resource " + id);
                    }
                    m_bufferItems.add(mountList.get(i));
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
        Log.d("mmee:MountListFragment", "LoadFirstData");
        MountImageTask mountImageTask = new MountImageTask(Constant.FIRST_TEN, new AsyncCallback() {
            @Override
            public void onSuccess(Object object) {

                ArrayList<MountVO> mountList = MountManager.getInstance().getItems();

                // 이미지 10개 view 출력
                m_bufferItems.clear();
                for (int i = 0; i < 10; i++) {
                    if (Constant.X != 0.0) {
                        mountList.get(i).setDistance(
                                Calculator.calculateDistance(
                                        mountList.get(i).getLocX(),
                                        mountList.get(i).getLocY()
                                )
                        );
                    } else {
                        mountList.get(i).setDistance(0);
                        Toast.makeText(getContext(), "위치를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                    m_bufferItems.add(MountManager.getInstance().getItems().get(i));
                }
                m_adapter.addAll(m_bufferItems);
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
        mountImageTask.execute();
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

        loadFirstData();
        m_mountRecycleView.smoothScrollToPosition(0);
        m_adapter.notifyDataSetChanged();
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
