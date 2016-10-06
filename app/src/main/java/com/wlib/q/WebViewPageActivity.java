package com.wlib.q;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

@SuppressLint("SetJavaScriptEnabled")
public class WebViewPageActivity extends Activity implements OnClickListener{
	private WebView webView;
	private String url;
	private String title = "";
	public static final String WEB_URL = "url";
	public static final String WEB_TITLE = "title";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_webview);
		webView =  (WebView)findViewById(R.id.takeaway_webview_content);
		initWebView();
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		default:
			break;
		}
	}
	
	//初始化 网页
	private void initWebView() {
		Bundle bundle = getIntent().getExtras();
		url = bundle.getString(WEB_URL);
		title = bundle.getString(WEB_TITLE);
		webView.loadUrl(url);
		webView.setWebChromeClient(new WebChromeClient() {
		});
		setJavaScript();
		setCacheMode(true);
	}
	
	@Override
	protected void onPause() {
		if (webView!=null) {
			webView.reload();
		}
		super.onPause();
	}
	
	//开启支持JavaScript
	private void setJavaScript() {
		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setSupportZoom(true);  
		settings.setDomStorageEnabled(true);  
//		settings.setPluginsEnabled(true);  
		webView.requestFocus();  
		settings.setUseWideViewPort(true);  
		settings.setLoadWithOverviewMode(true);  
		settings.setSupportZoom(true);  
		settings.setBuiltInZoomControls(true);  
	}
	
	private void setCacheMode(boolean useCache) {
		if (useCache) {
			webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		}else {
			webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		}
	}
}
