package net.soradigital.suaraku;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import net.soradigital.suaraku.helper.ApiHelper;
import net.soradigital.suaraku.helper.CustomDialog;
import net.soradigital.suaraku.helper.SessionManager;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;

public class RecapActivity extends AppCompatActivity {
    Toolbar toolbar;
    WebView webView;
    SessionManager sessionManager;
    ApiHelper apiHelper;
    CustomDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recap);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        webView = (WebView) findViewById(R.id.webContent);
        sessionManager = new SessionManager(this);
        apiHelper = new ApiHelper();
        dialog = new CustomDialog(this);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back_black));
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimaryDark));

        setTitle("Rekapitulasi dukungan perwilayah");

        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.setWebViewClient(new customWebClient(dialog));
        webView.setWebChromeClient(new customChromeClient());
        load_data();
    }

    public void load_data(){
        HashMap<String,String> session = sessionManager.get_session(sessionManager.LOGIN_SESSION);
        try{
            JSONObject obj_user = new JSONObject(session.get("data"));
            String ACC_KOWIL = obj_user.getString("ACC_KOWIL");

            String url = apiHelper.getUrl()+"&datatype=rekap&cmd=rekap&regcode="+ACC_KOWIL;
            url = url.replaceAll(" ", "%20");
            webView.loadUrl(url);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    class customWebClient extends WebViewClient{
        CustomDialog cdialog;
        public customWebClient(CustomDialog dialog){
            this.cdialog = dialog;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon){
            cdialog.showDialog();
        }

        @Override
        public void onPageFinished(WebView view, String url){
            cdialog.hideDialog();
        }

    }

    class  customChromeClient extends WebChromeClient {

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            Log.d("Got console message",consoleMessage.message());
            return true;
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            Log.d("Got Allert",message + result);
            return super.onJsAlert(view, url, message, result);
        }
    }
}
