package com.projetounivesp.pi2020_2.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.projetounivesp.pi2020_2.R;
import com.projetounivesp.pi2020_2.helper.UsuarioFirebase;
import com.projetounivesp.pi2020_2.model.Produto;

import java.io.ByteArrayOutputStream;

/**
 * Created by Gislene
 */

public class NovoProdutoActivity extends AppCompatActivity {

    private EditText editProdutoNome, editProdutoDescricao, editProdutoPreco;

    private ImageView imgProduto;
    private static final int SELECAO_GALERIA = 200;
    private StorageReference storageReference;
    private DatabaseReference firebaseRef;

    private String idUsuario;
    private String idProduto;
    private String urlImagemSelecionada = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_produto);

        inicializarComponentes();
        idUsuario = UsuarioFirebase.getIdUsuario();

        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo Produto");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap imagem = null;

            try {

                switch (requestCode) {
                    case SELECAO_GALERIA:
                        Uri localImagem = data.getData();
                        imagem = MediaStore.Images
                                .Media
                                .getBitmap(
                                        getContentResolver(),
                                        localImagem
                                );
                        break;
                }

                if (imagem != null) {

                    imgProduto.setImageBitmap(imagem);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    final StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("produtos")
                            .child(idProduto + "jpeg");

                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(NovoProdutoActivity.this,
                                    "Erro ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    task.getResult();
                                    Uri url = task.getResult();

                                    urlImagemSelecionada = url.toString();

                                    Toast.makeText(NovoProdutoActivity.this,
                                            "Sucesso ao fazer upload da imagem",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public void validarNovoProduto(View view){
        String nome = editProdutoNome.getText().toString();
        String descricao = editProdutoDescricao.getText().toString();
        String preco = editProdutoPreco.getText().toString();

        if ( !nome.isEmpty() ){
            if ( !descricao.isEmpty() ){
                if ( !preco.isEmpty() ){

                    Produto produto = new Produto();

                    produto.setIdUsuario(idUsuario);
                    produto.setNome(nome);
                    produto.setDescricao(descricao);
                    produto.setPreco( Double.parseDouble(preco));
                    produto.setUrlImagem(urlImagemSelecionada);

                    produto.salvar();
                    finish();

                    exibirMensagem("Produto salvo com sucesso!");
                }else{
                    exibirMensagem("Informe o preço");
                }
            }else{
                exibirMensagem("Informe a descrição");
            }
        }else{
            exibirMensagem("Informe o nome do produto");
        }
    }

    private void exibirMensagem(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

    private void inicializarComponentes(){
        editProdutoNome = findViewById(R.id.editNovoProdutoNome);
        editProdutoDescricao = findViewById(R.id.editNovoProdutoDescricao);
        editProdutoPreco = findViewById(R.id.editNovoProdutoPreco);
        imgProduto = findViewById(R.id.imgNovoProduto);
    }
}