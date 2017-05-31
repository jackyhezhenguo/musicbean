package com.musicbean.ui.user;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.musicbean.App;
import com.musicbean.R;
import com.musicbean.http.ResponseHandler;
import com.musicbean.http.api.UserApi;
import com.musicbean.http.bean.BaseResponse;
import com.musicbean.http.bean.UserInfoBean;
import com.musicbean.manager.LoginManager;
import com.musicbean.ui.BackActivity;
import com.musicbean.util.BitmapUtils;
import com.musicbean.widget.ChooseImageDialog;

import net.tsz.afinal.FinalBitmap;

import java.io.File;
import java.lang.reflect.Type;

/**
 * Created by boyo on 16/7/3.
 */
public class UserInfoActivity extends BackActivity implements View.OnClickListener {
    private ImageView mHeadImg;
    private ImageView mCoverImg;
    private TextView mStudio;
    private TextView mName;
    private TextView mSign;
    private TextView mHonor;
    private TextView mOpus;
    private TextView mSex;
    private TextView mId;

    private TextView mBtnCopy;

    private static final String SRC_PHOTO_FILE = "srcfile";
    private File photoFile;
    private String mImgPath;
    private final static int REQUEST_CODE_CAMERA = 10001;
    private final static int REQUEST_CODE_GALLERY = 1002;
    private final static int REQUEST_CODE_CROP = 1003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_info);

        setTitle("个人资料");

        mHeadImg = (ImageView) findViewById(R.id.head_img);
        mCoverImg = (ImageView) findViewById(R.id.cover_img);
        mStudio = (TextView) findViewById(R.id.studio);
        mName = (TextView) findViewById(R.id.name);
        mSign = (TextView) findViewById(R.id.sign);
        mHonor = (TextView) findViewById(R.id.honor);
        mOpus = (TextView) findViewById(R.id.opus);
        mSex = (TextView) findViewById(R.id.sex);
        mId = (TextView) findViewById(R.id.user_id);
        mBtnCopy = (TextView) findViewById(R.id.btn_copy);
        mBtnCopy.setOnClickListener(this);

        findViewById(R.id.head_layout).setOnClickListener(this);
        findViewById(R.id.cover_layout).setOnClickListener(this);
        findViewById(R.id.name_layout).setOnClickListener(this);
        findViewById(R.id.studio_layout).setOnClickListener(this);
        findViewById(R.id.sex_layout).setOnClickListener(this);
        findViewById(R.id.honor_layout).setOnClickListener(this);
        findViewById(R.id.opus_layout).setOnClickListener(this);
        findViewById(R.id.sign_layout).setOnClickListener(this);

        initAvatarFile();
    }

    @Override
    protected void onResume() {
        super.onResume();

        UserInfoBean uinfo = LoginManager.getInstance().getUserCookie().userinfo;
        refreshView(uinfo);
    }

    private void refreshView(UserInfoBean uinfo) {
        mName.setText(uinfo.nickname);
        mStudio.setText(uinfo.studio);
        mSign.setText(uinfo.spec_sign);
        mHonor.setText(uinfo.honor);
        mOpus.setText(uinfo.opus);
        mSex.setText(uinfo.getSexStr());
        mId.setText(uinfo.musicid);
        FinalBitmap.getInstance().display(mHeadImg, uinfo.image);
        FinalBitmap.getInstance().display(mCoverImg, uinfo.h_cover);
    }


    private boolean isHeadImage = true;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_layout:
                isHeadImage = true;
                showChooseImgDialog();
                break;
            case R.id.cover_layout:
                isHeadImage = false;
                showChooseImgDialog();
                break;
            case R.id.name_layout:
                Intent intent = new Intent(this, EditNameActivity.class);
                intent.putExtra("str", mName.getText());
                startActivity(intent);
                break;
            case R.id.studio_layout:
                if (LoginManager.getInstance().getUserInfo().user_level < 3) {
                    Toast.makeText(this, "等级3级才可以设置", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(mStudio.getText())) {
                    startActivity(new Intent(this, EditStudioActivity.class));
                }
                break;
            case R.id.sex_layout:
                startActivity(new Intent(this, EditSexActivity.class));
                break;
            case R.id.honor_layout:
                intent = new Intent(this, EditHonorActivity.class);
                intent.putExtra("str", mHonor.getText());
                startActivity(intent);
                break;
            case R.id.opus_layout:
                intent = new Intent(this, EditOpusActivity.class);
                intent.putExtra("str", mOpus.getText());
                startActivity(intent);
                break;
            case R.id.sign_layout:
                intent = new Intent(this, EditSignActivity.class);
                intent.putExtra("str", mSign.getText());
                startActivity(intent);
                break;
            case R.id.btn_copy:
                ClipboardManager myClipboard;
                myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                myClipboard.setPrimaryClip(ClipData.newPlainText("text", mId.getText()));
                Toast.makeText(this, "复制成功", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private ChooseImageDialog mChooseImgDialog;

    private void showChooseImgDialog() {
        if (mChooseImgDialog == null) {
            mChooseImgDialog = new ChooseImageDialog(this);
            Window dialogWindow = mChooseImgDialog.getWindow();
            dialogWindow.setGravity(Gravity.BOTTOM);
            //dialogWindow.setWindowAnimations(R.style.PopupAnimation);
            mChooseImgDialog.setCanceledOnTouchOutside(true);
            mChooseImgDialog.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.btn_takephoto:
                            takePhoto();
                            dissDialog();
                            break;
                        case R.id.btn_pickphoto:
                            pickPhoto();
                            dissDialog();
                            break;
                        case R.id.btn_cancel:
                            dissDialog();
                            break;
                        default:
                            dissDialog();
                            break;
                    }
                }
            });
        }
        if (mChooseImgDialog.isShowing()) {
            mChooseImgDialog.dismiss();
        } else {
            mChooseImgDialog.show();
        }
    }


    private void dissDialog() {
        if (mChooseImgDialog != null) {
            mChooseImgDialog.dismiss();
        }
    }

    private void takePhoto() {
        if (photoFile != null) {
            Intent intent_camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent_camera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile((photoFile)));
            startActivityForResult(intent_camera, REQUEST_CODE_CAMERA);
        }

    }

    private void pickPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, REQUEST_CODE_GALLERY);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    if (photoFile != null && !photoFile.exists()) {
                        Bundle bundle = data.getExtras();
                        Bitmap bitmap = (Bitmap) bundle.get("data");
                        BitmapUtils.saveBitmap(bitmap, photoFile);
                    }

                    Intent intent = new Intent(this, CropImageActivity.class);
                    if (!isHeadImage) {
                        intent.putExtra("size", 1080);
                    }
                    intent.putExtra(CropImageActivity.EXTRA_KEY_IMAGE_PATH, photoFile.getAbsolutePath());
                    startActivityForResult(intent, REQUEST_CODE_CROP);

                }
                break;
            case REQUEST_CODE_GALLERY:
                if (resultCode == Activity.RESULT_OK) {
                    Uri uri = data.getData();
                    String path = BitmapUtils.getImageUriPath(uri, this);
                    if (TextUtils.isEmpty(path)) {
                        Toast.makeText(this, "选择图片失败", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(this, CropImageActivity.class);
                        if (!isHeadImage) {
                            intent.putExtra("size", 1080);
                        }
                        intent.putExtra(CropImageActivity.EXTRA_KEY_IMAGE_PATH, path);
                        startActivityForResult(intent, REQUEST_CODE_CROP);
                    }
                }
                break;
            case REQUEST_CODE_CROP:
                if (resultCode == Activity.RESULT_OK) {
                    mImgPath = data.getStringExtra(CropImageActivity.CROP_IMAGE_PATH);
                    //Bitmap bmp = BitmapFactory.decodeFile(mImgPath);
                    //mHeadImg.setImageBitmap(bmp);

                    if (isHeadImage) {
                        saveHeadImage();
                    } else {
                        saveCoverImage();
                    }
                }
                break;
        }
    }

    private void initAvatarFile() {
        photoFile = new File(App.getInstance().getCachePath(), SRC_PHOTO_FILE + ".jpg");
    }

    private void saveHeadImage() {
        showDialog();
        UserApi.uploadUserImage(this, mImgPath, new ResponseHandler() {
            @Override
            public void onSuccess(String data) {
                Type objectType = new TypeToken<BaseResponse<UserInfoBean>>() {
                }.getType();
                BaseResponse<UserInfoBean> res = new Gson().fromJson(data, objectType);

                LoginManager.getInstance().setUserInfo(res.data);

                FinalBitmap.getInstance().display(mHeadImg, res.data.image);

                Toast.makeText(UserInfoActivity.this, "头像上传成功！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int responseCode, String errorMsg) {
                Toast.makeText(UserInfoActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                hideDialog();
            }
        });
    }

    private void saveCoverImage() {
        showDialog();
        UserApi.uploadCoverImage(this, mImgPath, new ResponseHandler() {
            @Override
            public void onSuccess(String data) {
                Type objectType = new TypeToken<BaseResponse<UserInfoBean>>() {
                }.getType();
                BaseResponse<UserInfoBean> res = new Gson().fromJson(data, objectType);

                LoginManager.getInstance().setUserInfo(res.data);

                FinalBitmap.getInstance().display(mCoverImg, res.data.h_cover);

                Toast.makeText(UserInfoActivity.this, "封面上传成功！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int responseCode, String errorMsg) {
                Toast.makeText(UserInfoActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                hideDialog();
            }
        });
    }
}
