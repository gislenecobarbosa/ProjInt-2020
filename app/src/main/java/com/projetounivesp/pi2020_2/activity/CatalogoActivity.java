package com.projetounivesp.pi2020_2.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.projetounivesp.pi2020_2.R;
import com.projetounivesp.pi2020_2.adapter.AdapterProduto;
import com.projetounivesp.pi2020_2.helper.ConfiguracaoFirebase;
import com.projetounivesp.pi2020_2.helper.UsuarioFirebase;
import com.projetounivesp.pi2020_2.listener.RecyclerItemClickListener;
import com.projetounivesp.pi2020_2.model.Empresa;
import com.projetounivesp.pi2020_2.model.Pedido;
import com.projetounivesp.pi2020_2.model.PedidoItens;
import com.projetounivesp.pi2020_2.model.Produto;
import com.projetounivesp.pi2020_2.model.Usuario;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

/**
 * Created by Gislene
 */

public class CatalogoActivity extends AppCompatActivity {

    private RecyclerView recyclerProdutoCatalogo;
    private ImageView imageEmpresaCatalogo;
    private TextView textNomeEmpresaCatalogo;
    private Empresa empresaSelecionada;

    private AdapterProduto adapterProduto;
    private List<Produto> produtos = new ArrayList();
    private List<PedidoItens> itenscarrinho = new ArrayList();
    private DatabaseReference firebaseRef;
    private String idEmpresa = "WqbK5ol5AmWJr6kxodXS44xRvjh2";
    private String idUsuarioLogado;
    private Usuario usuario;
    private Pedido pedidoRecuperado;
    private TextView textCarrinhoQtde, textCarrinhoTotal;

    private int qtItensCarrinho;
    private Double totalCarrinho;
    private AlertDialog dialog;

    private int tipopagto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogo);

        inicializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            empresaSelecionada = (Empresa) bundle.getSerializable("empresa");
            textNomeEmpresaCatalogo.setText(empresaSelecionada.getNome());
            idEmpresa = empresaSelecionada.getIdUsuario();
            String url = empresaSelecionada.getUrlImagem();
            Picasso.get().load(url).into(imageEmpresaCatalogo);
        }

        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Catálogo");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //recyclerview
        recyclerProdutoCatalogo.setLayoutManager(new LinearLayoutManager(this));
        recyclerProdutoCatalogo.setHasFixedSize(true);
        adapterProduto = new AdapterProduto(produtos, this);
        recyclerProdutoCatalogo.setAdapter(adapterProduto);

        //evento click
        recyclerProdutoCatalogo.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerProdutoCatalogo,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                confirmarQuantidade(position);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );

        recuperarProdutos();
        recuperarUsuario();

    }

    private void confirmarQuantidade(final int posicao){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quantidade");
        builder.setMessage("Digite a quantidade");

        final EditText editQuantidade = new EditText(this);
        editQuantidade.setText("1");

        builder.setView(editQuantidade);

        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String quantidade = editQuantidade.getText().toString();

                //recupera produto selecionado e adiciona no carrinho
                Produto produtoSelecionado = produtos.get(posicao);
                PedidoItens item = new PedidoItens();
                item.setIdProduto(produtoSelecionado.getIdProduto());
                item.setDescricao(produtoSelecionado.getDescricao());
                item.setPreco(produtoSelecionado.getPreco());
                item.setQuantidade(Integer.parseInt(quantidade));

                //verificar se o item já foi adicionado
                itenscarrinho.add(item);

                if (pedidoRecuperado == null){
                    pedidoRecuperado = new Pedido(idUsuarioLogado, idEmpresa);
                }

                pedidoRecuperado.setNome(usuario.getNome());
                pedidoRecuperado.setEndereco(usuario.getEndereco());
                pedidoRecuperado.setBairro(usuario.getBairro());
                pedidoRecuperado.setCep(usuario.getCep());
                pedidoRecuperado.setTelefone(usuario.getTelefone());
                pedidoRecuperado.setItens(itenscarrinho);
                pedidoRecuperado.salvar();


            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void recuperarUsuario(){
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando dados")
                .setCancelable(false)
                .build();
        dialog.show();

        DatabaseReference usuarioRef = firebaseRef
                .child("usuarios")
                .child(idUsuarioLogado);
        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null ){
                    usuario = dataSnapshot.getValue(Usuario.class);
                }
                recuperarPedido();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void recuperarPedido() {
        DatabaseReference pedidoRef = firebaseRef
                .child("pedidos")
                .child(idEmpresa)
                .child(idUsuarioLogado);

        pedidoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                qtItensCarrinho = 0;
                totalCarrinho = 0.0;
                itenscarrinho = new ArrayList<>();

                //caso não tenha pedido
                if(dataSnapshot.getValue() != null){
                    pedidoRecuperado = dataSnapshot.getValue(Pedido.class);
                    itenscarrinho = pedidoRecuperado.getItens();

                    for (PedidoItens item: itenscarrinho){
                        int qt = item.getQuantidade();
                        Double preco = item.getPreco();

                        totalCarrinho += (qt * preco);
                        qtItensCarrinho += qt;

                    }
                }

                DecimalFormat df = new DecimalFormat("0.00");
                textCarrinhoQtde.setText("qtde: " + String.valueOf(qtItensCarrinho));
                textCarrinhoTotal.setText("R$: " + String.valueOf(df.format(totalCarrinho)));
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void recuperarProdutos(){
        DatabaseReference produtosRef = firebaseRef
                .child("produtos")
                .child(idEmpresa);

        produtosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                produtos.clear();

                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    produtos.add(ds.getValue(Produto.class));
                }

                adapterProduto.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_catalogo, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuPedido:
                confirmarPedido();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void confirmarPedido(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecione um método de pagamento");

        CharSequence[] itens = new CharSequence[]{
                "Dinheiro", "Cartão de Crédito", "Cartão de Débito"
        };
        builder.setSingleChoiceItems(itens, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tipopagto = which;
            }
        });

        final EditText editObservacao = new EditText(this);
        editObservacao.setHint("Deseja incluir alguma observação?");
        builder.setView(editObservacao);

        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String observacao = editObservacao.getText().toString();
                pedidoRecuperado.setTipoPagamento(tipopagto);
                pedidoRecuperado.setObservacao(observacao);
                pedidoRecuperado.setStatus("confirmado");
                pedidoRecuperado.confirmar();
                pedidoRecuperado.remover();
                pedidoRecuperado = null;
                finish();

            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void inicializarComponentes(){
        recyclerProdutoCatalogo = findViewById(R.id.recyclerProdutoCatalogo);
        imageEmpresaCatalogo = findViewById(R.id.imgEmpresaCatalogo);
        textNomeEmpresaCatalogo = findViewById(R.id.textNomeEmpresaCatalogo);

        textCarrinhoQtde = findViewById(R.id.textCarrinhoQtde);
        textCarrinhoTotal = findViewById(R.id.textCarrinhoTotal);
    }
}