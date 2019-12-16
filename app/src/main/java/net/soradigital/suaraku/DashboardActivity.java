package net.soradigital.suaraku;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import net.soradigital.suaraku.adapter.GridHomeOptionAdapter;
import net.soradigital.suaraku.adapter.GridSpacingItemDecoration;
import net.soradigital.suaraku.adapter.ListNewsAdapter;
import net.soradigital.suaraku.classes.HomeOption;
import net.soradigital.suaraku.classes.HomeOptionData;
import net.soradigital.suaraku.classes.News;
import net.soradigital.suaraku.helper.ApiHelper;
import net.soradigital.suaraku.helper.GridHelper;
import net.soradigital.suaraku.helper.RequestAdapter;
import net.soradigital.suaraku.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class DashboardActivity extends AppCompatActivity {

    RelativeLayout content;
    DrawerLayout drawerLayout;
    ImageView btn_menu;
    RecyclerView rv_menu,rv_news;
    LinearLayout btn_update_profile,btn_logout;
    TextView btn_update_pass;
    SessionManager sessionManager;
    ApiHelper apiHelper;
    ImageView image_kandidat;
    TextView txt_username;

    ArrayList<HomeOption> homeOptions = new ArrayList<>();
    ArrayList<News> newsArrayList = new ArrayList<>();
    ListNewsAdapter newsAdapter;
    GridHelper gridHelper;
    JSONObject sessionObj;
    BottomNavigationView bot_nav;

//    LinearLayout layoutBottomSheet;
//    Button btn_cukcok;
//    BottomSheetBehavior sheetBehavior;

    private String TAG = DashboardActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        content = (RelativeLayout) findViewById(R.id.content_dashboard);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        btn_menu = (ImageView) findViewById(R.id.btn_menu);
        rv_menu = (RecyclerView) findViewById(R.id.rv_menu);
        rv_news = (RecyclerView) findViewById(R.id.rv_news);
        btn_update_profile = (LinearLayout) findViewById(R.id.btn_update_profile);
        btn_update_pass = (TextView) findViewById(R.id.btn_update_pass);
        btn_logout = (LinearLayout) findViewById(R.id.btn_logout);
        apiHelper = new ApiHelper();
        sessionManager = new SessionManager(this);
        gridHelper = new GridHelper(this);
        image_kandidat = (ImageView) findViewById(R.id.image_kandidat);
        bot_nav = (BottomNavigationView) findViewById(R.id.navigation);
        txt_username = (TextView) findViewById(R.id.txt_username);

        newsArrayList = new ArrayList<>();
        newsAdapter = new ListNewsAdapter(this,newsArrayList);

        homeOptions.addAll(HomeOptionData.getListData());
//        btn_cukcok = (Button) findViewById(R.id.btn_login);
//        layoutBottomSheet = (LinearLayout) findViewById(R.id.bottom_sheet);
//        sheetBehavior = (BottomSheetBehavior) findViewById(R.id.)

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close) {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                float slideX = drawerView.getWidth() * slideOffset;
                content.setTranslationX(slideX*-1);
            }
        };

        drawerLayout.setScrimColor(Color.TRANSPARENT);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        btn_menu.setOnClickListener(v->{
            drawerLayout.openDrawer(Gravity.RIGHT);
        });

        btn_update_profile.setOnClickListener(v->{
            Intent intent = new Intent(this,ProfileActivity.class);
            startActivity(intent);
        });

        btn_update_pass.setOnClickListener(v->{
           Intent intent = new Intent(this,PasswordChangeActivity.class);
           startActivity(intent);
        });

        btn_logout.setOnClickListener(v->{
            logout();
        });

        prepare_dashboard();

    }

    private void load_menu(){
        rv_menu.setLayoutManager(new GridLayoutManager(this,3));
        rv_menu.addItemDecoration(new GridSpacingItemDecoration(3, gridHelper.dpToPx(8), true));
        rv_menu.setItemAnimator(new DefaultItemAnimator());
        rv_menu.setNestedScrollingEnabled(false);
        GridHomeOptionAdapter gridHomeOptionAdapter = new GridHomeOptionAdapter(homeOptions,this);
        rv_menu.setAdapter(gridHomeOptionAdapter);


        rv_news.setLayoutManager(new GridLayoutManager(this,1));
        rv_news.addItemDecoration(new GridSpacingItemDecoration(3, gridHelper.dpToPx(8), true));
        rv_news.setItemAnimator(new DefaultItemAnimator());
        rv_news.setNestedScrollingEnabled(false);
        rv_news.setAdapter(newsAdapter);

    }

    public void prepare_dashboard(){
        HashMap<String,String> session = sessionManager.get_session(sessionManager.LOGIN_SESSION);
        try {
            sessionObj = new JSONObject(session.get("data"));
            get_kandidat_data(sessionObj.getString("PEM_KOWIL"));
            txt_username.setText(sessionObj.getString("ACC_ALIAS"));
        }catch (Exception e){
            e.printStackTrace();
        }
        load_menu();
        load_berita();
        bot_nav.setOnNavigationItemSelectedListener(mOnNavigationItemReselectedListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemReselectedListener = menuItem -> {
        switch (menuItem.getItemId()){
            case R.id.profile_nav :
                Intent profile = new Intent(DashboardActivity.this,ProfileWebActivity.class);
                startActivity(profile);
                return false;
            case R.id.famili_nav :
                Intent family = new Intent(DashboardActivity.this,KeluargaActivity.class);
                startActivity(family);
                return false;
//            case R.id.reward_nav:
//                Intent reward = new Intent(DashboardActivity.this,RewardActivity.class);
//                startActivity(reward);
//                return false;
        }
        return true;
    };

    public void get_kandidat_data(String kabupaten_code){
        String url_image = apiHelper.getBase_url()+"images/calon/";
        String url = apiHelper.getUrl()+"&datatype=qcount&cmd=getcandidate&regcode="+kabupaten_code;
        url = url.replaceAll(" ", "%20");
        JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    if (response.has("candidate")){
                        try {
                            JSONArray kandidat_array = response.getJSONArray("candidate");
                            if (kandidat_array.length()>0){
                                JSONObject kandidat_obj = kandidat_array.getJSONObject(0);
                                Glide.with(this)
                                        .load(url_image+kandidat_obj.getString("CAN_IMAGE"))
                                        .placeholder(getResources().getDrawable(R.drawable.noavatar))
                                        .error(getResources().getDrawable(R.drawable.noavatar))
                                        .into(image_kandidat);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else if (response.has("error")){
                        try {
                            JSONArray error = response.getJSONArray("error");
                            JSONObject error_obj = error.getJSONObject(0);
                            Toast.makeText(getApplicationContext(),error_obj.getString("message"),Toast.LENGTH_LONG).show();
                            Glide.with(this)
                                    .load(getResources().getDrawable(R.drawable.noavatar))
                                    .into(image_kandidat);
                        } catch (JSONException e) {
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

    private void logout(){
        sessionManager.unset_session(sessionManager.LOGIN_SESSION);
        Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
        startActivity(intent);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        finishAffinity();
        finish();
    }


    public void load_berita(){
        String PEM_CODE = "";
        try {
            PEM_CODE = sessionObj.getString("PEM_CODE");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = apiHelper.getUrl()+"&datatype=news&cmd=get_berita&limit=5&page=1&pem_code="+PEM_CODE;
        url = url.replaceAll(" ", "%20");
        JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, url, null,
            response -> {
                try{
                    if (response.has("news")){
                        newsArrayList.clear();
                        JSONArray news_arr = response.getJSONArray("news");
                        for (int i=0;i<news_arr.length();i++){
                            JSONObject news_obj = news_arr.getJSONObject(i);
                            News news = new News();
                            news.setId_berita(news_obj.getInt("id_berita"));
                            news.setId_kategori(news_obj.getInt("id_kategori"));
                            news.setJudul(news_obj.getString("judul"));
                            news.setGambar(news_obj.getString("gambar"));
                            newsArrayList.add(news);
                        }
                        newsAdapter.notifyDataSetChanged();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            },
            error -> {

            }
        );
        RequestAdapter.getInstance().addToRequestQueue(request1);
    }
}
