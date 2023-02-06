package com.cursoandroid.encontrarpetscampinas.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.cursoandroid.encontrarpetscampinas.R;
import com.cursoandroid.encontrarpetscampinas.config.ConfiguracaoFirebase;
import com.cursoandroid.encontrarpetscampinas.databinding.ActivityEditarPerfilBinding;
import com.cursoandroid.encontrarpetscampinas.helper.Permissao;
import com.cursoandroid.encontrarpetscampinas.helper.UsuarioFirebase;
import com.cursoandroid.encontrarpetscampinas.model.Pet;
import com.cursoandroid.encontrarpetscampinas.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class EditarPerfilActivity extends AppCompatActivity {

    private ActivityEditarPerfilBinding binding;
    private Usuario usuarioLogado;
    Usuario u = new Usuario();
    private static final int SELECAO_GALERIA = 200;
    private StorageReference storageRef;
    private String identificadorUsuario;
    private String tell;
    private ProgressDialog progressDialog;

    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEditarPerfilBinding.inflate(getLayoutInflater());
        setContentView( binding.getRoot() );

        //Validar permissões
        Permissao.validarPermissoes( permissoesNecessarias, this, 1 );

        //Configurações iniciais
        usuarioLogado = UsuarioFirebase.getdadosUsuarioLogado();
        storageRef = ConfiguracaoFirebase.getFirebaseStorage();
        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();

        setSupportActionBar(findViewById(com.cursoandroid.encontrarpetscampinas.R.id.toolbarCadastroPets));
        getSupportActionBar().setTitle( "Editar dados pefil" );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_black_24p);

        //Recuperar dados do usuario
        FirebaseUser usuarioPerfil = UsuarioFirebase.getUsuarioAtual();
        binding.editTextNome.setText( usuarioPerfil.getDisplayName() );
        binding.editTextEmail.setText( usuarioPerfil.getEmail() );
        binding.editTextTelefone.setText( UsuarioFirebase.getTelefoneUsuario() );
        //getTelefoneUsuario();

        //init progressDialog
        progressDialog = new ProgressDialog(this);
        //set properties
        progressDialog.setTitle("Por favor, espere!");             //set title
        progressDialog.setMessage("Alterando foto do usuário...");   //set message
        progressDialog.setCanceledOnTouchOutside(false);    //disable dismiss when touching outside of progress dialog

        Uri url = usuarioPerfil.getPhotoUrl();

        if ( url != null ){
            Glide.with( EditarPerfilActivity.this)
                    .load( url )
                    .into( binding.imageFotoPerfil );
        }else {
            binding.imageFotoPerfil.setImageResource( R.drawable.padrao );
        }

        //Alterar foto do usuário
        binding.imageEditarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if( i.resolveActivity(getPackageManager()) != null ){
                    startActivityForResult(i, SELECAO_GALERIA );
                }
            }
        });

        //Alterar nome do usuário
        binding.imageViewNomeEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nome = binding.editTextNome.getText().toString();
                if ( !nome.isEmpty() ){
                    atualizarNomeUsuario( nome );
                }else {
                    Toast.makeText(EditarPerfilActivity.this, "Nome não pode ser vazio!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Alterar telefone do usuário
        binding.imageViewTelefoneEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String telefone = binding.editTextTelefone.getText().toString();
                if ( !telefone.isEmpty() ){
                    atualizarTelefoneUsuario( telefone );
                }else {
                    Toast.makeText(EditarPerfilActivity.this, "Telefone não pode ser vazio!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        progressDialog.show();

        if ( resultCode == RESULT_OK ){
            Bitmap imagem = null;

            try {
                //Selecao apenas da galeria
                switch ( requestCode ){
                    case SELECAO_GALERIA:
                        Uri localImagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada );
                        break;
                }

                //Caso tenha sido escolhido uma imagem
                if ( imagem != null ){



                    //Configura imagem na tela
                    binding.imageFotoPerfil.setImageBitmap( imagem );

                    //Recuperar dados da imagem para o firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    //Salvar imagem no firebase
                    StorageReference imagemRef = storageRef
                            .child("imagens")
                            .child("perfil")
                            .child( identificadorUsuario + ".jpeg");
                    UploadTask uploadTask = imagemRef.putBytes( dadosImagem );
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditarPerfilActivity.this,
                                    "Erro ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Uri url = task.getResult();
                                    atualizarFotoUsuario( url );
                                }
                            });

                            Toast.makeText(EditarPerfilActivity.this,
                                    "Sucesso ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();

                            progressDialog.dismiss();

                        }
                    });



                }

            }catch ( Exception e ){
                e.printStackTrace();
                progressDialog.dismiss();
            }



        } else {
            progressDialog.dismiss();
        }

    }

    private void atualizarFotoUsuario( Uri url ){

        //Atualizar foto no perfil
        UsuarioFirebase.atualizarFotoUsuario( url );

        //Atualizar foto no Firebase
        usuarioLogado.setCaminhoFoto( url.toString() );

        usuarioLogado.atualizar( "caminhoFoto" );

        Toast.makeText(EditarPerfilActivity.this,
                "Sua foto foi atualizada!",
                Toast.LENGTH_SHORT).show();



    }

    private void atualizarNomeUsuario( String nome){

        //Atualizar foto no perfil
        UsuarioFirebase.atualizarNomeUsuario( nome );

        usuarioLogado.setNome( nome );
        usuarioLogado.atualizar( "nome" );
        atualizarDadosDoUsuario();

        Toast.makeText(EditarPerfilActivity.this,
                "Seu nome foi atualizado!",
                Toast.LENGTH_SHORT).show();



    }

    private void atualizarTelefoneUsuario( String telefone){

        //Atualizar foto no perfil
        //UsuarioFirebase.atualizarNomeUsuario( telefone );

        usuarioLogado.setTelefone( telefone );
        usuarioLogado.atualizar( "telefone" );
        atualizarDadosDoUsuario();

        Toast.makeText(EditarPerfilActivity.this,
                "Seu telefone foi atualizado!",
                Toast.LENGTH_SHORT).show();



    }

    private void getTelefoneUsuario(){
        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference aa = databaseReference.child("usuarios")
                .child( identificadorUsuario );

        aa.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                u = snapshot.getValue( Usuario.class );
                binding.editTextTelefone.setText( u.getTelefone() );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void atualizarDadosDoUsuario(){

        //Alterar telefone do usuário
        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabase()
                .child( "pets" ).child(identificadorUsuario);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for ( DataSnapshot dados : snapshot.getChildren() ){

                    Pet pet = dados.getValue( Pet.class );
                    pet.setUsuario( usuarioLogado );
                    pet.atualizar( "usuario" );

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}