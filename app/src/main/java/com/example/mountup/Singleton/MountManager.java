package com.example.mountup.Singleton;

import com.example.mountup.VO.MountVO;

import java.util.ArrayList;

public class MountManager {

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
}