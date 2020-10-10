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
import com.projetounivesp.pi2020_2.model.Usuario;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

/**
 * Created by Gislene
 */

public class ConfUsuarioActivity extends AppCompatActivity {


    private EditText editUrlImagem;
    private EditText editNome;
    private EditText editEndereco;
    private EditText editBairro;
    private EditText editCep;
    private EditText editTelefone;

    private ImageView imgPerfilUsuario;
    private static final int SELECAO_GALERIA = 200;
    private StorageReference storageReference;
    private String urlImagemSelecionada = "";

    private DatabaseReference firebaseRef;
    private String idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conf_usuario);

        inicializarComponentes();
        idUsuario = UsuarioFirebase.getIdUsuario();
        firebaseRef = ConfiguracaoFirebase.getFirebase();

        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações do Usuário");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imgPerfilUsuario.setOnClickListener(new View.OnClickListener() {
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
        //Recupera dados do usuário
        recuperarDadosUsuario();
    }

    private void recuperarDadosUsuario() {

        DatabaseReference usuarioRef = firebaseRef
                .child("usuarios")
                .child(idUsuario);

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {
                    Usuario usuario = dataSnapshot.getValue(Usuario.class);
                    editNome.setText(usuario.getNome());
                    editEndereco.setText(usuario.getEndereco());
                    editBairro.setText(usuario.getBairro().toString());
                    editCep.setText(usuario.getCep());
                    editTelefone.setText(usuario.getTelefone());

                    urlImagemSelecionada = usuario.getUrlImagem();
                    if (urlImagemSelecionada != "") {
                        Picasso.get()
                                .load(urlImagemSelecionada)
                                .into(imgPerfilUsuario);
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

                    imgPerfilUsuario.setImageBitmap(imagem);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    final StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("usuarios")
                            .child(idUsuario + "jpeg");


                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ConfUsuarioActivity.this,
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

                                    Toast.makeText(ConfUsuarioActivity.this,
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

    public void validarDadosUsuario(View view){

        String nome = editNome.getText().toString();
        String endereco = editEndereco.getText().toString();
        String bairro = editBairro.getText().toString();
        String cep = editCep.getText().toString();
        String telefone = editTelefone.getText().toString();

        if ( !nome.isEmpty() ){
            if ( !endereco.isEmpty() ){
                if ( !bairro.isEmpty() ){
                    if ( !cep.isEmpty() ){
                        if ( !telefone.isEmpty() ){

                            Usuario usuario = new Usuario();
                            usuario.setIdUsuario(idUsuario);

                            usuario.setNome(nome);
                            usuario.setEndereco(endereco);
                            usuario.setBairro(bairro);
                            usuario.setCep(cep);
                            usuario.setTelefone(telefone);

                            usuario.setUrlImagem(urlImagemSelecionada);
                            usuario.salvar();
                            finish();

                        }else{
                            exibirMensagem("Informe o seu telefone");
                        }
                    }else{
                        exibirMensagem("Informe o CEP do seu endereço");
                    }
                }else{
                    exibirMensagem("Informe o nome do seu bairro");
                }
            }else{
                exibirMensagem("Informe o seu endereço");
            }
        }else{
            exibirMensagem("Informe o seu nome");
        }

    }

    private void inicializarComponentes(){

        editNome = findViewById(R.id.editUsuarioNome);
        editEndereco = findViewById(R.id.editUsuarioEndereco);
        editBairro = findViewById(R.id.editUsuarioBairro);
        editCep = findViewById(R.id.editUsuarioCep);
        editTelefone = findViewById(R.id.editUsuarioTelefone);
        imgPerfilUsuario = findViewById(R.id.imgUsuario);

    }

    private void exibirMensagem(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

}