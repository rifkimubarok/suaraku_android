package net.soradigital.suaraku;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonObject;

import net.soradigital.suaraku.helper.ApiHelper;
import net.soradigital.suaraku.helper.CustomDialog;
import net.soradigital.suaraku.helper.RequestAdapter;
import net.soradigital.suaraku.helper.SessionManager;
import net.soradigital.suaraku.helper.ValidationHelper;

import org.json.JSONArray;
import org.json.JSONObject;

public class VerifikasiActivity extends AppCompatActivity {

    Toolbar toolbar;
    Button btn_verifikasi;
    SessionManager sessionManager;
    ApiHelper apiHelper;
    TextView txt_otp_title;
    EditText txt_verif_code;
    JSONObject signup_obj;
    ValidationHelper validation;
    CustomDialog dialog;
    private String TAG = VerifikasiActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifikasi);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        btn_verifikasi = (Button) findViewById(R.id.btn_verifikasi);
        txt_otp_title = (TextView) findViewById(R.id.txt_otp_title);
        txt_verif_code = (EditText) findViewById(R.id.txt_verif_code);
        sessionManager = new SessionManager(this);
        apiHelper = new ApiHelper();
        validation = new ValidationHelper(this);
        dialog = new CustomDialog(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back_black));
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimaryDark));
        setTitle("Aktivasi");
        btn_verifikasi.setOnClickListener(verifikasi_act);
        prepare_statement();
    }

    private View.OnClickListener verifikasi_act = v -> {
        verifikasi_akun();
    };

    private void prepare_statement(){
        try{
            JSONObject data_obj = new JSONObject(sessionManager.get_session(sessionManager.SIGNUP_SESSION));
            signup_obj = new JSONObject(data_obj.getString("data"));
            String message = "Masukan kode verifikasi yang kami kirim SMS ke nomor ";
            message+=signup_obj.getString("ACC_PHONE");
            txt_otp_title.setText(message);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void verifikasi_akun(){
        dialog.showDialog();
        try{
            String verif_code = txt_verif_code.getText().toString();
            String url = apiHelper.getUrl();
            url += "&datatype=user&cmd=verifcode&username="+signup_obj.getString("ACC_ALIAS")+"&vercode="+verif_code;
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,url,null,response -> {
               try {
                   if (response.has("status")){
                       boolean verif_obj = response.getBoolean("status");
                       dialog.hideDialog();
                       if (verif_obj){
                           Intent intent = new Intent(VerifikasiActivity.this,UpdatePasswordActivity.class);
                           startActivity(intent);
                       }else{
                            validation.createDialog("Terjadi Kesalahan");
                       }
                   }else{
                       JSONArray error_arr = response.getJSONArray("error");
                       JSONObject error_obj = error_arr.getJSONObject(0);
                       dialog.hideDialog();
                       validation.createDialog(error_obj.getString("message"));
                   }
               }catch (Exception e){
                   e.printStackTrace();
                   dialog.hideDialog();
               }
            },error -> {
                error.printStackTrace();
                dialog.hideDialog();
            });
            request.setShouldCache(false);
            RequestAdapter.getInstance().addToRequestQueue(request);
        }catch (Exception e){
            e.printStackTrace();
            dialog.hideDialog();
        }
    }
}
