package net.soradigital.suaraku.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import net.soradigital.suaraku.InviteActivity;
import net.soradigital.suaraku.ProfileActivity;
import net.soradigital.suaraku.ProfileWebActivity;
import net.soradigital.suaraku.R;
import net.soradigital.suaraku.TambahKeluargaActivity;
import net.soradigital.suaraku.classes.Account;
import net.soradigital.suaraku.helper.ApiHelper;
import net.soradigital.suaraku.helper.CustomDialog;
import net.soradigital.suaraku.helper.RequestAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListInviteAdapter extends RecyclerView.Adapter<ListInviteAdapter.ListViewHolder> {
    ArrayList<Account> accountArrayList;
    ApiHelper apiHelper = new ApiHelper();
    CustomDialog customDialog;
    Context context;
    Activity activity;
    public ListInviteAdapter(ArrayList<Account> accounts, Context context, Activity activity){
        this.accountArrayList = accounts;
        this.context = context;
        this.activity = activity;
        customDialog = new CustomDialog(this.activity);
    }
    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_invite,parent,false);
        return new ListInviteAdapter.ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        String nomor = String.valueOf(position+1)+".";
        Account account = accountArrayList.get(position);
        holder.text_no.setText(nomor);
        holder.text_nama.setText(account.getACC_ALIAS());
        switch (account.getACC_STATUS()){
            case Account.ACTIVE:
                    holder.btn_accept.setVisibility(View.GONE);
                    holder.btn_reject.setVisibility(View.GONE);
                    holder.text_update.setVisibility(View.GONE);
                break;
            case Account.REGISTER:
                holder.btn_accept.setVisibility(View.GONE);
                holder.btn_reject.setVisibility(View.GONE);
                break;
            case Account.NOT_ACTIVE:
                holder.text_update.setVisibility(View.GONE);
                break;
        }

        holder.btn_accept.setOnClickListener(v->{
            String message = "Anda yakin akan mengaktivasi akun "+account.getACC_ALIAS()+" ?";
            AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
            builder.setMessage(message)
                    .setNegativeButton("Batal",(dialogInterface, i) -> {})
                    .setPositiveButton("SETUJUI", (dialog, which) -> {
                        aktivasi_user(account.getACC_ALIAS());
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });

        holder.btn_reject.setOnClickListener(v->{
            Toast.makeText(this.context,"Tolak",Toast.LENGTH_SHORT).show();
        });

        holder.act_show_profile.setOnClickListener(v->{
            Intent profile = new Intent(context, ProfileWebActivity.class);
            profile.putExtra("ACC_KOWIL",account.getACC_KOWIL());
            profile.putExtra("ACC_NOREG",account.getACC_NOREG());
            context.startActivity(profile);
        });
    }

    @Override
    public int getItemCount() {
        return accountArrayList.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        TextView text_no,text_nama,text_update;
        Button btn_accept,btn_reject;
        CardView act_show_profile;
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            text_no =  itemView.findViewById(R.id.label_nomor);
            text_nama = itemView.findViewById(R.id.label_nama);
            btn_accept = itemView.findViewById(R.id.btn_accept);
            btn_reject = itemView.findViewById(R.id.btn_reject);
            text_update = itemView.findViewById(R.id.label_update);
            act_show_profile = itemView.findViewById(R.id.act_show_profile);
        }
    }

    public void aktivasi_user(String username){
        String url = apiHelper.getUrl();
        url+= "&datatype=user&cmd=usersetactive&ACC_ALIAS="+username;
        customDialog.showDialog();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,null, response -> {
            try{
                customDialog.hideDialog();
                JSONArray arr_result = response.getJSONArray("error");
                JSONObject row = arr_result.getJSONObject(0);
                if (row.getString("number").equals("2017")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
                    builder.setMessage(row.getString("message"))
                            .setCancelable(false)
                            .setPositiveButton("OK", (dialog, which) -> {
                                ((InviteActivity)this.context).load_invite_user();
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }catch (Exception e){
                customDialog.hideDialog();
            }
        },
        error -> {
            customDialog.hideDialog();
        });
        RequestAdapter.getInstance().addToRequestQueue(request);
    }
}
