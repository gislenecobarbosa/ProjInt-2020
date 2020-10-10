package com.projetounivesp.pi2020_2.model;

import com.google.firebase.database.DatabaseReference;
import com.projetounivesp.pi2020_2.helper.ConfiguracaoFirebase;

/**
 * Created by Gislene
 */

public class Usuario {

    private String idUsuario;
    private String urlImagem;
    private String nome;
    private String email;
    private String endereco;
    private String bairro;
    private String cep;
    private String telefone;

    public Usuario() {

    }

    public void salvar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference usuarioRef = firebaseRef
                .child("usuarios")
                .child(getIdUsuario());
        usuarioRef.setValue(this);
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}
