package com.cursoandroid.encontrarpetscampinas.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.cursoandroid.encontrarpetscampinas.R;
import com.cursoandroid.encontrarpetscampinas.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {



    private Button botaoLogar;

    private EditText textEmail, textSenha;

    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.cursoandroid.encontrarpetscampinas.R.layout.activity_login);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        textEmail = findViewById(R.id.campoEmailLogin);
        textSenha = findViewById(R.id.campoSenhaLogin);
        botaoLogar = findViewById(R.id.botaoContato);

        firebaseAuth = FirebaseAuth.getInstance();

        //init progressDialog
        progressDialog = new ProgressDialog(this);
        //set properties
        progressDialog.setTitle("Por favor, espere!");             //set title
        progressDialog.setMessage("Autenticando usuário...");   //set message
        progressDialog.setCanceledOnTouchOutside(false);    //disable dismiss when touching outside of progress dialog


    }

    public void logarUsuario(Usuario usuario) {

        firebaseAuth.signInWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    abrirTelaPrincipal();
                } else {

                    progressDialog.dismiss();
                    String excecao = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        excecao = "Usuário não está cadastrado.";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        excecao = "E-mail e senha não correspondem a um usuário cadastrado";
                    } catch (Exception e) {
                        excecao = "Erro ao Logar usuário: " + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(LoginActivity.this,
                            excecao,
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public void validarAutenticacaoUsuario(View view) {



        //Recuperar textos dos campos
        String textoEmail = textEmail.getText().toString();
        String textoSenha = textSenha.getText().toString();

        //Validar se e-mail e senha foram digitados
        if (!textoEmail.isEmpty()) {//verifica e-mail
            if (!textoSenha.isEmpty()) {//verifica senha

                Usuario usuario = new Usuario();
                usuario.setEmail(textoEmail);
                usuario.setSenha(textoSenha);

                logarUsuario(usuario);

            } else {
                Toast.makeText(LoginActivity.this,
                        "Preencha a senha!",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(LoginActivity.this,
                    "Preencha o email!",
                    Toast.LENGTH_SHORT).show();
        }


    }







    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser usuarioAtual = firebaseAuth.getCurrentUser();
        if (usuarioAtual != null) {
            abrirTelaPrincipal();
        }
    }

    public void abrirTelaCadastro(View view) {

        Intent i = new Intent(LoginActivity.this, CadastroActivity1.class);
        startActivity(i);
    }

    public void abrirTelaPrincipal() {

        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(i);
    }

    public void abrirTelaEsqueceuASenha( View view ) {

        Intent i = new Intent(LoginActivity.this, EsqueceuASenhaActivity.class);
        startActivity(i);
    }


}