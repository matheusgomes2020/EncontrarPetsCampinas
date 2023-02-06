package com.cursoandroid.encontrarpetscampinas.model;

import android.graphics.Bitmap;


import com.cursoandroid.encontrarpetscampinas.config.ConfiguracaoFirebase;
import com.cursoandroid.encontrarpetscampinas.helper.UsuarioFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Usuario implements Serializable {

    private String id;
    private String nome;
    private String data;
    private String endereco;
    private String telefone;
    private String email;
    private String senha;
    private String foto;
    private Bitmap imagem;
    private String latitude;
    private String longitude;
    private String caminhoFoto;


    public Usuario() {
    }

    public void salvar(){

        DatabaseReference firebaseref = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference usuario = firebaseref.child( "usuarios" ).child( getId() );

        usuario.setValue( this );

    }

    public void atualizar( String dado ){

        String idUsuario = UsuarioFirebase.getIdentificadorUsuario();

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child( "usuarios" )
                .child( idUsuario );

        Map objeto = new HashMap();

        switch ( dado ){
            case "nome" :
                objeto.put( "nome", getNome() );
                break;
            case "caminhoFoto" :
                objeto.put( "caminhoFoto", getCaminhoFoto() );
                break;
            case "telefone" :
                objeto.put( "telefone", getTelefone() );
                break;

        }



        firebaseRef.updateChildren( objeto );

    }

    public void remover(){

        String idUsuario = UsuarioFirebase.getIdentificadorUsuario();

        DatabaseReference usuarios = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("usuarios")
                .child( idUsuario );

        usuarios.removeValue();


    }

    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    @Exclude
    public Bitmap getImagem() {
        return imagem;
    }

    public void setImagem(Bitmap imagem) {
        this.imagem = imagem;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }
}
