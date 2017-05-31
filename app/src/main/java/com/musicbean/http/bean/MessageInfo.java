package com.musicbean.http.bean;

/**
 * Created by boyo on 16/7/2.
 */
public class MessageInfo {
    public String id;
    public String image;

    public String name;
    public String msg;
    public String level;

    public GiftBean gift;
    public int giftHit;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MessageInfo)) {
            return false;
        }

        if (this == o) {
            return true;
        }

        MessageInfo target = (MessageInfo) o;
        if (this.id.equals(target.id) && this.gift.id == target.gift.id) {
            return true;
        }

        return false;
    }
}
