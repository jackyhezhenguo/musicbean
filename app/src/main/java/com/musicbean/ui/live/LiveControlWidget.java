package com.musicbean.ui.live;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.musicbean.R;
import com.musicbean.http.ResponseHandler;
import com.musicbean.http.api.ChatApi;
import com.musicbean.http.api.LiveApi;
import com.musicbean.http.api.UserApi;
import com.musicbean.http.bean.BaseResponse;
import com.musicbean.http.bean.DiamondLeftInfo;
import com.musicbean.http.bean.HotItemInfo;
import com.musicbean.http.bean.ListData;
import com.musicbean.http.bean.LiveFinishBean;
import com.musicbean.http.bean.MessageInfo;
import com.musicbean.http.bean.UserInfoBean;
import com.musicbean.im.CustomUserInfo;
import com.musicbean.im.DanmuMessage;
import com.musicbean.im.GagMessage;
import com.musicbean.im.GiftMessage;
import com.musicbean.im.LiveEndMessage;
import com.musicbean.im.LiveStatusContent;
import com.musicbean.im.LiveStatusMessage;
import com.musicbean.im.ManagerMessage;
import com.musicbean.im.MyTxtMessage;
import com.musicbean.im.TextContent;
import com.musicbean.manager.GiftManager;
import com.musicbean.manager.IMManager;
import com.musicbean.manager.LoginManager;
import com.musicbean.ui.live.danmu.DanmuView;
import com.musicbean.ui.user.FansRankActivity;
import com.musicbean.util.DateUtils;
import com.musicbean.util.LogUtils;
import com.musicbean.util.ScreenUtils;
import com.musicbean.widget.NotifyDialog;
import com.musicbean.widget.animup.PeriscopeLayout;
import com.musicbean.widget.gift.BigGiftWidget;
import com.musicbean.widget.gift.SmallGiftGroup;
import com.qihoo.share.framework.ShareParam;

import net.tsz.afinal.FinalBitmap;

import java.lang.reflect.Type;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;

/**
 * Created by boyo on 16/7/2.
 */
