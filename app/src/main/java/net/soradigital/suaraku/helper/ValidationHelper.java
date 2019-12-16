package net.soradigital.suaraku.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.Spinner;

public class ValidationHelper {
    private Context context;
    public ValidationHelper(Context context){
        this.context = context;
    }
    public boolean isTextValid(EditText editText,String message){
        if(isEmpty(editText)){
            setError(editText,message);
            return false;
        }else{
            clearError(editText);
            return true;
        }
    }

    public boolean isEmpty(EditText txt_editor){
        String value = txt_editor.getText().toString().trim();
        return value.length() == 0;
    }

    public void setError(EditText txt_editor,String msg_error){
        txt_editor.setError(msg_error);
        txt_editor.requestFocus();
        txt_editor.setSelection(txt_editor.getText().length());
    }

    public  void clearError(EditText txt_editor){
        txt_editor.setError(null);
    }

    public void createDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setCancelable(true)
                .setPositiveButton("OK", (dialog, which) -> {

                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
