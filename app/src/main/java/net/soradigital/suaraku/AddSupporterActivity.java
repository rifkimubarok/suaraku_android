package net.soradigital.suaraku;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;

import net.soradigital.suaraku.api.CandidateService;
import net.soradigital.suaraku.api.RetrofitClientInstance;
import net.soradigital.suaraku.helper.SessionManager;
import net.soradigital.suaraku.model.Candidate;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddSupporterActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView txt_reff;
    SessionManager sessionManager;
    Button btn_share;
    String Kode_ajakan;
    CandidateService candidateService;
    String shareBody = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_supporter);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txt_reff = (TextView) findViewById(R.id.kode_ajakan);
        btn_share = (Button) findViewById(R.id.btn_share);
        sessionManager = new SessionManager(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back_black));
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimaryDark));
        setTitle("Tambah Pendukung");
        candidateService = RetrofitClientInstance.getRetrofitInstance().create(CandidateService.class);
        Kode_ajakan = "";
        HashMap<String,String> sesi = sessionManager.get_session(sessionManager.LOGIN_SESSION);
        String reff_id = sessionManager.getSessionString("reff_id");

        loadCandidate(reff_id);
        txt_reff.setText(reff_id);
        btn_share.setOnClickListener(v->{
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Mengajak");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Bagikan Melalui"));
        });
    }

    private void loadCandidate(String reff){
        String token = sessionManager.getSessionString("token");
        Call<HashMap<String, Object>> call = candidateService.getCandidate2(token);
        call.enqueue(new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
                String message = response.body().get("message").toString();
                Boolean success = Boolean.parseBoolean(response.body().get("success").toString());
                if(success) {
                    Map<String, Object> data = (Map<String, Object>) response.body().get("data");
                    Gson gson = new Gson();
                    String json = gson.toJson(data);
                    Candidate candidate = gson.fromJson(json, Candidate.class);
                    shareBody = candidate.getCANPROMOSI() + "\n kemudian masukan ID Rujukan *"+ reff + "*";
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {

            }
        });
    }
}
