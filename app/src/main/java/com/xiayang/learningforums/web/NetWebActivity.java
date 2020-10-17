package com.xiayang.learningforums.web;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.xiayang.learningforums.R;

public class NetWebActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar pb;
    private TextView tvTitle;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_web);
        webView = findViewById(R.id.webView);
        pb = findViewById(R.id.net_web_pb);
        tvTitle = findViewById(R.id.net_web_title);
        toolbar = findViewById(R.id.net_web_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // 设置 webview 的信息配置
        WebSettings settings = webView.getSettings();
        // 设置能够加载 js
        settings.setJavaScriptEnabled(true);
        // 设置能够访问文件
        settings.setAllowFileAccess(true);
        // 设置里面包含路径的url文件
        settings.setAllowFileAccessFromFileURLs(true);



        // 加载网页信息
        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        webView.loadUrl(data);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                // 使打开网页时不调用系统浏览器， 而是在本WebView中显示
                view.loadUrl("data");
                return false;
            }


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                // 开始加载网页是处理
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // 网页加载完成时处理，如：图片的点击事件
                addImageClickListener(view);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                // 网页加载失败时的处理，如：提示失败，或显示新的页面
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            // 进度条
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    pb.setVisibility(View.GONE);
                } else {
                    pb.setVisibility(View.VISIBLE);
                    pb.setProgress(newProgress);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                tvTitle.setText(title);
                super.onReceivedTitle(view, title);
            }
        });

        // 因为在 js 当中调用了Android 的代码，所以需要设置通道
        webView.addJavascriptInterface(new NetJavaScriptInterface(this), "imageListener");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 页面回退,而不是直接finish
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // 添加网页当中的图片监听的函数
    public void addImageClickListener(WebView webView) {
        webView.loadUrl("javascript:(function() {" +
                "\t\t\tvar objs = document.getElementsByTagName(\"img\")" +
                "\t\t\tvar arr = [];" +
                "\t\t\tfor(var i = 0; i < objs.length; i++) {" +
                "\t\t\t\tarr[i] = [i].getAttribute('data-src')" +
                "\t\t\t}\n" +
                "\t\t\tfor(var i = 0; i < objs.length; i++) {" +
                "\t\t\t\tobjs[i].onclick = function() {" +
                "\t\t\t\t\twindow.imageListener.openImage(arr, this.getAttribute('data-src'))" +
                "\t\t\t\t}" +
                "\t\t\t}" +
                "\t\t})()");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return true;
    }
}
