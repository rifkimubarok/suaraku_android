package net.soradigital.suaraku;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import net.soradigital.suaraku.helper.ApiHelper;
import net.soradigital.suaraku.helper.CustomDialog;
import net.soradigital.suaraku.helper.RequestAdapter;
import net.soradigital.suaraku.helper.SessionManager;
import net.soradigital.suaraku.helper.ValidationHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PasswordChangeActivity extends AppCompatActivity {

    Toolbar toolbar;
    SessionManager sessionManager;
    ApiHelper apiHelper;
    JSONObject sign_obj;
    ValidationHelper validation;
    CustomDialog dialog;
    TextView label_username,label_no_hp;
    EditText txt_password_aktif,txt_password,txt_password_confirm;
    Button btn_change_pass;
    AlertDialog.Builder dialogbuilder;

    private String TAG = UpdatePasswordActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        label_username = (TextView) findViewById(R.id.label_username);
        label_no_hp = (TextView) findViewById(R.id.label_no_hp);
        txt_password = (EditText) findViewById(R.id.txt_input_password_baru);
        txt_password_aktif = (EditText) findViewById(R.id.txt_input_password_aktif);
        txt_password_confirm = (EditText) findViewById(R.id.txt_input_password_konfirmasi);
        btn_change_pass = (Button) findViewById(R.id.btn_change_pass);
        dialogbuilder = new AlertDialog.Builder(this);
        sessionManager = new SessionManager(this);
        apiHelper = new ApiHelper();
        validation = new ValidationHelper(this);
        dialog = new CustomDialog(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back_black));
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimaryDark));
        setTitle("Edit Profile");
        prepare_statement();
        btn_change_pass.setOnClickListener(v->{
            if (validasi()){
                change_password();
            }
        });
    }

    private void prepare_statement(){
        try{
            JSONObject data_obj = new JSONObject(sessionManager.get_session(sessionManager.LOGIN_SESSION));
            sign_obj = new JSONObject(data_obj.getString("data"));
            label_username.setText(sign_obj.getString("ACC_ALIAS"));
            label_no_hp.setText(sign_obj.getString("ACC_PHONE"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void change_password(){
        dialog.showDialog();
        try {
            String url = apiHelper.getUrl();
            String username = sign_obj.getString("ACC_ALIAS");
            String password = txt_password.getText().toString();
            String aktif_password = txt_password_aktif.getText().toString();
            String password_konfirm = txt_password_confirm.getText().toString();
            String acc_noreg = sign_obj.getString("ACC_NOREG");
            url += "&datatype=user&cmd=changepassword";
            url = url.replaceAll(" ", "%20");
            StringRequest request = new StringRequest(Request.Method.POST,url, responses -> {
                try{
                    JSONObject response = new JSONObject(responses);
                    if (response.has("status")){
                        boolean status = response.getBoolean("status");
                        Log.d(TAG,response.toString());
                        if (status){
                            dialogbuilder.setMessage("Update Password Berhasil!\nSilahkan Lanjutkan Login.");
                            dialogbuilder.setCancelable(false);
                            dialogbuilder.setPositiveButton("OK", (dialog, which) -> {
                                Intent intent = new Intent(PasswordChangeActivity.this,LoginActivity.class);
                                startActivity(intent);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                finishAffinity();
                                finish();
                            });
                            AlertDialog alertDialog = dialogbuilder.create();
                            alertDialog.show();
                        }else{
                            validation.createDialog("Terjadi Kesalahan");
                        }
                    }else{
                        JSONArray error_arr = response.getJSONArray("error");
                        JSONObject obj_error = error_arr.getJSONObject(0);
                        validation.createDialog(obj_error.getString("message"));
                    }
                    dialog.hideDialog();
                }catch (Exception e){
                    dialog.hideDialog();
                    validation.createDialog("Terjadi Kesalahan");
                    e.printStackTrace();
                }
            },error -> {
                dialog.hideDialog();
                validation.createDialog("Terjadi Kesalahan");
                error.printStackTrace();
            }){
                @Override
                protected Map<String,String> getParams(){
                    HashMap<String,String> param = new HashMap<>();
                    param.put("passwordAktif",aktif_password);
                    param.put("passwordBaru",password);
                    param.put("passwordKonfirmasi",password_konfirm);
                    param.put("ACC_NOREG",acc_noreg);
                    param.put("username",username);
                    return param;
                }
            };
            request.setShouldCache(false);
            RequestAdapter.getInstance().addToRequestQueue(request);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean validasi(){
        String password = txt_password.getText().toString();
        String konfirmasi = txt_password_confirm.getText().toString();

        boolean txt_pass_aktif = validation.isTextValid(txt_password_aktif,"Password Aktif harus diisi.");
        if (!txt_pass_aktif)return false;
        boolean txt_pass = validation.isTextValid(txt_password,"Password baru harus diisi.");
        if (!txt_pass)return false;
        boolean txt_konfirmasi = validation.isTextValid(txt_password_confirm,"Konfirmasi Password harus diisi.");
        if (!txt_konfirmasi)return false;
        if (!password.equals(konfirmasi)){
            validation.setError(txt_password_confirm,"Password Tidak Sama");
            return false;
        }
        return true;
    }
}