public class LiveControlWidget extends RelativeLayout implements View.OnClickListener, IMManager.IMessageListener,
        GiftWindow.OnGiftSendListener, UserPageWindow.OnUserPageListener {

    private ImageView mPreparingView;
    private ImageView mAnchorImg;
    private TextView mAnchorName;
    protected TextView mOnlineNum;
    private TextView mBeansNum;
    private RecyclerView mUserImgList;
    private TextView mTxtAnchorInfo;

    protected ListView mMsgList;
    private LiveMsgAdapter mMsgAdapter;

    private PeriscopeLayout mFavorsLayout;
    private LiveUsersAdapter mUserImgAdapter;

    protected String mRoomId;
    protected UserInfoBean mAnchorData;

    private View mInputLayout;
    private EditText mEdtInput;
    private CheckBox mOpenDanmu;
    private TextView mBtnSend;

    private DanmuView mDanmuView;

    private View mLoadingView;

    private long mValidTime = 10 * 1000;

    private int mStart = 0;
    private int COUNT = 20;

    private SmallGiftGroup mSmallGiftGroup;
    private BigGiftWidget mBigGiftWidget;

    private NotifyDialog mNotifyDialog;

    private View mTopLayout;

    private int mSendFavorCount = 0;

    private boolean needToFinishPage = false;

    private TextView mRelationTxt;
    private View mRelationLine;

    private View mPauseTipView;

    public LiveControlWidget(Context context) {
        this(context, null);
    }

    public LiveControlWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();

        GiftManager.getInstance().refreshGift();
    }

    public void initChat(String roomid) {
        mRoomId = roomid;
        joinRoom();

        post(mRefreshRunnable);
    }

    private Runnable mRefreshRunnable = new Runnable() {
        @Override
        public void run() {
            mStart = 0;
            needLoadMore = true;
            getOnlineUsers();

            getAnchorBeans();

            postDelayed(this, mValidTime);
        }
    };

    private boolean needLoadMore = true;

    private void getOnlineUsers() {
        if (!needLoadMore) {
            return;
        }

        LiveApi.getOnlineList(getContext(), mRoomId, mStart, COUNT, new ResponseHandler() {
            @Override
            public void onSuccess(String data) {
                Type objectType = new TypeToken<BaseResponse<ListData<UserInfoBean>>>() {
                }.getType();
                BaseResponse<ListData<UserInfoBean>> res = new Gson().fromJson(data, objectType);

                if (mStart == 0) {
                    mUserImgAdapter.setDatas(res.data.list);
                } else {
                    mUserImgAdapter.addDatas(res.data.list);
                }
                mStart++;

                if (mUserImgAdapter.getItemCount() >= res.data.total) {
                    needLoadMore = false;
                }

                mOnlineNum.setText(res.data.total + "");
            }

            @Override
            public void onFailure(int responseCode, String errorMsg) {

            }
        });
    }

    private void getAnchorBeans() {
        UserApi.getUserInfo(getContext(), mAnchorData.id, new ResponseHandler() {
            @Override
            public void onSuccess(String data) {
                Type objectType = new TypeToken<BaseResponse<UserInfoBean>>() {
                }.getType();
                BaseResponse<UserInfoBean> res = new Gson().fromJson(data, objectType);

                mAnchorData = res.data;

                mBeansNum.setText(mAnchorData.in_bean + "");
            }

            @Override
            public void onFailure(int responseCode, String errorMsg) {

            }
        });
    }

    private void joinRoom() {
        IMManager.getInstance().addMessageListener(this);
        IMManager.getInstance().joinChatRoom(mRoomId);

        LiveApi.joinRoom(getContext(), mRoomId, new ResponseHandler() {
            @Override
            public void onSuccess(String data) {

            }

            @Override
            public void onFailure(int responseCode, String errorMsg) {
                if (responseCode == 3005) {
                    ((Activity) getContext()).finish();
                }
            }
        });
    }

    @Override
    public void onReceiveMessage(final Message msg) {
        LogUtils.e("LiveContolWidget", msg.getContent().toString());

        post(new Runnable() {
            @Override
            public void run() {
                try {
                    processMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onError(int type, RongIMClient.ErrorCode errorCode) {
        switch (type) {
            case IMManager.TYPE_JOIN_ROOM:
                Toast.makeText(getContext(), "加入聊天室失败，请退出重进!!!", Toast.LENGTH_SHORT).show();
                break;
            case IMManager.TYPE_QUIT_ROOM:
                break;
            case IMManager.TYPE_SEND_MSG:
                if (RongIMClient.ErrorCode.FORBIDDEN_IN_CHATROOM == errorCode) {
                    mNotifyDialog.show("对不起，您已被禁言");
                }

                break;
        }
    }

    @Override
    public void onSuccess(int type) {
        switch (type) {
            case IMManager.TYPE_JOIN_ROOM:
                sendNormalMsg("来了");
                break;
            case IMManager.TYPE_QUIT_ROOM:
                break;
            case IMManager.TYPE_SEND_MSG:
                break;
        }
    }

    private void processMessage(Message msg) {
        MessageContent mc = msg.getContent();

        MessageInfo mInfo = new MessageInfo();

        if (mc instanceof MyTxtMessage) {
            MyTxtMessage tm = (MyTxtMessage) mc;
            if (tm.getUserInfo() != null) {
                mInfo.name = tm.getUserInfo().getName();
                mInfo.level = tm.getUserInfo().user_level;
                mInfo.id = tm.getUserInfo().getUserId();
            } else {
                mInfo.name = "某人";
            }
            mInfo.msg = tm.content.text;

            if (tm.content.text_type == 1) {
                mFavorsLayout.addHeart();
            }
        } else if (mc instanceof GiftMessage) {
            GiftMessage gm = (GiftMessage) mc;

            if (gm.getUserInfo() != null) {
                mInfo.name = gm.getUserInfo().getName();
                mInfo.image = gm.getUserInfo().getPortraitUri().toString();
                mInfo.id = gm.getUserInfo().getUserId();
                mInfo.level = gm.getUserInfo().user_level;
            } else {
                mInfo.name = "某人";
            }

            mInfo.gift = GiftManager.getInstance().findGift(gm.content.giftid);
            if (mInfo.gift == null) {
                return;
            }
            mInfo.msg = "送了礼物" + mInfo.gift.name;
            mInfo.giftHit = gm.content.hit;
            sendGift(mInfo);

//            for (int ii = 1; ii <= gm.content.hit; ii++) {
//                MessageInfo tInfo = new MessageInfo();
//                tInfo.name = mInfo.name;
//                tInfo.image = mInfo.image;
//                tInfo.id = mInfo.id;
//                tInfo.level = mInfo.level;
//
//                tInfo.gift = mInfo.gift;
//                tInfo.giftHit = ii;
//                sendGift(tInfo);
//            }

            LogUtils.e("wcb", "礼物来啦" + gm.content.giftid + "x" + gm.content.hit);
        } else if (mc instanceof GagMessage) {
            GagMessage gmsg = (GagMessage) mc;
            mInfo.name = "系统消息";
            mInfo.msg = gmsg.content.gagnickname + "被禁言";

            if (gmsg.content.gagid.equals(LoginManager.getInstance().getUserInfo().id)) {
                mNotifyDialog.show("对不起，您已被禁言");
            }
        } else if (mc instanceof LiveEndMessage) {
            if (mAnchorData.id.equals(LoginManager.getInstance().getUserInfo().id)) {
                Toast.makeText(getContext(), "您已被下线", Toast.LENGTH_LONG).show();
            }

            // TODO 直播结束
            Activity act = (Activity) getContext();
            if (!act.isFinishing()) {
                act.finish();
                needToFinishPage = true;
            }
            return;
        } else if (mc instanceof ManagerMessage) {
            ManagerMessage mmsg = (ManagerMessage) mc;
            mInfo.name = "系统消息";
            mInfo.msg = mmsg.content.managernickname + "被设为管理员";

            if (mmsg.content.managerid.equals(LoginManager.getInstance().getUserInfo().id)) {
                mNotifyDialog.show("主播把你设为管理员");
            }
        } else if (mc instanceof DanmuMessage) {
            DanmuMessage dm = (DanmuMessage) mc;
            if (dm.getUserInfo() != null) {
                mInfo.name = dm.getUserInfo().getName();
                mInfo.image = dm.getUserInfo().getPortraitUri().toString();
                mInfo.id = dm.getUserInfo().getUserId();
                mInfo.level = dm.getUserInfo().user_level;
            } else {
                mInfo.name = "某人";
            }

            mInfo.msg = dm.content.content;

            mDanmuView.addDanmu(mInfo);
        } else if (mc instanceof LiveStatusMessage) {
            LiveStatusMessage lm = (LiveStatusMessage) mc;
            if (lm.content.status.equals(LiveStatusContent.ST_PAUSE)) {
                mPauseTipView.setVisibility(View.VISIBLE);
            } else if (lm.content.status.equals(LiveStatusContent.ST_RESUME)) {

                if (mPauseTipView.getVisibility() == View.GONE && getContext() instanceof LivePlayActivity) {
                    ((LivePlayActivity) getContext()).replay();
                }
                mPauseTipView.setVisibility(View.GONE);
            }

            return;
        }

        mMsgAdapter.addData(mInfo);
        mMsgList.setSelection(mMsgAdapter.getCount());
    }

    protected void initViews() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_live_control, this);

        mPreparingView = (ImageView) findViewById(R.id.prepareing_view);
        Glide.with(getContext()).load(R.drawable.preparing).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(mPreparingView);

        mFavorsLayout = (PeriscopeLayout) findViewById(R.id.zan_layout);
        mFavorsLayout.setOnClickListener(this);

        mAnchorImg = (ImageView) findViewById(R.id.anchor_img);
        mAnchorImg.setOnClickListener(this);
        mAnchorName = (TextView) findViewById(R.id.anchor_name);
        mOnlineNum = (TextView) findViewById(R.id.online_num);
        mBeansNum = (TextView) findViewById(R.id.anchor_beans);
        mTxtAnchorInfo = (TextView) findViewById(R.id.anchor_info);

        mMsgList = (ListView) findViewById(R.id.list_msg);
        mMsgAdapter = new LiveMsgAdapter();
        mMsgList.setAdapter(mMsgAdapter);
        mMsgList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MessageInfo mInfo = mMsgAdapter.getItem(position);
                if (mInfo != null && !TextUtils.isEmpty(mInfo.id)) {
                    UserInfoBean info = new UserInfoBean();
                    info.id = mInfo.id;
                    info.image = mInfo.image;
                    info.nickname = mInfo.name;
                    if (TextUtils.isEmpty(mInfo.level)) {
                        mInfo.level = "1";
                    }
                    info.user_level = Integer.parseInt(mInfo.level);

                    showUserWindow(info);
                }
            }
        });

        mUserImgList = (RecyclerView) findViewById(R.id.user_img_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mUserImgList.setLayoutManager(layoutManager);
        mUserImgList.addOnScrollListener(mUserListScrollListener);
        mUserImgAdapter = new LiveUsersAdapter();
        mUserImgList.setAdapter(mUserImgAdapter);
        mUserImgAdapter.setOnItemClickListener(new LiveUsersAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, UserInfoBean data) {
                showUserWindow(data);
            }
        });

        mInputLayout = findViewById(R.id.input_layout);
        mEdtInput = (EditText) findViewById(R.id.edt_input);
        mOpenDanmu = (CheckBox) findViewById(R.id.cbx_open_danmu);
        mOpenDanmu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mEdtInput.setHint("开启弹幕，1钻/条");
                } else {
                    mEdtInput.setHint("大家一起来互动吧!");
                }
            }
        });
        mBtnSend = (TextView) findViewById(R.id.btn_send_msg);
        mBtnSend.setOnClickListener(this);

