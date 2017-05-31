package com.musicbean.ui.live;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.Toast;

import com.musicbean.R;
import com.musicbean.http.HostManager;
import com.musicbean.util.ScreenUtils;
import com.qihoo.share.framework.BaseShareAPI;
import com.qihoo.share.framework.ShareCallBackListener;
import com.qihoo.share.framework.ShareParam;
import com.qihoo.share.framework.ShareResult;
import com.qihoo.share.framework.ShareSdk;
import com.qihoo.share.util.ShareUtil;

/**
 * Created by boyo on 16/7/3.
 */
public class ShareWindow extends BottomPopupWindow implements View.OnClickListener, ShareCallBackListener {

    public ShareWindow(Activity context) {
        super(context);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.share_layout;
    }

    @Override
    protected void setSize() {
        setWidth(ScreenUtils.getScreenWidth(mActivityRef.get()));
        setHeight(ScreenUtils.dp2px(mActivityRef.get(), 180));
    }

    @Override
    protected void initViews() {
        View v = getContentView();
        v.findViewById(R.id.share_wechat).setOnClickListener(this);
        v.findViewById(R.id.share_friends).setOnClickListener(this);
        v.findViewById(R.id.share_qq).setOnClickListener(this);
        v.findViewById(R.id.share_weibo).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        ShareSdk.API_NAME name = ShareSdk.API_NAME.QQ;
        String nameStr = "";
        switch (v.getId()) {
            case R.id.share_friends:
                name = ShareSdk.API_NAME.WXTimeLine;
                nameStr = "微信";
                break;
            case R.id.share_wechat:
                name = ShareSdk.API_NAME.WXSession;
                nameStr = "微信";
                break;
            case R.id.share_qq:
                name = ShareSdk.API_NAME.QQ;
                nameStr = "QQ";
                break;
            case R.id.share_weibo:
                name = ShareSdk.API_NAME.Weibo;
                nameStr = "微博";
                break;
        }

        BaseShareAPI api = ShareSdk.getShareAPI(name, v.getContext());
        if (api.isSurpport()) {
            doShare(api);
        } else {
            Toast.makeText(v.getContext(), "您的设备没有安装" + nameStr + "或者版本过低，不支持分享", Toast.LENGTH_SHORT).show();
        }

        dismiss();
    }

    private ShareParam mParam;

    public void setParam(String name, String uid, String type, String vid) {
        mParam = buildParam(mActivityRef.get(), name, uid, type, vid);
    }

    public void setParam(ShareParam sp) {
        mParam = sp;
    }

    public static ShareParam buildParam(Context context, String url, String title, String desc) {
        ShareParam param = new ShareParam();
        param.setMessageType(ShareParam.MSG_TYPE_WEBPAGE);
        param.setTitle(title);
        param.setText(param.getTitle());
        param.setWebUrl(url);
        param.setDescription(desc);
        param.setImageUrl("http://www.musicbean.cn/images/logo.jpg");
        Bitmap b = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        param.setThumbData(ShareUtil.bmpToByteArray(b, true, false));
        param.setImageData(param.getThumbData());
        return param;
    }

    public static ShareParam buildParam(Context context, String name, String uid, String type, String vid) {
        ShareParam param = new ShareParam();
        param.setMessageType(ShareParam.MSG_TYPE_WEBPAGE);
        param.setText(param.getTitle());
        param.setWebUrl(getShareUrl(uid, type, vid));
//        if (HotItemInfo.live.equals(type)) {
//            param.setTitle("听说唱歌好的人都来音悦豆直播了，你还不来？");
//            param.setDescription("快来看啊，" + name + "正在直播，走过路过千万不要错过～");
//        } else {
        param.setTitle("给你好听，让你好看!");
        param.setDescription("word天，" + name + "在音悦豆直播，快来围观呀!");
        //}
        param.setImageUrl("http://p15.qhimg.com/t01fcaa7ae305e1a7cd.png");
        Bitmap b = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        param.setThumbData(ShareUtil.bmpToByteArray(b, true, false));
        param.setImageData(param.getThumbData());
        return param;
    }

    private void doShare(BaseShareAPI api) {

        if (mParam == null) {
            mParam = new ShareParam();
            mParam.setMessageType(ShareParam.MSG_TYPE_WEBPAGE);
            mParam.setTitle("给你好听，让你好看!");
            mParam.setWebUrl("http://musicbean-app-server-dev.obaymax.com/download/app-release.apk");
            mParam.setDescription("word天，" + "我在音悦豆直播，快来围观呀!");
            mParam.setImageUrl("http://p15.qhimg.com/t01fcaa7ae305e1a7cd.png");
            Bitmap b = BitmapFactory.decodeResource(mActivityRef.get().getResources(), R.mipmap.ic_launcher);
            mParam.setThumbData(ShareUtil.bmpToByteArray(b, true, false));
        }

        api.setCallBackListener(this);
        api.share(mParam, mActivityRef.get());
    }

    @Override
    public void callback(ShareResult shareResult) {

    }

    public static String getShareUrl(String uid, String type, String vid) {
        return HostManager.getShareHost() + "?uid=" + uid + "&type=" + type + "&record_id=" + vid + "&video_id=" + vid;
//        return "http://musicbean-app-server-dev.obaymax.com/share.html?"
//                + "bigImgUrl=" + cover
//                + "&smallImgUrl=" + headimg
//                + "&downloadUrl=http://musicbean-app-server-dev.obaymax.com/download/app-release.apk"
//                + "&numPeople=" + onlinenum
//                + "&studioName=" + studio
//                + "&nickName=" + name;
    }
}
