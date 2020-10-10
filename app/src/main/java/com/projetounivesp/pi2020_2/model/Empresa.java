package com.projetounivesp.pi2020_2.model;

import com.google.firebase.database.DatabaseReference;
import com.projetounivesp.pi2020_2.helper.ConfiguracaoFirebase;

import java.io.Serializable;

/**
 * Created by Gislene
 */

public class Empresa implements Serializable {
    private String idUsuario;
    private String urlImagem;
    private String nome;
    private String endereco;
    private String bairro;
    private String cep;
    private String telefone;

    public Empresa(){

    }

    public void salvar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference empresaRef = firebaseRef.child("empresas")
                .child(getIdUsuario());
        empresaRef.setValue(this);
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

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}
