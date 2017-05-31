package com.musicbean.ui.live;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.musicbean.R;
import com.musicbean.http.ResponseHandler;
import com.musicbean.http.api.ChatApi;
import com.musicbean.http.api.UserApi;
import com.musicbean.http.bean.BaseResponse;
import com.musicbean.http.bean.HotItemInfo;
import com.musicbean.http.bean.UserInfoBean;
import com.musicbean.http.bean.UserPageInfo;
import com.musicbean.manager.LevelManager;
import com.musicbean.manager.LoginManager;
import com.musicbean.ui.pay.ConfirmDialog;
import com.musicbean.ui.user.ManagerListActivity;
import com.musicbean.ui.user.UserPageActivity;
import com.musicbean.widget.NotifyDialog;

import net.tsz.afinal.FinalBitmap;

import java.lang.reflect.Type;

/**
 * Created by boyo on 16/7/3.
 */
public class UserPageWindow extends ShareWindow implements View.OnClickListener {

    private TextView mName;
    private TextView mStudio;
    private TextView mAttendsFans;
    private TextView mSign;

    private ImageView mImage;
    private ImageView mFansImage;

    private TextView mBtnFollow;
    private TextView mBtnManage;
    private TextView mBtnAt;

    private View mOpLayout;

    private View mManageLayout;
    private View mSetManagerLayout;
    private View mManagerListLayout;

    private String mRoomId;

    public UserPageWindow(Activity context, String roomid) {
        super(context);
        mRoomId = roomid;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.user_page_window_layout;
    }

    @Override
    protected void initViews() {
        View v = getContentView();
        v.findViewById(R.id.btn_close).setOnClickListener(this);
        mBtnFollow = (TextView) v.findViewById(R.id.btn_attend);
        mBtnFollow.setOnClickListener(this);
        mBtnAt = (TextView) v.findViewById(R.id.btn_at);
        mBtnAt.setOnClickListener(this);
        mBtnManage = (TextView) v.findViewById(R.id.btn_manage);
        mBtnManage.setOnClickListener(this);

        mName = (TextView) v.findViewById(R.id.txt_name);
        mStudio = (TextView) v.findViewById(R.id.txt_studio);
        mAttendsFans = (TextView) v.findViewById(R.id.txt_attends_fans);
        mSign = (TextView) v.findViewById(R.id.txt_sign);

        mImage = (ImageView) v.findViewById(R.id.user_img);
        mFansImage = (ImageView) v.findViewById(R.id.fans_img);
        mImage.setOnClickListener(this);
        mFansImage.setOnClickListener(this);

        mOpLayout = v.findViewById(R.id.op_layout);

        mSetManagerLayout = v.findViewById(R.id.set_manager_layout);
        mManagerListLayout = v.findViewById(R.id.managers_layout);
        mManageLayout = v.findViewById(R.id.manage_layout);
        v.findViewById(R.id.btn_gag).setOnClickListener(this);
        v.findViewById(R.id.btn_cancel).setOnClickListener(this);
        v.findViewById(R.id.btn_set_manager).setOnClickListener(this);
        v.findViewById(R.id.btn_managers).setOnClickListener(this);

    }

    private boolean hideOpLayout = false;
    private String mType;

