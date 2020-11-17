package net.soradigital.suaraku;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import net.soradigital.suaraku.adapter.SpinnerAdapter;
import net.soradigital.suaraku.api.AuthService;
import net.soradigital.suaraku.api.CandidateService;
import net.soradigital.suaraku.api.RegionService;
import net.soradigital.suaraku.api.RetrofitClientInstance;
import net.soradigital.suaraku.classes.City;
import net.soradigital.suaraku.classes.Province;
import net.soradigital.suaraku.helper.ApiHelper;
import net.soradigital.suaraku.helper.CustomDialog;
import net.soradigital.suaraku.helper.RequestAdapter;
import net.soradigital.suaraku.helper.SessionManager;
import net.soradigital.suaraku.helper.ValidationHelper;
import net.soradigital.suaraku.model.Candidate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    Toolbar toolbar;
    Button btn_register;
    Spinner spn_provinsi,spn_kabupaten;
    ImageView image_kandidat;
    SpinnerAdapter adapter_provinsi,adapter_kabupaten;
    ArrayList<String> list_provinsi,list_kabupaten;
    ArrayList<Province> list_provinsi_map;
    ArrayList<City> list_kabupaten_map;
    EditText txt_username,txt_no_hp,txt_ref_id, txt_input_password;
    AppCompatCheckBox showPassword;
    int have_candidate=0;
    ApiHelper apiHelper;
    CustomDialog dialog;
    ValidationHelper validation;
    private int isCandidateAvailable=0;
    SessionManager sessionManager;
    String TAG = RegisterActivity.class.getSimpleName();
    RegionService regionService;
    CandidateService candidateService;
    AuthService authService;
    private String pem_code = "";
    AlertDialog.Builder dialogbuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        btn_register = (Button) findViewById(R.id.btn_regis);
        spn_provinsi = (Spinner) findViewById(R.id.spinner_province);
        spn_kabupaten = (Spinner) findViewById(R.id.spinner_city);
        image_kandidat = (ImageView) findViewById(R.id.image_candidate);
        txt_username = (EditText) findViewById(R.id.txt_input_username);
        txt_no_hp = (EditText) findViewById(R.id.txt_input_nohp);
        txt_ref_id = (EditText) findViewById(R.id.txt_input_rujukan);
        txt_input_password = (EditText) findViewById(R.id.txt_input_password);
        showPassword = (AppCompatCheckBox) findViewById(R.id.showPassword);
        validation = new ValidationHelper(this);
        sessionManager = new SessionManager(this);

        apiHelper = new ApiHelper();
        dialog = new CustomDialog(this);
        dialogbuilder = new AlertDialog.Builder(this);

        regionService = RetrofitClientInstance.getRetrofitInstance().create(RegionService.class);
        candidateService = RetrofitClientInstance.getRetrofitInstance().create(CandidateService.class);
        authService = RetrofitClientInstance.getRetrofitInstance().create(AuthService.class);

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
        setTitle("Pendaftaran");
        btn_register.setOnClickListener(act_register);
        load_spinner();

        TextWatcher usernameValidator = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String str = charSequence.toString();
                if(str.length() > 0 && str.contains(" "))
                {
                    String username = txt_username.getText().toString();
                    String fix_username = username.replace(" ","");
                    txt_username.setText(fix_username);
                    txt_username.setSelection(fix_username.length());
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        txt_username.addTextChangedListener(usernameValidator);

        showPassword.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (!isChecked) {
                // hide password
                txt_input_password.setTransformationMethod(PasswordTransformationMethod.getInstance());

            } else {
                // show password
                txt_input_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
        });
    }


    private void load_spinner(){
        list_provinsi = new ArrayList<>();
        list_kabupaten = new ArrayList<>();
        list_provinsi_map = new ArrayList<>();
        list_kabupaten_map = new ArrayList<>();

//        Initial Value
        list_provinsi.add("Pilih Provinsi");
        Province province = new Province();
        province.setPrv_kowil("");
        list_provinsi_map.add(province);
        list_kabupaten.add("Pilih Kabupaten/Kota");
        City city = new City();
        city.setKab_kowil("");
        list_kabupaten_map.add(city);

//        Inital adapter
        adapter_provinsi = new SpinnerAdapter(getApplicationContext(),list_provinsi);
        adapter_kabupaten = new SpinnerAdapter(getApplicationContext(),list_kabupaten);

//        set adapter to spinner
        spn_provinsi.setAdapter(adapter_provinsi);
        spn_kabupaten.setAdapter(adapter_kabupaten);

        spn_provinsi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Province province = list_provinsi_map.get(position);
                spn_kabupaten.setSelection(0);
                if(!province.getPrv_kowil().isEmpty()){
                    get_kabupaten_data(province.getPrv_kowil());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spn_kabupaten.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position>0){
                    City city = list_kabupaten_map.get(position);
                    if (!city.getKab_kowil().isEmpty()){
                        get_kandidat_data(city.getKab_kowil());
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        get Provinsi data
        get_provinsi_data();
    }


    private View.OnClickListener act_register = v -> {
        if(!validasi()){
            return;
        }else{
            do_regis();
        }
    };

//    method get provinsi
    public void get_provinsi_data(){
        dialog.showDialog();

        Call<HashMap<String, Object>> call = regionService.province();

        call.enqueue(new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
                Boolean success = Boolean.parseBoolean(response.body().get("success").toString());
                String message = response.body().get("message").toString();

                if (success) {
                    ArrayList<Map<String, String>> data = (ArrayList<Map<String, String>>) response.body().get("data");

                    for (int i= 0; i < data.size(); i++){
                        Map<String, String> obj_provinsi = data.get(i);
                        list_provinsi.add(obj_provinsi.get("prv_nama"));
                        Province province = new Province();
                        province.setPrv_idx("");
                        province.setPrv_dapil("");
                        province.setPrv_kowil(obj_provinsi.get("prv_kowil"));
                        province.setPrv_nama(obj_provinsi.get("prv_nama"));
                        list_provinsi_map.add(province);
                    }
                    adapter_provinsi.notifyDataSetChanged();
                }else{
                    dialog.createToast(message + success);
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

    public void get_kabupaten_data(String provinceCode){
        Call<HashMap<String, Object>> call = regionService.city(provinceCode);
        list_kabupaten_map.clear();
        list_kabupaten.clear();
        list_kabupaten.add("Pilih Kabupaten/Kota");
        City cit = new City();
        cit.setKab_kowil("");
        list_kabupaten_map.add(cit);
        call.enqueue(new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
                Boolean success = Boolean.parseBoolean(response.body().get("success").toString());
                String message = response.body().get("message").toString();
                if (success) {
                    ArrayList<Map<String, String>> data = (ArrayList<Map<String, String>>) response.body().get("data");
                    for (int i= 0; i < data.size(); i++){
                        Map<String, String> objKabupaten = data.get(i);
                        list_kabupaten.add(objKabupaten.get("kab_nama"));
                        City city = new City();
                        city.setKab_idx("");
                        city.setKab_dapil("");
                        city.setKab_kowil(objKabupaten.get("kab_kowil"));
                        city.setKab_nama(objKabupaten.get("kab_nama"));
                        list_kabupaten_map.add(city);
                    }
                    adapter_kabupaten.notifyDataSetChanged();
                }else{
                    dialog.createToast(message);
                }
                dialog.hideDialog();
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {

            }
        });
    }

    public void get_kandidat_data(String kabupaten_code){
        have_candidate = 0;
        Call<HashMap<String, Object>> call = candidateService.getCandidate(kabupaten_code);
        call.enqueue(new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
                Boolean success = Boolean.parseBoolean(response.body().get("success").toString());
                String message = response.body().get("message").toString();
                if (success) {
                    Map<String, Object> data = (Map<String, Object>) response.body().get("data");
                    Map<String, Object> image = (Map<String, Object>) data.get("images");
                    Gson gson = new Gson();
                    String json = gson.toJson(data);
                    Candidate candidate = gson.fromJson(json, Candidate.class);
                    Glide.with(RegisterActivity.this)
                            .load(apiHelper.newBaseUrl +  candidate.getImages().getFilename())
                            .placeholder(getResources().getDrawable(R.drawable.noavatar))
                            .error(getResources().getDrawable(R.drawable.noavatar))
                            .into(image_kandidat);
                    isCandidateAvailable = 1;
                    have_candidate = 1;
                    pem_code = data.get("PEM_CODE").toString();
                }else{
                    Glide.with(RegisterActivity.this)
                            .load(getResources().getDrawable(R.drawable.noavatar))
                            .into(image_kandidat);
                    dialog.createToast("Tidak Ada Kandidat");
                    isCandidateAvailable = 0;
                    have_candidate = 0;
                    pem_code = "";
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {
                Glide.with(RegisterActivity.this)
                        .load(getResources().getDrawable(R.drawable.noavatar))
                        .into(image_kandidat);
                dialog.createToast("Tidak Ada Kandidat");
                isCandidateAvailable = 0;
                have_candidate = 0;
            }
        });
    }

    public void do_regis(){
        dialog.showDialog();
        String username = txt_username.getText().toString();
        String no_hp = txt_no_hp.getText().toString();
        String ref_id = txt_ref_id.getText().toString();
        City city = list_kabupaten_map.get(spn_kabupaten.getSelectedItemPosition());
        String reg_code = city.getKab_kowil();
        String password = txt_input_password.getText().toString();

        Call<HashMap<String, Object>> call = authService.register(no_hp, ref_id, reg_code, pem_code, username, password, username);

        call.enqueue(new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
                Boolean success = Boolean.parseBoolean(response.body().get("success").toString());
                String message = response.body().get("message").toString();

                if (success){
                    dialogbuilder.setMessage("Registrasi Berhasil!\nSilahkan Lanjutkan Login.");
                    dialogbuilder.setCancelable(false);
                    dialogbuilder.setPositiveButton("OK", (dialog, which) -> {
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        finishAffinity();
                        finish();
                    });
                    AlertDialog alertDialog = dialogbuilder.create();
                    alertDialog.show();
                }else{
                    Map<String, String> data = (Map<String, String>) response.body().get("data");
                    String error = "";
                    for(Map.Entry<String, String> entry : data.entrySet()){
                        error += entry.getKey() + " : " + entry.getValue() + "\n";
                    }
                    validation.createDialog(error);
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

    public boolean validasi(){
        boolean no_hp = validation.isTextValid(txt_no_hp,"Nomoh Ponsel harus diisi.");
        boolean ref_id = validation.isTextValid(txt_ref_id,"ID Rujukan harus diisi.");
        boolean username = validation.isTextValid(txt_username,"Username harus diisi.");
        boolean pass = validation.isTextValid(txt_input_password,"Passowrd Harus diisi.");
        if(!(no_hp && ref_id && username && pass)) return  false;
        Province province = list_provinsi_map.get(spn_provinsi.getSelectedItemPosition());
        if (province.getPrv_kowil().isEmpty()){
            validation.createDialog("Provinsi Harus diisi");
            return false;
        }
        City city = list_kabupaten_map.get(spn_kabupaten.getSelectedItemPosition());
        if (city.getKab_kowil().isEmpty()){
            validation.createDialog("Kabupaten/Kota Harus diisi");
            return false;
        }
        if (isCandidateAvailable == 0){
            validation.createDialog("Dikota yang anda pilih tidak ada PEMILU.");
            return false;
        }
        return true;
    }
}
