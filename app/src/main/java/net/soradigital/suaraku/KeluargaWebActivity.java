package net.soradigital.suaraku;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;

import net.soradigital.suaraku.api.RetrofitClientInstance;
import net.soradigital.suaraku.api.SupporterService;
import net.soradigital.suaraku.caching.FileCacher;
import net.soradigital.suaraku.helper.ApiHelper;
import net.soradigital.suaraku.helper.CustomDialog;
import net.soradigital.suaraku.helper.RequestAdapter;
import net.soradigital.suaraku.helper.SessionManager;
import net.soradigital.suaraku.helper.ValidationHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KeluargaWebActivity extends AppCompatActivity {
    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    private static final int INPUT_FILE_REQUEST_CODE = 1;
    private static final int FILECHOOSER_RESULTCODE = 1;
    private WebView webView;
    private SwipeRefreshLayout swipeContent;
    private ProgressBar progressBar;
    static String TAG = WebActivity.class.getSimpleName();
    private JSONObject sessionObj;
    private ApiHelper apiHelper;
    Toolbar toolbar;
    SessionManager sessionManager;
    String reg_code = "";
    String acc_noreg = "";
    JSONObject sesi_obj;
    HashMap<String,String> user_sesi;
    JSONObject user_session;
    private String PEM_PARENT = "";
    int statusSave = 0;
    SupporterService supporterService;

    MenuItem menuItem;
    private static final int ItemThreeDotMenu = 10;
    CustomDialog customDialog;
    AlertDialog.Builder dialogBuilder;
    ValidationHelper validationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keluarga_web);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        progressBar = (ProgressBar) findViewById(R.id.progress_webview);
        swipeContent = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        webView = (WebView) findViewById(R.id.webContent);
        sessionManager = new SessionManager(this);
        apiHelper = new ApiHelper();
        customDialog = new CustomDialog(this);
        dialogBuilder = new AlertDialog.Builder(this);
        validationHelper = new ValidationHelper(this);
        supporterService = RetrofitClientInstance.getRetrofitInstance().create(SupporterService.class);

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
        setTitle("Tambah Data");
        //        get Session
        HashMap<String,String> sesi = sessionManager.get_session(sessionManager.LOGIN_SESSION);
        try {
            user_session = new JSONObject(sesi.get("data"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        user_sesi = sessionManager.get_session(sessionManager.LOGIN_SESSION);
        try {
            sesi_obj = new JSONObject(user_sesi.get("data"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        webView.setWebChromeClient(new customChrome(progressBar));
        webView.setWebViewClient(new customView(progressBar));
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            if (webView.getScrollY() == 0){
                swipeContent.setEnabled(true);
            }else{
                swipeContent.setEnabled(false);
            }
        });

        swipeContent.setOnRefreshListener(() -> webView.reload());
        HashMap<String,String> session = sessionManager.get_session(sessionManager.LOGIN_SESSION);


        if (getIntent().getExtras() != null){
            try{
                Bundle bundle = getIntent().getExtras();
                acc_noreg = bundle.getString("noreg");
                if (!bundle.getString("title").isEmpty())setTitle(bundle.getString("title"));
                String url = apiHelper.newBaseUrl + "dashboard/mobile/set-profile/" + acc_noreg + "?isFamily=true";
                webView.loadUrl(url);
                statusSave = bundle.getInt("statusSave");
            }catch (Exception e){
//                customDialog.createToast("Error "+ e.getMessage());
                e.printStackTrace();
            }
        }else{
            try {
                sessionObj = new JSONObject(session.get("data"));
                String url = apiHelper.newBaseUrl + "dashboard/mobile/set-profile/" + acc_noreg + "?isFamily=true";
                customDialog.createToast("URL "+ url);
                webView.loadUrl(url);
            }catch (Exception e){
                e.printStackTrace();
//                customDialog.createToast("Error "+ e.getMessage());
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu){
        if (statusSave == 1){
            menuItem = (MenuItem) menu.add(0,ItemThreeDotMenu,0,null);
            menuItem.setIcon(R.drawable.ic_delete_black).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
//        View v = MenuItemCompat.getActionView(menuItem);
//        showPopupMenu(v);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case ItemThreeDotMenu:
                Log.d(TAG,"Button Delete clicked");
                openDialog();
                break;
        }
        return true;
    }

    public void openDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi");
        builder.setMessage("Data ini akan dihapus. Lanjutkan ?");
        builder.setPositiveButton("Lanjutkan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteData();
            }
        });
        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialogconfirm = builder.create();
        dialogconfirm.show();
    }

    public void deleteData(){
        String token = sessionManager.getSessionString("token");
        Call<HashMap<String, Object>> call = supporterService.deleteFamily(token, acc_noreg);
        customDialog.showDialog();
        call.enqueue(new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
                String message = response.body().get("message").toString();
                Boolean success = Boolean.parseBoolean(response.body().get("success").toString());
                if(success){
                    dialogBuilder.setMessage(message)
                            .setCancelable(true)
                            .setPositiveButton("OK", (dialog, which) -> {
                                onBackPressed();
                            });
                    AlertDialog alertDialog = dialogBuilder.create();
                    alertDialog.show();
                }else{
                    validationHelper.createDialog(message);
                }
                customDialog.hideDialog();
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {
                validationHelper.createDialog("Terjadi Kesalahan");
            }
        });

      /*  String url = apiHelper.getUrl()+"&datatype=profile&cmd=deleteAccount&regcode="+reg_code+"&acc_noreg="+acc_noreg;
        customDialog.showDialog();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,url,null, response -> {

            if (response.has("status")){
                dialogBuilder.setMessage("Data berhasil dihapus.")
                        .setCancelable(true)
                        .setPositiveButton("OK", (dialog, which) -> {
                            onBackPressed();
                        });
                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
            }else{
                validationHelper.createDialog("Terjadi kesalahan dalam menghapus data.");
            }
            customDialog.hideDialog();
        },error -> {
            customDialog.hideDialog();
        });

        RequestAdapter.getInstance().addToRequestQueue(request);*/
    }



    class customChrome extends WebChromeClient {
        ProgressBar progressbar;
        private View mCustomView;
        private CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        public customChrome(ProgressBar progress){
            this.progressbar = progress;
        }

        public Bitmap getDefaultVideoPoster()
        {
            if (mCustomView == null) {
                return null;
            }
            return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
        }

        public void onHideCustomView()
        {
            ((FrameLayout)getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, CustomViewCallback paramCustomViewCallback)
        {
            if (this.mCustomView != null)
            {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout)getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getWindow().getDecorView().setSystemUiVisibility(3846);
        }

        private File createImageFile() throws IOException {
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            File imageFile = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
            return imageFile;
        }

        public void onProgressChanged(WebView view, int progress) {
            progressbar.setProgress(progress *100);
        }

        // For Android 5.0
        public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> filePath, FileChooserParams fileChooserParams) {
            // Double check that we don't have any existing callbacks
            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
            }
            mFilePathCallback = filePath;
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                    takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    Log.e(TAG, "Unable to create Image File", ex);
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                } else {
                    takePictureIntent = null;
                }
            }
            Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
            contentSelectionIntent.setType("image/*");
            Intent[] intentArray;
            if (takePictureIntent != null) {
                intentArray = new Intent[]{takePictureIntent};
            } else {
                intentArray = new Intent[0];
            }
            Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
            chooserIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
