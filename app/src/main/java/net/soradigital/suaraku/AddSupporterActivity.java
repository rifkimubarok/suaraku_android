package net.soradigital.suaraku;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import net.soradigital.suaraku.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class AddSupporterActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView txt_reff;
    SessionManager sessionManager;
    Button btn_share;
    String Kode_ajakan;
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

        Kode_ajakan = "";
        HashMap<String,String> sesi = sessionManager.get_session(sessionManager.LOGIN_SESSION);
        try{
            JSONObject obj_sesi = new JSONObject(sesi.get("data"));
            txt_reff.setText(obj_sesi.getString("ACC_REFF_ID"));
            Kode_ajakan = obj_sesi.getString("ACC_REFF_ID");
        }catch (Exception e){
            e.printStackTrace();
        }

        btn_share.setOnClickListener(v->{
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Untuk mendukung A IWAN SAPUTRA menjadi Bupati Tasikmalaya 2020-2025, pastikan anda dan keluarga anda TERDAFTAR dalam Aplikasi SUARAKU.\n" +
                    "Untuk mendaftar sebagai pendukung A IWAN SAPUTRA silahkan Klik link berikut ini https://bit.ly/2qhVBQb\n" +
                    "kemudian masukan ID Rujukan "+Kode_ajakan;
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Mengajak");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        });
    }
}
