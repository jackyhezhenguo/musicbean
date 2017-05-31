package com.musicbean.ui.pay;

/**
 * Created by boyo on 16/7/26.
 */
public class BuyInfo {
    public int diamond;
    public int money;
    public int free;

    public boolean isSelected = false;

    public BuyInfo(int d, int m, int f) {
        diamond = d;
        money = m;
        free = f;
    }
}
