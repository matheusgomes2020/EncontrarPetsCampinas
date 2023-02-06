package com.cursoandroid.encontrarpetscampinas.helper;


import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;


import com.cursoandroid.encontrarpetscampinas.config.ConfiguracaoFirebase;
import com.cursoandroid.encontrarpetscampinas.model.Usuario;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


public class UsuarioFirebase {

    private static Usuario u = new Usuario();

    public static String getIdentificadorUsuario(){
        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAutenticacao();
        String identificadorUsuario = usuario.getCurrentUser().getUid();

        return identificadorUsuario;
    }

    public static FirebaseUser getUsuarioAtual(){
        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAutenticacao();
        return usuario.getCurrentUser();

    }

    public static boolean atualizarNomeUsuario(String nome){

        try {
            FirebaseUser user = getUsuarioAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName( nome )
                    .build();

            user.updateProfile( profile ).addOnCompleteListener( new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if ( !task.isSuccessful() ){
                        Log.d( "Perfil", "Erro ao atualizar nome de perfil." );
                    }
                }
            });
            return true;
        }catch ( Exception e ){
            e.printStackTrace();
            return false;
        }

    }



    public static boolean atualizarFotoUsuario(Uri url){

        try {
            FirebaseUser user = getUsuarioAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setPhotoUri( url )
                    .build();

            user.updateProfile( profile ).addOnCompleteListener( new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if ( !task.isSuccessful() ){
                        Log.d( "Perfil", "Erro ao atualizar foto de perfil." );
                    }
                }
            });
            return true;
        }catch ( Exception e ){
            e.printStackTrace();
            return false;
        }

    }

    public static Usuario getdadosUsuarioLogado(){

        FirebaseUser firebaseUser = getUsuarioAtual();

        Usuario usuario = new Usuario();
        usuario.setEmail( firebaseUser.getEmail() );
        usuario.setNome( firebaseUser.getDisplayName() );
        usuario.setId( getIdentificadorUsuario() );
        usuario.setTelefone( getTelefoneUsuario() );
        //Log.d( "usutttt: " , "tel: " + getTelefoneUsuario() );

        if ( firebaseUser.getPhotoUrl() == null ){
            usuario.setFoto("");
        }else {
            usuario.setFoto( firebaseUser.getPhotoUrl().toString() );
        }

        return usuario;
    }


    public static String getTelefoneUsuario(){
        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference aa = databaseReference.child("usuarios")
                .child( getIdentificadorUsuario() );

        aa.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                u = snapshot.getValue( Usuario.class );

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return u.getTelefone();
    }

    public static void atualizarDadosLocalizacao( Double lat, Double lon ){

        //Define nó de local de usuário
        DatabaseReference localUsuario = ConfiguracaoFirebase.getFirebaseDatabase()
                .child( "local_usuario" );

        GeoFire geoFire = new GeoFire( localUsuario );

        //Recupera dados do usuário logado
        Usuario usuarioLogado = UsuarioFirebase.getdadosUsuarioLogado();

        //Configura localiza~ção do usuário
        geoFire.setLocation(
                usuarioLogado.getId(),
                new GeoLocation(lat, lon),
                new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {

                        if ( error != null ){

                        }

                    }
                }
        );

    }



}