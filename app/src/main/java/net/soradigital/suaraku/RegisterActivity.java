package net.soradigital.suaraku;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;

import net.soradigital.suaraku.adapter.SpinnerAdapter;
import net.soradigital.suaraku.classes.City;
import net.soradigital.suaraku.classes.Province;
import net.soradigital.suaraku.helper.ApiHelper;
import net.soradigital.suaraku.helper.CustomDialog;
import net.soradigital.suaraku.helper.RequestAdapter;
import net.soradigital.suaraku.helper.SessionManager;
import net.soradigital.suaraku.helper.ValidationHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {

    Toolbar toolbar;
    Button btn_register;
    Spinner spn_provinsi,spn_kabupaten;
    ImageView image_kandidat;
    SpinnerAdapter adapter_provinsi,adapter_kabupaten;
    ArrayList<String> list_provinsi,list_kabupaten;
    ArrayList<Province> list_provinsi_map;
    ArrayList<City> list_kabupaten_map;
    EditText txt_username,txt_no_hp,txt_ref_id;
    int have_candidate=0;
    ApiHelper apiHelper;
    CustomDialog dialog;
    ValidationHelper validation;
    private int isCandidateAvailable=0;
    SessionManager sessionManager;
    String TAG = RegisterActivity.class.getSimpleName();

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
        validation = new ValidationHelper(this);
        sessionManager = new SessionManager(this);

        apiHelper = new ApiHelper();
        dialog = new CustomDialog(this);

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
        spn_provinsi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Province province = list_provinsi_map.get(position);
                get_kabupaten_data(province.getPrv_kowil());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spn_kabupaten.setAdapter(adapter_kabupaten);
        spn_kabupaten.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position>=0){
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
//        Intent intent = new Intent(getApplicationContext(),VerifikasiActivity.class);
//        startActivity(intent);
    };

