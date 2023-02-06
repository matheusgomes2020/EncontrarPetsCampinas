package com.cursoandroid.encontrarpetscampinas.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.cursoandroid.encontrarpetscampinas.activity.ArquivadosActivity;
import com.cursoandroid.encontrarpetscampinas.activity.ConfiguracoesActivity;
import com.cursoandroid.encontrarpetscampinas.activity.EditarPerfilActivity;
import com.cursoandroid.encontrarpetscampinas.activity.LoginActivity;
import com.cursoandroid.encontrarpetscampinas.config.ConfiguracaoFirebase;
import com.cursoandroid.encontrarpetscampinas.databinding.FragmentSlideBinding;
import com.cursoandroid.encontrarpetscampinas.helper.UsuarioFirebase;
import com.cursoandroid.encontrarpetscampinas.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SlideFragment extends Fragment {

    private FragmentSlideBinding binding;
    private FirebaseAuth firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private Usuario usuarioFirebase = UsuarioFirebase.getdadosUsuarioLogado();
    private FirebaseUser firebaseUser = UsuarioFirebase.getUsuarioAtual();



    public SlideFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Recuperar dados do usuario
        FirebaseUser usuarioPerfil = UsuarioFirebase.getUsuarioAtual();

        binding = FragmentSlideBinding.inflate( inflater, container, false );
        View root =  binding.getRoot();

        binding.textViewEditarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent( getActivity(), EditarPerfilActivity.class);
                startActivity( i );
            }
        });

        binding.textViewSair.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firebaseAuth.signOut();
                startActivity( new Intent(getActivity(), LoginActivity.class));

            }
        });

        binding.textViewArquivados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity( new Intent(getActivity(), ArquivadosActivity.class));
            }
        });

        binding.textNomeSlide.setText( usuarioPerfil.getDisplayName() );


        binding.textViewPoliticaDePrivacidade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent( getActivity(), ConfiguracoesActivity.class);
                startActivity( i );
            }
        });

        binding.textViewSobre.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getActivity(), "App desenvolvido para o TCC Unip", Toast.LENGTH_SHORT).show();

            }
        });

        binding.textsolicitarExclusaoConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                excluirUsuario();
                Toast.makeText(getActivity(), "Solicitar exclusão de conta", Toast.LENGTH_SHORT).show();
            }
        });



        Uri url = usuarioPerfil.getPhotoUrl();

        if ( url != null ){
            Glide.with( SlideFragment.this)
                    .load( url )
                    .into( binding.imageView7 );
        }else {
            binding.imageView7.setImageResource( com.cursoandroid.encontrarpetscampinas.R.drawable.padrao );
        }


        return root;
    }

    public void excluirUsuario(){

        androidx.appcompat.app.AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle("Confirmar exclusão");
        dialog.setMessage("Deseja excluir  o usuário " + usuarioFirebase.getNome() + " ?");

        dialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        usuarioFirebase.remover();
                        Intent i = new Intent( getActivity(), LoginActivity.class );
                        startActivity( i );

                    }
                });
            }
        });

        dialog.setNegativeButton("Não", null);
        dialog.create();
        dialog.show();



    }

}