//        findViewById(R.id.btn_chat).setOnClickListener(this);
//        findViewById(R.id.btn_gift).setOnClickListener(this);
//        findViewById(R.id.btn_share).setOnClickListener(this);
        findViewById(R.id.btn_close).setOnClickListener(this);
        findViewById(R.id.bean_layout).setOnClickListener(this);
        findViewById(R.id.loading_view).setOnClickListener(this);

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mFavorsLayout.addHeart();
                    // TODO
                    if (mSendFavorCount == 0) {
                        mSendFavorCount++;
                        sendFavorMsg();
                    }
                }
                return false;
            }
        });

        mDanmuView = (DanmuView) findViewById(R.id.danmuview);
        mDanmuView.setVisibility(View.GONE);
        mDanmuView.setZOrderOnTop(true);
        mDanmuView.setZOrderMediaOverlay(true);
        mDanmuView.getHolder().setFormat(PixelFormat.TRANSLUCENT);

        mBigGiftWidget = (BigGiftWidget) findViewById(R.id.big_gift_widget);
        mSmallGiftGroup = (SmallGiftGroup) findViewById(R.id.small_gift_group);

        mLoadingView = findViewById(R.id.loading_view);

        mNotifyDialog = new NotifyDialog(getContext());

        mTopLayout = findViewById(R.id.toplayout);

        mRelationTxt = (TextView) findViewById(R.id.btn_follow);
        mRelationLine = findViewById(R.id.line_follow);

        mPauseTipView = findViewById(R.id.pause_tips);
    }

    public void setData(UserInfoBean info) {
        mAnchorData = info;
        FinalBitmap.getInstance().display(mAnchorImg, info.image);
        mAnchorName.setText(info.nickname);
        mTxtAnchorInfo.setText(
                getResources().getString(R.string.live_anchor, info.studio, info.musicid,
                        DateUtils.timeMillisToDateString(System.currentTimeMillis())));
        mBeansNum.setText(info.in_bean + "");
    }

    private RecyclerView.OnScrollListener mUserListScrollListener = new RecyclerView.OnScrollListener() {
        boolean isSlidingToLast;

        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
            // 当不滚动时
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                //获取最后一个完全显示的ItemPosition
                int lastVisiblePos = manager.findLastVisibleItemPosition();
                int totalItemCount = manager.getItemCount();

                // 判断是否滚动到底部
                if (lastVisiblePos == (totalItemCount - 1) && isSlidingToLast) {
                    // TODO 加载更多功能的代码
                    getOnlineUsers();
                }
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            //dx用来判断横向滑动方向，dy用来判断纵向滑动方向
            if (dy > 0) {
                //大于0表示，正在向下滚动
                isSlidingToLast = true;
            } else {
                //小于等于0 表示停止或向上滚动
                isSlidingToLast = false;
            }

        }
    };

    protected void onCloseClick(){
        ((Activity) getContext()).finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                onCloseClick();
                break;
//            case R.id.btn_chat:
//
//                toggleInputbox();
//
//                break;
            case R.id.btn_send_msg:
                String msg = mEdtInput.getText().toString();
                if (msg != null) {
                    msg = msg.trim();
                }

                if (TextUtils.isEmpty(msg)) {
                    Toast.makeText(getContext(), "请输入内容", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean ret = false;
                if (mOpenDanmu.isChecked()) {
                    ret = sendDanmuMsg(msg);
                } else {
                    sendNormalMsg(msg);

                    // for test
                    MessageInfo minfo = new MessageInfo();
                    UserInfoBean uinfo = LoginManager.getInstance().getUserCookie().userinfo;
                    minfo.name = uinfo.nickname;
                    minfo.msg = msg;
                    minfo.image = uinfo.image;
                    minfo.id = uinfo.id;
                    minfo.level = uinfo.user_level + "";
                    mMsgAdapter.addData(minfo);
                    mMsgList.setSelection(mMsgAdapter.getCount());

                    ret = true;
                }

                if (ret) {
                    mEdtInput.setText("");
                    toggleInputbox();
                }
                break;

//            case R.id.btn_share:
//                hideInputbox();
//                ShareWindow sw = new ShareWindow((Activity) getContext());
//                sw.setParam(getShareParam());
//                sw.show();
//                break;
            case R.id.anchor_img:
                showUserWindow(mAnchorData);
                break;
//            case R.id.btn_gift:
//                hideInputbox();
//
//                showGiftWindow();
//                break;
            case R.id.bean_layout:
                Intent intent = new Intent(getContext(), FansRankActivity.class);
                intent.putExtra("uid", mAnchorData.id);
                getContext().startActivity(intent);
                break;
        }
    }

    protected ShareParam getShareParam() {
        return ShareWindow.buildParam(getContext(), mAnchorData.nickname, mAnchorData.id, HotItemInfo.live, null);
    }

    private GiftWindow mGiftWindow;

    private void showGiftWindow() {
        if (mGiftWindow == null) {
            mGiftWindow = new GiftWindow((Activity) getContext(), mRoomId, mAnchorData.id);
            mGiftWindow.setOnGiftSendListener(this);
        }

        if (!mGiftWindow.isShowing()) {
            mGiftWindow.show();
        }
    }

    @Override
    public void onSendGift(MessageInfo minfo) {
        mMsgAdapter.addData(minfo);
        mMsgList.setSelection(mMsgAdapter.getCount());
        mFavorsLayout.addHeart();

        sendGift(minfo);
    }

    private void sendGift(MessageInfo minfo) {
        if (minfo.gift.is_doublehit == 1) {
            mSmallGiftGroup.put(minfo);
        } else {
            mBigGiftWidget.put(minfo);
        }
    }

    protected UserPageWindow mUserWindow;

    protected void showUserWindow(UserInfoBean info) {
        if (mUserWindow == null) {
            mUserWindow = new UserPageWindow((Activity) getContext(), mRoomId);
            mUserWindow.setOnUserPageListener(this);
        }

        if (!mUserWindow.isShowing()) {
            mUserWindow.show(info, mAnchorData.id);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (oldh > h) {
            LogUtils.d("input window show");

        } else {
            LogUtils.d("input window hidden");
            hideInputbox();

        }
    }

    private void toggleInputbox() {
        LayoutParams lp = (LayoutParams) mInputLayout.getLayoutParams();
        if (lp.bottomMargin == 0) {
            lp.bottomMargin = ScreenUtils.dp2px(getContext(), -43);
            mInputLayout.setVisibility(View.INVISIBLE);
            mEdtInput.clearFocus();

            mTopLayout.setVisibility(View.VISIBLE);
        } else {
            lp.bottomMargin = 0;
            mInputLayout.setVisibility(View.VISIBLE);
            mEdtInput.requestFocus();

            mTopLayout.setVisibility(INVISIBLE);
        }

        requestLayout();

        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void hideInputbox() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEdtInput.getWindowToken(), 0);

        LayoutParams lp = (LayoutParams) mInputLayout.getLayoutParams();
        lp.bottomMargin = ScreenUtils.dp2px(getContext(), -43);
        mInputLayout.setVisibility(View.INVISIBLE);
        mEdtInput.clearFocus();

        mTopLayout.setVisibility(VISIBLE);

        requestLayout();
        invalidate();
    }

    private void sendNormalMsg(String txt) {
        MyTxtMessage content = new MyTxtMessage();
        UserInfoBean uinfo = LoginManager.getInstance().getUserCookie().userinfo;
        content.setUserInfo(new CustomUserInfo(uinfo.id, uinfo.nickname, Uri.parse(uinfo.image), uinfo.user_level + ""));
        content.content = new TextContent();
        content.content.text = txt;
        content.content.text_type = 0;
        Message msg = Message.obtain(mRoomId, Conversation.ConversationType.CHATROOM, content);
        IMManager.getInstance().sendMessage(msg);
    }

    private boolean sendDanmuMsg(String txt) {
        // TODO
        if (LoginManager.getInstance().getUserInfo().diamond > 0) {
            MessageInfo minfo = new MessageInfo();
            UserInfoBean uinfo = LoginManager.getInstance().getUserCookie().userinfo;
            minfo.name = uinfo.nickname;
            minfo.image = uinfo.image;
            minfo.msg = txt;
            minfo.level = uinfo.user_level + "";
            minfo.id = uinfo.id;

            // FIXME test
            mDanmuView.addDanmu(minfo);
            mMsgAdapter.addData(minfo);
            mMsgList.setSelection(mMsgAdapter.getCount());

            ChatApi.sendDanmu(getContext(), mRoomId, mAnchorData.id, txt, new ResponseHandler() {
                @Override
                public void onSuccess(String data) {
                    Type typeObject = new TypeToken<BaseResponse<DiamondLeftInfo>>() {
                    }.getType();
                    BaseResponse<DiamondLeftInfo> ret = new Gson().fromJson(data, typeObject);

                    LoginManager.getInstance().getUserInfo().diamond = ret.data.curDiamond;
                    LoginManager.getInstance().saveUserInfo();
                }

                @Override
                public void onFailure(int responseCode, String errorMsg) {

                }
            });

            return true;
        } else {
            Toast.makeText(getContext(), "钻不足，请充值", Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    private void sendFavorMsg() {
        // 发送赞的消息
        String infoText = "点亮了一颗音乐符";
        MyTxtMessage content = new MyTxtMessage();
        UserInfoBean uinfo = LoginManager.getInstance().getUserCookie().userinfo;
        content.setUserInfo(new CustomUserInfo(uinfo.id, uinfo.nickname, Uri.parse(uinfo.image), uinfo.user_level + ""));
        content.content = new TextContent();
        content.content.text = infoText;
        content.content.text_type = 1;
        Message msg = Message.obtain(mRoomId, Conversation.ConversationType.CHATROOM, content);
        IMManager.getInstance().sendMessage(msg);

        MessageInfo minfo = new MessageInfo();
        minfo.id = uinfo.id;
        minfo.name = uinfo.nickname;
        minfo.image = uinfo.image;
        minfo.level = uinfo.user_level + "";
        minfo.msg = infoText;

        mMsgAdapter.addData(minfo);
        mMsgList.setSelection(mMsgAdapter.getCount());
    }

    public void onDestroy() {
        removeCallbacks(mRefreshRunnable);

        mSmallGiftGroup.onDestroy();
        mBigGiftWidget.onDestroy();

        mDanmuView.stop();
        mDanmuView.release();

        if (mGiftWindow != null && mGiftWindow.isShowing()) {
            mGiftWindow.dismiss();
        }
        if (mUserWindow != null && mUserWindow.isShowing()) {
            mUserWindow.dismiss();
        }

        quitRoom();
    }

    protected void quitRoom() {
        IMManager.getInstance().removeMessageListener(this);
        IMManager.getInstance().quitChatRoom(mRoomId);

        LiveApi.quitRoom(getContext(), mRoomId, new ResponseHandler() {
            @Override
            public void onSuccess(String data) {
                if (needToFinishPage) {
                    Type objectType = new TypeToken<BaseResponse<LiveFinishBean>>() {
                    }.getType();
                    BaseResponse<LiveFinishBean> res = new Gson().fromJson(data, objectType);

                    Intent intent = new Intent(getActivity(), MyLiveFinishActivity.class);
                    intent.putExtra("onlines", res.data.income_users);
                    getActivity().startActivity(intent);
                }
            }

            @Override
            public void onFailure(int responseCode, String errorMsg) {

            }
        });
    }

    private Activity getActivity() {
        return (Activity) getContext();
    }

    public void showLoading() {
        mLoadingView.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        mLoadingView.setVisibility(View.GONE);
    }

    public String getOnlineNum() {
        return mOnlineNum.getText().toString();
    }

    @Override
    public void onAtSomeone(final String name) {
        mInputLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                toggleInputbox();
                mEdtInput.setText("@" + name + " ");
                mEdtInput.setSelection(name.length() + 2);
            }
        }, 500);
    }

    @Override
    public void onAttendChanged() {
        if (mRelationTxt.getVisibility() == View.VISIBLE) {
            mRelationTxt.setVisibility(View.GONE);
            mRelationLine.setVisibility(View.GONE);
        } else {
            mRelationTxt.setVisibility(View.VISIBLE);
            mRelationLine.setVisibility(View.VISIBLE);
        }
    }

    public void showPreparing() {
        mPreparingView.setVisibility(View.VISIBLE);
    }

    public void hidePreparing() {
        mPreparingView.setVisibility(View.GONE);
    }

    public void onPause() {
        LiveStatusMessage content = new LiveStatusMessage();
        content.content = new LiveStatusContent();
        UserInfoBean uinfo = LoginManager.getInstance().getUserCookie().userinfo;
        content.setUserInfo(new CustomUserInfo(uinfo.id, uinfo.nickname, Uri.parse(uinfo.image), uinfo.user_level + ""));
        content.content.status = LiveStatusContent.ST_PAUSE;
        Message msg = Message.obtain(mRoomId, Conversation.ConversationType.CHATROOM, content);
        IMManager.getInstance().sendMessage(msg);
    }

    public void onResume() {
        LiveStatusMessage content = new LiveStatusMessage();
        content.content = new LiveStatusContent();
        UserInfoBean uinfo = LoginManager.getInstance().getUserCookie().userinfo;
        content.setUserInfo(new CustomUserInfo(uinfo.id, uinfo.nickname, Uri.parse(uinfo.image), uinfo.user_level + ""));
        content.content.status = LiveStatusContent.ST_RESUME;
        Message msg = Message.obtain(mRoomId, Conversation.ConversationType.CHATROOM, content);
        IMManager.getInstance().sendMessage(msg);
    }
}