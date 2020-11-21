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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.projetounivesp.pi2020_2.R;
import com.projetounivesp.pi2020_2.helper.ConfiguracaoFirebase;
import com.projetounivesp.pi2020_2.helper.UsuarioFirebase;
import com.projetounivesp.pi2020_2.model.Empresa;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;


/**
 * Created by Gislene
 */

public class ConfEmpresaActivity extends AppCompatActivity {
    private EditText editEmpNome, editEmpEndereco, editEmpBairro, editEmpCep, editEmpTelefone;
    private ImageView imgPerfilEmpresa;

    private static final int SELECAO_GALERIA = 200;
    private StorageReference storageReference;
    private DatabaseReference firebaseRef;
    private String idUsuarioLogado;
    private String urlImagemSelecionada = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conf_papelaria);

        //Configurações iniciais
        inicializarComponentes();
        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        //Configurações Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imgPerfilEmpresa.setOnClickListener(    new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                );
                if( i.resolveActivity(getPackageManager()) != null ){
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });

        /*Recuperar dados da empresa*/
        recuperarDadosEmpresa();


    }

    private void recuperarDadosEmpresa() {

        DatabaseReference empresaRef = firebaseRef
                .child("empresas")
                .child(idUsuarioLogado);

        empresaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {
                    Empresa empresa = dataSnapshot.getValue(Empresa.class);
                    editEmpNome.setText(empresa.getNome());
                    editEmpEndereco.setText(empresa.getEndereco());
                    editEmpBairro.setText(empresa.getBairro());
                    editEmpCep.setText(empresa.getCep());
                    editEmpTelefone.setText(empresa.getTelefone());

                    urlImagemSelecionada = empresa.getUrlImagem();
                    if (urlImagemSelecionada != "") {
                        Picasso.get()
                                .load(urlImagemSelecionada)
                                .into(imgPerfilEmpresa);
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void validarConfDadosEmpresa(View view) {

        //Valida se os campos foram preenchidos
        String nome = editEmpNome.getText().toString();
        String endereco = editEmpEndereco.getText().toString();
        String bairro = editEmpBairro.getText().toString();
        String cep = editEmpCep.getText().toString();
        String telefone = editEmpTelefone.getText().toString();

        if (!nome.isEmpty()) {
            if (!endereco.isEmpty()) {
                if (!bairro.isEmpty()) {
                    if (!cep.isEmpty()) {
                        if (!telefone.isEmpty()) {
                            Empresa empresa = new Empresa();
                            empresa.setIdUsuario(idUsuarioLogado);
                            empresa.setNome(nome);
                            empresa.setEndereco(endereco);
                            empresa.setBairro(bairro);
                            empresa.setCep(cep);
                            empresa.setTelefone(telefone);
                            empresa.setUrlImagem(urlImagemSelecionada);
                            empresa.salvar();

                            exibirMensagem("Dados atualizados com sucesso!");

                            finish();
                        } else {
                            exibirMensagem("Informe o telefone");
                        }
                    } else {
                        exibirMensagem("Informe o CEP");
                    }
                } else {
                    exibirMensagem("Informe o Bairro");
                }
            } else {
                exibirMensagem("Informe o Endereco");
            }
        } else {
            exibirMensagem("Informe o Nome");
        }

    }

    private void exibirMensagem(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT)
                .show();
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

                    imgPerfilEmpresa.setImageBitmap(imagem);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    final StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("empresas")
                            .child(idUsuarioLogado + "jpeg");


                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ConfEmpresaActivity.this,
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

                                     Toast.makeText(ConfEmpresaActivity.this,
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

    private void inicializarComponentes() {
        editEmpNome = findViewById(R.id.editConfEmpresaNome);
        editEmpEndereco = findViewById(R.id.editConfEmpresaEndereco);
        editEmpBairro = findViewById(R.id.editConfEmpresaBairro);
        editEmpCep = findViewById(R.id.editConfEmpresaCep);
        editEmpTelefone = findViewById(R.id.editConfEmpresaTelefone);
        imgPerfilEmpresa = findViewById(R.id.imgConfEmpresa);
    }


}