    public void hideOpLayout(String type) {
        hideOpLayout = true;
        mType = type;
//        if (HotItemInfo.video.equals(type)) {
//        } else if (HotItemInfo.record.equals(type)) {
//            //mBtnFollow.setVisibility(View.INVISIBLE);
//            mBtnAt.setVisibility(View.INVISIBLE);
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                dismiss();
                break;
            case R.id.btn_attend:
                UserInfoBean info = (UserInfoBean) mBtnFollow.getTag();
                if (info == null) {
                    return;
                }

                if (info.relation == 0) {
                    follow(v.getContext(), info);
                    mBtnFollow.setText("已关注");
                    info.relation = 1;
                } else {
                    disfollow(v.getContext(), info);
                    mBtnFollow.setText("关注");
                    info.relation = 0;
                }

                if (mCurUser.id.equals(mAnchorId) && mListener != null) {
                    mListener.onAttendChanged();
                }
                break;
            case R.id.btn_manage:
                info = (UserInfoBean) mBtnManage.getTag();
                if (info == null) {
                    return;
                }
                if (mBtnManage.getText().equals("管理")) {
                    if (!isManager) {
                        NotifyDialog dialog = new NotifyDialog(v.getContext());
                        dialog.show("对不起，您不是该直播间的管理员");
                        return;
                    }

                    mManageLayout.setVisibility(View.VISIBLE);
                    if (mAnchorId.equals(LoginManager.getInstance().getUserCookie().userinfo.id)) {
                        // 主播管理
                        mSetManagerLayout.setVisibility(View.VISIBLE);
                        mManagerListLayout.setVisibility(View.VISIBLE);
                    } else {
                        // 普通管理
                        mSetManagerLayout.setVisibility(View.GONE);
                        mManagerListLayout.setVisibility(View.GONE);
                    }
                } else {
                    //report(v.getContext(), info);
                    showReportDialog(v.getContext(), info);
                }

                break;
            case R.id.user_img:
                Intent intent = new Intent(v.getContext(), UserPageActivity.class);
                intent.putExtra("uinfo", mCurUser);
                v.getContext().startActivity(intent);
                break;
            case R.id.fans_img:
                info = (UserInfoBean) mFansImage.getTag();
                if (info == null) {
                    return;
                }

                intent = new Intent(v.getContext(), UserPageActivity.class);
                intent.putExtra("uinfo", info);
                v.getContext().startActivity(intent);
                break;
            case R.id.btn_cancel:
                mManageLayout.setVisibility(View.GONE);
                break;
            case R.id.btn_set_manager:
                setManager(v.getContext());
                mManageLayout.setVisibility(View.GONE);
                break;
            case R.id.btn_managers:
                intent = new Intent(v.getContext(), ManagerListActivity.class);
                intent.putExtra("uid", mAnchorId);
                v.getContext().startActivity(intent);
                break;
            case R.id.btn_gag:
                gagUser(v.getContext());
                mManageLayout.setVisibility(View.GONE);
                break;
            case R.id.btn_at:
                dismiss();
                if (mListener != null) {
                    mListener.onAtSomeone(mCurUser.nickname);
                }
                break;
        }
    }

    private void setManager(Context context) {
        ChatApi.setManager(context, mCurUser.id, mRoomId, new ResponseHandler() {
            @Override
            public void onSuccess(String data) {

            }

            @Override
            public void onFailure(int responseCode, String errorMsg) {

            }
        });
    }

    private void gagUser(Context context) {
        ChatApi.gagUser(context, mCurUser.id, mRoomId, new ResponseHandler() {
            @Override
            public void onSuccess(String data) {

            }

            @Override
            public void onFailure(int responseCode, String errorMsg) {

            }
        });
    }

    private ConfirmDialog mReportDialog;

    private void showReportDialog(final Context context, final UserInfoBean info) {
        if (mReportDialog == null) {
            mReportDialog = new ConfirmDialog(context);
            mReportDialog.setOnDialogListener(new ConfirmDialog.OnDialogListener() {
                @Override
                public void onEnsure() {
                    report(context, info);
                }
            });
        }
        mReportDialog.show("确定举报该用户?");
    }

    private void report(final Context context, UserInfoBean info) {
        UserApi.report(context, info.id, new ResponseHandler() {
            @Override
            public void onSuccess(String data) {
                Toast.makeText(context, "举报成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int responseCode, String errorMsg) {

            }
        });
    }

    private void follow(final Context context, final UserInfoBean info) {
        UserApi.follow(context, info.id, new ResponseHandler() {
            @Override
            public void onSuccess(String data) {
                info.relation = 1;
                Toast.makeText(context, "关注成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int responseCode, String errorMsg) {

            }
        });
    }

    private void disfollow(final Context context, final UserInfoBean info) {
        UserApi.disFollow(context, info.id, new ResponseHandler() {
            @Override
            public void onSuccess(String data) {
                info.relation = 0;
                Toast.makeText(context, "取消成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int responseCode, String errorMsg) {

            }
        });
    }

    public void setData(UserInfoBean info) {
        if (info.id.equals(LoginManager.getInstance().getUserCookie().userinfo.id)) {
            mOpLayout.setVisibility(View.INVISIBLE);
        } else if (hideOpLayout) {
            if (HotItemInfo.record.equals(mType)) {
                //mBtnFollow.setVisibility(View.INVISIBLE);
                mOpLayout.setVisibility(View.VISIBLE);
                mBtnAt.setVisibility(View.INVISIBLE);
            } else {
                mOpLayout.setVisibility(View.INVISIBLE);
            }
        } else {
            mOpLayout.setVisibility(View.VISIBLE);
        }

        FinalBitmap.getInstance().display(mImage, info.image);
        mName.setText(LevelManager.getInstance().getNameSexLevelSS(info));
        mStudio.setText(info.studio);
        mSign.setText("个性签名：" + info.spec_sign);

        setAttendFan("0", "0");

        if (info.relation == 1) {
            mBtnFollow.setText("已关注");
        } else {
            mBtnFollow.setText("关注");
        }

        mBtnFollow.setTag(info);
        mBtnManage.setTag(info);
    }

    private void setAttendFan(String attends, String fans) {
        String str = getContentView().getResources().getString(R.string.attend_fans, attends, fans);
        SpannableString ss = new SpannableString(str);
        ForegroundColorSpan fcs = new ForegroundColorSpan(getContentView().getResources().getColor(R.color.text_green));
        ss.setSpan(fcs, 2, 2 + attends.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        fcs = new ForegroundColorSpan(getContentView().getResources().getColor(R.color.text_green));
        ss.setSpan(fcs, 6 + attends.length(), 6 + attends.length() + fans.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        mAttendsFans.setText(ss);
    }

    private UserInfoBean mCurUser;
    private String mAnchorId;
    private boolean isManager;

    public void show(final UserInfoBean info, String anchorId) {
        mCurUser = info;
        mAnchorId = anchorId;
        setData(info);
        if (mCurUser.id.equals(mAnchorId)) {
            mBtnManage.setText("举报");
        } else {
            mBtnManage.setText("管理");
        }
        isManager = false;
        mFansImage.setImageResource(R.drawable.icon_default_new_header);

        super.show();

        UserApi.getUserpageLive(mActivityRef.get(), info.id, anchorId, new ResponseHandler() {
            @Override
            public void onSuccess(String data) {
                if (isShowing() && info.id.equals(mCurUser.id)) {
                    Type objectType = new TypeToken<BaseResponse<UserPageInfo>>() {
                    }.getType();
                    BaseResponse<UserPageInfo> res = new Gson().fromJson(data, objectType);

                    isManager = res.data.manager_power == 1;

                    setData(res.data.userinfo);
                    if (res.data.followinfo.total > 0) {
                        FinalBitmap.getInstance().display(mFansImage, res.data.followinfo.list.get(0).image);
                        mFansImage.setTag(res.data.followinfo.list.get(0));
                    }
                    setAttendFan(res.data.conceninfo.total + "", res.data.followinfo.total + "");
                }
            }

            @Override
            public void onFailure(int responseCode, String errorMsg) {

            }
        });
    }

    @Override
    public void dismiss() {
        if (mManageLayout.getVisibility() == View.VISIBLE) {
            mManageLayout.setVisibility(View.GONE);
        }
        super.dismiss();
    }

    public interface OnUserPageListener {
        void onAtSomeone(String name);

        void onAttendChanged();
    }

    private OnUserPageListener mListener;

    public void setOnUserPageListener(OnUserPageListener l) {
        mListener = l;
    }
}
