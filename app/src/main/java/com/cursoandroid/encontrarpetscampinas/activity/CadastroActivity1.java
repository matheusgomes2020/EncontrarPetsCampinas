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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.cursoandroid.encontrarpetscampinas.R;
import com.cursoandroid.encontrarpetscampinas.databinding.ActivityCadastro1Binding;
import com.cursoandroid.encontrarpetscampinas.helper.Permissao;
import com.cursoandroid.encontrarpetscampinas.model.Usuario;

public class CadastroActivity1 extends AppCompatActivity {

    private ActivityCadastro1Binding binding;

    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };
    private static final int SELECAO_GALERIA = 200;
    private Usuario usuario = new Usuario();

    private ProgressDialog progressDialog;
    private Bitmap imagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCadastro1Binding.inflate(getLayoutInflater());
        setContentView( binding.getRoot() );

        setSupportActionBar(findViewById(R.id.toolbarCad1));
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        //Validar permissões
        Permissao.validarPermissoes(permissoesNecessarias, this, 1);

        //init progressDialog
        progressDialog = new ProgressDialog(this);
        //set properties
        progressDialog.setTitle("Por favor, espere!");             //set title
        progressDialog.setMessage("Salvando imagem...");   //set message
        progressDialog.setCanceledOnTouchOutside(false);    //disable dismiss when touching outside of progress dialog

        binding.imageViewInserir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });

    }


    public void validarCampos( View view ){

        String nome = binding.editTextNome.getText().toString();
        String endereco = binding.editTextEndereco.getText().toString();
        String telefone = binding.editTextTelefone.getMasked().toString();


        if ( !nome.isEmpty()  && !telefone.isEmpty() ){

            usuario.setNome( nome );
            usuario.setEndereco( endereco );
            usuario.setTelefone( telefone );
            Intent i = new Intent( CadastroActivity1.this, CadastroActivity2.class );
            i.putExtra( "dados", usuario );

            startActivity( i );

            /*if (imagem!=null){

                try {
                    ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, bStream);

                    i.putExtra( "imagem", bStream.toByteArray() );
                }catch (Exception e){
                    e.printStackTrace();
                }


            }

             */



        }else {
            if ( nome.isEmpty() ){
                binding.imageCheckNome.setImageResource(R.drawable.ic_check_vermelho);
                Toast.makeText(CadastroActivity1.this, "Preencha o nome!", Toast.LENGTH_SHORT).show();
            }else {binding.imageCheckNome.setImageResource(R.drawable.ic_check_verde); }
            if ( imagem == null ){

                binding.imageCheckImagem.setImageResource(R.drawable.ic_check_vermelho);
            }else {binding.imageCheckImagem.setImageResource(R.drawable.ic_check_verde); }
            if ( telefone.isEmpty() ){
                Toast.makeText(CadastroActivity1.this, "Preencha o telefone!", Toast.LENGTH_SHORT).show();
                binding.imageCheckTelefone.setImageResource(R.drawable.ic_check_vermelho);
            }else {binding.imageCheckTelefone.setImageResource(R.drawable.ic_check_verde); }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            progressDialog.show();


            try {
                Uri localImagemSelecionada = data.getData();
                imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);

                if (imagem != null) {

                    binding.imageViewImagemSelecionada.setImageBitmap( imagem );
                    binding.linearImagem.setVisibility( View.VISIBLE );
                    progressDialog.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}