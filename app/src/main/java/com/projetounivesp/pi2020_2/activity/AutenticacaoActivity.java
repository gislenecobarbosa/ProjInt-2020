package com.projetounivesp.pi2020_2.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.projetounivesp.pi2020_2.R;
import com.projetounivesp.pi2020_2.helper.ConfiguracaoFirebase;
import com.projetounivesp.pi2020_2.helper.UsuarioFirebase;

/**
 * Created by Gislene
 */

public class AutenticacaoActivity extends AppCompatActivity {

    private Button botaoAcessar;
    private EditText campoEmail, campoSenha;
    private Switch tipoAcesso;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autenticacao);

        inicializaCompoenentes();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.signOut();

        verificaUsuarioLogado();

        botaoAcessar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String email = campoEmail.getText().toString();
                String senha = campoSenha.getText().toString();

                if (!email.isEmpty()){
                    if (!senha.isEmpty()){
                        if ( tipoAcesso.isChecked()){ //cadastrar
                            autenticacao.createUserWithEmailAndPassword(
                                    email,senha
                            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()){

                                        Toast.makeText(AutenticacaoActivity.this,
                                                "Cadastro realizado com sucesso!",
                                                Toast.LENGTH_SHORT).show();
                                        String tipoUsuario = "U";
                                        UsuarioFirebase.atualizaTipoUsuario(tipoUsuario);
                                        abrirTelaPrincipal(tipoUsuario);
                                    }else{
                                        String erro = "";
                                        try {
                                            throw task.getException();
                                        }catch (FirebaseAuthWeakPasswordException e){
                                            erro = "Senha muito fraca";
                                        }catch (FirebaseAuthInvalidCredentialsException e){
                                            erro = "E-mail inválido";
                                        }catch (FirebaseAuthUserCollisionException e){
                                            erro = "E-mail já cadastrado";
                                        }catch (Exception e){
                                            erro = " ao cadastrar usuário: " + e.getMessage();
                                            e.printStackTrace();
                                        }
                                        Toast.makeText(AutenticacaoActivity.this,
                                                "Erro: " + erro,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else{ //Login
                            autenticacao.signInWithEmailAndPassword(
                                    email,senha
                            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(AutenticacaoActivity.this,
                                                "Logado com sucesso",
                                                Toast.LENGTH_SHORT).show();
                                        String tipoUsuario = task.getResult().getUser().getDisplayName();
                                        abrirTelaPrincipal(tipoUsuario);
                                    }else {
                                        Toast.makeText(AutenticacaoActivity.this,
                                                "Erro ao fazer login" + task.getException(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    } else{
                        Toast.makeText(AutenticacaoActivity.this,
                                "Informe a senha!",
                                Toast.LENGTH_SHORT).show();
                    }
                } else{
                    Toast.makeText(AutenticacaoActivity.this,
                            "Informe o e-mail!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void verificaUsuarioLogado(){
        FirebaseUser usuarioAtual = autenticacao.getCurrentUser();
        if (usuarioAtual != null){
            String tipoUsuario = usuarioAtual.getDisplayName();
            abrirTelaPrincipal(tipoUsuario);
        }
    }

    private void abrirTelaPrincipal(String tipoUsuario){
        if (tipoUsuario.equals("E")){
            startActivity(new Intent(getApplicationContext(),EmpresaActivity.class));
        }else{
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
        }

    }
    private void inicializaCompoenentes(){
        campoEmail = findViewById(R.id.editLoginEmail);
        campoSenha = findViewById(R.id.editLoginSenha);
        botaoAcessar = findViewById(R.id.botaoLogar);
        tipoAcesso = findViewById(R.id.switchTipoAcesso);
    }


}