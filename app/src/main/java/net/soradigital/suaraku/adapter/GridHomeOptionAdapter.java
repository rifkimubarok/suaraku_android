package net.soradigital.suaraku.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import net.soradigital.suaraku.AddSupporterActivity;
import net.soradigital.suaraku.InviteActivity;
import net.soradigital.suaraku.PerhitunganSuara;
import net.soradigital.suaraku.R;
import net.soradigital.suaraku.RecapActivity;
import net.soradigital.suaraku.RewardActivity;
import net.soradigital.suaraku.VIsiMisiActivity;
import net.soradigital.suaraku.WebActivity;
import net.soradigital.suaraku.classes.HomeOption;

import java.util.ArrayList;

public class GridHomeOptionAdapter extends RecyclerView.Adapter<GridHomeOptionAdapter.GridViewHolder> {

    private ArrayList<HomeOption> homeOptions;
    private String TAG = GridHomeOptionAdapter.class.getSimpleName();
    private Context context;

    public GridHomeOptionAdapter(ArrayList<HomeOption> list,Context context){
        this.homeOptions = list;
        this.context = context;
    }
    @NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_row_option,viewGroup,false);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridViewHolder holder, int position) {
        holder.labelname.setText(homeOptions.get(position).getLabelname());
        Glide.with(holder.itemView.getContext())
                .load(homeOptions.get(position).getImage())
                .apply(new RequestOptions())
                .into(holder.icon);
        holder.cardView.setOnClickListener(v -> {
            check_activity(homeOptions.get(position).getAction());
        });
    }

    @Override
    public int getItemCount() {
        return homeOptions.size();
    }

    public class GridViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView labelname;
        CardView cardView;
        public GridViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon_act);
            labelname =itemView.findViewById(R.id.label_act);
            cardView = itemView.findViewById(R.id.cv_item);
        }
    }

    private void check_activity(String text){
        switch (text){
            case "add_pendukung" :
                Intent intent = new Intent(context, InviteActivity.class);
                context.startActivity(intent);
                break;
            case "berita" :
                Intent berita = new Intent(context, WebActivity.class);
                context.startActivity(berita);
                break;
            case "rekap_dukungan" :
                Intent rekap_dukungan = new Intent(context, RecapActivity.class);
                context.startActivity(rekap_dukungan);
                break;
            case "perolehan" :
                Intent perolehan = new Intent(context, PerhitunganSuara.class);
                context.startActivity(perolehan);
                break;

            case "profile_calon" :
                Intent profile = new Intent(context, VIsiMisiActivity.class);
                context.startActivity(profile);
                break;

        }
    }
}
