package com.projetounivesp.pi2020_2.model;

import com.google.firebase.database.DatabaseReference;
import com.projetounivesp.pi2020_2.helper.ConfiguracaoFirebase;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Gislene
 */

public class Pedido {
    private String idUsuario;
    private String idEmpresa;
    private String idPedido;
    private String nome;
    private String endereco;
    private String bairro;
    private String cep;
    private String telefone;
    private List<PedidoItens> itens;
    private Double total;
    private String status = "pendente";
    private int tipoPagamento;
    private String observacao;

    public Pedido(){

    }

    public Pedido(String idUsu, String idEmp){
        setIdUsuario(idUsu);
        setIdEmpresa(idEmp);

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef
                .child("pedido_usuario")
                .child(idUsu)
                .child(idEmp);

        setIdPedido(pedidoRef.push().getKey());
    }

    public void salvar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef
                .child("pedido_usuario")
                .child(getIdEmpresa())
                .child(getIdUsuario());

        pedidoRef.setValue(this);
    }

    public void remover(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef
                .child("pedido_usuario")
                .child(getIdEmpresa())
                .child(getIdUsuario());

        pedidoRef.removeValue();
    }

    public void confirmar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef
                .child("pedidos")
                .child(getIdEmpresa())
                .child(getIdPedido());

        pedidoRef.setValue(this);
    }

    public void atualizarStatus(){

        HashMap <String, Object> status = new HashMap<>();
        status.put("status", getStatus());

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef
                .child("pedidos")
                .child(getIdEmpresa())
                .child(getIdPedido());

        pedidoRef.updateChildren(status);
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

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(String idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(String idRequisicao) {
        this.idPedido = idRequisicao;
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

    public List<PedidoItens> getItens() {
        return itens;
    }

    public void setItens(List<PedidoItens> itens) {
        this.itens = itens;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTipoPagamento() {
        return tipoPagamento;
    }

    public void setTipoPagamento(int tipoPagamento) {
        this.tipoPagamento = tipoPagamento;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