//            chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
            startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);
            return true;
        }

        // openFileChooser for Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            mUploadMessage = uploadMsg;
            // Create AndroidExampleFolder at sdcard
            // Create AndroidExampleFolder at sdcard
            File imageStorageDir = new File(
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES)
                    , "AndroidExampleFolder");
            if (!imageStorageDir.exists()) {
                // Create AndroidExampleFolder at sdcard
                imageStorageDir.mkdirs();
            }
            // Create camera captured image file path and name
            File file = new File(
                    imageStorageDir + File.separator + "IMG_"
                            + String.valueOf(System.currentTimeMillis())
                            + ".jpg");
            mCapturedImageURI = Uri.fromFile(file);
            // Camera capture image intent
            final Intent captureIntent = new Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            // Create file chooser intent
            Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
            // Set camera intent to file chooser
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS
                    , new Parcelable[]{captureIntent});
            // On select image call onActivityResult method of activity
            startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
        }

        // openFileChooser for Android < 3.0
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooser(uploadMsg, "");
        }

        //openFileChooser for other Android versions
        public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                    String acceptType,
                                    String capture) {
            openFileChooser(uploadMsg, acceptType);
        }
    }

    class customView extends WebViewClient {
        private ProgressBar progressBar;

        public customView(ProgressBar progressBar) {
            this.progressBar=progressBar;
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
            swipeContent.setRefreshing(false);
            if (url.startsWith(apiHelper.newBaseUrl + "hitungsuara/login-mobile")){
                FileCacher username_cache = new FileCacher(getApplicationContext(),"username");
                FileCacher password_cache = new FileCacher(getApplicationContext(),"password");
                String username = "";
                String password = "";
                try {
                    username = username_cache.readCache().toString();
                    password = password_cache.readCache().toString();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                String ur = "javascript:" +
                        "document.getElementById('password').value = '" + password + "';"  +
                        "document.getElementById('username').value = '" + username + "';"  +
                        "document.forms[0].submit()";
                Log.d(TAG, "username" + username);
                Log.d(TAG, password);
                view.loadUrl(ur);
            }
            if (url.startsWith(apiHelper.newBaseUrl + "login")){
                FileCacher username_cache = new FileCacher(getApplicationContext(),"username");
                FileCacher password_cache = new FileCacher(getApplicationContext(),"password");
                String username = "";
                String password = "";
                try {
                    username = username_cache.readCache().toString();
                    password = password_cache.readCache().toString();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                String ur = "javascript:" +
                        "document.getElementById('password').value = '" + password + "';"  +
                        "document.getElementById('username').value = '" + username + "';"  +
                        "document.forms[0].submit()";
                Log.d(TAG, "username" + username);
                Log.d(TAG, password);
                view.loadUrl(ur);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }
}
