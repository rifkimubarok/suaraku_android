package net.soradigital.suaraku;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import net.soradigital.suaraku.api.AuthService;
import net.soradigital.suaraku.api.RetrofitClientInstance;
import net.soradigital.suaraku.caching.FileCacher;
import net.soradigital.suaraku.helper.ApiHelper;
import net.soradigital.suaraku.helper.CustomDialog;
import net.soradigital.suaraku.helper.SessionManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SsoActivity extends AppCompatActivity {

    private ApiHelper apiHelper;
    SessionManager sessionManager;
    AuthService authService;
    private WebView webView;
    private ProgressBar progressBar;
    CustomDialog customDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sso);

        webView = (WebView) findViewById(R.id.webContent);
        progressBar = (ProgressBar) findViewById(R.id.progress_webview);
        sessionManager = new SessionManager(this);
        apiHelper = new ApiHelper();
        authService = RetrofitClientInstance.getRetrofitInstance().create(AuthService.class);
        customDialog = new CustomDialog(SsoActivity.this);

        webView.setWebViewClient(new customView(progressBar));
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.getSettings().setDomStorageEnabled(true);

        loginByToken();
        webView.setVisibility(View.GONE);
    }

    public void loginByToken(){
        customDialog.showDialog();
        String token = sessionManager.getSessionString("token");
        Call<HashMap<String, Object>> call = authService.requestToken(token);
        call.enqueue(new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
                String message = response.body().get("message").toString();
                Boolean success = Boolean.parseBoolean(response.body().get("success").toString());
                if (success) {
                    Map<String, String> data = (Map<String, String>) response.body().get("data");
                    String refreshToken = data.get("token");
                    String url = apiHelper.newBaseUrl + "api/v1/mobile/auth-by-token?token=" + refreshToken;
                    webView.loadUrl(url);
                    customDialog.hideDialog();
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {
            }
        });

    }

    class customView extends WebViewClient {
        private ProgressBar progressBar;

        public customView(ProgressBar progressBar) {
            this.progressBar=progressBar;
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);

            if (url.startsWith(apiHelper.newBaseUrl + "dashboard")){
                Intent intent = new Intent(SsoActivity.this, DashboardActivity.class);
                startActivity(intent);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                finishAffinity();
                finish();
            }
        }
    }
}