//    method get provinsi
    public void get_provinsi_data(){
        dialog.showDialog();
        String url = apiHelper.getUrl()+"&datatype=region&region=province&cmd=getlist";
        url = url.replaceAll(" ", "%20");
        JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
            dialog.hideDialog();
//            check if result have province
                    if (response.has("province")){
//                        list_provinsi.clear();
                        try {
//                            push data to adapter
                            JSONArray provinsi = response.getJSONArray("province");
                            for (int i=0;i<provinsi.length();i++){
                                JSONObject obj_provinsi = provinsi.getJSONObject(i);
                                list_provinsi.add(obj_provinsi.getString("prv_nama"));
                                Province province = new Province();
                                province.setPrv_idx(obj_provinsi.getString("prv_idx"));
                                province.setPrv_dapil(obj_provinsi.getString("prv_dapil"));
                                province.setPrv_kowil(obj_provinsi.getString("prv_kowil"));
                                province.setPrv_nama(obj_provinsi.getString("prv_nama"));
                                list_provinsi_map.add(province);
                            }
                            adapter_provinsi.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                (error)->{
                    dialog.hideDialog();
                    Toast.makeText(getApplicationContext(),"Terjadi Kesalahan.",Toast.LENGTH_SHORT).show();
                });
        request1.setShouldCache(false);
        RequestAdapter.getInstance().addToRequestQueue(request1);
    }

    public void get_kabupaten_data(String provinsi_kode){
        String url = apiHelper.getUrl()+"&datatype=region&region=city&cmd=getlist&regcode="+provinsi_kode;
        url = url.replaceAll(" ", "%20");
        JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    if (response.has("city")){
                        list_kabupaten_map.clear();
                        list_kabupaten.clear();
                        list_kabupaten.add("Pilih Kabupaten/Kota");
                        City cit = new City();
                        cit.setKab_kowil("");
                        list_kabupaten_map.add(cit);
                        try {
                            JSONArray kabupaten = response.getJSONArray("city");
                            for (int i=0;i<kabupaten.length();i++){
                                JSONObject obj_kabupaten = kabupaten.getJSONObject(i);
                                list_kabupaten.add(obj_kabupaten.getString("kab_nama"));
                                City city = new City();
                                city.setKab_idx(obj_kabupaten.getString("kab_idx"));
                                city.setKab_dapil(obj_kabupaten.getString("kab_dapil"));
                                city.setKab_kowil(obj_kabupaten.getString("kab_kowil"));
                                city.setKab_nama(obj_kabupaten.getString("kab_nama"));
                                list_kabupaten_map.add(city);
                            }
                            adapter_kabupaten.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                (error)->{
                    Toast.makeText(getApplicationContext(),"Terjadi Kesalahan.",Toast.LENGTH_SHORT).show();
                });
        request1.setShouldCache(false);
        RequestAdapter.getInstance().addToRequestQueue(request1);
    }

    public void get_kandidat_data(String kabupaten_code){
        have_candidate = 0;
        String url_image = apiHelper.getBase_url()+"images/calon/";
        String url = apiHelper.getUrl()+"&datatype=qcount&cmd=getcandidate&regcode="+kabupaten_code;
        url = url.replaceAll(" ", "%20");
        JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    if (response.has("candidate")){
                        try {
                            JSONArray kandidat_array = response.getJSONArray("candidate");
                            if (kandidat_array.length()>0){
                                have_candidate = 1;
                                JSONObject kandidat_obj = kandidat_array.getJSONObject(0);
                                isCandidateAvailable = 1;
                                Glide.with(this)
                                        .load(url_image+kandidat_obj.getString("CAN_IMAGE"))
                                        .placeholder(getResources().getDrawable(R.drawable.noavatar))
                                        .error(getResources().getDrawable(R.drawable.noavatar))
                                        .into(image_kandidat);
                            }
                        } catch (Exception e) {
                            isCandidateAvailable = 0;
                            e.printStackTrace();
                        }
                    }else if (response.has("error")){
                        try {
                            isCandidateAvailable = 0;
                            JSONArray error = response.getJSONArray("error");
                            JSONObject error_obj = error.getJSONObject(0);
                            Toast.makeText(getApplicationContext(),error_obj.getString("message"),Toast.LENGTH_LONG).show();
                            Glide.with(this)
                                    .load(getResources().getDrawable(R.drawable.noavatar))
                                    .into(image_kandidat);
                        } catch (JSONException e) {
                            isCandidateAvailable = 0;
                            e.printStackTrace();
                        }

                    }
                },
                (error)->{
                    isCandidateAvailable = 0;
                    Toast.makeText(getApplicationContext(),"Terjadi Kesalahan.",Toast.LENGTH_SHORT).show();
                });
        request1.setShouldCache(false);
        RequestAdapter.getInstance().addToRequestQueue(request1);
    }

    public void do_regis(){
        dialog.showDialog();
        String username = txt_username.getText().toString();
        String no_hp = txt_no_hp.getText().toString();
        String ref_id = txt_ref_id.getText().toString();
        City city = list_kabupaten_map.get(spn_kabupaten.getSelectedItemPosition());
        String reg_code = city.getKab_kowil();

        String url = apiHelper.getUrl();
        url += "&datatype=user&cmd=signup&regcode="+reg_code+"&phone="+no_hp+"&refId="+ref_id+"&username="+username;
        url = url.replaceAll(" ", "%20");
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,url,null,response -> {
            Log.d(TAG,response.toString());
            try{
               if (response.has("user")){
                    JSONObject object_data = response.getJSONObject("user");
                    sessionManager.set_session(object_data.toString(),sessionManager.SIGNUP_SESSION);
                    dialog.hideDialog();
                   Intent intent = new Intent(RegisterActivity.this,UpdatePasswordActivity.class);
                   startActivity(intent);
               }else{
                   if (response.has("error")){
                       JSONArray arr_error = response.getJSONArray("error");
                       JSONObject obj_error = arr_error.getJSONObject(0);
                       dialog.hideDialog();
                       validation.createDialog(obj_error.getString("message"));
                   }
               }
           }catch (Exception e){
               dialog.hideDialog();
               e.printStackTrace();
           }
        },error -> {
            dialog.hideDialog();
            error.printStackTrace();
        });
        request.setShouldCache(false);
        RequestAdapter.getInstance().addToRequestQueue(request);
    }

    public boolean validasi(){
        boolean username = validation.isTextValid(txt_username,"Username harus diisi.");
        if(!username)return false;
        boolean no_hp = validation.isTextValid(txt_no_hp,"Nomoh Ponsel harus diisi.");
        if(!no_hp)return false;
        boolean ref_id = validation.isTextValid(txt_ref_id,"ID Rujukan harus diisi.");
        if(!ref_id)return false;
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
