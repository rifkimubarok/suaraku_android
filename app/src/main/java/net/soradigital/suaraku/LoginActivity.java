package net.soradigital.suaraku;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import net.soradigital.suaraku.api.AuthService;
import net.soradigital.suaraku.api.RetrofitClientInstance;
import net.soradigital.suaraku.helper.ApiHelper;
import net.soradigital.suaraku.helper.CustomDialog;
import net.soradigital.suaraku.helper.SessionManager;
import net.soradigital.suaraku.helper.ValidationHelper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {

    TextView btn_register;
    Button btn_login;
    private ApiHelper apiHelper = new ApiHelper();
    private EditText txt_username;
    private EditText txt_password;
    private SessionManager sessionManager;
    private ValidationHelper validation;

    private String TAG = LoginActivity.class.getCanonicalName();

    private static final int WRITE_REQUEST_CODE =1;
    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_register = (TextView)findViewById(R.id.txt_register);
        btn_login = (Button) findViewById(R.id.btn_login);
        txt_username = (EditText) findViewById(R.id.txt_input_username);
        txt_password =(EditText) findViewById(R.id.txt_input_password);
        sessionManager = new SessionManager(this);
        validation = new ValidationHelper(this);
        btn_register.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        if (Build.VERSION.SDK_INT >= 23){
            requestPermissions(permissions, WRITE_REQUEST_CODE);
        }

        btn_login.setOnClickListener( v -> {
            do_login(txt_username.getText().toString(),txt_password.getText().toString());
        });
    }

    private void do_login(String username,String password){
        if (!validation()){
            return;
        }
        CustomDialog dialog = new CustomDialog(this);
        dialog.showDialog();

        AuthService authService = RetrofitClientInstance.getRetrofitInstance().create(AuthService.class);
        Call<HashMap<String, Object>> call = authService.login(username, password);
        call.enqueue(new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
                String message = response.body().get("message").toString();
                Boolean success = Boolean.parseBoolean(response.body().get("success").toString());
                if (success) {
                    Map<String, Object> data = (Map<String, Object>) response.body().get("data");
                    String token = data.get("token").toString();
                    String name = data.get("name").toString();
                    String reff_id = data.get("reff_id").toString();
                    List<String> roles = (List<String>) data.get("role");
                    String role = roles.size() > 0 ? roles.get(0) : "";

                    sessionManager.set_session("Bearer " + token, "token");
                    sessionManager.set_session(name, "name");
                    sessionManager.set_session(role, "role");
                    sessionManager.set_session(reff_id, "reff_id");
                    sessionManager.set_session(username, "username");
                    sessionManager.set_session(password, "password");

                    Intent intent = new Intent(LoginActivity.this, SsoActivity.class);
                    startActivity(intent);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    finishAffinity();
                    finish();
                } else {
                    dialog.createToast(message);
                }
                dialog.hideDialog();
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {
                dialog.hideDialog();
                dialog.createToast("Terjadi Kesalahan");
            }
        });
    }


    public boolean validation(){

        boolean txt_user = validation.isTextValid(txt_username,"Username Tidak boleh kosong");
        if (!txt_user){return false;}
        boolean txt_pass = validation.isTextValid(txt_password,"Password tidak boleh kosong");
        if(!txt_pass){return false;}
        return true;
    }
}
