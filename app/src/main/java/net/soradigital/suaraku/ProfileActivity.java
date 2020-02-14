package net.soradigital.suaraku;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import net.soradigital.suaraku.adapter.SpinnerAdapter;
import net.soradigital.suaraku.classes.District;
import net.soradigital.suaraku.classes.Organisasi;
import net.soradigital.suaraku.classes.Village;
import net.soradigital.suaraku.helper.ApiHelper;
import net.soradigital.suaraku.helper.CustomDialog;
import net.soradigital.suaraku.helper.RequestAdapter;
import net.soradigital.suaraku.helper.SessionManager;
import net.soradigital.suaraku.helper.ValidationHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText txt_no_induk,txt_input_fullname,txt_input_tmplahir,txt_input_tgllahir,
            txt_input_alamat,txt_rt,txt_rw;
    TextView txt_judul;
    RadioButton jk_laki,jk_wanita;
    Spinner spinner_kecamatan,spinner_desa,spinner_marige,spinner_jenis_org,spinner_pilih_org,spinner_pilih_relawan;
    ApiHelper apiHelper = new ApiHelper();
    JSONObject user_session;
    SessionManager sessionManager;
    CustomDialog dialog;
    DatePickerDialog datePickerDialog;
    ArrayList<String> status_nikah_arr,list_kecamatan_data,list_desa_data,jenis_org,list_organisasi_data,list_relawan_data;
    ArrayList<HashMap> status_nikah_map;
    ArrayList<District> list_kecamatan_map;
    ArrayList<Village> list_desa_map;
    ArrayList<Organisasi> list_organisasi_map,list_relawan_map;
    SpinnerAdapter adapter_status_nikah,adapter_kecamatan,adapter_desa,adapter_jenis_org;
    HashMap<String,String> user_sesi;
    JSONObject sesi_obj;
    Button btn_save_profile;
    int request_desa = 0;
    int request_org = 0;
    ValidationHelper validation;
    ArrayAdapter<String> adapter_organisasi,adapter_relawan;
    String reg_code ="";
    String acc_noreg = "";


    String TAG = ProfileActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

//        Initial Object
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        btn_save_profile = (Button) findViewById(R.id.btn_save_profile);
        txt_no_induk = (EditText) findViewById(R.id.txt_no_induk) ;
        txt_input_fullname = (EditText) findViewById(R.id.txt_input_fullname) ;
        txt_input_tmplahir = (EditText) findViewById(R.id.txt_input_tmplahir) ;
        txt_input_tgllahir = (EditText) findViewById(R.id.txt_input_tgllahir) ;
        txt_input_alamat = (EditText) findViewById(R.id.txt_input_alamat) ;
        txt_rt = (EditText) findViewById(R.id.txt_rt) ;
        txt_rw = (EditText) findViewById(R.id.txt_rw) ;
        jk_laki = (RadioButton) findViewById(R.id.jk_laki);
        jk_wanita = (RadioButton) findViewById(R.id.jk_wanita);
        spinner_kecamatan = (Spinner) findViewById(R.id.spinner_kecamatan);
        spinner_desa = (Spinner) findViewById(R.id.spinner_desa);
        spinner_marige = (Spinner) findViewById(R.id.spinner_marige);
        spinner_jenis_org = (Spinner)findViewById(R.id.spinner_jenis_org);
        spinner_pilih_org = (Spinner) findViewById(R.id.spinner_pilih_org);
        spinner_pilih_relawan = (Spinner) findViewById(R.id.spinner_pilih_relawan);
        txt_judul = (TextView) findViewById(R.id.txt_judul);
        dialog = new CustomDialog(this);
        list_kecamatan_data = new ArrayList<>();
        list_kecamatan_map = new ArrayList<>();
        list_desa_data = new ArrayList<>();
        list_desa_map = new ArrayList<>();
        list_organisasi_map = new ArrayList<>();
        list_organisasi_data = new ArrayList<>();
        list_relawan_data = new ArrayList<>();
        list_relawan_map = new ArrayList<>();
        sessionManager = new SessionManager(getApplicationContext());
        validation = new ValidationHelper(this);
        jenis_org = new ArrayList<>();
        status_nikah_arr = new ArrayList<>();
        status_nikah_map = new ArrayList<>();
        status_nikah_arr.add("Belum menikah");
        status_nikah_arr.add("Sudah menikah");
        status_nikah_arr.add("Cerai hidup");
        status_nikah_arr.add("Cerai mati");

