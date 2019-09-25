package com.example.mountup.Singleton;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.example.mountup.VO.MountVO;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class MountManager {

    private int loadPercent;

    private MountManager() {
        m_items = new ArrayList();
    }

    private static class MountManagerHolder {
        public static final MountManager instance = new MountManager();
    }

    public static MountManager getInstance() {
        return MountManagerHolder.instance;
    }

    private ArrayList<MountVO> m_items;

    public ArrayList<MountVO> getItems() { return m_items; }

    public Bitmap getMountBitmapFromURL(String url, String srcName) {
        InputStream is;
        Drawable mount_drawable = null;
        Bitmap mount_bitmap = null;

        try {
            is = (InputStream) new URL(url).getContent();
            mount_drawable = Drawable.createFromStream(is, srcName);
            mount_bitmap = ((BitmapDrawable)mount_drawable).getBitmap();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mount_bitmap;
    }

    public void setLoadPercent(int percent) {
        loadPercent = percent;
    }

    public int getLoadPercent() {
        return loadPercent;
    }
}