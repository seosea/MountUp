package com.example.mountup.Singleton;
import com.example.mountup.VO.ReviewVO;
import java.util.ArrayList;

public class ReviewManager {

    private ArrayList<ReviewVO> m_items;

    private static class ReviewManagerHolder {
        public static final ReviewManager instance = new ReviewManager();
    }

    private ReviewManager() {
        m_items = new ArrayList();
    }

    public static ReviewManager getInstance() {
        return ReviewManagerHolder.instance;
    }

    public ArrayList<ReviewVO> getItems() { return m_items; }
}