//        jenis_org.add("Tidak Ada");
//        jenis_org.add("Organisasi");
//        jenis_org.add("Wadah Relawan");

        list_organisasi_data.add("Tidak Ada");
        Organisasi org = new Organisasi();
        org.setINDEX(0);
        list_organisasi_map.add(org);

        adapter_status_nikah = new SpinnerAdapter(getApplicationContext(),status_nikah_arr);
        spinner_marige.setAdapter(adapter_status_nikah);

        adapter_kecamatan = new SpinnerAdapter(getApplicationContext(),list_kecamatan_data);
        spinner_kecamatan.setAdapter(adapter_kecamatan);

        adapter_desa = new SpinnerAdapter(getApplicationContext(),list_desa_data);
        spinner_desa.setAdapter(adapter_desa);

//        adapter_jenis_org = new SpinnerAdapter(getApplicationContext(),jenis_org);
//        spinner_jenis_org.setAdapter(adapter_jenis_org);

        adapter_organisasi = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,list_organisasi_data);
        spinner_pilih_org.setAdapter(adapter_organisasi);

        adapter_relawan = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,list_relawan_data);
        spinner_pilih_relawan.setAdapter(adapter_relawan);

        spinner_kecamatan.setOnItemSelectedListener(item_kecamatan_selected);
//        spinner_jenis_org.setOnItemSelectedListener(item_type_org_selected);
//        initial top bar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back_black));
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimaryDark));
        setTitle("Profile");

