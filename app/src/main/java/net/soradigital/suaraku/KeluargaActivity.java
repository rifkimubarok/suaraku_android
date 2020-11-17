package net.soradigital.suaraku;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.soradigital.suaraku.adapter.ListPersonAdapter;
import net.soradigital.suaraku.api.RetrofitClientInstance;
import net.soradigital.suaraku.api.SupporterService;
import net.soradigital.suaraku.classes.Person;
import net.soradigital.suaraku.helper.ApiHelper;
import net.soradigital.suaraku.helper.CustomDialog;
import net.soradigital.suaraku.helper.RequestAdapter;
import net.soradigital.suaraku.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KeluargaActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<Person> personArrayList;
    Toolbar toolbar;
    ListPersonAdapter listPersonAdapter;
    SessionManager sessionManager;
    JSONObject session;
    ApiHelper apiHelper;
    CustomDialog dialog;
    FloatingActionButton btn_add;
    String TAG = KeluargaActivity.class.getCanonicalName();
    String PEM_PARENT = "";
    SupporterService supporterService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keluarga);

        recyclerView = (RecyclerView) findViewById(R.id.rv_keluarga);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        btn_add = (FloatingActionButton) findViewById(R.id.btn_add_family);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back_black));
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimaryDark));

        setTitle("Keluarga & Relasi");

        recyclerView.setHasFixedSize(true);
        supporterService = RetrofitClientInstance.getRetrofitInstance().create(SupporterService.class);

        personArrayList = new ArrayList<>();
        sessionManager = new SessionManager(this);
        apiHelper = new ApiHelper();
        dialog = new CustomDialog(this);
        listPersonAdapter = new ListPersonAdapter(personArrayList,this);


        prepare_activity();

    }

    public void onRestart(){
        load_data_family();
        super.onRestart();
    }

    public void prepare_activity(){
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listPersonAdapter);

        HashMap<String,String> data = sessionManager.get_session(sessionManager.LOGIN_SESSION);
        try{
            session = new JSONObject(data.get("data"));
        }catch (Exception e){
            e.printStackTrace();
        }

        load_data_family();
        btn_add.setOnClickListener(v->{
            Intent intent = new Intent(KeluargaActivity.this,KeluargaWebActivity.class);
            intent.putExtra("PEM_PARENT",PEM_PARENT);
            intent.putExtra("title","");
            intent.putExtra("noreg","");
            intent.putExtra("statusSave",0);
            startActivity(intent);
        });
    }

    public void openDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Informasi");
        builder.setMessage("Anda baru bisa menambahkan Keluarga & Relasi setelah melengkapi Profile.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onBackPressed();
            }
        });
        AlertDialog dialogconfirm = builder.create();
        dialogconfirm.show();
    }

    public void load_data_family(){
        if (personArrayList.size()>0){
            personArrayList.clear();
        }
        dialog.showDialog();
        String token = sessionManager.getSessionString("token");
        Call<HashMap<String, Object>> call = supporterService.family(token);
        call.enqueue(new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
                String message = response.body().get("message").toString();
                Boolean success = Boolean.parseBoolean(response.body().get("success").toString());
                dialog.hideDialog();
                if(success){
                    Map<String, Object> data = (Map<String, Object>) response.body().get("data");
                    Map<String, Object> profile = (Map<String, Object>) data.get("profile");
                    if(profile.containsKey("PEM_INDEX")){
                        PEM_PARENT = profile.get("PEM_INDEX").toString();
                        ArrayList<Map<String, Object>> families = (ArrayList<Map<String, Object>>) data.get("family");
                        for (int i=0;i<families.size();i++){
                            Map<String, Object> family = families.get(i);
                            Person person = new Person();
                            person.setNama(family.get("PEM_NAMA").toString());
                            Double pemIndex = Double.parseDouble(family.get("PEM_INDEX").toString());
                            int pemIdx = pemIndex.intValue();
                            person.setPem_index(pemIdx);
                            person.setAcc_noreg(family.get("PEM_NOREG").toString());
                            person.setNik(family.get("PEM_NIK").toString());
                            person.setTmp_lahir("");
                            person.setTgl_lahir("");
                            person.setJk("");
                            person.setStatus_nikah("");
                            person.setAlamat("");
                            person.setKecamatan("");
                            person.setDesa("");
                            person.setRt("");
                            person.setRw("");
                            Double pemParent = Double.parseDouble(PEM_PARENT);
                            person.setPem_parrent(pemParent.intValue());
                            personArrayList.add(person);
                        }
                        refresh_adapter();
                    }else{
                        openDialog();
                    }
                }else{
                    Toast.makeText(KeluargaActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {
                dialog.hideDialog();
            }
        });

//        try {
//            String ACC_NOREG = session.getString("ACC_NOREG");
//            String ACC_KOWIL = session.getString("ACC_KOWIL");
//            String url = apiHelper.getUrl()+"&datatype=profile&cmd=getfamily&PEM_NOREG="+ACC_NOREG+"&regcode="+ACC_KOWIL;
//            url = url.replaceAll(" ", "%20");
//            Log.d(TAG,url);
//            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,url,null,response->{
//                try{
//                    dialog.hideDialog();
//                    if(response.has("PEM_INDEX")){
//                        PEM_PARENT = response.getString("PEM_INDEX");
//                    }else{
//                        openDialog();
//                    }
//                    if (response.has("data")){
//                        if(response.has("PEM_INDEX")){
//                            PEM_PARENT = response.getString("PEM_INDEX");
//                        }
//                        JSONArray arr_data = response.getJSONArray("data");
//                        for (int i=0;i<arr_data.length();i++){
//                            JSONObject obj_data = arr_data.getJSONObject(i);
//                            Person person = new Person();
//                            person.setNama(obj_data.getString("PEM_NAMA"));
//                            person.setPem_index(obj_data.getInt("PEM_INDEX"));
//                            person.setAcc_noreg(obj_data.getString("PEM_NOREG"));
//                            person.setNik(obj_data.getString("PEM_NIK"));
//                            person.setTmp_lahir(obj_data.getString("PEM_LAHIRTMP"));
//                            person.setTgl_lahir(obj_data.getString("PEM_LAHIRTGL"));
//                            person.setJk(obj_data.getString("PEM_KELAMIN"));
//                            person.setStatus_nikah(obj_data.getString("PEM_STATUSNIKAH"));
//                            person.setAlamat(obj_data.getString("PEM_ALAMAT"));
//                            person.setKecamatan(obj_data.getString("DIS_CODE"));
//                            person.setDesa(obj_data.getString("VIL_CODE"));
//                            person.setRt(obj_data.getString("PEM_RT"));
//                            person.setRw(obj_data.getString("PEM_RW"));
//                            person.setPem_parrent(Integer.parseInt(PEM_PARENT));
//                            personArrayList.add(person);
//                            refresh_adapter();
//                        }
//                    }
//                }catch (Exception e){
//                    dialog.hideDialog();
//
//                    e.printStackTrace();
//                }
//                dialog.hideDialog();
//            },error->{
//                error.printStackTrace();
//                dialog.hideDialog();
//            });
//
//            RequestAdapter.getInstance().addToRequestQueue(request);
//        }catch (Exception e){
//            e.printStackTrace();
//            dialog.hideDialog();
//        }
    }

    public void refresh_adapter(){
        listPersonAdapter.notifyDataSetChanged();
    }

}
