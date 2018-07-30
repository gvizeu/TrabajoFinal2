package com.example.gonza.trabajofinal2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.gonza.trabajofinal2.Objetos.Coche;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;



public class Adapter extends RecyclerView.Adapter<Adapter.CochesViewHolder> {

    Context context;
    List<Coche> coches;
    public Adapter(List<Coche> coches, Context context) {

        this.context = context;
        this.coches = coches;
    }
    @Override
    public CochesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fila_layout, parent, false);
        CochesViewHolder holder = new CochesViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(CochesViewHolder holder, int position) {
        Coche coche = coches.get(position);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference httpsReference;
        if (coche.getImagenURL()==null){
            httpsReference = storage.getReferenceFromUrl("https://firebasestorage.googleapis.com/v0/b/pfinal2-e0d35.appspot.com/o/logo.png?alt=media&token=f891feec-4918-4231-bde0-474bdcba344c");

        }else{
            httpsReference  = storage.getReferenceFromUrl(coche.getImagenURL());
        }

        if (coche.getMarca()== null){
            holder.tvMarca.setText("Desconocido");
        }else{
            holder.tvMarca.setText(coche.getMarca());
        }
        if (coche.getModelo()==null){
            holder.tvProp.setText("Dsscocnocido");
        }else {
            holder.tvProp.setText(coche.getModelo());
        }



        holder.tvProp.setText(coche.getModelo());
        Glide.with(context).using(new FirebaseImageLoader()).load(httpsReference).into(holder.tvFoto);
        //holder.tvFoto.setImageDrawable(Drawable.createFromPath(coche.getImagenURL()));

    }

    @Override
    public int getItemCount() {
        return coches.size();
    }

    public static class CochesViewHolder extends RecyclerView.ViewHolder{
        TextView tvMarca, tvProp;
        ImageView tvFoto;

        @SuppressLint("WrongViewCast")
        public CochesViewHolder(View itemView) {
            super(itemView);
            tvMarca = itemView.findViewById(R.id.tv_marca);
            tvProp = itemView.findViewById(R.id.tv_propietario);
            tvFoto = itemView.findViewById(R.id.fotoCoche);



        }
    }

}
