package com.example.mountup.Singleton;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.example.mountup.VO.MountVO;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MountManager {

    private int loadPercent;
    private String currentSort;

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

    public void setCurrentSort(String sort) {
        currentSort = sort;
    }

    public String getCurrentSort() {
        return currentSort;
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
        } else if (str.equals("산 높이 ↑")) {
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
        } else if (str.equals("산 높이 ↓")) {
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
        } else if (str.equals("가나다 순")) {
            Collections.sort(MountManager.getInstance().getItems(), new Comparator<MountVO>() {
                @Override
                public int compare(MountVO o1, MountVO o2) {
                    if (o1.getName().toString().
                            compareTo(o2.getName().toString()) > 0) {
                        return 1;
                    } else if (o1.getName().toString().
                            compareTo(o2.getName().toString()) < 0) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            });
        }
    }
}