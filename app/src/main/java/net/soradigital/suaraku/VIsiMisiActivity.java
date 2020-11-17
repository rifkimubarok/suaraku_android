package net.soradigital.suaraku;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import net.soradigital.suaraku.helper.ApiHelper;
import net.soradigital.suaraku.helper.SessionManager;

import org.json.JSONObject;

import java.util.HashMap;

public class VIsiMisiActivity extends AppCompatActivity {

    SessionManager sessionManager;
    JSONObject sessionObj;
    WebView webView;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visi_misi);
        webView = (WebView) findViewById(R.id.web_visi_misi);
        sessionManager = new SessionManager(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back_black));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimaryDark));
        setTitle("Profil Calon");
        ApiHelper api = new ApiHelper();
        HashMap<String,String> session = sessionManager.get_session(sessionManager.LOGIN_SESSION);
        try {
            sessionObj = new JSONObject(session.get("data"));
            String reg_code = sessionObj.getString("PEM_KOWIL");
            String url = api.getBase_url() + "index.php?req=api&key=21232f297a57a5a743894a0e4a801fc3&datatype=profile&cmd=get_profile_candidate&regcode="+reg_code;
            webView.getSettings().setAllowFileAccess(true);
            webView.getSettings().setAllowContentAccess(true);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setAllowFileAccessFromFileURLs(true);
            webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.loadUrl(url);
            Log.d(VIsiMisiActivity.class.getSimpleName(),url);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
