package com.example.navigation.model;

import java.util.List;

/**
 * ================================================
 * 类名：com.example.navigation.model
 * 时间：2021/8/31 17:33
 * 描述：
 * 修改人：
 * 修改时间：
 * 修改备注：
 * ================================================
 *
 * @author Admin
 */
public class BottomBar {

    public int selectTab;
    public List<Tab> tabs;

    public static class Tab {

        public int size;
        public boolean enable;

        public int index;
        public String pageUrl;
        public String title;
    }
}

