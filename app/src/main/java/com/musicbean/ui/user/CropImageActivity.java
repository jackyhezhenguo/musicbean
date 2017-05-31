package com.musicbean.ui.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Toast;

import com.musicbean.App;
import com.musicbean.R;
import com.musicbean.ui.BackActivity;
import com.musicbean.util.BitmapUtils;
import com.musicbean.widget.cropimage.ClipSelectWidget;

import java.io.File;


public class CropImageActivity extends BackActivity implements OnClickListener {

    public final static String EXTRA_KEY_IMAGE_PATH = "extra_image_path";
    public final static String CROP_IMAGE_PATH = "crop_image_path";
    private ClipSelectWidget clipWidget;
    private String imgPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corp_image);
        initView();
        initBitmap();

        int size = getIntent().getIntExtra("size", -1);

        if (size > 0) {
            clipWidget.setScaleSizeMax(size);
        }
    }

    private void initView() {
        clipWidget = (ClipSelectWidget) findViewById(R.id.clip_layout);
        findViewById(R.id.btn_ok).setOnClickListener(this);
        findViewById(R.id.btn_rotate).setOnClickListener(this);
        setTitle("移动和缩放");
    }

    private void initBitmap() {
        try {

            imgPath = getIntent().getStringExtra(EXTRA_KEY_IMAGE_PATH);
            File file = new File(imgPath);
            if (file.exists()) {
                Window window = getWindow();
                clipWidget.setSourceImage(BitmapFactory.decodeFile(imgPath), window);
            } else {
                Toast.makeText(this, "图片不存在", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (OutOfMemoryError e) {
            // TODO: handle exception
            Toast.makeText(this, "图片处理失败", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clipWidget.onDestory();
    }

    private void clipBitmap() {
        try {
            Bitmap bitmap = clipWidget.getBitmap();
            File file = new File(App.getInstance().getCachePath(), "corp.jpg");
            if (bitmap != null && file != null) {
                boolean isSave = BitmapUtils.saveBitmap(bitmap, file);
                if (isSave) {
                    Intent intent = new Intent();
                    intent.putExtra(CROP_IMAGE_PATH, file.getAbsolutePath());
                    setResult(RESULT_OK, intent);
                }
            }
            this.finish();
        } catch (Exception e) {
            // TODO: handle exception
            this.finish();
        }


    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btn_ok:
                clipBitmap();
                break;
            case R.id.btn_rotate:
                if (clipWidget != null) {
                    clipWidget.rotate(90f);
                }
                break;
            default:
                break;
        }
    }

}
