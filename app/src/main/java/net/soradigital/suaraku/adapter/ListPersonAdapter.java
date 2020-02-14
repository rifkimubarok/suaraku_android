package net.soradigital.suaraku.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import net.soradigital.suaraku.KeluargaWebActivity;
import net.soradigital.suaraku.R;
import net.soradigital.suaraku.TambahKeluargaActivity;
import net.soradigital.suaraku.classes.Person;

import java.util.ArrayList;

public class ListPersonAdapter extends RecyclerView.Adapter<ListPersonAdapter.ListViewHolder>  {
    private ArrayList<Person> personArrayList;
    Context context;
    public  ListPersonAdapter(ArrayList<Person> person, Context context){
        this.personArrayList = person;
        this.context = context;
    }
    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_keluarga,parent,false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        Person person = personArrayList.get(position);
        String nomor = String.valueOf(position+1)+".";
        String PEM_PARENT = String.valueOf(person.getPem_parrent());
        holder.text_no.setText(nomor);
        holder.text_nama.setText(person.getNama());
        holder.cv_see_family.setOnClickListener(v->{
            Intent intent = new Intent(context, KeluargaWebActivity.class);
            intent.putExtra("noreg",person.getAcc_noreg());
            intent.putExtra("title","Ubah Data Keluarga");
            intent.putExtra("PEM_PARENT",PEM_PARENT);
            intent.putExtra("statusSave",1);
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return personArrayList.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        TextView text_no,text_nama,text_update;
        CardView cv_see_family;
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            text_no =  itemView.findViewById(R.id.label_nomor);
            text_nama = itemView.findViewById(R.id.label_nama);
            cv_see_family = itemView.findViewById(R.id.cv_see_family);
            /*text_update = itemView.findViewById(R.id.label_update);*/
        }
    }
}
