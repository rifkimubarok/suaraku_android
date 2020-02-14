package net.soradigital.suaraku.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import net.soradigital.suaraku.R;
import net.soradigital.suaraku.WebActivity;
import net.soradigital.suaraku.classes.News;

import java.util.ArrayList;

public class ListNewsAdapter extends RecyclerView.Adapter<ListNewsAdapter.ListViewHolder>  {
    ArrayList<News> list_news;
    Context context;
    public ListNewsAdapter(Context context, ArrayList<News> list_news){
        this.context = context;
        this.list_news = list_news;
    }
    @NonNull
    @Override
    public ListNewsAdapter.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_berita_custom,parent,false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListNewsAdapter.ListViewHolder holder, int position) {
        News news = list_news.get(position);

        Glide.with(holder.itemView.getContext())
                .load(news.getGambar())
                .placeholder(R.drawable.logosuaraku)
                .apply(new RequestOptions())
                .into(holder.news_thumb);
        holder.news_title.setText(news.getJudul());
        holder.cv_item.setOnClickListener(v -> {
            Intent intent = new Intent(this.context, WebActivity.class);
            intent.putExtra("news_id",news.getId_berita());
            this.context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list_news.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        ImageView news_thumb;
        TextView news_title;
        Button btn_read;
        CardView cv_item;
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            news_thumb = itemView.findViewById(R.id.news_thumb);
            news_title = itemView.findViewById(R.id.news_title);
            btn_read = itemView.findViewById(R.id.btn_read);
            cv_item = itemView.findViewById(R.id.cv_item);
        }
    }
}
