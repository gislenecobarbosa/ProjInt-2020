package com.projetounivesp.pi2020_2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.projetounivesp.pi2020_2.R;
import com.projetounivesp.pi2020_2.model.Produto;

import java.util.List;

/**
 * Created by Gislene
 */

public class AdapterProduto extends RecyclerView.Adapter<AdapterProduto.MyViewHolder>{

    private List<Produto> produtos;
    private Context context;

    public AdapterProduto(List<Produto> produtos, Context context) {
        this.produtos = produtos;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_produto, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        Produto produto = produtos.get(i);
        holder.nome.setText(produto.getNome());
        holder.descricao.setText(produto.getDescricao());
        holder.valor.setText("R$ " + Double.parseDouble(produto.getPreco().toString()));

        //Carregar imagem
        String url = produto.getUrlImagem();
//        Picasso.get().load(url).into(holder.imagemProduto);

    }

    @Override
    public int getItemCount() {
        return produtos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imagemProduto;
        TextView nome;
        TextView descricao;
        TextView valor;

        public MyViewHolder(View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.textAdapterProdutoNome);
            descricao = itemView.findViewById(R.id.textAdapterProdutoDescricao);
            valor = itemView.findViewById(R.id.textAdapterProdutoPreco);
            imagemProduto = itemView.findViewById(R.id.imgAdapterProduto);


        }
    }
}