//        get Session
        if (getIntent().getExtras() != null){
            try{
                Bundle bundle = getIntent().getExtras();
                reg_code = bundle.getString("ACC_KOWIL");
                acc_noreg = bundle.getString("ACC_NOREG");
                disable_input();
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            HashMap<String,String> sesi = sessionManager.get_session(sessionManager.LOGIN_SESSION);
            try {
                user_session = new JSONObject(sesi.get("data"));
                reg_code = user_session.getString("ACC_KOWIL");
                acc_noreg = user_session.getString("ACC_NOREG");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        user_sesi = sessionManager.get_session(sessionManager.LOGIN_SESSION);
        try {
            sesi_obj = new JSONObject(user_sesi.get("data"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        load_profile();
        prepared_statement();
        btn_save_profile.setOnClickListener(v->{
            saveProfile();
        });
    }

    AdapterView.OnItemSelectedListener item_kecamatan_selected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            District district = list_kecamatan_map.get(position);
            request_desa++;
            if (request_desa>1){
                get_desa_data(district.getKec_kowil(),"");
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

//    AdapterView.OnItemSelectedListener item_type_org_selected = new AdapterView.OnItemSelectedListener() {
//        @Override
//        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//            String type = jenis_org.get(i);
//            request_org++;
//            Log.d("PosisiORG",String.valueOf(request_org));
//            if (request_org >2){
//                get_organisasi(type,0);
//            }
//        }
//
//        @Override
//        public void onNothingSelected(AdapterView<?> adapterView) {
//
//        }
//    };

    public void disable_input(){
        txt_no_induk.setInputType(0);
        txt_input_fullname.setInputType(0);
        txt_input_tmplahir.setInputType(0);
        txt_input_tgllahir.setInputType(0);
        txt_input_alamat.setInputType(0);
        txt_rt.setInputType(0);
        txt_rw.setInputType(0);
        jk_laki.setInputType(0);
        jk_wanita.setInputType(0);
        spinner_kecamatan.setEnabled(false);
        spinner_desa.setEnabled(false);
        spinner_marige.setEnabled(false);
        spinner_jenis_org.setEnabled(false);
        spinner_pilih_org.setEnabled(false);
        btn_save_profile.setVisibility(View.GONE);
        setTitle("Profile Pendukung");
        txt_judul.setVisibility(View.GONE);
    }

    public void prepared_statement(){

        Calendar calendar = Calendar.getInstance();
        txt_input_tgllahir.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus){
                String tgl = txt_input_tgllahir.getText().toString();
                if (!tgl.isEmpty()){
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date date = dateFormat.parse(tgl);
                        calendar.setTime(date);
                        Log.d(TAG,date.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    Date date = new Date();
                    calendar.setTime(date);
                }
                datePickerDialog = new DatePickerDialog(this,
                        (datePicker, year, month, day) -> {
                            month++;
                            String month2 = "0";
                            String day2 = "0";
                            String pattern = "00";
                            month2 = pattern.substring(String.valueOf(month).length())+month;
                            day2 = pattern.substring(String.valueOf(day).length())+day;

                            txt_input_tgllahir.setText(year+"-"+month2+"-"+day2);
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
                datePickerDialog.show();
            }
        });


        String[] status = {"B","S","H","M"};
        HashMap<String,Integer> stat = new HashMap<>();
        HashMap<Integer,String> stat2 = new HashMap<>();
        for(int i=0;i<status.length;i++){
            stat.put(status[i],i);
            stat2.put(i,status[i]);
        }

        status_nikah_map.add(stat);
        status_nikah_map.add(stat2);
    }


    public void load_profile(){
        try {

            String url = apiHelper.getUrl()+"&datatype=profile&cmd=getprofile&regcode="+reg_code+"&acc_noreg="+acc_noreg;
            dialog.showDialog();
            url = url.replaceAll(" ", "%20");
            JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        try{
                            dialog.hideDialog();
//            check if result have province
                            if (response.has("profile")){
//                            push data to Edit Text
                                if (response.has("profile")){
                                    JSONArray profile = response.getJSONArray("profile");
                                    JSONObject profile_obj = profile.getJSONObject(0);
                                    txt_no_induk.setText(profile_obj.getString("PEM_NIK"));
                                    txt_input_fullname.setText(profile_obj.getString("PEM_NAMA"));
                                    txt_input_tmplahir.setText(profile_obj.getString("PEM_LAHIRTMP"));
                                    txt_input_tgllahir.setText(profile_obj.getString("PEM_LAHIRTGL"));
                                    String jenis_kelamin = profile_obj.getString("PEM_KELAMIN");
                                    if (jenis_kelamin.equals("L"))jk_laki.setChecked(true);
                                    else jk_wanita.setChecked(true);
                                    Log.d(TAG,"Jenis Kelamin "+jenis_kelamin);
                                    String statusnikah = profile_obj.getString("PEM_STATUSNIKAH");
                                    if (!statusnikah.isEmpty()){
                                        HashMap<String,Integer> status_map = status_nikah_map.get(0);
                                        spinner_marige.setSelection(status_map.get(statusnikah));
                                    }
                                    txt_input_alamat.setText(profile_obj.getString("PEM_ALAMAT"));
                                    txt_rt.setText(profile_obj.getString("PEM_RT"));
                                    txt_rw.setText(profile_obj.getString("PEM_RW"));
                                    get_kecamatan_data(reg_code,profile_obj.getString("DIS_CODE"));
                                    get_desa_data(profile_obj.getString("DIS_CODE"),profile_obj.getString("VIL_CODE"));
                                    String type_org = profile_obj.getString("PEM_ORG_TYPE");
                                    spinner_jenis_org.setSelection(jenis_org.indexOf(type_org));
                                    int pem_org = profile_obj.getInt("PEM_ORG");
                                    get_organisasi(type_org,pem_org);
                                    get_relawan("",pem_org);
                                }else{
                                    Log.d(TAG,sesi_obj.getString("ACC_KOWIL"));
                                    get_kecamatan_data(sesi_obj.getString("ACC_KOWIL"),"");
                                    request_desa++;
                                }
                            }else{
                                Log.d(TAG,sesi_obj.getString("ACC_KOWIL"));
                                get_kecamatan_data(sesi_obj.getString("ACC_KOWIL"),"");
                                request_desa++;
                                get_relawan("",0);
                                get_organisasi("",0);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    },
                    (error)->{
                        dialog.hideDialog();
                        Toast.makeText(getApplicationContext(),"Terjadi Kesalahan.",Toast.LENGTH_SHORT).show();
                        get_relawan("",0);
                        get_organisasi("",0);
                        try {
                            get_kecamatan_data(sesi_obj.getString("ACC_KOWIL"),"");
                            request_desa++;
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    });
            request1.setShouldCache(false);
            RequestAdapter.getInstance().addToRequestQueue(request1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void get_kecamatan_data(String regcode,String current_regcode){
        String url = apiHelper.getUrl()+"&datatype=region&region=district&cmd=getlist&regcode="+regcode;
        url = url.replaceAll(" ", "%20");
        JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    if (response.has("district")){
                        list_kecamatan_map.clear();
                        list_kecamatan_data.clear();
                        list_kecamatan_data.add("Pilih Kecamatan");
                        District dist = new District();
                        dist.setKec_kowil("");
                        list_kecamatan_map.add(dist);
                        try {
                            JSONArray kecamatan = response.getJSONArray("district");
                            int position=0;
                            for (int i=0;i<kecamatan.length();i++){
                                JSONObject obj_kecamatan = kecamatan.getJSONObject(i);
                                if (obj_kecamatan.getString("kec_kowil").equals(current_regcode)) position = i+1;
                                list_kecamatan_data.add(obj_kecamatan.getString("kec_nama"));
                                District district = new District();
                                district.setKec_idx(obj_kecamatan.getString("kec_idx"));
                                district.setKec_dapil(obj_kecamatan.getString("kec_dapil"));
                                district.setKec_kowil(obj_kecamatan.getString("kec_kowil"));
                                district.setKec_nama(obj_kecamatan.getString("kec_nama"));
                                list_kecamatan_map.add(district);
                            }
                            adapter_kecamatan.notifyDataSetChanged();
                            spinner_kecamatan.setSelection(position);
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

    public void get_desa_data(String regcode,String current_regcode){
        Log.d(TAG,"ini regionalnya "+regcode+"  "+current_regcode);
        String url = apiHelper.getUrl()+"&datatype=region&region=village&cmd=getlist&regcode="+regcode;
        url = url.replaceAll(" ", "%20");
        JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {

                    if (response.has("village")){
                        list_desa_map.clear();
                        list_desa_data.clear();
                        list_desa_data.add("Pilih Desa/Kelurahan");
                        Village vill = new Village();
                        vill.setKel_kowil("");
                        list_desa_map.add(vill);
                        try {
                            JSONArray kecamatan = response.getJSONArray("village");
                            int position=0;
                            for (int i=0;i<kecamatan.length();i++){
                                JSONObject obj_kelurahan= kecamatan.getJSONObject(i);
                                if (obj_kelurahan.getString("kel_kowil").equals(current_regcode)) position = i+11;
                                list_desa_data.add(obj_kelurahan.getString("kel_nama"));
                                Village village = new Village();
                                village.setKel_idx(obj_kelurahan.getString("kel_idx"));
                                village.setKel_dapil(obj_kelurahan.getString("kel_dapil"));
                                village.setKel_kowil(obj_kelurahan.getString("kel_kowil"));
                                village.setKel_nama(obj_kelurahan.getString("kel_nama"));
                                list_desa_map.add(village);
                            }
                            adapter_desa.notifyDataSetChanged();
                            spinner_desa.setSelection(position);
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

    public void saveProfile(){
        dialog.showDialog();
        HashMap<Integer,String> statusnikah = status_nikah_map.get(1);
        try{
            String url = apiHelper.getUrl();
            url+= "&datatype=profile&cmd=saveprofile&regcode="+sesi_obj.getString("ACC_KOWIL");
            url = url.replaceAll(" ", "%20");
            String nik = txt_no_induk.getText().toString();
            String acc_noreg = sesi_obj.getString("ACC_NOREG");
            String nama = txt_input_fullname.getText().toString();
            String tmp_lahir = txt_input_tmplahir.getText().toString();
            String tgl_lahir = txt_input_tgllahir.getText().toString();
            String alamat = txt_input_alamat.getText().toString();
            String rt = txt_rt.getText().toString();
            String rw = txt_rw.getText().toString();
            String jk = jk_laki.isChecked() ? "L" : "P";
            String status_nikah = statusnikah.get(spinner_marige.getSelectedItemPosition());
            District district = list_kecamatan_map.get(spinner_kecamatan.getSelectedItemPosition());
            String kecamatan = district.getKec_kowil();
            Village village = list_desa_map.get(spinner_desa.getSelectedItemPosition());
            String desa = village.getKel_kowil();
            String kota = sesi_obj.getString("ACC_KOWIL");
//            String type_org = jenis_org.get(spinner_jenis_org.getSelectedItemPosition());
            Organisasi org = list_organisasi_map.get(spinner_pilih_org.getSelectedItemPosition());
            Organisasi relawan = list_relawan_map.get(spinner_pilih_org.getSelectedItemPosition());
            int selected_org = org.getINDEX();
            int selected_relawan = relawan.getINDEX();

            StringRequest request = new StringRequest(Request.Method.POST,url,response -> {
                Log.d(TAG,response);
                dialog.hideDialog();
                try {
                    JSONObject obj = new JSONObject(response);
                    JSONArray arr = obj.getJSONArray("error");
                    JSONObject obj_err = arr.getJSONObject(0);
                    validation.createDialog(obj_err.getString("message"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            },error -> {
                dialog.hideDialog();
                error.printStackTrace();
            }){
                @Override
                protected Map<String,String> getParams(){
                    HashMap<String,String> param = new HashMap<>();
                    param.put("nik",nik);
                    param.put("nama",nama);
                    param.put("acc_noreg",acc_noreg);
                    param.put("tmp_lahir",tmp_lahir);
                    param.put("tgl_lahir",tgl_lahir);
                    param.put("alamat",alamat);
                    param.put("rt",rt);
                    param.put("rw",rw);
                    param.put("jk",jk);
                    param.put("status_nikah",status_nikah);
                    param.put("kecamatan",kecamatan);
                    param.put("kelurahan",desa);
                    param.put("kota",kota);
                    param.put("type_org","");
                    param.put("pem_org",String.valueOf(selected_org));
                    param.put("pem_rel",String.valueOf(selected_relawan));
                    return param;
                }
            };

            request.setShouldCache(false);
            RequestAdapter.getInstance().addToRequestQueue(request);

        }catch (Exception e){
                e.printStackTrace();
        }

    }

    public void get_organisasi(String type,int current_organisasi){
//        if (type.equals("Tidak Ada")){
//            list_organisasi_map.clear();
//            list_organisasi_data.clear();
//            Organisasi org = new Organisasi();
//            org.setINDEX(0);
//            list_organisasi_map.add(org);
//            list_organisasi_data.add("Tidak Ada");
//            adapter_organisasi.notifyDataSetChanged();
//        }
//        String url = apiHelper.getUrl()+"&datatype=org&cmd="+type;
        String url = apiHelper.getUrl()+"&datatype=org&cmd=Organisasi";
        url = url.replaceAll(" ", "%20");

        list_organisasi_data.clear();
        list_organisasi_map.clear();
        list_organisasi_data.add("-");
        Organisasi org1 = new Organisasi();
        org1.setINDEX(0);
        org1.setORG_NAME("-");
        list_organisasi_map.add(org1);

        JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d(TAG,response.toString());
                    if (response.has("data")){
                        try {
                            JSONArray organisasi = response.getJSONArray("data");
                            int position=0;
                            for (int i=0;i<organisasi.length();i++){
                                JSONObject obj_organisasi = organisasi.getJSONObject(i);
                                if (obj_organisasi.getInt("INDEX") == current_organisasi) {
                                    position = i;
                                    Log.d("PosisiORG",String.valueOf(position));
                                }
                                Organisasi org = new Organisasi();
                                org.setINDEX(obj_organisasi.getInt("INDEX"));
                                org.setORG_NAME(obj_organisasi.getString("ORG_NAME"));
                                org.setORG_TYPE(obj_organisasi.getString("ORG_TYPE"));
                                list_organisasi_map.add(org);
                                list_organisasi_data.add(obj_organisasi.getString("ORG_NAME"));
                            }
                            adapter_organisasi.notifyDataSetChanged();
                            spinner_pilih_org.setSelection(position);
                            Log.d("PosisiORG",String.valueOf(position));
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

    public void get_relawan(String type,int current_organisasi){
//        if (type.equals("Tidak Ada")){
//            list_organisasi_map.clear();
//            list_organisasi_data.clear();
//            Organisasi org = new Organisasi();
//            org.setINDEX(0);
//            list_organisasi_map.add(org);
//            list_organisasi_data.add("Tidak Ada");
//            adapter_organisasi.notifyDataSetChanged();
//        }
//        String url = apiHelper.getUrl()+"&datatype=org&cmd="+type;
        String url = apiHelper.getUrl()+"&datatype=org&cmd=Wadah Relawan";
        url = url.replaceAll(" ", "%20");


        list_relawan_map.clear();
        list_relawan_data.clear();
        list_relawan_data.add("-");
        Organisasi org1 = new Organisasi();
        org1.setINDEX(0);
        org1.setORG_NAME("-");
        list_relawan_map.add(org1);

        JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d(TAG,response.toString());
                    if (response.has("data")){
                        try {
                            JSONArray organisasi = response.getJSONArray("data");
                            int position=0;
                            for (int i=0;i<organisasi.length();i++){
                                JSONObject obj_organisasi = organisasi.getJSONObject(i);
                                if (obj_organisasi.getInt("INDEX") == current_organisasi) {
                                    position = i;
                                    Log.d("PosisiORG",String.valueOf(position));
                                }
                                Organisasi org = new Organisasi();
                                org.setINDEX(obj_organisasi.getInt("INDEX"));
                                org.setORG_NAME(obj_organisasi.getString("ORG_NAME"));
                                org.setORG_TYPE(obj_organisasi.getString("ORG_TYPE"));
                                list_relawan_map.add(org);
                                list_relawan_data.add(obj_organisasi.getString("ORG_NAME"));
                            }
                            adapter_relawan.notifyDataSetChanged();
                            spinner_pilih_relawan.setSelection(position);
                            Log.d("PosisiORG",String.valueOf(position));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                (error)->{
                    Log.d(TAG,error.toString());
                    Toast.makeText(getApplicationContext(),"Terjadi Kesalahan.",Toast.LENGTH_SHORT).show();
                });
        request1.setShouldCache(false);
        RequestAdapter.getInstance().addToRequestQueue(request1);
    }
}
