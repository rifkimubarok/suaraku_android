package net.soradigital.suaraku;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import net.soradigital.suaraku.adapter.ListInviteAdapter;
import net.soradigital.suaraku.api.RetrofitClientInstance;
import net.soradigital.suaraku.api.SupporterService;
import net.soradigital.suaraku.classes.Account;
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

public class InviteActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    ArrayList<Account> accountArrayList;
    MenuItem menuItem;
    SessionManager sessionManager;
    private static final int ItemThreeDotMenu = 1;
    private static String TAG = InviteActivity.class.getCanonicalName();
    JSONObject session;
    ApiHelper apiHelper;
    CustomDialog dialog;
    ListInviteAdapter listInviteAdapter;
    SupporterService supporterService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.rv_user_invite);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back_black));
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimaryDark));
        setTitle("Daftar Pendukung");

        accountArrayList = new ArrayList<>();
        sessionManager = new SessionManager(this);
        apiHelper = new ApiHelper();
        dialog = new CustomDialog(this);
        supporterService = RetrofitClientInstance.getRetrofitInstance().create(SupporterService.class);

        prepare_activity();
    }

    public void prepare_activity(){
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listInviteAdapter = new ListInviteAdapter(accountArrayList,this,this);
        recyclerView.setAdapter(listInviteAdapter);

        HashMap<String,String> data = sessionManager.get_session(sessionManager.LOGIN_SESSION);
        try{
            session = new JSONObject(data.get("data"));
        }catch (Exception e){
            e.printStackTrace();
        }
        load_invite_user();
    }

    public void load_invite_user(){
        if (accountArrayList.size()>0){
            accountArrayList.clear();
        }
        dialog.showDialog();
        String token = sessionManager.getSessionString("token");
        Call<HashMap<String, Object>> call = supporterService.invited(token);
        call.enqueue(new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
                String message = response.body().get("message").toString();
                Boolean success = Boolean.parseBoolean(response.body().get("success").toString());
                dialog.hideDialog();

                if(success){
                    ArrayList<Map<String, Object>> data = (ArrayList<Map<String, Object>>) response.body().get("data");
                    for (int i=0; i < data.size(); i++){
                        Gson gson = new Gson();
                        String obj = gson.toJson(data.get(i));
                        Account acc = gson.fromJson(obj, Account.class);
                        Log.d(TAG,"Acc Noreg" + acc.getACC_NOREG());
                        accountArrayList.add(acc);
                    }
                    refresh_adapter();
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {

            }
        });
    }

    public void refresh_adapter(){
        listInviteAdapter.notifyDataSetChanged();
    }

    public  boolean onCreateOptionsMenu(Menu menu){
        menuItem = (MenuItem) menu.add(0,ItemThreeDotMenu,0,null);
        menuItem.setIcon(R.drawable.add_group).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//        View v = MenuItemCompat.getActionView(menuItem);
//        showPopupMenu(v);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case ItemThreeDotMenu:
                Intent intent = new Intent(InviteActivity.this,AddSupporterActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }
}
