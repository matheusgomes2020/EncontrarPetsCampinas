package com.cursoandroid.encontrarpetscampinas.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cursoandroid.encontrarpetscampinas.R;
import com.cursoandroid.encontrarpetscampinas.config.ConfiguracaoFirebase;
import com.cursoandroid.encontrarpetscampinas.databinding.ActivityCadastro2Binding;
import com.cursoandroid.encontrarpetscampinas.helper.UsuarioFirebase;
import com.cursoandroid.encontrarpetscampinas.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.storage.StorageReference;

public class CadastroActivity2 extends AppCompatActivity {

    private ActivityCadastro2Binding binding;
    private FirebaseAuth auth;
    private Usuario usuario = new Usuario();
    private Bitmap imagem;
    private StorageReference storage;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCadastro2Binding.inflate(getLayoutInflater());
        setContentView( binding.getRoot() );

        setSupportActionBar(findViewById(R.id.toolbarCad2));
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        //init progressDialog
        progressDialog = new ProgressDialog(this);
        //set properties
        progressDialog.setTitle("Por favor, espere!");             //set title
        progressDialog.setMessage("Salvando usuário...");   //set message
        progressDialog.setCanceledOnTouchOutside(false);    //disable dismiss when touching outside of progress dialog

        storage = ConfiguracaoFirebase.getFirebaseStorage();

            Bundle bundle = getIntent().getExtras();

            usuario =  ( Usuario ) bundle.getSerializable( "dados" );
            /*
        try {
            if (getIntent().hasExtra("imagem")){
                ImageView p = new ImageView(this);
                Bitmap b = BitmapFactory.decodeByteArray(
                        getIntent().getByteArrayExtra("imagem"),0,getIntent().getByteArrayExtra("imagem").length
                );

                imagem = b;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

             */

    }


    public void validarCampos( View view ){

        String email = binding.editTextEmail.getText().toString();
        String senha = binding.editTextSenha.getText().toString();


        if ( !email.isEmpty() && !senha.isEmpty() ){

            usuario.setEmail( email );
            usuario.setSenha( senha );
            cadastrarUsuario( usuario );

        }else {
            if ( email.isEmpty() ){
                binding.imageCheckEmail.setImageResource(R.drawable.ic_check_vermelho);
                Toast.makeText(CadastroActivity2.this, "Preencha o nome!", Toast.LENGTH_SHORT).show();
            }else {binding.imageCheckEmail.setImageResource(R.drawable.ic_check_verde); }
            if ( senha.isEmpty() ){
                Toast.makeText(CadastroActivity2.this, "Preencha o endereço!", Toast.LENGTH_SHORT).show();
                binding.imageCheckSenha.setImageResource(R.drawable.ic_check_vermelho);
            }else {binding.imageCheckSenha.setImageResource(R.drawable.ic_check_verde); }
        }

    }

    public void cadastrarUsuario( Usuario usuario ){

        progressDialog.show();
        auth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        auth.createUserWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if ( task.isSuccessful() ){

                    Toast.makeText(CadastroActivity2.this,
                            "Sucesso ao cadastrar usuário!",
                            Toast.LENGTH_SHORT).show();
                    UsuarioFirebase.atualizarNomeUsuario( usuario.getNome() );
                    finish();
                    progressDialog.dismiss();

                    try {

                        usuario.setId( UsuarioFirebase.getIdentificadorUsuario() );
                        usuario.salvar();

                        startActivity( new Intent( CadastroActivity2.this, MainActivity.class) );
                        finish();

                    }catch ( Exception e ){
                        e.printStackTrace();
                    }


                }else {

                    progressDialog.dismiss();
                    String excessao = "";
                    try {
                        throw  task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        excessao = "Digite uma senha mais forte!";
                    }
                    catch (FirebaseAuthInvalidCredentialsException e){
                        excessao = "Por favor, digite um e-mail válido!";
                    }
                    catch (FirebaseAuthUserCollisionException e){
                        excessao = "Esta conta já foi cadastrada!";
                    }
                    catch (Exception e){
                        excessao = "Erro ao cadastrar usuário: " + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(CadastroActivity2.this,
                            excessao,
                            Toast.LENGTH_SHORT).show();



                }
            }
        });

    }

}