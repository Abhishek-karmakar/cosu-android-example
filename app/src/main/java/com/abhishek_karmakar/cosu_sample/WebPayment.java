package com.abhishek_karmakar.cosu_sample;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Created by abhishek on 17/7/17.
 */

public class WebPayment extends Activity
{
    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.webpayment);
        WebView webview = (WebView)findViewById(R.id.webview);
        webview.loadUrl("https://forums.yuplaygod.com");
        // set the javasript to true
        WebSettings websettings = webview.getSettings();
        websettings.setJavaScriptEnabled(true);
    }
}
