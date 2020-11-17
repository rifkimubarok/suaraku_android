package net.soradigital.suaraku.helper;

import android.content.Context;

import net.soradigital.suaraku.caching.FileCacher;

import java.util.HashMap;

public class SessionManager {
    private Context context;

    public String LOGIN_SESSION = "userSession";
    public String SIGNUP_SESSION = "signUpSession";

    public SessionManager(Context context){
        this.context = context;
    }

    public void set_session(String data,String session_name){
        FileCacher<String> fileCacher = new FileCacher<>(context,session_name);
        try {
            if (fileCacher.hasCache()){
                fileCacher.clearCache();
                fileCacher.writeCache(data);
            }else{
                fileCacher.writeCache(data);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public HashMap<String,String> get_session(String session_name){
        HashMap<String,String> session = new HashMap<>();
        try{
            FileCacher<String> fileCacher = new FileCacher<>(context,session_name);
            if (fileCacher.hasCache()){
                session.put("status","1");
                session.put("data",fileCacher.readCache().toString());
            }else{
                session.put("status","0");
                session.put("data","");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return session;
    }

    public String getSessionString(String sessionName){
        String session = "";
        try{
            FileCacher<String> fileCacher = new FileCacher<>(context,sessionName);
            if (fileCacher.hasCache()){
                session = fileCacher.readCache().toString();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return session;
    }

    public void unset_session(String session_name){
        try {
            FileCacher<String> fileCacher = new FileCacher<>(context,session_name);
            fileCacher.clearCache();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
