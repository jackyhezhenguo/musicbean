package com.musicbean.http.bean;

/**
 * Created by boyo on 16/7/3.
 */
public class GiftBean {
//    "id": "4",
//            "name": "测试4",
//            "image": "http://p1.qhimg.com/t016b4288d0c7653e17.png",
//            "diamond": "10",
//            "experence": "100",
//            "cartoon_type": "1",
//            "sort": "0",
//            "is_doublehit": "1",
//            "is_del": "0",
//            "createline": "1467031822",
//            "updateline": "1467031822"

    public int id;
    public String name;
    public String image;
    public int diamond;
    public int experence;
    public int cartoon_type;
    public String sort;
    public int is_doublehit;

    public static final int TYPE_GIF = 2;
    public static final int TYPE_NORMAL = 1;

    public transient boolean isSelected = false;

}
