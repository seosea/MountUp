package com.example.mountup.Fragment;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mountup.R;

public class MyReviewFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_review, container, false);
        ReviewAdapter adapter = new ReviewAdapter();

        RecyclerView recyclerView = view.findViewById(R.id.rc_review_recyclerview);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);

        adapter.addItem(new Review("shin","good",BitmapFactory.decodeResource(getResources(),R.drawable.image)));
        adapter.addItem(new Review("shin","good",BitmapFactory.decodeResource(getResources(),R.drawable.image)));
        adapter.addItem(new Review("shin","good",BitmapFactory.decodeResource(getResources(),R.drawable.image)));
        adapter.addItem(new Review("shin","good",BitmapFactory.decodeResource(getResources(),R.drawable.image)));
        adapter.addItem(new Review("shin","good",BitmapFactory.decodeResource(getResources(),R.drawable.image)));
        adapter.addItem(new Review("shin","good",BitmapFactory.decodeResource(getResources(),R.drawable.image)));
        adapter.addItem(new Review("shin","good",BitmapFactory.decodeResource(getResources(),R.drawable.image)));

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        return view;
    }

}
