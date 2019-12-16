package net.soradigital.suaraku;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.soradigital.suaraku.adapter.ListInviteAdapter;
import net.soradigital.suaraku.classes.Account;
import net.soradigital.suaraku.helper.ApiHelper;
import net.soradigital.suaraku.helper.CustomDialog;
import net.soradigital.suaraku.helper.RequestAdapter;
import net.soradigital.suaraku.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

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
        try {
            String url = apiHelper.getUrl()+"&datatype=user&cmd=getuserinvite&ACC_REFF_ID=";
            String ACC_REFF_ID = session.getString("ACC_REFF_ID");
            url+= ACC_REFF_ID;
            url = url.replaceAll(" ", "%20");
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,url,null,response -> {
                try{
                    if (response.has("data")){
                        JSONArray arr_user = response.getJSONArray("data");
                        for (int i=0;i<arr_user.length();i++){
                            JSONObject obj_user = arr_user.getJSONObject(i);
                            Account account = new Account();
                            account.setACC_INDEX(obj_user.getString("ACC_INDEX"));
                            account.setACC_ALIAS(obj_user.getString("ACC_ALIAS"));
                            account.setACC_STATUS(obj_user.getInt("ACC_STATUS"));
                            account.setACC_DATEREG(obj_user.getString("ACC_DATEREG"));
                            account.setACC_EMAIL(obj_user.getString("ACC_EMAIL"));
                            account.setACC_NOREG(obj_user.getString("ACC_NOREG"));
                            account.setACC_KOWIL(obj_user.getString("ACC_KOWIL"));
                            accountArrayList.add(account);
                        }
                        refresh_adapter();
                        dialog.hideDialog();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    dialog.hideDialog();
                }
            },error -> {
                dialog.hideDialog();
               error.printStackTrace();
            });

            RequestAdapter.getInstance().addToRequestQueue(request);

        } catch (Exception e) {
            dialog.hideDialog();
            e.printStackTrace();
        }
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
        Log.v(TAG,"item menunya "+item.getItemId());
        switch (item.getItemId()){
            case ItemThreeDotMenu:
                Intent intent = new Intent(InviteActivity.this,AddSupporterActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }
}
