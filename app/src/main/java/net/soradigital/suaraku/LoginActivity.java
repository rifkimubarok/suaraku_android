package net.soradigital.suaraku;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import net.soradigital.suaraku.helper.ApiHelper;
import net.soradigital.suaraku.helper.CustomDialog;
import net.soradigital.suaraku.helper.RequestAdapter;
import net.soradigital.suaraku.helper.SessionManager;
import net.soradigital.suaraku.helper.ValidationHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {

    TextView btn_register;
    Button btn_login;
    private ApiHelper apiHelper = new ApiHelper();
    private EditText txt_username;
    private EditText txt_password;
    private SessionManager sessionManager;
    private ValidationHelper validation;

    private String TAG = LoginActivity.class.getCanonicalName();

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

        String url = apiHelper.getUrl()+"&datatype=user&cmd=signin&username="+username+"&password="+password;
        url = url.replaceAll(" ", "%20");
        JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, url, null,
            response -> {
                if (response.has("user")){
                    try {
                        JSONArray user_arr = response.getJSONArray("user");
                        JSONObject user_obj = user_arr.getJSONObject(0);
                        sessionManager.set_session(user_obj.toString(),sessionManager.LOGIN_SESSION);
                        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                        startActivity(intent);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        finishAffinity();
                        finish();
                    }catch (Exception io){
                        Log.d(TAG, String.valueOf(io));
                    }
                }else{
                    try {
                        if (response.has("error")){
                            JSONArray error = response.getJSONArray("error");
                            JSONObject errObjt = error.getJSONObject(0);
                            Toast.makeText(getApplicationContext(),errObjt.getString("message"),Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getApplicationContext(),"Username / Password Salah",Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                dialog.hideDialog();
            },
            (error)->{
                dialog.hideDialog();
                Toast.makeText(getApplicationContext(),"Terjadi Kesalahan.",Toast.LENGTH_SHORT).show();
        });
        request1.setShouldCache(false);
        RequestAdapter.getInstance().addToRequestQueue(request1);
    }

    public boolean validation(){

        boolean txt_user = validation.isTextValid(txt_username,"Username Tidak boleh kosong");
        if (!txt_user){return false;}
        boolean txt_pass = validation.isTextValid(txt_password,"Password tidak boleh kosong");
        if(!txt_pass){return false;}
        return true;
    }
}
