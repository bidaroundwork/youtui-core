/**
 * Copyright 2010-present Facebook.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.bidaround.ytcore.social;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import cn.bidaround.ytcore.login.AuthActivity;
import cn.bidaround.ytcore.login.AuthListener;
import cn.bidaround.ytcore.util.Util;

public class WebDialog extends Dialog {
	
    private static final int NO_PADDING_SCREEN_WIDTH = 480;
    private static final int MAX_PADDING_SCREEN_WIDTH = 800;
    private static final int NO_PADDING_SCREEN_HEIGHT = 800;
    private static final int MAX_PADDING_SCREEN_HEIGHT = 1280;
    private static final double MIN_SCALE_FACTOR = 0.5;
    
    public static final int DEFAULT_THEME = android.R.style.Theme_Translucent_NoTitleBar;
    
    private String expectedRedirectUrl;
    private WebView webView;
    private ProgressDialog spinner;
    private FrameLayout contentFrameLayout;
    
    private Bundle bundle;
    
    private boolean finish = false;
    
    private AuthListener listener;
    
    private Context context;
    
    private String url;

    private OnAfterRequest afterRequest;
    
    public WebDialog(Context context, String url, String redirectUrl, AuthListener listener, OnAfterRequest afterRequest) {
        this(context, url, redirectUrl, DEFAULT_THEME, listener, afterRequest);
    }

    public WebDialog(Context context, String url, String redirectUrl, int theme, AuthListener listener, OnAfterRequest afterRequest) {
        super(context, theme);
        this.expectedRedirectUrl = redirectUrl;
        this.listener = listener;
        this.context = context;
        this.url = url;
        this.afterRequest = afterRequest;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        spinner = new ProgressDialog(getContext());
        spinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
        spinner.setMessage(getContext().getString(getContext().getResources().getIdentifier("yt_loading", "string", getContext().getPackageName())));
        spinner.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
            	WebDialog.this.onCancel();
            }
        });

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        contentFrameLayout = new FrameLayout(getContext());

        calculateSize();
        getWindow().setGravity(Gravity.CENTER);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        
//        spinner.show();
        
        spinner.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				
			}
		});
        
        setUpWebView(0);
    }
    
    @Override
    public void onBackPressed() {
    	super.onBackPressed();
    	onCancel();
    }
    
    private void onCancel(){
    	dismiss();
        if(listener != null)
        	listener.onAuthCancel();
        
        if(context instanceof AuthActivity)
        	((Activity)context).finish();
    }

    protected WebView getWebView() {
        return webView;
    }

    private void calculateSize() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels < metrics.heightPixels ? metrics.widthPixels : metrics.heightPixels;
        int height = metrics.widthPixels < metrics.heightPixels ? metrics.heightPixels : metrics.widthPixels;

        int dialogWidth = Math.min(
                getScaledSize(width, metrics.density, NO_PADDING_SCREEN_WIDTH, MAX_PADDING_SCREEN_WIDTH),
                metrics.widthPixels);
        int dialogHeight = Math.min(
                getScaledSize(height, metrics.density, NO_PADDING_SCREEN_HEIGHT, MAX_PADDING_SCREEN_HEIGHT),
                metrics.heightPixels);

        getWindow().setLayout(dialogWidth, dialogHeight);
    }

    private int getScaledSize(int screenSize, float density, int noPaddingSize, int maxPaddingSize) {
        int scaledSize = (int) ((float) screenSize / density);
        double scaleFactor;
        if (scaledSize <= noPaddingSize) {
            scaleFactor = 1.0;
        } else if (scaledSize >= maxPaddingSize) {
            scaleFactor = MIN_SCALE_FACTOR;
        } else {
            scaleFactor = MIN_SCALE_FACTOR +
                    ((double) (maxPaddingSize - scaledSize))
                            / ((double) (maxPaddingSize - noPaddingSize))
                            * (1.0 - MIN_SCALE_FACTOR);
        }
        return (int) (screenSize * scaleFactor);
    }

    @SuppressWarnings("deprecation")
	@SuppressLint("SetJavaScriptEnabled")
    protected void setUpWebView(int margin) {
        LinearLayout webViewContainer = new LinearLayout(getContext());
        webView = new WebView(getContext()) {
            @Override
            public void onWindowFocusChanged(boolean hasWindowFocus) {
                try {
                    super.onWindowFocusChanged(hasWindowFocus);
                } catch (NullPointerException e) {
                }
            }
        };
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setWebViewClient(new DialogWebViewClient());
        webView.setWebChromeClient(new WebChromeClient(){
        	@Override
        	public void onProgressChanged(WebView view, int newProgress) {
        		super.onProgressChanged(view, newProgress);
        		if(newProgress == 100)
        			if(!finish)
                    	spinner.dismiss();
        	}
        });
        webView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        webView.setVisibility(View.INVISIBLE);
        webView.getSettings().setSavePassword(false);
        webView.getSettings().setSaveFormData(false);
        
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);

        webViewContainer.setPadding(margin, margin, margin, margin);
        webViewContainer.addView(webView);
        
        contentFrameLayout.addView(webViewContainer);
        setContentView(contentFrameLayout);
    }
    
    private class DialogWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            dismiss();
            if(listener != null)
            	listener.onAuthFail();
        }
        
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            
            if (url.startsWith(WebDialog.this.expectedRedirectUrl)) {
        		Uri u = Uri.parse(url);
        		bundle = Util.parseUrlQueryString(u.getQuery());
        		bundle.putAll(Util.parseUrlQueryString(u.getFragment()));
            	
        		finish = true;
        		
        		if(afterRequest != null)
        			afterRequest.onAfterRequest(bundle);
        		
        		// 以这个开头的又会进行重定向
        		if(!url.startsWith("https://open.t.qq.com/cgi-bin/oauth2/authorize"))
        			finish();
            } 
            else
            	if(!spinner.isShowing())
                		spinner.show();
        }
        
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            contentFrameLayout.setBackgroundColor(Color.TRANSPARENT);
            webView.setVisibility(View.VISIBLE);
        }
    }
    
    private void finish(){
    	webView.stopLoading();
    	dismiss();
    }
    
    public interface OnAfterRequest{
    	public void onAfterRequest(Bundle bundle);
    }
    

    @Override
    public void dismiss() {
        if (webView != null) {
            webView.stopLoading();
        }
        if (spinner.isShowing()) {
            spinner.dismiss();
        }
        super.dismiss();
    }
    
    @Override
    public void show() {
    	super.show();
    	spinner.show();
    }
}
