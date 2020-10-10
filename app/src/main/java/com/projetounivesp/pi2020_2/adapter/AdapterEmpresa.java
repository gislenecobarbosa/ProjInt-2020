package com.projetounivesp.pi2020_2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.projetounivesp.pi2020_2.R;
import com.projetounivesp.pi2020_2.model.Empresa;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Gislene
 */

public class AdapterEmpresa extends RecyclerView.Adapter<AdapterEmpresa.MyViewHolder> {

    private List<Empresa> empresas;

    public AdapterEmpresa(List<Empresa> empresas) {
        this.empresas = empresas;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_empresa, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        Empresa empresa = empresas.get(i);

        //Carregar imagem
        String urlImagem = empresa.getUrlImagem();
        Picasso.get().load( urlImagem ).into( holder.imagemEmpresa );

    }

    @Override
    public int getItemCount() {
        return empresas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imagemEmpresa;
        TextView nomeEmpresa;
        TextView endereco;
        TextView telefone;

        public MyViewHolder(View itemView) {
            super(itemView);

            imagemEmpresa = itemView.findViewById(R.id.imgAdapterEmpresa);
        }
    }
}
