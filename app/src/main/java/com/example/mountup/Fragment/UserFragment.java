package com.example.mountup.Fragment;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mountup.Adapter.ReviewAdapter;
import com.example.mountup.Model.Review;
import com.example.mountup.R;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;

public class UserFragment extends Fragment{

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        ReviewAdapter adapter = new ReviewAdapter();

        RecyclerView recyclerView = view.findViewById(R.id.rc_review_recyclerview);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);

       

        adapter.addItem(new Review("shin","이 산 너무 좋아요 다음에 또 오고싶어요. 이 산 너무 좋아요 다음에 또 오고싶어요. 이 산 너무 좋아요 다음에 또 오고싶어요. ",
                BitmapFactory.decodeResource(getResources(),R.drawable.mt2),1004,(float)4.75,true));
        adapter.addItem(new Review("shin","good",BitmapFactory.decodeResource(getResources(),R.drawable.mt),1004,4.35,true));
        adapter.addItem(new Review("shin","good",BitmapFactory.decodeResource(getResources(),R.drawable.image),1004,3.1,false));
        adapter.addItem(new Review("shin","good",BitmapFactory.decodeResource(getResources(),R.drawable.image),1004,1.0,false));
        adapter.addItem(new Review("shin","good",BitmapFactory.decodeResource(getResources(),R.drawable.image),1004,0.1,false));
        adapter.addItem(new Review("shin","good",BitmapFactory.decodeResource(getResources(),R.drawable.image),1004,0.0,false));
        adapter.addItem(new Review("shin","good",BitmapFactory.decodeResource(getResources(),R.drawable.image),1004,2.0,false));

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        return view;
    }



}
