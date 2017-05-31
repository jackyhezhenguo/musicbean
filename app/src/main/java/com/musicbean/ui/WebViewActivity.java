package com.musicbean.ui;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CompoundButton;

import com.musicbean.R;
import com.musicbean.ui.live.ShareWindow;


public class WebViewActivity extends BackActivity {

    String url;
    String title;
    private WebView mWebView;
    private int type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        Uri uri = getIntent().getData();
        if (uri != null) {
            url = uri.getQueryParameter("url");
            title = url;
            String t = uri.getQueryParameter("type");
            if (!TextUtils.isEmpty(t)) {
                type = Integer.parseInt(t);
            }
        } else {
            url = getIntent().getStringExtra("url");
            title = getIntent().getStringExtra("title");
            type = getIntent().getIntExtra("type", 0);
        }

        if (type == 1) {
            setRightButtonDrawable(R.drawable.icon_web_share);
            setCallback(new TitleBarRightButtonCallback() {
                @Override
                public void onRightButtonClick(View view) {
                    ShareWindow swin = new ShareWindow(WebViewActivity.this);
                    swin.setParam(swin.buildParam(WebViewActivity.this, url, title, title));
                    swin.show();
                }

                @Override
                public void onRightButtonStatusChange(CompoundButton button, boolean status) {

                }
            });
        }

        if (TextUtils.isEmpty(title)) {
            title = url;
        }
        setTitle(title);

        mWebView = (WebView) findViewById(R.id.webview);

        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO Auto-generated method stub
                if (newProgress == 100) {
                    // 网页加载完成h
                    hideDialog();
                } else {
                    // 加载中
                }
            }

//            @Override
//            public boolean onJsAlert(WebView view, String url, String message,
//                                     final JsResult result) {
//                ShareWindow swin = new ShareWindow(WebViewActivity.this);
//                swin.setParam(swin.buildParam(WebViewActivity.this, url, title, title));
//                swin.show();
//
//                result.confirm();//如果不加这句，线程就会卡死，alert只会劫持一次，以后再也不会劫持
//                return true;
//            }
        });

        showDialog();
        if (TextUtils.isEmpty(url)) {
            finish();
            return;
        }
        if (!url.startsWith("file") && !url.startsWith("http")) {
            url = "http://" + url;
        }

        // FIXME test
        //url = "http://139.129.27.90:8000/huodong/index.html?nsukey=4DAUyxISSrM%2FH5piJy1mqkdYcEjyZnLGW3qT4o8c5lONnuUGA5oZlRqTz4bXhzmfQcFJ4XWdgQLJ9e%2BDSHp0LclIhuc9YyyMrAtuVkfSkUuafq9IvJMQnzfDwK6meIVylnBAlW9x9Cuv8NNJwiMuz21sv7K06Y42b3faJ4KcI9yajvMksCwnuBLdeJRZFz1C";

        showDialog();
        mWebView.loadUrl(url);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.destroy();
    }
}
