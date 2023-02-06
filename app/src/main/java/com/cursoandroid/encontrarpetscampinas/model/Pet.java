package com.cursoandroid.encontrarpetscampinas.model;

import com.cursoandroid.encontrarpetscampinas.config.ConfiguracaoFirebase;
import com.cursoandroid.encontrarpetscampinas.helper.UsuarioFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pet implements Serializable {

    private String id;
    private String nome;
    private String idade;
    private String foto;
    private List<String> fotos;
    private String dataPerdido;
    private String raca;
    private String ultimaLocalizacao;
    private String ultimaLocalizacaoBairro;
    private Usuario usuario;
    private String DataCriacao;
    private String tipo;
    private String Descricao;
    private String situacao;
    private String perdidoOuAvistado;
    private Endereco endereco;

    public Pet() {

        DatabaseReference petRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child( "pets" );

        setId( petRef.push().getKey() );

    }

    public void salvar(){

        String idUsuario = UsuarioFirebase.getIdentificadorUsuario();

        setSituacao( "perdido" );
        setUsuario( UsuarioFirebase.getdadosUsuarioLogado() );
        //setUsuario().setId( idUsuario );

        DatabaseReference pets = ConfiguracaoFirebase.getFirebaseDatabase()
                .child( "pets" );

        pets.child( idUsuario )
                .child( getId())
                .setValue( this );

        salvarPetPublico();

    }

    public void salvarPetPublico(){

        DatabaseReference pets = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("petspublicos");

        pets.child( getId() )
                .setValue(this);
    }

    public void remover(){

        String idUsuario = UsuarioFirebase.getIdentificadorUsuario();

        DatabaseReference pets = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("pets")
                .child( idUsuario )
                .child( getId() );

        pets.removeValue();
        removerPetPublico();


    }

    public void removerPetPublico(){

        DatabaseReference pets = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("petspublicos")
                .child( getId() );

        pets.removeValue();

    }

    public void atualizar( String dado ){

        String idUsuario = UsuarioFirebase.getIdentificadorUsuario();

        DatabaseReference pets = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("pets");

        DatabaseReference petP = pets
                .child( idUsuario )
                .child( getId() );

        DatabaseReference todosPets = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("petspublicos");

        DatabaseReference petpublico = todosPets
                .child( getId() );

        Map objeto = new HashMap();


        switch ( dado ){
            case "nome" :
                objeto.put( "nome", getNome() );
                break;
            case "tipo" :
                objeto.put( "tipo", getTipo() );
                break;
            case "raca" :
                objeto.put( "raca", getRaca() );
                break;
            case "idade" :
                objeto.put( "idade", getIdade() );
                break;
            case "dataPerdido" :
                objeto.put( "dataPerdido", getDataPerdido() );
                break;
            case "descricao" :
                objeto.put( "descricao", getDescricao() );
                break;

            case "situacao" :
                objeto.put( "situacao", getSituacao() );
                break;

            case "usuario" :
                objeto.put( "usuario", getUsuario() );
                break;


        }

        //Atualiza pet do usuário
        petP.updateChildren( objeto );
        //Atualiza pet público
        petpublico.updateChildren( objeto );

    }

    public Map<String, Object> converterParaMap(){

        HashMap<String, Object> petMap = new HashMap<>();
        petMap.put( "nome", getNome() );
        petMap.put( "tipo", getTipo() );
        petMap.put( "raca", getRaca() );
        petMap.put( "idade", getIdade() );
        petMap.put( "dataPerdido", getDataPerdido() );
        petMap.put( "descricao", getDescricao() );

        return petMap;

    }

    public void favoritar(){

        String idUsuario = UsuarioFirebase.getIdentificadorUsuario();

        DatabaseReference favoritosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child( "favoritos" );

        favoritosRef.child( idUsuario )
                .child( getId() )
                        .setValue( this );


    }

    public void desfavoritar(){

        String idUsuario = UsuarioFirebase.getIdentificadorUsuario();

        DatabaseReference favoritosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child( "favoritos" );

        favoritosRef.child( idUsuario )
                .child( getId() )
                .removeValue();


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

    public String getIdade() {
        return idade;
    }

    public void setIdade(String idade) {
        this.idade = idade;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getDataPerdido() {
        return dataPerdido;
    }

    public void setDataPerdido(String dataPerdido) {
        this.dataPerdido = dataPerdido;
    }

    public String getRaca() {
        return raca;
    }

    public void setRaca(String raca) {
        this.raca = raca;
    }

    public String getUltimaLocalizacao() {
        return ultimaLocalizacao;
    }

    public void setUltimaLocalizacao(String ultimaLocalizacao) {
        this.ultimaLocalizacao = ultimaLocalizacao;
    }

    public String getUltimaLocalizacaoBairro() {
        return ultimaLocalizacaoBairro;
    }

    public void setUltimaLocalizacaoBairro(String ultimaLocalizacaoBairro) {
        this.ultimaLocalizacaoBairro = ultimaLocalizacaoBairro;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getDataCriacao() {
        return DataCriacao;
    }

    public void setDataCriacao(String dataCriacao) {
        DataCriacao = dataCriacao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return Descricao;
    }

    public void setDescricao(String descricao) {
        Descricao = descricao;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getPerdidoOuAvistado() {
        return perdidoOuAvistado;
    }

    public void setPerdidoOuAvistado(String perdidoOuAvistado) {
        this.perdidoOuAvistado = perdidoOuAvistado;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public List<String> getFotos() {
        return fotos;
    }

    public void setFotos(List<String> fotos) {
        this.fotos = fotos;
    }

}
